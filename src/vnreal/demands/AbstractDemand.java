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

import vnreal.constraints.AbstractConstraint;
import vnreal.mapping.Mapping;
import vnreal.network.NetworkEntity;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.resources.AbstractResource;
import vnreal.resources.ResourceVisitorAdapter;

/**
 * Base class for demands which are associated to a certain
 * {@link VirtualNetwork}.
 * 
 * @author Michael Duelli
 * @author Daniel Schlosser
 * @author Vlad Singeorzan
 * @since 2010-08-20
 */
public abstract class AbstractDemand extends AbstractConstraint {
	private final ResourceVisitorAdapter acceptsVisitor;
	private final ResourceVisitorAdapter fulfillsVisitor;

	/**
	 * Create a demand with automatic name
	 * 
	 * @param ne
	 * @param name Name (should be unique)
	 */
	protected AbstractDemand(NetworkEntity<? extends AbstractConstraint> ne) {
		super(ne);
		this.acceptsVisitor = createAcceptsVisitor();
		this.fulfillsVisitor = createFulfillsVisitor();
	}
	
	
	/**
	 * Create a demand with given name
	 * 
	 * @param ne
	 * @param name Name (should be unique)
	 */
	protected AbstractDemand(NetworkEntity<? extends AbstractConstraint> ne, String name) {
		super(ne, name);
		this.acceptsVisitor = createAcceptsVisitor();
		this.fulfillsVisitor = createFulfillsVisitor();
	}

	protected abstract ResourceVisitorAdapter createAcceptsVisitor();

	protected abstract ResourceVisitorAdapter createFulfillsVisitor();

	public final ResourceVisitorAdapter getAcceptsVisitor() {
		return acceptsVisitor;
	}

	public final ResourceVisitorAdapter getFulfillsVisitor() {
		return fulfillsVisitor;
	}

	/**
	 * Has to be implemented in sub-classes, such that the type of Resource can
	 * be properly resolved.
	 * 
	 * @param res
	 *            The resource to be occupied.
	 * @return true, if the resource was occupied, false otherwise.
	 */
	public abstract boolean occupy(AbstractResource res);

	/**
	 * Has to be implemented in sub-classes, such that the type of Resource can
	 * be properly resolved.
	 * 
	 * @param res
	 *            The resource to be freed.
	 * @return true, if the resource was freed, false otherwise.
	 */
	public abstract boolean free(AbstractResource res);

	protected final Mapping getMapping(AbstractResource res) {
		for (Mapping f : mappings)
			if (f.getResource().equals(res))
				return f;

		return null;
	}
	
	public abstract AbstractDemand getCopy(
			NetworkEntity<? extends AbstractDemand> owner);
}
