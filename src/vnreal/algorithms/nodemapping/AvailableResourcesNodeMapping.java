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
package vnreal.algorithms.nodemapping;

/**
 * This class implements the node mapping based on a greedy algorithm, 
 * taking into account the available resources of a node.
 * 
 * 
 * This node mapping algorithm was proposed in:
 *  
 * -  Minlan Yu, Yung Yi, Jennifer Rexford, and Mung Chiang. Rethinking
 *    virtual network embedding: Substrate support for path splitting and 
 *    migration. ACM SIGCOMM CCR, 38(2):17–29, April 2008.
 * 
 * @author Juan Felipe Botero
 * @author Lisset Diaz
 * @since 2010-10-15
 */

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import vnreal.algorithms.AbstractNodeMapping;
import vnreal.algorithms.utils.NodeLinkAssignation;
import vnreal.demands.AbstractDemand;
import vnreal.demands.CpuDemand;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;
import vnreal.resources.BandwidthResource;
import vnreal.resources.CpuResource;

public class AvailableResourcesNodeMapping extends AbstractNodeMapping {
	int distance;
	boolean withDist;

	/**
	 * Constructor of the algorithm
	 * 
	 * @param sNet
	 *            substrate Network
	 * @param distance
	 * @param withDistance
	 *            Indicates if the distance will be taken into account to chose
	 *            the candidate substrate nodes
	 * @param nodesOverload
	 *            Indicatesif the fact that more that one node in one VNR are
	 *            mapped to the same node in the SN
	 */
	public AvailableResourcesNodeMapping(SubstrateNetwork sNet, int distance,
			boolean withDistance, boolean nodesOverload) {
		super(sNet, nodesOverload);
		this.distance = distance;
		this.withDist = withDistance;
	}

