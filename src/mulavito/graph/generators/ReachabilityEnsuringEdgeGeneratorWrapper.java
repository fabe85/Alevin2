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
package mulavito.graph.generators;

import java.util.ArrayList;
import java.util.List;

import mulavito.graph.IEdge;
import mulavito.graph.ILayer;
import mulavito.graph.IVertex;
import mulavito.utils.distributions.UniformStream;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Generate a random graph.
 * 
 * This is only the graph structure NOT the layout.
 * 
 * Loops as well as parallel edges are prevented.
 * 
 * @author Michael Duelli
 */
public final class ReachabilityEnsuringEdgeGeneratorWrapper<V extends IVertex, E extends IEdge>
		implements IEdgeGenerator<V, E> {
	private final IEdgeGenerator<V, E> generator;

	public ReachabilityEnsuringEdgeGeneratorWrapper(
			IEdgeGenerator<V, E> generator) {
		this.generator = generator;
	}

	@Override
	public void generate(ILayer<V, E> g) {
		// Apply internal generator.
		generator.generate(g);

		// Add some edges.
		// Note that the default is for undirected edges.
		UniformStream rnd = new UniformStream();
		List<V> vs = new ArrayList<V>(g.getVertices());

		// Prevent creation of loops and parallel edges.
		DijkstraShortestPath<V, E> dsp = new DijkstraShortestPath<V, E>(g);

		List<Pair<V>> unreachables = new ArrayList<Pair<V>>();
		List<Pair<V>> removeUnreachables = new ArrayList<Pair<V>>();

		for (V v : g.getVertices())
			for (V w : g.getVertices())
				if (dsp.getDistance(v, w) == null)
					unreachables.add(new Pair<V>(v, w));

		// Add further random edges until full connectivity.
		while (!unreachables.isEmpty()) {
			int num = rnd.nextInt(g.getVertexCount() - 1);
			V v = vs.get(num);

			int num2 = rnd.nextInt(g.getVertexCount() - 1);
			V w = vs.get(num2);

			// Prevent creation of loops and parallel edges.
			if (!v.equals(w) && g.findEdge(v, w) == null) {
				g.addEdge(g.getEdgeFactory().create(), v, w);

				dsp.reset();

				for (Pair<V> p : unreachables) {
					V v2 = p.getFirst();
					V w2 = p.getSecond();

					if (dsp.getDistance(v2, w2) != null)
						removeUnreachables.add(p);
				}

				for (Pair<V> p : removeUnreachables)
					unreachables.remove(p);

				removeUnreachables.clear();
			}
		}
	}
}
