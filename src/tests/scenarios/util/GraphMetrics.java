package tests.scenarios.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import mulavito.graph.IEdge;
import mulavito.graph.IVertex;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.Graph;

/**
* Utility methods to evaluate the generated topologies.
* TODO write unit tests for this!!!
* 
* @author Philip Huppert
*/
public class GraphMetrics {
	private GraphMetrics() {}

	/**
	 * Test whether a graph is connected.
	 * 
	 * @param graph to test.
	 * @return true if graph is connected, false otherwise.
	 */
	public static boolean connected(Graph<IVertex, IEdge> graph) {
		DijkstraShortestPath<IVertex, IEdge> sp = new DijkstraShortestPath<>(graph);
		Collection<IVertex> verts = graph.getVertices();

		// forall vertA, vertB in graph exists connecting path <=> graph is connected
		for (IVertex vertA : verts) {
			for (IVertex vertB : verts) {
				if (sp.getDistance(vertA, vertB) == null) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Determine the outdegree distribution of a graph.
	 * 
	 * @param graph
	 *            to analyze.
	 * @return a map with the outdegree as keys and the number of nodes with
	 *         this outdegree as the corresponding value.
	 */
	public static Map<Integer,Integer> outdegreeDistribution(Graph<IVertex, IEdge> graph) {
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		Collection<IVertex> verts = graph.getVertices();
		for (IVertex vertex : verts) {
			int outdegree = graph.getOutEdges(vertex).size();
			Integer cnt = result.get(outdegree);
			cnt = (cnt == null) ? 0 : cnt;
			cnt++;
			result.put(outdegree, cnt);
		}
		return result;
	}

	/**
	 * Determine the number of centers of a graph. A vertex in a graph is a
	 * center if its distance (shortest path) to all other vertices is
	 * minimal.
	 * 
	 * @param graph
	 *            to analyze.
	 * @return the number of centers of a graph. A non-connected graph will
	 *         have 0 centers.
	 */
	public static int numCenters(Graph<IVertex, IEdge> graph) {
		DijkstraShortestPath<IVertex, IEdge> sp = new DijkstraShortestPath<>(graph);
		Collection<IVertex> verts = graph.getVertices();
		int numCenters = 0;
		Integer globalMin = null;

		for (IVertex vertA : verts) {
			int localMax = 0;

			for (IVertex vertB : verts) {
				if (vertA == vertB) {
					continue;
				}

				Number dst = sp.getDistance(vertA, vertB);
				if (dst == null) {
					return 0;
				}
				int iDst = dst.intValue();
				if (iDst > localMax) {
					localMax = iDst;
				}
			}

			if (globalMin == null) {
				globalMin = localMax;
			}

			if (localMax < globalMin) {
				globalMin = localMax;
				numCenters = 0;
			}

			if (localMax == globalMin) {
				numCenters++;
			} 
		}

		return numCenters;
	}
}