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

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import vnreal.gui.GUI;
import vnreal.gui.transformer.LinkLabelTransformer;
import vnreal.gui.transformer.NodeLabelTransformer;

/**
 * @author Michael Duelli
 * @since 2010-11-19
 */
@SuppressWarnings("serial")
public final class ViewMenu extends JMenu implements ActionListener {
	public ViewMenu() {
		super("View");
		setMnemonic(KeyEvent.VK_V);

		JMenuItem cb = new JCheckBoxMenuItem("Show node labels");
		cb.setActionCommand("node_labels");
		cb.addActionListener(this);
		add(cb);

		cb = new JCheckBoxMenuItem("Show link labels");
		cb.setActionCommand("link_labels");
		cb.addActionListener(this);
		add(cb);

		addSeparator();

		add(GUI.getInstance().getBottomPane().getHiddenTabs());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("node_labels")) {
			NodeLabelTransformer<?> nodeLT = GUI.getInstance().getGraphPanel()
					.getNodeLT();
			nodeLT.setEnabled(!nodeLT.isEnabled());
			GUI.getInstance().getGraphPanel().updateUI();
		} else if (cmd.equals("link_labels")) {
			LinkLabelTransformer<?> linkLT = GUI.getInstance().getGraphPanel()
					.getLinkLT();
			linkLT.setEnabled(!linkLT.isEnabled());
			GUI.getInstance().getGraphPanel().updateUI();
		}
	}
}
