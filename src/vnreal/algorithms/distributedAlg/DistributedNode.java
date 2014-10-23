
package vnreal.algorithms.distributedAlg;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import vnreal.algorithms.distributedAlg.messages.BellmanFordMessage;
import vnreal.algorithms.distributedAlg.messages.ImprovedMsgMessage;
import vnreal.algorithms.distributedAlg.messages.Message;
import vnreal.algorithms.distributedAlg.messages.MessageEnum;
import vnreal.algorithms.distributedAlg.messages.MsgMessage;
import vnreal.algorithms.distributedAlg.messages.NextMessage;
import vnreal.algorithms.distributedAlg.messages.NotifyMessage;
import vnreal.algorithms.distributedAlg.messages.StartMessage;
import vnreal.algorithms.distributedAlg.messages.StopMessage;
import vnreal.algorithms.utils.SubgraphBasicVN.Utils;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import edu.uci.ics.jung.graph.util.Pair;

public class DistributedNode {

	private LinkedList<Message> receivedMessages = new LinkedList<Message>();
	private LinkedList<BellmanFordMessage> bfMessages = new LinkedList<BellmanFordMessage>();
	private SubstrateNode node;
	private Vector<SubstrateNode> allSubstrateNodes = new Vector<SubstrateNode>();
	private SubstrateNetwork subNetwork;
	private LinkedList<Request> requests = new LinkedList<Request>();

	private LinkedList<Message> msgToSend = new LinkedList<Message>();
	private LinkedList<BellmanFordMessage> bfMsgToSend = new LinkedList<BellmanFordMessage>();

	public int receivedMsgsCounter = 0;
	public boolean hubNode = false;

	public HashMap<Long, DistanceEntry> shortestKnownPath = new HashMap<Long, DistanceEntry>();

	//if true, the BellmanFord algorithm is started every time before a link gets mapped
	//otherwise the algorithm is performed once per cluster mapping
	private boolean updatePathsOften = true;
	private boolean debug = true;

	DistributedNode(SubstrateNode node, SubstrateNetwork subNetwork) {
		this.node = node;
		this.subNetwork = subNetwork;
		for (SubstrateNode sn : subNetwork.getVertices()) {
			allSubstrateNodes.add(sn);
		}
	}

	/**
	 * Uses the received messages.
	 * @return New messages to send.
	 */
	public LinkedList<Message> action() {
		if (receivedMessages.size() > 0) {
			//System.out.println("Knoten " + getId() + " ist dran");
		}
		msgToSend = new LinkedList<Message>();

		receivedMsgsCounter = receivedMsgsCounter + receivedMessages.size();
		
		for (Message m : receivedMessages) {
			if (m.getMessageType() == MessageEnum.STOP) {
				StopMessage m2 = (StopMessage) m;
				//System.out.println("stop");
				addNewMessagesToSend(this.stopMsg(m2.getRequestId()));
				receivedMessages = new LinkedList<Message>();
				return (msgToSend);
			}
		}

		for (Message m : receivedMessages) {
			if (m.getMessageType() == MessageEnum.START) {
				StartMessage m2 = (StartMessage) m;
				//System.out.println("start");
				addNewMessagesToSend(this.startMsg(m2.getRequest()));
			} else if (m.getMessageType() == MessageEnum.NOTIFY) {
				//System.out.println("notify");
				NotifyMessage m2 = (NotifyMessage) m;
				addNewMessagesToSend(this.notifyMsg(m2.getRequestId(),
						m2.getVNode(), m2.getSubNode(), m2.getVLink()));
			} else if (m.getMessageType() == MessageEnum.NEXT) {
				NextMessage m2 = (NextMessage) m;
				 //System.out.println("next");
				addNewMessagesToSend(this.nextMsg(m2.getRequestId()));
			} else if (m.getMessageType() == MessageEnum.MSG) {
				//System.out.println("msgs");
				MsgMessage m2 = (MsgMessage) m;
				addNewMessagesToSend(this.msgMsg(m2.getSender(),
						m2.getAvailableCapacity()));
			} else if (m.getMessageType() == MessageEnum.STOP) {
				StopMessage m2 = (StopMessage) m;
				//System.out.println("stop");
				addNewMessagesToSend(this.stopMsg(m2.getRequestId()));
				receivedMessages = new LinkedList<Message>();
			} else if (m.getMessageType() == MessageEnum.IMPROVED_MSG) {
				ImprovedMsgMessage m2 = (ImprovedMsgMessage) m;
				addNewMessagesToSend(this.improvedMsgMsg(m2.getSender(),
						m2.getAvailableCapacity(),
						m2.getDirectLinks()));
				receivedMessages = new LinkedList<Message>();
			}
		}
		
		for (Message m : msgToSend) {
			if (m.getMessageType() == MessageEnum.STOP) {
				StopMessage m2 = (StopMessage) m;
				addNewMessagesToSend(this.stopMsg(m2.getRequestId()));
			}
		}

		receivedMessages = new LinkedList<Message>();

		return msgToSend;
	}

