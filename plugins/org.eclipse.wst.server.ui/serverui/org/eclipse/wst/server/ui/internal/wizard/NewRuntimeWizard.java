package org.eclipse.wst.server.ui.internal.wizard;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. � This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
�*
 * Contributors:
 *    IBM - Initial API and implementation
 *
 **********************************************************************/
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.task.FinishWizardFragment;
import org.eclipse.wst.server.ui.internal.task.SaveRuntimeTask;
import org.eclipse.wst.server.ui.internal.wizard.fragment.NewRuntimeWizardFragment;
import org.eclipse.wst.server.ui.wizard.TaskWizard;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
/**
 * A wizard to create a new runtime.
 */
public class NewRuntimeWizard extends TaskWizard implements INewWizard {
	/**
	 * NewRuntimeWizard constructor comment.
	 */
	public NewRuntimeWizard() {
		super(ServerUIPlugin.getResource("%wizNewRuntimeWizardTitle"), new WizardFragment() {
			public void createSubFragments(List list) {
				list.add(new NewRuntimeWizardFragment());
				list.add(new FinishWizardFragment(new SaveRuntimeTask()));
			}
		});

		setForcePreviousAndNextButtons(true);
	}
	
	public void init(IWorkbench newWorkbench, IStructuredSelection newSelection) { }
}