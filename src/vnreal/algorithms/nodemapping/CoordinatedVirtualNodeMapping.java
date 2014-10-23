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
 * This class implements the coordinated node mapping algorithm
 * making use of the GLPK open source LP solver and its interface to work in
 * Java.
 * 
 * See: http://www.gnu.org/software/glpk/
 * http://sourceforge.net/projects/glpk-java/
 * 
 * This node mapping algorithm was proposed in:
 *  
 * - N. M. M. K. Chowdhury, Muntasir Raihnan Rahman, and Raouf Boutaba. Virtual
 * network embedding with coordinated node and link mapping. In Proc. IEEE
 * INFOCOM. IEEE Infocom, April 2009
 * 
 * @author Juan Felipe Botero
 * @author Lisset Diaz
 * @since 2010-10-15
 */

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import vnreal.algorithms.AbstractNodeMapping;
import vnreal.algorithms.utils.Consts;
import vnreal.algorithms.utils.CopyNetwork;
import vnreal.algorithms.utils.LpSolver;
import vnreal.algorithms.utils.MiscelFunctions;
import vnreal.algorithms.utils.NodeLinkAssignation;
import vnreal.algorithms.utils.dataSolverFile;
import vnreal.demands.AbstractDemand;
import vnreal.demands.CpuDemand;
import vnreal.hiddenhopmapping.BandwidthCpuHiddenHopMapping;
import vnreal.hiddenhopmapping.IHiddenHopMapping;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;
import vnreal.resources.BandwidthResource;
import vnreal.resources.CpuResource;

public class CoordinatedVirtualNodeMapping extends AbstractNodeMapping {

	// PRIVATE variables
	private int distance, type; // Distance to select the candidate nodes to be
	// mapped.
	// Type variable indicates the rounding type
	private double wCpu, wBw; // Weights of CPU and bandwidth
	private SubstrateNetwork augmentedSubstrate;
	private static Map<VirtualNode, List<SubstrateNode>> candiNodes;
	private static Map<VirtualNode, SubstrateNode> augmentedCorrespond;
	boolean firstTime = true;

	/**
	 * Constructor of the node mapping algorithm
	 * 
	 * @param sNet
	 * @param dist
	 * @param wCpu
	 * @param wBw
	 * @param type
	 * @param nodeOverload
	 */
	public CoordinatedVirtualNodeMapping(SubstrateNetwork sNet, int dist,
			double wCpu, double wBw, int type, boolean nodeOverload) {
		super(sNet, nodeOverload);
		this.distance = dist;
		this.type = type;
		this.wCpu = wCpu;
		this.wBw = wBw;
	}

	/**
	 * Node mapping method implementation
	 */
	@Override
	protected boolean nodeMapping(VirtualNetwork vNet) {

		Random intGenerator = new Random();
		// Temporal solution for executing
		// at the same time different algorithms using
		// this link mapping that must accede to different data files

		double hhFactor = 0;
		String dataFileName = Consts.LP_SOLVER_DATAFILE
				+ Integer.toString(intGenerator.nextInt(2001)) + ".dat";

		candiNodes = new LinkedHashMap<VirtualNode, List<SubstrateNode>>();
		LpSolver problemSolver = new LpSolver();
		List<VirtualNode> unMappedVirtualNodes = super.getUnmappedvNodes();
		List<SubstrateNode> unMappedSubstrateNodes = super.getUnmappedsNodes();

		if (augmentedCreation(vNet, unMappedVirtualNodes,
				unMappedSubstrateNodes, distance)) {
			Map<List<String>, Double> solverResult;
			Map<List<String>, Double> solverResultFlow;

			dataSolverFile lpNodeMappingData = new dataSolverFile(
					Consts.LP_SOLVER_FOLDER + dataFileName);

			if (hhMappings != null) {
			    for (IHiddenHopMapping hhCpuMapping : hhMappings) {
                                if (hhCpuMapping instanceof BandwidthCpuHiddenHopMapping) {
                                        hhFactor = ((BandwidthCpuHiddenHopMapping) hhCpuMapping)
                                                        .getFactor();
                                        break;
                                }
                        }
			}
			

			lpNodeMappingData.createDataSolverFile(augmentedSubstrate, sNet,
					vNet, nodeMapping, wCpu, wBw, true, hhFactor);

			problemSolver.solve(Consts.LP_SOLVER_FOLDER,
					Consts.LP_NODEMAPPING_MODEL, dataFileName);

			solverResult = MiscelFunctions.processSolverResult(
					problemSolver.getSolverResult(), "lambda[]");

			solverResultFlow = MiscelFunctions.processSolverResult(
					problemSolver.getSolverResultFlow(), "flow[]");

			return virtualNodeMapping(unMappedVirtualNodes, augmentedSubstrate,
					augmentedCorrespond, solverResult, solverResultFlow, type);
		} else {
			return false;
		}
	}

