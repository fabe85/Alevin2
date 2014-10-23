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
import vnreal.resources.ResourceVisitorAdapter;
import vnreal.resources.PhysicalProvidedSecurity;

/**
 * A demand for Security Extensions.
 * 
 * N.b.: This demand is applicable for nodes and links.
 * 
 * @author Michael Duelli
 * @author Vlad Singeorzan
 * @author Julian Willerding
 * @since 2012-01-20
 */
public final class VirtualDemandedSecurity extends AbstractDemand implements
		INodeConstraint, ILinkConstraint {
	private double virtualDemandedSecurityLevel;

	public VirtualDemandedSecurity(Node<? extends AbstractConstraint> owner) {
		super(owner);
	}

	public VirtualDemandedSecurity(Link<? extends AbstractConstraint> owner) {
		super(owner);
	}
	
	
	public VirtualDemandedSecurity(Node<? extends AbstractConstraint> owner, String name) {
		super(owner, name);
	}

	public VirtualDemandedSecurity(Link<? extends AbstractConstraint> owner, String name) {
		super(owner, name);
	}

	@ExchangeParameter
	public void setVirtualDemandedSecurityLevel(
			Double virtualDemandedSecurityLevel) {
		this.virtualDemandedSecurityLevel = virtualDemandedSecurityLevel;
	}

	@ExchangeParameter
	public Double getVirtualDemandedSecurityLevel() {
		return virtualDemandedSecurityLevel;
	}

	@Override
	protected ResourceVisitorAdapter createAcceptsVisitor() {
		return new ResourceVisitorAdapter() {
			@Override
			public boolean visit(PhysicalProvidedSecurity res) {
				return true;
			}
		};
	}

	@Override
	protected ResourceVisitorAdapter createFulfillsVisitor() {
		return new ResourceVisitorAdapter() {
			@Override
			public boolean visit(PhysicalProvidedSecurity res) {
				return MiscelFunctions.roundThreeDecimals(res
						.getPhysicalProvidedSecurityLevel()) >= MiscelFunctions
						.roundThreeDecimals(getVirtualDemandedSecurityLevel());
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
		return new String("VirtualDemandedSecurityLevel: "
				+ getVirtualDemandedSecurityLevel());
	}

	@Override
	public AbstractDemand getCopy(NetworkEntity<? extends AbstractDemand> owner) {
		VirtualDemandedSecurity clone;
		
		if(owner.getClass().getSimpleName().contains("Node"))
			clone = new VirtualDemandedSecurity((Node<? extends AbstractConstraint>)owner, this.getName());
		else if(owner.getClass().getSimpleName().contains("Link"))
			clone = new VirtualDemandedSecurity((Link<? extends AbstractConstraint>)owner, this.getName());
		else
			throw new Error("The class: "+owner.getClass()+" is invalid for "+this.getClass().getSimpleName());
		
		clone.virtualDemandedSecurityLevel = this.virtualDemandedSecurityLevel;
		return clone;
	}
}
