/* ***** BEGIN LICENSE BLOCK *****
 * Copyright (C) 2008-2011, The 100GET-E3-R3G Project Team.
 * 
 * This work has been funded by the Federal Ministry of Education
 * and Research of the Federal Republic of Germany
 * (BMBF Förderkennzeichen 01BP0775). It is part of the EUREKA project
 * "100 Gbit/s Carrier-Grade Ethernet Transport Technologies
 * (CELTIC CP4-001)". The authors alone are responsible for this work.
 *
 * See the file AUTHORS for details and contact information.
 * 
 * This file is part of MuLaViTo (Multi-Layer Visualization Tool).
 *
 * MuLaViTo is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License Version 3 or later
 * (the "GPL"), or the GNU Lesser General Public License Version 3 or later
 * (the "LGPL") as published by the Free Software Foundation.
 *
 * MuLaViTo is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * or the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and
 * GNU Lesser General Public License along with MuLaViTo; see the file
 * COPYING. If not, see <http://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ***** */
package mulavito.samples;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import mulavito.algorithms.shortestpath.disjoint.SuurballeTarjan;
import mulavito.algorithms.shortestpath.ksp.Eppstein;
import mulavito.algorithms.shortestpath.ksp.Yen;
import mulavito.gui.Gui;
import mulavito.samples.utils.MyE;
import mulavito.samples.utils.MyL;
import mulavito.samples.utils.SampleGraphDocument;
import mulavito.samples.utils.SampleGraphDocument.MyV;
import mulavito.samples.utils.SampleGraphPanel;
import mulavito.utils.Resources;
import mulavito.utils.distributions.UniformStream;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * This demo visualizes the k-shortest path algorithm and disjoint path
 * algorithms.
 * 
 * @author Michael Duelli
 * @since 2011-04-29
 */
@SuppressWarnings("serial")
public final class GraphAlgorithmDemo extends Gui implements ActionListener {
	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				GraphAlgorithmDemo main = new GraphAlgorithmDemo();
				main.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						System.exit(0);
					}
				});
			}
		});
	}

	private SampleGraphPanel graphpanel;

	public GraphAlgorithmDemo() {
		super("MuLaViTo Algorithms Demo");

		// Set the frame icon.
		ImageIcon icon = Resources.getIconByName("/img/mulavito-logo.png");
		if (icon != null)
			setIconImage(icon.getImage());

		// Create toolbar.
		JToolBar toolbar = new JToolBar();
		getToolBarPane().add(toolbar);

		JButton btn;

		btn = new JButton("New Graph");
		btn.setActionCommand("graph");
		btn.addActionListener(this);
		toolbar.add(btn);

		btn = new JButton("Auto Size");
		btn.setActionCommand("autosize");
		btn.addActionListener(this);
		toolbar.add(btn);

		btn = new JButton("Eppstein");
		btn.setActionCommand("eppstein");
		btn.addActionListener(this);
		toolbar.add(btn);

		btn = new JButton("Yen");
		btn.setActionCommand("yen");
		btn.addActionListener(this);
		toolbar.add(btn);

		btn = new JButton("Suurballe-Tarjan");
		btn.setActionCommand("suurballe-tarjan");
		btn.addActionListener(this);
		toolbar.add(btn);

		btn = new JButton("About");
		btn.setActionCommand("about");
		btn.addActionListener(this);
		toolbar.add(btn);

		normalOutput("Welcome to MuLaViTo demonstrator.\n");
		debugOutput("Click on \"New Graph\" to create a new graph.\n");
		warnOutput("Click Eppstein, Yen, or Suurballe-Tarjan to randomly "
				+ "select source and destination and run the algorithm\n");
		notifyOutput("Have fun!!!\n");

		setPreferredSize(new Dimension(500, 600));
		setMinimumSize(new Dimension(500, 600));
		setVisible(true);
	}

	@Override
	protected JComponent createCenterPane() {
		// graphpanel filling
		graphpanel = new SampleGraphPanel();
		return graphpanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		if (cmd.equals("about")) {
			String html = "<html><h1>" + getTitle() + "</h1>";
			html += "WWW: http://mulavito.sf.net";
			html += "<h3>Demo Authors</h3>";
			html += "in alphabetical order:";
			html += "<ul>";
			html += "<li>Michael Duelli";
			html += "</ul>";
			html += "</html>";
			JOptionPane.showMessageDialog(this, html);
		} else if (cmd.equals("graph")) {
			graphpanel.setLayerStack(SampleGraphDocument.createConnectedDemo(1)
					.getMlg());
		} else if (cmd.equals("autosize")) {
			graphpanel.autoZoomToFit();
		} else {
			MyL layer = graphpanel.getLayerStack().iterator().next();
			PickedState<MyV> pickedVertices = graphpanel.getViewer(layer)
					.getPickedVertexState();
			PickedState<MyE> pickedEdges = graphpanel.getViewer(layer)
					.getPickedEdgeState();

			List<MyV> vertices = new ArrayList<MyV>(layer.getVertices());
			UniformStream rnd = new UniformStream();

			int srcId = rnd.nextInt(vertices.size() - 1);
			int dstId;
			do {
				dstId = rnd.nextInt(vertices.size() - 1);
			} while (dstId == srcId);

			MyV source = vertices.get(srcId);
			MyV target = vertices.get(dstId);

			Transformer<MyE, Number> nev = new Transformer<MyE, Number>() {
				@Override
				public Number transform(MyE input) {
					return 1.0;
				}
			};

			List<List<MyE>> paths = null;
			if (cmd.equals("eppstein")) {
				Eppstein<MyV, MyE> algo = new Eppstein<MyV, MyE>(layer, nev);
				int k = 3;
				paths = algo.getShortestPaths(source, target, k);
			} else if (cmd.equals("yen")) {
				Yen<MyV, MyE> algo = new Yen<MyV, MyE>(layer, nev);
				int k = 3;
				paths = algo.getShortestPaths(source, target, k);
			} else if (cmd.equals("suurballe-tarjan")) {
				SuurballeTarjan<MyV, MyE> algo = new SuurballeTarjan<MyV, MyE>(
						layer, nev);
				paths = algo.getDisjointPaths(source, target);
			}

			pickedVertices.clear();
			pickedEdges.clear();

			if (paths != null) {
				pickedVertices.pick(source, true);
				pickedVertices.pick(target, true);
				for (List<MyE> p : paths)
					for (MyE edge : p)
						pickedEdges.pick(edge, true);

				normalOutput("algo=" + cmd + ", src=" + source + ", dst="
						+ target + ":" + paths + "\n");
			}

			graphpanel.updateUI();
		}
	}
}
