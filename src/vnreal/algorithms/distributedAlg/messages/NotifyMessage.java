package vnreal.algorithms.distributedAlg.messages;

import java.util.LinkedList;

import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNode;

public class NotifyMessage extends Message {
	private int requestId;
	private SubstrateNode sNode;
	private VirtualNode vNode;
	private VirtualLink vLink;

	public NotifyMessage(long sender, int requestId, SubstrateNode sNode, VirtualNode vNode,
			VirtualLink vLink, LinkedList<Long> receiverIds) {
		super(sender, MessageEnum.NOTIFY, receiverIds);
		this.requestId = requestId;
		this.sNode = sNode;
		this.vNode = vNode;
		this.vLink = vLink;
	}

	public int getRequestId() {
		return requestId;
	}
	
	public SubstrateNode getSubNode() {
		return sNode;
	}
	
	public VirtualNode getVNode() {
		return vNode;
	}
	
	public VirtualLink getVLink() {
		return vLink;
	}
}
