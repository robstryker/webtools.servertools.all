package org.eclipse.jst.server.tomcat.core.internal;
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
import java.io.File;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
/**
 * The Tomcat plugin.
 */
public class TomcatPlugin extends Plugin {
	protected static TomcatPlugin singleton;

	public static final String PLUGIN_ID = "org.eclipse.jst.server.tomcat.core";
	
	public static final String TOMCAT_32 = "org.eclipse.jst.server.tomcat.32";
	public static final String TOMCAT_40 = "org.eclipse.jst.server.tomcat.40";
	public static final String TOMCAT_41 = "org.eclipse.jst.server.tomcat.41";
	public static final String TOMCAT_50 = "org.eclipse.jst.server.tomcat.50";
	public static final String TOMCAT_55 = "org.eclipse.jst.server.tomcat.55";
	
	protected static final String VERIFY_INSTALL_FILE = "verifyInstall.properties";
	protected static String[] verify32;
	protected static String[] verify40;
	protected static String[] verify41;
	protected static String[] verify50;
	protected static String[] verify55;
	
	/**
	 * TomcatPlugin constructor comment.
	 */
	public TomcatPlugin() {
		super();
		singleton = this;
	}

	/**
	 * Returns the singleton instance of this plugin.
	 * @return org.eclipse.jst.server.tomcat.internal.TomcatPlugin
	 */
	public static TomcatPlugin getInstance() {
		return singleton;
	}

	/**
	 * Return the install location preference.
	 * 
	 * @param id
	 * @return
	 */
	public static String getPreference(String id) {
		return getInstance().getPluginPreferences().getString(id);
	}
	
	/**
	 * Set the install location preference.
	 * 
	 * @param id
	 * @return
	 */
	public static void setPreference(String id, String value) {
		getInstance().getPluginPreferences().setValue(id, value);
		getInstance().savePluginPreferences();
	}

	/**
	 * Convenience method for logging.
	 *
	 * @param status org.eclipse.core.runtime.IStatus
	 */
	public static void log(IStatus status) {
		getInstance().getLog().log(status);
	}

	/**
	 * Returns the translated String found with the given key.
	 * @return java.lang.String
	 * @param key java.lang.String
	 */
	public static String getResource(String key) {
		try {
			return Platform.getResourceString(getInstance().getBundle(), key);
		} catch (Exception e) {
			return key;
		}
	}
	
	/**
	 * Returns the translated String found with the given key,
	 * and formatted with the given object.
	 * @return java.lang.String
	 * @param key java.lang.String
	 * @param obj java.lang.Object[]
	 */
	public static String getResource(String key, Object[] obj) {
		try {
			return MessageFormat.format(getResource(key), obj);
		} catch (Exception e) {
			return key;
		}
	}
		
	/**
	 * Returns the translated String found with the given key,
	 * and formatted with the given object.
	 * @return java.lang.String
	 * @param key java.lang.String
	 * @param obj java.lang.Object[]
	 */
	public static String getResource(String key, String arg) {
		return getResource(key, new String[] { arg });
	}
	
	/**
	 * Returns the translated String found with the given key,
	 * and formatted with the given object.
	 * @return java.lang.String
	 * @param key java.lang.String
	 * @param obj java.lang.Object[]
	 */
	public static String getResource(String key, String arg1, String arg2) {
		return getResource(key, new String[] { arg1, arg2 });
	}
	
	/**
	 * Returns the Tomcat home directory.
	 * @return java.lang.String
	 */
	protected static String getTomcatStateLocation() {
		try {
			return getInstance().getStateLocation().toOSString();
		} catch (Exception e) {
			return null;
		}
	}
	
	public static ITomcatVersionHandler getTomcatVersionHandler(String id) {
		id = id.substring(0, id.length() - 8);
		if (TOMCAT_32.equals(id))
			return new Tomcat32Handler();
		else if (TOMCAT_40.equals(id))
			return new Tomcat40Handler();
		else if (TOMCAT_41.equals(id))
			return new Tomcat41Handler();
		else if (TOMCAT_50.equals(id))
			return new Tomcat50Handler();
		else if (TOMCAT_55.equals(id))
			return new Tomcat55Handler();
		else
			return null;
	}
	
	/**
	 * Returns the file with which to verify the Tomcat installation.
	 *
	 * @param boolean
	 * @return java.lang.String[]
	 */
	public static void loadVerifyFiles() {
		if (verify32 != null)
			return;
	
		try {
			URL url = getInstance().getBundle().getEntry(VERIFY_INSTALL_FILE);
			url = Platform.resolve(url);
			Properties p = new Properties();
			p.load(url.openStream());

			String verify = p.getProperty("verify32install");
			verify.replace('/', File.separatorChar);

			StringTokenizer st = new StringTokenizer(verify, ",");
			List list = new ArrayList();
			while (st.hasMoreTokens())
				list.add(st.nextToken());
			Trace.trace(Trace.FINEST, "Verify32: " + list.toString());
			verify32 = new String[list.size()];
			list.toArray(verify32);

			// v4.0
			verify = p.getProperty("verify40install");
			verify.replace('/', File.separatorChar);

			st = new StringTokenizer(verify, ",");
			list = new ArrayList();
			while (st.hasMoreTokens())
				list.add(st.nextToken());
			Trace.trace(Trace.FINEST, "Verify40: " + list.toString());
			verify40 = new String[list.size()];
			list.toArray(verify40);
			
			// v4.1
			verify = p.getProperty("verify41install");
			verify.replace('/', File.separatorChar);

			st = new StringTokenizer(verify, ",");
			list = new ArrayList();
			while (st.hasMoreTokens())
				list.add(st.nextToken());
			Trace.trace(Trace.FINEST, "Verify41: " + list.toString());
			verify41 = new String[list.size()];
			list.toArray(verify41);
			
			// v5.0
			verify = p.getProperty("verify50install");
			verify.replace('/', File.separatorChar);

			st = new StringTokenizer(verify, ",");
			list = new ArrayList();
			while (st.hasMoreTokens())
				list.add(st.nextToken());
			Trace.trace(Trace.FINEST, "Verify50: " + list.toString());
			verify50 = new String[list.size()];
			list.toArray(verify50);

			// v5.5
			verify = p.getProperty("verify55install");
			verify.replace('/', File.separatorChar);

			st = new StringTokenizer(verify, ",");
			list = new ArrayList();
			while (st.hasMoreTokens())
				list.add(st.nextToken());
			Trace.trace(Trace.FINEST, "Verify55: " + list.toString());
			verify55 = new String[list.size()];
			list.toArray(verify55);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not load installation verification properties", e);
			verify32 = new String[0];
			verify40 = new String[0];
			verify41 = new String[0];
			verify50 = new String[0];
			verify55 = new String[0];
		}
	}

	public static boolean verifyInstallPath(IPath installPath, String id) {
		if (installPath == null)
			return false;
		
		String dir = installPath.toOSString();
		if (!dir.endsWith(File.separator))
			dir += File.separator;

		// look for the following files and directories
		TomcatPlugin.loadVerifyFiles();
		
		String[] paths = null;
		if (TOMCAT_32.equals(id))
			paths = verify32;
		else if (TOMCAT_40.equals(id))
			paths = verify40;
		else if (TOMCAT_41.equals(id))
			paths = verify41;
		else if (TOMCAT_50.equals(id))
			paths = verify50;
		else if (TOMCAT_55.equals(id))
			paths = verify55;
		else
			return false;
		
		for (int i = 0; i < paths.length; i++) {
			File temp = new File(dir + paths[i]);
			if (!temp.exists())
				return false;
		}
		return true;
	}
}