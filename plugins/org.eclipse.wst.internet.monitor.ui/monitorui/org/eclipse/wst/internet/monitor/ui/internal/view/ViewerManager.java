/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 �*
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.internet.monitor.ui.internal.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.internet.monitor.core.*;
import org.eclipse.wst.internet.monitor.ui.ContentViewer;
import org.eclipse.wst.internet.monitor.ui.EditableContentViewer;
import org.eclipse.wst.internet.monitor.ui.internal.MonitorUIPlugin;
import org.eclipse.wst.internet.monitor.ui.internal.Trace;
import org.eclipse.wst.internet.monitor.ui.internal.viewers.ByteViewer;
import org.eclipse.wst.internet.monitor.ui.internal.viewers.HeaderViewer;
/**
 * 
 */
public class ViewerManager implements IViewerManager{
	private boolean displayHeaderInf;

	protected ContentViewer reqViewer;
	protected ContentViewer respViewer;

	protected HeaderViewer reqHeader;
	protected HeaderViewer respHeader;

	protected Composite reqHComp;
	protected Composite reqVComp;

	protected Composite respHComp;
	protected Composite respVComp;

	protected List viewers;

	protected IRequest request;

	protected List filters = new ArrayList();

	public ViewerManager(Composite reqHeadParent, Composite reqViewParent, Composite respHeadParent, Composite respViewParent) {
		reqHComp = reqHeadParent;
		respHComp = respHeadParent;
		reqVComp = reqViewParent;
		respVComp = respViewParent;
		reqHeader = new HeaderViewer(reqHComp, HeaderViewer.REQUEST_HEADER);
		respHeader = new HeaderViewer(respHComp, HeaderViewer.RESPONSE_HEADER);
		reqViewer = new ByteViewer();
		reqViewer.init(reqVComp);
		respViewer = new ByteViewer();
		respViewer.init(respVComp);
		setDisplayHeaderInfo(MonitorUIPlugin.getShowHeaderPreference());
		setAvailableViewers();
	}

