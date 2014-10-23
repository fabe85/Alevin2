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
package vnreal.algorithms.nodemapping;

/**
 * This is the class of the virtual embedding algorithm that calculates the exact optimal 
 * virtual network embedding with regard to cost in the substrate network, it is modified 
 * to include hidden hops.
 * 
 * 
 * This algorithm was proposed in:
 *  
 *  @article{Houidi20111011,
 *  title = "Virtual network provisioning across multiple substrate networks",
 *  journal = "Computer Networks",
 *  volume = "55",
 *  number = "4",
 *  pages = "1011 - 1023",
 *  year = "2011",
 *  note = "Special Issue on Architectures and Protocols for the Future Internet",
 *  issn = "1389-1286",
 *  doi = "DOI: 10.1016/j.comnet.2010.12.011",
 *  url = "http://www.sciencedirect.com/science/article/pii/S1389128610003786",
 *  author = "Ines Houidi and Wajdi Louati and Walid Ben Ameur and Djamal Zeghlache",
 }

 * 
 * @author Juan Felipe Botero
 * @since 2011-01-10
 */

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import vnreal.algorithms.AbstractNodeMapping;
import vnreal.algorithms.utils.Consts;
import vnreal.algorithms.utils.LpSolver;
import vnreal.algorithms.utils.MiscelFunctions;
import vnreal.algorithms.utils.NodeLinkAssignation;
import vnreal.algorithms.utils.dataSolverFile;
import vnreal.demands.AbstractDemand;
import vnreal.demands.BandwidthDemand;
import vnreal.demands.CpuDemand;
import vnreal.hiddenhopmapping.BandwidthCpuHiddenHopMapping;
import vnreal.hiddenhopmapping.IHiddenHopMapping;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;

public class CompleteNodeLinkMappingHiddenHop extends AbstractNodeMapping {
	// PRIVATE variables
	private int distance; // Distance to select the candidate nodes to be
	// mapped.
	// Type variable indicates the rounding type
	private double wCpu, wBw; // Weights of CPU and bandwidth
	private Map<VirtualNode, List<SubstrateNode>> candiNodes;
	boolean withDist;
	NetworkStack stack;

	public CompleteNodeLinkMappingHiddenHop(SubstrateNetwork sNet, int dist,
			double wCpu, double wBw, boolean nodeOverload,
			boolean withDistance, NetworkStack stack) {
		super(sNet, nodeOverload);
		this.distance = dist;
		this.wCpu = wCpu;
		this.wBw = wBw;
		this.withDist = withDistance;
		this.stack = stack;
	}

	// Node and link mapping phase (in this case it is performed in
	// the same phase).
	@Override
	protected boolean nodeMapping(VirtualNetwork vNet) {
		candiNodes = createCandidateSet(vNet);
		stack.clearVnrMappings(vNet);
		Map<List<String>, Double> x;
		Map<List<String>, Double> flow;
		LpSolver problemSolver = new LpSolver();
		Random intGenerator = new Random();
		double hhFactor = 0;

		String dataFileName = Consts.LP_SOLVER_DATAFILE
				+ Integer.toString(intGenerator.nextInt(2001)) + ".dat";

		dataSolverFile lpNodeMappingData = new dataSolverFile(
				Consts.LP_SOLVER_FOLDER + dataFileName);

		for (IHiddenHopMapping hhCpuMapping : hhMappings) {
			if (hhCpuMapping instanceof BandwidthCpuHiddenHopMapping) {
				hhFactor = ((BandwidthCpuHiddenHopMapping) hhCpuMapping)
						.getFactor();
				break;
			}
		}

		lpNodeMappingData.createExactMipSolverFile(sNet, vNet, candiNodes,
				hhFactor, wBw, wCpu, true);
		problemSolver.solveMIP(Consts.LP_SOLVER_FOLDER,
				Consts.ILP_EXACTMAPPING_MODEL_HIDDEN_HOP, dataFileName);

		if (!problemSolver.problemFeasible())
			return false;

		// x and flow are the variables of the MIP model indicating the results
		// of node and link mapping (refer to the Houdi paper)

		x = MiscelFunctions.processSolverResult(problemSolver.getX(), "x[]");
		flow = MiscelFunctions.processSolverResult(problemSolver.getFlow(),
				"flow[]");

		if (performNodeMapping(vNet, x)) {
			if (!performLinkMapping(vNet, flow)) {
				return false;
			}
		} else {
			return false;
		}

		return true;
	}

