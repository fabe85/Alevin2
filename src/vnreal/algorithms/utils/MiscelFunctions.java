/* ***** BEGIN LICENSE BLOCK *****
 * Copyright (C) 2010-2011, The VNREAL Project Team.
 * 
 * This work has been funded by the European FP7
 * Network of Excellence "Euro-NF" (grant agreement no. 216366)
 * through the Specific Joint Developments and Experiments Project
 * "Virtual Network Resource Embedding Algorithms" (VNREAL). 
 *
 * The VNREAL Project Team consists of members from:
 * - University of Wuerzburg, Germany
 * - Universitat Politecnica de Catalunya, Spain
 * - University of Passau, Germany
 * See the file AUTHORS for details and contact information.
 * 
 * This file is part of ALEVIN (ALgorithms for Embedding VIrtual Networks).
 *
 * ALEVIN is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License Version 3 or later
 * (the "GPL"), or the GNU Lesser General Public License Version 3 or later
 * (the "LGPL") as published by the Free Software Foundation.
 *
 * ALEVIN is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * or the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and
 * GNU Lesser General Public License along with ALEVIN; see the file
 * COPYING. If not, see <http://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ***** */
package vnreal.algorithms.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import vnreal.demands.AbstractDemand;
import vnreal.demands.BandwidthDemand;
import vnreal.demands.CpuDemand;
import vnreal.hiddenhopmapping.BandwidthCpuHiddenHopMapping;
import vnreal.hiddenhopmapping.IHiddenHopMapping;
import vnreal.mapping.Mapping;
import vnreal.network.Link;
import vnreal.network.Network;
import vnreal.network.NetworkStack;
import vnreal.network.Node;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;
import vnreal.resources.BandwidthResource;
import vnreal.resources.CpuResource;

/**
 * Class with some util functions that are called from one or more virtual
 * network embedding algorithm
 * 
 * @author Juan Felipe Botero
 */

public class MiscelFunctions {

	/**
	 * 
	 * @param d
	 *            number to round to two decimal places
	 * @return rounded double value
	 */
	public static Double roundTwoDecimals(double d) {
		return roundToDecimals(d, 2);
	}

	/**
	 * 
	 * @param d
	 *            number to round to three decimal places
	 * @return rounded double value
	 */
	public static Double roundThreeDecimals(double d) {
		return roundToDecimals(d, 3);
	}

