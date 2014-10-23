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
package vnreal.algorithms.isomorphism;

import vnreal.algorithms.utils.SubgraphBasicVN.Mapping;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.virtual.VirtualNetwork;

/**
 * see
 * "A Virtual Network Mapping Algorithm based on Subgraph Isomorphism Detection"
 * , advanced approach.
 * 
 * @author Michael Till Beck
 */
public class AdvancedSubgraphIsomorphismAlgorithm extends
		SubgraphIsomorphismAlgorithm {
	private int min;
	private int max;
	private int stepsize;

	/**
	 * 
	 * @param useEnergyResource
	 *            True, if EnergyResource should be considered, otherwise false
	 */
	public AdvancedSubgraphIsomorphismAlgorithm(boolean useEnergyResource) {
		this(1, 10, 1, useEnergyResource); // as described in paper
	}

	public AdvancedSubgraphIsomorphismAlgorithm(int min, int max, int stepsize,
			boolean useEnergyResource) {

		super(useEnergyResource);

		if (stepsize <= 0) {
			throw new IllegalArgumentException();
		}

		this.min = min;
		this.max = max;
		this.stepsize = stepsize;
	}

	@Override
	public boolean mapNetwork(SubstrateNetwork sNetwork, VirtualNetwork vNetwork) {

		int omega = 4 * vNetwork.getVertexCount();
		return mapNetwork(sNetwork, vNetwork, omega);
	}

	boolean mapNetwork(SubstrateNetwork sNetwork, VirtualNetwork vNetwork,
			int omega) {

		for (int epsilon = min; epsilon <= max; epsilon += stepsize) {
			Mapping result = mapNetwork(sNetwork, vNetwork, epsilon, omega);

			if (result != null) {
				return true;
			}

		}

		return false;
	}
}
