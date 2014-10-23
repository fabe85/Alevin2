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

import vnreal.ExchangeParameter;
import vnreal.algorithms.utils.MiscelFunctions;
import vnreal.constraints.AbstractConstraint;
import vnreal.constraints.INodeConstraint;
import vnreal.demands.AbstractDemand;
import vnreal.demands.CpuDemand;
import vnreal.demands.DemandVisitorAdapter;
import vnreal.mapping.Mapping;
import vnreal.network.NetworkEntity;
import vnreal.network.Node;

/**
 * A resource for CPU cycles.
 * 
 * N.b.: This resource is applicable for nodes only.
 * 
 * @author Michael Duelli
 * @since 2010-09-10
 */
public final class CpuResource extends AbstractResource implements
		INodeConstraint {
	private double cycles;
	private double occupiedCycles = 0;

	public CpuResource(Node<? extends AbstractConstraint> owner) {
		super(owner);
	}
	
	public CpuResource(Node<? extends AbstractConstraint> owner, String name) {
		super(owner, name);
	}

	@ExchangeParameter
	public void setCycles(Double cycles) {
		this.cycles = cycles;
	}

	@ExchangeParameter
	public Double getCycles() {
		return this.cycles;
	}

	/*
	 * Method for the distributed algorithm
	 */
	public void setOccupiedCycles(Double occupiedCycles) {
		this.occupiedCycles = occupiedCycles;
	}

	public Double getAvailableCycles() {
		return MiscelFunctions.roundThreeDecimals(cycles - occupiedCycles);
	}

	@Override
	public boolean accepts(AbstractDemand dem) {
		return dem.getAcceptsVisitor().visit(this);
	}

	@Override
	public boolean fulfills(AbstractDemand dem) {
		return dem.getFulfillsVisitor().visit(this);
	}

	@Override
	protected DemandVisitorAdapter createOccupyVisitor() {
		return new DemandVisitorAdapter() {
			@Override
			public boolean visit(CpuDemand dem) {
				if (fulfills(dem)) {
					occupiedCycles += MiscelFunctions.roundThreeDecimals(dem
							.getDemandedCycles());
					new Mapping(dem, getThis());
					return true;
				} else
					return false;
			}
		};
	}

	@Override
	protected DemandVisitorAdapter createFreeVisitor() {
		return new DemandVisitorAdapter() {
			@Override
			public boolean visit(CpuDemand dem) {
				if (getMapping(dem) != null) {
					occupiedCycles -= MiscelFunctions.roundThreeDecimals(dem
							.getDemandedCycles());
					return getMapping(dem).unregister();
				} else
					return false;
			}
		};
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CpuResource: cycles=");
		sb.append(getCycles());
		sb.append(" occupiedCycles=");
		sb.append(occupiedCycles);
		if (getMappings().size() > 0)
			sb.append(getMappingsString());
		return sb.toString();
	}

	@Override
	public AbstractResource getCopy(
			NetworkEntity<? extends AbstractConstraint> owner) {

		CpuResource clone = new CpuResource(
				(Node<? extends AbstractConstraint>) owner, this.getName());
		clone.cycles = cycles;
		clone.occupiedCycles = occupiedCycles;

		return clone;
	}
}
