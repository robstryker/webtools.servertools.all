/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
�*
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.model;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.wst.server.core.ILaunchable;
import org.eclipse.wst.server.core.IServer;
/**
 * A launchable client is a client side application or test harness that can
 * be launched (run) against a resource running on a server.
 * <p>
 * This abstract class is intended to be extended only by clients
 * to extend the <code>clients</code> extension point.
 * </p>
 */
public abstract class ClientDelegate {
	/**
	 * Returns true if this launchable can be run by this client.
	 * 
	 * @param server
	 * @param launchable
	 * @param launchMode
	 * @return 
	 */
	public abstract boolean supports(IServer server, ILaunchable launchable, String launchMode);

	/**
	 * Opens or executes on the launchable.
	 * 
	 * @param server
	 * @param launchable
	 * @param launchMode
	 * @param launch
	 * @return 
	 */
	public abstract IStatus launch(IServer server, ILaunchable launchable, String launchMode, ILaunch launch);
}