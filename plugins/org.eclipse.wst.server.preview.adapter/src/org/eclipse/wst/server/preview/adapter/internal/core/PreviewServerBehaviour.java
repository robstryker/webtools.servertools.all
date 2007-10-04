/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.preview.adapter.internal.core;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerPort;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;
import org.eclipse.wst.server.core.util.IStaticWeb;
import org.eclipse.wst.server.core.util.PublishUtil;
import org.eclipse.wst.server.core.util.SocketUtil;
/**
 * Preview server.
 */
public class PreviewServerBehaviour extends ServerBehaviourDelegate {
	// the thread used to ping the server to check for startup
	protected transient PingThread ping = null;
	protected transient IDebugEventSetListener processListener;

	/**
	 * PreviewServer.
	 */
	public PreviewServerBehaviour() {
		super();
	}

	public void initialize(IProgressMonitor monitor) {
		// do nothing
	}

	public PreviewRuntime getPreviewRuntime() {
		if (getServer().getRuntime() == null)
			return null;

		return (PreviewRuntime) getServer().getRuntime().loadAdapter(PreviewRuntime.class, null);
	}

	public PreviewServer getPreviewServer() {
		return (PreviewServer) getServer().getAdapter(PreviewServer.class);
	}

	/**
	 * Returns the runtime base path for relative paths in the server
	 * configuration.
	 * 
	 * @return the base path
	 */
	public IPath getRuntimeBaseDirectory() {
		return getServer().getRuntime().getLocation();
	}

	/**
	 * Setup for starting the server.
	 * 
	 * @param launch ILaunch
	 * @param launchMode String
	 * @param monitor IProgressMonitor
	 * @throws CoreException if anything goes wrong
	 */
	protected void setupLaunch(ILaunch launch, String launchMode, IProgressMonitor monitor) throws CoreException {
		// check that ports are free
		ServerPort[] ports = getPreviewServer().getServerPorts();
		int port = ports[0].getPort();
		
		if (SocketUtil.isPortInUse(port, 5))
			throw new CoreException(new Status(IStatus.ERROR, PreviewPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorPortInUse, new String[] {port + "", getServer().getName()}), null));
		
		// generate preview config file
		XMLMemento memento = XMLMemento.createWriteRoot("server");
		memento.putInteger("port", port);
		
		IModule[] modules = getServer().getModules();
		int size = modules.length;
		for (int i = 0; i < size; i++) {
			IMemento mod = memento.createChild("module");
			mod.putString("name", modules[i].getName());
			String type = modules[i].getModuleType().getId();
			if ("wst.web".equals(type)) {
				IStaticWeb staticWeb = (IStaticWeb) modules[i].loadAdapter(IStaticWeb.class, null);
				mod.putString("context", staticWeb.getContextRoot());
				mod.putString("type", "static");
			}
			mod.putString("path", getModulePublishDirectory(modules[i]).toPortableString());
		}
		try {
			memento.saveToFile(getTempDirectory().append("preview.xml").toOSString());
		} catch (IOException e) {
			Trace.trace(Trace.SEVERE, "Could not write preview config", e);
			throw new CoreException(new Status(IStatus.ERROR, PreviewPlugin.PLUGIN_ID, 0, "Could not write preview configuration", null));
		}
		
		setServerRestartState(false);
		setServerState(IServer.STATE_STARTING);
		setMode(launchMode);
		
		// ping server to check for startup
		try {
			String url = "http://localhost";
			if (port != 80)
				url += ":" + port;
			ping = new PingThread(getServer(), url, this);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Can't ping for Tomcat startup.");
		}
	}

	protected void addProcessListener(final IProcess newProcess) {
		if (processListener != null || newProcess == null)
			return;
		
		processListener = new IDebugEventSetListener() {
			public void handleDebugEvents(DebugEvent[] events) {
				if (events != null) {
					int size = events.length;
					for (int i = 0; i < size; i++) {
						if (newProcess != null && newProcess.equals(events[i].getSource()) && events[i].getKind() == DebugEvent.TERMINATE) {
							stop(true);
						}
					}
				}
			}
		};
		DebugPlugin.getDefault().addDebugEventListener(processListener);
	}

	protected void setServerStarted() {
		setServerState(IServer.STATE_STARTED);
	}

	protected void publishServer(int kind, IProgressMonitor monitor) throws CoreException {
		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.done();

		setServerPublishState(IServer.PUBLISH_STATE_NONE);
	}

	/*
	 * Publishes the given module to the server.
	 */
	protected void publishModule(int kind, int deltaKind, IModule[] moduleTree, IProgressMonitor monitor) throws CoreException {
		IModule module = moduleTree[moduleTree.length - 1]; 
		IPath to = getModulePublishDirectory(module);
		
		if (kind == IServer.PUBLISH_CLEAN || deltaKind == ServerBehaviourDelegate.REMOVED) {
			IStatus[] status = PublishUtil.deleteDirectory(to.toFile(), monitor);
			throwException(status);
		}
		
		IModuleResource[] res = getResources(moduleTree);
		IStatus[] status = PublishUtil.publishSmart(res, to, monitor);
		throwException(status);
		
		setModulePublishState(moduleTree, IServer.PUBLISH_STATE_NONE);
	}

	/**
	 * Utility method to throw a CoreException based on the contents of a list of
	 * error and warning status.
	 * 
	 * @param status a List containing error and warning IStatus
	 * @throws CoreException
	 */
	private static void throwException(IStatus[] status) throws CoreException {
		if (status == null || status.length == 0)
			return;
		
		if (status.length == 1)
			throw new CoreException(status[0]);
		
		String message = Messages.errorPublish;
		MultiStatus status2 = new MultiStatus(PreviewPlugin.PLUGIN_ID, 0, status, message, null);
		throw new CoreException(status2);
	}

	public void restart(String launchMode) throws CoreException {
		setServerState(IServer.STATE_STOPPED);
		setServerState(IServer.STATE_STARTED);
	}

	/**
	 * Cleanly shuts down and terminates the server.
	 * 
	 * @param force <code>true</code> to kill the server
	 */
	public void stop(boolean force) {
		int state = getServer().getServerState();
		if (state == IServer.STATE_STOPPED)
			return;
		
		setServerState(IServer.STATE_STOPPING);
		
		if (ping != null) {
			ping.stop();
			ping = null;
		}
		
		if (processListener != null) {
			DebugPlugin.getDefault().removeDebugEventListener(processListener);
			processListener = null;
		}
		
		try {
			Trace.trace(Trace.FINEST, "Killing the process");
			ILaunch launch = getServer().getLaunch();
			if (launch != null)
				launch.terminate();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error killing the process", e);
		}
		
		setServerState(IServer.STATE_STOPPED);
	}

	protected IPath getTempDirectory() {
		return super.getTempDirectory();
	}

	/**
	 * Returns the module's publish path.
	 * 
	 * @param module a module
	 * @return the publish directory for the module
	 */
	protected IPath getModulePublishDirectory(IModule module) {
		return getTempDirectory().append(module.getName());
	}

	/**
	 * Return a string representation of this object.
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		return "PreviewServer";
	}

	protected IModuleResourceDelta[] getPublishedResourceDelta(IModule[] module) {
		return super.getPublishedResourceDelta(module);
	}
}