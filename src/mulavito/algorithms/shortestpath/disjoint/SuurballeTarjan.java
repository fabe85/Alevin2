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
package mulavito.algorithms.shortestpath.disjoint;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mulavito.algorithms.shortestpath.ShortestPathAlgorithm;

import org.apache.commons.collections15.ListUtils;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.MapTransformer;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * Implementation of the SuurballeTarjan algorithm as found in:
 * 
 * <pre>
 * Disjoint Paths in a Network
 * 
 * J. W. Suurballe
 *   Bell Telephone Laboratories, Inc., Holmdel, New Jersey
 * </pre>
 * 
 * and
 * 
 * <pre>
 * A Quick Method for Finding Shortest Pairs of Disjoint Paths
 * 
 * J. W. Suurballe
 *   Bell Laboratories, West Long Branch, New Jersey
 * R. E. Tarjan
 *   Bell Laboratories,Murray Hill, New Jersey
 * </pre>
 * 
 * @see http://en.wikipedia.org/wiki/Suurballe%27s_algorithm
 * 
 * @author Michael Duelli
 * @author Thilo Mueller
 * @since 2010-09-15
 */
public class SuurballeTarjan<V, E> extends ShortestPathAlgorithm<V, E> {
	/**
	 * Constructor of the SuurballeTarjan-Algorithm
	 * 
	 * @param graph
	 *            the original graph
	 * @param nev
	 *            the weight-transformer
	 */
	public SuurballeTarjan(Graph<V, E> graph, Transformer<E, Number> nev) {
		this(graph, nev, null);
	}

	private final Comparator<E> defaultComparator = new Comparator<E>() {
		@Override
		public int compare(E o1, E o2) {
			return o1.equals(o2) ? 0 : 1;
		}
	};

	/**
	 * A comparator to decide whether two edges are seen as equal. This allows
	 * to generalize the concept of edge equity for future usage.
	 * 
	 * @since 2011-08-07
	 */
	private final Comparator<E> comparator;

	public SuurballeTarjan(Graph<V, E> graph, Transformer<E, Number> nev,
			Comparator<E> comparator) {
		super(graph, nev);

		if (comparator == null)
			this.comparator = defaultComparator;
		else
			this.comparator = comparator;
	}

	/**
	 * The heart of the SuurballeTarjan algorithm.
	 * 
	 * @param source
	 *            the source node
	 * @param target
	 *            the target node
	 * @return two disjoint paths from the source to target node.
	 */
	public List<List<E>> getDisjointPaths(V source, V target) {
		if (dijkstra.getDistance(source, target) == null)
			return null; // target is not reachable!

		List<E> sp = dijkstra.getPath(source, target);

		// Determine length of shortest path from "source" to any other node.
		Map<V, Number> lengthMap = dijkstra.getDistanceMap(source);

		// Length transformation.
		Transformer<E, Double> lengthTrans = lengthTransformation(graph,
				MapTransformer.getInstance(lengthMap));

		// Get shortest path in g with reversed shortest path...
		Graph<V, E> revG = reverseEdges(graph, sp);
		DijkstraShortestPath<V, E> revDijkstra = new DijkstraShortestPath<V, E>(
				revG, lengthTrans);

		if (revDijkstra.getDistance(source, target) == null) {
			// no alternate path
			List<List<E>> result = new LinkedList<List<E>>();
			result.add(sp);
			return result;
		}

		List<E> revSp = revDijkstra.getPath(source, target);

		validate(source, target, sp, graph);
		validate(source, target, revSp, revG);

		LinkedList<E> spCopy = new LinkedList<E>(sp);

		List<List<E>> paths = findTwoWays(sp, revSp);

		if (paths == null) {
			// no disjoint solution found, just return shortest path
			LinkedList<List<E>> result = new LinkedList<List<E>>();
			result.add(spCopy);
			return result;
		}

		// Check path validity.
		for (List<E> path : paths)
			validate(source, target, path, graph);

		return paths;
	}

	private void validate(V source, V target, List<E> path, Graph<V, E> graph)
			throws AssertionError {
		if (!graph.isSource(source, path.get(0)))
			throw new AssertionError("invalid source");

		Iterator<E> it = path.iterator();
		E e1 = it.next();

		while (it.hasNext()) {
			E e2 = it.next();

			if (!graph.isSource(graph.getDest(e1), e2))
				throw new AssertionError("invalid path");

			e1 = e2;
		}

		if (!graph.isDest(target, path.get(path.size() - 1)))
			throw new AssertionError("invalid destination");
	}