	/**
	 * Uses the received BellmanFordMessages.
	 * 
	 * @return New shortest paths.
	 */
	public LinkedList<BellmanFordMessage> bellmannFord() {
		bfMsgToSend = new LinkedList<BellmanFordMessage>();
		for (BellmanFordMessage m : bfMessages) {

			SubstrateNode sender = null;
			for (SubstrateNode n : allSubstrateNodes) {
				if (m.getSender() == n.getId()) {
					sender = n;
				}
			}

			if (sender == null) {
				System.out.println("Sender-Id " + m.getSender() + " unbekannt");
				System.out.println(allSubstrateNodes.size());
				for (SubstrateNode n : allSubstrateNodes) {
					System.out.print(n.getId() + " ");
				}
			}

			addNewBFMessagesToSend(updateShortestPaths(sender,
					m.getMinimumBandwidthRequired(), m.getDistances()));
		}

		bfMessages = new LinkedList<BellmanFordMessage>();

		return bfMsgToSend;

	}

	private void addNewBFMessagesToSend(LinkedList<BellmanFordMessage> newMsgs) {
		for (BellmanFordMessage m : newMsgs) {
			bfMsgToSend.add(m);
		}
	}

	/**
	 * Adds a new BellmanFordMessage to receive.
	 * @param m BellmanFordMessage.
	 */
	public void addBellmannFordMessagesToReceive(BellmanFordMessage m) {
		bfMessages.add(m);
	}

	private void addNewMessagesToSend(LinkedList<Message> newMsgs) {
		for (Message m : newMsgs) {
			msgToSend.add(m);
		}
	}

	private LinkedList<Message> startMsg(Request request) {
		requests.add(request);
		LinkedList<Message> messagesToSend = new LinkedList<Message>();

		if (request.getVirtualNodesToEmbed().size() == 0) {
			return messagesToSend;
		}
		
		//find minimum required cpu of virtual nodes
		double minimumRequiredCpu = Utils.getCpuDemand(request
				.getVirtualNodesToEmbed().get(0)).getDemandedCycles();
		for (VirtualNode vn : request.getVirtualNodesToEmbed()) {
			double cpuDemand = Utils.getCpuDemand(vn).getDemandedCycles();
			if (cpuDemand < minimumRequiredCpu) {
				minimumRequiredCpu = cpuDemand;
			}
		}

		//if own cpu too small, do nothing
		if (Utils.getCpuAvailable(this.node) < minimumRequiredCpu) {
			return messagesToSend;
		}

		allSubstrateNodes.clear();
		for (SubstrateNode sn : subNetwork.getVertices()) {
			allSubstrateNodes.add(sn);
		}

		//save all nodes with enough cpu for embedding and with no
		//virtual node embedded on them
		Vector<SubstrateNode> subNodesWithEnoughCapacity = new Vector<SubstrateNode>();

		for (SubstrateNode sn : allSubstrateNodes) {
			if (Utils.getCpuAvailable(sn) >= minimumRequiredCpu 
					&& request.getMapping().getVirtualNodes(sn) == null) {
					subNodesWithEnoughCapacity.add(sn);
			}
		}

		
		sortSubNodesAccordingToCapacity(subNodesWithEnoughCapacity);

		request.setSubNodesWithoutEmbeddedVirtNodes(subNodesWithEnoughCapacity);
		
		sortVirtualNodesAccordingToCapacity(request.getVirtualNodesToEmbed());
		
		SubstrateNode subHub = subNodesWithEnoughCapacity.get(0);
		
		
		if (DistributedAlgorithm.improvedAlgorithm) {
			subHub = getNextHub(request);
			if (subHub == null) {
				if (debug) {
					System.out.println("No substrate nodes with enough bw found");
				}
				messagesToSend.add(new StopMessage(this.getId(), request
						.getRequestId(), false, getIdList(subNetwork
						.getVertices())));
				return messagesToSend;
			}
		}

		//if this node isn't the node with the most cpu, do nothing
		if (node.getId() != subHub.getId()) {
			return messagesToSend;
		}
		
		//get the virtual hub and the virtual spokes to embed
		VirtualNode hub = request.getVirtualNodesToEmbed().get(0);
		Collection<VirtualNode> neighbors = request.getVirtualNetwork()
				.getNeighbors(hub);
		Vector<VirtualNode> spokes = new Vector<VirtualNode>();
		
		for (VirtualNode vn : neighbors) {
			if (request.getVirtualLinksToEmbed().contains(
					findDirectPathInVirt(hub, vn, request.getVirtualNetwork(), true)) 
							&& request.getVirtualNodesToEmbed().contains(vn)) {
				spokes.add(vn);
			}
		}

		//mapping
		messagesToSend.addAll(hubSpokesMapping(hub, spokes,
				request.getRequestId()));

		//if nothing to embed anymore, send stop message
		if (request.getVirtualNodesToEmbed().size() == 0
				&& request.getVirtualLinksToEmbed().size() == 0) {
			messagesToSend.add(new StopMessage(this.getId(), request
					.getRequestId(), true, getIdList(subNetwork
					.getVertices())));
		} else {
			//if unmapped nodes, send message to node with the most free cpu
			if (request.getVirtualNodesToEmbed().size() > 0) {
				sortSubNodesAccordingToCapacity(request.getSubNodesWithoutEmbeddedVirtNodes());
				
				if (!DistributedAlgorithm.improvedAlgorithm) {		
					messagesToSend.add(new NextMessage(this.getId(), 
							request.getRequestId(), true, 
							getIdList(request.getSubNodesWithoutEmbeddedVirtNodes().firstElement())));
				} else {
					messagesToSend.add(new NextMessage(this.getId(), 
							request.getRequestId(), true, 
							getIdList(getNextHub(request))));
				}
				
				//if unmapped links, send message to one of the connected nodes
			} else if (request.getVirtualLinksToEmbed().size() > 0) {
				sortVirtualLinks(request.getVirtualLinksToEmbed());
				VirtualLink l = request.getVirtualLinksToEmbed().firstElement();
				Pair<VirtualNode> p = request.getVirtualNetwork().getEndpoints(l);
				SubstrateNode sn = request.getMapping().getSubstrateNode(p.getFirst());
				messagesToSend.add(new NextMessage(getId(), request.getRequestId(),
						false, getIdList(sn)));
			}
		}
		return messagesToSend;
	}

