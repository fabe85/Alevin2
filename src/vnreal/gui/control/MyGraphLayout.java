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
package vnreal.gui.control;

import java.awt.geom.Point2D;

import org.apache.commons.collections15.Transformer;

import vnreal.constraints.AbstractConstraint;
import vnreal.network.Link;
import vnreal.network.Node;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author Michael Duelli
 * 
 * @param <V>
 *            The vertices
 * @param <E>
 *            The edges
 */
public final class MyGraphLayout<T extends AbstractConstraint, V extends Node<T>, E extends Link<T>>
		extends StaticLayout<V, E> {
	public MyGraphLayout(Graph<V, E> g) {
		super(g, new Transformer<V, Point2D>() {
			@Override
			public Point2D transform(V input) {
				return new Point2D.Double(input.getCoordinateX(),
						input.getCoordinateY());
			}
		});
	}

	@Override
	public void setLocation(V v, Point2D p) {
		// Update own storage.
		v.setCoordinateX(p.getX());
		v.setCoordinateY(p.getY());

		super.setLocation(v, p);
	}
}
