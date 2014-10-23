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
package vnreal.evaluations.metrics;

/**
 * Class to obtain the cost incurred in the substrate network 
 * after performing a mapping over a set of VNRs.
 * 
 * The cost of realizing the embedding of one virtual network request 
 * over a substrate network is defined by the next equation (Latex format)
 * (Notation in http://www3.informatik.uni-wuerzburg.de/research/projects/vnreal/wiki/doku.php?id=private:nomenclature):
 * 
 * cost(G^k(V^k,A^k))= \sum_{i^k \in V^k} ND_{cpu}(i^k) + \sum_{(i,j) \in A}\sum_{(i^k,j^k) \in A^k} \rho_{i^k,j^k}(i,j) LD_{bw}(i^k,j^k)  
 * 
 * where ND_{cpu}(i^k) is the demand of CPU in the node i^k and LD_bw(i^k,j^k) is the 
 * link demand of virtual link (i^k,j^k) belonging to the VNR k. The total cost is 
 * the sum of the cost incurred by the SN to map all the VNRs.
 *  
 * The cost can be defined as the resources spent in the substrate network to embed the virtual network request.
 * 
 * @author Juan Felipe Botero
 * @since 2011-03-08
 *
 */
import java.util.Iterator;

import vnreal.demands.BandwidthDemand;
import vnreal.demands.CpuDemand;
import vnreal.mapping.Mapping;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNode;
import vnreal.resources.AbstractResource;
import vnreal.resources.BandwidthResource;
import vnreal.resources.CpuResource;

public class Cost extends AbstractEvaluation {

	@Override
	public double calculate() {
		return (calculateCost());
	}

	/**
	 * 
	 * @return the cost of incurred in the susbtrate network after performing a
	 *         mapping over a set of VNRs
	 */
	public double calculateCost() {
		double nodeCost = 0;
		double linkCost = 0;
		CpuDemand tmpCpuDem;
		BandwidthDemand tmpBwDem;
		for (Iterator<SubstrateLink> tmpSLink = stack.getSubstrate().getEdges()
				.iterator(); tmpSLink.hasNext();) {
			SubstrateLink currSLink = tmpSLink.next();
			for (AbstractResource res : currSLink) {
				if (res instanceof BandwidthResource) {
					for (Mapping f : res.getMappings()) {
						tmpBwDem = (BandwidthDemand) f.getDemand();
						linkCost += tmpBwDem.getDemandedBandwidth();
					}
				}
			}
		}
		for (Iterator<SubstrateNode> tmpNode = stack.getSubstrate()
				.getVertices().iterator(); tmpNode.hasNext();) {
			SubstrateNode tmps = tmpNode.next();
			for (AbstractResource res : tmps) {
				if (res instanceof CpuResource) {
					for (Mapping f : res.getMappings()) {
						tmpCpuDem = (CpuDemand) f.getDemand();
						nodeCost += tmpCpuDem.getDemandedCycles();
					}
				}
			}
		}
		return (nodeCost + linkCost);
	}

	@Override
	public String toString() {
		return "Cost";
	}

}