	private LinkedList<Long> getIdList(Collection<SubstrateNode> subNodes) {
		LinkedList<Long> idList = new LinkedList<Long>();
		for (SubstrateNode sNode : subNodes) {
			if (sNode.getId() != getId()) {
				idList.add(sNode.getId());
			}
		}
		return idList;
	}
	
	/**
	 * Returns the SubstrateNode with the most available cpu and maybe enough
	 * bandwidth at the direct links.
	 * @param request
	 * @return
	 */
	private SubstrateNode getNextHub(Request request) {
		VirtualNode vnWithMaxReqCpu = sortVirtualNodesAccordingToCapacity(request.getVirtualNodesToEmbed()).get(0);
		
		//copy subnode vector
		Vector<SubstrateNode> subNodes = new Vector<SubstrateNode>();
		for (SubstrateNode sn : request.getSubNodesWithoutEmbeddedVirtNodes()) {
			subNodes.add(sn);
		}

		//get required maximal bandwidth and required sum of bandwidth
		double sumOfRequiredBw = 0;
		double maxRequiredBW = 0;
		for (VirtualLink vl : request.getVirtualNetwork().getIncidentEdges(vnWithMaxReqCpu)) {
			if (request.getVirtualLinksToEmbed().contains(vl)) {
				double bwDemand = Utils.getBandwidthDemand(vl);
				sumOfRequiredBw += bwDemand;
				if (bwDemand > maxRequiredBW) {
					maxRequiredBW = bwDemand;
				}
			}
		}
		
		LinkedList<SubstrateNode> snToDelete = new LinkedList<SubstrateNode>();
		
		//delete all nodes with not enough bandwidth from the list
		for (SubstrateNode sn : subNodes) {
			double sumOfBW = 0;
			double maxBW = 0;
			for (SubstrateLink sl : subNetwork.getIncidentEdges(sn)) {
				sumOfBW += Utils.getBandwidthAvailable(sl);
				if (Utils.getBandwidthAvailable(sl) > maxBW) {
					maxBW = Utils.getBandwidthAvailable(sl);
				}
			}
			
			if (sumOfBW < sumOfRequiredBw || maxBW < maxRequiredBW) {
				snToDelete.add(sn);
			}
		}
		
		for (SubstrateNode sn : snToDelete) {
			subNodes.remove(sn);
		}
		
		if (subNodes.size() == 0) {
			return null;
		}
		
		
		sortSubNodesAccordingToCapacity(subNodes);
		return subNodes.get(0);
	}
	
	private LinkedList<Long> getIdList(SubstrateNode subNode) {
		LinkedList<Long> idList = new LinkedList<Long>();
		idList.add(subNode.getId());
		return idList;
	}

	private LinkedList<Message> notifyMsg(int reqId, VirtualNode vNode,
			SubstrateNode sNode, VirtualLink vLink) {
		Request request = null;
		for (Request r : requests) {
			if (reqId == r.getRequestId()) {
				request = r;
			}
		}

		if (request == null) {
			return (new LinkedList<Message>());
		}

		request.getVirtualNodesToEmbed().remove(vNode);
		request.getSubNodesWithoutEmbeddedVirtNodes().remove(sNode);
		// link and reversed link are removed in the mapALink method
		// request.getVirtualLinksToEmbed().remove(vLink);
		// request.getMapping().add(vNode, sNode);

		return (new LinkedList<Message>());
	}

