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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.commons.collections15.ListUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.filters.EdgePredicateFilter;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.Graph;

/**
 * <p>
 * Calculate the first <i>k</i>-shortest paths between source node and target
 * node using <i>Yen</i>'s algorithm.
 * </p>
 * 
 * <p>
 * Method {@link #getkShortestPath} returns the first k shortest paths (KSP) for
 * a given <code>source</code> and <code>target</code>.
 * </p>
 * 
 * <p>
 *"This paper presents an algorithm for finding the K loop-less paths that have
 * the shortest lengths from one node to another node in a network. The
 * significance of the new algorithm is that its computational upper bound
 * increases only linearly with the value of K. Consequently, in general, the
 * new algorithm is extremely efficient as com- pared with the algorithms
 * proposed by Bock, Kantner, and Haynes [2],Pollack [7], [81, Clarke,
 * Krikorian, and Rausan [3],Sakarovitch [9]and others. This paper first reviews
 * the algorithms presently available for finding the K shortest loop-less paths
 * in terms of the computational effort and memory addresses they require. This
 * is followed by the presentation of the new algorithm and its justification.
 * Finally, the efficiency of the new algorithm is examined and compared with
 * that of other algorithms."
 * 
 * <pre>
 * @Article{ Yen71,
 * 	author = {Yen, Jin Y.},
 * 	title = {{Another algorithm for finding the K shortest loop-less network paths}},
 * 	journal = {Management Science}
 * 	volume = {17},
 * 	number = {11},
 * 	month = jul,
 * 	year = {1971},
 * 	pages = {712--716},
 * 	publisher = {JSTOR}
 * }
 * </pre>
 * 
 * </p>
 * 
 * @author Michael Duelli
 * @author Thilo Müller
 * @author Xiaohua Qin (original implementation)
 * 
 * @param <V>
 *            The parameter for vertices
 * @param <E>
 *            The parameter for edges
 */
public class Yen<V, E> extends KShortestPathAlgorithm<V, E> {
	public Yen(Graph<V, E> graph, Transformer<E, Number> nev) {
		super(graph, nev);
	}

	private final class WeightedPath implements Comparable<WeightedPath> {
		private final List<E> path;
		private final double weight;

		public WeightedPath(List<E> path) {
			this.path = path;

			double tmp = 0.0;
			for (E e : path)
				tmp += nev.transform(e).doubleValue();

			this.weight = tmp;
		}

		public List<E> getPath() {
			return path;
		}

		@Override
		public int compareTo(WeightedPath o) {
			if (weight < o.weight)
				return -1;
			else if (weight > o.weight)
				return 1;

			// from here on equal weight
			int sizeDiff = path.size() - o.path.size();
			if (sizeDiff < 0)
				return -1;
			else if (sizeDiff > 0)
				return 1;

			return 0;
		}

		@Override
		public String toString() {
			return path + ";" + weight;
		}
	}

	@Override
	protected List<List<E>> getShortestPathsIntern(final V source,
			final V target, int k) {
		LinkedList<List<E>> found_paths = new LinkedList<List<E>>();
		PriorityQueue<WeightedPath> prioQ = new PriorityQueue<WeightedPath>();
		DijkstraShortestPath<V, E> blockedDijkstra;

		// Check if target is reachable from source.
		if (dijkstra.getDistance(source, target) == null)
			return found_paths;

		// Add Dijkstra solution, the first shortest path.
		found_paths.add(dijkstra.getPath(source, target));

		while (found_paths.size() < k) {
			List<E> curShortestPath = found_paths.getLast();

			int maxIndex = curShortestPath.size();

			// Split path into Head and NextEdge
			for (int i = 0; i < maxIndex; i++) {
				List<E> head = curShortestPath.subList(0, i /* excluded */);
				V deviation = head.isEmpty() ? source : graph.getDest(head
						.get(i - 1));

				// 1. Block edges.
				Graph<V, E> blocked = blockFilter(head, deviation, found_paths);

				// 2. Get shortest path in graph with blocked edges.
				blockedDijkstra = new DijkstraShortestPath<V, E>(blocked, nev);

				Number dist = blockedDijkstra.getDistance(deviation, target);
				if (dist == null)
					continue;

				List<E> tail = blockedDijkstra.getPath(deviation, target);

				// 3. Combine head and tail into new path.
				List<E> candidate = new ArrayList<E>(i + tail.size());
				candidate.addAll(head);
				candidate.addAll(tail);

				// Check if we already found this solution
				boolean duplicate = false;
				for (WeightedPath path : prioQ)
					if (ListUtils.isEqualList(path.getPath(), candidate)) {
						duplicate = true;
						break;
					}

				if (!duplicate)
					prioQ.add(new WeightedPath(candidate));
			}

			if (prioQ.isEmpty())
				break; // We have not found any new candidate!
			else
				found_paths.add(prioQ.poll().getPath());
		}

		return found_paths;
	}

	/**
	 * Blocks all incident edges of the vertices in head as well as the edge
	 * connecting head to the next node by creating a new filtered graph.
	 * 
	 * @param head
	 *            The current head, from source to deviation node
	 * @param deviation
	 *            The edge to the next node
	 * @param foundPaths
	 *            The solutions already found and to check against
	 * @return The filtered graph without the blocked edges.
	 */
	private Graph<V, E> blockFilter(List<E> head, V deviation,
			List<List<E>> foundPaths) {
		final Set<E> blocked = new HashSet<E>();

		// Block incident edges to make all vertices in head unreachable.
		for (E e : head)
			for (E e2 : graph.getIncidentEdges(graph.getSource(e)))
				blocked.add(e2);

		// Block all outgoing edges that have been used at deviation vertex
		for (List<E> path : foundPaths)
			if (path.size() > head.size()
					&& ListUtils
							.isEqualList(path.subList(0, head.size()), head))
				for (E e : path)
					if (graph.isSource(deviation, e)) {
						blocked.add(e);
						break; // Continue with next path.
					}

		EdgePredicateFilter<V, E> filter = new EdgePredicateFilter<V, E>(
				new Predicate<E>() {
					@Override
					public boolean evaluate(E e) {
						return !blocked.contains(e);
					}
				});

		return filter.transform(graph);
	}
}
