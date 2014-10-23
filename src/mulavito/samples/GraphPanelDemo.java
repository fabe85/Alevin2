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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import mulavito.gui.Gui;
import mulavito.samples.utils.SampleGraphDocument;
import mulavito.samples.utils.SampleGraphPanel;
import mulavito.utils.Resources;

/**
 * This demo shows a JFrame hosting a toolbar and a GraphPanel, showing 45
 * random layers.
 * 
 * @author Julian Ott
 * @author Michael Duelli
 * @since 2010-08-24
 */
@SuppressWarnings("serial")
public final class GraphPanelDemo extends Gui implements ActionListener {
	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				GraphPanelDemo main = new GraphPanelDemo();
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

	public GraphPanelDemo() {
		super("MuLaViTo Multi-Layer Graph Demo");

		// Set the frame icon.
		ImageIcon icon = Resources.getIconByName("/img/mulavito-logo.png");
		if (icon != null)
			setIconImage(icon.getImage());

		// graphpanel filling
		graphpanel = new SampleGraphPanel();
		add(graphpanel, BorderLayout.CENTER);

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

		btn = new JButton("Sync / Unsync");
		btn.setActionCommand("sync");
		btn.addActionListener(this);
		toolbar.add(btn);

		btn = new JButton("About");
		btn.setActionCommand("about");
		btn.addActionListener(this);
		toolbar.add(btn);

		setPreferredSize(new Dimension(500, 400));
		setMinimumSize(new Dimension(500, 400));
		setVisible(true);
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
			html += "<li>Julian Ott";
			html += "</ul>";
			html += "</html>";
			JOptionPane.showMessageDialog(this, html);
		} else if (cmd.equals("sync")) {
			graphpanel.setSynced(!graphpanel.isSynced());
		} else if (cmd.equals("graph")) {
			graphpanel.setLayerStack(SampleGraphDocument.createDemo(45)
					.getMlg());
		} else if (cmd.equals("autosize")) {
			graphpanel.autoZoomToFit();
		}
	}
}