	/**
	 * 
	 * @param vNet
	 * @return The set of substrate candidates for each virtual node of vNet
	 */
	private Map<VirtualNode, List<SubstrateNode>> createCandidateSet(
			VirtualNetwork vNet) {
		Map<VirtualNode, List<SubstrateNode>> candidateSet = new LinkedHashMap<VirtualNode, List<SubstrateNode>>();
		List<SubstrateNode> substrateSet;
		for (Iterator<VirtualNode> itt = vNet.getVertices().iterator(); itt
				.hasNext();) {
			substrateSet = new LinkedList<SubstrateNode>();
			VirtualNode currVnode = itt.next();
			if (nodeMapping.containsKey(currVnode)) {
				substrateSet.add(nodeMapping.get(currVnode));
			} else {
				substrateSet.addAll(findFullfilingNodes(currVnode));
			}
			candidateSet.put(currVnode, substrateSet);
		}
		return candidateSet;
	}

	/**
	 * 
	 * @param vNode
	 * @return The set of feasible candidate substrate nodes for the virtual
	 *         vNode
	 */
	private Collection<SubstrateNode> findFullfilingNodes(VirtualNode vNode) {
		List<SubstrateNode> nodes = new LinkedList<SubstrateNode>();
		boolean isCandidate = false;
		for (SubstrateNode n : sNet.getVertices()) {
			if (withDist) {
				if (nodeDistance(vNode, n, distance)) {
					for (AbstractResource res : n){
						for (AbstractDemand dem : vNode){
							if (res.accepts(dem) && res.fulfills(dem)) {
								isCandidate = true;
							}else{
								isCandidate = false;
								break;
							}
						}
						if (isCandidate)
							nodes.add(n);
					}
						
							
				}
			} else {
				for (AbstractResource res : n){
					for (AbstractDemand dem : vNode){
						if (res.accepts(dem) && res.fulfills(dem)) {
							isCandidate = true;
						}else{
							isCandidate = false;
							break;
						}
					}
					if (isCandidate)
						nodes.add(n);
				}
			}
		}
		return nodes;
	}

