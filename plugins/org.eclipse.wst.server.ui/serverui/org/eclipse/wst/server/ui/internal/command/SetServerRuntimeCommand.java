package org.eclipse.wst.server.ui.internal.command;
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
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;

/**
 * Command to change the server runtime.
 */
public class SetServerRuntimeCommand extends ServerCommand {
	protected IRuntime runtime;
	protected IRuntime oldRuntime;

	/**
	 * SetServerRuntimeCommand constructor comment.
	 */
	public SetServerRuntimeCommand(IServerWorkingCopy server, IRuntime runtime) {
		super(server);
		this.runtime = runtime;
	}

	/**
	 * Execute the command.
	 * @return boolean
	 */
	public boolean execute() {
		oldRuntime = server.getRuntime();
		server.setRuntime(runtime);
		return true;
	}

	/**
	 * Returns this command's description.
	 * @return java.lang.String
	 */
	public String getDescription() {
		return ServerUIPlugin.getResource("%serverEditorOverviewRuntimeDescription");
	}

	/**
	 * Returns this command's label.
	 * @return java.lang.String
	 */
	public String getName() {
		return ServerUIPlugin.getResource("%serverEditorOverviewRuntimeCommand");
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		server.setRuntime(oldRuntime);
	}
}