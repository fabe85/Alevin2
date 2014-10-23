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
package vnreal.algorithms.basicVN;

import java.util.Collection;

import vnreal.algorithms.utils.energy.linkStressTransformers.SubstrateLinkStressTransformer;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;

/**
 * See
 * "Algorithms for Assigning Substrate Network Resources to Virtual Network Components"
 * .
 * 
 * @author Michael Till Beck
 */
public class BasicVNLinkStressAssignmentAlgorithm extends
		BasicVNAssignmentAlgorithm {
	/**
	 * @param delta_N
	 *            : default = 1
	 * @param delta_L
	 *            : default = 1
	 */
	public BasicVNLinkStressAssignmentAlgorithm(NetworkStack stack,
			double delta_N, double delta_L, boolean useEnergyDemand) {

		super(stack, delta_N, delta_L, useEnergyDemand);
	}

	@Override
	public double getNRValue(SubstrateNetwork sNetwork, SubstrateNode n) {

		int linkS = 0;
		for (SubstrateLink l : sNetwork.getOutEdges(n)) {
			linkS += biggestLinkStress - getStressLevel(sNetwork, n, l);
		}

		return linkS;
	}

	@Override
	public double nodePotential(SubstrateNetwork sNetwork, SubstrateNode v,
			Collection<SubstrateNode> currentlyChosenNodes) {

		SubstrateLinkStressTransformer transformer = new SubstrateLinkStressTransformer(
				this, sNetwork, null, biggestLinkStress, delta_L);

		double sum = 0.0;
		for (SubstrateNode n : currentlyChosenNodes) {
			DistanceEntry distance = getDistance(sNetwork, v, n, transformer);
			if (distance != null) {
				sum += distance.distance;
			}
		}

		return sum;
	}
}
