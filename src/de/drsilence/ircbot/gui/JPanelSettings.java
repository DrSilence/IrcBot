package de.drsilence.ircbot.gui;



import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.JTabbedPane;

import java.awt.BorderLayout;

import java.util.Map;
import java.util.Map.Entry;



public class JPanelSettings extends JPanel {

	private JTabbedPane tabbedPane;
	private JTable tableSettings;

	/**
	 * Create Panel
	 */
	public JPanelSettings() {
		setLayout(new BorderLayout());

		tabbedPane = new JTabbedPane();
		add(tabbedPane, BorderLayout.CENTER);
	}

	public void addSettings(final Map<String,Object> map) {
		TableModel model = new AbstractTableModel() {
			Map<String,Object> _map = map;

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				int i = 0;
				for(Entry<String, Object> e : _map.entrySet() ) {
					if(i++ == rowIndex) {
						if(columnIndex==0) {
							return e.getKey();
						} else if(columnIndex==1) {
							return e.getValue();
						}
					}
				}
				return null;
			}

			@Override
			public int getRowCount() {
				return _map.size();
			}

			@Override
			public int getColumnCount() {
				return 2;  // fixed on maps -> 2 (key, value)
			}
		};
		JTable table = new JTable(model);
		tabbedPane.addTab("Title", null, table, null);
	}

}
