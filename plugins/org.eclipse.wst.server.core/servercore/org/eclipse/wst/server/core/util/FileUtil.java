/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
�*
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.util;

import java.io.*;
import java.util.zip.*;
import java.net.URL;
import org.eclipse.core.runtime.*;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.core.internal.Trace;
/**
 * Utility class with an assortment of useful file methods.
 */
public class FileUtil {
	// size of the buffer
	private static final int BUFFER = 10240;

	// the buffer
	private static byte[] buf = new byte[BUFFER];

	/**
	 * FileUtil cannot be created. Use static methods.
	 */
	private FileUtil() {
		super();
	}

	/**
	 * Copys a directory from a to b.
	 *
	 * @param from java.lang.String
	 * @param to java.lang.String
	 */
	public static void copyDirectory(String from, String to, IProgressMonitor monitor) {
		try {
			File fromDir = new File(from);
			File toDir = new File(to);
	
			File[] files = fromDir.listFiles();
	
			toDir.mkdir();
	
			// cycle through files
			int size = files.length;
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(ServerPlugin.getResource("%copyingTask", new String[] {from, to}), size * 50);
	
			for (int i = 0; i < size; i++) {
				File current = files[i];
				String fromFile = current.getAbsolutePath();
				String toFile = to;
				if (!toFile.endsWith(File.separator))
					toFile += File.separator;
				toFile += current.getName();
				if (current.isFile()) {
					copyFile(fromFile, toFile);
					monitor.worked(50);
				} else if (current.isDirectory()) {
					monitor.subTask(ServerPlugin.getResource("%copyingTask", new String[] {fromFile, toFile}));
					copyDirectory(fromFile, toFile, ProgressUtil.getSubMonitorFor(monitor, 50));
				}
				if (monitor.isCanceled())
					return;
			}
			monitor.done();
		} catch (Exception e) {
			Trace.trace("Error copying directory", e);
		}
	}

