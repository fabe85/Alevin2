package vnreal.algorithms.distributedAlg;

import java.util.Vector;

import vnreal.algorithms.utils.SubgraphBasicVN.Mapping;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

public class Request {
	private int id;
	private VirtualNetwork virtNetwork;
	private Vector<VirtualNode> virtualNodesToEmbed;
	private Vector<VirtualLink> virtualLinksToEmbed;
	private Mapping mapping = new Mapping(false);
	private Vector<SubstrateNode> subNodesWithoutEmbeddedVirtNode;
	
	public Request(int id, VirtualNetwork virtNetwork) {
		this.id = id;
		this.virtNetwork = virtNetwork;
		
		virtualNodesToEmbed = new Vector<VirtualNode>();

		for (VirtualNode vNode : virtNetwork.getVertices()) {
			virtualNodesToEmbed.add(vNode);
		}
		
		virtualLinksToEmbed = new Vector<VirtualLink>();

		for (VirtualLink vLink : virtNetwork.getEdges()) {
			virtualLinksToEmbed.add(vLink);
		}
		
		mapping.clear();
		
	}
	
	public int getRequestId() {
		return id;
	}
	
	public VirtualNetwork getVirtualNetwork() {
		return virtNetwork;
	}
	
	public Mapping getMapping() {
		return mapping;
	}
	
	public Vector<VirtualNode> getVirtualNodesToEmbed() {
		return virtualNodesToEmbed;
	}
	
	public Vector<VirtualLink> getVirtualLinksToEmbed() {
		return virtualLinksToEmbed;
	}
	
	public void setSubNodesWithoutEmbeddedVirtNodes(Vector<SubstrateNode> subNodes) {
		subNodesWithoutEmbeddedVirtNode = new Vector<SubstrateNode>();
		for (SubstrateNode sn : subNodes) {
			subNodesWithoutEmbeddedVirtNode.add(sn);
		}
	}
	
	public Vector<SubstrateNode> getSubNodesWithoutEmbeddedVirtNodes() {
		return subNodesWithoutEmbeddedVirtNode;
	}
	
	
	
}
