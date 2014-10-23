package vnreal.algorithms.distributedAlg.metriken;

import vnreal.algorithms.distributedAlg.DistributedAlgorithm;
import vnreal.evaluations.metrics.SimpleEvaluation;

public class BFRunCounter extends SimpleEvaluation {
	public double calculate() {
		return DistributedAlgorithm.bfRun;
	}
	
	public String toString() {
		return "NumberOfBellmanFordRuns";
	}
}
