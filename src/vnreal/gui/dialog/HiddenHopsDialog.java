/* ***** BEGIN LICENSE BLOCK *****
 * Copyright (C) 2010-2011, The VNREAL Project Team.
 * 
 * This work has been funded by the European FP7
 * Network of Excellence "Euro-NF" (grant agreement no. 216366)
 * through the Specific Joint Developments and Experiments Project
 * "Virtual Network Resource Embedding Algorithms" (VNREAL). 
 *
 * The VNREAL Project Team consists of members from:
 * - University of Wuerzburg, Germany
 * - Universitat Politecnica de Catalunya, Spain
 * - University of Passau, Germany
 * See the file AUTHORS for details and contact information.
 * 
 * This file is part of ALEVIN (ALgorithms for Embedding VIrtual Networks).
 *
 * ALEVIN is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License Version 3 or later
 * (the "GPL"), or the GNU Lesser General Public License Version 3 or later
 * (the "LGPL") as published by the Free Software Foundation.
 *
 * ALEVIN is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * or the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and
 * GNU Lesser General Public License along with ALEVIN; see the file
 * COPYING. If not, see <http://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ***** */
package vnreal.gui.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import mulavito.gui.dialogs.AbstractButtonDialog;
import mulavito.utils.ClassScanner;
import vnreal.Scenario;
import vnreal.algorithms.GenericMappingAlgorithm;
import vnreal.gui.GUI;
import vnreal.hiddenhopmapping.IHiddenHopMapping;

/**
 * @author Vlad Singeorzan
 * @since 2011-05-19
 */
@SuppressWarnings("serial")
public class HiddenHopsDialog extends AbstractButtonDialog {
	private GenericMappingAlgorithm algorithm;
	private JTable hhTable;
	private MyTableModel hhTableModel;
	
	private final Scenario scenario;

	public HiddenHopsDialog(GenericMappingAlgorithm algo, Scenario scenario) {
		super(GUI.getInstance(), "Set HiddenHops", "Set", new Dimension(500,
				300));
		this.scenario = scenario;
		algorithm = algo;
		pack();
		setVisible(true);
	}

	@Override
	protected JPanel createContent() {
		JPanel content = new JPanel();
		GroupLayout layout = new GroupLayout(content);
		content.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		// parameters panel
		hhTable = new JTable();
		hhTable.setAutoscrolls(true);
		JScrollPane resScrollPane = new JScrollPane(hhTable);
		resScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		resScrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		hhTable.setFillsViewportHeight(true);
		hhTable.setPreferredScrollableViewportSize(new Dimension(400, 100));
		hhTableModel = new MyTableModel();
		hhTable.setModel(hhTableModel);
		hhTable.setDefaultRenderer(Class.class, new MyClassTableCellRenderer());
		hhTable.setDefaultEditor(Double.class, new MyDoubleTableCellEditor());
		hhTableModel.addColumn("Select");
		hhTableModel.addColumn("Hidden Hop Mapping");
		hhTableModel.addColumn("Factor");
		JPanel hhPanel = new JPanel();
		hhPanel.setBorder(BorderFactory.createTitledBorder("Hidden Hops"));
		hhPanel.add(resScrollPane);

		// create the content for the hh panel
		for (Class<?> hh : getAllHhs()) {
			try {
				hh.getDeclaredField(new String("factor"));
				// add a row to the factor table.
				hhTableModel
						.addRow(new Object[] { false, hh, new Double(0.0) });
			} catch (NoSuchFieldException ex) {
			}
		}
		// add the panels
		layout.setHorizontalGroup(layout.createParallelGroup().addComponent(
				hhPanel));
		layout.setVerticalGroup(layout.createSequentialGroup().addComponent(
				hhPanel));

		return content;
	}

	@Override
	protected void doAction() {
		if (scenario.getNetworkStack() == null) {
			JOptionPane.showMessageDialog(this, "Error: No Scenario open!",
					"Constraints Generator", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// stop the editing of the tables to ensure we read every parameter
		// entered
		if (hhTable.getCellEditor() != null)
			hhTable.getCellEditor().stopCellEditing();

		// create and add HHMappings
		List<IHiddenHopMapping> hhs = new LinkedList<IHiddenHopMapping>();
		Class<?> hhClass;
		for (int r = 0; r < hhTable.getRowCount(); r++) {
			// if selected
			if ((Boolean) hhTable.getValueAt(r, 0)) {
				try {
					hhClass = (Class<?>) hhTable.getValueAt(r, 1);
					if (hhTable.getValueAt(r, 2) != null)
						hhs.add((IHiddenHopMapping) hhClass.getConstructor(
								Double.class).newInstance(
								hhTable.getValueAt(r, 2)));
					else
						hhs.add((IHiddenHopMapping) hhClass.getConstructor()
								.newInstance());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		algorithm.setHhMappings(hhs);
	}

	private Class<?>[] getAllHhs() {
		List<Class<?>> hhClasses = Arrays.asList(ClassScanner
				.getClassesImplementing(this.getClass(),
						"vnreal.hiddenhopmapping", IHiddenHopMapping.class));

		// sort.
		Collections.sort(hhClasses, new Comparator<Class<?>>() {
			@Override
			public int compare(Class<?> o1, Class<?> o2) {
				// Sort by class name
				return o1.getSimpleName().compareTo(o2.getSimpleName());
			}
		});

		return hhClasses.toArray(new Class[hhClasses.size()]);
	}

	private class MyTableModel extends DefaultTableModel {
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return getValueAt(0, columnIndex).getClass();
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			if (column == 0)
				return true;
			else
				return (Boolean) getValueAt(row, 0);
		}
	}

	private class MyDoubleTableCellEditor extends AbstractCellEditor implements
			TableCellEditor {
		private JSpinner spinner;

		public MyDoubleTableCellEditor() {
			super();
			spinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 0.1));
		}

		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			spinner.setValue(value);
			return spinner;
		}

		@Override
		public Object getCellEditorValue() {
			return spinner.getValue();
		}
	}

	private class MyClassTableCellRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			return new JLabel(((Class<?>) value).getSimpleName());
		}
	}
}
