package org.eclipse.wst.server.ui.internal.view.tree;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. � This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
�*
 * Contributors:
 *    IBM - Initial API and implementation
 *
 **********************************************************************/
import org.eclipse.jface.action.MenuManager;
/**
 * 
 */
public class DisabledMenuManager extends MenuManager {
	public DisabledMenuManager(String label) {
		super(label);
	}
	public boolean isEnabled() {
		return false;
	}
}