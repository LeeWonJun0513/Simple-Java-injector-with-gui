package me.roman.injection.utils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

@SuppressWarnings("serial")
public class DialogSelectClass extends JDialog {
	
	private JTree tree;
	private static String input;

	public DialogSelectClass(final ClassSelectEvent event, String input) {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Select class");
		setResizable(false);
		setModal(true);
		setBounds(100, 100, 318, 316);
		setSize(444, 400);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		
		DialogSelectClass.input = input;
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		
		JButton btnSelect = new JButton("Select");
		btnSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (tree.getSelectionPath() != null) {
					String clazz = tree.getSelectionPath().getPath()[1].toString().replace("/", ".").replace(".class", "");
					
					event.onSelect(clazz);
					
					setVisible(false);
					dispose();
				}
			}
		});
		
		JScrollPane scrollPane = new JScrollPane();
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap(170, Short.MAX_VALUE)
					.addComponent(btnSelect)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnCancel)
					.addContainerGap())
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 418, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(12, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 310, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnCancel)
						.addComponent(btnSelect))
					.addContainerGap())
		);
		
		tree = new JTree();
		tree.setShowsRootHandles(true);
		tree.setRootVisible(false);
		tree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("Root") {
				{
					addNodes(this);
				}
			}
		));
		tree.setCellRenderer(new DefaultTreeCellRenderer() {
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
				super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
				return this;
			}
		});
		scrollPane.setViewportView(tree);
		getContentPane().setLayout(groupLayout);
	}
	
	public void addNodes(DefaultMutableTreeNode node) {
		node.removeAllChildren();
		
		try {
			JarFile jar = new JarFile(new File(input));
			
			Enumeration<? extends JarEntry> entries = jar.entries();
			
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();				
				
				if (entry.getName().toLowerCase().endsWith(".class")) {
					node.add(new DefaultMutableTreeNode(entry.getName()));
				}
			}
			

			
			jar.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "ERROR: Failed loading classes: " + ex.getClass().getSimpleName() + ": " + ex.getMessage(), "JxInject", JOptionPane.ERROR_MESSAGE);
		}
	}
}
