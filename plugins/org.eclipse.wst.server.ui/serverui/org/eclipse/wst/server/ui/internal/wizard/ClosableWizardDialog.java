package org.eclipse.wst.server.ui.internal.wizard;
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
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
/**
 * A closable wizard dialog.
 */
public class ClosableWizardDialog extends WizardDialog {
	/**
	 * Constructor for ClosableWizardDialog.
	 * @param shell
	 * @param wizard
	 */
	public ClosableWizardDialog(Shell shell, IWizard wizard) {
		super(shell, wizard);
	}
	
	/**
	 * The Finish button has been pressed.
	 */
	public void finishPressed() {
		super.finishPressed();
	}
}