	private LinkedList<Message> nextMsg(int reqId) {
		Request request = null;
		for (Request r : requests) {
			if (reqId == r.getRequestId()) {
				request = r;
			}
		}

		if (request == null) {
			return (new LinkedList<Message>());
		}

		LinkedList<Message> messagesToSend = new LinkedList<Message>();

		//if nodes to embed
		if (request.getVirtualNodesToEmbed().size() != 0) {
			VirtualNode hub = request.getVirtualNodesToEmbed().get(0);
			sortSubNodesAccordingToCapacity(request
					.getSubNodesWithoutEmbeddedVirtNodes());

			//if this node is the node with the most cpu
			if (this.getId() == request.getSubNodesWithoutEmbeddedVirtNodes()
					.get(0).getId() || Utils.getCpuAvailable(this.node) 
					== Utils.getCpuAvailable(request.getSubNodesWithoutEmbeddedVirtNodes()
					.get(0))) {

				//find the neighbors
				Collection<VirtualNode> neighbors = request.getVirtualNetwork()
						.getNeighbors(hub);
				Vector<VirtualNode> spokes = new Vector<VirtualNode>();

				//find all not mapped neighbor nodes of the hub, which links
				//shall be embedded
				for (VirtualNode vn : neighbors) {
					if (request.getVirtualLinksToEmbed().contains(
							findDirectPathInVirt(hub, vn,
									request.getVirtualNetwork(), true))
							&& !request.getMapping().isMapped(vn)) {
						spokes.add(vn);
					}
				}

				
				messagesToSend.addAll(hubSpokesMapping(hub, spokes, reqId));
			
			}
			
			//if unmapped nodes, send message to node with the most free cpu
			sortSubNodesAccordingToCapacity(request.getSubNodesWithoutEmbeddedVirtNodes());
			
			if (request.getVirtualNodesToEmbed().size() > 0) {
				if (!DistributedAlgorithm.improvedAlgorithm) {		
					messagesToSend.add(new NextMessage(this.getId(), 
							request.getRequestId(), true, 
							getIdList(request.getSubNodesWithoutEmbeddedVirtNodes().firstElement())));
				} else {
					messagesToSend.add(new NextMessage(this.getId(), 
							request.getRequestId(), true, 
							getIdList(getNextHub(request))));
				}
			}

		}

		//if no nodes but links to embed
		if (request.getVirtualNodesToEmbed().size() == 0
				&& request.getVirtualLinksToEmbed().size() != 0) {

			if (request.getMapping().getVirtualNodes(this.node) == null) {
				return messagesToSend;
			}

			for (VirtualNode hub : request.getMapping().getVirtualNodes(this.node)) {
	
				for (VirtualNode virtNeighbor : request.getVirtualNetwork().getNeighbors(
						hub)) {
					
					SubstrateNode subNeighbor = request.getMapping()
							.getSubstrateNode(virtNeighbor);
					VirtualLink toMap = findDirectPathInVirt(hub, virtNeighbor,
							request.getVirtualNetwork(), true);
					
					if (request.getVirtualLinksToEmbed().contains(toMap)) {
	
						DistributedAlgorithm.bellmanFordAlgorithmus(Utils
								.getBandwidthDemand(toMap));
	
						if (shortestKnownPath.get(subNeighbor.getId()) == null) {
							if (debug) {
								System.out.println(request.getMapping().getVirtualNodes(this.node).size());
								System.out
										.println("Hub-Knoten " + getId() + " findet keinen Pfad zu Spokes-Knoten " + subNeighbor.getId()
												+ " mit genuegend Bandbreite! " + Utils.getBandwidthDemand(toMap));
								for (SubstrateNode sn : subNetwork.getNeighbors(subNeighbor)) {
									System.out.print(Utils.getBandwidthAvailable(findDirectPathInSub(subNeighbor, sn, true)) + " ");
								}
								System.out.println();
							}
							messagesToSend.add(new StopMessage(this.getId(), reqId,
									false, getIdList(allSubstrateNodes)));
							return messagesToSend;
						}
	
						if (!mapALink(hub, virtNeighbor, shortestKnownPath.get(subNeighbor.getId())
										.getPath(), request)) {
							messagesToSend.add(new StopMessage(getId(), request
									.getRequestId(), false,
									getIdList(allSubstrateNodes)));
							if (debug) {
								System.out.println("MapALink hat nicht geklappt");
							}
							return messagesToSend;
						}
	
						messagesToSend.add(new NotifyMessage(this.getId(),
								reqId, null, null, toMap,
								getIdList(allSubstrateNodes)));
					}
				}
			}
			
			//if there are still virtual links to embed, send nextMessage to
			//a substrate node on which one of the virtual nodes of the link
			//is embedded
			if (request.getVirtualLinksToEmbed().size() != 0) {
				sortVirtualLinks(request.getVirtualLinksToEmbed());
				VirtualLink l = request.getVirtualLinksToEmbed().firstElement();
				Pair<VirtualNode> p = request.getVirtualNetwork().getEndpoints(l);
				SubstrateNode sn = request.getMapping().getSubstrateNode(p.getFirst());
				messagesToSend.add(new NextMessage(getId(), reqId, false, getIdList(sn)));
			}
		}
		
		if (request.getVirtualNodesToEmbed().size() == 0
				&& request.getVirtualLinksToEmbed().size() == 0) {
			messagesToSend.add(new StopMessage(this.getId(), reqId, true,
					getIdList(allSubstrateNodes)));
		}


		return messagesToSend;
	}

