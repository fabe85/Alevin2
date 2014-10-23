package vnreal.algorithms.distributedAlg.metriken;

import vnreal.algorithms.distributedAlg.DistributedAlgorithm;
import vnreal.evaluations.metrics.SimpleEvaluation;

public class NormalMessageCounter extends SimpleEvaluation {
	public double calculate() {
		return DistributedAlgorithm.numberOfMessages;
	}
	
	public String toString() {
		return "NumberOfNormalMessages";
	}
}
