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
package vnreal.algorithms.linkmapping;

import java.util.List;
import java.util.Map;

import vnreal.algorithms.AbstractLinkMapping;
import vnreal.algorithms.utils.EppsteinAlgorithm;
import vnreal.algorithms.utils.LinkWeight;
import vnreal.algorithms.utils.NodeLinkAssignation;
import vnreal.demands.AbstractDemand;
import vnreal.demands.BandwidthDemand;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

public class kShortestPathLinkMapping extends AbstractLinkMapping {

	// Number of shortest paths
	private int k;

	/**
	 * 
	 * @param sNet
	 *            Substrate Network
	 * @param k
	 *            Number of shortest paths
	 */
	public kShortestPathLinkMapping(SubstrateNetwork sNet, int k) {
		super(sNet);
		this.k = k;
	}

	/**
	 * Algorithm with the k-Shortest path implementation of virtual link mapping
	 * (hops is taken as the metric)
	 */
	@Override
	protected boolean linkMapping(VirtualNetwork vNet,
			Map<VirtualNode, SubstrateNode> nodeMapping) {
		this.processedLinks = 0;
		this.mappedLinks = 0;

		// Search for path in filtered substrate using KShortestPaths
		LinkWeight linkWeight = new LinkWeight();
		EppsteinAlgorithm kshortestPaths = new EppsteinAlgorithm(sNet,
				linkWeight);

		// Iterate all VirtualLinks on the current VirtualNetwork
		for (VirtualLink tVLink : vNet.getEdges()) {
			processedLinks++; // increase number of processed.

			// Find the source and destiny of the current VirtualLink (tmpl)
			VirtualNode srcVnode = vNet.getSource(tVLink);
			VirtualNode dstVnode = vNet.getDest(tVLink);

			// Find their mapped SubstrateNodes
			final SubstrateNode sNode = nodeMapping.get(srcVnode);
			final SubstrateNode dNode = nodeMapping.get(dstVnode);

			// Get current VirtualLink demand
			for (AbstractDemand dem : tVLink) {
				if (dem instanceof BandwidthDemand) {
					// System.out.println("   DemandedBandwidth: "
					// + bwDem.getDemandedBandwidth());
				}
			}

			// get the k shortest paths to the dstSnode in increasing order of
			// weight
			List<List<SubstrateLink>> paths = kshortestPaths.getShortestPaths(
					sNode, dNode, k);
			int ind2 = 0;

			/*
			 * If source and destination substrate node are different, in any
			 * other case, the virtual link demand will not be mapped because
			 * the link is created between the same node.
			 */
			if (!sNode.equals(dNode)) {
				for (List<SubstrateLink> path : paths) {

					// Verify if the path fulfills the demand
					if (!NodeLinkAssignation.verifyPath(tVLink, path, sNode,
							sNet)) {

						// Current path cannot fulfill the VirtualLink or the
						// Hidden Hop Demand... Lets check another
						ind2 = 1;
					} else {
						// Perform virtual link mapping (VLM) for each link in
						// the path.
						if (!NodeLinkAssignation.vlm(tVLink, path, sNet, sNode))
							throw new AssertionError("But we checked before!");

						mappedLinks++;
						ind2 = 0;
						break;
					}
				}
				// if there are no paths fulfilling the demand.
				if (ind2 == 1) {
					processedLinks = vNet.getEdges().size();
					return false;
				}
			} else {
				// FIXME Hidden hops demand will be applied?
			}
		}
		return true;
	}
}
