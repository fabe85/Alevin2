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
package vnreal.algorithms.utils.energy.linkStressTransformers;

import java.util.Collection;

import org.apache.commons.collections15.Transformer;

import vnreal.algorithms.basicVN.BasicVNAssignmentAlgorithm;
import vnreal.algorithms.utils.SubgraphBasicVN.Utils;
import vnreal.demands.AbstractDemand;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;

public class SubstrateLinkStressTransformer implements
		Transformer<SubstrateLink, Double> {

	private final BasicVNAssignmentAlgorithm algorithm;
	private final SubstrateNetwork sNetwork;

	private Collection<AbstractDemand> vldemands;

	private double biggestLinkStress;
	private double delta_L;

	public SubstrateLinkStressTransformer(BasicVNAssignmentAlgorithm algorithm,
			SubstrateNetwork sNetwork, Collection<AbstractDemand> vldemands,
			double biggestLinkStress, double delta_L) {

		this.algorithm = algorithm;
		this.sNetwork = sNetwork;

		this.vldemands = vldemands;

		this.biggestLinkStress = biggestLinkStress;
		this.delta_L = delta_L;
	}

	public Double transform(SubstrateLink l) {
		if (!Utils.fulfills(vldemands, l)) {
			return Double.POSITIVE_INFINITY;
		}

		double stress = algorithm.getStressLevel(sNetwork, sNetwork
				.getEndpoints(l).getFirst(), l);
		if (biggestLinkStress < stress) {
			biggestLinkStress = stress;
		}

		return (1.0 / (biggestLinkStress + delta_L - stress));
	}
}
