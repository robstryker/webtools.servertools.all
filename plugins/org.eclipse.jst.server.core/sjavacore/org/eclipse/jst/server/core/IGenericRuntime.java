/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.core;

import org.eclipse.jdt.launching.IVMInstall;
/**
 * 
 */
public interface IGenericRuntime {
	/**
	 * Return the VM install (installed JRE) that this runtime is using.
	 * 
	 * @return the current VM install
	 */
	public IVMInstall getVMInstall();
}