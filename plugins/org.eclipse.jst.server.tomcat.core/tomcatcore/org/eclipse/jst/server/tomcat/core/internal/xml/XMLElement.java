package org.eclipse.jst.server.tomcat.core.internal.xml;
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
import java.io.Serializable;
import org.w3c.dom.*;
/**
 * An XML element.
 */
public class XMLElement implements Serializable {

	private static final long serialVersionUID = 1L;
	private Element xmlElement;
	protected Factory factory;

	public XMLElement() { }

	public Attr addAttribute(String s, String s1) {
		Attr attr = factory.createAttribute(s, xmlElement);
		attr.setValue(s1);
		return attr;
	}

	public XMLElement createElement(int index, String s) {
		return factory.createElement(index, s, xmlElement);
	}

	public XMLElement createElement(String s) {
		return factory.createElement(s, xmlElement);
	}

	public void createEmptyBody() {
	}

	public XMLElement findElement(String s) {
		NodeList nodelist = xmlElement.getElementsByTagName(s);
		int i = nodelist == null ? 0 : nodelist.getLength();
		for (int j = 0; j < i; j++) {
			Node node = nodelist.item(j);
			String s1 = node.getNodeName().trim();
			if (s1.equals(s))
				return factory.newInstance((Element) node);
		}
	
		return createElement(s);
	}

	public XMLElement findElement(String s, int i) {
		NodeList nodelist = xmlElement.getElementsByTagName(s);
		int j = nodelist == null ? 0 : nodelist.getLength();
		for (int k = 0; k < j; k++) {
			Node node = nodelist.item(k);
			String s1 = node.getNodeName().trim();
			if (s1.equals(s) && k == i)
				return factory.newInstance((Element) node);
		}
	
		return createElement(s);
	}

	public String getAttributeValue(String s) {
		Attr attr = xmlElement.getAttributeNode(s);
		if (attr != null)
			return attr.getValue();
		else
			return null;
	}
	
	public String getElementName() {
		return xmlElement.getNodeName();
	}
	
	public String getElementValue() {
		return getElementValue(xmlElement);
	}
	
	protected static String getElementValue(Element element) {
		String s = element.getNodeValue();
		if (s != null)
			return s;
		NodeList nodelist = element.getChildNodes();
		for (int i = 0; i < nodelist.getLength(); i++)
			if (nodelist.item(i) instanceof Text)
				return ((Text) nodelist.item(i)).getData();
	
		return null;
	}
	
	public Element getSubElement(String s) {
		NodeList nodelist = xmlElement.getElementsByTagName(s);
		int i = nodelist == null ? 0 : nodelist.getLength();
		for (int j = 0; j < i; j++) {
			Node node = nodelist.item(j);
			String s1 = node.getNodeName().trim();
			if (s1.equals(s))
				return (Element) node;
		}
	
		return null;
	}

	public String getSubElementValue(String s) {
		Element element = getSubElement(s);
		if (element == null)
			return null;
	
		String value = getElementValue(element);
		if (value == null)
			return null;
		
		return value.trim();
	}

	public boolean removeAttribute(String s) {
		try {
			xmlElement.removeAttribute(s);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public boolean removeElement(String s, int i) {
		NodeList nodelist = xmlElement.getElementsByTagName(s);
		int j = nodelist == null ? 0 : nodelist.getLength();
		for (int k = 0; k < j; k++) {
			Node node = nodelist.item(k);
			String s1 = node.getNodeName().trim();
			if (s1.equals(s) && k == i) {
				xmlElement.removeChild(node);
				return true;
			}
		}
	
		return false;
	}

	public void setAttributeValue(String s, String s1) {
		Attr attr = xmlElement.getAttributeNode(s);
		if (attr == null)
			attr = addAttribute(s, s1);
		else
			attr.setValue(s1);
	}

	void setElement(Element element) {
		xmlElement = element;
	}

	protected static void setElementValue(Element element, String value) {
		String s = element.getNodeValue();
		if (s != null) {
			element.setNodeValue(value);
			return;
		}
		NodeList nodelist = element.getChildNodes();
		for (int i = 0; i < nodelist.getLength(); i++)
			if (nodelist.item(i) instanceof Text) {
				Text text = (Text) nodelist.item(i);
				text.setData(value);
				return;
			}
	
		return;
	}

	void setFactory(Factory factory1) {
		factory = factory1;
	}

	public void setSubElementValue(String s, String value) {
		Element element = getSubElement(s);
		if (element == null) {
			element = factory.document.createElement(s);
			element.appendChild(factory.document.createTextNode("temp"));
			xmlElement.appendChild(element);
		}
		setElementValue(element, value);
	}

	public int sizeOfElement(String s) {
		NodeList nodelist = xmlElement.getElementsByTagName(s);
		int i = nodelist == null ? 0 : nodelist.getLength();
		return i;
	}

	public void updateElementValue(String s) {
		try {
			xmlElement.setNodeValue(s);
		} catch (DOMException ex) {
			NodeList nodelist = xmlElement.getChildNodes();
			int i = nodelist == null ? 0 : nodelist.getLength();
			if (i > 0) {
				for (int j = 0; j < i; j++)
					if (nodelist.item(j) instanceof Text) {
						((Text) nodelist.item(j)).setData(s);
						return;
					}
			} else {
				xmlElement.appendChild(factory.document.createTextNode(s));
			}
		}
	}
}