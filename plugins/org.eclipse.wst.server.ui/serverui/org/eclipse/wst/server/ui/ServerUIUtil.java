package org.eclipse.wst.server.ui;
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
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.window.Window;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.ui.editor.IServerEditorInput;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.internal.editor.ServerEditorInput;
import org.eclipse.wst.server.ui.internal.publish.PublishDialog;
import org.eclipse.wst.server.ui.internal.task.FinishWizardFragment;
import org.eclipse.wst.server.ui.internal.task.InputWizardFragment;
import org.eclipse.wst.server.ui.internal.task.SaveRuntimeTask;
import org.eclipse.wst.server.ui.internal.wizard.ClosableWizardDialog;
import org.eclipse.wst.server.ui.internal.wizard.fragment.NewRuntimeWizardFragment;
import org.eclipse.wst.server.ui.wizard.*;

import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Server UI utility methods.
 */
public class ServerUIUtil {
	/**
	 * ServerUIUtil constructor comment.
	 */
	private ServerUIUtil() {
		super();
	}

	/**
	 * Open the passed server resources in the server editor.
	 *
	 * @param resource org.eclipse.core.resources.IServerResource
	 */
	public static void editServer(IServer server, IServerConfiguration configuration) {
		if (server == null && configuration == null)
			return;

		String serverId = null;
		if (server != null)
			serverId = server.getId();
		String configurationId = null;
		if (configuration != null)
			configurationId = configuration.getId();
		editServer(serverId, configurationId);
	}

	/**
	 * Open the passed resources in the server editor.
	 *
	 * @param resource org.eclipse.core.resources.IServerResource
	 */
	public static void editServer(String serverId, String configurationId) {
		if (serverId == null && configurationId == null)
			return;

		IWorkbenchWindow workbenchWindow = ServerUIPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = workbenchWindow.getActivePage();

		try {
			IServerEditorInput input = new ServerEditorInput(serverId, configurationId);
			page.openEditor(input, IServerEditorInput.EDITOR_ID);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error opening server editor", e);
		}
	}
	
	/**
	 * Publish with the given server control, and display
	 * the publishing in a dialog. If keepOpen is true, the publish
	 * dialog will remain open after publishing. If false, it will
	 * only remain open if there was an error, info, or warning
	 * message.
	 *
	 * @param control org.eclipse.wst.server.core.IServerControl
	 * @param keepOpen boolean
	 * @return IStatus
	 */
	public static IStatus publishWithDialog(IServer server, boolean keepOpen) {
		Shell shell = ServerUIPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow().getShell();
		return PublishDialog.publish(shell, server, keepOpen);
	}

	/**
	 * Publish with the given server control, and display
	 * the publishing in a dialog. If keepOpen is true, the publish
	 * dialog will remain open after publishing. If false, it will
	 * only remain open if there was an error, info, or warning
	 * message.
	 *
	 * @param control org.eclipse.wst.server.core.IServerControl
	 * @param keepOpen boolean
	 * @return IStatus
	 */
	public static IStatus publishWithDialog(Shell shell, IServer server, boolean keepOpen) {
		return PublishDialog.publish(shell, server, keepOpen);
	}

	/**
	 * Prompts the user if the server is dirty. Returns true if the server was
	 * not dirty or if the user decided to continue anyway. Returns false if
	 * the server is dirty and the user chose to cancel the operation.
	 *
	 * @return boolean
	 */
	public static boolean promptIfDirty(Shell shell, IServer server) {
		if (server == null)
			return false;

		String title = ServerUIPlugin.getResource("%resourceDirtyDialogTitle");
		
		if (server != null && server.isAWorkingCopyDirty()) {
			String message = ServerUIPlugin.getResource("%resourceDirtyDialogMessage", server.getName());
			String[] labels = new String[] {ServerUIPlugin.getResource("%resourceDirtyDialogContinue"), IDialogConstants.CANCEL_LABEL};
			MessageDialog dialog = new MessageDialog(shell, title, null, message, MessageDialog.INFORMATION, labels, 0);
	
			if (dialog.open() != 0)
				return false;
		}
	
		IServerConfiguration config = server.getServerConfiguration();
		if (config != null)
			return promptIfDirty(shell, config);
		else
			return true;
	}