	/**
	 * Copy a file from a to b. Closes the input stream after use.
	 *
	 * @param in java.io.InputStream
	 * @param to java.lang.String
	 */
	public static IStatus copyFile(InputStream in, String to) {
		OutputStream out = null;
	
		try {
			out = new FileOutputStream(to);
	
			int avail = in.read(buf);
			while (avail > 0) {
				out.write(buf, 0, avail);
				avail = in.read(buf);
			}
			return new Status(IStatus.OK, ServerCore.PLUGIN_ID, 0, ServerPlugin.getResource("%copyingTask", new String[] {to}), null);
		} catch (Exception e) {
			Trace.trace("Error copying file", e);
			return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, ServerPlugin.getResource("%errorCopyingFile", new String[] {to, e.getLocalizedMessage()}), e);
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception ex) { }
			try {
				if (out != null)
					out.close();
			} catch (Exception ex) { }
		}
	}

	/**
	 * Copy a file from a to b.
	 *
	 * @param from java.lang.String
	 * @param to java.lang.String
	 */
	public static IStatus copyFile(String from, String to) {
		try {
			return copyFile(new FileInputStream(from), to);
		} catch (Exception e) {
			Trace.trace("Error copying file", e);
			return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, ServerPlugin.getResource("%errorCopyingFile", new String[] {to, e.getLocalizedMessage()}), e);
		}
	}

	/**
	 * Copy a file from a to b.
	 *
	 * @param from java.net.URL
	 * @param to java.lang.String
	 */
	public static IStatus copyFile(URL from, String to) {
		try {
			return copyFile(from.openStream(), to);
		} catch (Exception e) {
			Trace.trace("Error copying file", e);
			return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, ServerPlugin.getResource("%errorCopyingFile", new String[] {to, e.getLocalizedMessage()}), e);
		}
	}
	/**
	 * Recursively deletes a directory.
	 *
	 * @param dir java.io.File
	 */
	public static void deleteDirectory(File dir, IProgressMonitor monitor) {
		try {
			if (!dir.exists() || !dir.isDirectory())
				return;
	
			File[] files = dir.listFiles();
			int size = files.length;
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(ServerPlugin.getResource("%deletingTask", new String[] {dir.getAbsolutePath()}), size * 10);
	
			// cycle through files
			for (int i = 0; i < size; i++) {
				File current = files[i];
				if (current.isFile()) {
					current.delete();
					monitor.worked(10);
				} else if (current.isDirectory()) {
					monitor.subTask(ServerPlugin.getResource("%deletingTask", new String[] {current.getAbsolutePath()}));
					deleteDirectory(current, ProgressUtil.getSubMonitorFor(monitor, 10));
				}
			}
			dir.delete();
			monitor.done();
		} catch (Exception e) {
			Trace.trace("Error deleting directory " + dir.getAbsolutePath(), e);
		}
	}

	/**
	 * Expand a zip file to a given directory.
	 *
	 * @param zipFile java.io.File
	 * @param dir java.io.File
	 */
	public static void expandZip(File zipFile, File dir, IProgressMonitor monitor) {
		ZipInputStream zis = null;
	
		try {
			// first, count number of items in zip file
			zis = new ZipInputStream(new FileInputStream(zipFile));
			int count = 0;
			while (zis.getNextEntry() != null)
				count++;
	
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(ServerPlugin.getResource("%unZippingTask", new String[] {zipFile.getName()}), count);
			
			zis = new ZipInputStream(new FileInputStream(zipFile));
			ZipEntry ze = zis.getNextEntry();
	
			FileOutputStream out = null;
	
			while (ze != null) {
				try {
					monitor.subTask(ServerPlugin.getResource("%expandingTask", new String[] {ze.getName()}));
					File f = new File(dir, ze.getName());
	
					if (ze.isDirectory()) {
						out = null;
						f.mkdirs();
					} else {
						out = new FileOutputStream(f);
	
						int avail = zis.read(buf);
						while (avail > 0) {
							out.write(buf, 0, avail);
							avail = zis.read(buf);
						}
					}
				} catch (FileNotFoundException ex) {
					Trace.trace("Error extracting " + ze.getName() + " from zip " + zipFile.getAbsolutePath(), ex);
				} finally {
					try {
						if (out != null)
							out.close();
					} catch (Exception e) { }
				}
				ze = zis.getNextEntry();
				monitor.worked(1);
				if (monitor.isCanceled())
					return;
			}
			monitor.done();
		} catch (Exception e) {
			Trace.trace("Error expanding zip file " + zipFile.getAbsolutePath(), e);
		} finally {
			try {
				if (zis != null)
					zis.close();
			} catch (Exception ex) {
			}
		}
	}

	/**
	 * Copys a directory from a to b, only modifying as needed
	 * and deleting old files and directories.
	 *
	 * @param from java.lang.String
	 * @param to java.lang.String
	 * @param IProgressMonitor
	 */
	public static void smartCopyDirectory(String from, String to, IProgressMonitor monitor) {
		try {
			File fromDir = new File(from);
			File toDir = new File(to);
	
			File[] fromFiles = fromDir.listFiles();
			int fromSize = fromFiles.length;
	
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(ServerPlugin.getResource("%copyingTask", new String[] {from, to}), 550);
	
			File[] toFiles = null;
	
			// delete old files and directories from this directory
			if (toDir.exists() && toDir.isDirectory()) {
				toFiles = toDir.listFiles();
				int toSize = toFiles.length;
	
				// check if this exact file exists in the new directory
				for (int i = 0; i < toSize; i++) {
					String name = toFiles[i].getName();
					boolean isDir = toFiles[i].isDirectory();
					boolean found = false;
					for (int j = 0; j < fromSize; j++) {
						if (name.equals(fromFiles[j].getName()) && isDir == fromFiles[j].isDirectory())
							found = true;
					}
	
					// delete file if it can't be found or isn't the correct type
					if (!found) {
						if (isDir)
							deleteDirectory(toFiles[i], new NullProgressMonitor());
						else
							toFiles[i].delete();
					}
					if (monitor.isCanceled())
						return;
				}
			} else {
				if (toDir.isFile())
					toDir.delete();
				toDir.mkdir();
			}
			monitor.worked(50);
	
			// cycle through files and only copy when it doesn't exist
			// or is newer
			toFiles = toDir.listFiles();
			int toSize = toFiles.length;
			int dw = 0;
			if (toSize > 0)
				dw = 500 / toSize;
	
			for (int i = 0; i < fromSize; i++) {
				File current = fromFiles[i];
	
				// check if this is a new or newer file
				boolean copy = true;
				if (!current.isDirectory()) {
					String name = current.getName();
					long mod = current.lastModified();
					for (int j = 0; j < toSize; j++) {
						if (name.equals(toFiles[j].getName()) && mod <= toFiles[j].lastModified())
							copy = false;
					}
				}
	
				if (copy) {
					String fromFile = current.getAbsolutePath();
					String toFile = to;
					if (!toFile.endsWith(File.separator))
						toFile += File.separator;
					toFile += current.getName();
					if (current.isFile()) {
						copyFile(fromFile, toFile);
						monitor.worked(dw);
					} else if (current.isDirectory()) {
						monitor.subTask(ServerPlugin.getResource("%copyingTask", new String[] {fromFile, toFile}));
						smartCopyDirectory(fromFile, toFile, ProgressUtil.getSubMonitorFor(monitor, dw));
					}
				}
				if (monitor.isCanceled())
					return;
			}
			monitor.worked(500 - dw * toSize);
			monitor.done();
		} catch (Exception e) {
			Trace.trace("Error smart copying directory " + from + " - " + to, e);
		}
	}
}