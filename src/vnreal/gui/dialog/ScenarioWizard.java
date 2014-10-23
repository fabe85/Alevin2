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
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.LayoutStyle;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import mulavito.gui.dialogs.AbstractButtonDialog;
import vnreal.Scenario;
import mulavito.graph.generators.WaxmanGraphGenerator;
import vnreal.gui.GUI;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

@SuppressWarnings("serial")
public class ScenarioWizard extends AbstractButtonDialog {
	private JPanel snPanel;
	private JPanel vnPanel;
	private JSpinner snNodesSpinner;
	private JSpinner snAlphaSpinner;
	private JSpinner snBetaSpinner;
	private JSpinner vnNumberSpinner;
	private JTable vnDataTable;
	MyTableModel tableModel;
	private JSpinner defaultVNodesNumberSpinner;
	
	private final Scenario scenario;

	public ScenarioWizard(Scenario scenario) {
		super(GUI.getInstance(), "Scenario Wizard", "Create", new Dimension(
				400, 300));
		this.scenario = scenario;
		pack();
		setVisible(true);
	}

	@Override
	protected JPanel createContent() {
		JPanel content = new JPanel();

		// create a layout
		GroupLayout layout = new GroupLayout(content);
		content.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		SequentialGroup row = layout.createSequentialGroup();
		layout.setVerticalGroup(row);

		ParallelGroup col = layout.createParallelGroup();
		layout.setHorizontalGroup(col);

		// create the panels contained by the content panel

		// substrate network panel
		snPanel = new JPanel();
		snPanel.setBorder(BorderFactory.createTitledBorder("Substrate Network"));
		GroupLayout snLayout = new GroupLayout(snPanel);
		snPanel.setLayout(snLayout);
		snLayout.setAutoCreateGaps(true);
		snLayout.setAutoCreateContainerGaps(true);

		snNodesSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 100, 1));
		snNodesSpinner
				.setToolTipText("<html>the number of substrate nodes</html>");
		JLabel snNodesLabel = new JLabel("Substrate nodes");
		snAlphaSpinner = new JSpinner(
				new SpinnerNumberModel(1, 1E-10, 100, 0.1));
		snAlphaSpinner.setToolTipText("<html>alpha > 0<br>"
				+ "An increase in alpha will increase<br>"
				+ "the number of edges in the graph.</html>");
		JLabel snAlphaLabel = new JLabel("Alpha");
		snBetaSpinner = new JSpinner(new SpinnerNumberModel(0.5, -100, 1, 0.1));
		snBetaSpinner.setToolTipText("<html>beta &lt= 1<br>"
				+ "An increase in beta will increase<br>"
				+ "the ratio of long edges relative to short edges.</html>");
		JLabel snBetaLabel = new JLabel("Beta");

		SequentialGroup snHorizontal = snLayout.createSequentialGroup();
		snLayout.setHorizontalGroup(snHorizontal);
		snHorizontal.addGroup(snLayout.createParallelGroup()
				.addComponent(snNodesLabel).addComponent(snAlphaLabel)
				.addComponent(snBetaLabel));
		snHorizontal.addGroup(snLayout
				.createParallelGroup()
				.addComponent(snNodesSpinner, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(snAlphaSpinner, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(snBetaSpinner, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));

		SequentialGroup snVertical = snLayout.createSequentialGroup();
		snLayout.setVerticalGroup(snVertical);
		snVertical.addGroup(snLayout
				.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(snNodesLabel).addComponent(snNodesSpinner));
		snVertical.addGroup(snLayout
				.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(snAlphaLabel)
				.addComponent(snAlphaSpinner, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
		snVertical.addGroup(snLayout
				.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(snBetaLabel)
				.addComponent(snBetaSpinner, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));

		// virtual network panel
		vnPanel = new JPanel();
		vnPanel.setBorder(BorderFactory.createTitledBorder("Virtual Networks"));

		GroupLayout vnLayout = new GroupLayout(vnPanel);
		vnPanel.setLayout(vnLayout);
		vnLayout.setAutoCreateGaps(true);
		vnLayout.setAutoCreateContainerGaps(true);

		vnDataTable = new JTable();
		JScrollPane scrollPane = new JScrollPane(vnDataTable);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		vnDataTable.setFillsViewportHeight(true);
		vnDataTable.setPreferredScrollableViewportSize(new Dimension(250, 100));
		tableModel = new MyTableModel();
		vnDataTable.setModel(tableModel);
		tableModel.addColumn("#");
		tableModel.addColumn("# nodes");
		tableModel.addColumn("alpha");
		tableModel.addColumn("beta");

		DefaultTableCellRenderer nodesRenderer = new DefaultTableCellRenderer();
		nodesRenderer
				.setToolTipText("<html>the number of nodes for this virtual network</html>");
		TableColumn nodesColumn = vnDataTable.getColumnModel().getColumn(1);
		nodesColumn.setCellEditor(new MyTableCellEditor(
				MyTableCellEditor.TYPE_NUMBER_NODES));
		nodesColumn.setCellRenderer(nodesRenderer);

		DefaultTableCellRenderer alphaRenderer = new DefaultTableCellRenderer();
		alphaRenderer.setToolTipText("<html>alpha > 0<br>"
				+ "An increase in alpha will increase<br>"
				+ "the number of edges in the graph.</html>");
		TableColumn alphaColumn = vnDataTable.getColumnModel().getColumn(2);
		alphaColumn.setCellRenderer(alphaRenderer);
		alphaColumn.setCellEditor(new MyTableCellEditor(
				MyTableCellEditor.TYPE_ALPHA));

		DefaultTableCellRenderer betaRenderer = new DefaultTableCellRenderer();
		betaRenderer.setToolTipText("<html>beta &lt= 1<br>"
				+ "An increase in beta will increase<br>"
				+ "the ratio of long edges relative to short edges.</html>");
		TableColumn betaColumn = vnDataTable.getColumnModel().getColumn(3);
		betaColumn.setCellRenderer(betaRenderer);
		betaColumn.setCellEditor(new MyTableCellEditor(
				MyTableCellEditor.TYPE_BETA));

		tableModel.addRow(new Object[] { new Integer(1), new Integer(1),
				new Double(1), new Double(0.5) });
		vnNumberSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 100, 1));
		JLabel vnNumberLabel = new JLabel("number");
		defaultVNodesNumberSpinner = new JSpinner(new SpinnerNumberModel(1, 0,
				100, 1));
		defaultVNodesNumberSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				for (int row = 0; row < tableModel.getRowCount(); row++)
					tableModel.setValueAt(
							defaultVNodesNumberSpinner.getValue(), row, 1);
			}
		});
		JLabel defaultVNodesNumberLabel = new JLabel(
				"default number of virtual nodes");
		vnNumberSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				int rows = tableModel.getRowCount();
				int vns = (Integer) vnNumberSpinner.getValue();
				if (vns == 0)
					vnDataTable.setVisible(false);
				else
					vnDataTable.setVisible(true);
				if (rows < vns)
					for (int i = rows + 1; i <= vns; i++)
						tableModel.addRow(new Object[] {
								new Integer(i),
								(Integer) defaultVNodesNumberSpinner.getValue(),
								new Double(1), new Double(0.5) });
				else
					for (int i = rows - 1; i >= vns; i--)
						tableModel.removeRow(i);
			}
		});

		ParallelGroup vnHorizontal = vnLayout.createParallelGroup();
		vnLayout.setHorizontalGroup(vnHorizontal);
		vnHorizontal.addGroup(vnLayout
				.createSequentialGroup()
				.addComponent(vnNumberLabel)
				.addComponent(vnNumberSpinner, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				.addComponent(defaultVNodesNumberLabel)
				.addComponent(defaultVNodesNumberSpinner,
						GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
						GroupLayout.PREFERRED_SIZE));
		vnHorizontal.addComponent(scrollPane);

		SequentialGroup vnVertical = vnLayout.createSequentialGroup();
		vnLayout.setVerticalGroup(vnVertical);
		vnVertical.addGroup(vnLayout
				.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(vnNumberLabel).addComponent(vnNumberSpinner)
				.addComponent(defaultVNodesNumberLabel)
				.addComponent(defaultVNodesNumberSpinner));
		vnVertical.addComponent(scrollPane);

		// add the components to the content panel
		col.addComponent(snPanel).addComponent(vnPanel);
		row.addComponent(snPanel).addComponent(vnPanel);

		return content;
	}

	@Override
	protected void doAction() {
		// get the value of the cell that is being edited
		if (vnDataTable.getCellEditor() != null)
			vnDataTable.getCellEditor().stopCellEditing();

		if (scenario.getNetworkStack() != null)
			if (JOptionPane
					.showConfirmDialog(
							this,
							"By generating a new scenario you will lose any unsaved changes.\nContinue?",
							"Scenario Wizard", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
				return;
		// substrate network params
		int substrateNodes = (Integer) snNodesSpinner.getValue();
		double substrateAlpha = (Double) snAlphaSpinner.getValue();
		double substrateBeta = (Double) snBetaSpinner.getValue();

		// create arrays for virtual networks generation
		int vnNumber = (Integer) vnNumberSpinner.getValue();
		int[] vnsNodes = new int[vnNumber];
		double[] vnsAlpha = new double[vnNumber];
		double[] vnsBeta = new double[vnNumber];

		for (int i = 0; i < vnNumber; i++) {
			vnsNodes[i] = (Integer) tableModel.getValueAt(i, 1);
			vnsAlpha[i] = (Double) tableModel.getValueAt(i, 2);
			vnsBeta[i] = (Double) tableModel.getValueAt(i, 3);
		}

		// finally generate the scenario
		scenario.setNetworkStack(generateTopology(substrateNodes, substrateAlpha, substrateBeta,
				vnNumber, vnsNodes, vnsAlpha, vnsBeta));

		GUI.getInstance().getGraphPanel().autoZoomToFit();
	}

	/**
	 * Generates a new scenario using the {@link WaxmanGraphGenerator} with the
	 * given parameters. WARNING: This method disposes of any open scenario and
	 * overrides it!
	 * 
	 * @param snNodes
	 *            Number of substrate network nodes.
	 * @param snAlpha
	 *            The value of alpha for the substrate network.
	 * @param snBeta
	 *            The value of beta for the substrate network.
	 * @param vnNumber
	 *            The number of virtual networks.
	 * @param vnsNodes
	 *            An array containing the number of nodes for each virtual
	 *            network. vnsNodes[i] is the number of nodes for layer i + 1.
	 * @param vnsAlpha
	 *            An array containing the alpha parameter for each virtual
	 *            network. vnsAlpha[i] is the alpha value for layer i + 1.
	 * @param vnsBeta
	 *            An array containing the beta parameter for each virtual
	 *            network. vnsBeta[i] is the beta value for layer i + 1.
	 */
	public static NetworkStack generateTopology(int snNodes, double snAlpha,
			double snBeta, int vnNumber, int[] vnsNodes, double[] vnsAlpha,
			double[] vnsBeta) {
		
		NetworkStack result =
				new NetworkStack(new SubstrateNetwork(false),
						new LinkedList<VirtualNetwork>());
		SubstrateNetwork substrate = result.getSubstrate();

		// generate substrate network
		while (snNodes-- > 0)
			substrate.addVertex(new SubstrateNode());

		WaxmanGraphGenerator<SubstrateNode, SubstrateLink> sgg = new WaxmanGraphGenerator<SubstrateNode, SubstrateLink>(
				snAlpha, snBeta, false);
		sgg.generate(substrate);

		HashMap<SubstrateNode, Point2D> spos = sgg.getPositions();
		for (SubstrateNode v : substrate.getVertices()) {
			v.setCoordinateX(100.0 * spos.get(v).getX());
			v.setCoordinateY(100.0 * spos.get(v).getY());
		}

		// generate virtual networks
		VirtualNetwork vn;
		int virtualNodes;
		double virtualAlpha;
		double virtualBeta;
		for (int layer = 1; layer <= vnNumber; layer++) {
			vn = new VirtualNetwork(layer);
			result.addLayer(vn);
			virtualNodes = vnsNodes[layer - 1];
			virtualAlpha = vnsAlpha[layer - 1];
			virtualBeta = vnsBeta[layer - 1];
			while (virtualNodes-- > 0)
				vn.addVertex(new VirtualNode(layer));

			WaxmanGraphGenerator<VirtualNode, VirtualLink> vgg = new WaxmanGraphGenerator<VirtualNode, VirtualLink>(
					virtualAlpha, virtualBeta, false);
			vgg.generate(vn);

			HashMap<VirtualNode, Point2D> vpos = vgg.getPositions();
			for (VirtualNode v : vn.getVertices()) {
				v.setCoordinateX(100.0 * vpos.get(v).getX());
				v.setCoordinateY(100.0 * vpos.get(v).getY());
			}
		}
		
		return result;
	}

	private class MyTableModel extends DefaultTableModel {

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return getValueAt(0, columnIndex).getClass();
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			if (column == 0)
				return false;

			return true;
		}
	}

	private class MyTableCellEditor extends AbstractCellEditor implements
			TableCellEditor {
		public static final int TYPE_NUMBER_NODES = 1;
		public static final int TYPE_ALPHA = 2;
		public static final int TYPE_BETA = 3;

		private JSpinner spinner;

		public MyTableCellEditor(int type) {
			super();
			switch (type) {
			case TYPE_NUMBER_NODES:
				spinner = new JSpinner(new SpinnerNumberModel(1, 0, 100, 1));
				break;
			case TYPE_ALPHA:
				spinner = new JSpinner(new SpinnerNumberModel(1, 1E-10, 100,
						0.1));
				break;
			case TYPE_BETA:
				spinner = new JSpinner(
						new SpinnerNumberModel(0.5, -100, 1, 0.1));
				break;
			default:
				spinner = new JSpinner();
			}
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

}