	private LinkedList<Message> stopMsg(int reqId) {
		Request toRemove = null;

		for (Request r : requests) {
			if (r.getRequestId() == reqId) {
				toRemove = r;
			}
		}

		if (toRemove != null) {
			requests.remove(toRemove);
		}
		return (new LinkedList<Message>());
	}

	private LinkedList<Message> msgMsg(long sender, double nodeCapacity) {
		//cause of java references no "real" update needed
		
		//usually the node should send MsgMessages to all other nodes
		//but since that happens only once, that's implemented in the
		//DistributedAlgorithm class
		
		return (new LinkedList<Message>());
	}
	
	private LinkedList<Message> improvedMsgMsg(long sender, double nodeCapacity, 
			Collection<SubstrateLink> links) {
		//cause of java references no "real" update needed
		
		//usually the node should send MsgMessages to all other nodes
		//but since that happens only once, that's implemented in the
		//DistributedAlgorithm class
		
		return (new LinkedList<Message>());
	}

	/**
	 * Adds new messages to receive.
	 * @param messages Messages.
	 */
	public void addMessagesToReceive(LinkedList<Message> messages) {
		for (Message m : messages) {
			receivedMessages.add(m);
		}
	}

	/**
	 * Adds a new message to receive.
	 * @param m A message.
	 */
	public void addMessageToReceive(Message m) {
		receivedMessages.add(m);
	}

	private Vector<SubstrateNode> sortSubNodesAccordingToCapacity(
			Vector<SubstrateNode> subNodeVector) {
		for (int i = 0; i < subNodeVector.size(); i++) {
			for (int j = 0; j < subNodeVector.size(); j++) {
				if (Utils.getCpuAvailable(subNodeVector.get(i)) > Utils
						.getCpuAvailable(subNodeVector.get(j))) {
					SubstrateNode tempNode = subNodeVector.get(i);
					subNodeVector.set(i, subNodeVector.get(j));
					subNodeVector.set(j, tempNode);
				}
			}
		}

		return subNodeVector;
	}

	private Vector<VirtualNode> sortVirtualNodesAccordingToCapacity(
			Vector<VirtualNode> virtNodeVector) {
		for (int i = 0; i < virtNodeVector.size(); i++) {
			for (int j = 0; j < virtNodeVector.size(); j++) {
				if (Utils.getCpuDemand(virtNodeVector.get(i)).getDemandedCycles() > Utils
						.getCpuDemand(virtNodeVector.get(j)).getDemandedCycles()) {
					VirtualNode tempNode = virtNodeVector.get(i);
					virtNodeVector.set(i, virtNodeVector.get(j));
					virtNodeVector.set(j, tempNode);
				}
			}
		}

		return virtNodeVector;
	}
	
	private Vector<VirtualLink> sortVirtualLinks(
			Vector<VirtualLink> linkVector) {
		for (int i = 0; i < linkVector.size(); i++) {
			for (int j = 0; j < linkVector.size(); j++) {
				if (Utils.getBandwidthDemand(linkVector.get(i)) > Utils
						.getBandwidthDemand(linkVector.get(j))) {
					VirtualLink tempLink = linkVector.get(i);
					linkVector.set(i, linkVector.get(j));
					linkVector.set(j, tempLink);
				}
			}
		}

		return linkVector;
	}

	/**
	 * Returns the id of the substrate node.
	 * @return The id of the substrate node.
	 */
	public long getId() {
		return node.getId();
	}

