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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
/**
 * 
 */
public class ServerTypeComposite extends AbstractTreeComposite {
	protected IServerType selection;
	protected ServerTypeSelectionListener listener;
	protected ServerTypeTreeContentProvider contentProvider;
	protected boolean initialSelection = true;
	
	protected String type;
	protected String version;
	
	protected boolean includeTestEnvironments = true;
	protected boolean isLocalhost;
	protected boolean includeIncompatibleVersions;
	
	public interface ServerTypeSelectionListener {
		public void serverTypeSelected(IServerType type);
	}
	
	public ServerTypeComposite(Composite parent, int style, String type, String version, ServerTypeSelectionListener listener2) {
		super(parent, style);
		this.listener = listener2;
		this.type = type;
		this.version = version;
	
		contentProvider = new ServerTypeTreeContentProvider(ServerTypeTreeContentProvider.STYLE_VENDOR, type, version);
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setLabelProvider(new ServerTypeTreeLabelProvider());
		treeViewer.setInput(AbstractTreeContentProvider.ROOT);

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				Object obj = getSelection(event.getSelection());
				if (obj instanceof IServerType) {
					selection = (IServerType) obj;
					setDescription(selection.getDescription());
				} else {
					selection = null;
					setDescription("");
				}
				listener.serverTypeSelected(selection);
			}
		});
		
		treeViewer.setSorter(new ViewerSorter() {
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1 instanceof IServerType && !(e2 instanceof IServerType))
					return 1;
				if (!(e1 instanceof IServerType) && e2 instanceof IServerType)
					return -1;
				if (!(e1 instanceof IServerType && e2 instanceof IServerType))
					return super.compare(viewer, e1, e2);
				IServerType r1 = (IServerType) e1;
				IServerType r2 = (IServerType) e2;
				/*if (r1.getOrder() > r2.getOrder())
					return -1;
				else if (r1.getOrder() < r2.getOrder())
					return 1;
				else
					return super.compare(viewer, e1, e2);*/
				return r1.getName().compareTo(r2.getName());
			}
		});
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible && initialSelection) {
			initialSelection = false;
			if (contentProvider.getInitialSelection() != null)
				treeViewer.setSelection(new StructuredSelection(contentProvider.getInitialSelection()), true);
		}
	}
	
	public void setHost(boolean newHost) {
		if (isLocalhost == newHost)
			return;
		
		isLocalhost = newHost;
		ISelection sel = treeViewer.getSelection();
		contentProvider.setLocalhost(isLocalhost);
		treeViewer.refresh();
		//treeViewer.expandToLevel(2);
		treeViewer.setSelection(sel, true);
	}

	public void setIncludeTestEnvironments(boolean b) {
		includeTestEnvironments = b;
		ISelection sel = treeViewer.getSelection();
		contentProvider.setIncludeTestEnvironments(b);
		treeViewer.refresh();
		treeViewer.setSelection(sel, true);
		//treeViewer.expandToLevel(2);
	}
	
	public void setIncludeIncompatibleVersions(boolean b) {
		includeIncompatibleVersions = b;
		ISelection sel = treeViewer.getSelection();
		contentProvider.setIncludeIncompatibleVersions(b);
		treeViewer.refresh();
		treeViewer.setSelection(sel, true);
	}

	protected String getDescriptionLabel() {
		return null; //ServerUIPlugin.getResource("%serverTypeCompDescription");
	}

	protected String getTitleLabel() {
		return ServerUIPlugin.getResource("%serverTypeCompDescription");
	}

	protected String[] getComboOptions() {
		return new String[] { ServerUIPlugin.getResource("%name"),
			ServerUIPlugin.getResource("%vendor"), ServerUIPlugin.getResource("%version"),
			ServerUIPlugin.getResource("%moduleSupport") };
	}

	protected void viewOptionSelected(byte option) {
		ISelection sel = treeViewer.getSelection();
		contentProvider = new ServerTypeTreeContentProvider(option, type, version);
		contentProvider.setLocalhost(isLocalhost);
		contentProvider.setIncludeTestEnvironments(includeTestEnvironments);
		contentProvider.setIncludeIncompatibleVersions(includeIncompatibleVersions);
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setSelection(sel);
	}

	public IServerType getSelectedServerType() {
		return selection;
	}
}