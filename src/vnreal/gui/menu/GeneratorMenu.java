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
package vnreal.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import vnreal.Scenario;
import vnreal.gui.GUI;
import vnreal.gui.dialog.ConstraintsGeneratorDialog;
import vnreal.gui.dialog.MultiAlgoScenarioWizard;
import vnreal.gui.dialog.ScenarioWizard;

/**
 * @author Michael Duelli
 * @since 2010-12-08
 */
@SuppressWarnings("serial")
public final class GeneratorMenu extends JMenu implements ActionListener {

	private static final String ACTN_GENERATE_CONSTR =
			"generate constraints";
	private static final String ACTN_SCENARIO_WIZARD_MULTI =
			"scenario wizard multiple algorithms";
	private static final String ACTN_SCENARIO_WIZARD =
			"scenario wizard";
	private static final String ACTN_CLEAR =
			"clear";

	private final Scenario scenario;

	public GeneratorMenu(Scenario scenario) {
		super("Generators");
		this.scenario = scenario;
		setMnemonic(KeyEvent.VK_G);

		JMenuItem mi;

		mi = new JMenuItem("Scenario Wizard");
		mi.setActionCommand(ACTN_SCENARIO_WIZARD);
		mi.addActionListener(this);
		add(mi);

		//addSeparator();

		mi = new JMenuItem("Scenario Wizard (multiple algorithms)");
		mi.setActionCommand(ACTN_SCENARIO_WIZARD_MULTI);
		mi.addActionListener(this);
		add(mi);

		addSeparator();

		mi = new JMenuItem("Generate Constraints");
		mi.setActionCommand(ACTN_GENERATE_CONSTR);
		mi.addActionListener(this);
		add(mi);

		addSeparator();

		mi = new JMenuItem("Clear all constraints");
		mi.setActionCommand(ACTN_CLEAR);
		mi.addActionListener(this);
		add(mi);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		if (cmd.equals(ACTN_CLEAR)) {
			scenario.getNetworkStack().clearConstraints();
			GUI.getInstance().notifyOutput("Cleared all constraints.\n");

		} else if (cmd.equals(ACTN_SCENARIO_WIZARD)) {
			new ScenarioWizard(scenario);

		} else if (cmd.equals(ACTN_SCENARIO_WIZARD_MULTI)) {
			new MultiAlgoScenarioWizard(scenario);

		} else if (cmd.equals(ACTN_GENERATE_CONSTR)) {
			// only with an open scenario
			if (scenario.getNetworkStack() == null) {
				JOptionPane
						.showMessageDialog(
								GUI.getInstance(),
								"Error: A Scenario must be open to generate Constraints.",
								"Generate Constraints",
								JOptionPane.ERROR_MESSAGE);
			} else {
				new ConstraintsGeneratorDialog(scenario);
			}
		}
	}
}
