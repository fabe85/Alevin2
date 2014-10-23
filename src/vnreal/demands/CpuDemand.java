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
package vnreal.demands;

import vnreal.ExchangeParameter;
import vnreal.algorithms.utils.MiscelFunctions;
import vnreal.constraints.AbstractConstraint;
import vnreal.constraints.ILinkConstraint;
import vnreal.constraints.INodeConstraint;
import vnreal.network.Link;
import vnreal.network.NetworkEntity;
import vnreal.network.Node;
import vnreal.resources.AbstractResource;
import vnreal.resources.CpuResource;
import vnreal.resources.ResourceVisitorAdapter;

/**
 * A demand for CPU cycles.
 * 
 * N.b.: This demand is applicable for nodes only.
 * 
 * @author Michael Duelli
 * @author Vlad Singeorzan
 * @since 2010-09-10
 */
public final class CpuDemand extends AbstractDemand implements INodeConstraint,
		ILinkConstraint {
	private double demandedCycles;

	public CpuDemand(Node<? extends AbstractConstraint> owner) {
		super(owner);
	}

	public CpuDemand(Link<? extends AbstractConstraint> owner) {
		super(owner);
	}
	
	public CpuDemand(Node<? extends AbstractConstraint> owner, String name) {
		super(owner, name);
	}
	
	public CpuDemand(Link<? extends AbstractConstraint> owner, String name) {
		super(owner, name);
	}


	@ExchangeParameter
	public void setDemandedCycles(Double cycles) {
		this.demandedCycles = cycles;
	}

	@ExchangeParameter
	public Double getDemandedCycles() {
		return demandedCycles;
	}

	@Override
	protected ResourceVisitorAdapter createAcceptsVisitor() {
		return new ResourceVisitorAdapter() {
			@Override
			public boolean visit(CpuResource res) {
				return true;
			}
		};
	}

	@Override
	protected ResourceVisitorAdapter createFulfillsVisitor() {
		return new ResourceVisitorAdapter() {
			@Override
			public boolean visit(CpuResource res) {
				return MiscelFunctions.roundThreeDecimals(res
						.getAvailableCycles()) >= MiscelFunctions
						.roundThreeDecimals(getDemandedCycles());
			}
		};
	}

	@Override
	public boolean occupy(AbstractResource res) {
		return res.getOccupyVisitor().visit(this);
	}

	@Override
	public boolean free(AbstractResource res) {
		return res.getFreeVisitor().visit(this);
	}

	@Override
	public String toString() {
		return new String("CpuDemand: demand" + "ed cycles="
				+ getDemandedCycles());
	}

	@Override
	public AbstractDemand getCopy(NetworkEntity<? extends AbstractDemand> owner) {
		CpuDemand clone = new CpuDemand((Node<? extends AbstractConstraint>) owner, this.getName());
		clone.demandedCycles = this.demandedCycles;
		
		return clone;
	}
}
