/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.tests.extension;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.ServerCore;

public class RuntimeTypesTestCase extends TestCase {
	public static Test suite() {
		return new TestSuite(RuntimeTypesTestCase.class, "RuntimeTypesTestCase");
	}

	public void testRuntimeTypesExtension() throws Exception {
		IRuntimeType[] runtimeTypes = ServerCore.getRuntimeTypes();
		if (runtimeTypes != null) {
			int size = runtimeTypes.length;
			for (int i = 0; i < size; i++)
				System.out.println(runtimeTypes[i].getId() + " - " + runtimeTypes[i].getName());
		}
	}
}