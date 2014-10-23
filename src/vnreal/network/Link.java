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

import mulavito.graph.IEdge;
import vnreal.constraints.AbstractConstraint;
import vnreal.constraints.ILinkConstraint;

/**
 * A basic network link class.
 * 
 * @author Michael Duelli
 * @author Vlad Singeorzan
 * 
 * @param <T>
 *            Either resource or demand.
 */
public abstract class Link<T extends AbstractConstraint> extends
		NetworkEntity<T> implements IEdge {

	public Link() {
		super();
	}

	@Override
	protected boolean preAddCheck(T t) {
		// Only allow to add this type.
		if (t instanceof ILinkConstraint)
			if (!this.containsConstraintType(t))
				return true;
			else {
				throw new RuntimeException("Cannot add constraint " + t + " to link "
						+ this
						+ " because it already has a constraint of this type.");
			}
		else {
			System.err.println("Cannot add non-ILinkConstraint " + t.getClass().getSimpleName()
					+ " to link " + this);
			return false;
		}
	}
	
	public abstract Link<T> getCopy();

	protected boolean containsConstraintType(T t) {
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
