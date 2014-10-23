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
 * This is the class of the virtual node embedding algorithm that calculates the VNE
 * based on node ranking 
 * 
 * This algorithm was proposed in:
 * 
 * X. Cheng, S. Su, Z. Zhang, H. Wang, F. Yang, Y. Luo, J. Wang, Virtual network embedding 
 * through topology-aware node ranking, SIGCOMM Comput. Commun. Rev. 41 (2011) 38--47.
 * 
 * @author Juan Felipe Botero
 * @since 2011-02-10
 */

import java.util.Iterator;
import java.util.Map;

import vnreal.algorithms.AbstractNodeMapping;
import vnreal.algorithms.utils.MiscelFunctions;
import vnreal.algorithms.utils.NodeLinkAssignation;
import vnreal.network.Node;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

public class NodeRankingBasedAlgorithm extends AbstractNodeMapping {
	private double epsilon;
	private boolean withDist;
	private int dist;

	public NodeRankingBasedAlgorithm(SubstrateNetwork sNet,
			boolean subsNodeOverload, double epsilon, boolean withDist, int dist) {
		super(sNet, subsNodeOverload);
		this.epsilon = epsilon;
		this.withDist = withDist;
		this.dist = dist;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean nodeMapping(VirtualNetwork vNet) {
		/*
		 * Node ranking calculation for virtual and substrate network
		 * see (Algorithm 1 and 2)
		 * 
		 * X. Cheng, S. Su, Z. Zhang, H. Wang, F. Yang, Y. Luo, J. Wang, Virtual network embedding 
		 * through topology-aware node ranking, SIGCOMM Comput. Commun. Rev. 41 (2011) 38--47.
		 * 
		 */
		Map<Node<?>, Double> SortedNodeNR_i_opt_subs = MiscelFunctions
				.sortByValue(MiscelFunctions.create_NR(sNet, epsilon));
		Map<Node<?>, Double> SortedNodeNR_i_opt_virt = MiscelFunctions
				.sortByValue(MiscelFunctions.create_NR(vNet, epsilon));
		boolean mappingPerformed;

		for (Iterator<Node<?>> vNode = SortedNodeNR_i_opt_virt.keySet()
				.iterator(); vNode.hasNext();) {
			VirtualNode toMapvNode = (VirtualNode) vNode.next();
			mappingPerformed = false;
			if (!nodeMapping.containsKey(toMapvNode)) {
				for (Iterator<Node<?>> sNode = SortedNodeNR_i_opt_subs.keySet()
						.iterator(); sNode.hasNext();) {
					SubstrateNode candidateSnode = (SubstrateNode) sNode.next();
					if (!nodeMapping.containsValue(candidateSnode)) {
						if (withDist) {
							if (NodeLinkAssignation.isMappable(toMapvNode,
									candidateSnode)
									&& MiscelFunctions.nodeDistance(toMapvNode,
											candidateSnode, dist)) {
								if (NodeLinkAssignation.vnm(toMapvNode,
										candidateSnode)) {
									nodeMapping.put(toMapvNode, candidateSnode);
									mappingPerformed = true;
								} else {
									throw new AssertionError(
											"But we checked before!");
								}
							}
						} else {
							if (NodeLinkAssignation.isMappable(toMapvNode,
									candidateSnode)) {
								if (NodeLinkAssignation.vnm(toMapvNode,
										candidateSnode)) {
									nodeMapping.put(toMapvNode, candidateSnode);
									mappingPerformed = true;
									break;
								} else {
									throw new AssertionError(
											"But we checked before!");
								}
							}

						}
					}

				}
			} else {
				mappingPerformed = true;
			}

			if (!mappingPerformed)
				return false;

		}

		return true;
	}

}
