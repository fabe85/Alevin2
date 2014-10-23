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
package vnreal.algorithms.energy.optimal;

import java.util.Collection;
import java.util.Comparator;

import vnreal.algorithms.utils.SubgraphBasicVN.ResourceDemandEntry;
import vnreal.demands.PowerDemand;
import vnreal.resources.PowerResource;

public class EnergyResourceChecker implements
		Comparator<OptimalMappingAlgorithmMappingResult> {

	private Collection<ResourceDemandEntry<PowerResource, PowerDemand>> best = null;

	public EnergyResourceChecker() {
	}

	public void setBest(
			Collection<ResourceDemandEntry<PowerResource, PowerDemand>> m) {
		this.best = m;
	}

	public boolean check(
			Collection<ResourceDemandEntry<PowerResource, PowerDemand>> m) {
		return best == null || m.size() < best.size();
	}

	public Collection<ResourceDemandEntry<PowerResource, PowerDemand>> getBest() {
		return best;
	}

	public int compare(OptimalMappingAlgorithmMappingResult o1,
			OptimalMappingAlgorithmMappingResult o2) {

		if (o1.resources.size() < o2.resources.size()) {
			return -1;
		}
		if (o1.resources.size() > o2.resources.size()) {
			return 1;
		}

		return 0;
	}

}