	/**
	 * Combines two disjoint paths from two SuurballeTarjan input paths.
	 * 
	 * @param path1
	 *            Dijkstra shortest path
	 * @param path2
	 *            Dijkstra shortest path in partly reverted graph
	 * @return the two disjoint paths
	 */
	private List<List<E>> findTwoWays(List<E> path1, List<E> path2) {
		final V source = graph.getSource(path1.get(0));
		final V target = graph.getDest(path1.get(path1.size() - 1));

		// Remove common links.
		Iterator<E> it1 = path1.iterator();
		while (it1.hasNext()) {
			E e1 = it1.next();

			Iterator<E> it2 = path2.iterator();
			while (it2.hasNext()) {
				E e2 = it2.next();

				// ensure disjointness
				if (comparator.compare(e1, e2) == 0) { // for multigraph
					if (graph.isSource(source, e1)
							|| graph.isSource(source, e2)
							|| graph.isDest(target, e1)
							|| graph.isDest(target, e2))
						return null; // Removing required edge

					it1.remove();
					it2.remove();
					break; // inner loop
				}
			}
		}

		if (path1.isEmpty() || path2.isEmpty())
			return null; // no disjoint solution found

		// Now recombine the two paths.
		List<E> union = ListUtils.union(path1, path2); // concatenate

		List<E> p1 = recombinePaths(path1, target, union);
		if (p1 == null)
			return null;

		List<E> p2 = recombinePaths(path2, target, union);
		if (p2 == null)
			return null;
		
		if (!union.isEmpty())
			throw new AssertionError("BUG");

		List<List<E>> solution = new LinkedList<List<E>>();
		solution.add(p1);
		solution.add(p2);
		return solution;
	}

	private List<E> recombinePaths(List<E> path, V target, List<E> union) {
		LinkedList<E> p = new LinkedList<E>(); // provides getLast
		p.add(path.get(0));
		union.remove(path.get(0));

		V curDest;
		while (!(curDest = graph.getDest(p.getLast())).equals(target)) {
			boolean progress = false;
			for (E e : union)
				if (graph.isSource(curDest, e)) {
					p.add(e);
					progress = true;
					union.remove(e);
					break;
				}

			if (!progress)
				return null;

			if (union.isEmpty()) {
				if (!graph.isDest(target, p.getLast())) {
					throw new AssertionError("bug");
				} else
					break;
			}
		}
		return p;
	}

	/**
	 * This method reverse the path "path" in the graph "graph" and returns it.
	 * 
	 * @param graph
	 *            the input graph which will not be changed.
	 * @param path
	 *            the path to reverse
	 * @return a new graph with the reversed path
	 */
	private Graph<V, E> reverseEdges(Graph<V, E> graph, List<E> path) {
		if (graph == null || path == null)
			throw new IllegalArgumentException();
		Graph<V, E> clone = new DirectedOrderedSparseMultigraph<V, E>();

		for (V v : graph.getVertices())
			clone.addVertex(v);
		for (E e : graph.getEdges())
			clone.addEdge(e, graph.getEndpoints(e));

		for (E link : path) {
			V src = clone.getSource(link);
			V dst = clone.getDest(link);
			clone.removeEdge(link);
			clone.addEdge(link, dst, src, EdgeType.DIRECTED);
		}
		return clone;
	}

	/**
	 * This method does the following length transformation:
	 * 
	 * <pre>
	 *  c'(v,w) = c(v,w) - d (s,w) + d (s,v)
	 * </pre>
	 * 
	 * @param graph1
	 *            the graph
	 * @param slTrans
	 *            The shortest length transformer
	 * @return the transformed graph
	 */
	private Transformer<E, Double> lengthTransformation(Graph<V, E> graph1,
			Transformer<V, Number> slTrans) {
		Map<E, Double> map = new HashMap<E, Double>();

		for (E link : graph1.getEdges()) {
			double newWeight = nev.transform(link).doubleValue()
					- slTrans.transform(graph1.getDest(link)).doubleValue()
					+ slTrans.transform(graph1.getSource(link)).doubleValue();

			map.put(link, newWeight);
		}
		return MapTransformer.getInstance(map);
	}
}
