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
 * Command to remove a web module.
 */
public class RemoveWebModuleCommand extends ConfigurationCommand {
	protected int index;
	protected WebModule module;

	/**
	 * RemoveWebModuleCommand constructor comment.
	 */
	public RemoveWebModuleCommand(ITomcatConfigurationWorkingCopy configuration, int index) {
		super(configuration);
		this.index = index;
	}

	/**
	 * Execute the command.
	 * @return boolean
	 */
	public boolean execute() {
		module = (WebModule) configuration.getWebModules().get(index);
		configuration.removeWebModule(index);
		return true;
	}

	/**
	 * Returns this command's description.
	 * @return java.lang.String
	 */
	public String getDescription() {
		if (module == null)
			module = (WebModule) configuration.getWebModules().get(index);
		return TomcatPlugin.getResource("%configurationEditorActionRemoveWebModuleDescription", module.getPath());
	}

	/**
	 * Returns this command's label.
	 * @return java.lang.String
	 */
	public String getName() {
		return TomcatPlugin.getResource("%configurationEditorActionRemoveWebModule");
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		configuration.addWebModule(index, module);
	}
}