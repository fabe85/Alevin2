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
import vnreal.resources.PhysicalDemandedSecurity;

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
public final class VirtualProvidedSecurity extends AbstractDemand implements
		INodeConstraint, ILinkConstraint {
	private double virtualProvidedSecurityLevel;

	public VirtualProvidedSecurity(Node<? extends AbstractConstraint> owner) {
		super(owner);
	}

	public VirtualProvidedSecurity(Link<? extends AbstractConstraint> owner) {
		super(owner);
	}
	
	public VirtualProvidedSecurity(Node<? extends AbstractConstraint> owner, String name) {
		super(owner, name);
	}

	public VirtualProvidedSecurity(Link<? extends AbstractConstraint> owner, String name) {
		super(owner, name);
	}

	@ExchangeParameter
	public void setVirtualProvidedSecurityLevel(
			Double virtualProvidedSecurityLevel) {
		this.virtualProvidedSecurityLevel = virtualProvidedSecurityLevel;
	}

	@ExchangeParameter
	public Double getVirtualProvidedSecurityLevel() {
		return virtualProvidedSecurityLevel;
	}

	@Override
	protected ResourceVisitorAdapter createAcceptsVisitor() {
		return new ResourceVisitorAdapter() {
			@Override
			public boolean visit(PhysicalDemandedSecurity res) {
				return true;
			}
		};
	}

	@Override
	protected ResourceVisitorAdapter createFulfillsVisitor() {
		return new ResourceVisitorAdapter() {
			@Override
			public boolean visit(PhysicalDemandedSecurity res) {
				return MiscelFunctions.roundThreeDecimals(res
						.getPhysicalDemandedSecurityLevel()) <= MiscelFunctions
						.roundThreeDecimals(getVirtualProvidedSecurityLevel());
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
		return new String("VirtualProvidedSecurityLevel: "
				+ getVirtualProvidedSecurityLevel());
	}

	@Override
	public AbstractDemand getCopy(NetworkEntity<? extends AbstractDemand> owner) {
		VirtualProvidedSecurity clone;
		
		if(owner.getClass().getSimpleName().contains("Node"))
			clone = new VirtualProvidedSecurity((Node<? extends AbstractConstraint>)owner, this.getName());
		else if(owner.getClass().getSimpleName().contains("Link"))
			clone = new VirtualProvidedSecurity((Link<? extends AbstractConstraint>)owner, this.getName());
		else
			throw new Error("The class: "+owner.getClass()+" is invalid for "+this.getClass().getSimpleName());
		
		clone.virtualProvidedSecurityLevel = this.virtualProvidedSecurityLevel;
		return clone;
	}
	
}
