package tests.topologies.embeddingalgos;

import vnreal.algorithms.AbstractAlgorithm;
import vnreal.algorithms.basicVN.BasicVNAssignmentAlgorithm;
import vnreal.network.NetworkStack;

/**
 * Placeholder for {@link BasicVNAssignmentAlgorithm}.
 * 
 * @author Philip Huppert
 */
public class Stressbased extends FutureEmbeddingAlgorithm {
	private static final boolean DEFAULT_USE_ENERGY_DEMAND = false;
	private static final int DEFAULT_DELTA_L = 1;
	private static final int DEFAULT_DELTA_N = 1;

	private double delta_N;
	private double delta_L;
	private boolean useEnergyDemand;

	public Stressbased() {
		this(DEFAULT_DELTA_N, DEFAULT_DELTA_L, DEFAULT_USE_ENERGY_DEMAND);
	}

	public Stressbased(double delta_N, double delta_L, boolean useEnergyDemand) {
		this.delta_N = delta_N;
		this.delta_L = delta_L;
		this.useEnergyDemand = useEnergyDemand;
	}

	@Override
	public AbstractAlgorithm getAlgorithm(NetworkStack nstack) {
		return new BasicVNAssignmentAlgorithm(nstack, this.delta_N,
				this.delta_L, this.useEnergyDemand);
	}

	@Override
	public String toString() {
		return "Stressbased [delta_N=" + delta_N + "; delta_L=" + delta_L
				+ "; useEnergyDemand=" + useEnergyDemand + "]";
	}
}
