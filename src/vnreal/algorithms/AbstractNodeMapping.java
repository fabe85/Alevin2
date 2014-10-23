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
package vnreal.algorithms;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import vnreal.algorithms.utils.NodeLinkAssignation;
import vnreal.demands.AbstractDemand;
import vnreal.demands.IdDemand;
import vnreal.hiddenhopmapping.IHiddenHopMapping;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;
import vnreal.resources.IdResource;

/**
 * Basic class for the node mapping algorithm phases.
 * 
 * @author Juan Felipe Botero
 * @since 2010-12-20
 * 
 */
public abstract class AbstractNodeMapping {
	protected Map<VirtualNode, SubstrateNode> nodeMapping;
	protected SubstrateNetwork sNet;
	private List<VirtualNode> unmappedvNodes;
	private List<SubstrateNode> unmappedsNodes;
	protected boolean nodeOverload;
	protected List<IHiddenHopMapping> hhMappings;

	protected void setHhMappings(List<IHiddenHopMapping> hhMappings) {
		this.hhMappings = hhMappings;
	}

	/**
	 * The constructor of the class initializes its variables
	 * 
	 * @param sNet
	 *            is the substrate network over which, the mapping will be
	 *            performed.
	 * 
	 * @param unmappedvNodes
	 *            is the variable that will have the virtual nodes that are not
	 *            mapped after the prenode mapping stage.
	 * 
	 * @param unmappedsNodes
	 *            is the variable that will have the substrate nodes that are
	 *            not mapped after the prenode mapping stage.
	 * 
	 */
	protected AbstractNodeMapping(SubstrateNetwork sNet,
			boolean subsNodeOverload) {
		nodeMapping = new LinkedHashMap<VirtualNode, SubstrateNode>();
		this.sNet = sNet;
		unmappedvNodes = null;
		unmappedsNodes = null;
		nodeOverload = subsNodeOverload;
	}

	/**
	 * This method realizes the prenode mapping stage. This stage consists in
	 * the mapping of virtual nodes requested by their idDemand. Nodes with
	 * idDemand should be mapped to nodes with the same idResource.
	 * 
	 * @param vNet
	 * @return
	 */
	public boolean isPreNodeMappingFeasible(VirtualNetwork vNet) {
		nodeMapping.clear();

		List<VirtualNode> mappedVnodes = new LinkedList<VirtualNode>();
		List<SubstrateNode> mappedSnodes = new LinkedList<SubstrateNode>();

		// In this loop the virtual nodes with ID demand are analyzed
		for (Iterator<VirtualNode> vn = vNet.getVertices().iterator(); vn
				.hasNext();) {
			VirtualNode tmp = vn.next();
			for (AbstractDemand dem : tmp) {
				if (dem instanceof IdDemand) {
					SubstrateNode sNode = findDemandedNode(dem);
					if (sNode == null) {
						// FIXME: How to manage this kind of errors (Create a
						// log file?)
						return false;
					} else if (verifyDemand(sNode, tmp)) {
						if (NodeLinkAssignation.vnm(tmp, sNode)) {
							nodeMapping.put(tmp, sNode);
						} else {
							throw new AssertionError("But we checked before!");
						}

						mappedVnodes.add(tmp);
						mappedSnodes.add(sNode);
					} else {
						// FIXME: How to manage this kind of errors (Create a
						// log file?)
						return false;
					}
				}
			}
		}
		unmappedvNodes = new LinkedList<VirtualNode>();
		unmappedsNodes = new LinkedList<SubstrateNode>();
		for (VirtualNode vn : vNet.getVertices()) {
			if (!mappedVnodes.contains(vn)) {
				unmappedvNodes.add(vn);
			}
		}
		for (SubstrateNode sn : sNet.getVertices()) {
			if (!mappedSnodes.contains(sn)) {
				unmappedsNodes.add(sn);
			}
		}
		return true;
	}

	/**
	 * 
	 * @return a boolean value indicating whether the prenode Mapping phase has
	 *         mapped all the virtual network nodes
	 */
	public boolean isPreNodeMappingComplete() {
		return unmappedvNodes.isEmpty();
	}

	/**
	 * This method should be implemented by the node mapping algorithms it will
	 * have all the logic of the node mapping algorithm.
	 * 
	 * @param vNet
	 *            is the Virtual network request that should be mapped
	 * @return true if the node mapping was successfully performed and false
	 *         otherwise.
	 */
	protected abstract boolean nodeMapping(VirtualNetwork vNet);

	protected Map<VirtualNode, SubstrateNode> getNodeMapping() {
		return nodeMapping;
	}

	protected List<VirtualNode> getUnmappedvNodes() {
		return unmappedvNodes;
	}

	protected List<SubstrateNode> getUnmappedsNodes() {
		return unmappedsNodes;
	}

	/**
	 * 
	 * @param IDdem
	 * @param tmp
	 * @return the substrate node with the IDresource demanded by the virtual
	 *         node tmp
	 */
	private SubstrateNode findDemandedNode(AbstractDemand IDdem) {
		SubstrateNode sNode = new SubstrateNode();
		String resID = Integer.toString(0);
		String demID = Integer.toString(0);
		for (SubstrateNode n : sNet.getVertices()) {
			for (AbstractResource res : n) {
				if (res instanceof IdResource) {
					resID = ((IdResource) res).getId();
					demID = ((IdDemand) IDdem).getDemandedId();
					if (resID.equals(demID))
						sNode = n;
				}
			}
		}
		return sNode;
	}

	/**
	 * 
	 * @param sNode
	 * @param vNode
	 * @return a boolean value indicating if the demands of virtual node vNode
	 *         can be accomplished by the substrate node sNode
	 */
	private boolean verifyDemand(SubstrateNode sNode, VirtualNode vNode) {
		boolean answer;
		boolean returnValue = true;
		for (AbstractDemand dem : vNode) {
			answer = false;
			for (AbstractResource res : sNode) {
				if (res.accepts(dem) && res.fulfills(dem)) {
					answer = true;
				}
			}
			if (!answer)
				returnValue = false;
		}
		return returnValue;
	}
	
	/**
	 * This Method writes evaluationData into the {@link NetworkStack}
	 * @return Map of String (name of the field) and Double (value), null if not used
	 */
	public Map<String, Double> exportEvaluationData() {
		//default does nothing
		return null;
	}
}
