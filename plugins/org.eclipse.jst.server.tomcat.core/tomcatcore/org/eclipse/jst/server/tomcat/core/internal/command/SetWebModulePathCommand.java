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
 * Command to modify the path of a Web module.
 */
public class SetWebModulePathCommand extends ConfigurationCommand {
	protected int index;
	protected WebModule oldModule;
	protected String path;

	/**
	 * SetWebModulePathCommand constructor comment.
	 */
	public SetWebModulePathCommand(ITomcatConfigurationWorkingCopy configuration, int index, String contextRoot) {
		super(configuration);
		this.index = index;
		this.path = contextRoot;
	}

	/**
	 * Execute the command.
	 * @return boolean
	 */
	public boolean execute() {
		oldModule = (WebModule) configuration.getWebModules().get(index);
		configuration.removeWebModule(index);
		
		WebModule module = new WebModule(path, oldModule.getDocumentBase(), oldModule.getMemento(), oldModule.isReloadable());
		configuration.addWebModule(index, module);
		return true;
	}

	/**
	 * Returns this command's description.
	 * @return java.lang.String
	 */
	public String getDescription() {
		if (oldModule == null)
			oldModule = (WebModule) configuration.getWebModules().get(index);
		
		return TomcatPlugin.getResource("%configurationEditorActionEditWebModuleDescription", oldModule.getPath(), path);
	}

	/**
	 * Returns this command's label.
	 * @return java.lang.String
	 */
	public String getName() {
		return TomcatPlugin.getResource("%configurationEditorActionEditWebModulePath");
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		configuration.removeWebModule(index);
		configuration.addWebModule(index, oldModule);
	}
}