/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal.xml;

import java.io.*;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

import org.eclipse.jst.server.tomcat.core.internal.Trace;
/**
 * Factory for reading and writing from XML files.
 */
public class Factory {
	protected String packageName;
	protected Document document;

	public Factory() {
		// do nothing
	}
	
	protected Attr createAttribute(String s, Element element) {
		Attr attr = document.createAttribute(s);
		element.setAttributeNode(attr);
		return attr;
	}
	
	protected XMLElement createElement(int index, String s, Node node) {
		if (index < 0)
			return createElement(s, node);
	
		Element element = document.createElement(s);
		try {
			Node child = node.getFirstChild();
			for (int i = 0; i < index; i++)
				child = child.getNextSibling();
	
			node.insertBefore(element, child);
		} catch (Exception e) {
			node.appendChild(element);
		}
		return newInstance(element);
	}
	
	protected XMLElement createElement(String s, Node node) {
		Element element = document.createElement(s);
		node.appendChild(element);
		return newInstance(element);
	}
	
	public byte[] getContents() throws IOException {
		return XMLUtil.getContents(document);
	}
	
	/**
	 * 
	 * @return org.w3c.dom.Document
	 */
	public Document getDocument() {
		return document;
	}

	public String getPackageName() {
		return packageName;
	}

	public XMLElement loadDocument(InputStream in) throws IOException {
		try {
			document = XMLUtil.getDocumentBuilder().parse(new InputSource(in));
			Element element = document.getDocumentElement();
			return newInstance(element);
		} catch (Exception exception) {
			Trace.trace(Trace.WARNING, "Error loading document", exception);
			throw new IOException("Could not load document");
		}
	}

	protected XMLElement newInstance(Element element) {
		String s = element.getNodeName();
		try {
			// change "web-app:test" to "WebAppTest"
			s = s.substring(0, 1).toUpperCase() + s.substring(1);
			int i = s.indexOf("-");
			while (i >= 0) {
				s = s.substring(0, i) + s.substring(i+1, i+2).toUpperCase() + s.substring(i+2);
				i = s.indexOf("-");
			}
			i = s.indexOf(":");
			while (i >= 0) {
				s = s.substring(0, i) + s.substring(i+1, i+2).toUpperCase() + s.substring(i+2);
				i = s.indexOf(":");
			}
	
			// add package name
			if (packageName != null)
				s = packageName + "." + s;
			Class class1 = Class.forName(s);
	
			XMLElement xmlElement = (XMLElement) class1.newInstance();
			xmlElement.setElement(element);
			xmlElement.setFactory(this);
			return xmlElement;
		} catch (Exception exception) {
			// ignore
		}
		return null;
	}
	
	public void save(String filename) throws IOException {
		XMLUtil.save(filename, document);
	}
	
	public void setDocument(Document d) {
		document = d;
	}
	
	public void setPackageName(String s) {
		packageName = s;
	}
}