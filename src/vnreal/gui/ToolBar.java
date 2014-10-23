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

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JToolBar;

import mulavito.gui.components.QuickSearchBar;
import mulavito.utils.Resources;
import vnreal.Scenario;
import vnreal.gui.control.MyFileChooser;
import vnreal.gui.control.MyFileChooser.MyFileChooserType;
import vnreal.gui.utils.FileFilters;
import vnreal.io.XMLImporter;

/**
 * A tool bar.
 * 
 * @author Michael Duelli
 * @author Vlad Singeorzan
 * @since 2010-10-21
 */
@SuppressWarnings("serial")
public final class ToolBar extends JToolBar {
	
	private final Scenario scenario;
	
	@SuppressWarnings("unchecked")
	public ToolBar(final MyGraphPanel graphpanel, Scenario scenario) {
		super("ALEVIN's tool bar");
		this.scenario = scenario;
		setAlignmentX(LEFT_ALIGNMENT);

		JButton btn;

		add(btn = new JButton(
				Resources
						.getIconByName("/img/icons/32x32/emblems/emblem-symbolic-link.png")));
		btn.setToolTipText("Import Exemplary Scenario");
		btn.setPreferredSize(new Dimension(40, 40));
		btn.setMargin(new Insets(0, 0, 0, 0));
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MyFileChooser fileChooser = new MyFileChooser(
						"Scenario Import", MyFileChooserType.XML, true);
				fileChooser.addChoosableFileFilter(FileFilters.xmlFilter);
				fileChooser.showOpenDialog(GUI.getInstance());
				if (fileChooser.getSelectedFile() != null) {
					try {
						XMLImporter.importScenario(fileChooser.getSelectedFile()
								.getCanonicalPath(), ToolBar.this.scenario);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});

		add(btn = new JButton(
				Resources
						.getIconByName("/img/icons/32x32/actions/view-fullscreen.png")));
		btn.setToolTipText("Auto Size");
		btn.setPreferredSize(new Dimension(40, 40));
		btn.setMargin(new Insets(0, 0, 0, 0));
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				graphpanel.autoZoomToFit();
				GUI.getInstance().update();
			}
		});

		add(btn = new JButton(
				Resources
						.getIconByName("/img/icons/32x32/actions/system-lock-screen.png")));
		btn.setToolTipText("Sync / Unsync");
		btn.setPreferredSize(new Dimension(40, 40));
		btn.setMargin(new Insets(0, 0, 0, 0));
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				graphpanel.setSynced(!graphpanel.isSynced());
			}
		});

		add(Box.createHorizontalGlue());

		// Quick search
		QuickSearchBar qsb = new QuickSearchBar();
		qsb.setGraphPanel(graphpanel);
		add(qsb);
	}
}