	private void setAvailableViewers() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(MonitorUIPlugin.PLUGIN_ID, "viewers");
		int size = cf.length;
		viewers = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			viewers.add(cf[i]);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.internet.monitor.internal.view.IViewerManager#setDisplayHeaderInfo(boolean)
	 */
	public void setDisplayHeaderInfo(boolean b) {
		displayHeaderInf = b;
		reqHeader.setDisplayHeader(b);
		respHeader.setDisplayHeader(b);
		MonitorUIPlugin.setShowHeaderPreference(b);
		if (b) {
			reqHeader.setEditable(false);
			if (request instanceof IResendRequest && request.getResponse(IRequest.TRANSPORT) == null) {
				reqHeader.setEditable(true);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.internet.monitor.internal.view.IViewerManager#getDisplayHeaderInfo()
	 */
	public boolean getDisplayHeaderInfo() {
		return displayHeaderInf;
	}

	public void setRequest(IRequest rr) {
		// Maintain the state of the request and request header if they've been modified.
		if (request instanceof IResendRequest
				&& request.getResponse(IRequest.ALL) == null) {
			if (reqViewer instanceof EditableContentViewer) {
				IResendRequest resRequest = (IResendRequest) request;
				EditableContentViewer editViewer = (EditableContentViewer) reqViewer;
				byte[] content = editViewer.getContent();
				if (content != null && !MonitorCore.parse(resRequest.getRequest(IRequest.CONTENT)).equals(MonitorCore.parse(content))) {
					resRequest.setRequest(content, IRequest.CONTENT);
				}
				byte[] header = reqHeader.getContent();
				if (header != null && !MonitorCore.parse(resRequest.getRequest(IRequest.TRANSPORT)).equals(MonitorCore.parse(header))) {
					resRequest.setRequest(header, IRequest.TRANSPORT);
				}
			}
		}
		reqHeader.setRequestResponse(rr);
		respHeader.setRequestResponse(rr);
		byte[] b = null;
		if (rr != null)
			b = filter(rr.getRequest(IRequest.CONTENT));
		reqViewer.setContent(b);
		b = null;
		if (rr != null)
			b = filter(rr.getResponse(IRequest.CONTENT));
		respViewer.setContent(b);
		request = rr;
		// Set the editor to editable if the request hasn't  been sent and the
		// editor can be set as editable.
		if (request instanceof IResendRequest
				&& request.getResponse(IRequest.ALL) == null) {
			if (displayHeaderInf) {
				reqHeader.setEditable(true);
			}
			if (reqViewer instanceof EditableContentViewer)
				((EditableContentViewer) reqViewer).setEditable(true);
		} else {
			if (displayHeaderInf) {
				reqHeader.setEditable(false);
			}
			if (reqViewer instanceof EditableContentViewer)
				((EditableContentViewer) reqViewer).setEditable(false);
		}
	}

	public void addFilter(IContentFilter filter) {
		filters.add(filter);
		setRequest(request);
	}

	public void removeFilter(IContentFilter filter) {
		filters.remove(filter);
		setRequest(request);
	}

	protected byte[] filter(byte[] b) {
		if (b == null)
			return null;
		Iterator iterator = filters.iterator();
		while (iterator.hasNext()) {
			IContentFilter filter = (IContentFilter) iterator.next();
			try {
				b = filter.filter(request, false, b);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Error while filtering with " + filter.getId(), e);
			}
		}
		return b;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tcpip.monitor.internal.view.IViewerManager#getRequestViewers()
	 */
	public List getRequestViewers() {
		IConfigurationElement element;
		Iterator iterator = viewers.iterator();
		List temp = new ArrayList();
		while (iterator.hasNext()) {
			element = (IConfigurationElement) iterator.next();
			if (element.getAttribute("type").toLowerCase().indexOf("request") >= 0)
				temp.add(element);
		}
		return temp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tcpip.monitor.internal.view.IViewerManager#getResponseViewers()
	 */
	public List getResponseViewers() {
		IConfigurationElement element;
		Iterator iterator = viewers.iterator();
		List temp = new ArrayList();
		while (iterator.hasNext()) {
			element = (IConfigurationElement) iterator.next();
			if (element.getAttribute("type").toLowerCase().indexOf("response") >= 0)
				temp.add(element);
		}
		return temp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tcpip.monitor.internal.view.IViewerManager#setRequestViewer(java.lang.String)
	 */
	public void setRequestViewer(IConfigurationElement element) {
		// Call set request to save and reset the request.
		setRequest(request);
		reqViewer.dispose();
		try {
			reqViewer = (ContentViewer) element.createExecutableExtension("class");
		} catch (CoreException e) {
			Trace.trace(Trace.SEVERE, "Error", e);
		}
		reqViewer.init(reqVComp);
		//reqViewer.setRequestResponse(rr);
		byte[] b = null;
		if (request != null) {
			b = filter(request.getRequest(IRequest.CONTENT));
			// Set the editor to editable if the request hasn't  been sent and the
			// editor can be set as editable.
			if (request instanceof IResendRequest
					&& request.getResponse(IRequest.TRANSPORT) == null) {
				if (reqViewer instanceof EditableContentViewer)
					((EditableContentViewer) reqViewer).setEditable(true);
			} else {
				if (reqViewer instanceof EditableContentViewer)
					((EditableContentViewer) reqViewer).setEditable(false);
			}
		}
		reqViewer.setContent(b);
		reqVComp.layout(true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tcpip.monitor.internal.view.IViewerManager#setResponseViewer(java.lang.String)
	 */
	public void setResponseViewer(IConfigurationElement element) {
		respViewer.dispose();
		try {
			respViewer = (ContentViewer) element.createExecutableExtension("class");
		} catch (CoreException e) {
			Trace.trace(Trace.SEVERE, "Error", e);
		}
		respViewer.init(respVComp);
		//respViewer.setRequestResponse(rr);
		byte[] b = null;
		if (request != null)
			b = filter(request.getResponse(IRequest.CONTENT));
		respViewer.setContent(b);
		respVComp.layout(true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.internet.monitor.ui.internal.view.IViewerManager#getCurrentRequestViewer()
	 */
	public ContentViewer getCurrentRequestViewer() {
		return reqViewer;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.internet.monitor.ui.internal.view.IViewerManager#getCurrentRequestHeaderViewer()
	 */
	public HeaderViewer getCurrentRequestHeaderViewer() {
		return reqHeader;
	}
}