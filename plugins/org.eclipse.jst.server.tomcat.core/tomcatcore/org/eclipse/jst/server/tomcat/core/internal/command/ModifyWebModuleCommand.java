package org.eclipse.jst.server.tomcat.core.internal.command;
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
import org.eclipse.jst.server.tomcat.core.ITomcatConfigurationWorkingCopy;
import org.eclipse.jst.server.tomcat.core.WebModule;
import org.eclipse.jst.server.tomcat.core.internal.TomcatPlugin;
/**
 * Command to change a web module.
 */
public class ModifyWebModuleCommand extends ConfigurationCommand {
	protected int index;
	protected WebModule oldModule;
	protected WebModule newModule;

	public ModifyWebModuleCommand(ITomcatConfigurationWorkingCopy configuration, int index, WebModule module) {
		super(configuration);
		this.index = index;
		newModule = module;
	}

	/**
	 * Execute the command.
	 * @return boolean
	 */
	public boolean execute() {
		oldModule = (WebModule) configuration.getWebModules().get(index);
		configuration.modifyWebModule(index, newModule.getDocumentBase(), newModule.getPath(), newModule.isReloadable());
		return true;
	}

	/**
	 * Returns this command's description.
	 * @return java.lang.String
	 */
	public String getDescription() {
		return TomcatPlugin.getResource("%configurationEditorActionModifyWebModuleDescription");
	}

	/**
	 * Returns this command's label.
	 * @return java.lang.String
	 */
	public String getName() {
		return TomcatPlugin.getResource("%configurationEditorActionModifyWebModule");
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		configuration.modifyWebModule(index, oldModule.getDocumentBase(), oldModule.getPath(), oldModule.isReloadable());
	}
}