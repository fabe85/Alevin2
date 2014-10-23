package vnreal.algorithms.distributedAlg.messages;

import java.util.LinkedList;

import vnreal.algorithms.distributedAlg.Request;

public class StartMessage extends Message {
	private Request request;

	//NodeKapazitaet ??
	public StartMessage(long sender, Request request, LinkedList<Long> receiverIds) {
		super(sender, MessageEnum.START, receiverIds);
		this.request = request;
	}

	public int getId() {
		return request.getRequestId();
	}
	
	public Request getRequest() {
		return request;
	}
}
