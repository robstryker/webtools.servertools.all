package org.eclipse.wst.server.ui.internal.viewers;
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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;

import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
/**
 * 
 */
public abstract class AbstractTreeComposite extends Composite {
	protected Tree tree;
	protected TreeViewer treeViewer;
	protected Label description;
	
	public AbstractTreeComposite(Composite parent, int style) {
		super(parent, style);
		
		createWidgets();
	}
	
	protected void createWidgets() {
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 3;
		layout.verticalSpacing = 3;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 2;
		setLayout(layout);

		//GridData data = new GridData(GridData.FILL_BOTH);
		//setLayoutData(data);
		
		String descriptionText = getDescriptionLabel();
		if (descriptionText != null) {
			Label label = new Label(this, SWT.WRAP);
			label.setText(descriptionText);
			GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
			data.horizontalSpan = 2;
			label.setLayoutData(data);
		}
		
		Label label = new Label(this, SWT.WRAP);
		label.setText(getTitleLabel());
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
		data.horizontalSpan = 2;
		label.setLayoutData(data);

		tree = new Tree(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE);
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		tree.setLayoutData(data);
		treeViewer = new TreeViewer(tree);
		
		treeViewer.setSorter(new ViewerSorter());
		
		label = new Label(this, SWT.NONE);
		label.setText("");
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
		label.setLayoutData(data);
		
		// view composite
		Composite comp = new Composite(this, SWT.NONE);
		layout = new GridLayout();
		layout.horizontalSpacing = 3;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 2;
		comp.setLayout(layout);

		data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER);
		comp.setLayoutData(data);
		
		label = new Label(comp, SWT.NONE);
		label.setText(ServerUIPlugin.getResource("%viewBy"));
		data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER);
		label.setLayoutData(data);
	
		final Combo combo = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setItems(getComboOptions());
		combo.select(1);
		combo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.GRAB_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER));
		combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int sel = combo.getSelectionIndex();
				viewOptionSelected((byte) sel);
			}
		});
		
		if (hasDescription()) {
			description = new Label(this, SWT.WRAP);
			description.setText(ServerUIPlugin.getResource("%wizDescription"));
			data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
			data.horizontalSpan = 2;
			data.heightHint = 35;
			description.setLayoutData(data);
		}
	}
	
	protected abstract String getDescriptionLabel();
	
	protected abstract String getTitleLabel();
	
	protected abstract String[] getComboOptions();
	
	protected boolean hasDescription() {
		return true;
	}
	
	protected void setDescription(String text) {
		if (description != null && text != null)
			description.setText(ServerUIPlugin.getResource("%wizDescription") + " " + text);
	}
	
	protected abstract void viewOptionSelected(byte option);
	
	protected TreeViewer getTreeViewer() {
		return treeViewer;
	}

	protected Object getSelection(ISelection sel2) {
		IStructuredSelection sel = (IStructuredSelection) sel2;
		return sel.getFirstElement();
	}
	
	public void refresh() {
		treeViewer.refresh();
	}
	
	public void refresh(Object obj) {
		treeViewer.refresh(obj);
	}

	public void remove(Object obj) {
		treeViewer.remove(obj);
	}
}