package vnreal.algorithms.distributedAlg.metriken;

import vnreal.algorithms.distributedAlg.DistributedAlgorithm;
import vnreal.evaluations.metrics.SimpleEvaluation;

public class NodesUsedSolelyForForwarding extends SimpleEvaluation {
	public double calculate() {
		return DistributedAlgorithm.nodesUsedSolelyForForwarding;
	}
	
	public String toString() {
		return "NodesUsedSolelyForForwarding";
	}
}
