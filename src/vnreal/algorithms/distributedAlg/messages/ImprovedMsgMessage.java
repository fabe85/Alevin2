package vnreal.algorithms.distributedAlg.messages;

import java.util.Collection;
import java.util.LinkedList;

import vnreal.network.substrate.SubstrateLink;


public class ImprovedMsgMessage extends Message {
	private double availableCapacity;
	private double nodeWithCapacity;
	private Collection<SubstrateLink> directLinks;

	public ImprovedMsgMessage(long sender, long nodeWithCapacity, Collection<SubstrateLink> directLinks,
			double availableCapacity, LinkedList<Long> receiverIds) {
		super(sender, MessageEnum.IMPROVED_MSG, receiverIds);
		this.availableCapacity = availableCapacity;
		this.nodeWithCapacity = nodeWithCapacity;
		this.directLinks = directLinks;
	}
	
	public double getAvailableCapacity() {
		return availableCapacity;
	}
	
	public double getNodeWithCapacity() {
		return nodeWithCapacity;
	}
	
	public Collection<SubstrateLink> getDirectLinks() {
		return directLinks;
	}
}