	private boolean refreshInformation(VirtualNode hub,
			Vector<VirtualNode> spokes, Vector<DistanceEntry> pathList,
			Vector<SubstrateNode> subSpokes, Request request) {
		spokes = sortVirtualNodesAccordingToCapacity(spokes);

		// FIXME: changing the maximalBandwidth/CPUDemand to a more precise
		//value for each link/node could result in a better acceptRatio
		double maximalBandwidthDemand = Utils
				.getBandwidthDemand(findDirectPathInVirt(hub, spokes.get(0),
						request.getVirtualNetwork(), true));
		double maximalCPUDemand = Utils.getCpuDemand(spokes.get(0)).getDemandedCycles();

		// find the maximal needed bandwidth and cpu
		for (VirtualNode spoke : spokes) {
			if (maximalBandwidthDemand < Utils
					.getBandwidthDemand(findDirectPathInVirt(hub, spoke,
							request.getVirtualNetwork(), true))) {
				maximalBandwidthDemand = Utils
						.getBandwidthDemand(findDirectPathInVirt(hub, spoke,
								request.getVirtualNetwork(), true));
			}

			if (maximalCPUDemand < Utils.getCpuDemand(spoke).getDemandedCycles()) {
				maximalCPUDemand = Utils.getCpuDemand(spoke).getDemandedCycles();
			}
		}

		DistributedAlgorithm.bellmanFordAlgorithmus(maximalBandwidthDemand);

		for (SubstrateNode subNode : request
				.getSubNodesWithoutEmbeddedVirtNodes()) {
			if (Utils.getCpuAvailable(subNode) >= maximalCPUDemand
					&& shortestKnownPath.get(subNode.getId()) != null) {
				pathList.add(shortestKnownPath.get(subNode.getId()));
			}
		}

		// FIXME: a more intelligent selection of the hub could result
		//in a better acceptRatio
		if (pathList.size() < spokes.size()) {
			if (debug) {
				System.out
						.println("Hub-Knoten findet nicht genug Spokes-Knoten, "
								+ "die die Bandbreiten/CPU-Demands erfuellen! ("
								+ spokes.size()
								+ " Knoten mit Bandbreitenanbindung "
								+ maximalBandwidthDemand
								+ " und CPU "
								+ maximalCPUDemand + ")");
			}
			return false;
		}

		sortPathes(pathList);

		// get as many substrate nodes as we need for the virtual spoke nodes
		for (int x = 0; x < spokes.size(); x++) {
			if (pathList.get(x).getN2().getId() != this.getId()) {
				subSpokes.add(pathList.get(x).getN2());
			} else {
				subSpokes.add(pathList.get(x).getN1());
			}
		}

		sortSubNodesAccordingToCapacity(subSpokes);

		return true;
	}

	private LinkedList<Message> hubSpokesMapping(VirtualNode hub,
			Vector<VirtualNode> spokes, int requestId) {
		if (debug) {
			System.out.println(hub.getId() + " hat " + spokes.size() + " Spokes");
		}
		
		Request request = null;
		for (Request r : requests) {
			if (requestId == r.getRequestId()) {
				request = r;
			}
		}

		LinkedList<Message> msgToSend = new LinkedList<Message>();

		if (Utils.fulfills(this.node, hub)) {
			DistributedAlgorithm.demandedResources.addAll(Utils
					.occupyResources(hub.get(), this.node.get()));
		} else {
			System.out.println("Dieser Knoten hat nicht genug CPU fuer Hub! "
					+ Utils.getCpuAvailable(this.node) + " <-> "
					+ Utils.getCpuDemand(hub));
			msgToSend.add(new StopMessage(getId(), requestId, false,
					getIdList(allSubstrateNodes)));
			return msgToSend;
		}

		request.getMapping().add(hub, this.node);
		//msgToSend.add(new MsgMessage(this.getId(), true, this.getId(), Utils
				//.getCpuAvailable(this.node), getIdList(allSubstrateNodes)));
		request.getVirtualNodesToEmbed().remove(hub);
		request.getSubNodesWithoutEmbeddedVirtNodes().remove(this.node);
		msgToSend.add(new NotifyMessage(this.getId(), requestId, this.node,
				hub, null, getIdList(allSubstrateNodes)));

		//no spokes to embed
		if (spokes.size() == 0) {
			return msgToSend;
		}

		Vector<DistanceEntry> pathList = new Vector<DistanceEntry>();
		Vector<SubstrateNode> subSpokes = new Vector<SubstrateNode>();

		if (!updatePathsOften) {
			if (!refreshInformation(hub, spokes, pathList, subSpokes, request)) {
				msgToSend.add(new StopMessage(getId(), requestId, false,
						getIdList(allSubstrateNodes)));
				return msgToSend;
			}
		}

		while (spokes.size() != 0) {
			if (updatePathsOften) {
				pathList = new Vector<DistanceEntry>();
				subSpokes = new Vector<SubstrateNode>();

				if (!refreshInformation(hub, spokes, pathList, subSpokes,
						request)) {
					msgToSend.add(new StopMessage(getId(), requestId, false,
							getIdList(allSubstrateNodes)));
					return msgToSend;
				}
			}

			if (Utils.fulfills(subSpokes.get(0), spokes.get(0))) {
				DistributedAlgorithm.demandedResources.addAll(Utils
						.occupyResources(spokes.get(0).get(), subSpokes.get(0).get()));
			} else {
				System.out
						.println("Doesn't fulfill cpu demands! (hubSpokesMapping2): "
								+ Utils.getCpuDemand(spokes.get(0)) + " <-> ");
				for (SubstrateNode s : subSpokes) {
					System.out.print(Utils.getCpuAvailable(s) + " ");
				}
				msgToSend.add(new StopMessage(getId(), requestId, false,
						getIdList(allSubstrateNodes)));
				return msgToSend;
			}

			request.getMapping().add(spokes.get(0), subSpokes.get(0));
			//msgToSend.add(new MsgMessage(this.getId(), true,
			//		subSpokes.get(0).getId(), Utils.getCpuAvailable(subSpokes
			//				.get(0)), getIdList(allSubstrateNodes)));

			if (!mapALink(hub, spokes.get(0), pathList.get(0).getPath(),
					request)) {
				msgToSend.add(new StopMessage(getId(), request.getRequestId(),
						false, getIdList(allSubstrateNodes)));
				return msgToSend;
			}

			msgToSend.add(new NotifyMessage(this.getId(), requestId, subSpokes
					.get(0), spokes.get(0), findDirectPathInVirt(hub,
					spokes.get(0), request.getVirtualNetwork(), true),
					getIdList(allSubstrateNodes)));
			request.getVirtualNodesToEmbed().remove(spokes.get(0));
			spokes.remove(0);
			request.getSubNodesWithoutEmbeddedVirtNodes().remove(
					subSpokes.get(0));
			subSpokes.remove(subSpokes.get(0));
			pathList.remove(0);

		}

		return msgToSend;

	}

