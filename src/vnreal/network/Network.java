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

import java.util.List;

import mulavito.graph.ILayer;
import vnreal.constraints.AbstractConstraint;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.UndirectedOrderedSparseMultigraph;

@SuppressWarnings("serial")
public abstract class Network<T extends AbstractConstraint, V extends Node<T>, E extends Link<T>>
		extends ObservableGraph<V, E> implements ILayer<V, E> {

	private boolean autoUnregisterConstraints = true;

	protected Network(boolean autoUnregisterConstraints) {
		this(autoUnregisterConstraints, true);
	}
		
	protected Network(boolean autoUnregisterConstraints, boolean directed) {
		super(directed ? new DirectedOrderedSparseMultigraph<V, E>() : new UndirectedOrderedSparseMultigraph<V,E>());
		this.autoUnregisterConstraints = autoUnregisterConstraints;
	}
	
	@Override
	public boolean removeVertex(V v) {
		if (autoUnregisterConstraints) {
			// unregister all mappings first
			boolean unregistered = true;
			List<T> constraints = v.get();
			for (T cons : constraints)
				unregistered = unregistered && cons.unregisterAll();
			return (unregistered && super.removeVertex(v));
		} else {
			return super.removeVertex(v);
		}
	}

	@Override
	public boolean removeEdge(E e) {
		if (autoUnregisterConstraints) {
			// unregister all mappings first
			boolean unregistered = true;
			List<T> constraints = e.get();
			for (T cons : constraints)
				unregistered = unregistered && cons.unregisterAll();
			return (unregistered && super.removeEdge(e));
		} else {
			return super.removeEdge(e);
		}
	}

	public abstract Network<T, V, E> getInstance(boolean autoUnregister);

	public abstract Network<T, V, E> getCopy(boolean autoUnregister);
	
	/**
	 * Tries to find a Entity by name
	 * 
	 * @param name Name of the Entity
	 * @return The Entity or null if not found
	 */
	public NetworkEntity<T> getEntitieByName(String name) {
		for(Node<T> n : getVertices()) {
			if(n.getName().equals(name))
				return n;
		}
		
		for(Link<T> l : getEdges()) {
			if(l.getName().equals(name))
				return l;
		}
		
		return null;
	}
	
	public int getSize() {
	    int count = 0;
	    for (Node<T> n : getVertices()) {
	        count += 1;
	    }
	    return count;
	}

}
