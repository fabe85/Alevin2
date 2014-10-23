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
import vnreal.demands.AbstractDemand;
import vnreal.demands.DemandVisitorAdapter;
import vnreal.mapping.Mapping;
import vnreal.network.NetworkEntity;
import vnreal.network.substrate.SubstrateNetwork;

/**
 * Base class for resources which can be added to the {@link SubstrateNetwork}.
 * 
 * @author Michael Duelli
 * @author Daniel Schlosser
 * @author Vlad Singeorzan
 * @since 2010-08-20
 */
public abstract class AbstractResource extends AbstractConstraint {
	private final DemandVisitorAdapter occupyVisitor;
	private final DemandVisitorAdapter freeVisitor;
	
	
	/**
	 * Create a resource with automatic name
	 * 
	 * @param ne
	 * @param name Name (should be unique)
	 */
	protected AbstractResource(NetworkEntity<? extends AbstractConstraint> ne) {
		super(ne);
		this.occupyVisitor = createOccupyVisitor();
		this.freeVisitor = createFreeVisitor();
	}
	
	/**
	 * Create a resource with given name
	 * 
	 * @param ne
	 * @param name Name (should be unique)
	 */
	protected AbstractResource(NetworkEntity<? extends AbstractConstraint> ne, String name) {
		super(ne, name);
		this.occupyVisitor = createOccupyVisitor();
		this.freeVisitor = createFreeVisitor();
	}

	protected abstract DemandVisitorAdapter createOccupyVisitor();

	protected abstract DemandVisitorAdapter createFreeVisitor();

	public final DemandVisitorAdapter getOccupyVisitor() {
		return occupyVisitor;
	}

	public final DemandVisitorAdapter getFreeVisitor() {
		return freeVisitor;
	}

	/**
	 * Has to be implemented in sub-classes, such that the type of Demand can be
	 * properly resolved.
	 * 
	 * @param dem
	 *            The demand to be accepted.
	 * @return true, if the demand fits to a resource, false otherwise.
	 */
	public abstract boolean accepts(AbstractDemand dem);

	/**
	 * Has to be implemented in sub-classes, such that the type of Demand can be
	 * properly resolved.
	 * 
	 * @param dem
	 *            The demand to be fulfilled.
	 * @return true, if the demand can be fulfilled by this resource, false
	 *         otherwise.
	 */
	public abstract boolean fulfills(AbstractDemand dem);

	protected final Mapping getMapping(AbstractDemand dem) {
		for (Mapping f : mappings)
			if (f.getDemand().equals(dem))
				return f;

		return null;
	}

	// required for visitor pattern
	protected final AbstractResource getThis() {
		return this;
	}

	protected String getMappingsString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" by ");
		for (Mapping mapping : getMappings()) {
			sb.append(mapping.getDemand());
			sb.append("@");
			sb.append(mapping.getDemand().getOwner());
			sb.append(", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		return sb.toString();
	}

	public abstract AbstractResource getCopy(
			NetworkEntity<? extends AbstractConstraint> owner);

}
