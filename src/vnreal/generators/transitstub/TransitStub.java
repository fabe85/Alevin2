package vnreal.generators.transitstub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import vnreal.generators.transitstub.generators.AGenerator;
import vnreal.generators.transitstub.generators.WaxmanGenerator;
import vnreal.generators.transitstub.graph.Edge;
import vnreal.generators.transitstub.graph.Graph;
import vnreal.generators.transitstub.graph.Vertex;
import vnreal.generators.transitstub.util.MeanRNG;


/**
 * TODO doc
 * 
 * Transit stub generator from gt-itm, implemented in java.
 * More resource hungry than c version, but hopefully more readable.
 * @author Philip Huppert
 *
 */
public class TransitStub {
	private static final double TRANSIT_SCALE = 0.01;
	private static final double STUB_MAX_DIST = 5 * TRANSIT_SCALE;
	private static final double STUB_MIN_DIST = 1 * TRANSIT_SCALE;
	private static final int RANDMULT = 3;
	private static final double STUB_SCALE = 0.5 * TRANSIT_SCALE;

	private TransitStub() {}

	/**
	 * TODO doc
	 * @param seed
	 * @param numDomains
	 * @param numMeanTransitVerts
	 * @param numMeanStubsPerTransitVert
	 * @param numMeanStubVerts
	 * @param numRandomTransitStubEdges
	 * @param numRandomStubStubEdges
	 * @param domainAlpha
	 * @param domainBeta
	 * @param transitAlpha
	 * @param transitBeta
	 * @param stubAlpha
	 * @param stubBeta
	 * @return
	 */
	public static Graph generateWithWaxman(
			final long seed,
			final int numDomains,
			final int numMeanTransitVerts,
			final int numMeanStubsPerTransitVert,
			final int numMeanStubVerts,
			final int numRandomTransitStubEdges,
			final int numRandomStubStubEdges,
			final double domainAlpha,
			final double domainBeta,
			final double transitAlpha,
			final double transitBeta,
			final double stubAlpha,
			final double stubBeta
			) {
		AGenerator domainGenerator =
				new WaxmanGenerator(domainAlpha, domainBeta);
		AGenerator transitGenerator =
				new WaxmanGenerator(transitAlpha, transitBeta);
		AGenerator stubGenerator =
				new WaxmanGenerator(stubAlpha, stubBeta);

		//transitGenerator = new DebugGenerator();
		//stubGenerator = new DebugGenerator();

		return TransitStub.generate(
				seed,
				numDomains,
				numMeanTransitVerts,
				numMeanStubsPerTransitVert,
				numMeanStubVerts,
				numRandomTransitStubEdges,
				numRandomStubStubEdges,
				domainGenerator,
				transitGenerator,
				stubGenerator);
	}


