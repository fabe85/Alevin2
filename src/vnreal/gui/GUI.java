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
package vnreal.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mulavito.gui.Gui;
import mulavito.gui.components.selectionpanel.SelectionPanel;
import mulavito.utils.Resources;
import vnreal.Scenario;
import vnreal.constraints.AbstractConstraint;
import vnreal.gui.mapping.MappingPanel;
import vnreal.gui.menu.AlgorithmsMenu;
import vnreal.gui.menu.FileMenu;
import vnreal.gui.menu.GeneratorMenu;
import vnreal.gui.menu.HelpMenu;
import vnreal.gui.menu.MetricsMenu;
import vnreal.gui.menu.ViewMenu;
import vnreal.gui.utils.MySelectionTreeModel;
import vnreal.network.Link;
import vnreal.network.Network;
import vnreal.network.Node;

/**
 * The main GUI class.
 * 
 * @author Vlad Singeorzan
 * @author Michael Duelli
 * @since 2010-10-04
 */
@SuppressWarnings("serial")
public final class GUI extends Gui {
	/**
	 * To have a graph panel which accepts substrate and virtual networks, we
	 * intentionally omit generics here.
	 */
	private MyGraphPanel graphpanel;
	private ToolBar toolbar;
	private MappingPanel mappingPanel;
	private static GUI singleton;
	private final Scenario scenario;
	private JComponent centerPane;
	private JComponent rightPane;

	public GUI(Scenario scenario) {
		super("ALEVIN - ALgorithms for Embedding VIrtual Networks");

		// Pane initialization requires this.scenario to be set,
		// therefore it cannot take place in the super constructor.
		this.scenario = scenario;
		this.initializeRightPane();
		this.initializeCenterPane();

		singleton = this;
		// setPreferredSize(preferredSize)

		// Set the frame icon.
		ImageIcon icon = Resources.getIconByName("/img/alevin-logo.png");
		if (icon != null)
			setIconImage(icon.getImage());

		toolbar = new ToolBar(graphpanel, scenario);
		// Add tool bar.
		getToolBarPane().add(toolbar);

		// Add menu bar.
		JMenuBar menubar = new JMenuBar();
		menubar.add(new FileMenu(scenario));
		menubar.add(new ViewMenu());
		menubar.add(new GeneratorMenu(scenario));
		menubar.add(new AlgorithmsMenu(scenario));
		menubar.add(new MetricsMenu(scenario));
		menubar.add(Box.createHorizontalGlue());
		menubar.add(new HelpMenu());
		setJMenuBar(menubar);

		normalOutput("Welcome to ALEVIN.\n");
		// debugOutput("Click \"Import\" to create an exemplary scenario.\n");
		// warnOutput("Have fun!!!\n");

		scenario.addChangeListener(new ChangeListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void stateChanged(ChangeEvent e) {
				// Network stack was updated.
				graphpanel.setLayerStack(GUI.this.scenario.getNetworkStack());
			}
		});

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);
				dispose();

				System.exit(0);
			}
		});

		pack();
		setVisible(true);
	}

	public ToolBar getToolBar() {
		return toolbar;
	}

	@Override
	protected JComponent createRightPane() {
		// Pane creation is deferred to initializeRightPane.
		this.rightPane = new JPanel();
		this.rightPane.setLayout(new BorderLayout());
		return this.rightPane;
	}

	private void initializeRightPane() {
		JTabbedPane tabs = new JTabbedPane();

		tabs.addTab("Selection", new SelectionPanel(new MySelectionTreeModel(),
				graphpanel));
		tabs.addTab("Mapping",
				mappingPanel = new MappingPanel(graphpanel, scenario));

		tabs.setPreferredSize(new Dimension(250, 300));
		this.rightPane.add(tabs, BorderLayout.CENTER);
	}

	@Override
	protected JComponent createCenterPane() {
		// Pane creation is deferred to initializeCenterPane.
		this.centerPane = new JPanel();
		this.centerPane.setLayout(new BorderLayout());
		return this.centerPane;
	}

	@SuppressWarnings("unchecked")
	private void initializeCenterPane() {
		graphpanel = new MyGraphPanel(scenario);
		graphpanel.setPreferredSize(new Dimension(520, 300));
		graphpanel.setSynced(false);
		graphpanel.setLayerStack(scenario.getNetworkStack());

		graphpanel.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("Viewers")) {
					if (evt.getNewValue() != null) {
						getStatusManager()
								.addHintFor(
										(Component) evt.getNewValue(),
										"Hint: CTRL+Mouse Wheel for Zoom, "
												+ "SHIFT+Left click to select arbitrary elements");
						getStatusManager().enableHints(
								(Component) evt.getNewValue(), true);
					} else if (evt.getOldValue() != null) {
						getStatusManager().removeHint(
								(Component) evt.getOldValue());
						getStatusManager().enableHints(
								(Component) evt.getOldValue(), false);
					}
				}
			}
		});

		this.centerPane.add(graphpanel, BorderLayout.CENTER);
	}

	public MyGraphPanel<?, ?, ?, ?> getGraphPanel() {
		return graphpanel;
	}

	public void update() {
		mappingPanel.update();
	}

	public static GUI getInstance() {
		return singleton;
	}

	public static boolean isInitialized() {
		return (singleton != null);
	}
}
