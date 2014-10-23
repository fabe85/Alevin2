package vnreal.algorithms.distributedAlg.messages;

import java.util.LinkedList;

import vnreal.algorithms.distributedAlg.DistanceEntry;

public class BellmanFordMessage extends Message {
	private LinkedList<DistanceEntry> distances;
	private double minimumBandwidthRequired;

	public BellmanFordMessage(long sender, double minimumBandwidthRequired, LinkedList<DistanceEntry> distances, 
			LinkedList<Long> receiverIds) {
		super(sender, MessageEnum.BELLMANFORD, receiverIds);
		this.setDistances(distances);
		this.setMinimumBandwidthRequired(minimumBandwidthRequired);
	}

	public LinkedList<DistanceEntry> getDistances() {
		return distances;
	}

	public void setDistances(LinkedList<DistanceEntry> distances) {
		this.distances = distances;
	}

	public double getMinimumBandwidthRequired() {
		return minimumBandwidthRequired;
	}

	public void setMinimumBandwidthRequired(double minimumBandwidthRequired) {
		this.minimumBandwidthRequired = minimumBandwidthRequired;
	}
}
