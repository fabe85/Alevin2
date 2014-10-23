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
import vnreal.demands.AbstractDemand;
import vnreal.demands.CostDemand;
import vnreal.demands.DemandVisitorAdapter;
import vnreal.network.Link;
import vnreal.network.NetworkEntity;

/**
 * Tag for link cost.
 * 
 * @author Alexander Findeis
 */
public final class CostResource extends AbstractResource implements	ILinkConstraint {
    private double cost;

	public CostResource(Link<? extends AbstractConstraint> owner) {
		super(owner);
	}

    @ExchangeParameter
    public void setCost(Double cost) {
        this.cost = cost;
    }

    @ExchangeParameter
    public Double getCost() {
        return cost;
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
			public boolean visit(CostDemand dem) {
				return true;
			}
		};
	}

	@Override
	protected DemandVisitorAdapter createFreeVisitor() {
		return new DemandVisitorAdapter() {
			public boolean visit(CostDemand dem) {
				return true;
			}
		};
	}

	@Override
	public String toString() {
		return "";
	}

    @Override
    public AbstractResource getCopy(NetworkEntity<? extends AbstractConstraint> owner) {
        CostResource copy = new CostResource((Link<? extends AbstractConstraint>) owner);
        copy.setCost(this.getCost());
        return copy;
    }
}
