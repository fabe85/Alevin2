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
package vnreal.network;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


import vnreal.constraints.AbstractConstraint;

/**
 * The building blocks of a network: either node or link.
 * 
 * @author Michael Duelli
 * 
 * @param <T>
 *            The constraint parameter, either resource or demand.
 */
public abstract class NetworkEntity<T extends AbstractConstraint> implements
		Iterable<T> {
	private final List<T> constraints = new LinkedList<T>();

	private static long idSource = 0;
	private static Object syncObject = new Object();
	
	private String name = null;

	private final long id;

	/**
	 * Used in GUI methods.
	 */
	protected NetworkEntity() {
		synchronized (syncObject) {
			this.id = idSource;
			idSource++;
			
			//Prevent overflow
			if(idSource == Long.MAX_VALUE)
				idSource = 0;
		}
		
		this.name = this.id + "";
	}
	
	/**
	 * Add a constraint.
	 * 
	 * @param t
	 *            The considered constraint.
	 * @return true on success, false otherwise.
	 */
	public final boolean add(T t) {
		if (preAddCheck(t)) {
			if (t.getOwner() == this)
				return constraints.add(t);
			else {
				System.err.println("Cannot add constraint " + t.getClass().getSimpleName() + " to entity "
						+ this + " because owner != " + this);
				return false;
			}
		}
		return false;
	}

	protected abstract boolean preAddCheck(T t);

	/**
	 * Remove a constraint.
	 * 
	 * @param t
	 *            The considered constraint.
	 * @return true on success, false otherwise.
	 */
	public final boolean remove(T t) {
		if (constraints.size() > 1) {
			return t.unregisterAll() && constraints.remove(t);
		}
		System.err.println("Cannot remove constraint " + t + " from entity "
				+ this + " because number of constraints is 1.");
		return false;
	}

	public final void removeAll() {
		for (AbstractConstraint c : constraints) {
			c.unregisterAll();
		}
		constraints.clear();
	}

	public final List<T> get() {
		return Collections.unmodifiableList(constraints);
	}

	@Override
	public final Iterator<T> iterator() {
		return constraints.iterator();
	}

	public final long getId() {
		return id;
	}

	public abstract String toStringShort();

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
