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

import org.eclipse.wst.server.core.IClient;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.wizard.page.SelectClientComposite;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.TaskWizard;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
import org.eclipse.swt.widgets.Composite;

/**
 * A wizard used to select a server client from a list.
 */
public class SelectClientWizard extends TaskWizard {
	protected static SelectClientComposite comp;

	/**
	 * SelectClientWizard constructor comment.
	 */
	public SelectClientWizard(final List elements) {
		super(ServerUIPlugin.getResource("%wizSelectClientWizardTitle"),
			new WizardFragment() {						
				public boolean hasComposite() {
					return true;
				}

				/* (non-Javadoc)
				 * @see org.eclipse.wst.server.ui.internal.task.WizardTask#getWizardPage()
				 */
				public Composite createComposite(Composite parent, IWizardHandle wizard) {
					comp = new SelectClientComposite(parent, wizard, elements);
					return comp;
				}
			}
		);

		setForcePreviousAndNextButtons(true);
	}

	/**
	 * Return the selected client.
	 * @return org.eclipse.wst.server.core.model.IServerClient
	 */
	public IClient getSelectedClient() {
		return comp.getSelectedClient();
	}
}