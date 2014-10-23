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

import mulavito.graph.IVertex;
import vnreal.ExchangeParameter;
import vnreal.constraints.AbstractConstraint;
import vnreal.constraints.INodeConstraint;

/**
 * A basic network node class.
 * 
 * @author Michael Duelli
 * @author Vlad Singeorzan
 * 
 * @param <T>
 *            Either resource or demand.
 */
public abstract class Node<T extends AbstractConstraint> extends
		NetworkEntity<T> implements IVertex {
	private double coordinateX;
	private double coordinateY;
	
	private String type;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public Node() {
		super();
	}

	@ExchangeParameter
	public final void setCoordinateX(double coordinateX) {
		this.coordinateX = coordinateX;
	}

	@ExchangeParameter
	public final double getCoordinateX() {
		// round to 3 decimals
		return Math.rint(coordinateX * 1000) / 1000;
	}

	@ExchangeParameter
	public final void setCoordinateY(double coordinateY) {
		this.coordinateY = coordinateY;
	}

	@ExchangeParameter
	public final double getCoordinateY() {
		// round to 3 decimals
		return Math.rint(coordinateY * 1000) / 1000;
	}

	@Override
	public final boolean preAddCheck(T t) {
		// Only allow to add this type.
		if (t instanceof INodeConstraint)
			if (!this.containsConstraintType(t))
				return true;
			else {
				throw new RuntimeException("Cannot add constraint " + t + " to node "
						+ this
						+ " because it already has a constraint of this type.");
			}
		else {
			System.err.println("Cannot add non-INodeConstraint " + t
					+ " to node " + this);
			return false;
		}
	}

	@Override
	public final String getLabel() {
		return toString();
	}

	private boolean containsConstraintType(T t) {
		Class<?> classs = t.getClass();
		for (T cons : this.get()) {
			if (cons.getClass().equals(classs)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public abstract String toString();
}
