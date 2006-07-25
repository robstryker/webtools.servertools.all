/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.jface.viewers.*;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.wizard.NewInstallableServerWizard;
/**
 * 
 */
public class RuntimeTypeComposite extends AbstractTreeComposite {
	protected IRuntimeType selection;
	protected RuntimeTypeSelectionListener listener;
	protected boolean creation;
	protected String type;
	protected String version;
	protected String runtimeTypeId;

	protected RuntimeTypeTreeContentProvider contentProvider;
	protected boolean initialSelection = true;

	public interface RuntimeTypeSelectionListener {
		public void runtimeTypeSelected(IRuntimeType runtimeType);
	}

	public RuntimeTypeComposite(Composite parent, int style, boolean creation, RuntimeTypeSelectionListener listener2, String type, String version, String runtimeTypeId) {
		super(parent, style);
		this.listener = listener2;
		this.creation = creation;
		this.type = type;
		this.version = version;
		this.runtimeTypeId = runtimeTypeId;
		
		contentProvider = new RuntimeTypeTreeContentProvider(RuntimeTypeTreeContentProvider.STYLE_VENDOR, creation, type, version, runtimeTypeId);
		treeViewer.setContentProvider(contentProvider);
		
		ILabelProvider labelProvider = new RuntimeTypeTreeLabelProvider();
		labelProvider.addListener(new ILabelProviderListener() {
			public void labelProviderChanged(LabelProviderChangedEvent event) {
				Object[] obj = event.getElements();
				if (obj == null)
					treeViewer.refresh(true);
				else {
					int size = obj.length;
					for (int i = 0; i < size; i++)
						treeViewer.refresh(obj[i], true);
				}
			}
		});
		treeViewer.setLabelProvider(labelProvider);
		treeViewer.setInput(AbstractTreeContentProvider.ROOT);
		
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				Object obj = getSelection(event.getSelection());
				if (obj instanceof IRuntimeType) {
					selection = (IRuntimeType) obj;
					setDescription(selection.getDescription());
				} else {
					selection = null;
					setDescription("");
				}
				listener.runtimeTypeSelected(selection);
			}
		});
		
		treeViewer.setSorter(new DefaultViewerSorter());
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible && initialSelection) {
			initialSelection = false;
			if (contentProvider.getInitialSelection() != null)
				treeViewer.setSelection(new StructuredSelection(contentProvider.getInitialSelection()), true);
		}
	}

	protected String getTitleLabel() {
		return Messages.runtimeTypeCompTree;
	}

	protected String getDescriptionLabel() {
		return Messages.runtimeTypeCompDescription;
	}

	protected String[] getComboOptions() {
		return new String[] { Messages.name,
			Messages.vendor, Messages.version,
			Messages.moduleSupport };
	}

	protected void viewOptionSelected(byte option) {
		ISelection sel = treeViewer.getSelection();
		treeViewer.setContentProvider(new RuntimeTypeTreeContentProvider(option, creation, type, version, runtimeTypeId));
		treeViewer.setSelection(sel);
	}

	public void refresh() {
		ISelection sel = treeViewer.getSelection();
		RuntimeTypeTreeContentProvider cp = (RuntimeTypeTreeContentProvider) treeViewer.getContentProvider();
		treeViewer.setContentProvider(new RuntimeTypeTreeContentProvider(cp.style, creation, type, version, runtimeTypeId));
		treeViewer.setSelection(sel);
	}

	public IRuntimeType getSelectedRuntimeType() {
		return selection;
	}

	protected String getDetailsLabel() {
		if (ServerPlugin.getInstallableServers().length > 0)
			return Messages.installableServerLink;
		return null;
	}

	protected void detailsSelected() {
		NewInstallableServerWizard wizard2 = new NewInstallableServerWizard();
		WizardDialog dialog = new WizardDialog(getShell(), wizard2);
		if (dialog.open() != Window.CANCEL)
			refresh();
	}
}