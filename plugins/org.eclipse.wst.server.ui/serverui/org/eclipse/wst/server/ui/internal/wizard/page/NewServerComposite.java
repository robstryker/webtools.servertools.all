package org.eclipse.wst.server.ui.internal.wizard.page;
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
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.JFaceResources;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.server.core.IModuleKind;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ITaskModel;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.model.IModule;
import org.eclipse.wst.server.ui.internal.*;
import org.eclipse.wst.server.ui.internal.viewers.ServerComposite;
import org.eclipse.wst.server.ui.internal.wizard.fragment.NewServerWizardFragment;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

/**
 * A wizard page used to select a server client.
 */
public class NewServerComposite extends Composite {
	protected IWizardHandle wizard;
	protected ITaskModel taskModel;
	protected IModule module;
	protected String launchMode;
	
	protected static final byte MODE_EXISTING = 0;
	protected static final byte MODE_DETECT = 1;
	protected static final byte MODE_MANUAL= 2;
	protected byte mode;

	protected Composite detectComp2;
	protected NewDetectServerComposite detectComp;
	protected HostnameComposite detectHostComp;
	protected Composite manualComp2;
	protected NewManualServerComposite manualComp;
	protected HostnameComposite manualHostComp;
	protected ServerComposite existingComp;
	
	protected Composite stack;
	protected StackLayout stackLayout; 
	
	protected String lastHostname;
	
	protected Button pref;
	protected boolean preferred;
	
	protected IServerWorkingCopy existingWC;