	/**
	 * Round the double d to the with c values after the comma
	 * 
	 * @param d
	 * @param c
	 * @return
	 */
	public static double roundToDecimals(double d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(Double.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}

	/**
	 * 
	 * @param d
	 *            number to round to twelve decimal places
	 * @return rounded double value
	 */
	public static double roundTwelveDecimals(double d) {
		return roundToDecimals(d, 12);
	}

	/**
	 * 
	 * @param d
	 *            number to round to six decimal places
	 * @return rounded double value
	 */
	public static double roundSixDecimals(double d) {
		return roundToDecimals(d, 6);
	}

	/**
	 * Process the results of the LP solver to create an answer in a HashedMap
	 * Structure with the substrate and virtual link and the LP solver answer.
	 * 
	 * @param solverResult
	 *            Result of the solver
	 * @param value
	 *            variable of the solver that will be processed
	 * @return Hashed map structure with organized solver results
	 * 
	 */
	public static Map<List<String>, Double> processSolverResult(
			Map<String, Double> solverResult, String value) {
		Map<List<String>, Double> newSolverResult = new LinkedHashMap<List<String>, Double>();

		int i;
		String word, subword;
		StringTokenizer elements, subElements;
		List<String> values, subValues = null;

		for (Iterator<String> cad = solverResult.keySet().iterator(); cad
				.hasNext();) {
			String ntmp = cad.next();
			Double vtmp = solverResult.get(ntmp);

			elements = new StringTokenizer(ntmp, value);
			values = new LinkedList<String>();

			while (elements.hasMoreTokens()) {
				word = elements.nextToken();
				i = 1;
				subElements = new StringTokenizer(word, ",");
				subValues = new LinkedList<String>();
				while (subElements.hasMoreTokens()) {
					subword = subElements.nextToken();
					subValues.add(subword);
					i++;
				}
				values.addAll(subValues);
			}
			newSolverResult.put(values, vtmp);
		}
		return newSolverResult;
	}

	/**
	 * 
	 * @param vNet
	 *            Virtual Network Request
	 * @return revenue of vNet
	 */
	public static double calculateVnetRevenue(VirtualNetwork vNet) {
		double total_demBW = 0;
		double total_demCPU = 0;
		Iterable<VirtualLink> tmpLinks;
		Iterable<VirtualNode> tmpNodes;
		tmpLinks = vNet.getEdges();
		tmpNodes = vNet.getVertices();
		for (Iterator<VirtualLink> tmpLink = tmpLinks.iterator(); tmpLink
				.hasNext();) {
			VirtualLink tmpl = tmpLink.next();
			for (AbstractDemand dem : tmpl) {
				if (dem instanceof BandwidthDemand) {
					total_demBW += ((BandwidthDemand) dem)
							.getDemandedBandwidth();
					break; // continue with next link
				}
			}
		}
		for (Iterator<VirtualNode> tmpNode = tmpNodes.iterator(); tmpNode
				.hasNext();) {
			VirtualNode tmps = tmpNode.next();
			for (AbstractDemand dem : tmps) {
				if (dem instanceof CpuDemand) {
					total_demCPU += ((CpuDemand) dem).getDemandedCycles();
					break; // continue with next node
				}
			}
		}
		return (total_demBW + total_demCPU);
	}

	/**
	 * Method to sort the set of virtual networks taking into account the
	 * revenues
	 * 
	 * @param stack
	 *            set of VNRs and substrate net
	 * @return ordered VNR stack by revenues
	 */
	@SuppressWarnings("unchecked")
	public static NetworkStack sortByRevenues(NetworkStack stack) {
		Map<Object, Double> Revenues = new LinkedHashMap<Object, Double>();
		Iterable networks = new LinkedList<Network<?, ?, ?>>();
		double revenue;
		for (Iterator<Network<?, ?, ?>> net = stack.iterator(); net.hasNext();) {
			Network<?, ?, ?> tmpN = net.next();
			if (tmpN.getLayer() != 0) {
				revenue = calculateVnetRevenue((VirtualNetwork) tmpN);
				Revenues.put(((VirtualNetwork) tmpN), revenue);
			}
		}
		Map<Object, Double> sortedRevenues = sortByValue(Revenues);
		networks = sortedRevenues.keySet();

		List<VirtualNetwork> virtualNetworks = new LinkedList<VirtualNetwork>();
		for (Iterator<Network<?, ?, ?>> n = networks.iterator(); n.hasNext();) {
			Network<?, ?, ?> tmp = n.next();
			virtualNetworks.add((VirtualNetwork) tmp);
		}
		NetworkStack new_stack = new NetworkStack(stack.getSubstrate(),
				virtualNetworks);
		return new_stack;
	}

	/**
	 * 
	 * @param map
	 * @return Ordered Map structure
	 */
	@SuppressWarnings( { "unchecked"})
	public static Map sortByValue(Map map) {
		List list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o2, Object o1) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
						.compareTo(((Map.Entry) (o2)).getValue());
			}
		});

		Map result = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	/**
	 * 
	 * @param net
	 * @param node
	 * @return available resources (or demands) of one node
	 */
	public static double getAr(Network<?, ?, ?> net, Node<?> node) {
		double incBw = 0, nodeAr = 0;

		if (net instanceof VirtualNetwork) {
			if (node instanceof VirtualNode) {
				for (AbstractDemand dem : ((VirtualNode) node))
					if (dem instanceof CpuDemand)
						nodeAr = ((CpuDemand) dem).getDemandedCycles();

				for (VirtualLink incLink : ((VirtualNetwork) net)
						.getOutEdges((VirtualNode) node))
					for (AbstractDemand dem : ((VirtualLink) incLink))
						if (dem instanceof BandwidthDemand)
							incBw += ((BandwidthDemand) dem)
									.getDemandedBandwidth();
			}
		}
		if (net instanceof SubstrateNetwork) {
			if (node instanceof SubstrateNode) {
				for (AbstractResource res : ((SubstrateNode) node))
					if (res instanceof CpuResource) {
						nodeAr = ((CpuResource) res).getAvailableCycles();
					}

				for (SubstrateLink incLink : ((SubstrateNetwork) net)
						.getOutEdges((SubstrateNode) node))
					for (AbstractResource res : incLink)
						if (res instanceof BandwidthResource) {
							incBw += ((BandwidthResource) res)
									.getAvailableBandwidth();
						}
			}
		}
		return (nodeAr * incBw);
	}

	/**
	 * 
	 * @param nodeSet
	 * @param node
	 * @param nodeAr
	 * @return the NodeRank (0) of node
	 */
	@SuppressWarnings("unchecked")
	public static double calculateNr_0_pJF(Collection<?> nodeSet, Node<?> node,
			Map<Node<?>, Double> nodeAr) {
		double sumAr = 0;
		for (Node<?> tempSnode : ((Collection<Node<?>>) nodeSet))
			sumAr += nodeAr.get(tempSnode);

		if (sumAr != 0) {
			return (nodeAr.get(node) / sumAr);
		} else {
			return 1;
		}

	}

	/**
	 * 
	 * @param nodeNR_i
	 * @param nodeNR_i_1
	 * @param nodeSet
	 * @return ||NodeRank(i+1) - NodeRank(i)||
	 */
	@SuppressWarnings("unchecked")
	public static double calculateNR_norm(Map<Node<?>, Double> nodeNR_i,
			Map<Node<?>, Double> nodeNR_i_1, Collection<?> nodeSet) {
		double sumNR = 0;
		for (Node<?> tempNode : ((Collection<Node<?>>) nodeSet))
			sumNR += Math.pow((nodeNR_i_1.get(tempNode) - nodeNR_i
					.get(tempNode)), 2);

		return Math.sqrt(sumNR);
	}

	/**
	 * 
	 * @param nodeNR_i
	 * @param p_J_u_v
	 * @param p_F_u_v
	 * @param nodeSet
	 * @return NodeRank (i+1) of the set of nodes from the NodeRank(i)
	 */
	@SuppressWarnings("unchecked")
	public static Map<Node<?>, Double> calculateNR_i_1(
			Map<Node<?>, Double> nodeNR_i, Map<NodexNode, Double> p_J_u_v,
			Map<NodexNode, Double> p_F_u_v, Collection<?> nodeSet) {
		Map<Node<?>, Double> nodeNR_i_1 = new LinkedHashMap<Node<?>, Double>();
		final double p_u_j = 0.15;
		final double p_u_f = 0.85;
		double nodeNR_i_1_value;
		for (Node<?> node1 : ((Collection<Node<?>>) nodeSet)) {
			nodeNR_i_1_value = 0;
			for (Iterator<NodexNode> tempNodePair = p_J_u_v.keySet().iterator(); tempNodePair
					.hasNext();) {
				NodexNode currNodexNode = tempNodePair.next();
				if (currNodexNode.getNode2().equals(node1))
					nodeNR_i_1_value += p_J_u_v.get(currNodexNode) * p_u_j
							* nodeNR_i.get(currNodexNode.getNode1());
			}

			for (Iterator<NodexNode> tempNodePair = p_F_u_v.keySet().iterator(); tempNodePair
					.hasNext();) {
				NodexNode currNodexNode = tempNodePair.next();
				if (currNodexNode.getNode2().equals(node1))
					nodeNR_i_1_value += p_F_u_v.get(currNodexNode) * p_u_f
							* nodeNR_i.get(currNodexNode.getNode1());
			}
			nodeNR_i_1.put(node1, nodeNR_i_1_value);
		}
		return nodeNR_i_1;
	}

	/**
	 * 
	 * @param vNode
	 *            virtual node
	 * @param sNode
	 *            substrate node
	 * @param distance
	 * @return true if vNode is separated from sNode by a value lower than
	 *         "distance"
	 */
	public static boolean nodeDistance(VirtualNode vNode, SubstrateNode sNode,
			int distance) {
		double dis;
		dis = Math.pow(sNode.getCoordinateX() - vNode.getCoordinateX(), 2)
				+ Math.pow(sNode.getCoordinateY() - vNode.getCoordinateY(), 2);
		if (Math.sqrt(dis) <= distance) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Function in charge of calculating the node ranking of a particular
	 * network (substrate or virtual network). see Algorithm 1 in: X. Cheng, S.
	 * Su, Z. Zhang, H. Wang, F. Yang, Y. Luo, J. Wang, Virtual network
	 * embedding through topology-aware node ranking, SIGCOMM Comput. Commun.
	 * Rev. 41 (2011) 38�47.
	 * 
	 * @param net
	 * @return the optimal NR
	 */
	public static Map<Node<?>, Double> create_NR(Network<?, ?, ?> net,
			double epsilon) {
		Map<Node<?>, Double> nodeAr = new LinkedHashMap<Node<?>, Double>();
		Map<Node<?>, Double> nodeNR_i = new LinkedHashMap<Node<?>, Double>();
		Map<Node<?>, Double> nodeNR_i_1 = new LinkedHashMap<Node<?>, Double>();
		Map<NodexNode, Double> p_J_u_v = new LinkedHashMap<NodexNode, Double>();
		Map<NodexNode, Double> p_F_u_v = new LinkedHashMap<NodexNode, Double>();
		NodexNode tempNodePair;
		Map<Node<?>, List<Node<?>>> nodeNeighbors = new LinkedHashMap<Node<?>, List<Node<?>>>();
		List<Node<?>> tempNodeNeighbors = new LinkedList<Node<?>>();
		double delta = epsilon + 1;

		for (Node<?> node : net.getVertices())
			nodeAr.put(node, getAr(net, node));

		for (Node<?> node : net.getVertices()) {
			nodeNR_i.put(node, calculateNr_0_pJF(net.getVertices(), node,
					nodeAr));
			tempNodeNeighbors.clear();

			if (net instanceof SubstrateNetwork)
				for (SubstrateLink sLink : ((SubstrateNetwork) net)
						.getOutEdges((SubstrateNode) node))
					tempNodeNeighbors.add(((SubstrateNetwork) net)
							.getDest(sLink));

			if (net instanceof VirtualNetwork)
				for (VirtualLink vLink : ((VirtualNetwork) net)
						.getOutEdges((VirtualNode) node))
					tempNodeNeighbors
							.add(((VirtualNetwork) net).getDest(vLink));

			nodeNeighbors.put(node, tempNodeNeighbors);
			for (Node<?> node2 : net.getVertices()) {
				if (!node.equals(node2)) {
					tempNodePair = new NodexNode(node, node2);
					p_J_u_v.put(tempNodePair, calculateNr_0_pJF(net
							.getVertices(), node2, nodeAr));
					if (tempNodeNeighbors.contains(node2))
						p_F_u_v.put(tempNodePair, calculateNr_0_pJF(
								tempNodeNeighbors, node2, nodeAr));
				}
			}
		}
		while (delta > epsilon) {
			nodeNR_i_1 = calculateNR_i_1(nodeNR_i, p_J_u_v, p_F_u_v, net
					.getVertices());
			delta = calculateNR_norm(nodeNR_i, nodeNR_i_1, net.getVertices());
			nodeNR_i = nodeNR_i_1;
		}
		return nodeNR_i_1;
	}

	/**
	 * 
	 * @param vNode
	 *            virtual node
	 * @param dem
	 *            demand of the virtual node
	 * @param filtratedsNodes
	 *            set of all nodes to extract the feasible substrate node
	 *            candidates
	 * @param dist
	 *            parameter to assure that the candidate nodes will be separated
	 *            of virtual node by a distance of, at maximum, equal to dist.
	 * @return The set of substrate nodes accomplishing a demand and a distance
	 *         dist from the vnode
	 */
	public static List<SubstrateNode> findFulfillingNodes(VirtualNode vNode,
			List<SubstrateNode> filtratedsNodes, int dist,
			Map<VirtualNode, SubstrateNode> nodeMapping) {
		List<SubstrateNode> nodes = new LinkedList<SubstrateNode>();
		for (SubstrateNode n : filtratedsNodes) {
			if (NodeLinkAssignation.isMappable(vNode, n)
					&& nodeDistance(vNode, n, dist)
					&& !nodeMapping.containsValue(n)) {
				nodes.add(n);
			}
		}
		return nodes;
	}

	/**
	 * 
	 * @param vNode
	 *            virtual node
	 * @param dem
	 *            demand of the virtual node
	 * @param filtratedsNodes
	 *            set of all nodes to extract the feasible substrate node
	 *            candidates
	 * @param dist
	 *            parameter to assure that the candidate nodes will be separated
	 *            of virtual node by a distance of, at maximum, equal to dist.
	 * @return The set of substrate nodes accomplishing a demand and a distance
	 *         dist from the vnode
	 */
	public static List<SubstrateNode> findFulfillingNodes(VirtualNode vNode,
			List<SubstrateNode> filtratedsNodes, int dist) {
		List<SubstrateNode> nodes = new LinkedList<SubstrateNode>();
		for (SubstrateNode n : filtratedsNodes) {
			if (NodeLinkAssignation.isMappable(vNode, n)
					&& nodeDistance(vNode, n, dist)) {
				nodes.add(n);
			}
		}
		return nodes;
	}

	/**
	 * 
	 * @param vNode
	 *            virtual node
	 * @param dem
	 *            demand of the virtual node
	 * @param filtratedsNodes
	 *            set of all nodes to extract the feasible substrate node
	 *            candidates
	 * @param dist
	 *            parameter to assure that the candidate nodes will be separated
	 *            of virtual node by a distance of, at maximum, equal to dist.
	 * @return The set of substrate nodes accomplishing a demand and a distance
	 *         dist from the vnode
	 */
	public static List<SubstrateNode> findFulfillingNodes(VirtualNode vNode,
			List<SubstrateNode> filtratedsNodes) {
		List<SubstrateNode> nodes = new LinkedList<SubstrateNode>();
		for (SubstrateNode n : filtratedsNodes) {
			if (NodeLinkAssignation.isMappable(vNode, n)) {
				nodes.add(n);
			}
		}
		return nodes;
	}

	/**
	 * 
	 * @param vNode
	 *            virtual node
	 * @param dem
	 *            demand of the virtual node
	 * @param filtratedsNodes
	 *            set of all nodes to extract the feasible substrate node
	 *            candidates
	 * 
	 * @return The set of substrate nodes accomplishing a demand
	 */
	public static List<SubstrateNode> findFulfillingNodes(VirtualNode vNode,
			List<SubstrateNode> filtratedsNodes,
			Map<VirtualNode, SubstrateNode> nodeMapping) {
		List<SubstrateNode> nodes = new LinkedList<SubstrateNode>();
		for (SubstrateNode n : filtratedsNodes) {
			if (NodeLinkAssignation.isMappable(vNode, n)
					&& !nodeMapping.containsValue(n)) {
				nodes.add(n);
			}
		}
		return nodes;
	}

	public static Link<?> getEdge(Network<?, ?, ?> net, Node<?> sourceNode,
			Node<?> destNode) {
		for (Link<?> currLink : net.getEdges()) {
			if (net instanceof SubstrateNetwork) {
				if (((SubstrateNetwork) net)
						.getSource((SubstrateLink) currLink).equals(
								(SubstrateNode) sourceNode)
						&& ((SubstrateNetwork) net).getDest(
								(SubstrateLink) currLink).equals(
								(SubstrateNode) destNode)) {
					return currLink;
				}
			}
			if (net instanceof VirtualNetwork) {
				if (((VirtualNetwork) net).getSource((VirtualLink) currLink)
						.equals((VirtualNode) sourceNode)
						&& ((VirtualNetwork) net).getDest(
								(VirtualLink) currLink).equals(
								(VirtualNode) destNode)) {
					return currLink;
				}
			}

		}
		return null;
	}

	/** unregister mapping of a virtual node that could not be mapped */
	public static void clearVnodeMappings(VirtualNetwork vNet, VirtualNode vNode) {

		for (AbstractDemand dem : vNode.get()) {
			List<Mapping> mappingsCopy = new ArrayList<Mapping>();
			if (!dem.getMappings().isEmpty()) {
				mappingsCopy.addAll(dem.getMappings());
				for (Mapping m : mappingsCopy)
					m.getDemand().free(m.getResource());
			}
		}

		for (VirtualLink link : vNet.getOutEdges(vNode)) {
			for (AbstractDemand dem : link.get()) {
				List<Mapping> mappingsCopy = new ArrayList<Mapping>();
				if (!dem.getMappings().isEmpty()) {
					mappingsCopy.addAll(dem.getMappings());
					for (Mapping m : mappingsCopy)
						m.getDemand().free(m.getResource());
				}
			}
			for (AbstractDemand dem : link.getHiddenHopDemands()) {
				List<Mapping> mappingsCopy = new ArrayList<Mapping>();
				if (!dem.getMappings().isEmpty()) {
					mappingsCopy.addAll(dem.getMappings());
					for (Mapping m : mappingsCopy)
						m.getDemand().free(m.getResource());
				}
			}
		}
		for (VirtualLink link : vNet.getInEdges(vNode)) {
			for (AbstractDemand dem : link.get()) {
				List<Mapping> mappingsCopy = new ArrayList<Mapping>();
				if (!dem.getMappings().isEmpty()) {
					mappingsCopy.addAll(dem.getMappings());
					for (Mapping m : mappingsCopy)
						m.getDemand().free(m.getResource());
				}
			}
			for (AbstractDemand dem : link.getHiddenHopDemands()) {
				List<Mapping> mappingsCopy = new ArrayList<Mapping>();
				if (!dem.getMappings().isEmpty()) {
					mappingsCopy.addAll(dem.getMappings());
					for (Mapping m : mappingsCopy)
						m.getDemand().free(m.getResource());
				}
			}
		}
	}

	public static boolean hasLinkMappings(VirtualLink vLink) {
		for (AbstractDemand dem : vLink.get())
			if (!dem.getMappings().isEmpty())
				return true;

		return false;
	}

	public static List<SubstrateNode> getUnmappedNodes(
			Collection<SubstrateNode> subNodes) {
		List<SubstrateNode> unmappedNodes = new LinkedList<SubstrateNode>();
		boolean hasMappings = false;
		for (SubstrateNode n : subNodes) {
			hasMappings = false;
			for (AbstractResource res : n) {
				if (!res.getMappings().isEmpty()) {
					hasMappings = true;
					break;
				}
			}
			if (!hasMappings)
				unmappedNodes.add(n);

		}
		return unmappedNodes;
	}

	public static List<SubstrateLink> getUnmappedLinks(
			Collection<SubstrateLink> subLinks) {
		List<SubstrateLink> unmappedLinks = new LinkedList<SubstrateLink>();
		boolean hasMappings = false;
		for (SubstrateLink l : subLinks) {
			hasMappings = false;
			for (AbstractResource res : l) {
				if (!res.getMappings().isEmpty()) {
					hasMappings = true;
					break;
				}
			}
			if (!hasMappings)
				unmappedLinks.add(l);

		}
		return unmappedLinks;
	}

	public static void createMatlabFiles(VirtualNetwork vNet/*
															 * ,
															 * Map<VirtualNode,
															 * SubstrateNode>
															 * nodeMapping
															 */) {
		int i;
		int[][] vNetTopo = new int[vNet.getVertices().size()][vNet
				.getVertices().size()];
		double[][] vNetHhDemands = new double[vNet.getVertices().size()][vNet
				.getVertices().size()];
		// FIXME Just for now, our algorithm will just have BW metrics in links
		// and CPU in nodes
		double[][] vNetBwDem = new double[vNet.getVertices().size()][vNet
				.getVertices().size()];
		double[] vNetCpuDem = new double[vNet.getVertices().size()];
		// int[] nodeMapp = new int[vNet.getVertices().size()];
		Map<Integer, Integer> nodeMapVirtVir = vNodesidToMatlabId(vNet);

		// Loop to fill the matrices with 0 values
		i = 0;
		for (Iterator<VirtualNode> itt = vNet.getVertices().iterator(); itt
				.hasNext();) {
			// VirtualNode tempVirNode = itt.next();
			itt.next();
			vNetCpuDem[i] = 0;
			// nodeMapp[i] = nodeMapping.get(tempVirNode).getId();
			for (int j = 0; j < vNet.getVertices().size(); j++) {
				vNetTopo[i][j] = 0;
				vNetBwDem[i][j] = 0;
				vNetHhDemands[i][j] = 0;
			}
			i++;
		}

		// Loop to fill the link demand (including hidden hops) matrices
		for (Iterator<VirtualLink> links = vNet.getEdges().iterator(); links
				.hasNext();) {
			VirtualLink tempLink = links.next();
			vNetTopo[nodeMapVirtVir.get(vNet.getSource(tempLink).getId())][nodeMapVirtVir
					.get(vNet.getDest(tempLink).getId())] = 1;
			for (AbstractDemand dem : tempLink)
				if (dem instanceof BandwidthDemand) {
					vNetBwDem[nodeMapVirtVir.get(vNet.getSource(tempLink)
							.getId())][nodeMapVirtVir.get(vNet
							.getDest(tempLink).getId())] = ((BandwidthDemand) dem)
							.getDemandedBandwidth();
					break;
				}

			// Get the hidden hops demand
			for (AbstractDemand dem : tempLink.getHiddenHopDemands())
				if (dem instanceof CpuDemand) {
					vNetHhDemands[nodeMapVirtVir.get(vNet.getSource(tempLink)
							.getId())][nodeMapVirtVir.get(vNet
							.getDest(tempLink).getId())] = ((CpuDemand) dem)
							.getDemandedCycles();
					break;
				}
		}

		// Loop to fill the node demand matrices
		i = 0;
		for (Iterator<VirtualNode> nodes = vNet.getVertices().iterator(); nodes
				.hasNext();) {
			VirtualNode tempNode = nodes.next();
			for (AbstractDemand dem : tempNode) {
				if (dem instanceof CpuDemand) {
					vNetCpuDem[i] = ((CpuDemand) dem).getDemandedCycles();
					break;
				}
			}
			i++;
		}

		/* Files that will be shared with matlab program are created */
		MatlabFilesCreator.createVirtualFiles(vNetTopo, vNetBwDem, vNetCpuDem,
				vNetHhDemands, vNet.getLayer());
	}

	public static Map<Integer, Integer> vNodesidToMatlabId(VirtualNetwork vNet) {
		int i = 0;
		Map<Integer, Integer> nodeMapVirtVir = new LinkedHashMap<Integer, Integer>();
		for (Iterator<VirtualNode> itt = vNet.getVertices().iterator(); itt
				.hasNext();) {
			VirtualNode tempVirNode = itt.next();
			nodeMapVirtVir.put((int) tempVirNode.getId(), i);
			i++;
		}
		return nodeMapVirtVir;
	}

	public static int maxNodeGrade(SubstrateNetwork net) {
		int maxNodeGrade = 0;
		for (SubstrateNode n : net.getVertices()) {
			if (net.degree(n) > maxNodeGrade)
				maxNodeGrade = net.degree(n);
		}
		return maxNodeGrade;
	}

	/**
	 * 
	 * @param net
	 * @param node
	 * @return available resources (or demands) of one node taking into account
	 *         the energy
	 */
	public static double getArEnergyAware(Network<?, ?, ?> net, Node<?> node,
			int mappedNodeWeight, int mappedLinkWeight, int unmappedNodeWeight,
			int unmappedLinkWeight) {
		double incBw = 0, nodeAr = 0;

		if (net instanceof VirtualNetwork) {
			if (node instanceof VirtualNode) {
				for (AbstractDemand dem : ((VirtualNode) node))
					if (dem instanceof CpuDemand)
						nodeAr = ((CpuDemand) dem).getDemandedCycles();

				for (VirtualLink incLink : ((VirtualNetwork) net)
						.getOutEdges((VirtualNode) node))
					for (AbstractDemand dem : ((VirtualLink) incLink))
						if (dem instanceof BandwidthDemand)
							incBw += ((BandwidthDemand) dem)
									.getDemandedBandwidth();

				for (VirtualLink incLink : ((VirtualNetwork) net)
						.getInEdges((VirtualNode) node))
					for (AbstractDemand dem : ((VirtualLink) incLink))
						if (dem instanceof BandwidthDemand)
							incBw += ((BandwidthDemand) dem)
									.getDemandedBandwidth();

			}
		}
		if (net instanceof SubstrateNetwork) {
			if (node instanceof SubstrateNode) {
				for (AbstractResource res : ((SubstrateNode) node))
					if (res instanceof CpuResource) {
						if (res.getMappings().isEmpty()) {
							nodeAr = (((CpuResource) res).getAvailableCycles())
									* unmappedNodeWeight;
						} else {
							nodeAr = (((CpuResource) res).getAvailableCycles())
									* mappedNodeWeight;
						}
					}

				for (SubstrateLink incLink : ((SubstrateNetwork) net)
						.getOutEdges((SubstrateNode) node))
					for (AbstractResource res : incLink)
						if (res instanceof BandwidthResource) {
							if (res.getMappings().isEmpty()) {
								incBw += (((BandwidthResource) res)
										.getAvailableBandwidth())
										* unmappedLinkWeight;
							} else {
								incBw += (((BandwidthResource) res)
										.getAvailableBandwidth())
										* mappedLinkWeight;
							}
						}

				for (SubstrateLink incLink : ((SubstrateNetwork) net)
						.getInEdges((SubstrateNode) node))
					for (AbstractResource res : incLink)
						if (res instanceof BandwidthResource) {
							if (res.getMappings().isEmpty()) {
								incBw += (((BandwidthResource) res)
										.getAvailableBandwidth())
										* unmappedLinkWeight;
							} else {
								incBw += (((BandwidthResource) res)
										.getAvailableBandwidth())
										* mappedLinkWeight;
							}
						}
			}
		}
		return (nodeAr * incBw);
	}

	/**
	 * 
	 * @param net
	 * @return the optimal NR with energy aware premises
	 */
	public static Map<Node<?>, Double> create_NR_EnergyAware(
			Network<?, ?, ?> net, double epsilon, int mappedNodeWeight,
			int mappedLinkWeight, int unmappedNodeWeight, int unmappedLinkWeight) {
		Map<Node<?>, Double> nodeAr = new LinkedHashMap<Node<?>, Double>();
		Map<Node<?>, Double> nodeNR_i = new LinkedHashMap<Node<?>, Double>();
		Map<Node<?>, Double> nodeNR_i_1 = new LinkedHashMap<Node<?>, Double>();
		Map<NodexNode, Double> p_J_u_v = new LinkedHashMap<NodexNode, Double>();
		Map<NodexNode, Double> p_F_u_v = new LinkedHashMap<NodexNode, Double>();
		NodexNode tempNodePair;
		Map<Node<?>, List<Node<?>>> nodeNeighbors = new LinkedHashMap<Node<?>, List<Node<?>>>();
		List<Node<?>> tempNodeNeighbors = new LinkedList<Node<?>>();
		double delta = epsilon + 1;

		for (Node<?> node : net.getVertices())
			nodeAr.put(node, getArEnergyAware(net, node, mappedNodeWeight,
					mappedLinkWeight, unmappedNodeWeight, unmappedLinkWeight));

		for (Node<?> node : net.getVertices()) {
			nodeNR_i.put(node, calculateNr_0_pJF(net.getVertices(), node,
					nodeAr));
			tempNodeNeighbors.clear();

			if (net instanceof SubstrateNetwork)
				for (SubstrateLink sLink : ((SubstrateNetwork) net)
						.getOutEdges((SubstrateNode) node))
					tempNodeNeighbors.add(((SubstrateNetwork) net)
							.getDest(sLink));

			if (net instanceof VirtualNetwork)
				for (VirtualLink vLink : ((VirtualNetwork) net)
						.getOutEdges((VirtualNode) node))
					tempNodeNeighbors
							.add(((VirtualNetwork) net).getDest(vLink));

			nodeNeighbors.put(node, tempNodeNeighbors);
			for (Node<?> node2 : net.getVertices()) {
				if (!node.equals(node2)) {
					tempNodePair = new NodexNode(node, node2);
					p_J_u_v.put(tempNodePair, calculateNr_0_pJF(net
							.getVertices(), node2, nodeAr));
					if (tempNodeNeighbors.contains(node2))
						p_F_u_v.put(tempNodePair, calculateNr_0_pJF(
								tempNodeNeighbors, node2, nodeAr));
				}
			}
		}
		while (delta > epsilon) {
			nodeNR_i_1 = calculateNR_i_1(nodeNR_i, p_J_u_v, p_F_u_v, net
					.getVertices());
			delta = calculateNR_norm(nodeNR_i, nodeNR_i_1, net.getVertices());
			nodeNR_i = nodeNR_i_1;
		}
		return nodeNR_i_1;
	}

	/**
	 * Method to see if the node that is going to be mapped in the substrate
	 * network have links in the virtual network with some virtual node that has
	 * been already mapped to it.
	 * 
	 * @param vNet
	 * @param vNode
	 * @param sNode
	 * @return
	 */
	public static boolean linksWithItself(VirtualNetwork vNet,
			VirtualNode vNode, SubstrateNode sNode,
			Map<VirtualNode, SubstrateNode> nodeMapping) {
		if (nodeMapping.containsValue(sNode)) {
			for (Iterator<VirtualNode> it = nodeMapping.keySet().iterator(); it
					.hasNext();) {
				VirtualNode tmpVnode = it.next();
				if (nodeMapping.get(tmpVnode).equals(sNode)) {
					for (VirtualLink tmpVlink : vNet.getOutEdges(tmpVnode)) {
						if (vNet.getDest(tmpVlink).equals(vNode))
							return true;
					}
					for (VirtualLink tmpVlink : vNet.getInEdges(tmpVnode)) {
						if (vNet.getSource(tmpVlink).equals(vNode))
							return true;
					}
				}
			}
		} else {
			return false;
		}
		return false;
	}

	/**
	 * 
	 * @param oldStack
	 * @return New stack with one virtual network that is a sum of the set of
	 *         virtual networks
	 */
	public static NetworkStack allVnsInOne(NetworkStack oldStack) {
		NetworkStack newStack;
		List<VirtualNetwork> vns = new LinkedList<VirtualNetwork>();
		VirtualNetwork vNet = new VirtualNetwork(1);
		vns.add(vNet);
		Map<VirtualNode, VirtualNode> virtualToVirtual = null;
		for (Network<?, ?, ?> net : oldStack) {
			virtualToVirtual = new LinkedHashMap<VirtualNode, VirtualNode>();
			if (net instanceof VirtualNetwork) {
				for (VirtualNode vNode : ((VirtualNetwork) net).getVertices()) {
					VirtualNode tempNode = new VirtualNode(vNet.getLayer());
					for (AbstractDemand dem : vNode) {
						if (dem instanceof CpuDemand) {
							CpuDemand tempCpuDem = new CpuDemand(tempNode);
							tempCpuDem.setDemandedCycles(((CpuDemand) dem)
									.getDemandedCycles());
							tempNode.add(tempCpuDem);
							vNet.addVertex(tempNode);
							virtualToVirtual.put(vNode, tempNode);
							break;
						}
					}
				}

				for (VirtualLink vLink : ((VirtualNetwork) net).getEdges()) {
					VirtualLink tempVlink = new VirtualLink(vNet.getLayer());
					for (AbstractDemand dem : vLink) {
						if (dem instanceof BandwidthDemand) {
							BandwidthDemand tempBwDem = new BandwidthDemand(
									tempVlink);
							tempBwDem
									.setDemandedBandwidth(((BandwidthDemand) dem)
											.getDemandedBandwidth());
							tempVlink.add(tempBwDem);
							vNet.addEdge(tempVlink, virtualToVirtual
									.get(((VirtualNetwork) net)
											.getSource(vLink)),
									virtualToVirtual.get(((VirtualNetwork) net)
											.getDest(vLink)));
							break;
						}
					}
				}
			}
		}
		newStack = new NetworkStack(oldStack.getSubstrate(), vns);
		return newStack;
	}

	/**
	 * 
	 * @param oldStack
	 * @return New stack with one virtual network that is a sum of the set of
	 *         virtual networks
	 */
	public static NetworkStack allVnsInOneforBFS(NetworkStack oldStack) {
		NetworkStack newStack;
		List<VirtualNetwork> vns = new LinkedList<VirtualNetwork>();
		VirtualNetwork vNet = new VirtualNetwork(1);
		vns.add(vNet);
		VirtualNode tempNode = null, nodePrevNet = null, nodeLastNet = null;
		VirtualLink tempVlink = null;
		Map<VirtualNode, VirtualNode> virtualToVirtual = new LinkedHashMap<VirtualNode, VirtualNode>();

		for (Network<?, ?, ?> net : oldStack) {

			if (net instanceof VirtualNetwork) {
				for (VirtualNode vNode : ((VirtualNetwork) net).getVertices()) {
					tempNode = new VirtualNode(vNet.getLayer());
					for (AbstractDemand dem : vNode) {
						if (dem instanceof CpuDemand) {
							CpuDemand tempCpuDem = new CpuDemand(tempNode);
							tempCpuDem.setDemandedCycles(((CpuDemand) dem)
									.getDemandedCycles());
							tempNode.add(tempCpuDem);
							vNet.addVertex(tempNode);
							virtualToVirtual.put(vNode, tempNode);
							break;
						}
					}
					if (nodeLastNet != null) {
						VirtualLink temp = new VirtualLink(vNet.getLayer());
						BandwidthDemand bwDem = new BandwidthDemand(temp);
						bwDem.setDemandedBandwidth((double) -1);
						temp.add(bwDem);
						vNet.addEdge(temp, virtualToVirtual.get(nodeLastNet),
								tempNode);
						nodeLastNet = null;
					}
					nodePrevNet = vNode;
				}
				nodeLastNet = nodePrevNet;

				for (VirtualLink vLink : ((VirtualNetwork) net).getEdges()) {
					tempVlink = new VirtualLink(vNet.getLayer());
					for (AbstractDemand dem : vLink) {
						if (dem instanceof BandwidthDemand) {
							BandwidthDemand tempBwDem = new BandwidthDemand(
									tempVlink);
							tempBwDem
									.setDemandedBandwidth(((BandwidthDemand) dem)
											.getDemandedBandwidth());
							tempVlink.add(tempBwDem);
							vNet.addEdge(tempVlink, virtualToVirtual
									.get(((VirtualNetwork) net)
											.getSource(vLink)),
									virtualToVirtual.get(((VirtualNetwork) net)
											.getDest(vLink)));
							break;
						}
					}
				}
			}
		}
		newStack = new NetworkStack(oldStack.getSubstrate(), vns);
		return newStack;
	}

	public static List<SubstrateNode> findFulfillingNodesEA(VirtualNode vNode,
			List<SubstrateNode> filtratedsNodes, int dist,
			Map<VirtualNode, SubstrateNode> nodeMapping,
			VirtualNode mappedParent, List<SubstrateNode> orderedCandidates,
			VirtualNetwork vNet) {
		List<SubstrateNode> mappedNodes = new LinkedList<SubstrateNode>();
		List<SubstrateNode> unMappedNodes = new LinkedList<SubstrateNode>();
		List<SubstrateNode> nodes = new LinkedList<SubstrateNode>();
		boolean isDest = isDestination(vNode, mappedParent, vNet);

		if (isDest) {
			for (VirtualLink tmpVlink : vNet.getOutEdges(mappedParent)) {
				VirtualNode tmpNode = vNet.getDest(tmpVlink);
				for (AbstractDemand dem : tmpNode) {
					if (dem instanceof CpuDemand) {
						if (!dem.getMappings().isEmpty()
								&& !linksWithItself(vNet, vNode, nodeMapping
										.get(tmpNode), nodeMapping)) {
							mappedNodes.add(nodeMapping.get(tmpNode));
						}
					}
				}
			}
		} else {
			for (VirtualLink tmpVlink : vNet.getInEdges(mappedParent)) {
				VirtualNode tmpNode = vNet.getSource(tmpVlink);
				for (AbstractDemand dem : tmpNode) {
					if (dem instanceof CpuDemand) {
						if (!dem.getMappings().isEmpty()
								&& !linksWithItself(vNet, vNode, nodeMapping
										.get(tmpNode), nodeMapping)) {
							mappedNodes.add(nodeMapping.get(tmpNode));
						}
					}
				}
			}
		}
		for (SubstrateNode n : orderedCandidates) {
			if (NodeLinkAssignation.isMappable(vNode, n)
					&& nodeDistance(vNode, n, dist) && mappedNodes.contains(n)) {
				nodes.add(n);
			}
		}

		for (SubstrateNode n : orderedCandidates) {
			if (NodeLinkAssignation.isMappable(vNode, n)
					&& nodeDistance(vNode, n, dist)
					&& filtratedsNodes.contains(n) && !nodes.contains(n)
					&& !linksWithItself(vNet, vNode, n, nodeMapping)) {
				for (AbstractResource res : n) {
					if (res instanceof CpuResource) {
						if (!res.getMappings().isEmpty()) {
							// nodes.add(n);
						} else {
							unMappedNodes.add(n);
						}
						break;
					}
				}
			}
		}
		nodes.addAll(unMappedNodes);
		return nodes;
	}

	public static List<SubstrateNode> findFulfillingNodesEA(VirtualNode vNode,
			List<SubstrateNode> filtratedsNodes,
			Map<VirtualNode, SubstrateNode> nodeMapping,
			VirtualNode mappedParent, List<SubstrateNode> orderedCandidates,
			VirtualNetwork vNet) {
		List<SubstrateNode> mappedNodes = new LinkedList<SubstrateNode>();
		List<SubstrateNode> unMappedNodes = new LinkedList<SubstrateNode>();
		List<SubstrateNode> nodes = new LinkedList<SubstrateNode>();
		boolean isDest = isDestination(vNode, mappedParent, vNet);

		if (isDest) {
			for (VirtualLink tmpVlink : vNet.getOutEdges(mappedParent)) {
				VirtualNode tmpNode = vNet.getDest(tmpVlink);
				for (AbstractDemand dem : tmpNode) {
					if (dem instanceof CpuDemand) {
						if (!dem.getMappings().isEmpty()
								&& NodeLinkAssignation.isMappable(vNode,
										nodeMapping.get(tmpNode))
								&& !linksWithItself(vNet, vNode, nodeMapping
										.get(tmpNode), nodeMapping)) {
							mappedNodes.add(nodeMapping.get(tmpNode));
						}
					}
				}
			}
		} else {
			for (VirtualLink tmpVlink : vNet.getInEdges(mappedParent)) {
				VirtualNode tmpNode = vNet.getSource(tmpVlink);
				for (AbstractDemand dem : tmpNode) {
					if (dem instanceof CpuDemand) {
						if (!dem.getMappings().isEmpty()
								&& NodeLinkAssignation.isMappable(vNode,
										nodeMapping.get(tmpNode))
								&& !linksWithItself(vNet, vNode, nodeMapping
										.get(tmpNode), nodeMapping)) {
							mappedNodes.add(nodeMapping.get(tmpNode));
						}
					}
				}
			}
		}
		for (SubstrateNode n : orderedCandidates) {
			if (NodeLinkAssignation.isMappable(vNode, n)
					&& mappedNodes.contains(n) && filtratedsNodes.contains(n)) {
				nodes.add(n);
			}
		}

		for (SubstrateNode n : orderedCandidates) {
			if (NodeLinkAssignation.isMappable(vNode, n)
					&& filtratedsNodes.contains(n) && !nodes.contains(n)
					&& !linksWithItself(vNet, vNode, n, nodeMapping)) {
				for (AbstractResource res : n) {
					if (res instanceof CpuResource) {
						if (!res.getMappings().isEmpty()) {
							nodes.add(n);
						} else {
							unMappedNodes.add(n);
						}
						break;
					}
				}
			}
		}
		nodes.addAll(unMappedNodes);
		return nodes;
	}

	private static boolean isDestination(VirtualNode child, VirtualNode parent,
			VirtualNetwork vNet) {

		for (VirtualLink tmpVlink : vNet.getOutEdges(parent)) {
			if (vNet.getDest(tmpVlink).equals(child))
				return true;
		}

		for (VirtualLink tmpVlink : vNet.getInEdges(parent)) {
			if (vNet.getSource(tmpVlink).equals(child))
				return false;
		}
		return false;
	}

	public static void remapHiddenHopsDemand(double hhFactor, NetworkStack stack) {
		LinkedList<IHiddenHopMapping> hhMappings = new LinkedList<IHiddenHopMapping>();
		hhMappings.add(new BandwidthCpuHiddenHopMapping(hhFactor));
		VirtualNode srcVnode;
		SubstrateNode srcSnode = null, tmpSrcSnode;
		SubstrateLink tmpSlink;
		CpuDemand tmpHhDemand = null;
		CpuResource tempCpuRes = null;
		for (Network<?, ?, ?> net : stack)
			if (net instanceof VirtualNetwork) {
				VirtualNetwork vNet = (VirtualNetwork) net;
				for (VirtualLink vLink : vNet.getEdges()) {
					// Clearing past hidden hops
					// demands
					vLink.clearHiddenHopDemands();
					srcVnode = vNet.getSource(vLink);
					for (AbstractDemand dem : srcVnode)
						if (dem instanceof CpuDemand)
							for (Mapping mapp : dem.getMappings())
								srcSnode = (SubstrateNode) mapp.getResource()
										.getOwner();

					for (AbstractDemand dem : vLink) {
						if (dem instanceof BandwidthDemand) {
							if (dem.getMappings().isEmpty()) {
								tmpHhDemand = (CpuDemand) new BandwidthCpuHiddenHopMapping(
										hhFactor).transform(dem);
								vLink.addHiddenHopDemand(tmpHhDemand);
							}
							List<Mapping> mappCopy = new ArrayList<Mapping>();
							mappCopy.addAll(dem.getMappings());
							for (Mapping map : mappCopy) {
								tmpSlink = (SubstrateLink) map.getResource()
										.getOwner();
								tmpSrcSnode = stack.getSubstrate().getSource(
										tmpSlink);
								if (!srcSnode.equals(tmpSrcSnode)) {
									tmpHhDemand = (CpuDemand) new BandwidthCpuHiddenHopMapping(
											hhFactor)
											.transform(map.getDemand());
									vLink.addHiddenHopDemand(tmpHhDemand);

									for (AbstractResource res : tmpSrcSnode) {
										if (res instanceof CpuResource) {
											tempCpuRes = (CpuResource) res;
											if ((int) ((double) (MiscelFunctions
													.roundTwoDecimals(tempCpuRes
															.getAvailableCycles()
															- tmpHhDemand
																	.getDemandedCycles()) * 100)) == 0)
												tmpHhDemand
														.setDemandedCycles(tempCpuRes
																.getAvailableCycles());
											// Mapping of cpu hidden hop demand
											// is performed
											if (NodeLinkAssignation.occupy(
													tmpHhDemand, res)) {
												break;
											} else {
												throw new AssertionError(
														"Some coding mistake, Embedding Algorithm checks");
											}
										}
									}
								}
							}
						}
					}
				}
			}
	}

	public static List<SubstrateLink> findLinksInactivesDemand(
			NetworkStack stack) {
		List<SubstrateLink> subsLinksOneDem = new LinkedList<SubstrateLink>();
		for (SubstrateNode tempSnode : stack.getSubstrate().getVertices()) {
			for (AbstractResource res : tempSnode) {
				if (res instanceof CpuResource) {
					if (res.getMappings().isEmpty()) {
						for (SubstrateLink tempSlink : stack.getSubstrate()
								.getIncidentEdges(tempSnode)) {
							if (!subsLinksOneDem.contains(tempSlink))
								subsLinksOneDem.add(tempSlink);
						}

						break;
					}

				}
			}
		}
		return subsLinksOneDem;
	}

	public static double getRemaRes(SubstrateNetwork sNet) {
		double nodeRemaRes = 0;
		double linkRemaRes = 0;
		for (SubstrateLink tmpSLink : sNet.getEdges())
			for (AbstractResource res : tmpSLink)
				if (res instanceof BandwidthResource)
					linkRemaRes += ((BandwidthResource) res)
							.getAvailableBandwidth();

		for (SubstrateNode tmpSNode : sNet.getVertices())
			for (AbstractResource res : tmpSNode)
				if (res instanceof CpuResource)
					nodeRemaRes += ((CpuResource) res).getAvailableCycles();

		return (nodeRemaRes + linkRemaRes);
	}

}
