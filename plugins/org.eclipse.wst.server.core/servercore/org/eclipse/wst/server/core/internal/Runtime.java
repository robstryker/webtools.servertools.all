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
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.*;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.IRuntimeDelegate;
import org.osgi.framework.Bundle;
/**
 * 
 */
public class Runtime extends Base implements IRuntime {
	protected static final String PROP_RUNTIME_TYPE_ID = "runtime-type-id";
	protected static final String PROP_LOCATION = "location";
	protected static final String PROP_TEST_ENVIRONMENT = "test-environment";

	protected IRuntimeType runtimeType;
	protected IRuntimeDelegate delegate;

	public Runtime(IFile file) {
		super(file);
	}

	public Runtime(IFile file, String id, IRuntimeType runtimeType) {
		super(file, id);
		this.runtimeType = runtimeType;
		map.put(PROP_NAME, runtimeType.getName());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IRuntime#getRuntimeType()
	 */
	public IRuntimeType getRuntimeType() {
		return runtimeType;
	}

	/**
	 * Return the validation status of the runtime.
	 * 
	 * @return
	 */
	public IStatus validate() {
		try {
			return getDelegate().validate();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate validate() " + toString(), e);
			return null;
		}
	}

	public IRuntimeDelegate getDelegate() {
		if (delegate != null)
			return delegate;
		
		synchronized (this) {
			if (delegate == null) {
				try {
					long time = System.currentTimeMillis();
					RuntimeType runtimeType2 = (RuntimeType) runtimeType;
					delegate = (IRuntimeDelegate) runtimeType2.getElement().createExecutableExtension("class");
					delegate.initialize(this);
					Trace.trace(Trace.PERFORMANCE, "Runtime.getDelegate(): <" + (System.currentTimeMillis() - time) + "> " + getRuntimeType().getId());
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Could not create delegate " + toString(), e);
				}
			}
		}
		return delegate;
	}
	
	/**
	 * Returns true if the delegate has been loaded.
	 * 
	 * @return
	 */
	public boolean isDelegateLoaded() {
		return delegate != null;
	}
	
	public void dispose() {
		if (delegate != null)
			delegate.dispose();
	}
	
	public IRuntimeWorkingCopy getWorkingCopy() {
		IRuntimeWorkingCopy wc = new RuntimeWorkingCopy(this); 
		addWorkingCopy(wc);
		return wc;
	}
	
	public boolean isWorkingCopy() {
		return false;
	}
	
	public boolean isDelegatePluginActivated() {
		IConfigurationElement element = ((RuntimeType) runtimeType).getElement();
		String pluginId = element.getDeclaringExtension().getNamespace();
		return Platform.getBundle(pluginId).getState() == Bundle.ACTIVE;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IRuntime#getLocation()
	 */
	public IPath getLocation() {
		String temp = getAttribute(PROP_LOCATION, (String)null);
		if (temp == null)
			return null;
		else
			return new Path(temp);
	}
	
	protected void deleteFromMetadata() {
		ResourceManager rm = (ResourceManager) ServerCore.getResourceManager();
		rm.removeRuntime(this);
	}

	protected void saveToMetadata(IProgressMonitor monitor) {
		super.saveToMetadata(monitor);
		ResourceManager rm = (ResourceManager) ServerCore.getResourceManager();
		rm.addRuntime(this);
	}

	protected String getXMLRoot() {
		return "runtime";
	}
	
	public boolean isTestEnvironment() {
		return getAttribute(PROP_TEST_ENVIRONMENT, false);
	}

	protected void setInternal(RuntimeWorkingCopy wc) {
		map = wc.map;
		runtimeType = wc.runtimeType;
		file = wc.file;
		delegate = wc.delegate;
		
		int timestamp = wc.getTimestamp();
		map.put("timestamp", Integer.toString(timestamp+1));
	}

	protected void loadState(IMemento memento) {
		String runtimeTypeId = memento.getString(PROP_RUNTIME_TYPE_ID);
		runtimeType = ServerCore.getRuntimeType(runtimeTypeId);
	}

	protected void saveState(IMemento memento) {
		if (runtimeType != null)
			memento.putString(PROP_RUNTIME_TYPE_ID, runtimeType.getId());
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Runtime))
			return false;
		
		Runtime runtime = (Runtime) obj;
		return runtime.getId().equals(getId());
	}
	
	public String toString() {
		return "Runtime[" + getId() + ", " + getName() + ", " + getLocation() + ", " + getRuntimeType() + "]";
	}
}