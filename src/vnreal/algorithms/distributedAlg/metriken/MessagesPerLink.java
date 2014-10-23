package vnreal.algorithms.distributedAlg.metriken;

import vnreal.algorithms.distributedAlg.DistributedAlgorithm;
import vnreal.evaluations.metrics.SimpleEvaluation;

public class MessagesPerLink extends SimpleEvaluation {
	public double calculate() {
		return DistributedAlgorithm.usedLinksForMessages / stack.getSubstrate().getEdges().size();
	}
	
	public String toString() {
		return "MessagesPerLink";
	}
}
