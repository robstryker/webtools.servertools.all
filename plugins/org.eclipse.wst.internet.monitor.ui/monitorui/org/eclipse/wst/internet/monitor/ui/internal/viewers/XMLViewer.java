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
package org.eclipse.wst.internet.monitor.ui.internal.viewers;
 
import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.jface.resource.JFaceResources;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.internet.monitor.core.MonitorCore;
import org.eclipse.wst.internet.monitor.ui.ContentViewer;
import org.eclipse.wst.internet.monitor.ui.internal.ContextIds;
import org.eclipse.wst.internet.monitor.ui.internal.MonitorUIPlugin;

import org.w3c.dom.*;
import org.xml.sax.*;
/**
 * XML Viewer.
 */
public class XMLViewer extends ContentViewer {
	protected GridData data;
	protected StackLayout layout;
	protected Text messageText;

	protected Composite rootComp;
	protected Composite viewerComp;
	protected Label messageLabel;
	
	protected boolean xmlTagMissing = false;
	protected boolean setEncoding = false;
	protected boolean missingEncoding = false;
	protected String originalEncoding;

	/* (non-Javadoc)
	 * @see org.eclipse.wst.internet.monitor.ui.IContentViewer#dispose()
	 */
	public void dispose() {
		viewerComp.dispose();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.internet.monitor.ui.IContentViewer#setContent()
	 */
	public void setContent(byte[] b) {
		String out = "";		
		if (b != null)
			out = MonitorCore.parse(b);
				
		String lineSeparator = System.getProperty("line.separator");
		int ls = lineSeparator.length();
		if (out.length() > ls) {
			while (out.substring(0, ls).indexOf(lineSeparator) >= 0)
			out = out.substring(ls, out.length()); 
		}
		
		String out_temp = out.toLowerCase();
		if (out_temp.indexOf("<?xml") < 0) 
			xmlTagMissing = true;
		
		if (out.length() > 0) {		
			byte[] b1 = createDocument(out);
			String finalMsg = new String (b1);
			if (finalMsg.startsWith("Invalid XML")) {
				//case: error parsing
				messageText.setVisible(false);
				layout.topControl = messageLabel;
				messageLabel.setVisible(true);
				messageLabel.setText("<" + MonitorUIPlugin.getResource("%xmlViewInvalid") + ">");
				
			}
			else if (xmlTagMissing && finalMsg.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")) {
				int x = finalMsg.indexOf("\n") + 1;
				String Msg = finalMsg.substring(x);
				finalMsg = Msg;
				
				messageText.setText(finalMsg);
			}
			
			else if (setEncoding) {
				//change back to original Encoding
				int begin = finalMsg.indexOf("UTF-8"); //location of opening "
				int last = begin + 5;  //location of closing "
				String first_half = finalMsg.substring(0,begin);
				String second_half = finalMsg.substring(last);
				finalMsg = first_half + originalEncoding + second_half;	
				
				messageText.setText(finalMsg);
			}
			
			else if (missingEncoding) {
				//remove encoding completely
				int begin = finalMsg.indexOf("encoding=\"UTF-8\""); //location of opening "
				int last = begin + 16;  //location of closing "
				String first_half = finalMsg.substring(0,begin);
				String second_half = finalMsg.substring(last);
				finalMsg = first_half + second_half;
				
				messageText.setText(finalMsg);	
			}			
		} else
			messageText.setText(out);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.internet.monitor.ui.IContentViewer#init(Composite)
	 */
	public void init(Composite parent) {
		rootComp = parent;
		
		viewerComp = new Composite(parent, SWT.NONE);
		layout = new StackLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		viewerComp.setLayout(layout);
		data = new GridData(GridData.FILL_BOTH);
		viewerComp.setLayoutData(data);
	
		messageText = new Text(viewerComp, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL);
		Display display = viewerComp.getDisplay();
		messageText.setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		messageText.setForeground(display.getSystemColor(SWT.COLOR_LIST_FOREGROUND));
		messageText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		messageText.setFont(JFaceResources.getTextFont());
		messageText.setVisible(true);
		WorkbenchHelp.setHelp(messageText, ContextIds.VIEW_RESPONSE);
		
		messageLabel = new Label(viewerComp, SWT.NONE);
		messageLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING));
		messageLabel.setVisible(false);
		
		layout.topControl = messageText;
	}
	
	/* (non-Javadoc)
	 * @#createDocument(String)
	 */
	protected byte[] createDocument(String str) {
		byte[] parseArray = null;
		Document document = null;
		byte[] result = null;	
		
		try {	
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			try {
				factory.setAttribute("http://apache.org/xml/features/allow-java-encodings", new Boolean(true));
				factory.setAttribute("http://apache.org/xml/features/continue-after-fatal-error", new Boolean(true));
			} catch (Exception e) {
				// ignore
			}
			DocumentBuilder parser = factory.newDocumentBuilder();
			
			if (xmlTagMissing) {
				str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + str;
			} else {
				String str_temp = str.toLowerCase();
				
				//if encoding present,then save original Encoding, change to UTF-8
				if (str_temp.indexOf("encoding=") >= 0) {
					setEncoding = true;
					String temp1 = str.substring(str_temp.indexOf("encoding="));
					int beginIndex = temp1.indexOf("\"") + 1;
					String temp2 = temp1.substring(beginIndex);
					int endIndex = temp2.indexOf("\"");
					originalEncoding = temp2.substring(0, endIndex);
				}	
				
				//if no encoding at all,then no changes to be made
				else if (str_temp.indexOf("encoding") < 0) {
					setEncoding = false;	
					missingEncoding = true;		
				}	
			}
			parseArray = str.getBytes();
			document = parser.parse(new InputSource(new ByteArrayInputStream(parseArray)));  
			result = getContents(document);
			
		} catch (Exception e) {
			result = "Invalid XML".getBytes();
		} 
		return result;
	}

	protected byte[] getContents(Document document) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Result result = new StreamResult(out);
		Source source = new DOMSource(document);
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
			transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
			transformer.transform(source, result);            
		}
		catch (TransformerConfigurationException e) {
			throw (IOException) (new IOException().initCause(e));
		}
		catch (TransformerException e) {
			throw (IOException) (new IOException().initCause(e));
		}
		return out.toByteArray();
	}
}