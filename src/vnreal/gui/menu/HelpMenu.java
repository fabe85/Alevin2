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

import vnreal.gui.GUI;

/**
 * @author Michael Duelli
 * @since 2011-04-08
 */
@SuppressWarnings("serial")
public final class HelpMenu extends JMenu implements ActionListener {
	public HelpMenu() {
		super("Help");
		setMnemonic(KeyEvent.VK_H);

		JMenuItem mi;

		mi = new JMenuItem("About");
		mi.setActionCommand("about");
		mi.addActionListener(this);
		add(mi);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		if (cmd.equals("about")) {
			String html = "<html><h1>ALEVIN</h1>";
			html += "WWW: http://alevin.sf.net";
			html += "<h3>Authors</h3>";
			html += "in alphabetical order:";
			html += "<ul>";
			html += "<li>Michael Till Beck";
			html += "<li>Juan Felipe Botero";
			html += "<li>Lisset Diaz Cervantes";
			html += "<li>Michael Duelli";
			html += "<li>Andreas Fischer";
			html += "<li>Xavier Hesselbach";
			html += "<li>Daniel Schlosser";
			html += "<li>Vlad Singeorzan";
			html += "</ul>";
			html += "</html>";
			JOptionPane.showMessageDialog(GUI.getInstance(), html);
		} else
			throw new AssertionError("Undefined menu action");
	}
}
