package vnreal.algorithms.distributedAlg.metriken;

import vnreal.algorithms.distributedAlg.DistributedAlgorithm;
import vnreal.evaluations.metrics.SimpleEvaluation;

public class ClusterCounter extends SimpleEvaluation {
	public double calculate() {
		return DistributedAlgorithm.clusterCounter;
	}
	
	public String toString() {
		return "ClusterCounter";
	}
}
