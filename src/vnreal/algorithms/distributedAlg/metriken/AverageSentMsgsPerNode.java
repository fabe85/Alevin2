package vnreal.algorithms.distributedAlg.metriken;

import vnreal.algorithms.distributedAlg.DistributedAlgorithm;
import vnreal.evaluations.metrics.SimpleEvaluation;

public class AverageSentMsgsPerNode extends SimpleEvaluation {
	public double calculate() {
		return DistributedAlgorithm.getMsgsSentPerNode();
	}
	
	public String toString() {
		return "AverageMsgsSentPerNode";
	}
}
