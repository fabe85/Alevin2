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

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import vnreal.Scenario;
import vnreal.demands.AbstractDemand;
import vnreal.mapping.Mapping;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.resources.AbstractResource;

/**
 * @author Vlad Singeorzan
 * @author Michael Duelli
 * @since 2010-11-08
 */
public final class MappingTreeModel implements TreeModel {
	
	final Scenario scenario;
	
	protected interface IProxy {
		Object getChildAt(int index);

		int getChildCount();
	}

	protected class MappingProxy implements IProxy {
		private Mapping mapping;

		public MappingProxy(Mapping m) {
			mapping = m;
		}

		public Mapping getMapping() {
			return mapping;
		}

		@Override
		public Object getChildAt(int index) {
			return null;
		}

		@Override
		public int getChildCount() {
			// leaf element
			return 0;
		}

		@Override
		public String toString() {
			return mapping.getDemand().getOwner().toStringShort() + "->"
					+ mapping.getDemand() + "->" + mapping.getResource() + "->"
					+ mapping.getResource().getOwner().toStringShort();
		}
	}

	protected abstract class LinkProxy implements IProxy {
	}

	protected class VirtualLinkProxy extends LinkProxy {
		private VirtualLink link;
		private VirtualNetwork vNet;

		public VirtualLinkProxy(VirtualLink link, VirtualNetwork vNet) {
			this.link = link;
			this.vNet = vNet;
		}

		public VirtualLink getLink() {
			return link;
		}

		public VirtualNetwork getvNet() {
			return vNet;
		}

		@Override
		public Object getChildAt(int index) {
			List<Mapping> mappings = new ArrayList<Mapping>();

			for (AbstractDemand dem : vNet.getSource(link).get())
				mappings.addAll(dem.getMappings());
			for (AbstractDemand dem : link.get())
				mappings.addAll(dem.getMappings());
			for (AbstractDemand dem : link.getHiddenHopDemands())
				mappings.addAll(dem.getMappings());
			for (AbstractDemand dem : vNet.getDest(link).get())
				mappings.addAll(dem.getMappings());

			return new MappingProxy(mappings.get(index));
		}

		@Override
		public int getChildCount() {
			if (scenario.getNetworkStack() == null)
				return 0;

			List<Mapping> mappings = new ArrayList<Mapping>();

			for (AbstractDemand dem : vNet.getSource(link).get())
				mappings.addAll(dem.getMappings());
			for (AbstractDemand dem : link.get())
				mappings.addAll(dem.getMappings());
			for (AbstractDemand dem : link.getHiddenHopDemands())
				mappings.addAll(dem.getMappings());
			for (AbstractDemand dem : vNet.getDest(link).get())
				mappings.addAll(dem.getMappings());

			return mappings.size();
		}

		@Override
		public String toString() {
			return vNet.getSource(link).toStringShort() + "->"
					+ link.toStringShort() + "->"
					+ vNet.getDest(link).toStringShort();
		}
	}

	protected class SubstrateLinkProxy extends LinkProxy {
		private SubstrateLink link;
		private SubstrateNetwork substrate;

		public SubstrateLinkProxy(SubstrateLink link) {
			this.link = link;
			this.substrate = scenario.getNetworkStack()
					.getSubstrate();
		}

		public SubstrateLink getLink() {
			return link;
		}

		public SubstrateNetwork getNet() {
			return substrate;
		}

		@Override
		public Object getChildAt(int index) {
			List<Mapping> mappings = new ArrayList<Mapping>();

			for (AbstractResource res : substrate.getSource(link).get())
				mappings.addAll(res.getMappings());
			for (AbstractResource res : link.get())
				mappings.addAll(res.getMappings());
			for (AbstractResource res : substrate.getDest(link).get())
				mappings.addAll(res.getMappings());

			return new MappingProxy(mappings.get(index));
		}

