package org.eclipse.wst.server.ui.editor;
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
import org.eclipse.ui.IEditorInput;

import org.eclipse.wst.server.core.IServerConfigurationWorkingCopy;
import org.eclipse.wst.server.core.IServerWorkingCopy;
/**
 *
 */
public interface IServerEditorPartInput extends IEditorInput {
	/**
	 * Returns the server to be edited.
	 *
	 * @return IServerWorkingCopy
	 */
	public IServerWorkingCopy getServer();

	/**
	 * Returns true if the server is read-only.
	 * 
	 * @return boolean
	 */
	public boolean isServerReadOnly();
	
	/**
	 * Gets the command manager. The editor is only responsible for creating an
	 * ICommand and passing it to the command manager, which actually performs
	 * the action and updates the server.
	 *
	 * @param commandManager ICommandManager
	 */
	public ICommandManager getServerCommandManager();

	/**
	 * Returns the server configuration to be edited.
	 * 
	 * @return IServerConfigurationWorkingCopy
	 */
	public IServerConfigurationWorkingCopy getServerConfiguration();

	/**
	 * Returns true if the server configuration is read-only.
	 * 
	 * @return boolean
	 */
	public boolean isServerConfigurationReadOnly();
	
	/**
	 * Gets the command manager. The editor is only responsible for creating an
	 * ICommand and passing it to the command manager, which actually performs
	 * the action and updates the server configuration.
	 *
	 * @param commandManager ICommandManager
	 */
	public ICommandManager getServerConfigurationCommandManager();
}