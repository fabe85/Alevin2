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

import java.util.Iterator;
import java.util.List;

import mulavito.algorithms.AbstractAlgorithmStatus;
import vnreal.algorithms.AbstractAlgorithm;
import vnreal.algorithms.isomorphism.SubgraphIsomorphismAlgorithm.MappingCandidate;
import vnreal.algorithms.utils.SubgraphBasicVN.Mapping;
import vnreal.network.Network;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

/**
 * See
 * "A Virtual Network Mapping Algorithm based on Subgraph Isomorphism Detection"
 * .
 * 
 * epsilon ~ 10 omega ~ 4 * |VN|
 * 
 * @author Michael Till Beck
 */
public class SubgraphIsomorphismStackAlgorithm extends AbstractAlgorithm {
	public static final boolean debug = false;

	private SubgraphIsomorphismAlgorithm algorithm;

	protected SubstrateNetwork sNetwork = null;
	private Iterator<VirtualNetwork> curIt = null;
	private Iterator<? extends Network<?, ?, ?>> curNetIt = null;

	public SubgraphIsomorphismStackAlgorithm(NetworkStack stack,
			SubgraphIsomorphismAlgorithm algorithm) {

		this.sNetwork = stack.getSubstrate();
		this.curNetIt = stack.iterator();

		this.algorithm = algorithm;
	}

	@SuppressWarnings("unchecked")
	protected boolean hasNext() {
		if (curIt == null || !curIt.hasNext()) {
			if (curNetIt.hasNext()) {
				@SuppressWarnings("unused")
				Network<?, ?, ?> tmp = curNetIt.next();

				curIt = (Iterator<VirtualNetwork>) curNetIt;
				return hasNext();
			} else
				return false;
		} else
			return true;
	}

	protected VirtualNetwork getNext() {
		if (!hasNext())
			return null;
		else {
			return curIt.next();
		}
	}

	@Override
	protected void evaluate() {
		boolean result = false;
		// Mapping previousResult = new Mapping();

		while (hasNext()) {
			VirtualNetwork vNetwork = getNext();

			result = algorithm.mapNetwork(sNetwork, vNetwork);

			// if (result == null) {
			// previousResult.freeAllResources();
			// break;
			// } else {
			// previousResult = result;
			// }

		}

		if (result) {
			if (debug) {
				System.out.println("result found!");
			}
		} else {
			if (debug) {
				System.out.println("no result found!");
			}
		}
	}

	@Override
	protected void postRun() {
	}

	@Override
	protected boolean preRun() {
		return true;
	}

	@Override
	public List<AbstractAlgorithmStatus> getStati() {
		return null;
	}

	static class MappingState {
		Mapping m;
		MappingCandidate<VirtualNode, SubstrateNode> c;

		public MappingState(Mapping m,
				MappingCandidate<VirtualNode, SubstrateNode> c) {
			this.m = m;
			this.c = c;
		}
	}

}
