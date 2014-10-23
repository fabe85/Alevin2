package vnreal.algorithms.basicVN;

//package vnreal.algorithms.energy.basicVN;
//
//import java.util.Collection;
//import java.util.LinkedList;
//import java.util.List;
//
//import vnreal.algorithms.utils.energy.Mapping;
//import vnreal.algorithms.utils.energy.Utils;
//import vnreal.network.NetworkStack;
//import vnreal.network.substrate.SubstrateLink;
//import vnreal.network.substrate.SubstrateNetwork;
//import vnreal.network.substrate.SubstrateNode;
//import vnreal.network.virtual.VirtualLink;
//import vnreal.network.virtual.VirtualNetwork;
//import vnreal.network.virtual.VirtualNode;
//
///**
// * See "Algorithms for Assigning Substrate Network Resources to Virtual Network Components".
// * 
// * @author Michael Till Beck
// */
//public final class SubVNAlgorithm extends BasicVNAssignmentAlgorithm {
//	
//	/**
//	 * delta_N, delta_L: default = 1
//	 */
//	public SubVNAlgorithm(NetworkStack<?, ?, ?, ?> stack, int delta_N, int delta_L, int epsilon) {
//		super(stack, delta_N, delta_L, epsilon);
//	}
//
//	
//	public Mapping mapNetwork(SubstrateNetwork sNetwork, VirtualNetwork vNetwork) {
//		Mapping result = new Mapping();
//		
//		List<SubstrateNode> v_a = new LinkedList<SubstrateNode>();
//
//		if (Utils.mapIdDemands(vNetwork, sNetwork, result) == null) {
//			return null;
//		}
//
//		// create subVNs
//		Collection<VirtualNode> g_r = vNetwork.getVertices();
//		Collection<VirtualNode> usedNodes = new LinkedList<VirtualNode>();
//		while (!g_r.isEmpty()) {
//			
//			Collection<VirtualNode> constrainedSubVNNodes = new LinkedList<VirtualNode>();
//			
//			// find maximum degree
//			VirtualNode maxDegreeNode = null;
//			int maxDegree = 0, tmp = 0;
//			for (VirtualNode vn : g_r) {
//				if (maxDegreeNode == null
//						|| (tmp = vNetwork.getInEdges(vn).size() + vNetwork.getOutEdges(vn).size()) > maxDegree) {
//					
//					maxDegreeNode = vn;
//					maxDegree = tmp;
//				}
//			}
//			g_r.remove(maxDegreeNode);
//			
//			// create subVN
//			VirtualNetwork subVNnetwork = new VirtualNetwork(1);
//			subVNnetwork.addVertex(maxDegreeNode);   // add center
//			boolean subVNConstrained = false;
//			for (VirtualLink vl : vNetwork.getInEdges(maxDegreeNode)) { //TODO: -> method
//				VirtualNode opposite = vNetwork.getOpposite(maxDegreeNode, vl);
//				boolean constrained = usedNodes.contains(opposite);
//				if (constrained) {
//					subVNConstrained = true;
//				}
//				subVNnetwork.addVertex(opposite);
//				subVNnetwork.addEdge(vl, opposite, maxDegreeNode);
//				if (!constrained) {
//					usedNodes.add(opposite);
//					constrainedSubVNNodes.add(opposite);
//				}
//			}
//			for (VirtualLink vl : vNetwork.getOutEdges(maxDegreeNode)) {
//				VirtualNode opposite = vNetwork.getOpposite(maxDegreeNode, vl);
//				boolean constrained = usedNodes.contains(opposite);
//				if (constrained) {
//					subVNConstrained = true;
//				}
//				subVNnetwork.addEdge(vl, maxDegreeNode, opposite);
//				if (!constrained) {
//					usedNodes.add(opposite);
//					constrainedSubVNNodes.add(opposite);
//				}
//			}
//			boolean centerConstrained = usedNodes.contains(maxDegreeNode);
//			if (centerConstrained) {
//				subVNConstrained = true;
//			}
//
//			if (subVNConstrained) {
//				SubstrateNode substrateCenter = null;
//				if (centerConstrained) {
//					substrateCenter = result.getSubstrateNode(maxDegreeNode);
//				} else {
//					SubstrateNode biggestNodeStressNode = Utils.findSubstrateNodeWithBiggestStress(sNetwork);
//					SubstrateLink biggestLinkStressLink = Utils.findSubstrateLinkWithBiggestStress(sNetwork);
//					substrateCenter = getNRNode(sNetwork, biggestNodeStressNode.getStressLevel(), biggestLinkStressLink.getStressLevel());
//				}
//				
//				mapSubVN(sNetwork, subVNnetwork, substrateCenter, centerConstrained);
//			} else {
//				mapNetwork(sNetwork, subVNnetwork);
//			}
//		}
//		
//		
//	}
//	
//	public double subVNNodePotential(SubstrateNetwork sNetwork, SubstrateNode v,
//			List<SubstrateNode> currentlyChosenNodes,
//			int biggestNodeStress, int biggestLinkStress, double delta_L, double delta_N) {
//		
//		double sum = 0.0;
//		for (SubstrateNode n : currentlyChosenNodes) {
//			DistanceEntry distance = distance(sNetwork, v, n, null, demandlessDijkstra);
//			if (distance != null) {
//				sum += distance.distance;
//			}
//		}
//		
//		int tmp = biggestNodeStress - v.getStressLevel();
//		return tmp == 0 ? sum : (sum / tmp);
//	}
//	
//	public Mapping mapSubVN(SubstrateNetwork sNetwork, VirtualNetwork vNetwork, SubstrateNode center, boolean centerConstrained) {
//		Mapping result = new Mapping();
//		
//		int biggestNodeStress = Utils.findSubstrateNodeWithBiggestStress(sNetwork).getStressLevel();
//		int biggestLinkStress = Utils.findSubstrateLinkWithBiggestStress(sNetwork).getStressLevel();
//
//		List<SubstrateNode> v_a = new LinkedList<SubstrateNode>();
//
//		if (centerConstrained) {
//			SubstrateNode nrNode = getNRNode(sNetwork, biggestNodeStress, biggestLinkStress);
//			v_a.add(nrNode);
//		}
//		
//		fill_v_a(vNetwork, sNetwork, v_a, biggestNodeStress, biggestLinkStress);
//		
//		List<Entry<SubstrateNode, Integer>> nrValues = get_nrValuesList(sNetwork, v_a, biggestNodeStress, biggestLinkStress);
//		List<Entry<VirtualNode, Integer>> degrees = get_degreesList(vNetwork);
//
//		if (!mapNodes(degrees, nrValues, result)) {
//			return null;
//		}
//		if (!mapLinks(vNetwork, sNetwork, biggestLinkStress, result)) {
//			return null;
//		}
//		
//		return result;
//	}
//	
//	private static class SubVNNode extends VirtualNode {
//		public VirtualNode vn;
//		public boolean isConstrained;
//		public SubVNNode(int layer, VirtualNode vn, boolean isConstrained) {
//			super(layer);
//			this.vn = vn;
//			this.isConstrained = isConstrained;
//		}
//	}
//	
// }
