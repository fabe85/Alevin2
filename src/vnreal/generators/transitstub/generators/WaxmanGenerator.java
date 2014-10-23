package vnreal.generators.transitstub.generators;

import java.util.Random;

import vnreal.generators.transitstub.graph.Edge;
import vnreal.generators.transitstub.graph.Graph;
import vnreal.generators.transitstub.graph.Vertex;




public class WaxmanGenerator extends AGenerator {
	private static final double L = Math.sqrt(2);

	private double alpha;
	private double beta;

	public WaxmanGenerator(double alpha, double beta) {
		this.alpha = alpha;
		this.beta = beta;
	}

	public double getAlpha() {
		return alpha;
	}

	public double getBeta() {
		return beta;
	}

	@Override
	public String toString() {
		return String.format("WaxmanGenerator [alpha=%f, beta=%f]",
				this.alpha, this.beta);
	}

	@Override
	public Graph generate(int numVertices, long seed) {
		Graph result = new Graph();
		Random rnd = new Random(seed);
		Vertex[] vertices = new Vertex[numVertices];

		for (int i = 0; i < numVertices; i++) {
			Vertex v = new Vertex();
			v.setXpos(rnd.nextDouble());
			v.setYpos(rnd.nextDouble());
			vertices[i] = v;
			result.addVertex(v);
		}

		for (Vertex a : vertices) {
			for (Vertex b : vertices) {
				if (a == b) {
					continue;
				}

				double dist = distance(a, b);
				double prob = this.alpha * Math.exp(-dist / (this.beta * L));

				if (rnd.nextDouble() < prob) {
					if (!result.connected(a, b)) {
						result.addEdge(new Edge(), a, b);
					}
				}
			}
		}

		return result;
	}

	private static double distance(Vertex a, Vertex b) {
		return Math.sqrt(
				Math.pow(a.getXpos() - b.getXpos(), 2)
				+ Math.pow(a.getYpos() - b.getYpos(), 2));
	}
}
