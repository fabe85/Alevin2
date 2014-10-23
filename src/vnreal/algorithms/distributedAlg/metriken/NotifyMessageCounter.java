package vnreal.algorithms.distributedAlg.metriken;

import vnreal.algorithms.distributedAlg.DistributedAlgorithm;
import vnreal.evaluations.metrics.SimpleEvaluation;

public class NotifyMessageCounter extends SimpleEvaluation {
	public double calculate() {
		return DistributedAlgorithm.numberOfNotifyMessages;
	}
	
	public String toString() {
		return "NumberOfNotifyMessages";
	}
}
