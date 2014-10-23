package vnreal.algorithms.distributedAlg.metriken;

import vnreal.algorithms.distributedAlg.DistributedAlgorithm;
import vnreal.evaluations.metrics.SimpleEvaluation;

public class BFMessageCounter extends SimpleEvaluation {
	public double calculate() {
		return (double) (DistributedAlgorithm.numberOfBFMessages);
	}
	
	public String toString() {
		return "NumberOfBellmanFordMessages";
	}
}
