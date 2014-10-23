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
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import mulavito.gui.Gui;
import mulavito.gui.components.LayerDataPanel;
import mulavito.gui.components.LayerViewer;
import mulavito.gui.components.QuickSearchBar;
import mulavito.gui.components.selectionpanel.SelectionPanel;
import mulavito.gui.control.FileDropTargetListener;
import mulavito.samples.utils.MyE;
import mulavito.samples.utils.MyL;
import mulavito.samples.utils.SampleGraphDocument;
import mulavito.samples.utils.SampleGraphDocument.MyV;
import mulavito.samples.utils.SampleGraphPanel;

/**
 * This demo shows a usual multi-layer GUI, with some empty panels, and a
 * graphpanel showing six layers. There is also a file drop support, which can
 * be used for opening documents.
 * 
 * @author Julian Ott
 * @author Michael Duelli
 * @since 2010-08-24
 */
@SuppressWarnings("serial")
public final class PaneledGuiDemo extends Gui implements ActionListener {
	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				PaneledGuiDemo main = new PaneledGuiDemo();
				main.pack();
				main.setVisible(true);
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

	public PaneledGuiDemo() {
		super("MuLaViTo Panel GUI Demo");
		// setPreferredSize(preferredSize)
		JToolBar toolbar = new JToolBar("Standard");
		toolbar.setAlignmentX(LEFT_ALIGNMENT);
		JButton btn;

		btn = new JButton("New Graph");
		btn.setActionCommand("graph");
		btn.addActionListener(this);
		toolbar.add(btn);

		btn = new JButton("AutoZoom");
		btn.setActionCommand("autozoom");
		btn.addActionListener(this);
		toolbar.add(btn);

		btn = new JButton("Sync / Unsync");
		btn.setActionCommand("sync");
		btn.addActionListener(this);
		toolbar.add(btn);

		toolbar.add(Box.createHorizontalGlue());

		// Quick search
		QuickSearchBar<MyL, LayerViewer<MyV, MyE>> qsb = new QuickSearchBar<MyL, LayerViewer<MyV, MyE>>();
		qsb.setGraphPanel(graphpanel);
		toolbar.add(qsb);
		getToolBarPane().add(toolbar);

		JMenuBar menubar = new JMenuBar();
		JMenu menu = new JMenu("Help");
		menubar.add(menu);
		setJMenuBar(menubar);

		JMenuItem mi = new JMenuItem("About");
		mi.setActionCommand("about");
		mi.addActionListener(this);
		menu.add(mi);

		normalOutput("Welcome to MuLaViTo demonstrator.\n");
		debugOutput("Click on \"New Graph\" to create a multi-layer graph.\n");
		warnOutput("CTRL-Mouse wheel to zoom the graph(s)\n");
		notifyOutput("Have fun!!!\n");

		LayerDataPanel<MyV, MyE> infoPanel = new LayerDataPanel<MyV, MyE>(
				getBottomPane());
		getBottomPane().addTab(infoPanel.getTitle(), null, infoPanel,
				"Shows information on a layer");
		infoPanel.setGraphPanel(graphpanel);
	}

	@Override
	protected JComponent createRightPane() {
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("One", new SelectionPanel(graphpanel));
		tabs.addTab("Two", new JLabel("Two"));
		tabs.addTab("Three", new JLabel("Three"));
		tabs.setPreferredSize(new Dimension(80, 300));
		return tabs;
	}

	@Override
	protected JComponent createCenterPane() {
		graphpanel = new SampleGraphPanel();
		graphpanel.setPreferredSize(new Dimension(600, 300));
		new DropTarget(graphpanel, new FileDropTargetListener() {
			@Override
			protected void openFile(File file) {
				// insert your own function for loading a file
				JOptionPane.showMessageDialog(graphpanel,
						"Opened " + file.getName());
			}

			@Override
			protected boolean canOpenFile() {
				// is GUI ready for opening a file?
				return true;
			}
		});
		return graphpanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		if (cmd.equals("graph")) {
			graphpanel
					.setLayerStack(SampleGraphDocument.createDemo(6).getMlg());
		} else if (cmd.equals("autozoom")) {
			graphpanel.autoZoomToFit();
		} else if (cmd.equals("sync")) {
			graphpanel.setSynced(!graphpanel.isSynced());
		} else if (cmd.equals("about")) {
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
		}
	}
}
