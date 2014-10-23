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
package vnreal.gui.control;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import mulavito.gui.components.LayerViewer;
import mulavito.gui.control.AbstractPopupMousePlugin;
import mulavito.utils.SVGExporter;

import org.apache.commons.collections15.Factory;

import vnreal.Scenario;
import vnreal.constraints.AbstractConstraint;
import vnreal.gui.GUI;
import vnreal.gui.MyGraphPanel;
import vnreal.gui.control.MyFileChooser.MyFileChooserType;
import vnreal.gui.dialog.AddConstraintDialog;
import vnreal.gui.dialog.EditConstraintDialog;
import vnreal.gui.dialog.RemoveConstraintDialog;
import vnreal.gui.utils.FileFilters;
import vnreal.network.Link;
import vnreal.network.Network;
import vnreal.network.NetworkEntity;
import vnreal.network.Node;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

/**
 * Provides the context menu functionality on substrate and virtual networks as
 * well as on nodes and links.
 * 
 * @author Michael Duelli
 * @author Vlad Singeorzan
 * @since 2010-10-27
 */
public final class MyEditingPopupGraphMousePlugin<T extends AbstractConstraint, V extends Node<T>, E extends Link<T>, N extends Network<T, V, E>>
		extends AbstractPopupMousePlugin<V, E, N, LayerViewer<V, E>> {
	private final LayerViewer<V, E> vv;
	
	private final Scenario scenario;

	public MyEditingPopupGraphMousePlugin(final LayerViewer<V, E> vv, Scenario scenario) {
		super(InputEvent.BUTTON3_MASK, new Factory<V>() {
			private final int layer = vv.getLayer().getLayer();

			@SuppressWarnings("unchecked")
			@Override
			public V create() {
				if (layer > 0)
					return (V) new VirtualNode(layer);
				else
					return (V) new SubstrateNode();
			}
		}, new Factory<E>() {
			private final int layer = vv.getLayer().getLayer();

			@SuppressWarnings("unchecked")
			@Override
			public E create() {
				if (layer > 0)
					return (E) new VirtualLink(layer);
				else
					return (E) new SubstrateLink();
			}
		});

		this.scenario = scenario;
		this.vv = vv;
	}

	@Override
	protected void createEdgeMenuEntries(final Point point, final N graph,
			final LayerViewer<V, E> vv, List<E> links) {
		JMenuItem mi;
		if (links.size() == 1) {
			final E link = links.get(0);
			final int layer = vv.getLayer().getLayer();

			// create the add/edit/remove resource/demand menu items
			createAddConstraintMenuItem(link, layer);
			createEditConstraintMenuItem(link, layer);
			createRemoveConstraintMenuItem(link, layer);
			popup.addSeparator();
			// deleting the selected link
			mi = new JMenuItem("Delete link");
			mi.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (JOptionPane.showConfirmDialog(GUI.getInstance(),
							"Delete link \"" + link.toString() + "\"?",
							"Delete link", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
						@SuppressWarnings("rawtypes")
						Network net = scenario.getNetworkStack()
								.getLayer(layer);
						if ((net instanceof SubstrateNetwork && ((SubstrateNetwork) net)
								.removeEdge((SubstrateLink) link))
								|| (net instanceof VirtualNetwork && ((VirtualNetwork) net)
										.removeEdge((VirtualLink) link))) {
							vv.updateUI();
						} else {
							throw new AssertionError("Deleting link failed!");
						}
					}
				}
			});
			popup.add(mi);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void createGeneralMenuEntries(final Point point, final N graph,
			final LayerViewer<V, E> vv) {
		JMenuItem mi;
		// creating a node
		mi = new JMenuItem("Create node");
		final int layer = vv.getLayer().getLayer();
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// adding an resource/demand
				boolean addConstraint = true;
				V node = vertexFactory.create();
				Point2D p2 = vv.getRenderContext().getMultiLayerTransformer()
						.inverseTransform(point);
				node.setCoordinateX(p2.getX());
				node.setCoordinateY(p2.getY());

				while (addConstraint) {
					new AddConstraintDialog(node, layer, GUI.getInstance(),
							new Dimension(300, 150));
					// if a resource/demand has been added
					if (node.get().size() > 0) {
						addConstraint = false;
						@SuppressWarnings("rawtypes")
						Network net = scenario.getNetworkStack()
								.getLayer(layer);
						if ((net instanceof SubstrateNetwork && ((SubstrateNetwork) net)
								.addVertex((SubstrateNode) node))
								|| (net instanceof VirtualNetwork && ((VirtualNetwork) net)
										.addVertex((VirtualNode) node))) {
							vv.updateUI();
						} else {
							throw new AssertionError("Adding Node failed.");
						}
					} else {
						String cons = (layer == 0 ? "Resource" : "Demand");

						int option = JOptionPane.showConfirmDialog(
								GUI.getInstance(),
								"A "
										+ cons
										+ " must be added for the node to be created!",
								"Create Node", JOptionPane.OK_CANCEL_OPTION);
						if (option == JOptionPane.CANCEL_OPTION
								|| option == JOptionPane.CLOSED_OPTION) {
							addConstraint = false;
						}
					}
				}
			}
		});
		popup.add(mi);

		popup.addSeparator();

		@SuppressWarnings("rawtypes")
		final MyGraphPanel pnl = GUI.getInstance().getGraphPanel();
		if (pnl.getMaximized() == null) {
			mi = new JMenuItem("Maximize");
			mi.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					pnl.setMaximized(vv);
				}
			});
		} else {
			mi = new JMenuItem("Restore");
			mi.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					pnl.setMaximized(null);
				}
			});
		}
		popup.add(mi);

		if (pnl.getMaximized() != null) {
			mi = new JMenuItem("Hide");
			mi.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					vv.getParent().setVisible(false);
				}
			});
			popup.add(mi);
		}

		popup.addSeparator();

		// Copied from MuLaNEO
		mi = new JMenuItem("Export layer to SVG");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int layer = vv.getLayer().getLayer();
				MyFileChooser fc = new MyFileChooser("Export layer " + layer
						+ " to SVG", MyFileChooserType.MISC, false);
				fc.addChoosableFileFilter(FileFilters.svgFilter);
				fc.setSelectedFile(new File("export-layer-" + layer + ".svg"));

				if (fc.showSaveDialog(GUI.getInstance()) == JFileChooser.APPROVE_OPTION)
					new SVGExporter().export(vv, fc.getSelectedFile());
			}
		});
		popup.add(mi);
	}

	@Override
	protected void createVertexMenuEntries(final Point point, final N graph,
			final LayerViewer<V, E> vv, final List<V> nodes) {
		JMenuItem mi;
		final int layer = vv.getLayer().getLayer();
		if (nodes.size() == 1) {
			final V node = nodes.get(0);

			// create the add/edit/remove resource/demand menu items
			createAddConstraintMenuItem(node, layer);
			createEditConstraintMenuItem(node, layer);
			createRemoveConstraintMenuItem(node, layer);
			popup.addSeparator();
			// delete the selected node
			mi = new JMenuItem("Delete node");
			mi.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (JOptionPane.showConfirmDialog(GUI.getInstance(),
							"Delete node \"" + node.toString() + "\"?",
							"Delete node", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
						@SuppressWarnings("rawtypes")
						Network net = scenario.getNetworkStack()
								.getLayer(layer);
						if ((net instanceof SubstrateNetwork && ((SubstrateNetwork) net)
								.removeVertex((SubstrateNode) node))
								|| (net instanceof VirtualNetwork && ((VirtualNetwork) net)
										.removeVertex((VirtualNode) node))) {
							vv.updateUI();
						} else {
							throw new AssertionError("Deleting node failed.");
						}
					}
				}
			});
			popup.add(mi);
		} else if (nodes.size() == 2) {
			// add a link from v1 to v2 or v2 to v1 if two nodes are selected
			final V v1 = nodes.get(0);
			final V v2 = nodes.get(1);
			// v1 to v2
			mi = new JMenuItem("Add link " + v1.toString() + " -> "
					+ v2.toString());
			mi.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					addLink(layer, v1, v2);
				}
			});
			popup.add(mi);

			// v2 to v1
			mi = new JMenuItem("Add link " + v2.toString() + " > "
					+ v1.toString());
			mi.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					addLink(layer, v2, v1);
				}
			});
			popup.add(mi);
		}
	}

	@Override
	protected List<E> getSelectedEdges() {
		return new ArrayList<E>(vv.getPickedEdgeState().getPicked());
	}

	@Override
	protected List<V> getSelectedVertices() {
		return new ArrayList<V>(vv.getPickedVertexState().getPicked());
	}

	private void addLink(int layer, V src, V dest) {
		boolean addConstraint = true;
		E edge = edgeFactory.create();
		while (addConstraint) {
			new AddConstraintDialog(edge, layer, GUI.getInstance(),
					new Dimension(300, 150));
			// if a resource/demand has been added
			if (edge.get().size() > 0) {
				addConstraint = false;
				@SuppressWarnings("rawtypes")
				Network net = scenario.getNetworkStack()
						.getLayer(layer);
				if ((net instanceof SubstrateNetwork && ((SubstrateNetwork) net)
						.addEdge((SubstrateLink) edge, (SubstrateNode) src,
								(SubstrateNode) dest))
						|| (net instanceof VirtualNetwork && ((VirtualNetwork) net)
								.addEdge((VirtualLink) edge, (VirtualNode) src,
										(VirtualNode) dest))) {
					vv.updateUI();
				} else {
					throw new AssertionError("Adding link failed.");
				}
			} else {
				int option = JOptionPane.showConfirmDialog(GUI.getInstance(),
						"A " + (layer == 0 ? "Resource" : "Demand")
								+ " must be added for the link to be created!",
						"Create Node", JOptionPane.OK_CANCEL_OPTION);
				if (option == JOptionPane.CANCEL_OPTION
						|| option == JOptionPane.CLOSED_OPTION) {
					addConstraint = false;
				}
			}
		}
	}

	private void createAddConstraintMenuItem(
			@SuppressWarnings("rawtypes") final NetworkEntity ne,
			final int layer) {
		JMenuItem mi = new JMenuItem("Add "
				+ (layer == 0 ? "resource" : "demand"));
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new AddConstraintDialog(ne, layer, GUI.getInstance(),
						new Dimension(300, 150));
			}
		});
		popup.add(mi);
	}

	private void createEditConstraintMenuItem(
			@SuppressWarnings("rawtypes") final NetworkEntity ne,
			final int layer) {
		JMenuItem mi = new JMenuItem("Edit "
				+ (layer == 0 ? "resource" : "demand"));
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new EditConstraintDialog(ne, layer, GUI.getInstance(),
						new Dimension(300, 150));
			}
		});
		popup.add(mi);
	}

	private void createRemoveConstraintMenuItem(
			@SuppressWarnings("rawtypes") final NetworkEntity ne,
			final int layer) {
		JMenuItem mi = new JMenuItem("Remove "
				+ (layer == 0 ? "resource" : "demand"));
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new RemoveConstraintDialog(ne, layer, GUI.getInstance(),
						new Dimension(300, 150));
			}
		});
		popup.add(mi);
	}
}
