package vnreal.algorithms.distributedAlg.messages;

import java.util.LinkedList;

public class NextMessage extends Message{
	private int requestId;
	//true if a node is to embed, false if a link is to embed
	private boolean nodeToEmbed;
	
	public NextMessage(long sender, int requestId, boolean nodeToEmbed, LinkedList<Long> receiverIds) {
		super(sender, MessageEnum.NEXT, receiverIds);
		this.requestId = requestId;
		this.nodeToEmbed = nodeToEmbed;
	}
	
	public NextMessage(int sender, int requestId, int receiverId) {
		super(sender, MessageEnum.NEXT, receiverId);
		this.requestId = requestId;
	}
	
	public int getRequestId() {
		return requestId;
	}
	
	public boolean isNodeToEmbed() {
		return nodeToEmbed;
	}
}
