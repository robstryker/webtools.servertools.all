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
package org.eclipse.wst.server.ui.internal.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.wst.server.core.IElement;
import org.eclipse.wst.server.core.ITask;
import org.eclipse.wst.server.ui.editor.ICommandManager;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;

/**
 * A command manager for a single server resource.
 */
public class ServerResourceCommandManager implements ICommandManager {
	protected ServerEditor editor;
	protected GlobalCommandManager commandManager;
	protected String id;

	public ServerResourceCommandManager(ServerEditor editor, String id, GlobalCommandManager commandManager) {
		this.editor = editor;
		this.commandManager = commandManager;
		this.id = id;
	}
	
	public boolean isReadOnly() {
		return commandManager.isReadOnly(id);
	}

	/**
	 * Execute the given command and place it in the undo stack.
	 * If the command cannot be undone, the user will be notifed
	 * before it is executed.
	 *
	 * @param command ICommand
	 */
	public void executeCommand(ITask command) {
		if (!validateEdit())
			return;

		if (commandManager.isReadOnly(id)) {
			warnReadOnly();
			return;
		}
		commandManager.executeCommand(id, command);
	}

	protected void warnReadOnly() {
		String title = ServerUIPlugin.getResource("%editorfileWarnTitle");
		String message = ServerUIPlugin.getResource("%editorfileWarnMessage");
		
		MessageDialog.openWarning(editor.getEditorSite().getShell(), title, message);
	}

	/**
	 * 
	 */
	protected boolean validateEdit() {
		if (commandManager.isDirty(id))
			return true;

		IFile[] files = commandManager.getReadOnlyFiles(id);
		if (files.length == 0)
			return true;
		
		IStatus status = ResourcesPlugin.getWorkspace().validateEdit(files, editor.getEditorSite().getShell());
		
		if (status.getSeverity() == IStatus.ERROR) {
			// inform user
			String message = ServerUIPlugin.getResource("%editorValidateEditFailureMessage");
			ErrorDialog.openError(editor.getEditorSite().getShell(), ServerUIPlugin.getResource("%errorDialogTitle"), message, status);
			
			// change to read-only
			commandManager.setReadOnly(id, true);
			
			// do not execute command
			return false;
		} else {
			// check file timestamp
			IElement serverfile = commandManager.getServerResource(id);
			if (commandManager.hasChanged(id))
				editor.promptReloadServerFile(id, serverfile);
			
			// allow edit
			return true;
		}
	}
}