	/**
	 * Create a new NewServerComposite.
	 */
	public NewServerComposite(Composite parent, IWizardHandle wizard, IModule module, String launchMode) {
		super(parent, SWT.NONE);
		this.wizard = wizard;
		this.module = module;
		this.launchMode = launchMode;
	
		wizard.setTitle(ServerUIPlugin.getResource("%wizNewServerTitle"));
		wizard.setDescription(ServerUIPlugin.getResource("%wizNewServerDescription"));
		wizard.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_WIZBAN_NEW_SERVER));
		
		createControl();
	}

	public NewServerComposite(Composite parent, IWizardHandle wizard) {
		this(parent, wizard, null, null);
	}
	
	protected Label createHeadingLabel(Composite parent, String text, int span) {
		Label label = createLabel(parent, text, span);
		label.setFont(JFaceResources.getBannerFont());
		return label;
	}
	
	protected Label createLabel(Composite parent, String text, int span) {
		Label label = new Label(parent, SWT.WRAP);
		label.setText(text);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = span;
		label.setLayoutData(data);
		return label;
	}
	
	protected Label createLabel(Composite parent, String text) {
		return createLabel(parent, text, 1);
	}
	
	protected Button createRadioButton(Composite parent, String text, int span) {
		Button button = new Button(parent, SWT.RADIO);
		button.setText(text);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = span;
		data.horizontalIndent = 10;
		button.setLayoutData(data);
		return button;
	}
	
	protected Text createText(Composite parent, String text2, int span) {
		Text text = new Text(parent, SWT.NONE);
		text.setText(text2);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = span;
		text.setLayoutData(data);
		return text;
	}

	/**
	 * Creates the UI of the page.
	 *
	 * @param org.eclipse.swt.widgets.Composite parent
	 */
	protected void createControl() {
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = SWTUtil.convertHorizontalDLUsToPixels(this, 4);
		layout.verticalSpacing = SWTUtil.convertVerticalDLUsToPixels(this, 4);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 1;
		setLayout(layout);
		//WorkbenchHelp.setHelp(this, ContextIds.SELECT_CLIENT_WIZARD);
	
		if (module != null)
			createHeadingLabel(this, ServerUIPlugin.getResource("%wizNewServerSelect"), 1);
			
		Button existing = null;
		if (module != null) {
			final Button predefined = createRadioButton(this, ServerUIPlugin.getResource("%wizNewServerExisting"), 1);
			predefined.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (predefined.getSelection())
						toggleMode(MODE_EXISTING);
				}
			});
			existing = predefined;
		}
		
		/*final Button auto = createRadioButton(this, ServerUIPlugin.getResource("%wizNewServerDetect"), 1);
		auto.setEnabled(false);
		auto.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (auto.getSelection())
					toggleMode(MODE_DETECT);
			}
		});*/
	
		Button manual = null;
		if (module != null) {
			final Button manualButton = createRadioButton(this, ServerUIPlugin.getResource("%wizNewServerManual"), 1);
			manualButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (manualButton.getSelection())
						toggleMode(MODE_MANUAL);
				}
			});
			manual = manualButton;
		}
		
		stack = new Composite(this, SWT.NONE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		stack.setLayoutData(data);
		stackLayout = new StackLayout();
		stackLayout.marginHeight = 0;
		stackLayout.marginWidth = 0;
		stack.setLayout(stackLayout);
		
		if (module != null)
			createExistingComposite(stack);
		createAutoComposite(stack);
		createManualComposite(stack);
	
		if (existingComp != null) {
			if (isExistingServer()) {
				mode = MODE_EXISTING;
				stackLayout.topControl = existingComp;
				existing.setSelection(true);
			} else {
				mode = MODE_MANUAL;
				stackLayout.topControl = manualComp2;
				manualComp.setVisible(true);
				if (manual != null)
					manual.setSelection(true);
				existing.setEnabled(false);
				existingComp.setEnabled(false);
			}
		} else {
			mode = MODE_MANUAL;
			stackLayout.topControl = manualComp2;
			manualComp.setVisible(true);
			if (manual != null)
				manual.setSelection(true);
		}
		
		if (module != null) {
			// preferred server button
			pref = new Button(this, SWT.CHECK | SWT.WRAP);
			pref.setText(ServerUIPlugin.getResource("%wizSelectServerPreferred"));
			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_END);
			//pref.setSelection(true);
			//preferred = true;
			data.horizontalSpan = 1;
			pref.setLayoutData(data);
			pref.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					preferred = pref.getSelection();
				}
			});
			WorkbenchHelp.setHelp(pref, ContextIds.SELECT_SERVER_PREFERENCE);
		}
		
		Dialog.applyDialogFont(this);
	}
	
	protected void toggleMode(byte newMode) {
		if (!isVisible())
			return;

		if (newMode == mode)
			return;
		
		mode = newMode;
		wizard.setMessage(null, IMessageProvider.NONE);
		
		if (mode == MODE_EXISTING) {
			stackLayout.topControl = existingComp;
			existingComp.setSelection(existingComp.getSelectedServer());
		} else if (mode == MODE_DETECT) {
			stackLayout.topControl = detectComp2;
			detectComp.setVisible(true);
		} else {
			stackLayout.topControl = manualComp2;
			manualComp.setVisible(true);
		}
		stack.layout();
		if (taskModel != null) {
			taskModel.putObject(NewServerWizardFragment.MODE, new Byte(mode));
			updateTaskModel();
		}
	}
	
	protected HostnameComposite createHostComposite(Composite comp) {
		HostnameComposite hostComp = new HostnameComposite(comp, new HostnameComposite.IHostnameSelectionListener() {
			public void hostnameSelected(String host) {
				lastHostname = host;
				if (detectComp != null)
					detectComp.setHost(host);
				if (manualComp != null)
					manualComp.setHost(host);
			}
		});
		
		if (lastHostname != null)
			hostComp.setHostname(lastHostname);
		
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		hostComp.setLayoutData(data);
		return hostComp;
	}
	
	protected void createAutoComposite(Composite comp) {
		detectComp2 = new Composite(comp, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = SWTUtil.convertHorizontalDLUsToPixels(this, 4);
		layout.verticalSpacing = SWTUtil.convertVerticalDLUsToPixels(this, 4);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 1;
		detectComp2.setLayout(layout);
		
		detectHostComp = createHostComposite(detectComp2);
		
		detectComp = new NewDetectServerComposite(detectComp2, new NewDetectServerComposite.IServerSelectionListener() {
			public void serverSelected(IServer server) {
			}
		});

		if (lastHostname != null)
			detectComp.setHost(lastHostname);
		else
			detectComp.setHost("localhost");
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		data.heightHint = 120;
		detectComp.setLayoutData(data);
	}

	protected void createExistingComposite(Composite comp) {
		existingComp = new ServerComposite(comp, SWT.NONE, new ServerComposite.ServerSelectionListener() {
			public void serverSelected(IServer server) {
				wizard.setMessage(null, IMessageProvider.NONE);
				
				// check for compatibility
				if (server != null && module != null) {
					IServerType serverType = server.getServerType();
					if (!ServerUtil.isSupportedModule(serverType, module)) {
						IModuleKind mk = ServerCore.getModuleKind(module.getType());
						String type = null;
						if (mk != null)
							type = mk.getName();
						wizard.setMessage(ServerUIPlugin.getResource("%errorVersionLevel", new Object[] { type, module.getVersion() }), IMessageProvider.ERROR);
						server = null;
					}
				}
				
				if (existingWC != null) {
					if (server != null && server.equals(existingWC.getOriginal()))
						return;
					existingWC.release();
					existingWC = null;
				}
				if (server != null)
					existingWC = server.getWorkingCopy();
				updateTaskModel();
			}
		}, module, launchMode);
		existingComp.setIncludeIncompatibleVersions(true);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		data.heightHint = 120;
		existingComp.setLayoutData(data);
	}
	
	protected boolean isExistingServer() {
		if (module == null || launchMode == null)
			return false;
		
		Iterator iterator = ServerCore.getResourceManager().getServers().iterator();
		while (iterator.hasNext()) {
			IServer server = (IServer) iterator.next();
			if (ServerUtil.isCompatibleWithLaunchMode(server, launchMode) &&
				ServerUtil.isSupportedModule(server.getServerType().getRuntimeType().getModuleTypes(), module.getType(), module.getVersion()))
					return true;
		}
		return false;
	}

	protected void createManualComposite(Composite comp) {
		manualComp2 = new Composite(comp, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = SWTUtil.convertHorizontalDLUsToPixels(this, 4);
		layout.verticalSpacing = SWTUtil.convertVerticalDLUsToPixels(this, 4);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 1;
		manualComp2.setLayout(layout);
		
		manualHostComp = createHostComposite(manualComp2);
		
		String type = null;
		String version = null;
		if (module != null) {
			type = module.getType();
			version = module.getVersion();
		}

		manualComp = new NewManualServerComposite(manualComp2, new NewManualServerComposite.IWizardHandle2() {
			public void run(boolean fork, boolean cancelable, IRunnableWithProgress runnable) throws InterruptedException, InvocationTargetException {
				wizard.run(fork, cancelable, runnable);
			}
			public void update() {
				wizard.update();
			}
			public void setMessage(String newMessage, int newType) {
				wizard.setMessage(newMessage, newType);
			}
		}, type, version, new NewManualServerComposite.ServerSelectionListener() {
			public void serverSelected(IServer server) {
				updateTaskModel();
			}
		});
		
		if (lastHostname != null)
			manualComp.setHost(lastHostname);
		else
			manualComp.setHost("localhost");

		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		data.heightHint = 200;
		manualComp.setLayoutData(data);
	}

	protected void updateTaskModel() {
		if (taskModel != null) {
			IServer server = getServer();
			if (server != null) {
				taskModel.putObject(ITaskModel.TASK_SERVER, server);
				taskModel.putObject(ITaskModel.TASK_RUNTIME, server.getRuntime());
			} else {
				taskModel.putObject(ITaskModel.TASK_SERVER, null);
				taskModel.putObject(ITaskModel.TASK_RUNTIME, null);
			}
			wizard.update();
		}
	}

	public void setTaskModel(ITaskModel model) {
		taskModel = model;
		taskModel.putObject(NewServerWizardFragment.MODE, new Byte(mode));
		updateTaskModel();
	}

	public IServer getServer() {
		if (mode == MODE_EXISTING)
			return existingWC; //existingComp.getSelectedServer();
		else if (mode == MODE_DETECT)
			return detectComp.getServer();
		else
			return manualComp.getServer();
	}

	public IRuntime getRuntime() {
		if (mode == MODE_EXISTING) {
			IServer server = existingComp.getSelectedServer();
			if (server != null)
				return server.getRuntime();
			else
				return null;
		} else if (mode == MODE_DETECT)
			return null;
		else
			return manualComp.getRuntime();
	}
	
	/**
	 * Returns true if this server should become the preferred server.
	 * 
	 * @return boolean
	 */
	public boolean isPreferredServer() {
		return preferred;
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		Control[] c = getChildren();
		if (c != null) {
			int size = c.length;
			for (int i = 0; i < size; i++)
				if (c[i] != null)
					c[i].setVisible(visible);
		}
	}
}