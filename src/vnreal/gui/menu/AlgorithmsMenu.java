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

import mulavito.algorithms.IAlgorithm;
import vnreal.Scenario;
import vnreal.algorithms.basicVN.BasicVNAssignmentAlgorithm;
import vnreal.algorithms.basicVN.BasicVNLinkStressAssignmentAlgorithm;
import vnreal.algorithms.isomorphism.AdvancedSubgraphIsomorphismAlgorithm;
import vnreal.algorithms.isomorphism.SubgraphIsomorphismAlgorithm;
import vnreal.algorithms.isomorphism.SubgraphIsomorphismStackAlgorithm;
import vnreal.algorithms.energy.optimal.OptimalMappingsAlgorithm;
import vnreal.algorithms.samples.SimpleDijkstraAlgorithm;
import vnreal.gui.GUI;
import vnreal.gui.dialog.AvailableResourcesPathSplittingWizard;
import vnreal.gui.dialog.AvailableResourcesWizard;
import vnreal.gui.dialog.BFSNRPSWizard;
import vnreal.gui.dialog.BFSNRWizard;
import vnreal.gui.dialog.CoordinatedMappingPathSplittingWizard;
import vnreal.gui.dialog.CoordinatedMappingRoundingPathStrippingWizard;
import vnreal.gui.dialog.CoordinatedMappingkShortestPathWizard;
//import vnreal.gui.dialog.EnergyWizardHiddenHop;
//import vnreal.gui.dialog.ExactMipHhWizard;
import vnreal.gui.dialog.MyProgressBarDialog;
import vnreal.gui.dialog.NRPSWizard;
import vnreal.gui.dialog.NRkSPWizard;
import vnreal.network.NetworkStack;

/**
 * @author Vlad Singeorzan
 * @author Lisset Diaz
 * @author Michael Duelli
 * @since 2010-10-19
 */
@SuppressWarnings("serial")
public final class AlgorithmsMenu extends JMenu implements ActionListener {
    
    private final Scenario scenario;

