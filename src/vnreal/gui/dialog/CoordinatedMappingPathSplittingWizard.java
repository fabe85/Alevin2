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

import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import mulavito.algorithms.IAlgorithm;
import mulavito.gui.dialogs.AbstractButtonDialog;
import vnreal.Scenario;
import vnreal.algorithms.CoordinatedMappingPathSplitting;
import vnreal.gui.GUI;
import vnreal.network.NetworkStack;

@SuppressWarnings("serial")
public class CoordinatedMappingPathSplittingWizard extends AbstractButtonDialog {
	
	private final Scenario scenario;
	
	public CoordinatedMappingPathSplittingWizard(Scenario scenario) {
		super(GUI.getInstance(), "Setting Parameters Wizard", "Submit",
				new Dimension(400, 300));
		this.scenario = scenario;
		pack();
		setVisible(true);
	}

	private JPanel parPanel;
	private JSpinner maxDistSpinner;
	private JSpinner cpuWSpinner;
	private JSpinner bwWSpinner;

	private JRadioButton deterministicButton;
	private JRadioButton randomizedButton;
	static String deterministicString = "Deterministic";
	static String ramdomizedString = "Ramdomized";

	private JRadioButton yesNodeOverloadButton;
	private JRadioButton noNodeOverloadButton;
	static String yesNodeOverloadString = "Yes";
	static String noNodeOverloadString = "No";

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
		parPanel = new JPanel();
		parPanel.setBorder(BorderFactory.createTitledBorder("Parameters"));
		GroupLayout snLayout = new GroupLayout(parPanel);
		parPanel.setLayout(snLayout);
		snLayout.setAutoCreateGaps(true);
		snLayout.setAutoCreateContainerGaps(true);

		maxDistSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 100, 1));
		maxDistSpinner
				.setToolTipText("<html>Maximum distance to consider any Virtual node as a candidate</html>");
		JLabel maxDistLabel = new JLabel("Maximum distance");

		cpuWSpinner = new JSpinner(new SpinnerNumberModel(1, 1E-10, 100, 0.1));
		cpuWSpinner.setToolTipText("<html>CPU weight</html>");
		JLabel cpuWLabel = new JLabel("CPU weight");

		bwWSpinner = new JSpinner(new SpinnerNumberModel(1, 1E-10, 100, 0.1));
		bwWSpinner.setToolTipText("<html>Bandwith weight</html>");
		JLabel bwWLabel = new JLabel("Bandwith weight");

		deterministicButton = new JRadioButton(deterministicString, false);
		deterministicButton.setMnemonic(KeyEvent.VK_D);
		deterministicButton.setActionCommand(deterministicString);
		JLabel typeLabel = new JLabel("Type of algorithm");

		randomizedButton = new JRadioButton(ramdomizedString, false);
		randomizedButton.setMnemonic(KeyEvent.VK_R);
		randomizedButton.setActionCommand(ramdomizedString);

		// Group the radio buttons.
		ButtonGroup typegroup = new ButtonGroup();
		typegroup.add(deterministicButton);
		typegroup.add(randomizedButton);

		yesNodeOverloadButton = new JRadioButton(yesNodeOverloadString);
		yesNodeOverloadButton.setMnemonic(KeyEvent.VK_D);
		JLabel nodeOverloadLabel = new JLabel("Is node overload considered?");

		noNodeOverloadButton = new JRadioButton(noNodeOverloadString);
		noNodeOverloadButton.setMnemonic(KeyEvent.VK_R);

		// Group the radio buttons.
		ButtonGroup nodeOverload = new ButtonGroup();
		nodeOverload.add(yesNodeOverloadButton);
		nodeOverload.add(noNodeOverloadButton);

		SequentialGroup snHorizontal = snLayout.createSequentialGroup();
		snLayout.setHorizontalGroup(snHorizontal);
		snHorizontal.addGroup(snLayout.createParallelGroup()
				.addComponent(maxDistLabel).addComponent(cpuWLabel)
				.addComponent(bwWLabel).addComponent(typeLabel)
				.addComponent(nodeOverloadLabel));
		snHorizontal.addGroup(snLayout
				.createParallelGroup()
				.addComponent(maxDistSpinner, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(cpuWSpinner, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(bwWSpinner, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(deterministicButton, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(randomizedButton, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(yesNodeOverloadButton,
						GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
						GroupLayout.PREFERRED_SIZE)
				.addComponent(noNodeOverloadButton, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));

		SequentialGroup snVertical = snLayout.createSequentialGroup();
		snLayout.setVerticalGroup(snVertical);

		snVertical.addGroup(snLayout
				.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(maxDistLabel).addComponent(maxDistSpinner));

		snVertical.addGroup(snLayout
				.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(cpuWLabel)
				.addComponent(cpuWSpinner, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));

		snVertical.addGroup(snLayout
				.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(bwWLabel)
				.addComponent(bwWSpinner, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));

		snVertical.addGroup(snLayout
				.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(typeLabel)
				.addComponent(deterministicButton, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));

		snVertical.addGroup(snLayout
				.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(typeLabel)
				.addComponent(randomizedButton, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));

		snVertical.addGroup(snLayout
				.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(nodeOverloadLabel)
				.addComponent(yesNodeOverloadButton,
						GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
						GroupLayout.PREFERRED_SIZE));

		snVertical.addGroup(snLayout
				.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(nodeOverloadLabel)
				.addComponent(noNodeOverloadButton, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));

		// add the components to the content panel
		col.addComponent(parPanel);
		row.addComponent(parPanel);

		return content;
	}

	@Override
	protected void doAction() {
		// Algorithm parameters
		NetworkStack ns = scenario.getNetworkStack();
		int distance = (Integer) maxDistSpinner.getValue();
		double wCpu = (Double) cpuWSpinner.getValue();
		double wBw = (Double) bwWSpinner.getValue();

		// finally generate the algorithm
		IAlgorithm coordinatedMappingPathSplitting = null;

		if (!deterministicButton.isSelected() && !randomizedButton.isSelected()) {
			JOptionPane
					.showMessageDialog(
							GUI.getInstance(),
							"<html><p><b>'Type'</b> parameter must be Deterministic or Randomized</p><html>");
			return;
		} else if (!yesNodeOverloadButton.isSelected()
				&& !noNodeOverloadButton.isSelected()) {
			JOptionPane
					.showMessageDialog(GUI.getInstance(),
							"<html><p><b>'Node Overload'</b> parameter must be Yes or No</p><html>");
			return;
		} else if (randomizedButton.isSelected()
				&& yesNodeOverloadButton.isSelected()) {
			coordinatedMappingPathSplitting = new CoordinatedMappingPathSplitting(
					ns, distance, wCpu, wBw, 1, true);
		} else if (randomizedButton.isSelected()
				&& noNodeOverloadButton.isSelected()) {
			coordinatedMappingPathSplitting = new CoordinatedMappingPathSplitting(
					ns, distance, wCpu, wBw, 1, false);
		} else if (deterministicButton.isSelected()
				&& yesNodeOverloadButton.isSelected()) {
			coordinatedMappingPathSplitting = new CoordinatedMappingPathSplitting(
					ns, distance, wCpu, wBw, 0, true);
		} else if (deterministicButton.isSelected()
				&& noNodeOverloadButton.isSelected()) {
			coordinatedMappingPathSplitting = new CoordinatedMappingPathSplitting(
					ns, distance, wCpu, wBw, 0, false);
		}
		new MyProgressBarDialog(coordinatedMappingPathSplitting, scenario);

		GUI.getInstance().getGraphPanel().autoZoomToFit();
	}
}
