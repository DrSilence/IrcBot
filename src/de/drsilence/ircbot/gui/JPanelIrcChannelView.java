package de.drsilence.ircbot.gui;


import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import javax.swing.JButton;

public class JPanelIrcChannelView extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2024863130958311536L;
	private JTable chatView;
	private JTable userList;
	private JPanel panel;
	private JButton btnSend;
	private JTextField inputField;

	/**
	 * Create the panel.
	 */
	public JPanelIrcChannelView() {
		setLayout(new BorderLayout());
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.99);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		add(splitPane, BorderLayout.CENTER);
		
		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setResizeWeight(0.9);
		splitPane.setLeftComponent(splitPane_1);
		
		chatView = new JTable();
		splitPane_1.setLeftComponent(chatView);
		
		userList = new JTable();
		splitPane_1.setRightComponent(userList);
		
		panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
		
		inputField = new JTextField();
		panel.add(inputField, BorderLayout.CENTER);
		inputField.setColumns(10);
		splitPane.setRightComponent(panel);
		
		btnSend = new JButton("Send");
		panel.add(btnSend, BorderLayout.EAST);

	}

}