	/**
	 * Prompts the user if the server configuration is dirty. Returns true if the server was
	 * not dirty or if the user decided to continue anyway. Returns false if
	 * the server is dirty and the user chose to cancel the operation.
	 *
	 * @return boolean
	 */
	public static boolean promptIfDirty(Shell shell, IServerConfiguration configuration) {
		if (configuration == null)
			return false;

		String title = ServerUIPlugin.getResource("%resourceDirtyDialogTitle");

		if (configuration != null && configuration.isAWorkingCopyDirty()) {
			String message = ServerUIPlugin.getResource("%resourceDirtyDialogMessage", configuration.getName());
			String[] labels = new String[] {ServerUIPlugin.getResource("%resourceDirtyDialogContinue"), IDialogConstants.CANCEL_LABEL};
			MessageDialog dialog = new MessageDialog(shell, title, null, message, MessageDialog.INFORMATION, labels, 0);

			if (dialog.open() != 0)
				return false;
		}
		return true;
	}

	/**
	 * Use the preference to prompt the user to save dirty editors, if applicable.
	 * 
	 * @return boolean  - Returns false if the user cancelled the operation
	 */
	public static boolean saveEditors() {
		byte b = ServerUICore.getPreferences().getSaveEditors();
		if (b == IServerUIPreferences.SAVE_EDITORS_NEVER)
			return true;
		else
			return ServerUIPlugin.getInstance().getWorkbench().saveAllEditors(b == IServerUIPreferences.SAVE_EDITORS_PROMPT);			
	}

	/**
	 * Publishes to the given server, if the preference is set and publishing was required.
	 * 
	 * @return boolean - Returns false if the user cancelled the operation.
	 */
	public static boolean publish(IServer server) {
		if (ServerCore.getServerPreferences().isAutoPublishing() && server.shouldPublish()) {
			// publish first
			IStatus status = publishWithDialog(server, false);

			if (status == null || status.getSeverity() == IStatus.ERROR) // user cancelled
				return false;
		}
		return true;
	}
	
	/**
	 * @deprecated - use showNewRuntimeWizard
	 */
	public static boolean showRuntimePreferencePage(Shell shell) {
		PreferenceManager manager = PlatformUI.getWorkbench().getPreferenceManager();
		IPreferenceNode node = manager.find("org.eclipse.wst.server.ui.preferencePage").findSubNode("org.eclipse.wst.server.ui.runtime.preferencePage");
		PreferenceManager manager2 = new PreferenceManager();
		manager2.addToRoot(node);
		final PreferenceDialog dialog = new PreferenceDialog(shell, manager2);
		final boolean[] result = new boolean[] { false };
		BusyIndicator.showWhile(shell.getDisplay(), new Runnable() {
			public void run() {
				dialog.create();
				if (dialog.open() == Window.OK)
					result[0] = true;
			}
		});
		return result[0];
	}

	public static boolean showNewRuntimeWizard(Shell shell) {
		return showNewRuntimeWizard(shell, null, null);
	}
	
	public static boolean showNewRuntimeWizard(Shell shell, final String runtimeTypeId) {
		IRuntimeType runtimeType = ServerCore.getRuntimeType(runtimeTypeId);
		if (runtimeType != null) {
			try {
				final IRuntimeWorkingCopy runtime = runtimeType.createRuntime(null);
				IWizardFragment fragment = new WizardFragment() {
					public void createSubFragments(List list) {
						list.add(new InputWizardFragment(ITaskModel.TASK_RUNTIME, runtime));
						list.add(ServerUICore.getWizardFragment(runtimeTypeId));
						list.add(new FinishWizardFragment(new SaveRuntimeTask()));
					}
				};
				TaskWizard wizard = new TaskWizard(ServerUIPlugin.getResource("%wizNewRuntimeWizardTitle"), fragment);
				wizard.setForcePreviousAndNextButtons(true);
				ClosableWizardDialog dialog = new ClosableWizardDialog(shell, wizard);
				return (dialog.open() == IDialogConstants.OK_ID);
			} catch (Exception e) {
				return false;
			}
		} else
			return showNewRuntimeWizard(shell, null, null, runtimeTypeId);
	}

	public static boolean showNewRuntimeWizard(Shell shell, final String type, final String version) {
		return showNewRuntimeWizard(shell, type, version, null);
	}

	public static boolean showNewRuntimeWizard(Shell shell, final String type, final String version, final String runtimeTypeId) {
		IWizardFragment fragment = new WizardFragment() {
			public void createSubFragments(List list) {
				list.add(new NewRuntimeWizardFragment(type, version, runtimeTypeId));
				list.add(new FinishWizardFragment(new SaveRuntimeTask()));
			}
		};
		TaskWizard wizard = new TaskWizard(ServerUIPlugin.getResource("%wizNewRuntimeWizardTitle"), fragment);
		wizard.setForcePreviousAndNextButtons(true);
		ClosableWizardDialog dialog = new ClosableWizardDialog(shell, wizard);
		return (dialog.open() == IDialogConstants.OK_ID);
	}
}