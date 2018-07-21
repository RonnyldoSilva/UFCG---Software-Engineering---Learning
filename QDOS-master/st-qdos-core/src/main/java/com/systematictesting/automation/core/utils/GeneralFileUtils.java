/**
 * Copyright (c) Jan 11, 2017 Systematic Testing Ltd. (www.systematictesting.com) to Present.
 * All rights reserved. 
 */
package com.systematictesting.automation.core.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import com.systematictesting.automation.core.constants.FrameworkParams;
import com.systematictesting.automation.core.constants.SystemParams;
import com.systematictesting.automation.core.main.ActivateFramework;

public class GeneralFileUtils {
	private static final Logger log = Logger.getLogger(GeneralFileUtils.class.getName());

	@SuppressWarnings("resource")
	public static boolean extractResourcesToTempFolder(String tempDir) {
		try {
			// If folder exist, delete it.
			String destPath = tempDir + File.separator + "SystematicTesting" + File.separator;
			File destPathDirectory = new File(destPath);
			if (destPathDirectory.isDirectory()) {
				FileUtils.deleteDirectory(destPathDirectory);
			}
			JarFile jarFile = new JarFile(ActivateFramework.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			Enumeration<JarEntry> enums = jarFile.entries();
			while (enums.hasMoreElements()) {
				JarEntry entry = enums.nextElement();
				if (entry.getName().startsWith(SystemParams.DRIVERS_DIR)) {
					log.log(Level.INFO, "EXTRACTING DRIVERS : ", entry.getName());
					File toWrite = new File(destPath + entry.getName());
					if (entry.isDirectory()) {
						toWrite.mkdirs();
						continue;
					}
					InputStream in = new BufferedInputStream(jarFile.getInputStream(entry));
					OutputStream out = new BufferedOutputStream(new FileOutputStream(toWrite));
					byte[] buffer = new byte[2048];
					for (;;) {
						int nBytes = in.read(buffer);
						if (nBytes <= 0) {
							break;
						}
						out.write(buffer, 0, nBytes);
					}
					out.flush();
					out.close();
					in.close();
				}
				log.log(Level.FINE, entry.getName());
			}
			if (CommandLineParamsUtils.getInstance().getOperatingSystem().equals(FrameworkParams.OS_MACOSX_10_12_1)) {
				Set<PosixFilePermission> perms = new HashSet<>();
				perms.add(PosixFilePermission.OWNER_EXECUTE);
				perms.add(PosixFilePermission.OWNER_WRITE);
				perms.add(PosixFilePermission.OWNER_READ);
				Files.setPosixFilePermissions(destPathDirectory.toPath(), perms);

				File driversPathDirectory = new File(destPath + SystemParams.DRIVERS_DIR);
				File[] fileList = driversPathDirectory.listFiles();
				for (File file : fileList) {
					if (!file.isDirectory()) {
						Files.setPosixFilePermissions(file.toPath(), perms);
					}
				}
			}

		} catch (IOException ex) {
			log.log(Level.SEVERE, "Exception in extracting files from JAR File : ", ex);
			return false;
		}
		return true;
	}
}
