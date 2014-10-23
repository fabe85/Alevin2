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
package mulavito.algorithms.shortestpath.ksp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.MapTransformer;

import edu.uci.ics.jung.graph.Graph;

/**
 * Main idea: Translate the problem from one with two terminals (s, t), to a
 * problem with only one terminal (s) by finding paths from s to any other
 * vertex and concatenating shortest path from that vertex to t.
 * 
 * k shortest paths problem with only one terminal: (breadth first search)
 * 
 * 1. Maintain a priority queue of paths, initially containing the single
 * zero-edge path from s to itself.
 * 
 * 2. Repeatedly remove the shortest path from the priority queue, add it to the
 * list of output paths and add all one-edge extensions of that path to the
 * priority queue.
 * 
 * <p>
 * For implementation the following reference was used:
 * 
 * <blockquote>We give algorithms for finding the k shortest paths (not required
 * to be simple) connecting a pair of vertices in a digraph. Our algorithms
 * output an implicit representation of these paths in a digraph with n vertices
 * and m edges, in time <math>O(m + n log n + k)</math>. We can also find the k
 * shortest paths from a given source s to each vertex in the graph, in total
 * time <math>O(m + n log n + kn)</math>.</blockquote>
 * 
 * <pre>
 * @Article{ Eppstein98,
 * 	title = {{Finding the $k$ Shortest Paths}},
 * 	author = {David Eppstein},
 * 	journal = {SIAM J. Computing},
 * 	publisher = {SIAM},
 * 	volume = {28},
 * 	number = {2},
 * 	pages = {652--673},
 * 	year = {1998},
 * 	url = {http://dx.doi.org/10.1137/S0097539795290477},
 * 	review = {MR-99h:05073}
 * }
 * </pre>
 * 
 * </p>
 * 
 * @author Thilo Müller
 * @author Michael Duelli
 * @since 2010-11-26
 * 
 * @param <V>
 *            The parameter for vertices
 * @param <E>
 *            The parameter for edges
 */
public class Eppstein<V, E> extends KShortestPathAlgorithm<V, E> {
	/**
	 * Constructor of the Eppstein-K-shortest-Path
	 * 
	 * @param graph
	 *            the original graph
	 * @param nev
	 *            the weight transformer
	 */
	public Eppstein(Graph<V, E> graph, Transformer<E, Number> nev) {
		super(graph, nev);
	}

	private final class WeightedPath implements Comparable<WeightedPath> {
		private final Set<V> path_vertices;
		private final List<E> path_edges;
		private double weight;
		private V lastV;

		public WeightedPath(V start) {
			weight = 0.0;
			path_edges = new LinkedList<E>();

			path_vertices = new HashSet<V>();
			path_vertices.add(start);
			lastV = start;
		}

		public WeightedPath(WeightedPath copy) {
			weight = copy.weight;
			path_edges = new LinkedList<E>(copy.path_edges);
			path_vertices = new HashSet<V>(copy.path_vertices);
			lastV = copy.lastV;
		}

		public void addHop(E via, Double weight, V v) {
			this.weight += weight;
			path_edges.add(via);
			path_vertices.add(v);
			lastV = v;
		}

		public V getLast() {
			return lastV;
		}

		public boolean contains(V v) {
			return path_vertices.contains(v);
		}

		public List<E> getPath() {
			return path_edges;
		}

		@Override
		public int compareTo(WeightedPath o) {
			if (weight < o.weight)
				return -1;
			else if (weight > o.weight)
				return 1;

			// from here on equal weight
			int sizeDiff = path_edges.size() - o.path_edges.size();
			if (sizeDiff < 0)
				return -1;
			else if (sizeDiff > 0)
				return 1;

			return 0;
		}

		@Override
		public String toString() {
			return path_vertices + ";" + path_edges + ";" + weight;
		}
	}

	/**
	 * Create <math>\delta(e)</math> for each edge in graph {@link #g}.
	 * 
	 * @param target
	 *            The target
	 * @return The delta edge weight.
	 */
	private Transformer<E, Double> prepareTransformations(V target) {
		Map<V, Double> lengthMap = new HashMap<V, Double>(); // d(x, t)
		Map<E, Double> edgeMap = new HashMap<E, Double>();

		// Search the shortest path from "target" to any vertex,
		// and store the length in a map.
		for (V v : graph.getVertices()) {
			Number dist = dijkstra.getDistance(v, target);

			if (dist != null) // target is reachable from v.
				lengthMap.put(v, dist.doubleValue());
			else {
				// Block edges from or to unreachable vertices.
				for (E e : graph.getIncidentEdges(v))
					edgeMap.put(e, Double.POSITIVE_INFINITY);
			}
		}

		// Calculate delta(e)
		for (E e : graph.getEdges()) {
			if (edgeMap.get(e) == null) {
				// Only consider edges to reachable vertices.
				double l = nev.transform(e).doubleValue() /* l(e) */
						+ lengthMap.get(graph.getDest(e)) /* d(head(e), t) */
						- lengthMap.get(graph.getSource(e)) /* d(tail(e), t) */;
				edgeMap.put(e, l);
			}
		}

		return MapTransformer.getInstance(edgeMap);
	}

	@Override
	protected List<List<E>> getShortestPathsIntern(V source, V target, int k) {
		PriorityQueue<WeightedPath> prioQ = new PriorityQueue<WeightedPath>();
		List<List<E>> found_paths = new LinkedList<List<E>>();

		Transformer<E, Double> delta = prepareTransformations(target);

		// Initialize with start vertex.
		prioQ.add(new WeightedPath(source));

		while (!prioQ.isEmpty() && found_paths.size() < k) {
			WeightedPath curPath = prioQ.poll(); // get & remove next shortest
			V curV = curPath.getLast();

			if (curV.equals(target)) {
				found_paths.add(curPath.getPath());
				continue;
			}

			// Create new paths for every expanded vertex ...
			for (V nextV : graph.getSuccessors(curV)) {
				if (curPath.contains(nextV))
					continue; // Prevent looping!

				// ... and every possible edge.
				for (E e : graph.findEdgeSet(curV, nextV)) {
					if (Double.isInfinite(delta.transform(e)))
						continue; // Skip unreachable vertices.

					WeightedPath tmpPath = new WeightedPath(curPath); // clone
					tmpPath.addHop(e, delta.transform(e), nextV);

					prioQ.add(tmpPath);
				}
			}
		}

		return found_paths;
	}
}
