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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;

import vnreal.algorithms.AbstractNodeMapping;
import vnreal.algorithms.utils.Consts;
import vnreal.algorithms.utils.LpSolver;
import vnreal.algorithms.utils.MiscelFunctions;
import vnreal.algorithms.utils.NodeLinkAssignation;
import vnreal.algorithms.utils.SubstrateLinkSingleTransformer;
import vnreal.algorithms.utils.dataSolverFile;
import vnreal.algorithms.utils.breadthfirstsearch.BFS;
import vnreal.algorithms.utils.tree.GenericTree;
import vnreal.algorithms.utils.tree.GenericTreeNode;
import vnreal.demands.AbstractDemand;
import vnreal.demands.BandwidthDemand;
import vnreal.demands.CpuDemand;
import vnreal.hiddenhopmapping.BandwidthCpuHiddenHopMapping;
import vnreal.hiddenhopmapping.IHiddenHopMapping;
import vnreal.network.Node;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

public class BFSNRnodeMappingPathSplitting extends AbstractNodeMapping {
	private double epsilon, wCpu, wBw, maxHop;
	private boolean withDist;
	private int dist, backtrackLimit;
	private Map<Node<?>, Double> SortedNodeNR_i_opt_virt;
	GenericTree<Node<?>> bfsTree;

