package vnreal.generators.transitstub.generators;

import vnreal.generators.transitstub.graph.Edge;
import vnreal.generators.transitstub.graph.Graph;
import vnreal.generators.transitstub.graph.Vertex;


public class DebugGenerator extends AGenerator {

	@Override
	public Graph generate(int numVertices, long seed) {
		double[] xs = {0, 1, 1, 0};
		double[] ys = {0, 0, 1, 1};

		Graph result = new Graph();
		Vertex[] vertices = new Vertex[numVertices];

		for (int i = 0; i < numVertices; i++) {
			Vertex v = new Vertex();
			v.setXpos(xs[i % xs.length]);
			v.setYpos(ys[i % ys.length]);
			vertices[i] = v;
			result.addVertex(v);
			if (i > 0) {
				result.addEdge(new Edge(), v, vertices[i-1]);
			}
		}

		return result;
	}

}
