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

import vnreal.constraints.AbstractConstraint;
import vnreal.constraints.INodeConstraint;
import vnreal.demands.AbstractDemand;
import vnreal.demands.DemandVisitorAdapter;
import vnreal.demands.PowerDemand;
import vnreal.mapping.Mapping;
import vnreal.network.NetworkEntity;
import vnreal.network.Node;
import vnreal.network.virtual.VirtualNode;

public class PowerResource extends AbstractResource implements INodeConstraint {

	Integer usedBy = 0;

	public PowerResource(Node<? extends AbstractConstraint> owner) {
		super(owner);
	}
	
	public PowerResource(Node<? extends AbstractConstraint> owner, String name) {
		super(owner, name);
	}

	public boolean isUsed() {
		return usedBy != 0;
	}

	public Integer usedBy() {
		return usedBy;
	}

	public Integer getIdleConsumption() {
		return 1;
	}

	public Integer getCurrentConsumption() {
		return (isUsed() ? 1 : 0);
	}

	public Integer getConsumptionAfterMapping(VirtualNode vn) {
		return 1;
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
			public boolean visit(PowerDemand dem) {
				if (fulfills(dem)) {
					usedBy++;
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
			public boolean visit(PowerDemand dem) {
				Mapping mapping = getMapping(dem);
				if (mapping != null) {
					boolean result = mapping.unregister();
					if (result) {
						usedBy--;
					}
					return result;
				} else
					return false;
			}
		};
	}

	@Override
	public String toString() {
		return new String("PowerResource: usedBy=" + usedBy);
	}

	@Override
	public AbstractResource getCopy(
			NetworkEntity<? extends AbstractConstraint> owner) {

		PowerResource clone = new PowerResource(
				(Node<? extends AbstractConstraint>) owner, this.getName());
		clone.usedBy = usedBy;

		return clone;
	}
}
