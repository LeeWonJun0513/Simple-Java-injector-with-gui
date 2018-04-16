package me.roman.injection;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import me.roman.injection.utils.ClassSelectEvent;
import me.roman.injection.utils.DialogSelectClass;
import me.roman.injection.utils.JarHandler;
import me.roman.injection.utils.Utils;

public class Gui extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField fileInputBox;
	private JTextField fileOutputBox;
	public static JTextField inWhichClassBox;
	private JTextField inWhichMethodBox;
	private JTextField stubInputBox;
	private JTextField classToInject;

	public Gui() {
		setTitle("Simple Javassist injector");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 452, 336);
		setResizable(false);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblJinjectx = new JLabel("JxInject");
		lblJinjectx.setFont(new Font("Sylfaen", Font.PLAIN, 35));
		lblJinjectx.setBounds(147, 11, 120, 38);
		contentPane.add(lblJinjectx);

		fileInputBox = new JTextField();
		fileInputBox.setBounds(104, 60, 265, 20);
		contentPane.add(fileInputBox);
		fileInputBox.setColumns(10);

		JLabel lblInput = new JLabel("File Input:");
		lblInput.setBounds(10, 63, 54, 14);
		contentPane.add(lblInput);

		JButton btnNewButton = new JButton("...");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File fileInput = Utils.showOpenDialog();
				if (fileInput != null) {
					fileInputBox.setText(fileInput.getAbsolutePath());
					fileOutputBox.setText(fileInput.getAbsolutePath().replace(".jar", "-new.jar"));

					try {
						String mainClass = null;
						JarFile jar = new JarFile(fileInput);
						Map<Object, Object> map = jar.getManifest().getMainAttributes();
						for (Object obj : map.keySet()) {
							if (obj.toString().equalsIgnoreCase("main-class")) {
								mainClass = map.get(obj).toString();
								break;
							}
						}
						inWhichClassBox.setText(mainClass);
						inWhichMethodBox.setText("main");
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		});
		btnNewButton.setBounds(379, 60, 47, 20);
		contentPane.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("INJECT");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Starting injection...");

				try {
					inject(new File(fileInputBox.getText()), new File(fileOutputBox.getText()),
							inWhichClassBox.getText(), inWhichMethodBox.getText(), new File(stubInputBox.getText()),
							classToInject.getText());
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("Done!");
				JOptionPane.showMessageDialog(null, "Done!", "JxInject", JOptionPane.INFORMATION_MESSAGE);
				System.exit(0);
			}
		});
		btnNewButton_1.setFont(new Font("Sylfaen", Font.PLAIN, 14));
		btnNewButton_1.setBounds(10, 275, 416, 23);
		contentPane.add(btnNewButton_1);

		JLabel lblFileOutput = new JLabel("File Output:");
		lblFileOutput.setBounds(10, 92, 57, 14);
		contentPane.add(lblFileOutput);

		fileOutputBox = new JTextField();
		fileOutputBox.setColumns(10);
		fileOutputBox.setBounds(104, 89, 265, 20);
		contentPane.add(fileOutputBox);

		JButton button = new JButton("...");
		button.setBounds(379, 89, 47, 20);
		contentPane.add(button);

		JLabel lblStub = new JLabel("Stub:");
		lblStub.setBounds(10, 206, 57, 14);
		contentPane.add(lblStub);

		stubInputBox = new JTextField();
		stubInputBox.setColumns(10);
		stubInputBox.setBounds(104, 203, 265, 20);
		contentPane.add(stubInputBox);

		JButton button_1 = new JButton("...");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File fileStub = Utils.showOpenDialog();
				if (fileStub != null) {
					stubInputBox.setText(fileStub.getAbsolutePath());

					try {
						String mainClass = null;
						JarFile jar = new JarFile(fileStub);
						Map<Object, Object> map = jar.getManifest().getMainAttributes();
						for (Object obj : map.keySet()) {
							if (obj.toString().equalsIgnoreCase("main-class")) {
								mainClass = map.get(obj).toString();
								break;
							}
						}
						classToInject.setText(mainClass);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		button_1.setBounds(379, 203, 47, 20);
		contentPane.add(button_1);

		JSeparator separator = new JSeparator();
		separator.setBounds(10, 182, 416, 2);
		contentPane.add(separator);

		JLabel lblInWhichClass = new JLabel("In which class:");
		lblInWhichClass.setBounds(10, 120, 70, 14);
		contentPane.add(lblInWhichClass);

		inWhichClassBox = new JTextField();
		inWhichClassBox.setColumns(10);
		inWhichClassBox.setBounds(104, 117, 265, 20);
		contentPane.add(inWhichClassBox);

		JButton button_2 = new JButton("...");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DialogSelectClass d = new DialogSelectClass(new ClassSelectEvent() {
					public void onSelect(String clazz) {
						inWhichClassBox.setText(clazz);
					}
				}, fileInputBox.getText());
				d.setVisible(true);
			}
		});
		button_2.setBounds(379, 117, 47, 20);
		contentPane.add(button_2);

		JLabel lblWhichClass = new JLabel("Class to inject:");
		lblWhichClass.setBounds(10, 237, 84, 14);
		contentPane.add(lblWhichClass);

		classToInject = new JTextField();
		classToInject.setColumns(10);
		classToInject.setBounds(104, 234, 265, 20);
		contentPane.add(classToInject);

		JButton button_3 = new JButton("...");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DialogSelectClass d = new DialogSelectClass(new ClassSelectEvent() {
					public void onSelect(String clazz) {
						classToInject.setText(clazz);
					}
				}, stubInputBox.getText());
				d.setVisible(true);
			}
		});
		button_3.setBounds(379, 237, 47, 20);
		contentPane.add(button_3);

		JLabel lblInWhichMethod = new JLabel("In which method:");
		lblInWhichMethod.setBounds(10, 148, 83, 14);
		contentPane.add(lblInWhichMethod);

		inWhichMethodBox = new JTextField();
		inWhichMethodBox.setColumns(10);
		inWhichMethodBox.setBounds(104, 145, 265, 20);
		contentPane.add(inWhichMethodBox);

		JLabel lblNewLabel = new JLabel("by Roman");
		lblNewLabel.setFont(new Font("Sylfaen", Font.PLAIN, 10));
		lblNewLabel.setBounds(262, 35, 54, 14);
		contentPane.add(lblNewLabel);
	}

	private static void inject(File input, File output, String whichClass, String whichMethod, File stubInput,
			String classToInject) {
		try {
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(output));
			StringBuilder sb = new StringBuilder();
			char[] charObjectArray = whichClass.toCharArray();
			for (char c : charObjectArray)
				sb.append(c == '.' ? "/" : c);

			ZipFile inp = new ZipFile(input);

			Enumeration<? extends ZipEntry> entries = inp.entries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				out.putNextEntry(entry);

				Utils.copy(inp.getInputStream(entry), out);

				out.closeEntry();
			}
			inp.close();

			JarHandler jarHandler = new JarHandler();
			ZipFile zip = new ZipFile(stubInput);
			Enumeration<? extends ZipEntry> entries1 = zip.entries();
			while (entries1.hasMoreElements()) {
				ZipEntry entry = entries1.nextElement();
				if (!entry.getName().toLowerCase().contains("meta-inf")) {
					InputStream in = zip.getInputStream(entry);
					out.putNextEntry(entry);
					Utils.copy(in, out);
					out.closeEntry();
					in.close();
				}
			}
			zip.close();
			out.closeEntry();

			out.close();

			ClassPool pool = ClassPool.getDefault();
			ClassPath classPath = pool.insertClassPath(output.getAbsolutePath());

			CtClass cc = pool.get(whichClass);
			String injectedMethodName = Utils.randomString(10);
			CtMethod m = CtNewMethod.make("public static void " + injectedMethodName + "() {" + "new "
					+ classToInject.replace(".class", "") + "();" + " }", cc);
			cc.addMethod(m);

			CtMethod method = cc.getDeclaredMethod(whichMethod);
			method.insertBefore(injectedMethodName + "();");

			byte[] b = cc.toBytecode();
			pool.removeClassPath(classPath);

			jarHandler.replaceJarFile(output.getAbsolutePath(), b, sb.toString() + ".class");
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "ERROR: " + e.getClass().getSimpleName() + ": " + e.getMessage(), "JxInject", JOptionPane.ERROR_MESSAGE);
		}
	}
}
