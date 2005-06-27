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
package org.eclipse.wst.server.ui.internal.view.servers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.wizard.ClosableWizardDialog;
import org.eclipse.wst.server.ui.internal.wizard.ModifyModulesWizard;
/**
 * 
 */
public class ModuleSloshAction extends AbstractServerAction {
	public ModuleSloshAction(Shell shell, ISelectionProvider selectionProvider, String name) {
		super(shell, selectionProvider, name);
	}

	/**
	 * Return true if this server can currently be acted on.
	 * @return boolean
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	public boolean accept(IServer server) {
		return true;
	}

	/**
	 * Perform action on this server.
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	public void perform(final IServer server) {
		if (server == null)
			return;
		
		//if (!ServerUIUtil.promptIfDirty(shell, server))
		//	return;
		
		// check if there are any projects first
		// get currently deployed modules
		List deployed = new ArrayList();
		List modules = new ArrayList();
		IModule[] currentModules = server.getModules();
		if (currentModules != null) {
			int size = currentModules.length;
			for (int i = 0; i < size; i++) {
				deployed.add(currentModules[i]);
			}
		}

		// get remaining modules
		IModule[] modules2 = ServerUtil.getModules(server.getServerType().getRuntimeType().getModuleTypes());
		if (modules != null) {
			int size = modules2.length;
			for (int i = 0; i < size; i++) {
				IModule module = modules2[i];
				if (!deployed.contains(module)) {
					IStatus status = server.canModifyModules(new IModule[] { module }, null, null);
					if (status != null && status.getSeverity() != IStatus.ERROR)
						modules.add(module);
				}
			}
		}
		
		if (deployed.isEmpty() && modules.isEmpty()) {
			MessageDialog.openInformation(shell, Messages.defaultDialogTitle, Messages.dialogAddRemoveModulesNone);
			return;
		}

		ModifyModulesWizard wizard = new ModifyModulesWizard(server);
		ClosableWizardDialog dialog = new ClosableWizardDialog(shell, wizard);
		dialog.open();
	}
}