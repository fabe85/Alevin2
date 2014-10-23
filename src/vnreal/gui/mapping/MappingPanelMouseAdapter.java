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
package vnreal.gui.mapping;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;

import mulavito.gui.components.selectionpanel.SelectionPanelMouseAdapter;

/**
 * Adopted from MuLaViTo's {@link SelectionPanelMouseAdapter}.
 * 
 * @author Michael Duelli
 * @since 2010-11-23
 */
public final class MappingPanelMouseAdapter extends MouseAdapter {
	private final JPopupMenu popup = new JPopupMenu();

	@Override
	public void mousePressed(MouseEvent e) {
		handlePopup(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		handlePopup(e);
	}

	private void handlePopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			JMenuItem mi;

			popup.removeAll();

			final JTree tree = (JTree) e.getSource();

			if (tree.getRowCount() > 0) {
				mi = new JMenuItem("Expand all");
				mi.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						for (int row = 0; row < tree.getRowCount(); row++)
							tree.expandRow(row);
					}
				});
				popup.add(mi);
			}

			if (tree.getSelectionCount() > 0) {
				mi = new JMenuItem("Clear selection");
				mi.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						tree.clearSelection();
					}
				});
				popup.add(mi);
			}

			if (popup.getComponentCount() > 0)
				popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}
