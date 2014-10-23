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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import mulavito.gui.dialogs.AbstractButtonDialog;
import mulavito.utils.ClassScanner;
import mulavito.utils.distributions.UniformStream;
import vnreal.ExchangeParameter;
import vnreal.Scenario;
import vnreal.constraints.ILinkConstraint;
import vnreal.constraints.INodeConstraint;
import vnreal.demands.AbstractDemand;
import vnreal.demands.IdDemand;
import vnreal.gui.GUI;
import vnreal.network.Network;
import vnreal.network.NetworkEntity;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;
import vnreal.resources.IdResource;

/**
 * @author Vlad Singeorzan
 * @author Michael Duelli
 */
@SuppressWarnings("serial")
public class ConstraintsGeneratorDialog extends AbstractButtonDialog {
	private JTable resourcesTable;
	private MyTableModel resTableModel;
	private List<JTable> demandsTables;
	private List<MyTableModel> demTableModels;

	private List<String[]> resParamNames;
	private List<String[]> demParamNames;

	private static UniformStream stream = new UniformStream();
	
	private final Scenario scenario;

	public ConstraintsGeneratorDialog(Scenario scenario) {
		super(GUI.getInstance(), "Generate Constraints", "Generate",
				new Dimension(450, 700));
		this.scenario = scenario;

		actionBtn.removeActionListener(actionBtn.getActionListeners()[0]);
		actionBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (checkInput())
					doAction();
			}
		});

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

		resParamNames = new LinkedList<String[]>();
		demParamNames = new LinkedList<String[]>();
		// resources panel
		resourcesTable = new JTable();
		resourcesTable.setAutoscrolls(true);
		JScrollPane resScrollPane = new JScrollPane(resourcesTable);
		resScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		resScrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		resourcesTable.setFillsViewportHeight(true);
		resourcesTable.setPreferredScrollableViewportSize(new Dimension(400,
				200));
		resourcesTable.setDefaultRenderer(Class.class,
				new MyClasTableCellRenderer());
		resourcesTable.setDefaultRenderer(String[].class,
				new MyParamsTableCellRenderer());
		resourcesTable.setDefaultEditor(String[].class,
				new MyParamsTableCellEditor());
		resTableModel = new MyTableModel();
		resourcesTable.setModel(resTableModel);
		resTableModel.addColumn("Select");
		resTableModel.addColumn("Resource");
		resTableModel.addColumn("Maximum parameter values");
		JPanel resourcesPanel = new JPanel();
		resourcesPanel.setBorder(BorderFactory.createTitledBorder("Resources"));
		resourcesPanel.add(resScrollPane);

		// add the resources to the table
		List<String> resParamNameList;
		for (Class<?> res : getAllResources()) {
			resParamNameList = new LinkedList<String>();
			if (!res.equals(IdResource.class)) {
				for (final Method m : (res.getDeclaredMethods())) {
					// for every setter method get the name of the parameter
					// to set and display it as a label along with a text
					// field for setting the value.
					if (m.isAnnotationPresent(ExchangeParameter.class)
							&& m.getName().startsWith("set")) {
						String name = m.getName().substring(3).toLowerCase();
						resParamNameList.add(name);
					}
				}
			}
			resParamNames.add(resParamNameList
					.toArray(new String[resParamNameList.size()]));
			String[] resParamValues = new String[resParamNameList.size()];
			for (int v = 0; v < resParamValues.length; v++)
				resParamValues[v] = new String();
			// add a new table row only if we have a parameter to set
			resTableModel.addRow(new Object[] { false, res, resParamValues });
			if (resParamNameList.size() == 0)
				resourcesTable.setRowHeight(resourcesTable.getRowCount() - 1,
						30);
		}

		// demands panel

		JTabbedPane demandsTabbedPane = new JTabbedPane(JTabbedPane.TOP,
				JTabbedPane.WRAP_TAB_LAYOUT);
		demandsTables = new ArrayList<JTable>();
		demTableModels = new ArrayList<MyTableModel>();
		int l = 1;
		for (Network<?, ?, ?> layer = scenario.getNetworkStack()
				.getLayer(l); layer != null; layer = scenario
				.getNetworkStack().getLayer(++l)) {
			JTable demandsTable = new JTable();
			demandsTables.add(demandsTable);
			demandsTable.setAutoscrolls(true);
			JScrollPane demScrollPane = new JScrollPane(demandsTable);
			demScrollPane
					.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			demScrollPane
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			demandsTable.setFillsViewportHeight(true);
			demandsTable.setPreferredScrollableViewportSize(new Dimension(400,
					200));
			demandsTable.setDefaultRenderer(Class.class,
					new MyClasTableCellRenderer());
			demandsTable.setDefaultRenderer(String[].class,
					new MyParamsTableCellRenderer());
			demandsTable.setDefaultEditor(String[].class,
					new MyParamsTableCellEditor());
			MyTableModel demTableModel = new MyTableModel();
			demTableModels.add(demTableModel);
			demandsTable.setModel(demTableModel);
			demTableModel.addColumn("Select");
			demTableModel.addColumn("Demand");
			demTableModel.addColumn(("Maximum parameter values"));
			List<String> demParamNameList;
			for (Class<?> dem : getAllDemands()) {
				demParamNameList = new LinkedList<String>();
				if (!dem.equals(IdDemand.class)) {
					for (final Method m : (dem.getDeclaredMethods())) {
						// for every setter method get the name of the
						// parameter
						// to set and display it as a label along with a
						// text
						// field for setting the value.
						if (m.isAnnotationPresent(ExchangeParameter.class)
								&& m.getName().startsWith("set")) {
							String name = m.getName().substring(3)
									.toLowerCase();
							demParamNameList.add(name);
						}
					}
				}
				demParamNames.add(demParamNameList
						.toArray(new String[demParamNameList.size()]));
				String[] demParamValues = new String[demParamNameList.size()];
				for (int v = 0; v < demParamValues.length; v++)
					demParamValues[v] = new String();

				demTableModel
						.addRow(new Object[] { false, dem, demParamValues });
				if (demParamNameList.size() == 0)
					demandsTable.setRowHeight(demandsTable.getRowCount() - 1,
							30);
			}
			JPanel tablePanel = new JPanel();
			tablePanel.add(demScrollPane);
			demandsTabbedPane.addTab("VN " + l, tablePanel);
		}

		JPanel demandsPanel = new JPanel();
		demandsPanel.setBorder(BorderFactory.createTitledBorder("Demands"));
		demandsPanel.add(demandsTabbedPane);

		// if at least one VN.
		if (scenario.getNetworkStack().getLayer(1) == null)
			// if no virtual networks present, hide the demands panel!
			demandsPanel.setVisible(false);

		// add the panels
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(resourcesPanel).addComponent(demandsPanel));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(resourcesPanel).addComponent(demandsPanel));

		return content;
	}

	private boolean checkInput() {
		// stop the editing of the tables to ensure we read every parameter
		// entered
		if (resourcesTable.getCellEditor() != null)
			resourcesTable.getCellEditor().stopCellEditing();
		for (JTable demandsTable : demandsTables)
			if (demandsTable.getCellEditor() != null)
				demandsTable.getCellEditor().stopCellEditing();

		// make sure values for the parameters of all selected constraints are
		// set.
		for (int r = 0; r < resTableModel.getRowCount(); r++)
			if ((Boolean) resTableModel.getValueAt(r, 0))
				for (String val : (String[]) resTableModel.getValueAt(r, 2))
					if (val.length() == 0) {
						JOptionPane.showMessageDialog(
								this,
								"Parameters for "
										+ ((Class<?>) resTableModel.getValueAt(
												r, 1)).getSimpleName()
										+ "not set.", "Error",
								JOptionPane.ERROR_MESSAGE);
						return false;
					}

		for (MyTableModel demTableModel : demTableModels) {
			for (int r = 0; r < demTableModel.getRowCount(); r++)
				if ((Boolean) demTableModel.getValueAt(r, 0))
					for (String val : (String[]) demTableModel.getValueAt(r, 2))
						if (val.length() == 0) {
							JOptionPane
									.showMessageDialog(
											this,
											"Parameters for "
													+ ((Class<?>) demTableModel
															.getValueAt(r, 1))
															.getSimpleName()
													+ " in VN "
													+ (demTableModels
															.indexOf(demTableModel) + 1)
													+ " not set.", "Error",
											JOptionPane.ERROR_MESSAGE);
							return false;
						}
		}

		return true;
	}

	@Override
	protected void doAction() {
		if (scenario.getNetworkStack() == null) {
			JOptionPane.showMessageDialog(this, "Error: No Scenario open!",
					"Constraints Generator", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// generate the constraints from the tables
		List<Class<?>> resClassesToGenerate = new LinkedList<Class<?>>();
		List<String[]> resParamNamesToGenerate = new LinkedList<String[]>();

		// resources

		// resMaxValues.get(i) are the maximum values for the resource class in
		// selectedResources.get(i)
		List<String[]> resMaxValues = new ArrayList<String[]>();

		for (int r = 0; r < resTableModel.getRowCount(); r++)
			if ((Boolean) resTableModel.getValueAt(r, 0)) {
				resClassesToGenerate.add((Class<?>) resTableModel.getValueAt(r,
						1));
				resParamNamesToGenerate.add(resParamNames.get(r));
				resMaxValues.add((String[]) resTableModel.getValueAt(r, 2));
			}
		generateConstraintsSubstrate(resClassesToGenerate,
				resParamNamesToGenerate, resMaxValues, scenario.getNetworkStack());
		// demands

		// demMaxValues.get(l).get(i) are the maximum values for the layer l and
		// the demand class in classesToGenerate.get(i)
		List<List<String[]>> demMaxValues = new ArrayList<List<String[]>>();

		List<List<Class<?>>> demClassesToGenerate = new LinkedList<List<Class<?>>>();
		List<List<String[]>> demParamNamesToGenerate = new LinkedList<List<String[]>>();
		for (MyTableModel demTableModel : demTableModels) {
			List<Class<?>> classes = new LinkedList<Class<?>>();
			demClassesToGenerate.add(classes);
			List<String[]> maxValues = new LinkedList<String[]>();
			demMaxValues.add(maxValues);
			List<String[]> paramNames = new LinkedList<String[]>();
			demParamNamesToGenerate.add(paramNames);

			for (int r = 0; r < demTableModel.getRowCount(); r++)
				if ((Boolean) demTableModel.getValueAt(r, 0)) {
					classes.add((Class<?>) demTableModel.getValueAt(r, 1));
					paramNames.add(demParamNames.get(r));
					maxValues.add((String[]) demTableModel.getValueAt(r, 2));
				}
		}
		generateConstraintsVirtual(demClassesToGenerate,
				demParamNamesToGenerate, demMaxValues, scenario.getNetworkStack());
		// TODO ..
		if (true) {
			setVisible(false);
			dispose();
		}

	}

	/**
	 * Generates the constraints specified by the classes in resClasss. The
	 * values of the parameters of this constraints are randomly determined
	 * according to a uniform distribution in the interval [0, maxValue], with
	 * maxValue specified in the resMaxParamValues and demMaxParamValues as
	 * described below.
	 * 
	 * @param resClasses
	 *            The classes of the resources to generate.
	 * @param resParamNames
	 *            The parameter names of the resources to generate.
	 *            resParamNames.get(i) returns an array containing the parameter
	 *            names for the resource class in resClasses.get(i).
	 * @param resParamMaxValues
	 *            The maximum values for the parameters of the resources to
	 *            generate. resParamMaxValues.get(i) returns an array containing
	 *            the maximum values for the parameters of the resource class in
	 *            resClasses.get(i).
	 */
	public static void generateConstraintsSubstrate(List<Class<?>> resClasses,
			List<String[]> resParamNames, List<String[]> resParamMaxValues, NetworkStack stack) {
		// generate the constraints from the tables
		for (int r = 0; r < resClasses.size(); r++) {
			// generate resources for the substrate network
			if (isNodeClass(resClasses.get(r))) {
				// node resource, get the parameters
				for (SubstrateNode sNode : stack.getSubstrate()
						.getVertices()) {
					addResource(sNode, resClasses.get(r), resParamNames.get(r),
							resParamMaxValues.get(r), stack);
				}
			} else {
				// link resource
				for (SubstrateLink sLink : stack.getSubstrate()
						.getEdges()) {
					addResource(sLink, resClasses.get(r), resParamNames.get(r),
							resParamMaxValues.get(r), stack);
				}
			}
		}
	}

	/**
	 * Generates the constraints specified by the classes in demClasses. The
	 * values of the parameters of this constraints are randomly determined
	 * according to a uniform distribution in the interval [0, maxValue], with
	 * maxValue specified in the resMaxParamValues and demMaxParamValues as
	 * described below.
	 * 
	 * @param demClasses
	 *            The classes of the demands to generate.
	 * @param demParamNames
	 *            The parameter names of the demands to generate.
	 *            demParamNames.get(i) returns an array containing the parameter
	 *            names for the demand class in demClasses.get(i).
	 * @param demParamMaxValues
	 *            The maximum values for the parameters of the demands to
	 *            generate. demParamMaxValues.get(l).get(i) returns an array
	 *            containing the maximum values for the parameters of the demand
	 *            class in demClasses.get(i) for the layer l.
	 */
	public static void generateConstraintsVirtual(
			List<List<Class<?>>> demClasses,
			List<List<String[]>> demParamNames,
			List<List<String[]>> demParamMaxValues, NetworkStack stack) {
		// generate demands for the virtual networks
		// iterate over the virtual networks.
		for (int l = 0; l < demClasses.size(); l++) {
			// iterate over the data for each vn.
			for (int d = 0; d < demClasses.get(l).size(); d++) {
				if (isNodeClass(demClasses.get(l).get(d))) {
					// node demand
					for (VirtualNode vNode : ((VirtualNetwork) stack
							.getLayer(l + 1)).getVertices())
						addDemand(vNode, demClasses.get(l).get(d),
								demParamNames.get(l).get(d), demParamMaxValues
										.get(l).get(d), stack);
				} else {
					// link demand
					System.out.println(stack.getLayer(l + 1));
					Collection<VirtualLink> edges = ((VirtualNetwork) stack.getLayer(l + 1)).getEdges();
							if (edges != null) {
								for (VirtualLink vLink : edges)
									addDemand(vLink, demClasses.get(l).get(d),
											demParamNames.get(l).get(d), demParamMaxValues
											.get(l).get(d), stack);
							}
				}
			}
		}
	}

	private Class<?>[] getAllDemands() {
		Class<?>[] nodeDem = ClassScanner.getClassesImplementing(
				this.getClass(), "vnreal.demands", INodeConstraint.class);
		Class<?>[] linkDem = ClassScanner.getClassesImplementing(
				this.getClass(), "vnreal.demands", ILinkConstraint.class);

		Set<Class<?>> demSet = new TreeSet<Class<?>>(
				new Comparator<Class<?>>() {
					@Override
					public int compare(Class<?> o1, Class<?> o2) {
						// Sort by class name
						return o1.getSimpleName().compareTo(o2.getSimpleName());
					}
				});

		for (Class<?> dem : nodeDem)
			demSet.add(dem);
		for (Class<?> dem : linkDem)
			demSet.add(dem);

		Class<?>[] demands = new Class<?>[demSet.size()];

		int i = 0;
		for (Class<?> dem : demSet)
			demands[i++] = dem;

		return demands;
	}

	private Class<?>[] getAllResources() {
		Class<?>[] nodeRes = ClassScanner.getClassesImplementing(
				this.getClass(), "vnreal.resources", INodeConstraint.class);
		Class<?>[] linkRes = ClassScanner.getClassesImplementing(
				this.getClass(), "vnreal.resources", ILinkConstraint.class);

		Set<Class<?>> resSet = new TreeSet<Class<?>>(
				new Comparator<Class<?>>() {
					@Override
					public int compare(Class<?> o1, Class<?> o2) {
						// Sort by class name
						return o1.getSimpleName().compareTo(o2.getSimpleName());
					}
				});

		for (Class<?> res : nodeRes)
			resSet.add(res);
		for (Class<?> res : linkRes)
			resSet.add(res);

		Class<?>[] resources = new Class<?>[resSet.size()];

		int i = 0;
		for (Class<?> res : resSet)
			resources[i++] = res;

		return resources;
	}

	private static boolean isNodeClass(Class<?> cls) {
		try {
			for (Class<?> i : cls.getInterfaces()) {
				if (i.equals(Class
						.forName("vnreal.constraints.INodeConstraint"))) {
					return true;
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new AssertionError("Class not found.");
		}
		return false;
	}

	protected static Method getSetterIgnoreCase(Class<?> cls, String fieldName) {
		for (Method m : cls.getDeclaredMethods()) {
			if (m.isAnnotationPresent(ExchangeParameter.class)
					&& m.getName().equalsIgnoreCase("set" + fieldName)) {
				return m;
			}
		}
		return null;
	}

	private static Object getRandomParamValue(Class<?> type, String max) {
		if (type.equals(Integer.class)) {
			return new Integer((int) Math.round(Double.parseDouble(max)
					* stream.nextDouble()));
		} else if (type.equals(Double.class)) {
			// round to 3 decimals
			return Math.rint(Double.parseDouble(max) * stream.nextDouble()
					* 1000) / 1000;
		} else {
			System.err.println("ERROR: Invalid parameter type.");
			throw new AssertionError();
		}
	}

	private static void addResource(NetworkEntity<AbstractResource> ne,
			Class<?> resClass, String[] paramNames, String[] maxValues, NetworkStack stack) {
		try {
			// id resource is a special case
			if (resClass.getSimpleName().equals("IdResource")) {
				SubstrateNode node = (SubstrateNode) ne;
				IdResource res = new IdResource(node, stack.getSubstrate());
				res.setId(Long.toString(node.getId()));

				if (!ne.add(res)) {
					throw new AssertionError("Resource " + res.toString()
							+ "could not be added to substrate network entity "
							+ ne.toString());
				}
			} else {
				Object res = resClass.getConstructor(
						ne.getClass().getSuperclass()).newInstance(ne);

				for (int p = 0; p < paramNames.length; p++) {
					Method setter = getSetterIgnoreCase(resClass, paramNames[p]);
					Class<?>[] paramType = setter.getParameterTypes();
					setter.invoke(res,
							getRandomParamValue(paramType[0], maxValues[p]));
				}
				if (!ne.add((AbstractResource) res)) {
					throw new AssertionError("Resource " + res.toString()
							+ "could not be added to substrate network entity "
							+ ne.toString());
				}
			}

		} catch (Exception e) {
			System.err
					.println("An error occured while trying to add a constraint.");
			e.printStackTrace();
		}
	}

	private static void addDemand(NetworkEntity<AbstractDemand> ne,
			Class<?> demClass, String[] paramNames, String[] maxValues, NetworkStack stack) {
		try {
			if (demClass.getSimpleName().equals("IdDemand")) {
				VirtualNode node = (VirtualNode) ne;
				IdDemand dem = new IdDemand(node);
				dem.setDemandedId(Integer.toString(stream.nextInt(stack.getSubstrate()
						.getVertexCount() - 1)));

				if (!ne.add(dem)) {
					throw new AssertionError("Resource " + dem.toString()
							+ "could not be added to substrate network entity "
							+ ne.toString());
				}
			} else {
				Object dem = demClass.getConstructor(
						ne.getClass().getSuperclass()).newInstance(ne);
				for (int p = 0; p < paramNames.length; p++) {
					Method setter = getSetterIgnoreCase(demClass, paramNames[p]);
					Class<?>[] paramType = setter.getParameterTypes();
					setter.invoke(dem,
							getRandomParamValue(paramType[0], maxValues[p]));
				}
				if (!ne.add((AbstractDemand) dem)) {
					throw new AssertionError("Demand " + dem.toString()
							+ "could not be added to virtual network entity "
							+ ne.toString());
				}
			}

		} catch (Exception e) {
			System.err
					.println("An error occured while trying to add a constraint.");
			e.printStackTrace();
			System.exit(-1);
		}
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

	private class MyClasTableCellRenderer implements TableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			JLabel classNameLabel = new JLabel(
					((Class<?>) value).getSimpleName());
			table.setRowHeight(row, Math.max(table.getRowHeight(row),
					(int) classNameLabel.getPreferredSize().getHeight()));
			TableColumn cm = table.getColumnModel().getColumn(column);
			cm.setMinWidth(Math.max(cm.getMinWidth(), (int) classNameLabel
					.getMinimumSize().getWidth()));
			return classNameLabel;
		}
	}

	private class MyParamsTableCellRenderer implements TableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			JPanel panel = new JPanel();
			if (value instanceof String[]) {
				String[] valueArray = (String[]) value;
				if (valueArray.length == 0)
					return panel;
				GroupLayout layout = new GroupLayout(panel);
				panel.setLayout(layout);
				layout.setAutoCreateGaps(true);
				layout.setAutoCreateContainerGaps(true);
				SequentialGroup cols = layout.createSequentialGroup();
				layout.setHorizontalGroup(cols);
				ParallelGroup col1 = layout
						.createParallelGroup(GroupLayout.Alignment.LEADING);
				ParallelGroup col2 = layout
						.createParallelGroup(GroupLayout.Alignment.TRAILING);
				cols.addGroup(col1)
						.addPreferredGap(
								LayoutStyle.ComponentPlacement.RELATED,
								GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(col2);
				SequentialGroup rows = layout.createSequentialGroup();
				layout.setVerticalGroup(rows);
				for (int i = 0; i < valueArray.length; i++) {
					// the value array has null elements for
					// IdResource/Demand
					if (valueArray[i] != null) {
						JLabel label = new JLabel("max. "
								+ resParamNames.get(row)[i]);
						JTextField tf = new JTextField(3);
						tf.setText(valueArray[i]);
						col1.addComponent(label);
						col2.addComponent(tf, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE);
						rows.addGroup(layout
								.createParallelGroup(
										GroupLayout.Alignment.CENTER)
								.addComponent(label).addComponent(tf));
					}
				}
			}
			table.setRowHeight(row, Math.max(table.getRowHeight(row),
					(int) panel.getPreferredSize().getHeight()));
			TableColumn cm = table.getColumnModel().getColumn(column);
			cm.setMinWidth(Math.max(cm.getMinWidth(), (int) panel
					.getPreferredSize().getWidth()));
			return panel;
		}
	}

	private class MyParamsTableCellEditor extends AbstractCellEditor implements
			TableCellEditor {

		private JPanel panel;

		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			panel = new JPanel();
			if (value instanceof String[]) {
				String[] valueArray = (String[]) value;
				if (valueArray.length == 0)
					return panel;
				GroupLayout layout = new GroupLayout(panel);
				panel.setLayout(layout);
				layout.setAutoCreateGaps(true);
				layout.setAutoCreateContainerGaps(true);
				SequentialGroup cols = layout.createSequentialGroup();
				layout.setHorizontalGroup(cols);
				ParallelGroup col1 = layout
						.createParallelGroup(GroupLayout.Alignment.LEADING);
				ParallelGroup col2 = layout
						.createParallelGroup(GroupLayout.Alignment.TRAILING);
				cols.addGroup(col1)
						.addPreferredGap(
								LayoutStyle.ComponentPlacement.RELATED,
								GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(col2);
				SequentialGroup rows = layout.createSequentialGroup();
				layout.setVerticalGroup(rows);

				for (int i = 0; i < valueArray.length; i++) {
					// create the label. use the resource param name for both
					// resource and demand params as it should be the same.
					JLabel label = new JLabel("max. "
							+ resParamNames.get(row)[i]);
					JTextField tf = new JTextField(3);
					tf.setText(valueArray[i]);
					tf.setEditable(true);
					col1.addComponent(label);
					col2.addComponent(tf, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE,
							GroupLayout.PREFERRED_SIZE);
					rows.addGroup(layout
							.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(label).addComponent(tf));
				}
			}
			table.setRowHeight(row, Math.max(table.getRowHeight(row),
					(int) panel.getPreferredSize().getHeight()));
			TableColumn cm = table.getColumnModel().getColumn(column);
			cm.setMinWidth(Math.max(cm.getMinWidth(), (int) panel
					.getPreferredSize().getWidth()));
			return panel;
		}

		@Override
		public Object getCellEditorValue() {
			ArrayList<String> list = new ArrayList<String>();
			for (Component c : panel.getComponents())
				if (c instanceof JTextField)
					// getText is null if no value is entered in the text field
					// we do not want null values so add an empty string instead
					if (((JTextField) c).getText() != null)
						list.add(((JTextField) c).getText());
					else
						list.add(new String());

			return list.toArray(new String[list.size()]);
		}
	}
}
