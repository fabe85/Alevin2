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
package mulavito.algorithms.shortestpath;

import java.util.Map;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.algorithms.shortestpath.ShortestPath;
import edu.uci.ics.jung.graph.Graph;

/**
 * Basis for all shortest path algorithms.
 * 
 * @author Xiaohua Qin
 * @author Michael Duelli (generics, full rewrite)
 * @since 2009-04-24
 * 
 * @param <V>
 *            The parameter for vertices
 * @param <E>
 *            The parameter for edges
 */
public abstract class ShortestPathAlgorithm<V, E> implements ShortestPath<V, E> {
	protected final Graph<V, E> graph;
	protected final DijkstraShortestPath<V, E> dijkstra;
	protected final Transformer<E, Number> nev;

	protected ShortestPathAlgorithm(Graph<V, E> graph,
			Transformer<E, Number> nev) {
		if (graph == null)
			throw new IllegalArgumentException("No graph given");
		if (nev == null)
			throw new IllegalArgumentException();

		this.graph = graph;
		this.nev = nev;
		this.dijkstra = new DijkstraShortestPath<V, E>(graph, nev);
	}

	@Override
	public final Map<V, E> getIncomingEdgeMap(V source) {
		throw new AssertionError("not implemented");
	}
}
