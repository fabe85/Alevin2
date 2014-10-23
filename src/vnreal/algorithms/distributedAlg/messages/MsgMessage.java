package vnreal.algorithms.distributedAlg.messages;

import java.util.LinkedList;

public class MsgMessage extends Message {
	private double availableCapacity;
	private double nodeWithCapacity;

	public MsgMessage(long sender, long nodeWithCapacity, double availableCapacity, LinkedList<Long> receiverIds) {
		super(sender, MessageEnum.MSG, receiverIds);
		this.availableCapacity = availableCapacity;
		this.nodeWithCapacity = nodeWithCapacity;
	}
	
	public double getAvailableCapacity() {
		return availableCapacity;
	}
	
	public double getNodeWithCapacity() {
		return nodeWithCapacity;
	}
}
