/* ***** BEGIN LICENSE BLOCK *****
 * Copyright (C) 2008-2011, The 100GET-E3-R3G Project Team.
 * 
 * This work has been funded by the Federal Ministry of Education
 * and Research of the Federal Republic of Germany
 * (BMBF Förderkennzeichen 01BP0775). It is part of the EUREKA project
 * "100 Gbit/s Carrier-Grade Ethernet Transport Technologies
 * (CELTIC CP4-001)". The authors alone are responsible for this work.
 *
 * See the file AUTHORS for details and contact information.
 * 
 * This file is part of MuLaViTo (Multi-Layer Visualization Tool).
 *
 * MuLaViTo is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License Version 3 or later
 * (the "GPL"), or the GNU Lesser General Public License Version 3 or later
 * (the "LGPL") as published by the Free Software Foundation.
 *
 * MuLaViTo is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * or the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and
 * GNU Lesser General Public License along with MuLaViTo; see the file
 * COPYING. If not, see <http://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ***** */
package mulavito.samples.utils;

import mulavito.graph.ILayer;
import mulavito.samples.utils.SampleGraphDocument.MyV;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * simple layer class with change support
 */
@SuppressWarnings("serial")
public abstract class MyL extends DirectedSparseMultigraph<MyV, MyE> implements
		ILayer<MyV, MyE> {
	public int layer = -1;
	public MyMLG owner = null;

	@Override
	public boolean addVertex(MyV vertex) {
		if (!super.addVertex(vertex))
			return false;
		vertex.id = getVertexCount();
		if (owner != null)
			owner.fireLayerChanged(this);
		return true;
	}

	@Override
	public boolean removeVertex(MyV vertex) {
		if (!super.removeVertex(vertex))
			return false;
		vertex.id = -1;
		if (owner != null)
			owner.fireLayerChanged(this);
		return true;
	}

	@Override
	public boolean addEdge(MyE edge, Pair<? extends MyV> endpoints,
			EdgeType edgeType) {
		if (!super.addEdge(edge, endpoints, edgeType))
			return false;
		edge.id = getEdgeCount();
		if (owner != null)
			owner.fireLayerChanged(this);
		return true;
	}

	@Override
	public boolean removeEdge(MyE edge) {
		if (!super.removeEdge(edge))
			return false;
		edge.id = -1;
		if (owner != null)
			owner.fireLayerChanged(this);
		return true;
	}

	@Override
	public String getLabel() {
		return "Layer " + layer;
	}

	@Override
	public int getLayer() {
		return layer;
	}

	@Override
	public int hashCode() {
		return layer;
	}

	public MyMLG getOwner() {
		return owner;
	}
}
