package vnreal.algorithms.distributedAlg.messages;

import java.util.LinkedList;

public class Message {
	private MessageEnum m;
	private LinkedList<Long> receiverIds = new LinkedList<Long>();
	private long sender;
	
	Message(long sender, MessageEnum m, LinkedList<Long> receiverIds) {	
		this.m = m;
		this.receiverIds = receiverIds;
		this.sender = sender;
	}
	
	Message(int sender, MessageEnum m, long receiverId) {	
		this.m = m;
		this.receiverIds.add(receiverId);
		this.sender = sender;
	}
	
	public MessageEnum getMessageType() {
		return m;
	}
	
	public LinkedList<Long> getReceiverIds() {
		return receiverIds;
	}
	
	public long getSender() {
		return sender;
	}
	
	
}