package vnreal.generators.transitstub.generators;

import vnreal.generators.transitstub.graph.Graph;

public abstract class AGenerator {

	private static final int MAX_TRIES = 100;

	public abstract Graph generate(int numVertices, long seed);

	public Graph generateConnected(int numVertices, long seed) {
		Graph result;
		int tries = 0;
		do {
			result = this.generate(numVertices, seed + tries);
			tries++;
		} while (!result.isConnected() && tries <= MAX_TRIES);

		if (tries > MAX_TRIES) {
			throw new RuntimeException("Failed to generate connected graph.");
		}

		return result;
	}

}
