package de.drsilence.ircbot.gui;



import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.JTabbedPane;

import java.awt.BorderLayout;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;



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
		
		addSettings(new TreeMap<String,Object>());
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
							return e.getValue().toString();
						}
					}
				}
				return null;
			}

			@Override
			public int getRowCount() {
				return _map.size() ;
			}

			@Override
			public int getColumnCount() {
				return 2;  // fixed on maps -> 2 (key, value)
			}

			@Override
			public String getColumnName(int column) {
				String tmp = super.getColumnName(column);
				return column == 0 ? "Key":"Value";
			}
			
		};
		//JTable table = new JTable(model);
		tableSettings.setModel(model);
		tabbedPane.addTab("Default", null, table, null);
	}

}