	/**
	 * Augmented substrate creation
	 * 
	 * @param vNet
	 * @param unmappedvNodes
	 *            (updated in the abstract node mapping)
	 * @param unmappedsNodes
	 *            (updated in the abstract node mapping)
	 * @param distance
	 * @return
	 */
	private boolean augmentedCreation(VirtualNetwork vNet,
			List<VirtualNode> unmappedvNodes,
			List<SubstrateNode> unmappedsNodes, int distance) {
		List<SubstrateNode> candidates = new LinkedList<SubstrateNode>();
		CopyNetwork copy = new CopyNetwork(sNet);
		augmentedCorrespond = new LinkedHashMap<VirtualNode, SubstrateNode>();
		augmentedSubstrate = copy.returnSubsCopy();
		CpuResource cpuRes;
		SubstrateNode newSubNode, tmpCandidate;
		BandwidthResource bwRes;
		double maxLinkResource = maxLinkResource();
		double maxNodeResource = maxNodeResource();
		SubstrateLink augSubsLink1, augSubsLink2;
		// The augmented substrate with the candidate nodes is created
		for (Iterator<VirtualNode> vn = unmappedvNodes.iterator(); vn.hasNext();) {
			VirtualNode tmpvNode = vn.next();

			for (AbstractDemand dem : tmpvNode) {
				if (dem instanceof CpuDemand) {
					candidates = findFulfillingNodes(tmpvNode, dem,
							unmappedsNodes, distance);
					candiNodes.put(tmpvNode, candidates);
				}
			}

			if (!candidates.isEmpty()) {
				newSubNode = new SubstrateNode();
				cpuRes = new CpuResource(newSubNode);
				// FIXME: This value should be modified to be always a very high
				// value
				// in comparison with the values of the substrate network cpu
				// resource.
				cpuRes.setCycles(maxNodeResource);
				newSubNode.add(cpuRes);
				augmentedSubstrate.addVertex(newSubNode);
				augmentedCorrespond.put(tmpvNode, newSubNode);
				nodeMapping.put(tmpvNode, newSubNode);
				for (Iterator<SubstrateNode> itSubNode = candidates.iterator(); itSubNode
						.hasNext();) {
					augSubsLink1 = new SubstrateLink();
					tmpCandidate = itSubNode.next();
					bwRes = new BandwidthResource(augSubsLink1);
					// This value should be modified to be always a very
					// high value (infinity)

					bwRes.setBandwidth(maxLinkResource);
					augSubsLink1.add(bwRes);
					augmentedSubstrate.addEdge(augSubsLink1, newSubNode,
							tmpCandidate);
					augSubsLink2 = new SubstrateLink();
					// FIXME: This value should be modified to be always a very
					// high value
					// in comparison with the values of the substrate network
					// cpu resource.
					bwRes = new BandwidthResource(augSubsLink2);
					bwRes.setBandwidth(maxLinkResource);
					augSubsLink2.add(bwRes);
					augmentedSubstrate.addEdge(augSubsLink2, tmpCandidate,
							newSubNode);
				}
			} else {
				return false;
			}

		}
		return true;
	}