	public AlgorithmsMenu(Scenario scenario) {
		super("Algorithms");
		this.scenario = scenario;
		
		setMnemonic(KeyEvent.VK_A);

		JMenuItem mi;

		mi = new JMenuItem("Simple Dijkstra");
		mi.setActionCommand("simple dijkstra");
		mi.addActionListener(this);
		add(mi);

		mi = new JMenuItem("Greedy Available and K Shortest Paths");
		mi.setActionCommand("greedy Available");
		mi.addActionListener(this);
		add(mi);

		mi = new JMenuItem("Available Resources and Path Splitting");
		mi.setActionCommand("path splitt");
		mi.addActionListener(this);
		add(mi);

		mi = new JMenuItem(
				"Coordinated Node and Link Mapping with Path Spliting");
		mi.setActionCommand("coordinatedPathSpplitting");
		mi.addActionListener(this);
		add(mi);

		mi = new JMenuItem(
				"Coordinated Node and Link Mapping with k Shortest Paths");
		mi.setActionCommand("coordinatedKPaths");
		mi.addActionListener(this);
		add(mi);

		mi = new JMenuItem(
				"Coordinated Node Mapping with Rounding PathStripping Link Mapping");
		mi.setActionCommand("coordinatedRoundStripping");
		mi.addActionListener(this);
		add(mi);

//		mi = new JMenuItem("Exact MIP VNE solution with Hidden Hops");
//		mi.setActionCommand("ExactMIPHiddenHop");
//		mi.addActionListener(this);
//		add(mi);

		mi = new JMenuItem("Node Ranking with Shortest Paths");
		mi.setActionCommand("NRkSP");
		mi.addActionListener(this);
		add(mi);

		mi = new JMenuItem("Node Ranking with Path Splitting");
		mi.setActionCommand("NRPS");
		mi.addActionListener(this);
		add(mi);

		mi = new JMenuItem("BFS Node Ranking Coordinated VNE");
		mi.setActionCommand("BFSNR");
		mi.addActionListener(this);
		add(mi);

		mi = new JMenuItem(
				"BFS Node Ranking Coordinated VNE with PathSplitting");
		mi.setActionCommand("BFSNRPS");
		mi.addActionListener(this);
		add(mi);
		
//		mi = new JMenuItem("Optimal Energy Mapping Algorithm with Hidden Hops");
//		mi.setActionCommand("EnergyAwareHiddenHop");
//		mi.addActionListener(this);
//		add(mi);

		addSeparator();

		mi = new JMenuItem("Basic VN");
		mi.setActionCommand("basicVN");
		mi.addActionListener(this);
		add(mi);

		mi = new JMenuItem("Basic VN Link Stress");
		mi.setActionCommand("basicVNLinkStress");
		mi.addActionListener(this);
		add(mi);

		mi = new JMenuItem("Subgraph Isomorphism");
		mi.setActionCommand("subgraph isomorphism");
		mi.addActionListener(this);
		add(mi);

		mi = new JMenuItem("Advanced Subgraph Isomorphism");
		mi.setActionCommand("advanced subgraph isomorphism");
		mi.addActionListener(this);
		add(mi);

		mi = new JMenuItem("Optimal Mappings Algorithm");
		mi.setActionCommand("optimal");
		mi.addActionListener(this);
		add(mi);

		addSeparator();

		mi = new JMenuItem("Remove all mappings");
		mi.setActionCommand("remove mappings");
		mi.addActionListener(this);
		add(mi);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		NetworkStack ns = scenario.getNetworkStack();
		String input;
		String cmd = e.getActionCommand();

		if (ns == null) {
			System.err.println("No network stack, no algorithm!");
			return;
		}

		if (cmd.equals("remove mappings")) {
			// remove all mappings
			ns.clearMappings();
			GUI.getInstance().update();
		} else {
			if (ns.hasMappings()) {
				JOptionPane
						.showMessageDialog(
								GUI.getInstance(),
								"This scenario already has mappings. An algorithm cannot be run.",
								"Problem Running Algorithm",
								JOptionPane.INFORMATION_MESSAGE);
			} else if (cmd.equals("simple dijkstra")) {
				IAlgorithm dijkstra = new SimpleDijkstraAlgorithm(ns);
				new MyProgressBarDialog(dijkstra, scenario);
			} else if (cmd.equals("greedy Available")) {
				new AvailableResourcesWizard(scenario);
			} else if (cmd.equals("path splitt")) {
				new AvailableResourcesPathSplittingWizard(scenario);
			} else if (cmd.equals("coordinatedPathSpplitting")) {
				new CoordinatedMappingPathSplittingWizard(scenario);
			} else if (cmd.equals("coordinatedKPaths")) {
				new CoordinatedMappingkShortestPathWizard(scenario);
			} else if (cmd.equals("coordinatedRoundStripping")) {
				new CoordinatedMappingRoundingPathStrippingWizard(scenario);
//			} else if (cmd.equals("ExactMIPHiddenHop")) {
//				new ExactMipHhWizard(scenario);
			} else if (cmd.equals("NRkSP")) {
				new NRkSPWizard(scenario);
			} else if (cmd.equals("NRPS")) {
				new NRPSWizard(scenario);
			} else if (cmd.equals("BFSNR")) {
				new BFSNRWizard(scenario);
			} else if (cmd.equals("BFSNRPS")) {
				new BFSNRPSWizard(scenario);
//			}  else if (cmd.equals("EnergyAwareHiddenHop")) {
//				new EnergyWizardHiddenHop(scenario);
			} else if (cmd.equals("basicVN")) {
				BasicVNAssignmentAlgorithm basicVN = new BasicVNAssignmentAlgorithm(
						ns, 1.0, 1.0, false);
				basicVN.performEvaluation();
				// new MyProgressBarDialog(...);
			} else if (cmd.equals("basicVNLinkStress")) {
				BasicVNLinkStressAssignmentAlgorithm basicVNLS = new BasicVNLinkStressAssignmentAlgorithm(
						ns, 1.0, 1.0, false);
				basicVNLS.performEvaluation();
				// new MyProgressBarDialog(...);
			} else if (cmd.equals("subgraph isomorphism")) {
				SubgraphIsomorphismStackAlgorithm subgraph = new SubgraphIsomorphismStackAlgorithm(
						ns, new SubgraphIsomorphismAlgorithm(false));
				subgraph.performEvaluation();
				// new MyProgressBarDialog(subgraph);
			} else if (cmd.equals("advanced subgraph isomorphism")) {
			    SubgraphIsomorphismStackAlgorithm aSubgraph = new SubgraphIsomorphismStackAlgorithm(
						ns, new AdvancedSubgraphIsomorphismAlgorithm(false));
				aSubgraph.performEvaluation();
				// new MyProgressBarDialog(...);
			} else if (cmd.equals("optimal")) {
				input = JOptionPane.showInputDialog(GUI.getInstance(),
						"epsilon");
				int epsilon = Integer.parseInt(input);
				OptimalMappingsAlgorithm optimal = new OptimalMappingsAlgorithm(
						ns, true, epsilon, false);
				optimal.performEvaluation();
				// new MyProgressBarDialog(...);
			} else
				throw new AssertionError("Undefined menu action");
		}
	}
}
