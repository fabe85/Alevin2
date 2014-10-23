package tests.scenarios.util;

import mulavito.graph.IEdge;
import mulavito.graph.IVertex;
import edu.uci.ics.jung.graph.Graph;
import vnreal.network.Network;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.virtual.VirtualNetwork;

public abstract class StackSummarizer {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String summarize(NetworkStack stack) {
		StringBuilder summary = new StringBuilder();
		summary.append(this.getSummaryTitle() + "\n");
		for (Network n : stack) {
			if (n instanceof SubstrateNetwork) {
				summary.append(" Substrate: " + this.evaluateGraph((Graph<IVertex, IEdge>) n) + "\n");
			} else if (n instanceof VirtualNetwork) {
				summary.append(" Virtual: " + this.evaluateGraph((Graph<IVertex, IEdge>) n) + "\n");
			} else {
				throw new RuntimeException("Cannot summarize unknown network: " + n.toString());
			}
		}
		return summary.toString();
	}

	protected abstract String evaluateGraph(Graph<IVertex, IEdge> graph);

	protected String getSummaryTitle() {
		return "Generic stack summary:";
	}
}
