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
import vnreal.constraints.AbstractConstraint;
import vnreal.constraints.ILinkConstraint;
import vnreal.constraints.INodeConstraint;
import vnreal.demands.AbstractDemand;
import vnreal.demands.DemandVisitorAdapter;
import vnreal.demands.VirtualProvidedSecurity;
import vnreal.mapping.Mapping;
import vnreal.network.Link;
import vnreal.network.NetworkEntity;
import vnreal.network.Node;

/**
 * A resource for Security Levels.
 * 
 * N.b.: This resource is applicable for nodes and links.
 * 
 * @author Michael Duelli
 * @author Julian Willerding
 * @since 2012-01-20
 */
public final class PhysicalDemandedSecurity extends AbstractResource implements
		INodeConstraint, ILinkConstraint {
	private double physicalDemandedSecurityLevel;

	public PhysicalDemandedSecurity(Node<? extends AbstractConstraint> owner) {
		super(owner);
	}

	public PhysicalDemandedSecurity(Link<? extends AbstractConstraint> owner) {
		super(owner);
	}
	
	public PhysicalDemandedSecurity(Node<? extends AbstractConstraint> owner, String name) {
		super(owner, name);
	}

	public PhysicalDemandedSecurity(Link<? extends AbstractConstraint> owner, String name) {
		super(owner, name);
	}

	@ExchangeParameter
	public void setPhysicalDemandedSecurityLevel(Double physicalDemandedSecurityLevel) {
		this.physicalDemandedSecurityLevel = physicalDemandedSecurityLevel;
	}

	@ExchangeParameter
	public Double getPhysicalDemandedSecurityLevel() {
		return this.physicalDemandedSecurityLevel;
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
			public boolean visit(VirtualProvidedSecurity dem) {
				if (fulfills(dem)) {
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
			public boolean visit(VirtualProvidedSecurity dem) {
				if (getMapping(dem) != null) {
					return getMapping(dem).unregister();
				} else
					return false;
			}
		};
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PhysicalDemandedSecurityLevel: ");
		sb.append(getPhysicalDemandedSecurityLevel());
		if (getMappings().size() > 0)
			sb.append(getMappingsString());
		return sb.toString();
	}

	@Override
	public AbstractResource getCopy(
			NetworkEntity<? extends AbstractConstraint> owner) {

		PhysicalDemandedSecurity clone;
		if(owner.getClass().getSimpleName().contains("Node"))
			clone = new PhysicalDemandedSecurity((Node<? extends AbstractConstraint>)owner, this.getName());
		else if(owner.getClass().getSimpleName().contains("Link"))
			clone = new PhysicalDemandedSecurity((Link<? extends AbstractConstraint>)owner, this.getName());
		else
			throw new Error("The class: "+owner.getClass()+" is invalid for "+this.getClass().getSimpleName());
		clone.physicalDemandedSecurityLevel = physicalDemandedSecurityLevel;
		return clone;
	}
}
