package vnreal.generators.transitstub.graph;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class Graph {
	private Set<Vertex> vertices = new HashSet<Vertex>();
	private Set<Edge> edges = new HashSet<Edge>();
	private Map<Vertex, Set<Edge>> verticesToEdges = new HashMap<Vertex, Set<Edge>>();
	private Map<Edge, Pair<Vertex>> edgesToVertices = new HashMap<Edge, Pair<Vertex>>();

	private static class Pair<A> {
		public A a, b;

		public Pair(A a, A b) {
			this.a = a;
			this.b = b;
		}
	}

	public Vertex[] getVertices() {
		Vertex[] result = new Vertex[this.vertices.size()];
		this.vertices.toArray(result);
		return result;
	}

	public int getNumVertices() {
		return this.vertices.size();
	}

	public void merge(Graph graph) {
		this.vertices.addAll(graph.vertices);
		this.edges.addAll(graph.edges);
		this.verticesToEdges.putAll(graph.verticesToEdges);
		this.edgesToVertices.putAll(graph.edgesToVertices);
	}

	public Edge[] getEdges(Vertex vertex) {
		Set<Edge> edges = this.verticesToEdges.get(vertex);
		Edge[] result = new Edge[edges.size()];
		edges.toArray(result);
		return result;
	}

	public Vertex[] getVertices(Edge edge) {
		Vertex[] result = new Vertex[2];
		Pair<Vertex> vertices = this.edgesToVertices.get(edge);
		result[0] = vertices.a;
		result[1] = vertices.b;
		return result;
	}

	public boolean connected(Vertex vertexA, Vertex vertexB) {
		if (!this.vertices.contains(vertexA)
				|| !this.vertices.contains(vertexB)) {
			throw new IllegalArgumentException("Unknown vertex provided.");
		}

		Set<Edge> edgesA = this.verticesToEdges.get(vertexA);
		Set<Edge> edgesB = this.verticesToEdges.get(vertexB);

		Set<Edge> isec = new HashSet<Edge>();
		isec.addAll(edgesA);
		isec.retainAll(edgesB);
		int size = isec.size();

		assert size == 0 || size == 1;
		return isec.size() == 1;
	}

	public void addEdge(Edge edge, Vertex vertexA, Vertex vertexB) {
		if (!this.vertices.contains(vertexA)
				|| !this.vertices.contains(vertexB)) {
			throw new IllegalArgumentException("Unknown vertex provided.");
		}

		if (this.edges.contains(edge)) {
			throw new IllegalArgumentException("Edge already in graph.");
		}

		if (this.connected(vertexA, vertexB)) {
			throw new IllegalArgumentException("Vertices already connected.");
		}

		this.edges.add(edge);

		this.verticesToEdges.get(vertexA).add(edge);
		this.verticesToEdges.get(vertexB).add(edge);

		this.edgesToVertices.put(edge, new Pair<Vertex>(vertexA, vertexB));
	}

	public Edge[] getEdges() {
		Edge[] result = new Edge[this.edges.size()];
		this.edges.toArray(result);
		return result;
	}

	public boolean isConnected() {
		if (this.getNumVertices() == 0) {
			return true;
		}

		Vertex[] vertices = this.getVertices();
		Stack<Vertex> toVisit = new Stack<Vertex>();
		toVisit.add(vertices[0]);
		Set<Vertex> visited = new HashSet<Vertex>();

		while (!toVisit.isEmpty()) {
			Vertex v = toVisit.pop();
			visited.add(v);
			Vertex[] next = this.getNeighbours(v);
			for (Vertex n : next) {
				if (!visited.contains(n)) {
					toVisit.add(n);
				}
			}
		}

		return visited.size() == vertices.length;
	}

	public Vertex[] getNeighbours(Vertex vertex) {
		if (!this.vertices.contains(vertex)) {
			throw new IllegalArgumentException("Unknown vertex provided.");
		}

		Set<Edge> edges = this.verticesToEdges.get(vertex);
		List<Vertex> neighbours = new ArrayList<Vertex>();

		for (Edge edge : edges) {
			Pair<Vertex> p = this.edgesToVertices.get(edge);
			if (p.a != vertex) {
				neighbours.add(p.a);
			} else {
				neighbours.add(p.b);
			}
		}

		Vertex[] result = new Vertex[neighbours.size()];
		neighbours.toArray(result);
		return result;
	}

	public void addVertex(Vertex vertex) {
		if (this.vertices.contains(vertex)) {
			throw new IllegalArgumentException("Vertex already in graph.");
		}

		this.vertices.add(vertex);
		this.verticesToEdges.put(vertex, new HashSet<Edge>());
	}

	public void translate(double x, double y) {
		for (Vertex vertex : this.vertices) {
			vertex.setXpos(vertex.getXpos() + x);
			vertex.setYpos(vertex.getYpos() + y);
		}
	}

	public void scale(double x, double y) {
		for (Vertex vertex : this.vertices) {
			vertex.setXpos(vertex.getXpos() * x);
			vertex.setYpos(vertex.getYpos() * y);
		}
	}
	
	public void normalizeCoordinates() {
		double minX = 1, minY = 1, maxX = 0, maxY = 0;

		for (Vertex vertex : this.vertices) {
			double x = vertex.getXpos();
			double y = vertex.getYpos();
			minX = Math.min(minX, x);
			maxX = Math.max(maxX, x);
			minY = Math.min(minY, y);
			maxY = Math.max(maxY, y);
		}

		this.translate(-minX, -minY);

		double scaleX = 1;
		if (maxX-minX > 0) {
			scaleX = 1/(maxX-minX);
		}

		double scaleY = 1;
		if (maxY-minY > 0) {
			scaleY = 1/(maxY-minY);
		}

		this.scale(scaleX, scaleY);
	}

}
