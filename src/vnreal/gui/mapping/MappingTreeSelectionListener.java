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

import java.awt.Color;
import java.awt.Paint;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import mulavito.gui.components.LayerViewer;

import org.apache.commons.collections15.Transformer;

import vnreal.gui.mapping.MappingTreeModel.LinkProxy;
import vnreal.gui.mapping.MappingTreeModel.MappingProxy;
import vnreal.gui.mapping.MappingTreeModel.SubstrateLinkProxy;
import vnreal.gui.mapping.MappingTreeModel.VirtualLinkProxy;
import vnreal.mapping.Mapping;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNode;

/**
 * @author Michael Duelli
 * @since 2010-12-07
 */
public final class MappingTreeSelectionListener implements
		TreeSelectionListener {
	private MappingPanel owner;
	private Map<Integer, Transformer<VirtualNode, Paint>> backupVN = new HashMap<Integer, Transformer<VirtualNode, Paint>>();
	private Map<Integer, Transformer<VirtualLink, Paint>> backupVL = new HashMap<Integer, Transformer<VirtualLink, Paint>>();
	private Transformer<SubstrateLink, Paint> backupSL;
	private Transformer<SubstrateNode, Paint> backupSN;

	public MappingTreeSelectionListener(MappingPanel owner) {
		if (owner == null)
			throw new IllegalArgumentException();
		this.owner = owner;
	}

	private class HighlightVLTransformer implements
			Transformer<VirtualLink, Paint> {
		private Set<VirtualLink> vls = new HashSet<VirtualLink>();

		public void addLink(VirtualLink vl) {
			this.vls.add(vl);
		}

		@Override
		public Paint transform(VirtualLink input) {
			if (vls.contains(input))
				return Color.CYAN;
			else
				return backupVL.get(input.getLayer()).transform(input);
		}
	}

	private final HighlightVLTransformer highlightVL = new HighlightVLTransformer();

	private class HighlightVNTransformer implements
			Transformer<VirtualNode, Paint> {
		private Set<VirtualNode> vns = new HashSet<VirtualNode>();

		public void addNode(VirtualNode vn) {
			this.vns.add(vn);
		}

		public void addNodes(Collection<VirtualNode> endpoints) {
			this.vns.addAll(endpoints);
		}

		@Override
		public Paint transform(VirtualNode input) {
			if (vns.contains(input))
				return Color.GREEN;
			else
				return backupVN.get(input.getLayer()).transform(input);
		}
	}

	private final HighlightVNTransformer highlightVN = new HighlightVNTransformer();

	private class HighlightSLTransformer implements
			Transformer<SubstrateLink, Paint> {
		private Set<SubstrateLink> links = new HashSet<SubstrateLink>();

		@Override
		public Paint transform(SubstrateLink input) {
			if (links.contains(input))
				return Color.CYAN;
			else
				return backupSL.transform(input);
		}
	}

	private final HighlightSLTransformer highlightSL = new HighlightSLTransformer();

	private class HighlightSNTransformer implements
			Transformer<SubstrateNode, Paint> {
		private Set<SubstrateNode> nodes = new HashSet<SubstrateNode>();
		private Set<SubstrateNode> nodesHH = new HashSet<SubstrateNode>();

		@Override
		public Paint transform(SubstrateNode input) {
			if (nodes.contains(input))
				return Color.GREEN; // source or destination
			else if (nodesHH.contains(input))
				return Color.MAGENTA; // hidden hops
			else
				return backupSN.transform(input);
		}
	}

	private final HighlightSNTransformer highlightSN = new HighlightSNTransformer();

	@SuppressWarnings("unchecked")
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		if (owner.getGraphPanel() == null)
			return;

		List<TreePath> linkProxies = new LinkedList<TreePath>();
		for (TreePath path : e.getPaths())
			if (path.getLastPathComponent() instanceof LinkProxy)
				linkProxies.add(path);

		// if we have a select and unselect of LinkProxies in one step ...
		if (linkProxies.size() == 2) {
			// ... ensure unselect comes before select
			if (e.isAddedPath(linkProxies.get(0)))
				Collections.reverse(linkProxies);
		}

		for (TreePath path : linkProxies) {
			Object obj = path.getLastPathComponent();

			final boolean state = e.isAddedPath(path);

			if (state) {
				if (obj instanceof VirtualLinkProxy) {
					// Highlight one virtual link and its nodes as well several
					// substrate elements
					VirtualLinkProxy lp = (VirtualLinkProxy) obj;
					VirtualLink vl = lp.getLink();

					backupVN.put(
							vl.getLayer(),
							((LayerViewer<VirtualNode, VirtualLink>) owner
									.getGraphPanel().getViewers()
									.get(vl.getLayer())).getRenderContext()
									.getVertexFillPaintTransformer());
					backupVL.put(
							vl.getLayer(),
							((LayerViewer<VirtualNode, VirtualLink>) owner
									.getGraphPanel().getViewers()
									.get(vl.getLayer())).getRenderContext()
									.getEdgeDrawPaintTransformer());

					highlightVN.addNodes(lp.getvNet().getEndpoints(vl));
					highlightVL.addLink(vl);

					backupSN = ((LayerViewer<SubstrateNode, SubstrateLink>) owner
							.getGraphPanel().getViewers().get(0))
							.getRenderContext().getVertexFillPaintTransformer();
					backupSL = ((LayerViewer<SubstrateNode, SubstrateLink>) owner
							.getGraphPanel().getViewers().get(0))
							.getRenderContext().getEdgeDrawPaintTransformer();

					int max = lp.getChildCount() - 1;
					for (int i = 0; i <= max; i++) {
						MappingProxy child = (MappingProxy) lp.getChildAt(i);

						Mapping mp = child.getMapping();
						if (mp.getResource().getOwner() instanceof SubstrateLink) {
							highlightSL.links.add((SubstrateLink) mp
									.getResource().getOwner());
						} else if (mp.getResource().getOwner() instanceof SubstrateNode) {
							SubstrateNode sn = (SubstrateNode) mp.getResource()
									.getOwner();
							if (i == 0 || i == max)
								highlightSN.nodes.add(sn);
							else
								highlightSN.nodesHH.add(sn);
						}
					}

					for (Integer layer : backupVN.keySet())
						setVNetVertexTransformer(layer, highlightVN);
					for (Integer layer : backupVL.keySet())
						setVNetEdgeTransformer(layer, highlightVL);

					setSNetVertexTransformer(highlightSN);
					setSNetEdgeTransformer(highlightSL);
				} else if (obj instanceof SubstrateLinkProxy) {
					// Highlight one substrate link and its nodes as well
					// several virtual elements in different virtual networks
					SubstrateLinkProxy lp = (SubstrateLinkProxy) obj;
					SubstrateLink sl = lp.getLink();

					backupSN = ((LayerViewer<SubstrateNode, SubstrateLink>) owner
							.getGraphPanel().getViewers().get(0))
							.getRenderContext().getVertexFillPaintTransformer();
					backupSL = ((LayerViewer<SubstrateNode, SubstrateLink>) owner
							.getGraphPanel().getViewers().get(0))
							.getRenderContext().getEdgeDrawPaintTransformer();

					highlightSL.links.add(sl);
					highlightSN.nodes.addAll(lp.getNet().getEndpoints(sl));

					int max = lp.getChildCount() - 1;
					for (int i = 0; i <= max; i++) {
						MappingProxy child = (MappingProxy) lp.getChildAt(i);

						Mapping mp = child.getMapping();
						if (mp.getDemand().getOwner() instanceof VirtualLink) {
							VirtualLink vl = (VirtualLink) mp.getDemand()
									.getOwner();
							backupVL.put(
									vl.getLayer(),
									((LayerViewer<VirtualNode, VirtualLink>) owner
											.getGraphPanel().getViewers()
											.get(vl.getLayer()))
											.getRenderContext()
											.getEdgeDrawPaintTransformer());
							highlightVL.addLink(vl);
						} else if (mp.getDemand().getOwner() instanceof VirtualNode) {
							VirtualNode vn = (VirtualNode) mp.getDemand()
									.getOwner();
							backupVN.put(
									vn.getLayer(),
									((LayerViewer<VirtualNode, VirtualLink>) owner
											.getGraphPanel().getViewers()
											.get(vn.getLayer()))
											.getRenderContext()
											.getVertexFillPaintTransformer());
							highlightVN.addNode(vn);
						}
					}

					for (Integer layer : backupVN.keySet())
						setVNetVertexTransformer(layer, highlightVN);
					for (Integer layer : backupVL.keySet())
						setVNetEdgeTransformer(layer, highlightVL);

					setSNetVertexTransformer(highlightSN);
					setSNetEdgeTransformer(highlightSL);
				}
			} else {
				for (Integer layer : backupVN.keySet())
					setVNetVertexTransformer(layer, backupVN.get(layer));
				for (Integer layer : backupVL.keySet())
					setVNetEdgeTransformer(layer, backupVL.get(layer));

				backupVN.clear();
				backupVL.clear();

				highlightVN.vns.clear();
				highlightVL.vls.clear();

				highlightSN.nodes.clear();
				highlightSN.nodesHH.clear();
				highlightSL.links.clear();

				setSNetVertexTransformer(backupSN);
				setSNetEdgeTransformer(backupSL);

				backupSN = null;
				backupSL = null;
			}
		}

		update();
	}

	@SuppressWarnings("unchecked")
	private void setVNetVertexTransformer(int layer,
			Transformer<VirtualNode, Paint> trans) {
		((LayerViewer<VirtualNode, VirtualLink>) owner.getGraphPanel()
				.getViewers().get(layer)).getRenderContext()
				.setVertexFillPaintTransformer(trans);
	}

	@SuppressWarnings("unchecked")
	private void setVNetEdgeTransformer(int layer,
			Transformer<VirtualLink, Paint> trans) {
		((LayerViewer<VirtualNode, VirtualLink>) owner.getGraphPanel()
				.getViewers().get(layer)).setEdgeDrawPaintTransformer(trans);
	}

	@SuppressWarnings("unchecked")
	private void setSNetVertexTransformer(
			Transformer<SubstrateNode, Paint> trans) {
		((LayerViewer<SubstrateNode, SubstrateLink>) owner.getGraphPanel()
				.getViewers().get(0)).getRenderContext()
				.setVertexFillPaintTransformer(trans);
	}

	@SuppressWarnings("unchecked")
	private void setSNetEdgeTransformer(Transformer<SubstrateLink, Paint> trans) {
		((LayerViewer<SubstrateNode, SubstrateLink>) owner.getGraphPanel()
				.getViewers().get(0)).setEdgeDrawPaintTransformer(trans);
	}

	private void update() {
		// Repaint all layers once.
		// This triggers the transform method in the transformers.
		owner.getGraphPanel().updateUI();
		owner.getTree().repaint();
	}
}
