package vnreal.algorithms.distributedAlg;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import mulavito.algorithms.AbstractAlgorithmStatus;
import vnreal.algorithms.AbstractAlgorithm;
import vnreal.algorithms.distributedAlg.messages.BellmanFordMessage;
import vnreal.algorithms.distributedAlg.messages.ImprovedMsgMessage;
import vnreal.algorithms.distributedAlg.messages.Message;
import vnreal.algorithms.distributedAlg.messages.MessageEnum;
import vnreal.algorithms.distributedAlg.messages.MsgMessage;
import vnreal.algorithms.distributedAlg.messages.NextMessage;
import vnreal.algorithms.distributedAlg.messages.StartMessage;
import vnreal.algorithms.distributedAlg.messages.StopMessage;
import vnreal.algorithms.utils.SubgraphBasicVN.ResourceDemandEntry;
import vnreal.algorithms.utils.SubgraphBasicVN.Utils;
import vnreal.demands.AbstractDemand;
import vnreal.network.Network;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.resources.AbstractResource;
import vnreal.resources.CpuResource;
import edu.uci.ics.jung.graph.util.Pair;

public class DistributedAlgorithm extends AbstractAlgorithm {
	private static final int DEFAULT_ID = -1;
	private static NetworkStack stack;
	private Iterator<VirtualNetwork> curIt = null;
	private Iterator<? extends Network<?, ?, ?>> curNetIt = null;
	
	/**
	 * List of the distributed nodes.
	 */
	public static LinkedList<DistributedNode> distributedNodes;
	
	/**
	 * Number of sent messages.
	 */
	public static int numberOfMessages = 0;
	
	/**
	 * Number of sent BellmanFordMessages.
	 */
	public static long numberOfBFMessages = 0;
	
	/**
	 * Number of sent BellmanFordMessages during the first BellmanFord run.
	 */
	public static int numberOfFirstBFMessages = 0;
	
	/**
	 * Counts the BellmanFord runs.
	 */
	public static int bfRun = 0;
	
	/**
	 * Counts the created clusters.
	 */
	public static int clusterCounter = 0;
	
	/**
	 * If true, an improved version of the algorithm is being used.
	 */
	public static boolean improvedAlgorithm = false;
	
	/**
	 * If one message is sent over three links, the value is 3. If 
	 * another message is sent over the same three links, the value is 6.
	 */
	public static int usedLinksForMessages = 0;
	
	/**
	 * Counts the nodes who are only forwarding and have no virtual nodes embedded.
	 */
	public static int nodesUsedSolelyForForwarding = 0;
	
	/**
	 * Counts the number of sent Msg messages.
	 */
	public static int numberOfMsgMessages = 0;
	
	/**
	 * Counts the number of Notify messages.
	 */
	public static int numberOfNotifyMessages = 0;
	
	/**
	 * A list of the demanded ressources.
	 */
	public static LinkedList<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> demandedResources;
	
	/**
	 * A list of the nodes who are sending messages.
	 */
	public static LinkedList<SubstrateNode> nodesUsedForMsgSending = new LinkedList<SubstrateNode>();
	
	private static HashMap<Long, HashMap<Long, DistanceEntry>> shortestPaths = new HashMap<Long, HashMap<Long, DistanceEntry>>();

	public DistributedAlgorithm(NetworkStack stack) {
		DistributedAlgorithm.stack = stack;
		this.curNetIt = stack.iterator();
		this.curIt = null;
	};
	
	public static void reset() {
		resetMetricValues();
	}

