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
package mulavito.gui.components.selectionpanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import mulavito.graph.IEdge;
import mulavito.graph.IVertex;
import mulavito.gui.components.GraphPanel;
import mulavito.gui.components.LayerViewer;
import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * Used in {@link SelectionPanel} only.
 * 
 * @author Michael Duelli
 * @author Julian Ott (remove selected items)
 */
public final class SelectionPanelMouseAdapter extends MouseAdapter {
	private JPopupMenu popup = new JPopupMenu();
	private SelectionPanel owner;

	public SelectionPanelMouseAdapter(SelectionPanel owner) {
		if (owner == null)
			throw new IllegalArgumentException();
		this.owner = owner;
	}

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

			if (!tree.isSelectionEmpty()) {
				// Clear selected
				mi = new JMenuItem("Remove selected");
				mi.addActionListener(new ActionListener() {
					@SuppressWarnings("unchecked")
					@Override
					public void actionPerformed(ActionEvent e2) {
						GraphPanel<?, ?> gp = owner.getGraphPanel();
						if (gp == null)
							return;

						// Deselect all selected objects.
						for (TreePath path : tree.getSelectionPaths()) {
							Object last = path.getLastPathComponent();

							if (last instanceof IVertex) {
								IVertex v = (IVertex) last;
								// unpick vertex, search all layers for the
								// given vertex
								for (LayerViewer<?, ?> vv : gp.getViewers()) {
									// ugly workaround to keep generics out
									// of selection panel but works, since
									// generics are not checked at runtime
									// and vertex is for sure of the expected
									// instance type
									((PickedState<IVertex>) vv
											.getPickedVertexState()).pick(v,
											false);
								}
							} else if (last instanceof IEdge) {
								IEdge e = (IEdge) last;
								// unpick edge, search all layers for the
								// given edge
								for (LayerViewer<?, ?> vv : gp.getViewers()) {
									// same workaround as atop
									((PickedState<IEdge>) vv
											.getPickedEdgeState()).pick(e,
											false);
								}
							}
						}
					}
				});
				popup.add(mi);
				popup.addSeparator();
			}

			if (tree.getRowCount() > 2) {
				// Clear all.
				mi = new JMenuItem("Clear");
				mi.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e2) {
						GraphPanel<?, ?> gp = owner.getGraphPanel();
						if (gp == null)
							return;

						for (LayerViewer<?, ?> vv : gp.getViewers()) {
							vv.getPickedVertexState().clear();
							vv.getPickedEdgeState().clear();
						}
					}
				});
				popup.add(mi);

				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}
}
