package org.eclipse.wst.server.ui.internal;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. � This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
�*
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.IProjectModule;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * PropertyPage for IProjects. It shows the server and runtime preference for the project.
 */
public class ProjectPropertyPage extends PropertyPage {
	protected IProject project;
	protected IProjectModule module;
	protected IServer server;
	
	protected RuntimeTargetComposite rtComp;

	/**
	 * ProjectPropertyPage constructor comment.
	 */
	public ProjectPropertyPage() {
		super();
	}

	/**
	 * Create the body of the page.
	 *
	 * @param parent org.eclipse.swt.widgets.Composite
	 * @return org.eclipse.swt.widgets.Control
	 */
	protected Control createContents(Composite parent) {
		try {
			IAdaptable element = getElement();
			if (element instanceof IProject)
				project = (IProject) element;

			Composite composite = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			layout.numColumns = 3;
			layout.verticalSpacing = 10;
			composite.setLayout(layout);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			Label label = new Label(composite, SWT.WRAP);
			label.setText(ServerUIPlugin.getResource("%prefProjectDescription"));
			GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			data.horizontalSpan = 3;
			data.widthHint = 200;
			label.setLayoutData(data);

			module = ServerUtil.getModuleProject(project);

			if (module == null) {
				label = new Label(composite, SWT.NONE);
				label.setText(ServerUIPlugin.getResource("%prefProjectNotModule"));
				data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
				data.horizontalSpan = 3;
				label.setLayoutData(data);
			} else {
				IModuleKind mk = ServerCore.getModuleKind(module.getType());
				if (mk != null) {
					label = new Label(composite, SWT.NONE);
					label.setText(ServerUIPlugin.getResource("%prefProject"));
					data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
					label.setLayoutData(data);
				
					Label moduleKind = new Label(composite, SWT.NONE);
					data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
					data.horizontalSpan = 2;
					moduleKind.setLayoutData(data);
					moduleKind.setText(module.getName() + " (" + mk.getName() + ")");
				}
				
				rtComp = new RuntimeTargetComposite(composite, project);
				
				IProjectProperties prefs = ServerCore.getProjectProperties(project);
				IServer prefServer = prefs.getDefaultServer();
	
				label = new Label(composite, SWT.NONE);
				label.setText(ServerUIPlugin.getResource("%prefProjectDefaultServer"));
				data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
				label.setLayoutData(data);
				
				final IServer[] servers = ServerUtil.getServersBySupportedModule(module);
				if (servers == null || servers.length == 0) {
					label = new Label(composite, SWT.WRAP);
					label.setText(ServerUIPlugin.getResource("%prefProjectNotConfigured"));
					data = new GridData();
					data.horizontalSpan = 2;
					label.setLayoutData(data);
				} else {
					final Table table = new Table(composite, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
					data = new GridData(GridData.FILL_HORIZONTAL);
					data.horizontalSpan = 2;
					data.heightHint = 70;
					table.setLayoutData(data);
					
					// add none option
					TableItem item = new TableItem(table, SWT.NONE);
					item.setText(ServerUIPlugin.getResource("%prefProjectNoServer"));
					//item.setImage();
					
					int size2 = servers.length;
					int count = 0;
					for (int j = 0; j < size2; j++) {
						item = new TableItem(table, SWT.NONE);
						item.setText(ServerUICore.getLabelProvider().getText(servers[j]));
						item.setImage(ServerUICore.getLabelProvider().getImage(servers[j]));
						item.setData(servers[j]);
						if (servers[j].equals(prefServer))
							count = j + 1;
					}

					table.setSelection(count);

					table.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent event) {
							int index = table.getSelectionIndex();
							if (index == 0) {
								server = null;
							} else if (index > 0) {
								server = servers[index-1];
							}
						}
					});
				}
			}
			
			Dialog.applyDialogFont(composite);

			return composite;
		} catch (Exception e) {
			Trace.trace("Error creating project property page", e);
			return null;
		}
	}

	/** 
	 * Method declared on IPreferencePage.
	 * Subclasses should override
	 */
	public boolean performOk() {
		if (module != null) {
			try {
				if (rtComp.hasChanged())
					rtComp.apply(new NullProgressMonitor());
				
				IProjectProperties props = ServerCore.getProjectProperties(project);
				props.setDefaultServer(server, new NullProgressMonitor());
			} catch (CoreException e) {
				Trace.trace(Trace.SEVERE, "Error setting preferred server", e);
				EclipseUtil.openError(ServerUIPlugin.getResource("%errorCouldNotSavePreference"), e.getStatus());
				return false;
			}
		}
		return super.performOk();
	}
}