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
package org.eclipse.jst.server.core;

import java.util.Properties;

import org.eclipse.wst.server.core.ILaunchable;
/**
 * 
 * @since 1.0
 */
public class JndiLaunchable implements ILaunchable {
	private Properties props;
	private String jndiName;
	private String server;
	private int port;

	public JndiLaunchable(Properties props, String jndiName) {
		this.jndiName = jndiName;
		this.props = props;
	}

	public Properties getProperties() {
		return props;
	}

	public String getJNDIName() {
		return jndiName;
	}
	
	public String getServer() {
		return server;
	}
	
	public int getHTTPPort() {
		return port;
	}
}