	@Override
	protected void evaluate() {
		SubstrateNetwork sNetwork = stack.getSubstrate();

		distributedNodes = new LinkedList<DistributedNode>();

		// create distributed nodes
		for (SubstrateNode sNode : sNetwork.getVertices()) {
			distributedNodes.add(new DistributedNode(sNode, sNetwork));
		}
		
		bellmanFordAlgorithmus(-1.0);
		
		//save shortest paths in this class for sending the messages
		for (DistributedNode sNode : distributedNodes) {
			HashMap<Long, DistanceEntry> map = new HashMap<Long, DistanceEntry>();
			
			//copy hashmap
			for (Long i : sNode.shortestKnownPath.keySet()) {
				map.put(i, sNode.shortestKnownPath.get(i));
			}
			shortestPaths.put(sNode.getId(), map);
		}

		// create Requests
		LinkedList<Request> requests = new LinkedList<Request>();
		int id = 0;
		while (hasNext()) {
			VirtualNetwork vn = getNext();
			//System.out.println(vn);
			requests.add(new Request(id, vn));
			id++;
		}

		boolean mappingFailed = false;
		LinkedList<Boolean> mappingFails = new LinkedList<Boolean>();
		clusterCounter = 0;
		
		for (Request r : requests) {
			
			demandedResources = new LinkedList<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>>();
			
			LinkedList<Message> msgsToSend = new LinkedList<Message>();
			LinkedList<Message> startMsg = new LinkedList<Message>();
			LinkedList<Message> msgMsg = new LinkedList<Message>();
			
			startMsg.add(new StartMessage(DEFAULT_ID, r,
					getDistributedNodeIds(distributedNodes)));
			sendMessages(startMsg);
			clusterCounter++;
			
			
			if (!improvedAlgorithm) {
				//for the check root procedure every node sends his capacity
				//to all other nodes
				for (DistributedNode dn : distributedNodes) {
					msgMsg.add(new MsgMessage(dn.getId(), dn.getId(),
							Utils.getCpuAvailable(dn.getNode()), 
							getDistributedNodeIds(distributedNodes)));
				}
				sendMessages(msgMsg);
			} else {
				//for the check root procedure every node sends his capacity
				//and the bandwidth of his direct links to all other nodes
				for (DistributedNode dn : distributedNodes) {
					msgMsg.add(new ImprovedMsgMessage(dn.getId(), dn.getId(),
							sNetwork.getIncidentEdges(dn.getNode()),
							Utils.getCpuAvailable(dn.getNode()),
							getDistributedNodeIds(distributedNodes)));
				}
				sendMessages(msgMsg);
			}

			boolean stop = false;
			mappingFailed = false;
			int counter = 0;
			
			while (!stop) {
				counter++;
				if (counter > 1000000) {
					System.out.println("Mapping schlaegt fehl, aber " +
							"Algorithmus erkennt das nicht. Zu mappen N/L " 
							+ r.getVirtualNodesToEmbed().size() + "/" 
							+ r.getVirtualLinksToEmbed().size());
					throw (new Error());
				}
				
				int msgs = 0;
				// all nodes may do sth
				for (DistributedNode n : distributedNodes) {
					if (!mappingFailed) {
						msgsToSend = n.action();
						msgs = msgs + msgsToSend.size();
						sendMessages(msgsToSend);
	
						for (Message m : msgsToSend) {
							if (m.getMessageType() == MessageEnum.STOP) {
								stop = true;
								StopMessage stopMsg = (StopMessage) m;
								
								if (!stopMsg.getMappingSuccessful()) {
									Utils.freeResources(demandedResources);
									mappingFailed = true;
								}
							}
						}
						
					}
				}
				
				if (msgs == 0) {
					System.out.println("es wird nichts mehr geschickt");
					System.out.println("Mapping failed: " + mappingFailed + " " 
					+ r.getVirtualNodesToEmbed().size() + " " + r.getVirtualLinksToEmbed().size());
				
				}
				
			}
			
			for (DistributedNode n : distributedNodes) {
				n.action();
			}
			
			mappingFails.add(mappingFailed);
			
		}
		
		
		for (SubstrateNode sn : nodesUsedForMsgSending) {
			double cpu = 0;
			for (AbstractResource res : sn.get()) {
				if (res instanceof CpuResource) {
					cpu = ((CpuResource) res).getCycles();
				}
			}
			
			if (cpu == Utils.getCpuAvailable(sn)) {
				nodesUsedSolelyForForwarding++;
			}
		}
		
		System.out.println("Mapping failed: ");
		for (Boolean b : mappingFails) {
			System.out.print(b + " ");
		}
		System.out.println();
		System.out.println("BellmanFord-Durchliauufe: " + bfRun);
		System.out.println("NormalMessages: " + numberOfMessages + " (" + numberOfMsgMessages + " MsgMsgs) ");
		System.out.println("BfMessages: " + numberOfBFMessages + " (" + numberOfFirstBFMessages + " first BF)");
		System.out.println("Bf-Messages per Run: " + numberOfBFMessages / bfRun);
		System.out.println("Msgs received/sent: " + getMsgsReceivedPerNode() + "/" + getMsgsSentPerNode());
	}

