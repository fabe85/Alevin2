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
package vnreal.resources;

import vnreal.AdditionalConstructParameter;
import vnreal.algorithms.utils.SubgraphBasicVN.Utils;
import vnreal.constraints.AbstractConstraint;
import vnreal.constraints.INodeConstraint;
import vnreal.network.NetworkEntity;
import vnreal.network.Node;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualNode;

@AdditionalConstructParameter(
		parameterNames = {"baseConsumption", "factor"},
		parameterGetters = {"getIdleConsumption", "getFactor"}
		)
public class LinearCpuEnergyResource extends PowerResource implements
		INodeConstraint {

	private int baseConsumption;
	private int factor;


	public LinearCpuEnergyResource(Node<? extends AbstractConstraint> owner,
			Integer baseConsumption, Integer factor) {

		super(owner);
		this.baseConsumption = baseConsumption;
		this.factor = factor;
	}
	
	public LinearCpuEnergyResource(Node<? extends AbstractConstraint> owner,
			int baseConsumption, int factor, String name) {

		super(owner, name);
		this.baseConsumption = baseConsumption;
	}

	@Override
	public Integer getIdleConsumption() {
		return baseConsumption;
	}

	
	
	public int getFactor() {
		return factor;
	}

	@Override
	public Integer getCurrentConsumption() {
		if (!isUsed()) {
			return 0;
		}
		
		CpuResource cpu = Utils.getCpuResource((SubstrateNode) getOwner());
		int cpuusage = (int) ((cpu.getCycles() - cpu.getAvailableCycles()) / cpu.getCycles());
		int result = baseConsumption + factor * cpuusage;
		return result;
	}

	@Override
	public Integer getConsumptionAfterMapping(VirtualNode vn) {
		CpuResource cpu = Utils.getCpuResource((SubstrateNode) getOwner());
		double demandedCycles = Utils.getCpuDemand(vn).getDemandedCycles();
		if (cpu.getAvailableCycles() < demandedCycles) {
			demandedCycles = cpu.getAvailableCycles();
		}

		int cpuusage = (int) ((cpu.getCycles() - cpu.getAvailableCycles() + demandedCycles) / cpu.getCycles());
		int result = baseConsumption + factor * cpuusage;
		return result;
	}

	@Override
	public String toString() {
		return "[" + super.toString()
				+ ";LinearCpuEnergyResource (" + baseConsumption + ":" + factor + ")]";
	}

	@Override
	public AbstractResource getCopy(
			NetworkEntity<? extends AbstractConstraint> owner) {

		LinearCpuEnergyResource clone = new LinearCpuEnergyResource(
				(Node<? extends AbstractConstraint>) owner, baseConsumption, factor, this.getName());
		clone.usedBy = usedBy;

		return clone;
	}
}
