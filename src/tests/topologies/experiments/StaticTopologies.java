package tests.topologies.experiments;

import edu.uci.ics.jung.graph.Graph;
import tests.scenarios.util.GraphMetrics;
import vnreal.generators.INetworkGenerator;
import vnreal.generators.WaxmanGenerator;

public class StaticTopologies {
	private StaticTopologies() {
	}

	/**
	 * Get four waxman generators for connected networks with 2, 8, 16 and 32
	 * nodes.
	 * 
	 * @return the generators.
	 */
	public static INetworkGenerator[] connectedWaxman() {
		long seed = 1234;
		INetworkGenerator[] gens = new INetworkGenerator[4];
		for (int i = 0; i < gens.length; i++) {
			while (true) {
				WaxmanGenerator vgen = new WaxmanGenerator();
				vgen.setNumNodes((int) Math.pow(2, i + 2));
				vgen.setAlpha(0.5);
				vgen.setBeta(0.5);
				vgen.setSeed(seed++);
				if (GraphMetrics.connected((Graph) vgen.generateVirtualNetwork(1))) {
					gens[i] = vgen;
					break;
				}
			}
		}
		return gens;
	}
}
