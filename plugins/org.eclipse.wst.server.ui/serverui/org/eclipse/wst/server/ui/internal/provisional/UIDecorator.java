/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.provisional;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;

public abstract class UIDecorator {
	public static final int ACTION_STOP = 0;

	/**
	 * Returns a string representing the given state.
	 *
	 * @return java.lang.String
	 */
	public abstract String getStateLabel(int state, String mode, int count);

	/**
	 * Returns an image representing the given state.
	 * 
	 * @return org.eclipse.jface.parts.IImage
	 */
	public abstract Image getStateImage(int state, String mode, int count);

	public String getModuleName() {
		return "module";
	}
	
	public boolean canRestart() {
		return false;
	}
	
	public void setupAction(Action action, int action2) {
		action.setToolTipText(ServerUIPlugin.getResource("%actionStopToolTip"));
		action.setText(ServerUIPlugin.getResource("%actionStop"));
		action.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_STOP));
		action.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_STOP));
		action.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_STOP));
	}
}