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
package vnreal.algorithms.utils.SubgraphBasicVN;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import vnreal.demands.AbstractDemand;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;

/**
 * TODO: implement remove methods so we don't have to use the copy constructor
 * as often
 */
public class Mapping {

	private HashMap<SubstrateNode, Collection<VirtualNode>> nodeMappings;
	private HashMap<VirtualNode, SubstrateNode> nodes;
	private HashMap<List<SubstrateLink>, Collection<VirtualLink>> linkMappings;
	private HashMap<VirtualLink, List<SubstrateLink>> links;
	private int mappedVNodes = 0;
	private int mappedVLinks = 0;
	private boolean useEnergyDemand;

	private Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> mappings;

	// TODO: implement hashCode in NetworkEntity class
	public Mapping(boolean useEnergyDemand) {
		this.useEnergyDemand = useEnergyDemand;
		clear();
	}

	public Mapping(Mapping copy) {
		copy.useEnergyDemand = useEnergyDemand;

		nodeMappings = new HashMap<SubstrateNode, Collection<VirtualNode>>();
		for (Entry<SubstrateNode, Collection<VirtualNode>> e : copy.nodeMappings
				.entrySet()) {
			nodeMappings.put(e.getKey(),
					new LinkedList<VirtualNode>(e.getValue()));
		}

		nodes = new HashMap<VirtualNode, SubstrateNode>(copy.nodes);

		linkMappings = new HashMap<List<SubstrateLink>, Collection<VirtualLink>>();
		for (Entry<List<SubstrateLink>, Collection<VirtualLink>> e : copy.linkMappings
				.entrySet()) {
			linkMappings.put(new LinkedList<SubstrateLink>(e.getKey()),
					new LinkedList<VirtualLink>(e.getValue()));
		}

		links = new HashMap<VirtualLink, List<SubstrateLink>>();
		for (Entry<VirtualLink, List<SubstrateLink>> e : copy.links.entrySet()) {
			links.put(e.getKey(), new LinkedList<SubstrateLink>(e.getValue()));
		}

		mappedVNodes = copy.mappedVNodes;
		mappedVLinks = copy.mappedVLinks;

		mappings = new LinkedList<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>>(
				copy.mappings);
	}

	public void clear() {
		nodeMappings = new HashMap<SubstrateNode, Collection<VirtualNode>>();
		nodes = new HashMap<VirtualNode, SubstrateNode>();
		linkMappings = new HashMap<List<SubstrateLink>, Collection<VirtualLink>>();
		links = new HashMap<VirtualLink, List<SubstrateLink>>();

		mappedVNodes = 0;
		mappedVLinks = 0;

		mappings = new LinkedList<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>>();
	}

	public Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> occupyAllResources(
			SubstrateNetwork sNetwork) {
		for (Entry<VirtualNode, SubstrateNode> e : nodes.entrySet()) {
			mappings.addAll(Utils.occupyResources(e.getKey().get(), e
					.getValue().get()));
		}
		for (Entry<VirtualLink, List<SubstrateLink>> e : links.entrySet()) {
			mappings.addAll(Utils.occupyPathResources(e.getKey(), e.getValue(),
					sNetwork, useEnergyDemand));
		}
		return new LinkedList<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>>(
				mappings);
	}

	public void freeAllResources() {
		Utils.freeResources(mappings);
		mappings = new LinkedList<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>>();
	}

	public boolean isEmpty() {
		return mappedVLinks == 0 && mappedVNodes == 0;
	}

	public void add(VirtualNode vn, SubstrateNode sn) {
		Collection<VirtualNode> list = nodeMappings.get(sn);
		if (list == null) {
			list = new LinkedList<VirtualNode>();
			nodeMappings.put(sn, list);
		}
		list.add(vn);
		nodes.put(vn, sn);
		mappedVNodes++;
	}

	public void add(VirtualLink vl, List<SubstrateLink> sl) {
		Collection<VirtualLink> list = linkMappings.get(sl);
		if (list == null) {
			list = new LinkedList<VirtualLink>();
			linkMappings.put(sl, list);
		}
		list.add(vl);
		links.put(vl, sl);
		mappedVLinks++;
	}

	public void add(VirtualLink vl, SubstrateLink sl) {
		List<SubstrateLink> path = new LinkedList<SubstrateLink>();
		path.add(sl);
		add(vl, path);
	}

	public HashMap<SubstrateNode, Collection<VirtualNode>> getNodeMappings() {
		return nodeMappings;
	}

	public HashMap<List<SubstrateLink>, Collection<VirtualLink>> getLinkMappings() {
		return linkMappings;
	}

	public HashMap<VirtualNode, SubstrateNode> getNodeEntries() {
		return nodes;
	}

	public Collection<VirtualLink> getVirtualLinks(SubstrateLink l) {
		return linkMappings.get(l);
	}

	public HashMap<VirtualLink, List<SubstrateLink>> getLinkEntries() {
		return links;
	}

	public Collection<VirtualNode> getVirtualNodes(SubstrateNode n) {
		return nodeMappings.get(n);
	}

	public SubstrateNode getSubstrateNode(VirtualNode n) {
		return nodes.get(n);
	}

	public List<SubstrateLink> getSubstratePath(VirtualLink l) {
		return links.get(l);
	}

	public boolean isMapped(VirtualNode n) {
		return nodes.containsKey(n);
	}

	public boolean isMapped(VirtualLink l) {
		return links.containsKey(l);
	}

	public int getMappedVNodesCount() {
		return mappedVNodes;
	}

	public int getMappedVLinksCount() {
		return mappedVLinks;
	}

	public void setMappings(
			Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> mappings) {
		if (mappings == null || !this.mappings.isEmpty()) {
			throw new AssertionError();
		}
		this.mappings = mappings;
	}

	public String toString() {
		return "node mapping: ==============\n" + nodeMappings.toString()
				+ "\n" + "link mapping: ==============\n"
				+ linkMappings.toString() + "\n";
	}

}
