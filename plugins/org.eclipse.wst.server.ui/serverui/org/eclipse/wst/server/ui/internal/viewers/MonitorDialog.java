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
package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.IServerPort;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
/**
 * Dialog that prompts a user to add or edit a server monitor.
 */
public class MonitorDialog extends Dialog {
	protected IServer server;
	protected IServerPort port;
	protected int monitorPort = -1;
	protected String[] portContentTypes;
	protected String[] contentTypes;
	protected boolean isEdit = false;
	protected boolean portChanged = false;
	
	protected Button ok;
	
	protected Table table;
	protected TableViewer tableViewer;
	protected boolean init;

	/**
	 * MonitorDialog constructor comment.
	 * @param parentShell org.eclipse.swt.widgets.Shell
	 * @
	 */
	public MonitorDialog(Shell parentShell, IServer server) {
		super(parentShell);

		this.server = server;
	}
	
	public MonitorDialog(Shell parentShell, IServer server, IServerPort port, int monitorPort, String[] contentTypes) {
		this(parentShell, server);
		this.monitorPort = monitorPort;
		this.contentTypes = contentTypes;
		this.port = port;
		isEdit = true;
	}

	/**
	 *
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(ServerUIPlugin.getResource("%dialogMonitorTitle"));
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		ok = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * 
	 */
	protected Control createDialogArea(Composite parent) {
		// create a composite with standard margins and spacing
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parent.getFont());
		//WorkbenchHelp.setHelp(composite, ContextIds.TERMINATE_SERVER_DIALOG);
		
		Label label = new Label(composite, SWT.WRAP);
		label.setText(ServerUIPlugin.getResource("%dialogMonitorAddDescription", new String[] { server.getName() } ));
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 2;
		data.widthHint = 275;
		label.setLayoutData(data);
		
		table = new Table(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		data.heightHint = 100;
		data.horizontalSpan = 2;
		table.setLayoutData(data);
		table.setLinesVisible(true);
		tableViewer = new TableViewer(table);
		
		TableLayout tableLayout = new TableLayout();
		table.setLayout(tableLayout);
		table.setHeaderVisible(true);
		
		tableLayout.addColumnData(new ColumnWeightData(12, 120, true));
		TableColumn col = new TableColumn(table, SWT.NONE);
		col.setText(ServerUIPlugin.getResource("%dialogMonitorColumnType"));
		
		tableLayout.addColumnData(new ColumnWeightData(4, 40, true));
		col = new TableColumn(table, SWT.NONE);
		col.setText(ServerUIPlugin.getResource("%dialogMonitorColumnPort"));
		
		tableViewer.setContentProvider(new PortContentProvider(server));
		tableViewer.setLabelProvider(new PortLabelProvider(server));
		tableViewer.setInput(AbstractTreeContentProvider.ROOT);
		
		tableViewer.setSorter(new ViewerSorter() {
			public int compare(Viewer viewer, Object e1, Object e2) {
				IServerPort port1 = (IServerPort) e1;
				IServerPort port2 = (IServerPort) e2;
				if (port1.getPort() == port2.getPort())
					return 0;
				else if (port1.getPort() > port2.getPort())
					return 1;
				else
					return -1;
			}
		});
		
		label = new Label(composite, SWT.NONE);
		label.setText(ServerUIPlugin.getResource("%dialogMonitorMonitorPort"));
		
		final Text portText = new Text(composite, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 150;
		portText.setLayoutData(data);
		if (monitorPort >= 0)
			portText.setText(monitorPort + "");

		portText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				try {
					monitorPort = Integer.parseInt(portText.getText());
					if (ok != null)
						ok.setEnabled(true);
				} catch (Exception ex) {
					monitorPort = -1;
					if (ok != null)
						ok.setEnabled(false);
				}
				portChanged = true;
			}
		});
		
		label = new Label(composite, SWT.NONE);
		label.setText(ServerUIPlugin.getResource("%dialogMonitorContentType"));
		
		final Combo combo = new Combo(composite, SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 150;
		combo.setLayoutData(data);
		combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int size = combo.getItemCount();
				int sel = combo.getSelectionIndex();
				if (sel == size - 1)
					contentTypes = portContentTypes;
				else
					contentTypes = new String[] { portContentTypes[sel] };
			}
		});
		
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				port = (IServerPort) getSelection(tableViewer.getSelection());
				if (port == null)
					return;
				if (!portChanged) {
					portText.setText((port.getPort() + 1) + "");
					portChanged = false;
				}
				portContentTypes = port.getContentTypes();
				String[] s = null;
				String all = ServerUIPlugin.getResource("%dialogMonitorContentTypeAll");
				if (portContentTypes == null || portContentTypes.length == 1) {
					s = new String[] { all };
				} else {
					int size = portContentTypes.length;
					s = new String[size+1];
					for (int i = 0; i < size; i++) {
						s[i] = MonitorLabelProvider.getContentTypeString(portContentTypes[i]);
					}
					s[size] = all;
				}
				combo.setItems(s);
				combo.setText(all);
			}
		});
		
		Dialog.applyDialogFont(composite);
		
		if (port != null) {
			portChanged = true;
			String[] ct = contentTypes;
			tableViewer.setSelection(new StructuredSelection(port));
			if (ct != null)
				combo.setText(MonitorLabelProvider.getContentTypeString(ct[0]));
		} else if (tableViewer != null) {
			try {
				Object obj = tableViewer.getElementAt(0);
				if (obj != null)
				tableViewer.setSelection(new StructuredSelection(obj));
			} catch (Exception e) { }
		}
		
		portChanged = false;
	
		return composite;
	}
	
	protected Object getSelection(ISelection sel2) {
		IStructuredSelection sel = (IStructuredSelection) sel2;
		return sel.getFirstElement();
	}
	
	public int getMonitorPort() {
		return monitorPort;
	}

	public IServerPort getServerPort() {
		return port;
	}

	public String[] getContentTypes() {
		return contentTypes;
	}
}