	private void sortPathes(Vector<DistanceEntry> pathList) {
		for (int i = 0; i < pathList.size(); i++) {
			for (int j = 0; j < pathList.size(); j++) {
				if (pathList.get(i).getDistance() > (pathList.get(j)
						.getDistance())) {
					DistanceEntry temp = pathList.get(i);
					pathList.set(i, pathList.get(j));
					pathList.set(j, temp);
				}
			}
		}
	}

	private LinkedList<BellmanFordMessage> updateShortestPaths(
			SubstrateNode sender, double minimumBandwidthRequired,
			LinkedList<DistanceEntry> receivedDistances) {
		LinkedList<BellmanFordMessage> msgsToSend = new LinkedList<BellmanFordMessage>();
		LinkedList<DistanceEntry> newDistances = new LinkedList<DistanceEntry>();

		//node gets a message from a node who isn't a direct neighbor, shouldn't happen
		if (shortestKnownPath.get(sender.getId()) == null) {
			if (debug) {
				System.out.println(this.node.getId()
						+ " bekommt Nachricht von " + sender.getId()
						+ ", obwohl das kein direkter Nachbar ist!");
			}
			return msgsToSend;
		}

		//if the link from sender to this node has not enough bandwidth, 
		//ignore the sender's suggestions 
		if (Utils.getBandwidthAvailable(findDirectPathInSub(this.node, sender,
				true)) < minimumBandwidthRequired) {
			if (debug) {
				System.out
						.println("Knoten sendet Nachricht ueber Link mit zu wenig Bandbreite!");
			}
			return msgsToSend;
		}

		for (DistanceEntry newDistance : receivedDistances) {

			if (newDistance.getN2().getId() != this.getId()) {
				
				//if no known path to the new node or the distance of 
				//sender to new node + the distance of sender to this node is
				//shorter than the known path from this node to the new node
				//take the new path
				if (shortestKnownPath.get(newDistance.getN2().getId()) == null
						|| (newDistance.getDistance() + shortestKnownPath.get(
								sender.getId()).getDistance()) < shortestKnownPath
								.get(newDistance.getN2().getId()).getDistance()) {
					LinkedList<SubstrateLink> path = new LinkedList<SubstrateLink>();
					path.addAll(newDistance.getPath());
					path.addAll(shortestKnownPath.get(sender.getId()).getPath());
					double distance = newDistance.getDistance()
							+ shortestKnownPath.get(sender.getId())
									.getDistance();

					DistanceEntry newEntry = new DistanceEntry(this.node,
							newDistance.getN2(), path, distance);
					shortestKnownPath
							.put(newDistance.getN2().getId(), newEntry);

					newDistances.add(newEntry);
				}
			}
		}

		if (newDistances.size() == 0) {
			return (new LinkedList<BellmanFordMessage>());
		}

		LinkedList<Long> ids = new LinkedList<Long>();

		// send message only to nodes who are connected to this node with
		// enough bandwidth
		for (SubstrateNode s : subNetwork.getNeighbors(this.node)) {
			if (Utils.getBandwidthAvailable(findDirectPathInSub(this.node, s,
					true)) >= minimumBandwidthRequired) {
				ids.add(s.getId());
			}
		}

		msgsToSend.add(new BellmanFordMessage(this.getId(),
				minimumBandwidthRequired, newDistances, ids));

		return msgsToSend;
	}

	private double getAdditiveWeight(SubstrateLink l) {
		return 1;
	}