	public BFSNRnodeMappingPathSplitting(SubstrateNetwork sNet, double wCpu,
			double wBandwidth, boolean subsNodeOverload, double epsilon,
			boolean withDist, int dist, int maxHop) {
		super(sNet, subsNodeOverload);
		this.epsilon = epsilon;
		this.withDist = withDist;
		this.dist = dist;
		this.backtrackLimit = 0;
		this.SortedNodeNR_i_opt_virt = null;
		this.wCpu = wCpu;
		this.wBw = wBandwidth;
		this.bfsTree = null;
		this.maxHop = maxHop;

	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean nodeMapping(VirtualNetwork vNet) {
		this.backtrackLimit = 3 * vNet.getVertices().size();
		int backtrack_count = 0;
		List<VirtualNode> discVnodes = new LinkedList<VirtualNode>();
		Map<Node<?>, Double> SortedNodeNR_i_opt_subs = MiscelFunctions
				.sortByValue(MiscelFunctions.create_NR(sNet, epsilon));
		SortedNodeNR_i_opt_virt = MiscelFunctions.sortByValue(MiscelFunctions
				.create_NR(vNet, epsilon));
		boolean removeNode = false;
		VirtualNode discVnode = null;

		Map<VirtualNode, List<SubstrateNode>> currCandidateSet = new LinkedHashMap<VirtualNode, List<SubstrateNode>>();
		List<SubstrateNode> tempCandidateSet = new LinkedList<SubstrateNode>();

		/* Throwing away disconnected virtual nodes */
		if (SortedNodeNR_i_opt_virt.containsValue(0.0)) {
			for (Iterator<Node<?>> vNode = SortedNodeNR_i_opt_virt.keySet()
					.iterator(); vNode.hasNext();) {
				if (removeNode) {
					VirtualNode tempNode = discVnode;
					discVnode = (VirtualNode) vNode.next();
					SortedNodeNR_i_opt_virt.remove(tempNode);
				} else {
					discVnode = (VirtualNode) vNode.next();
				}
				removeNode = false;
				if (SortedNodeNR_i_opt_virt.get(discVnode) == 0.0
						&& vNet.getIncidentEdges(discVnode).isEmpty()) {
					discVnodes.add(discVnode);
					if (!vNode.hasNext()) {
						SortedNodeNR_i_opt_virt.remove(discVnode);
					} else {
						removeNode = true;
					}
				}

			}
		}
		@SuppressWarnings("rawtypes")
		List<SubstrateNode> tmpCandiNodes = new LinkedList(
				SortedNodeNR_i_opt_subs.keySet());
		// Creation of candidates for each virtual node //
		for (VirtualNode currVnode : vNet.getVertices()) {
			if (withDist) {
				currCandidateSet.put(currVnode, MiscelFunctions
						.findFulfillingNodes(currVnode, tmpCandiNodes, dist,
								nodeMapping));
			} else {
				currCandidateSet.put(currVnode, MiscelFunctions
						.findFulfillingNodes(currVnode, tmpCandiNodes,
								nodeMapping));
			}
		}
		// ///////////////////////////////////////////////
		BFS BFSVnet = new BFS(SortedNodeNR_i_opt_virt.keySet().iterator()
				.next(), vNet, SortedNodeNR_i_opt_virt);
		List<Node<?>> bfsQueue = BFSVnet.search();
		bfsTree = BFSVnet.getNodeTree();
		boolean wasBacktrack = false;

		for (int i = 0; i < bfsQueue.size(); i++) {
			VirtualNode currVnode = (VirtualNode) bfsQueue.get(i);
			if (wasBacktrack) {
				tempCandidateSet = currCandidateSet.get(currVnode);
				tempCandidateSet.remove(nodeMapping.get(currVnode));
				if (tempCandidateSet.isEmpty())
					return false;

				currCandidateSet.put(currVnode, tempCandidateSet);
				MiscelFunctions.clearVnodeMappings(vNet, currVnode);
				nodeMapping.remove(currVnode);
			}
			if (currCandidateSet.get(currVnode).isEmpty()
					|| !match(vNet, currVnode, currCandidateSet.get(currVnode))) {
				backtrack_count++;
				i = i - 2;
				wasBacktrack = true;
			} else {
				wasBacktrack = false;
			}
			if (backtrack_count > backtrackLimit)
				return false;
		}
		/* Mapping of disconnected Nodes */
		if (!discVnodes.isEmpty()) {
			for (VirtualNode discVnod : discVnodes) {

				SubstrateNode chosenSnode = currCandidateSet.get(discVnod)
						.iterator().next();
				if (!NodeLinkAssignation.vnm(discVnod, chosenSnode))
					throw new AssertionError("But we checked before!");

				nodeMapping.put(discVnod, chosenSnode);

			}
		}
		/* ************************************* */
		return true;
	}
	
	
	private boolean match(VirtualNetwork vNet, VirtualNode vNode,
			List<SubstrateNode> candidates) {
		Iterator<Node<?>> tempIt = SortedNodeNR_i_opt_virt.keySet().iterator();
		List<SubstrateNode> tempCandiNodes = null;

		if (((VirtualNode) tempIt.next()).equals(vNode)) {
			if (NodeLinkAssignation.vnm(vNode, candidates.iterator().next())) {
				nodeMapping.put(vNode, candidates.iterator().next());
				return true;
			} else {
				throw new AssertionError("But we checked before!");
			}
		}
		GenericTreeNode<Node<?>> tempNode = bfsTree.find(vNode);
		VirtualNode parentNode = (VirtualNode) tempNode.getParent().getData();

		for (int tmpK = 1; tmpK < maxHop + 1; tmpK++) {
			DijkstraDistance<SubstrateNode, SubstrateLink> distanceGraph = new DijkstraDistance<SubstrateNode, SubstrateLink>(
					sNet, new SubstrateLinkSingleTransformer(), false);
			distanceGraph.setMaxDistance(tmpK);
			Map<SubstrateNode, Number> orderedMappedCandidates = distanceGraph
					.getDistanceMap(nodeMapping.get(parentNode));
			tempCandiNodes = new LinkedList<SubstrateNode>(
					orderedMappedCandidates.keySet());
			candidates = new LinkedList<SubstrateNode>();
			
			if(!orderedMappedCandidates.isEmpty()){
				if (withDist) {
					candidates = MiscelFunctions.findFulfillingNodes(vNode,
							tempCandiNodes, dist, nodeMapping);
				} else {
					candidates = MiscelFunctions.findFulfillingNodes(vNode,
							tempCandiNodes, nodeMapping);
				}
				for (SubstrateNode sCorrNode : candidates) {
					if (MapPathsWithMappedNodes(vNet, vNode, sCorrNode)) {
						if (!NodeLinkAssignation.vnm(vNode, sCorrNode)) {
							throw new AssertionError("But we checked before!");
						} else {
							nodeMapping.put(vNode, sCorrNode);
							return true;
						}
					} else {
						MiscelFunctions.clearVnodeMappings(vNet, vNode);
					}
				}
			}
		}
		return false;
	}


	private boolean MapPathsWithMappedNodes(VirtualNetwork vNet,
			VirtualNode vNode, SubstrateNode sNode) {
		SubstrateNode dstNode, srcNode;
		for (VirtualLink vLink : vNet.getOutEdges(vNode)) {

			if (nodeMapping.containsKey(vNet.getDest(vLink))
					&& !MiscelFunctions.hasLinkMappings(vLink)) {
				dstNode = nodeMapping.get(vNet.getDest(vLink));
				if (!performLinkMapping(sNode, dstNode, vLink, vNet)) {
					return false;
				}
			}
		}
		for (VirtualLink vLink : vNet.getInEdges(vNode)) {

			if (nodeMapping.containsKey(vNet.getSource(vLink))
					&& !MiscelFunctions.hasLinkMappings(vLink)) {
				srcNode = nodeMapping.get(vNet.getSource(vLink));
				if (!performLinkMapping(srcNode, sNode, vLink, vNet)) {
					return false;
				}
			}
		}

		return true;
	}

	private boolean performLinkMapping(SubstrateNode srcSnode,
			SubstrateNode dstSnode, VirtualLink vLink, VirtualNetwork vNet) {
		Random intGenerator = new Random();// FIXME Temp solution for executing
		// at the same time different algorithms using
		// this link mapping that must accede to different data files
		LpSolver problemSolver = new LpSolver();
		SubstrateNode tSNode = null, tDNode = null, hiddenHop;
		SubstrateLink tSLink;
		BandwidthDemand newBwDem, originalBwDem = null;
		CpuDemand tmpHhDemand = null;
		Map<List<String>, Double> solverResult;
		String dataFileName = Consts.LP_SOLVER_DATAFILE
				+ Integer.toString(intGenerator.nextInt(2001)) + ".dat";
		dataSolverFile lpLinkMappingData = new dataSolverFile(
				Consts.LP_SOLVER_FOLDER + dataFileName);
		lpLinkMappingData.createSingleDataSolverFile(sNet, srcSnode, dstSnode,
				vLink, wBw, wCpu);
		problemSolver.solve(Consts.LP_SOLVER_FOLDER,
				Consts.LP_LINKMAPPING_MODEL_HIDDENHOPS, dataFileName);
		double hhFactor = 0;

		if (problemSolver.problemFeasible()) {

			// In model HHVNE-Model.mod in /ILP-LP-Models is easy to see
			// that lambda is the variable of the multi-commodity flow problem
			// indicating if a substrate node is part of the solution to
			// map a virtual node demand.
			solverResult = MiscelFunctions.processSolverResult(problemSolver
					.getSolverResult(), "lambda[]");

			// Get current VirtualLink demand
			for (AbstractDemand dem : vLink) {
				if (dem instanceof BandwidthDemand) {
					originalBwDem = (BandwidthDemand) dem;
					break;
				}
			}

			// Iterate all links values given by the solver.
			for (Iterator<List<String>> cad = solverResult.keySet().iterator(); cad
					.hasNext();) {
				List<String> tmpValues = cad.next();
				Double vtmp = MiscelFunctions.roundTwelveDecimals(solverResult
						.get(tmpValues));

				if (srcSnode.getId() == Integer.parseInt(tmpValues.get(0))
						&& dstSnode.getId() == Integer.parseInt(tmpValues
								.get(1)) && vtmp != 0) {
					for (SubstrateNode n : sNet.getVertices()) {
						if (Integer.parseInt(tmpValues.get(2)) == n.getId()) {
							tSNode = n;
						} else {
							if (Integer.parseInt(tmpValues.get(3)) == n.getId()) {
								tDNode = n;
							}
						}
					}
					// get the susbstrate link (part of the mapping path
					// to dstSnode from srcSnode)
					tSLink = sNet.findEdge(tSNode, tDNode);
					// Create the new bandwidth demand that corresponds
					// to the percentage of BW in the solution of the
					// solver. tSLink is part of the path mapping tmpl

					newBwDem = new BandwidthDemand(vLink);
					newBwDem.setDemandedBandwidth(MiscelFunctions
							.roundThreeDecimals(vtmp
									* originalBwDem.getDemandedBandwidth()));

					vLink.add(newBwDem);

					// Getting the factor for the bandwidth to CPU
					// hidden hop mapping
					for (IHiddenHopMapping hh : hhMappings) {
						if (hh instanceof BandwidthCpuHiddenHopMapping)
							hhFactor = ((BandwidthCpuHiddenHopMapping) hh)
									.getFactor();

						break;
					}
					hiddenHop = null;
					if (!vLink.getHiddenHopDemands().isEmpty()) {
						// Hidden hops are considered, new hidden hop
						// demand should be created
						tmpHhDemand = (CpuDemand) new BandwidthCpuHiddenHopMapping(
								hhFactor).transform(newBwDem);
						vLink.addHiddenHopDemand(tmpHhDemand);
						if (!sNet.getSource(tSLink).equals(srcSnode)) {
							hiddenHop = sNet.getSource(tSLink);
						} else {
							hiddenHop = null;
						}
					}
					if (!NodeLinkAssignation.vlmSingleLink(vLink, newBwDem,
							tSLink, hiddenHop, tmpHhDemand))
						throw new AssertionError("But we checked before!");
				}
			}
		} else {
			return false;
		}
		return true;
	}

}
