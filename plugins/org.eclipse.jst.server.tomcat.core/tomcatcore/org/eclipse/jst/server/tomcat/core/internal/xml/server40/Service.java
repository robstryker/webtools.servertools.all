package org.eclipse.jst.server.tomcat.core.internal.xml.server40;
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
import org.eclipse.jst.server.tomcat.core.internal.xml.*;
/**
 * 
 */
public class Service extends XMLElement {
	public Service() { }

	public Connector getConnector(int index) {
		return (Connector) findElement("Connector", index);
	}

	public int getConnectorCount() {
		return sizeOfElement("Connector");
	}

	public Engine getEngine() {
		return (Engine) findElement("Engine");
	}

	public String getName() {
		return getAttributeValue("name");
	}

	public void setName(String name) {
		setAttributeValue("name", name);
	}
}