	/**
	 * Node mapping method implementation
	 */
	@Override
	protected boolean nodeMapping(VirtualNetwork vNet) {
		List<SubstrateNode> unmappedSnodes = super.getUnmappedsNodes();
		List<SubstrateNode> allNodes = new LinkedList<SubstrateNode>(
				sNet.getVertices());
		SubstrateNode mappedSnode = null;

		VirtualNode currVnode;
		List<SubstrateNode> candidates = new LinkedList<SubstrateNode>();

		// Move through the virtual nodes
		for (Iterator<VirtualNode> itt = vNet.getVertices().iterator(); itt
				.hasNext();) {
			currVnode = itt.next();
			// Find the candidate substrate nodes accomplishing the virtual node
			// demands
			for (AbstractDemand dem : currVnode) {
				if (dem instanceof CpuDemand) {
					if (!nodeOverload) {
						if (!withDist) {
							candidates = findFulfillingNodes(dem,
									unmappedSnodes);
						} else {
							candidates = findFulfillingNodes(currVnode, dem,
									unmappedSnodes, distance);
						}
					} else {
						if (!withDist) {
							candidates = findFulfillingNodes(dem, allNodes);
						} else {
							candidates = findFulfillingNodes(currVnode, dem,
									allNodes, distance);
						}
					}

				}
				// Find the feasible node with greatest available resources
				// The available resources are the H value defined in the
				// "Rethinking virtual network embedding" paper

				mappedSnode = bestAvailableResources(candidates);
				if (mappedSnode != null) {
					// Realize the node mapping
					if (NodeLinkAssignation.vnm(currVnode, mappedSnode)) {
						nodeMapping.put(currVnode, mappedSnode);
					} else {
						// If this exception is thrown, it means that something
						// in the code is wrong. The feasibility of nodes is
						// checked in findFulfillingNodes

						throw new AssertionError("But we checked before!");
					}
				} else {
					// Mapping is not possible, not feasible nodes
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @param dem
	 *            virtual node demand
	 * @param filtratedsNodes
	 *            set of all nodes to extract the feasible substrate node
	 *            candidates
	 * @return The set of nodes accomplishing a demand dem
	 */
	private List<SubstrateNode> findFulfillingNodes(AbstractDemand dem,
			List<SubstrateNode> filtratedsNodes) {
		List<SubstrateNode> nodes = new LinkedList<SubstrateNode>();
		for (SubstrateNode n : filtratedsNodes) {
			for (AbstractResource res : n)
				if ((res instanceof CpuResource) && res.accepts(dem)
						&& res.fulfills(dem)) {
					nodes.add(n);
					break;
				}
		}
		return nodes;
	}

	/**
	 * 
	 * @param candidates
	 *            feasible substrate node candidates
	 * @return substrate node among candidates with the highest available
	 *         resources value
	 */
	private SubstrateNode bestAvailableResources(List<SubstrateNode> candidates) {
		double resCPU = 0;
		double resLink = 0;
		double greatAr = 0;
		SubstrateNode chosenNode = null;
		for (Iterator<SubstrateNode> sn = candidates.iterator(); sn.hasNext();) {
			SubstrateNode tmp = sn.next();
			for (AbstractResource res : tmp) {
				if (res instanceof CpuResource) {
					resCPU = ((CpuResource) res).getAvailableCycles();
					resLink = calcLinksRes(tmp);
					if (!nodeOverload) {
						if ((resCPU * resLink >= greatAr)
								&& !nodeMapping.containsValue(tmp)) {
							greatAr = resCPU * resLink;
							chosenNode = tmp;
						}
					} else {
						if (resCPU * resLink >= greatAr) {
							greatAr = resCPU * resLink;
							chosenNode = tmp;
						}
					}

				}
			}
		}
		return chosenNode;
	}

	/**
	 * 
	 * @param sn
	 *            Substrate node
	 * @return The available resources of the node sn
	 */
	private double calcLinksRes(SubstrateNode sn) {
		SubstrateNode srcSnode = null;
		double resBW = 0;
		double total_resBW = 0;

		for (SubstrateLink sl : sNet.getEdges()) {
			srcSnode = sNet.getSource(sl);
			// If the processing SubstrateNode is equals to the source of a
			// SubstrateLink then add the link
			if (sn.equals(srcSnode)) {
				for (AbstractResource res : sl) {
					if (res instanceof BandwidthResource) {
						resBW = ((BandwidthResource) res)
								.getAvailableBandwidth();
						total_resBW += resBW;
					}
				}

			}
		}
		return total_resBW;
	}

	/**
	 * 
	 * @param vNode
	 *            virtual node
	 * @param dem
	 *            demand of the virtual node
	 * @param filtratedsNodes
	 *            set of all nodes to extract the feasible substrate node
	 *            candidates
	 * @param dist
	 *            parameter to assure that the candidate nodes will be separated
	 *            of virtual node by a distance of, at maximum, equal to dist.
	 * @return The set of substrate nodes accomplishing a demand and a distance
	 *         dist from the vnode
	 */
	private List<SubstrateNode> findFulfillingNodes(VirtualNode vNode,
			AbstractDemand dem, List<SubstrateNode> filtratedsNodes, int dist) {
		List<SubstrateNode> nodes = new LinkedList<SubstrateNode>();
		for (SubstrateNode n : filtratedsNodes) {
			for (AbstractResource res : n)
				if ((res instanceof CpuResource) && res.accepts(dem)
						&& res.fulfills(dem) && nodeDistance(vNode, n, dist)) {
					nodes.add(n);
					break;
				}
		}
		return nodes;
	}

	/**
	 * 
	 * @param vNode
	 *            virtual node
	 * @param sNode
	 *            substrate node
	 * @param distance
	 * @return true if vNode is separated from sNode by a value lower than
	 *         "distance"
	 */
	private boolean nodeDistance(VirtualNode vNode, SubstrateNode sNode,
			int distance) {
		double dis;
		dis = Math.pow(sNode.getCoordinateX() - vNode.getCoordinateX(), 2)
				+ Math.pow(sNode.getCoordinateY() - vNode.getCoordinateY(), 2);
		if (Math.sqrt(dis) <= distance) {
			return true;
		} else {
			return false;
		}
	}

}
