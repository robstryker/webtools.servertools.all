package org.eclipse.jst.server.tomcat.core.internal;
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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;

import org.eclipse.wst.server.core.IRuntime;
/**
 * 
 */
public class Tomcat32RuntimeTargetHandler extends TomcatRuntimeTargetHandler {
	public String getId() {
		return "org.eclipse.jst.server.tomcat.runtimeTarget.v32";
	}

	public String getLabel() {
		return TomcatPlugin.getResource("%target32runtime");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.target.IServerTargetDelegate#getClasspathEntries()
	 */
	public IClasspathEntry[] resolveClasspathContainer(IRuntime runtime) {
		IPath installPath = runtime.getLocation();
					
		if (installPath == null)
			return new IClasspathEntry[0];
		
		IPath path = installPath.append("lib");
		List list = new ArrayList();
		addLibraryEntries(list, path.toFile(), true);
		
		return resolveList(list);
	}
}