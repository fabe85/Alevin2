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
package vnreal.constraints;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import vnreal.mapping.Mapping;
import vnreal.network.NetworkEntity;

/**
 * Base class for resources AND demands
 * 
 * @author Michael Duelli
 * @author Vlad Singeorzan
 * @since 2010-08-20
 */
public abstract class AbstractConstraint {
	protected final List<Mapping> mappings = new LinkedList<Mapping>();
	private NetworkEntity<? extends AbstractConstraint> owner;
	private final String name;
	
	
	protected AbstractConstraint(
			NetworkEntity<? extends AbstractConstraint> owner) {
		this.owner = owner;
		//TODO: hashCode good at this point?  
		this.name = owner.getName()+"_"+this.hashCode();
	}
	
	protected AbstractConstraint(
			NetworkEntity<? extends AbstractConstraint> owner, String name) {
		this.owner = owner;
		this.name = name;
	}

	public final List<Mapping> getMappings() {
		return Collections.unmodifiableList(this.mappings);
	}

	public final boolean register(Mapping mapping) {
		return mappings.add(mapping);
	}

	public final boolean unregister(Mapping mapping) {
		return mappings.remove(mapping);
	}

	public final boolean unregisterAll() {
		boolean deletedAll = true;
		List<Mapping> delete = new LinkedList<Mapping>();
		delete.addAll(mappings);

		for (Mapping f : delete)
			deletedAll = deletedAll && f.unregister();

		return deletedAll;
	}
	
	public String getName() {
		return name;
	}

	public NetworkEntity<? extends AbstractConstraint> getOwner() {
		return owner;
	}

	@Override
	public abstract String toString();

	/**
	 * This Method uses the name of the object to match
	 */
	@Override
	public boolean equals(Object obj) {
		if((obj instanceof AbstractConstraint)) {
			AbstractConstraint ac = (AbstractConstraint)obj;
			if(this.getName().equals(ac.getName()))
				return true;
		}		
		return false;
	}
	
	
}
