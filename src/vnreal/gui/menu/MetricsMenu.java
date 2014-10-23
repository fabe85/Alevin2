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
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import mulavito.utils.ClassScanner;
import vnreal.Scenario;
import vnreal.evaluations.metrics.EvaluationMetric;
import vnreal.gui.dialog.MetricsDialog;

/**
 * @author Michael Duelli
 * @author Vlad Singeorzan
 * @since 2011-04-07
 */
@SuppressWarnings("serial")
public final class MetricsMenu extends JMenu implements ActionListener {
	Map<String, Class<?>> metricClasses;
	private final Scenario scenario;

	public MetricsMenu(Scenario scenario) {
		super("Metrics");
		this.scenario = scenario;
		setMnemonic(KeyEvent.VK_M);
		metricClasses = new TreeMap<String, Class<?>>();

		try {
			for (Class<?> c : ClassScanner.getDerivates(this.getClass(),
					"vnreal.evaluations.metrics", EvaluationMetric.class))
				metricClasses.put(c.getSimpleName(), c);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		JMenuItem mi;

		for (String key : metricClasses.keySet()) {
			mi = new JMenuItem(key);
			mi.setActionCommand(key);
			mi.addActionListener(this);
			add(mi);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (scenario.getNetworkStack() != null)
			if (scenario.getNetworkStack().hasMappings())
				try {
					new MetricsDialog((EvaluationMetric) metricClasses.get(cmd)
							.newInstance(), scenario);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			else
				System.err.println("Scenario does not have mappings!");
		else
			System.err.println("No scenario open!");
	}
}
