/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.io.File;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.*;
import org.eclipse.jst.server.core.internal.JavaServerPlugin;
import org.eclipse.jst.server.core.internal.ServerProfiler;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
/**
 * 
 */
public class TomcatLaunchConfigurationDelegate extends AbstractJavaLaunchConfigurationDelegate {

	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		IServer server = ServerUtil.getServer(configuration);
		if (server == null) {
			Trace.trace(Trace.FINEST, "Launch configuration could not find server");
			// throw CoreException();
			return;
		}
		
		TomcatServerBehaviour tomcatServer = (TomcatServerBehaviour) server.loadAdapter(TomcatServerBehaviour.class, null);
		
		String mainTypeName = tomcatServer.getRuntimeClass();
		
		IVMInstall vm = verifyVMInstall(configuration);
		
		IVMRunner runner = vm.getVMRunner(mode);
		if (runner == null)
			runner = vm.getVMRunner(ILaunchManager.RUN_MODE);
		
		File workingDir = verifyWorkingDirectory(configuration);
		String workingDirName = null;
		if (workingDir != null)
			workingDirName = workingDir.getAbsolutePath();
		
		// Program & VM args
		String pgmArgs = getProgramArguments(configuration);
		String vmArgs = getVMArguments(configuration);
		String[] envp = getEnvironment(configuration);
		
		if (ILaunchManager.PROFILE_MODE.equals(mode)) {
			ServerProfiler[] sp = JavaServerPlugin.getServerProfilers();
			if (sp == null || sp.length == 0 || runner == null) {
				tomcatServer.stopImpl();
				throw new CoreException(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, Messages.errorNoProfiler, null));
			}
			String vmArgs2 = sp[0].getVMArgs();
			if (vmArgs2 != null)
				vmArgs = vmArgs + " " + vmArgs2;
			
			String[] env = sp[0].getEnvironmentVariables();
			if (env != null && env.length > 0) {
				if (envp == null)
					envp = env;
				else {
					String[] s = new String[env.length + envp.length];
					System.arraycopy(envp, 0, s, 0, envp.length);
					System.arraycopy(env, 0, s, envp.length, env.length);
					envp = s;
				}
			}
		}
		
		ExecutionArguments execArgs = new ExecutionArguments(vmArgs, pgmArgs);
		
		// VM-specific attributes
		Map vmAttributesMap = getVMSpecificAttributesMap(configuration);
		
		// Classpath
		String[] classpath = getClasspath(configuration);
		
		// Create VM config
		VMRunnerConfiguration runConfig = new VMRunnerConfiguration(mainTypeName, classpath);
		runConfig.setProgramArguments(execArgs.getProgramArgumentsArray());
		runConfig.setVMArguments(execArgs.getVMArgumentsArray());
		runConfig.setWorkingDirectory(workingDirName);
		runConfig.setEnvironment(envp);
		runConfig.setVMSpecificAttributesMap(vmAttributesMap);

		// Bootpath
		String[] bootpath = getBootpath(configuration);
		if (bootpath != null && bootpath.length > 0)
			runConfig.setBootClassPath(bootpath);
		
		setDefaultSourceLocator(launch, configuration);
		
		// Launch the configuration
		tomcatServer.setupLaunch(launch, mode, monitor);
		try {
			runner.run(runConfig, launch, monitor);
			tomcatServer.setProcess(launch.getProcesses()[0]);
		} catch (Exception e) {
			// ignore - process failed
		}
	}
}