	private static void sendMessages(LinkedList<Message> msgToSend) {
		for (Message m : msgToSend) {
			
			for (DistributedNode n2 : distributedNodes) {

				// check which node receives which message
				for (Long receiver : m.getReceiverIds()) {
					if ((long) receiver == n2.getId()) {
						
						n2.addMessageToReceive(m);
						
						if (m.getSender() != DEFAULT_ID && n2.getId() != m.getSender()) {
							DistanceEntry entry = shortestPaths.get(n2.getId()).get(m.getSender());
							if (entry == null) {
								System.out.println("Node " + n2.getId() + " weiss nich wie er zu " + m.getSender() + " schickn soll");
							}
							
							if (m.getMessageType() == MessageEnum.MSG) {
								//numberOfMsgMessages = numberOfMsgMessages + entry.getPath().size();
								numberOfMsgMessages++;
							} else if (m.getMessageType() == MessageEnum.NOTIFY) {
								numberOfNotifyMessages++;
							} else if (m.getMessageType() == MessageEnum.NEXT) {
								NextMessage next = (NextMessage) m;
								if (next.isNodeToEmbed()) {
									clusterCounter++;
								}
							}
							
							
							//numberOfMessages = numberOfMessages + entry.getPath().size();
							numberOfMessages++;
							usedLinksForMessages = usedLinksForMessages + entry.getPath().size();
							
							for (SubstrateLink l : entry.getPath()) {
								Pair<SubstrateNode> p = stack.getSubstrate().getEndpoints(l);
								if (!nodesUsedForMsgSending.contains(p.getFirst()) 
										&& p.getFirst().getId() != m.getSender()
										&& p.getFirst().getId() != receiver) {
									nodesUsedForMsgSending.add(p.getFirst());
								}
								if (!nodesUsedForMsgSending.contains(p.getSecond())
										&& p.getSecond().getId() != m.getSender()
										&& p.getSecond().getId() != receiver) {
									nodesUsedForMsgSending.add(p.getSecond());
								}
							}
							
							
							/*
							DistanceEntry entry = n2.shortestKnownPath.get(m.getSender());
							
							if (entry == null) {
								System.out.println(n2.getId() + " -> " + m.getSender());
								System.out.println(stack.getSubstrate());
							}*/
						}
					}
				}
			}
		}
	}
	
	private static void sendBFMessages(LinkedList<BellmanFordMessage> msgToSend) {
		for (BellmanFordMessage m : msgToSend) {
			
			for (DistributedNode n2 : distributedNodes) {

				// check which node receives which message
				for (Long i : m.getReceiverIds()) {
					if ((long) i == n2.getId()) {
						n2.addBellmannFordMessagesToReceive(m);
						
						BellmanFordMessage bf = (BellmanFordMessage) m;
						
						if (bf.getMinimumBandwidthRequired() != -1) {
							numberOfBFMessages++;
						} else {
							numberOfFirstBFMessages++;
						}
					}
				}
			}
		}
	}

	/**
	 * Starts the distributed Bellman-Ford algorithm.
	 * @param minimumBandwidth
	 */
	public static void bellmanFordAlgorithmus(double minimumBandwidth) {
		bfRun++;
		
		LinkedList<BellmanFordMessage> msgsToSend = new LinkedList<BellmanFordMessage>();
		for (DistributedNode n : distributedNodes) {
			msgsToSend.add(n.getFirstBFMessage(minimumBandwidth));
		}
		sendBFMessages(msgsToSend);
		
		boolean stop = false;
		
		while (!stop) {
			int sending = 0;
			
			for (DistributedNode n : distributedNodes) {
				msgsToSend = n.bellmannFord();
				sendBFMessages(msgsToSend);
				
				if (msgsToSend.size() != 0) {
					sending++;
				}
			}
			
			if (sending == 0) {
				stop = true;
			}
			
			sending = 0;
		}
		
	}
	
	@Override
	public List<AbstractAlgorithmStatus> getStati() {
		return null;
	}

	@Override
	protected boolean preRun() {
		resetMetricValues();
		return true;
	}

	@Override
	protected void postRun() {
		return;
	}

	protected VirtualNetwork getNext() {
		if (!hasNext())
			return null;
		else {
			return curIt.next();
		}
	}


	@SuppressWarnings("unchecked")
	protected boolean hasNext() {
		if (curIt == null || !curIt.hasNext()) {
			if (curNetIt.hasNext()) {
				@SuppressWarnings("unused")
				Network<?, ?, ?> tmp = curNetIt.next();

				curIt = (Iterator<VirtualNetwork>) curNetIt;
				return hasNext();
			} else
				return false;
		} else
			return true;
	}

	private LinkedList<Long> getDistributedNodeIds(
			LinkedList<DistributedNode> nodes) {
		LinkedList<Long> ids = new LinkedList<Long>();
		
		for (DistributedNode n : nodes) {
			ids.add(n.getId());
		}
		return ids;
	}
	
	/**
	 * Returns the amount of sent messages.
	 * @return The amount of sent messages.
	 */
	public static double getMsgsSentPerNode() {
		//size added cause of msg message
		return (numberOfMessages + distributedNodes.size()) / distributedNodes.size();
	}
	
	/**
	 * Returns the amount of received messages.
	 * @return The amount of received messages.
	 */
	public static double getMsgsReceivedPerNode() {
		double received = 0;
		for (DistributedNode n : distributedNodes) {
			received = received + n.receivedMsgsCounter;
		}
		
		return received / distributedNodes.size();
	}
	
	private static void resetMetricValues() {
		numberOfBFMessages = 0;
		numberOfMessages = 0;
		bfRun = 0;
		usedLinksForMessages = 0;
		nodesUsedSolelyForForwarding = 0;
		numberOfMsgMessages = 0;
		numberOfNotifyMessages = 0;
		nodesUsedForMsgSending = new LinkedList<SubstrateNode>();
		clusterCounter = 0;
	}

}
