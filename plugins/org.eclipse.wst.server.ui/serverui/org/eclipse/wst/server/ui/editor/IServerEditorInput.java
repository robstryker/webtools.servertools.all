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
 *
 **********************************************************************/
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.ui.IEditorInput;
/**
 * This is the editor input for the server and server
 * configuration editor. The input includes both a server
 * and server configuration.
 *
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IServerEditorInput extends IEditorInput {
	public static final String EDITOR_ID = ServerUICore.PLUGIN_ID + ".editor";

	/**
	 * Returns the server id.
	 *
	 * @return java.lang.String
	 */
	public String getServerId();

	/**
	 * Returns the server configuration id.
	 *
	 * @return java.lang.String
	 */
	public String getServerConfigurationId();
}