	/**
	 * TODO doc
	 * @param seed
	 * @param numDomains
	 * @param numMeanTransitVerts
	 * @param numMeanStubsPerTransitVert
	 * @param numMeanStubVerts
	 * @param numRandomTransitStubEdges
	 * @param numRandomStubStubEdges
	 * @param domainGenerator
	 * @param transitGenerator
	 * @param stubGenerator
	 * @return
	 */
	public static Graph generate(
			final long seed,
			final int numDomains,
			final int numMeanTransitVerts,
			final int numMeanStubsPerTransitVert,
			final int numMeanStubVerts,
			final int numRandomTransitStubEdges,
			final int numRandomStubStubEdges,
			AGenerator domainGenerator,
			AGenerator transitGenerator,
			AGenerator stubGenerator
			) {
		final Random rnd = new Random(seed);
		final Graph resultGraph = new Graph();

		/*
		 * Theory of operation:
		 *
		 *  1. Generate domain graph
		 *
		 *  2. Generate transit domains
		 *     (generate transit graph for each vertex in domain graph)
		 *
		 *  3. Generate stub domains
		 *     (generate random number of stub graphs for each transit vertex)
		 *
		 *  4. Place inter-domain edges
		 *     (for each edge in domain graph connect two random vertices in
		 *      the corresponding transit graphs)
		 *
		 *  5. Attach stubs
		 *     (connect each transit vertex with a random vertex in each of its
		 *      associated stubs)
		 *
		 *  6. Add random transit-stub edges
		 *     (pick and connect a random stub vertex and transit vertex,
		 *      the stub must not be connected to the transit domain)
		 *
		 *  7. Add random stub-stub edges
		 *     (pick and connect two random stub vertices in distinct stubs)
		 */

		/*
		 * Generate domain graph: Vertex count is given in numDomains, generator
		 * is given in domainGenerator.
		 */
		final Graph domainGraph =
				domainGenerator.generateConnected(numDomains, rnd.nextLong());

		// Generate transit domains
		final Map<Vertex, Graph> transitGraphs = new HashMap<Vertex, Graph>();
		final MeanRNG transitSize = new MeanRNG(
				domainGraph.getNumVertices(),
				numMeanTransitVerts,
				RANDMULT,
				rnd.nextLong());

		for (Vertex domainVert : domainGraph.getVertices()) {
			/*
			 * Generate transit graph: Mean vertex count is given in
			 * numMeanTransitVerts, generator is given in transitGenerator.
			 */
			Graph transitGraph = transitGenerator.generateConnected(
					transitSize.nextInt(), rnd.nextLong());

			// Center transit graph over domainVert and shrink it.
			transitGraph.scale(TRANSIT_SCALE, TRANSIT_SCALE);
			transitGraph.translate(
					domainVert.getXpos() - 0.5 * TRANSIT_SCALE,
					domainVert.getYpos() - 0.5 * TRANSIT_SCALE);

			transitGraphs.put(domainVert, transitGraph);
			resultGraph.merge(transitGraph);
		}

		// Generate stub domains
		final List<Graph> stubs = new ArrayList<Graph>();
		// Keep track which transit domain each stub domain belongs to.
		final Map<Graph, Vertex> stubToTransitVertex = new HashMap<Graph, Vertex>();
		final Map<Graph, Graph> stubToTransitDomain = new HashMap<Graph, Graph>();

		for (Entry<Vertex, Graph> e : transitGraphs.entrySet()) {
			Graph transitGraph = e.getValue();
			Vertex domainVert = e.getKey();

			MeanRNG stubCount = new MeanRNG(
					transitGraph.getNumVertices(),
					numMeanStubsPerTransitVert,
					RANDMULT,
					rnd.nextLong());

			for (Vertex transitVert : transitGraph.getVertices()) {
				/*
				 * Mean number of stub domains for each transit vertex is given
				 * in numMeanStubsPerTransitVert.
				 */
				int numStubs = stubCount.nextInt();
				MeanRNG stubSize =new MeanRNG(
						numStubs, numMeanStubVerts, RANDMULT, rnd.nextLong());

				for (int i = 0; i < numStubs; i++) {
					/*
					 * Generate stub graph: Mean vertex count is given in
					 * numMeanStubVerts, generator is given in stubGenerator.
					 */
					Graph stubGraph = stubGenerator.generateConnected(
							stubSize.nextInt(), rnd.nextLong());

					// Place stub domain.
					double xpos = domainVert.getXpos() - 0.5 * STUB_SCALE;
					double ypos = domainVert.getYpos() - 0.5 * STUB_SCALE;
					double xoff = rnd.nextDouble() * (STUB_MAX_DIST-STUB_MIN_DIST) + STUB_MIN_DIST;
					double yoff = rnd.nextDouble() * (STUB_MAX_DIST-STUB_MIN_DIST) + STUB_MIN_DIST;
					xoff *= (rnd.nextBoolean()) ? 1 : -1;
					yoff *= (rnd.nextBoolean()) ? 1 : -1;
					xpos += xoff;
					ypos += yoff;

					stubGraph.scale(STUB_SCALE, STUB_SCALE);
					stubGraph.translate(xpos, ypos);

					stubs.add(stubGraph);
					stubToTransitVertex.put(stubGraph, transitVert);
					stubToTransitDomain.put(stubGraph, transitGraph);
					resultGraph.merge(stubGraph);
				}
			}
		}

		// Place inter-domain edges
		for (Edge domainEdge : domainGraph.getEdges()) {
			Vertex[] connected = domainGraph.getVertices(domainEdge);
			Vertex domainA = connected[0];
			Vertex domainB = connected[1];

			Vertex[] transitVertsA = transitGraphs.get(domainA).getVertices();
			Vertex[] transitVertsB = transitGraphs.get(domainB).getVertices();

			// Select random vertices in each domain to connect.
			Vertex vertexA = transitVertsA[rnd.nextInt(transitVertsA.length)];
			Vertex vertexB = transitVertsB[rnd.nextInt(transitVertsB.length)];

			resultGraph.addEdge(new Edge(), vertexA, vertexB);
		}

		// Attach stub domains to transit domains
		for (Entry<Graph, Vertex> e : stubToTransitVertex.entrySet()) {
			Graph stubGraph = e.getKey();
			Vertex[] stubVerts = stubGraph.getVertices();
			Vertex stubVert = stubVerts[rnd.nextInt(stubVerts.length)];

			Vertex transitVert = e.getValue();

			resultGraph.addEdge(new Edge(), transitVert, stubVert);
		}

		/*
		 * Add random transit-stub edges: Edge count is given in
		 * numRandomTransitStubEdges.
		 */
		for (int i = 0; i < numRandomTransitStubEdges; i++) {
			// Select random transit domain.
			Graph[] transits = new Graph[transitGraphs.size()];
			transitGraphs.values().toArray(transits);
			Graph transitGraph = transits[rnd.nextInt(transits.length)];

			// Select random transit vertex
			Vertex[] transitVerts = transitGraph.getVertices();
			Vertex transitVert = transitVerts[rnd.nextInt(transitVerts.length)];

			/*
			 * Select random stub domain that is not connected to selected
			 * transit domain.
			 */
			Graph stubGraph;
			do {
				stubGraph = stubs.get(rnd.nextInt(stubs.size()));
			} while (stubToTransitDomain.get(stubGraph) == transitGraph);

			// Select random stub vertex.
			Vertex[] stubVerts = stubGraph.getVertices();
			Vertex stubVert = stubVerts[rnd.nextInt(stubVerts.length)];

			// Create edge between stub and transit vertices.
			resultGraph.addEdge(new Edge(), transitVert, stubVert);
		}

		/*
		 * Add random stub stub edges: Edge count is given in
		 * numRandomStubStubEdges.
		 */
		for (int i = 0; i < numRandomStubStubEdges; i++) {
			Graph stubA, stubB;
			do {
				stubA = stubs.get(rnd.nextInt(stubs.size()));
				stubB = stubs.get(rnd.nextInt(stubs.size()));
			} while (stubA == stubB);

			Vertex[] vertsA = stubA.getVertices();
			Vertex[] vertsB = stubB.getVertices();

			Vertex vertA, vertB;
			do {
				vertA = vertsA[rnd.nextInt(vertsA.length)];
				vertB = vertsB[rnd.nextInt(vertsB.length)];
			} while (resultGraph.connected(vertA, vertB));

			resultGraph.addEdge(new Edge(), vertA, vertB);
		}

		resultGraph.normalizeCoordinates();
		return resultGraph;
	}
}