	/**
	 * Fills shortestKnownPath with the direct connections to the neighbors of
	 * this node. Ignores connections with less than the minimumBandwidth.
	 * 
	 * @param minimumBandwidth
	 * @return A BellmanFordMessage to the neighbors
	 */
	public BellmanFordMessage getFirstBFMessage(double minimumBandwidth) {
		shortestKnownPath.clear();

		//fill the shortestKnownPath with the direct connections of the neighbors
		for (SubstrateNode neighbor : subNetwork.getNeighbors(this.node)) {
			LinkedList<SubstrateLink> path = new LinkedList<SubstrateLink>();
			path.add(findDirectPathInSub(this.node, neighbor, true));

			if (Utils.getBandwidthAvailable(path.getFirst()) >= minimumBandwidth) {
				DistanceEntry e = new DistanceEntry(this.node, neighbor, path,
						getAdditiveWeight(path.getFirst()));
				shortestKnownPath.put(neighbor.getId(), e);
			}
		}

		LinkedList<DistanceEntry> entries = new LinkedList<DistanceEntry>();
		LinkedList<Long> ids = new LinkedList<Long>();

		for (SubstrateNode neighbor : subNetwork.getNeighbors(this.node)) {
			if (shortestKnownPath.get(neighbor.getId()) != null) {
				entries.add(shortestKnownPath.get(neighbor.getId()));
				ids.add(neighbor.getId());

			}
		}

		return (new BellmanFordMessage(this.getId(), minimumBandwidth, entries, ids));
	}

	/**
	 * Returns the direct substrate path between two nodes.
	 * @param s1 SubstrateNode 1
	 * @param s2 SubstrateNode 2
	 * @param direction If true then the edge from s1 to s2 is returned,
	 * otherwise the edge from s2 to s1.
	 * @return The substrate path between two nodes or null
	 */
	private SubstrateLink findDirectPathInSub(SubstrateNode s1,
			SubstrateNode s2, boolean direction) {
		if (direction) {
			return subNetwork.findEdge(s1, s2);
		} else {

			for (SubstrateLink sl : subNetwork.getOutEdges(s2)) {
				if (subNetwork.getEndpoints(sl).getFirst().getId() == s1
						.getId()
						|| subNetwork.getEndpoints(sl).getSecond().getId() == s1
								.getId()) {
					return sl;
				}
			}
		}

		//this algorithms only works for networks with undirected links, 
		//so should never happen as long as used in this context
		System.out.println("kein direkter Pfad gefunden!");
		return null;

	}

	private VirtualLink findDirectPathInVirt(VirtualNode v1, VirtualNode v2,
			VirtualNetwork vn, boolean direction) {

		if (direction) {
			return vn.findEdge(v1, v2);
		} else {

			for (VirtualLink vl : vn.getOutEdges(v2)) {
				if (vn.getEndpoints(vl).getFirst().getId() == v1.getId()
						|| vn.getEndpoints(vl).getSecond().getId() == v1
								.getId()) {
					return vl;
				}
			}
		}

		System.out.println("kein direkter Pfad gefunden!");
		return null;

	}

	/**
	 * Maps a virtual link to a substrate path and the reversed virtual link too
	 * @param v1 VirtualNode1
	 * @param v2 VirtualNode2
	 * @param path Substrate path
	 * @param request
	 * @return True if it worked, false otherwise
	 */
	private boolean mapALink(VirtualNode v1, VirtualNode v2,
			List<SubstrateLink> path, Request request) {
		VirtualNetwork vn = request.getVirtualNetwork();

		// create reversed path, because in ALEVIN links are directed
		List<SubstrateLink> reversedPath = new LinkedList<SubstrateLink>();
		for (SubstrateLink s : path) {
			Pair<SubstrateNode> p = subNetwork.getEndpoints(s);

			if (findDirectPathInSub(p.getFirst(), p.getSecond(), true).getId() == s
					.getId()) {
				reversedPath.add(findDirectPathInSub(p.getFirst(),
						p.getSecond(), false));
			} else {
				reversedPath.add(findDirectPathInSub(p.getFirst(),
						p.getSecond(), true));
			}
		}

		VirtualLink vl1ToEmbed = findDirectPathInVirt(v1, v2, vn, true);
		VirtualLink vl2ToEmbed = findDirectPathInVirt(v1, v2, vn, false);

		if (Utils.fulfills(path, vl1ToEmbed.get())) {
			DistributedAlgorithm.demandedResources.addAll(Utils
					.occupyPathResources(vl1ToEmbed, path, subNetwork, false));
			DistributedAlgorithm.demandedResources.addAll(Utils
					.occupyPathResources(vl2ToEmbed, reversedPath, subNetwork, false));
		} else {
			System.out.println("link doesn't fulfill demands");
			System.out.print(Utils.getBandwidthDemand(vl1ToEmbed) + " <-> ");
			for (SubstrateLink l : path) {
				System.out.print(Utils.getBandwidthAvailable(l) + " ");
			}
			System.out.println();
			return false;
		}

		request.getMapping().add(vl1ToEmbed, path);
		request.getMapping().add(vl2ToEmbed, reversedPath);
		request.getVirtualLinksToEmbed().remove(vl1ToEmbed);
		request.getVirtualLinksToEmbed().remove(vl2ToEmbed);
		return true;
	}

	/**
	 * Returns how many messages have been received.
	 * @return Number of received messages.
	 */
	public int getReceivedMessages() {
		return receivedMessages.size();
	}
	
	/**
	 * Returns the SubstrateNode.
	 * @return The SubstrateNode.
	 */
	public SubstrateNode getNode() {
		return node;
	}

}
