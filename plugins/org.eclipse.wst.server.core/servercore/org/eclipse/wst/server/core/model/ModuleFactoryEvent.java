/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
�*
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.model;

import org.eclipse.wst.server.core.IModule;
/**
 * An event fired when a module factory changes.
 */
public class ModuleFactoryEvent {
	protected IModule[] added;
	protected IModule[] removed;

	public ModuleFactoryEvent(IModule[] added, IModule[] removed) {
		this.added = added;
		this.removed = removed;
	}

	/**
	 * Returns any modules that have been added.
	 * 
	 * @return org.eclipse.wst.server.core.model.IModule[]
	 */
	public IModule[] getAddedModules() {
		return added;
	}

	/**
	 * Returns any modules that have been removed.
	 * 
	 * @return org.eclipse.wst.server.core.model.IModule[]
	 */
	public IModule[] getRemovedModules() {
		return removed;
	}
}