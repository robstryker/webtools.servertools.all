/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
�*
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.resources;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.server.core.model.IModule;
/**
 * 
 */
public class ProjectModuleFile extends ProjectModuleResource implements IModuleFile {
	public ProjectModuleFile(IModule module, IModuleFolder parent, IFile file) {
		super(module, parent, file);
	}

	/*
	 * @see IModuleFile#getContents()
	 */
	public InputStream getContents() throws CoreException {
		IFile file = (IFile) getResource();
		return file.getContents();
	}
}