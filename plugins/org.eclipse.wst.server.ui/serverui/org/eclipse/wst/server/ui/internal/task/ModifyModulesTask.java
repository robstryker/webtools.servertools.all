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
package org.eclipse.wst.server.ui.internal.task;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ITaskModel;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.model.IModule;
import org.eclipse.wst.server.core.model.IRunningActionServer;
import org.eclipse.wst.server.core.model.IServerDelegate;
import org.eclipse.wst.server.core.util.Task;

/**
 * 
 */
public class ModifyModulesTask extends Task {
	protected List add;
	protected List remove;
	
	public ModifyModulesTask() { }
	
	public void setAddModules(List add) {
		this.add = add;
	}
	
	public void setRemoveModules(List remove) {
		this.remove = remove;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.task.ITask#doTask()
	 */
	public void execute(IProgressMonitor monitor) throws CoreException {
		if ((add == null || add.isEmpty()) && (remove == null || remove.isEmpty()))
			return;

		IServerWorkingCopy workingCopy = (IServerWorkingCopy) getTaskModel().getObject(ITaskModel.TASK_SERVER);
		
		IServerDelegate delegate = workingCopy.getDelegate();
		if (delegate instanceof IRunningActionServer) {
			byte state = workingCopy.getServerState();
			if (state == IServer.SERVER_STOPPED || state == IServer.SERVER_UNKNOWN) {
				String mode = (String) getTaskModel().getObject(ITaskModel.TASK_LAUNCH_MODE);
				if (mode == null || mode.length() == 0)
					mode = ILaunchManager.DEBUG_MODE;
				
				workingCopy.synchronousStart(mode, monitor);
			}
		}

		// modify modules
		IModule[] remove2 = new IModule[0];
		if (remove != null) {
			remove2 = new IModule[remove.size()];
			remove.toArray(remove2);
		}
		
		IModule[] add2 = new IModule[0];
		if (add != null) {
			add2 = new IModule[add.size()];
			add.toArray(add2);
		}
		
		IFile file = workingCopy.getFile();
		if (file != null && !file.getProject().exists()) {
			IProject project = file.getProject();
			ServerCore.createServerProject(project.getName(), null, monitor);
		}
		
		workingCopy.modifyModules(add2, remove2, monitor);
	}
}