	/**
	 * Method to know if a substrate node is located in a distance less or equal
	 * than the predefined distance parameter
	 * 
	 * @param vNode
	 * @param sNode
	 * @param distance
	 * @return
	 */
	private boolean nodeDistance(VirtualNode vNode, SubstrateNode sNode,
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
	 * This method performs the node mapping taking into account the solver
	 * response in the variable x (containing the optimal node mapping)
	 * 
	 * @param vNet
	 * @param x
	 * @return boolean value indicating whether the node mapping has been
	 *         successful
	 */
	private boolean performNodeMapping(VirtualNetwork vNet,
			Map<List<String>, Double> x) {
		List<String> tmpValues;
		VirtualNode currVnode;
		SubstrateNode currSnode;
		for (Iterator<List<String>> cad = x.keySet().iterator(); cad.hasNext();) {
			tmpValues = cad.next();
			if (x.get(tmpValues) == 1) {
				currVnode = getVnodeById(vNet,
						Integer.parseInt(tmpValues.get(0)));
				currSnode = getSnodeById(Integer.parseInt(tmpValues.get(1)));
				if (NodeLinkAssignation.vnm(currVnode, currSnode)) {
					if (!nodeMapping.containsKey(currVnode))
						nodeMapping.put(currVnode, currSnode);
				} else {
					throw new AssertionError(
							"Implementation mistake MIP model checks");
				}
			}
		}
		return true;
	}

	/**
	 * This method performs the link mapping taking into account the solver
	 * response in the variable flow (containing the optimal link mapping)
	 * 
	 * @param vNet
	 * @param flow
	 * @return boolean value indicating whether the link mapping has been
	 *         successful
	 */
	private boolean performLinkMapping(VirtualNetwork vNet,
			Map<List<String>, Double> flow) {
		VirtualNode srcVnode, dstVnode;
		SubstrateNode corrSrcSNode, corrDstSNode, tempSrcSnode, tempDstSnode, hiddenHop = null;
		SubstrateLink tempSlink;
		BandwidthDemand newBwDem;
		CpuDemand tmpHhDemand = null;
		List<String> flowValues;
		Double tmpResult;
		Map<List<String>, Double> flowPercentages = findPercentages(flow, vNet);
		
		double VlOriginalDemand = 0;
		double hhFactor = 0;
		// Iterate all VirtualLinks on the current VirtualNetwork
		for (Iterator<VirtualLink> links = vNet.getEdges().iterator(); links
				.hasNext();) {
			VirtualLink tmpVlink = links.next();
			// Find the source and destiny of the current VirtualLink (tmpl)
			srcVnode = vNet.getSource(tmpVlink);
			dstVnode = vNet.getDest(tmpVlink);
			for (AbstractDemand dem : tmpVlink)
				if (dem instanceof BandwidthDemand) {
					VlOriginalDemand = ((BandwidthDemand) dem)
							.getDemandedBandwidth();
					break;
				}

			corrSrcSNode = nodeMapping.get(srcVnode);
			corrDstSNode = nodeMapping.get(dstVnode);
			if (!corrSrcSNode.equals(corrDstSNode)) {
				for (Iterator<List<String>> cad = flow.keySet().iterator(); cad
						.hasNext();) {
					flowValues = cad.next();
					if (flowPercentages.get(flowValues) == null) {
						tmpResult = 0.0;
					} else {
						tmpResult = MiscelFunctions
								.roundTwoDecimals(flowPercentages
										.get(flowValues));
					}
					if (tmpResult != 0) {
						if (corrSrcSNode.getId() == Integer.parseInt(flowValues
								.get(0))
								&& corrDstSNode.getId() == Integer
										.parseInt(flowValues.get(1))) {
							tempSrcSnode = getSnodeById(Integer
									.parseInt(flowValues.get(2)));
							tempDstSnode = getSnodeById(Integer
									.parseInt(flowValues.get(3)));
							tempSlink = sNet.findEdge(tempSrcSnode,
									tempDstSnode);
							// Create the new bandwidth demand that corresponds
							// to the percentage of BW in the solution of the
							// solver. tempSlink is part of the path mapping
							// tmpVlink

							newBwDem = new BandwidthDemand(tmpVlink);
							newBwDem.setDemandedBandwidth(MiscelFunctions
									.roundThreeDecimals(VlOriginalDemand
											* flowPercentages.get(flowValues)));
							tmpVlink.add(newBwDem);
							// Getting the factor for the bandwidth to CPU
							// hidden hop mapping
							if (hhFactor == 0) {
								for (IHiddenHopMapping hh : hhMappings) {
									if (hh instanceof BandwidthCpuHiddenHopMapping)
										hhFactor = ((BandwidthCpuHiddenHopMapping) hh)
												.getFactor();

									break;
								}
							}
							hiddenHop = null;
							if (!tmpVlink.getHiddenHopDemands().isEmpty()) {
								// Hidden hops are considered, new hidden hop
								// demand should be created
								tmpHhDemand = (CpuDemand) new BandwidthCpuHiddenHopMapping(
										hhFactor).transform(newBwDem);
								tmpVlink.addHiddenHopDemand(tmpHhDemand);
								if (!sNet.getSource(tempSlink).equals(
										corrSrcSNode)) {
									hiddenHop = sNet.getSource(tempSlink);
								} else {
									hiddenHop = null;
								}
							}
							if (!NodeLinkAssignation
									.vlmSingleLink(tmpVlink, newBwDem,
											tempSlink, hiddenHop, tmpHhDemand)){
								/*testing purposes*/
								for (Iterator<List<String>> cadiz = flow.keySet().iterator(); cadiz
								.hasNext();) {
									flowValues = cadiz.next();
									System.out.println(flowValues + "   "  + flow.get(flowValues) + "\n" );
								}
								/*////////////*/
								throw new AssertionError(
								"Some coding mistake, MIP model checks");						
							}
								
						}
					}
				}
			} else {
				//FIXME When source and destination nodes are the same, no mapping is performed
			}

		}
		return true;
	}

	/**
	 * 
	 * @param vNet
	 * @param id
	 * @return the virtual node corresponding to the id
	 */
	private VirtualNode getVnodeById(VirtualNetwork vNet, int id) {
		for (Iterator<VirtualNode> itt = vNet.getVertices().iterator(); itt
				.hasNext();) {
			VirtualNode tempVirNode = itt.next();
			if (tempVirNode.getId() == id)
				return tempVirNode;
		}
		return null;
	}

	/**
	 * 
	 * @param id
	 * @return the substrate node corresponding to id
	 */
	private SubstrateNode getSnodeById(int id) {
		for (SubstrateNode tempSubsNode : sNet.getVertices()) {
			if (tempSubsNode.getId() == id)
				return tempSubsNode;
		}
		return null;
	}

	private Map<List<String>, Double> findPercentages(
			Map<List<String>, Double> flow, VirtualNetwork vNet) {
		Map<List<String>, Double> newFlow = new LinkedHashMap<List<String>, Double>();
		Map<List<String>, Double> flowPercentages = new LinkedHashMap<List<String>, Double>();
		List<List<SubstrateNode>> mappedVLinks = new LinkedList<List<SubstrateNode>>();
		List<String> flowValues;
		SubstrateNode subNoSrc, subNoDst;
		double totalMappedFlow = 0;

		List<SubstrateNode> tempSubsNodePair = new LinkedList<SubstrateNode>();
		for (VirtualLink currVl : vNet.getEdges()) {
			tempSubsNodePair = new LinkedList<SubstrateNode>();
			tempSubsNodePair.add(nodeMapping.get(vNet.getSource(currVl)));
			tempSubsNodePair.add(nodeMapping.get(vNet.getDest(currVl)));
			if (!contains(mappedVLinks, tempSubsNodePair))
				mappedVLinks.add(tempSubsNodePair);
		}

		for (Iterator<List<String>> cad = flow.keySet().iterator(); cad
				.hasNext();) {
			flowValues = cad.next();
			if (MiscelFunctions.roundTwelveDecimals(flow.get(flowValues)) != 0) {
				newFlow.put(flowValues, MiscelFunctions
						.roundTwelveDecimals(flow.get(flowValues)));
			}
		}

		for (List<SubstrateNode> currNodePair : mappedVLinks) {
			subNoSrc = currNodePair.get(0);
			subNoDst = currNodePair.get(1);
			for (Iterator<List<String>> cad = newFlow.keySet().iterator(); cad
					.hasNext();) {
				flowValues = cad.next();
				if (subNoSrc.getId() == Integer.parseInt(flowValues.get(0))
						&& subNoDst.getId() == Integer.parseInt(flowValues
								.get(1))
						&& subNoSrc.getId() == Integer.parseInt(flowValues
								.get(2)))
					totalMappedFlow += newFlow.get(flowValues);
			}
			for (Iterator<List<String>> cad = newFlow.keySet().iterator(); cad
					.hasNext();) {
				flowValues = cad.next();
				if (subNoSrc.getId() == Integer.parseInt(flowValues.get(0))
						&& subNoDst.getId() == Integer.parseInt(flowValues
								.get(1))) {
					flowPercentages.put(
							flowValues,
							MiscelFunctions.roundTwelveDecimals(newFlow
									.get(flowValues) / totalMappedFlow));
				}
			}
			totalMappedFlow = 0;
		}
		return flowPercentages;
	}

	private boolean contains(List<List<SubstrateNode>> mappedVLinks,
			List<SubstrateNode> tempSubsNodePair) {
		for (List<SubstrateNode> currNodePair : mappedVLinks) {
			if (currNodePair.get(0).equals(tempSubsNodePair.get(0))
					&& currNodePair.get(1).equals(tempSubsNodePair.get(1)))
				return true;
		}

		return false;
	}

}