	/**
	 * Method to find the substrate nodes that can be considered to be mapped to
	 * one virtual node
	 * 
	 * @param vNode
	 * @param dem
	 * @param filtratedsNodes
	 * @param dist
	 * @return
	 */
	private List<SubstrateNode> findFulfillingNodes(VirtualNode vNode,
			AbstractDemand dem, List<SubstrateNode> filtratedsNodes, int dist) {
		List<SubstrateNode> nodes = new LinkedList<SubstrateNode>();
		for (SubstrateNode n : filtratedsNodes) {
			if (nodeDistance(vNode, n, dist)) {
				for (AbstractResource res : n)
					if (res.accepts(dem) && res.fulfills(dem)) {
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
	 * This method receives the ouput of the LP solver and performs the node
	 * mapping.
	 */
	private boolean virtualNodeMapping(List<VirtualNode> unmappedvNodes,
			SubstrateNetwork augmentedSub,
			Map<VirtualNode, SubstrateNode> augmentedCorrespond,
			Map<List<String>, Double> result,
			Map<List<String>, Double> resultFlow, int type) {
		Double vtmp;
		Double totalWeight = 0.0, tempWeight;
		List<String> tmpValues;
		VirtualNode tmpvNode;
		SubstrateNode mappedNode, tmpSNode;
		Map<SubstrateNode, Double> candiNodesWeight = new LinkedHashMap<SubstrateNode, Double>();

		for (Iterator<VirtualNode> vn = unmappedvNodes.iterator(); vn.hasNext();) {
			tmpvNode = vn.next();
			mappedNode = augmentedCorrespond.get(tmpvNode);
			for (Iterator<List<String>> cad = resultFlow.keySet().iterator(); cad
					.hasNext();) {
				tmpValues = cad.next();
				vtmp = resultFlow.get(tmpValues);
				if ((mappedNode.getId() == Integer.parseInt(tmpValues.get(2)) || mappedNode
						.getId() == Integer.parseInt(tmpValues.get(3)))
						&& vtmp != 0) {

					if (mappedNode.getId() == Integer
							.parseInt(tmpValues.get(2))) {
						tmpSNode = getSubstrateNode(Integer.parseInt(tmpValues
								.get(3)));
						if (candiNodesWeight.get(tmpSNode) != null) {
							totalWeight = candiNodesWeight.get(tmpSNode) + vtmp;
						} else {
							totalWeight = vtmp;
						}

						candiNodesWeight.put(tmpSNode, totalWeight);
					}

					if (mappedNode.getId() == Integer
							.parseInt(tmpValues.get(3))) {
						tmpSNode = getSubstrateNode(Integer.parseInt(tmpValues
								.get(2)));
						if (candiNodesWeight.get(tmpSNode) != null) {
							totalWeight = candiNodesWeight.get(tmpSNode) + vtmp;
						} else {
							totalWeight = vtmp;
						}

						candiNodesWeight.put(tmpSNode, totalWeight);
					}
				}
			}
			// Calculating the X(name of the variables in the paper)
			// (lambda variables in the model in the code)
			for (Iterator<List<String>> cad = result.keySet().iterator(); cad
					.hasNext();) {
				tmpValues = cad.next();
				vtmp = result.get(tmpValues);
				if ((mappedNode.getId() == Integer.parseInt(tmpValues.get(0)))
						&& vtmp != 0) {
					tmpSNode = getSubstrateNode(Integer.parseInt(tmpValues
							.get(1)));
					if (candiNodesWeight.get(tmpSNode) != null)
						candiNodesWeight.put(tmpSNode,
								vtmp * candiNodesWeight.get(tmpSNode));
				}
			}
			/*
			 * Calculating the weight percentage of each candidate node
			 */
			totalWeight = 0.0;
			for (Iterator<SubstrateNode> it = candiNodesWeight.keySet()
					.iterator(); it.hasNext();) {
				tmpSNode = it.next();
				totalWeight += candiNodesWeight.get(tmpSNode);
			}
			for (Iterator<SubstrateNode> it = candiNodesWeight.keySet()
					.iterator(); it.hasNext();) {
				tmpSNode = it.next();
				tempWeight = candiNodesWeight.get(tmpSNode);
				candiNodesWeight.put(tmpSNode, tempWeight / totalWeight);
			}
			if (type == 0) {
				/*
				 * If the node selection is made deterministically, the node
				 * with the greatest weight will be chosen.
				 */
				mappedNode = greatest(candiNodesWeight,
						candiNodes.get(tmpvNode), tmpvNode);
			}
			if (type == 1) {
				/*
				 * If the node selection is made probabilistically random, each
				 * node will have a probability equal to its weight to be
				 * chosen.
				 */
				mappedNode = randomizedElection(candiNodesWeight,
						candiNodes.get(tmpvNode), tmpvNode);
			}
			if (mappedNode != null) {
				if (NodeLinkAssignation.vnm(tmpvNode, mappedNode)) {
					nodeMapping.put(tmpvNode, mappedNode);
				} else {
					throw new AssertionError("But we checked before!");
				}

			} else {
				// Node mapping can not be performed.
				return false;
			}
			candiNodesWeight.clear();
		}
		return true;
	}

	/**
	 * Return the position of the greatest value of a set, if the resultant
	 * value is already mapped, the following greatest value is returned. If
	 * there is not feasible node because they have been chosen, a greedy
	 * available resources strategy is followed.
	 * 
	 * @param augmented
	 * @param flow
	 * @param size
	 * @param mapped
	 * @param virtualNode
	 * @return the substrate node to be mapped.
	 */
	private SubstrateNode greatest(Map<SubstrateNode, Double> candiNodesWeight,
			List<SubstrateNode> candiNodes, VirtualNode currVNode) {
		double greatest = 0;
		double greatestAR = 0;
		SubstrateNode tmpSNode;
		SubstrateNode greatestSnode = null;

		for (Iterator<SubstrateNode> it = candiNodesWeight.keySet().iterator(); it
				.hasNext();) {
			tmpSNode = it.next();
			if (!nodeOverload) {
				if (candiNodesWeight.get(tmpSNode) > greatest
						&& !nodeMapping.containsValue(tmpSNode)) {
					greatest = candiNodesWeight.get(tmpSNode);
					greatestSnode = tmpSNode;
				}
			} else {
				if (candiNodesWeight.get(tmpSNode) > greatest
						&& NodeLinkAssignation.isMappable(currVNode, tmpSNode)) {
					greatest = candiNodesWeight.get(tmpSNode);
					greatestSnode = tmpSNode;
				}
			}
		}

		// Mapping with a greedy available resources strategy
		if (greatestSnode == null) {
			for (Iterator<SubstrateNode> it = candiNodes.iterator(); it
					.hasNext();) {
				tmpSNode = it.next();
				if (!nodeOverload) {
					if (getAvResources(tmpSNode) > greatestAR
							&& !nodeMapping.containsValue(tmpSNode)) {
						greatestAR = getAvResources(tmpSNode);
						greatestSnode = tmpSNode;
					}
				} else {
					if (getAvResources(tmpSNode) > greatestAR
							&& NodeLinkAssignation.isMappable(currVNode,
									tmpSNode)) {
						greatestAR = getAvResources(tmpSNode);
						greatestSnode = tmpSNode;
					}
				}

			}
		}
		return greatestSnode;
	}

	/**
	 * Return the node with a probability equal to its weight (accumulated
	 * flow), if the resultant value is already mapped, the node is taken out
	 * and the procedure is repeated. If, finally, there is not feasible node
	 * because they have all been chosen, a greedy available resources strategy
	 * is followed.
	 * 
	 * @param candiNodesWeight
	 * @param candidates
	 * @return
	 */
	private SubstrateNode randomizedElection(
			Map<SubstrateNode, Double> candiNodesWeight,
			List<SubstrateNode> candiNodes, VirtualNode currVNode) {
		double spareWeight = 0, newWeight;
		boolean isCandidate = true;
		double greaterAR = 0;
		SubstrateNode tmpSNode;
		SubstrateNode tempCandiSNode = null;
		if(candiNodesWeight.isEmpty()){
			for (SubstrateNode tempSNode : candiNodes) {
				if (getAvResources(tempSNode) > greaterAR
						&& !nodeMapping.containsValue(tempSNode)) {
					greaterAR = getAvResources(tempSNode);
					tempCandiSNode = tempSNode;
				}
			}
			return tempCandiSNode;
		}

		tempCandiSNode = discrete(candiNodesWeight);
		if (nodeOverload
				&& NodeLinkAssignation.isMappable(currVNode, tempCandiSNode)) {
			return tempCandiSNode;
		} else {
			if (!nodeMapping.containsValue(tempCandiSNode)) {
				return tempCandiSNode;
			} else {
				while (isCandidate) {
					spareWeight = 0;
					candiNodesWeight.put(tempCandiSNode, 0.0);
					for (Iterator<SubstrateNode> it = candiNodesWeight.keySet()
							.iterator(); it.hasNext();) {
						tmpSNode = it.next();
						spareWeight += candiNodesWeight.get(tmpSNode);
					}
					if (spareWeight != 0) {
						for (Iterator<SubstrateNode> it = candiNodesWeight
								.keySet().iterator(); it.hasNext();) {
							tmpSNode = it.next();
							newWeight = candiNodesWeight.get(tmpSNode)
									/ spareWeight;
							candiNodesWeight.put(tmpSNode, newWeight);
						}
					}
					tempCandiSNode = discrete(candiNodesWeight);
					if (tempCandiSNode != null
							&& !nodeMapping.containsValue(tempCandiSNode)) {
						return tempCandiSNode;
					}
					if (tempCandiSNode != null
							&& nodeOverload
							&& NodeLinkAssignation.isMappable(currVNode,
									tempCandiSNode)) {
						return tempCandiSNode;
					}
					if (tempCandiSNode == null)
						isCandidate = false;
				}
			}
			// Mapping with a greedy available resources strategy
			if (tempCandiSNode == null) {
				for (Iterator<SubstrateNode> it = candiNodes.iterator(); it
						.hasNext();) {
					tmpSNode = it.next();
					if (getAvResources(tmpSNode) > greaterAR
							&& !nodeMapping.containsValue(tmpSNode)) {
						greaterAR = getAvResources(tmpSNode);
						tempCandiSNode = tmpSNode;
					}
				}
			}
		}
		return tempCandiSNode;
	}

	/**
	 * Method that returns the available resources (AR) of one substrate node.
	 * AR is defined as the CPU capacity of the resource, multiplied by the sum
	 * of the bandwidth of its incident arcs. Input: Substrate Node Output: The
	 * available resources of the node (Available resources are calculated as
	 * the product of the node's remaining CPU capacity and the sum of the
	 * remaining bandwidth of its incident links)
	 * 
	 * @param sNode
	 * @return
	 */
	private double getAvResources(SubstrateNode sNode) {
		double sumIncBW = 0, nodeCpu = 0;
		List<SubstrateLink> augmentedLinks = new LinkedList<SubstrateLink>(
				augmentedSubstrate.getEdges());
		SubstrateLink tmpSubsLink;
		for (Iterator<SubstrateLink> itSubLink = augmentedLinks.iterator(); itSubLink
				.hasNext();) {
			tmpSubsLink = itSubLink.next();
			if (sNode.equals(augmentedSubstrate.getSource(tmpSubsLink))) {
				for (AbstractResource res : tmpSubsLink) {
					if (res instanceof BandwidthResource)
						sumIncBW += ((BandwidthResource) res)
								.getAvailableBandwidth();

				}
			}

		}
		for (AbstractResource res : sNode) {
			if (res instanceof CpuResource)
				nodeCpu = ((CpuResource) res).getAvailableCycles();

		}
		return (nodeCpu * sumIncBW);
	}

	private SubstrateNode getSubstrateNode(int id) {
		for (SubstrateNode n : sNet.getVertices()) {
			if (n.getId() == id) {
				return n;
			}
		}
		return null;
	}

	private static SubstrateNode discrete(
			Map<SubstrateNode, Double> candiNodesWeight) {
		// precondition: sum of array entries equals 1
		SubstrateNode tmpSNode;
		double r = Math.random();
		double sum = 0.0;
		for (Iterator<SubstrateNode> it = candiNodesWeight.keySet().iterator(); it
				.hasNext();) {
			tmpSNode = it.next();
			sum += candiNodesWeight.get(tmpSNode);
			if (sum >= r)
				return tmpSNode;
		}
		return null;
	}

	// Returns the maximum node resource multiplied by 100
	private Double maxNodeResource() {
		double max = 0;
		for (SubstrateNode n : sNet.getVertices()) {
			for (AbstractResource res : n) {
				// Change it, and instance of abstract resource
				if (res instanceof CpuResource) {
					if ((((CpuResource) res).getAvailableCycles()) >= max)
						max = (((CpuResource) res).getAvailableCycles());

				}
			}
		}
		return (max * 100);
	}

	// Returns the maximum link resource multiplied by 1000
	private Double maxLinkResource() {
		double max = 0;
		for (Iterator<SubstrateLink> links = sNet.getEdges().iterator(); links
				.hasNext();) {
			SubstrateLink temp = links.next();
			for (AbstractResource res : temp) {
				if (res instanceof BandwidthResource) {
					if ((((BandwidthResource) res).getAvailableBandwidth()) >= max)
						max = (((BandwidthResource) res)
								.getAvailableBandwidth());
				}
			}

		}
		return (max * 100);
	}

}
