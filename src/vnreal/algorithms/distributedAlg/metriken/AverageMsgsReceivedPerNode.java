package vnreal.algorithms.distributedAlg.metriken;

import vnreal.algorithms.distributedAlg.DistributedAlgorithm;
import vnreal.evaluations.metrics.SimpleEvaluation;

public class AverageMsgsReceivedPerNode extends SimpleEvaluation {
	public double calculate() {
		return DistributedAlgorithm.getMsgsReceivedPerNode();
	}
	
	public String toString() {
		return "AverageMsgsReceivedPerNode";
	}
}
