/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
�*
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal.operations;

import org.eclipse.core.runtime.IStatus;
/**
 * A server.
 */
public interface IServer {
	/**
	 * 
	 */
	public boolean canOperate(IServerOperation[] operations);

	/**
	 * 
	 * @param operation
	 * @param state
	 * @param synchronous
	 * @return
	 */
	public IStatus operate(IServerOperation[] operations, ServerState state, boolean synchronous);
}