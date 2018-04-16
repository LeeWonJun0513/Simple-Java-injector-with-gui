package me.roman.injection.utils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Utils {

	public static void copy(InputStream in, OutputStream out) throws Exception {
		byte[] buf = new byte[1024];
		int bytesRead;
		while ((bytesRead = in.read(buf)) != -1) {
			out.write(buf, 0, bytesRead);
		}
	}

	public static String randomString(int len) {
		char[] letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890".toCharArray();
		String s = "";
		for (int i = 0; i < len; i++) {
			s += letters[new Random().nextInt(letters.length)];
		}
		return s;
	}

	public static String randomUnicodes(int len) {
		char[] letters = "	  ".toCharArray();
		String s = "";
		for (int i = 0; i < len; i++) {
			s += letters[new Random().nextInt(letters.length)];
		}
		return s;
	}

	public static File showOpenDialog() {
		JFileChooser c = new JFileChooser();
		FileFilter filter = new FileNameExtensionFilter("Executable Jar File", "jar");
		c.setFileFilter(filter);
		c.showOpenDialog(null);
		return c.getSelectedFile();
	}

}
