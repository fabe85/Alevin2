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

import java.util.HashMap;
import java.util.LinkedList;

import vnreal.constraints.AbstractConstraint;
import vnreal.demands.AbstractDemand;
import vnreal.mapping.Mapping;
import vnreal.network.NetworkEntity;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.virtual.VirtualLink;
import vnreal.resources.AbstractResource;

public class MaxPathLength extends SimpleEvaluation {
	@Override
	public double calculate() {
		HashMap<VirtualLink, Integer> lengths = new HashMap<VirtualLink, Integer>();

		SubstrateNetwork sNetwork = stack.getSubstrate();
		for (SubstrateLink sl : sNetwork.getEdges()) {
			LinkedList<VirtualLink> visited = new LinkedList<VirtualLink>();

			for (AbstractResource r : sl.get()) {
				for (Mapping m : r.getMappings()) {
					AbstractDemand d = m.getDemand();
					NetworkEntity<? extends AbstractConstraint> e = d
							.getOwner();
					if (e instanceof VirtualLink) {
						VirtualLink vl = (VirtualLink) e;

						if (!visited.contains(vl)) {
							visited.add(vl);

							Integer i = lengths.get(vl);
							if (i == null) {
								i = 1;
							} else {
								i++;
							}
							lengths.put(vl, i);

						}
					}
				}
			}
		}

		double result = 0.0;
		for (Integer i : lengths.values()) {
			if (i > result) {
				result = i;
			}
		}

		return result;
	}

	public String toString() {
		return "MaxPathLength";
	}
}
