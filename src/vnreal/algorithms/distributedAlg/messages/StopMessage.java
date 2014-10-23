package vnreal.algorithms.distributedAlg.messages;

import java.util.LinkedList;

public class StopMessage extends Message{
	private int requestId;
	private boolean mappingSuccessful;
	
	public StopMessage(long sender, int requestId, boolean mappingSuccessful, LinkedList<Long> receiverIds) {
		super(sender, MessageEnum.STOP, receiverIds);
		this.requestId = requestId;
		this.mappingSuccessful = mappingSuccessful;
	}
	
	public int getRequestId() {
		return requestId;
	}
	
	public boolean getMappingSuccessful() {
		return mappingSuccessful;
	}
}