		@Override
		public int getChildCount() {
			if (scenario.getNetworkStack() == null)
				return 0;

			List<Mapping> mappings = new ArrayList<Mapping>();

			for (AbstractResource res : substrate.getSource(link).get())
				mappings.addAll(res.getMappings());
			for (AbstractResource res : link.get())
				mappings.addAll(res.getMappings());
			for (AbstractResource res : substrate.getDest(link).get())
				mappings.addAll(res.getMappings());

			return mappings.size();
		}

		@Override
		public String toString() {
			return substrate.getSource(link).toStringShort() + "->"
					+ link.toStringShort() + "->"
					+ substrate.getDest(link).toStringShort();
		}
	}

	private class VirtualNetworkProxy implements IProxy {
		private VirtualNetwork vNet;

		public VirtualNetworkProxy(VirtualNetwork vn) {
			this.vNet = vn;
		}

		@Override
		public Object getChildAt(int index) {
			return new VirtualLinkProxy(vNet.getEdges().toArray(
					new VirtualLink[vNet.getEdgeCount()])[index], vNet);
		}

		@Override
		public int getChildCount() {
			return vNet.getEdgeCount();
		}

		@Override
		public String toString() {
			return vNet.getLabel();
		}
	}

	private class RootProxy implements IProxy {
		@Override
		public int getChildCount() {
			return 2;
		}

		@Override
		public Object getChildAt(int index) {
			switch (index) {
			case 0:
				return new VirtualNetworksProxy();
			case 1:
				return new SubstrateNetworkProxy();
			default:
				throw new AssertionError("invalid index");
			}
		}

		@Override
		public String toString() {
			return "Virtual Networks";
		}
	}

	private class VirtualNetworksProxy implements IProxy {
		@Override
		public int getChildCount() {
			if (scenario.getNetworkStack() == null)
				return 0;
			else
				// -1 because the SubstrateNetwork is not of interest here
				return scenario.getNetworkStack().size() - 1;
		}

		@Override
		public Object getChildAt(int index) {
			// index +1 due to SubstrateNetwork (layer 0)
			return new VirtualNetworkProxy((VirtualNetwork) scenario.getNetworkStack().getLayer(index + 1));
		}

		@Override
		public String toString() {
			return "Virtual Networks";
		}
	}

	private class SubstrateNetworkProxy implements IProxy {
		@Override
		public int getChildCount() {
			NetworkStack stack = scenario.getNetworkStack();
			if (stack == null)
				return 0;
			else
				return stack.getSubstrate().getEdgeCount();
		}

		@Override
		public Object getChildAt(int index) {
			SubstrateNetwork substrate = scenario
					.getNetworkStack().getSubstrate();
			return new SubstrateLinkProxy(substrate.getEdges().toArray(
					new SubstrateLink[substrate.getEdgeCount()])[index]);
		}

		@Override
		public String toString() {
			return "Substrate Network";
		}
	}

	private IProxy root;

	public MappingTreeModel(Scenario scenario) {
		root = new RootProxy();
		this.scenario = scenario;
	}

	@Override
	public Object getChild(Object parent, int index) {
		if (parent instanceof IProxy)
			return ((IProxy) parent).getChildAt(index);
		else if (parent instanceof TreeNode)
			return ((TreeNode) parent).getChildAt(index);
		else
			return null;
	}

	@Override
	public int getChildCount(Object parent) {
		if (parent instanceof IProxy)
			return ((IProxy) parent).getChildCount();
		else if (parent instanceof TreeNode)
			return ((TreeNode) parent).getChildCount();
		else
			return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if (parent == null || child == null)
			return -1;
		else {
			int num = getChildCount(parent);

			for (int i = 0; i < num; i++)
				if (getChild(parent, i).equals(child))
					return i;

			return -1;
		}
	}

	@Override
	public Object getRoot() {
		return root;
	}

	@Override
	public boolean isLeaf(Object node) {
		return getChildCount(node) == 0;
	}

	// listeners not used

	@Override
	public void addTreeModelListener(TreeModelListener arg0) {
	}

	@Override
	public void removeTreeModelListener(TreeModelListener arg0) {
	}

	@Override
	public void valueForPathChanged(TreePath arg0, Object arg1) {
	}
}
