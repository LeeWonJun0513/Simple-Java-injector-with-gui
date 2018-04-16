package me.roman.injection.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class JarHandler {

	public void replaceJarFile(String jarPathAndName, byte[] fileByteCode, String fileName) throws IOException {
		File jarFile = new File(jarPathAndName);
		File tempJarFile = new File(jarPathAndName + ".tmp");
		JarFile jar = new JarFile(jarFile);
		boolean jarWasUpdated = false;

		try {
			JarOutputStream tempJar = new JarOutputStream(new FileOutputStream(tempJarFile));

			byte[] buffer = new byte[1024];
			int bytesRead;

			try {
				try {

					JarEntry entry = new JarEntry(fileName);
					tempJar.putNextEntry(entry);
					tempJar.write(fileByteCode);

				} catch (Exception ex) {
					System.out.println(ex);
					tempJar.putNextEntry(new JarEntry("stub"));
				}
				InputStream entryStream = null;
				for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements();) {

					JarEntry entry = (JarEntry) entries.nextElement();

					if (!entry.getName().equals(fileName)) {

						entryStream = jar.getInputStream(entry);
						tempJar.putNextEntry(entry);

						while ((bytesRead = entryStream.read(buffer)) != -1) {
							tempJar.write(buffer, 0, bytesRead);
						}
					}
				}
				if (entryStream != null)
					entryStream.close();
				jarWasUpdated = true;
			} catch (Exception ex) {
				System.out.println(ex);

				tempJar.putNextEntry(new JarEntry("stub"));
			} finally {
				tempJar.close();
			}
		} finally {

			jar.close();

			if (!jarWasUpdated) {
				tempJarFile.delete();
			}
		}

		if (jarWasUpdated) {
			if (jarFile.delete()) {
				tempJarFile.renameTo(jarFile);
				System.out.println(jarPathAndName + " updated.");
			} else
				System.out.println("Could Not Delete JAR File");
		}
	}
}
