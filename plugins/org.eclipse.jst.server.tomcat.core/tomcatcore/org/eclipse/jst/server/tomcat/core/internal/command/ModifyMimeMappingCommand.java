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
import org.eclipse.jst.server.tomcat.core.internal.MimeMapping;
import org.eclipse.jst.server.tomcat.core.internal.TomcatPlugin;
/**
 * Command to change a mime type extension.
 */
public class ModifyMimeMappingCommand extends ConfigurationCommand {
	protected int index;
	protected MimeMapping oldMap;
	protected MimeMapping newMap;

	public ModifyMimeMappingCommand(ITomcatConfigurationWorkingCopy configuration, int index, MimeMapping map) {
		super(configuration);
		this.index = index;
		newMap = map;
	}

	/**
	 * Execute the command.
	 * @return boolean
	 */
	public boolean execute() {
		oldMap = (MimeMapping) configuration.getMimeMappings().get(index);
		configuration.modifyMimeMapping(index, newMap);
		return true;
	}

	/**
	 * Returns this command's description.
	 * @return java.lang.String
	 */
	public String getDescription() {
		return TomcatPlugin.getResource("%configurationEditorActionModifyMimeMappingDescription");
	}

	/**
	 * Returns this command's label.
	 * @return java.lang.String
	 */
	public String getName() {
		return TomcatPlugin.getResource("%configurationEditorActionModifyMimeMapping");
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		configuration.modifyMimeMapping(index, oldMap);
	}
}