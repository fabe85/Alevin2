package tests.topologies.embeddingalgos;

import vnreal.algorithms.AbstractAlgorithm;
import vnreal.algorithms.NodeRankingPathSplitting;
import vnreal.network.NetworkStack;

/**
 * Placeholder for {@link NodeRankingPathSplitting}.
 * 
 * @author Philip Huppert
 */
public class Pathsplitting extends FutureEmbeddingAlgorithm {
	private static final boolean DEFAULT_NODE_OVERLOAD = false;
	private static final boolean DEFAULT_IS_DISTANCE = false;
	private static final int DEFAULT_DISTANCE = 0;
	private static final double DEFAULT_BW_WEIGHT = 1.0;
	private static final double DEFAULT_CPU_WEIGHT = 1.0;

	private double weightCpu;
	private double weightBw;
	private int distance;
	private boolean isDistance;
	private boolean nodeOverload;

	public Pathsplitting() {
		this(DEFAULT_CPU_WEIGHT, DEFAULT_BW_WEIGHT, DEFAULT_DISTANCE,
				DEFAULT_IS_DISTANCE, DEFAULT_NODE_OVERLOAD);
	}

	public Pathsplitting(double weightCpu, double weightBw, int distance,
			boolean isDistance, boolean nodeOverload) {
		this.weightCpu = weightCpu;
		this.weightBw = weightBw;
		this.distance = distance;
		this.isDistance = isDistance;
		this.nodeOverload = nodeOverload;
	}

	@Override
	public AbstractAlgorithm getAlgorithm(NetworkStack nstack) {
		return new NodeRankingPathSplitting(nstack, weightCpu, weightBw,
				distance, isDistance, nodeOverload);
	}

	@Override
	public String toString() {
		return "Pathsplitting [weightCpu=" + weightCpu + "; weightBw="
				+ weightBw + "; distance=" + distance + "; isDistance="
				+ isDistance + "; nodeOverload=" + nodeOverload + "]";
	}

}
