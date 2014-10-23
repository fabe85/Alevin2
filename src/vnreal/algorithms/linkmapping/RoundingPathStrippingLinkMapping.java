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
package vnreal.algorithms.linkmapping;

/**
 * This class implements the rounding path splitting virtual link mapping algorithm
 * making use of the GLPK open source LP solver and its interface to work in
 * Java.
 * 
 * See: http://www.gnu.org/software/glpk/
 * http://sourceforge.net/projects/glpk-java/
 * 
 *  
 * @author Lisset Diaz
 * @author Juan Felipe Botero
 * @since 2011-02-21
 */

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import vnreal.algorithms.AbstractLinkMapping;
import vnreal.algorithms.utils.Consts;
import vnreal.algorithms.utils.EppsteinAlgorithm;
import vnreal.algorithms.utils.LinkWeight;
import vnreal.algorithms.utils.LpSolver;
import vnreal.algorithms.utils.MiscelFunctions;
import vnreal.algorithms.utils.NodeLinkAssignation;
import vnreal.algorithms.utils.dataSolverFile;
import vnreal.algorithms.utils.breadthfirstsearch.BreadthFirstSearch;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

public class RoundingPathStrippingLinkMapping extends AbstractLinkMapping {

	// PRIVATE variables bandwidth and cpu weights and type and the rounding
	// type
	// (0: Deterministic Rounding, 1: Randomized Rounding) and k is the number
	// of shortest paths.

	private int type, k;
	private double wBw, wCpu;

	/**
	 * Method constructor
	 * 
	 * @param sNet
	 *            substrate network
	 * @param cpuWeight
	 *            weight assigned to cpu demands
	 * @param bwWeight
	 *            weight assigned to bandwidth demands
	 * @param k
	 *            number of considered shortest paths
	 * @param type
	 *            rounding type (Deterministic or Randomized)
	 */
	public RoundingPathStrippingLinkMapping(SubstrateNetwork sNet,
			double cpuWeight, double bwWeight, int k, int type) {
		super(sNet);
		this.wBw = bwWeight;
		this.wCpu = cpuWeight;
		this.k = k;
		this.type = type;
	}

	/**
	 * Implementation of link mapping method. Resolution of a multi-commodity
	 * flow problem and rounding of multiple paths
	 */
	@Override
	protected boolean linkMapping(VirtualNetwork vNet,
			Map<VirtualNode, SubstrateNode> nodeMapping) {
		this.processedLinks = 0;
		this.mappedLinks = 0;
		Map<List<String>, Double> solverResult;
		SubstrateNode srcSnode = null;
		SubstrateNode dstSnode = null;
		SubstrateNode tSNode = new SubstrateNode();
		SubstrateNode tDNode = new SubstrateNode();
		SubstrateLink tSLink;
		List<SubstrateLink> tSPath;
		List<SubstrateLink> mappedPath = null;
		Map<List<SubstrateLink>, Double> candidatePaths = new LinkedHashMap<List<SubstrateLink>, Double>();
		List<VirtualLink> unMappedLinks = new LinkedList<VirtualLink>();
		List<List<SubstrateLink>> paths = null;

		Random intGenerator = new Random();

		String dataFileName = Consts.LP_SOLVER_DATAFILE
				+ Integer.toString(intGenerator.nextInt(2001)) + ".dat";
		LpSolver problemSolver = new LpSolver();
		dataSolverFile lpLinkMappingData = new dataSolverFile(
				Consts.LP_SOLVER_FOLDER + dataFileName);

		lpLinkMappingData.createDataSolverFile(sNet, null, vNet, nodeMapping,
				wBw, wCpu, false, 0); // Process all the current

		// Solving the multi-commodity flow problem
		problemSolver.solve(Consts.LP_SOLVER_FOLDER,
				Consts.LP_LINKMAPPING_MODEL_HIDDENHOPS, dataFileName);

		if (problemSolver.problemFeasible()) {
			solverResult = MiscelFunctions.processSolverResult(
					problemSolver.getSolverResult(), "lambda[]");

			// Iterate all VirtualLinks on the current VirtualNetwork
			for (Iterator<VirtualLink> links = vNet.getEdges().iterator(); links
					.hasNext();) {
				Map<SubstrateLink, Double> newSNet = new LinkedHashMap<SubstrateLink, Double>();
				VirtualLink tmpl = links.next();
				mappedLinks++; // increase number of processed.

				newSNet.clear();
				candidatePaths.clear();

				// Find the source and destiny of the current VirtualLink (tmpl)
				VirtualNode srcVnode = vNet.getSource(tmpl);
				VirtualNode dstVnode = vNet.getDest(tmpl);

				// Find their mapped SubstrateNodes
				srcSnode = nodeMapping.get(srcVnode);
				dstSnode = nodeMapping.get(dstVnode);

				/*
				 * If source and destination substrate node are different the
				 * algorithm is performed , in any other case, the virtual link
				 * demand will not be mapped because the link is created between
				 * the same node.
				 */
				if (!srcSnode.equals(dstSnode)) {
					// Iterate all links values given by the solver.
					for (Iterator<List<String>> cad = solverResult.keySet()
							.iterator(); cad.hasNext();) {
						List<String> tmpValues = cad.next();
						Double vtmp = MiscelFunctions
								.roundTwelveDecimals(solverResult
										.get(tmpValues));

						if (srcSnode.getId() == Integer.parseInt(tmpValues
								.get(0))
								&& dstSnode.getId() == Integer
										.parseInt(tmpValues.get(1))
								&& vtmp != 0) {
							for (SubstrateNode n : sNet.getVertices()) {
								if (Integer.parseInt(tmpValues.get(2)) == n
										.getId()) {
									tSNode = n;
								} else {
									if (Integer.parseInt(tmpValues.get(3)) == n
											.getId()) {
										tDNode = n;
									}
								}
							}
							// get the substrate link (part of the mapping path
							// to dstSnode from srcSnode)
							tSLink = sNet.findEdge(tSNode, tDNode);
							newSNet.put(tSLink, vtmp);
						}
					}
					do {
						// Find a path
						BreadthFirstSearch bfs = new BreadthFirstSearch(
								newSNet.keySet(), srcSnode.getId(),
								dstSnode.getId(), sNet);
						tSPath = bfs.search();

						if (tSPath == null) {
							// BFS hasn't found a path.
							break;
						}

						// Find the lowest weight (pathW) initially with a very
						// high value in the path that will
						// be the weight of the path
						double pathW = 1000;// This value will always be the
						// greatest because the
						// weights are distributed from 0 to 1
						for (SubstrateLink l : tSPath) {
							if (newSNet.get(l) < pathW)
								pathW = newSNet.get(l);
						}

						// Add a new entry to the list of candidates paths
						candidatePaths.put(tSPath, pathW);

						// Update weights of newSNet
						for (SubstrateLink l : tSPath) {
							if ((newSNet.get(l) - pathW) != 0) {
								newSNet.put(l, newSNet.get(l) - pathW);
							} else {
								newSNet.remove(l);
							}
						}
					} while (!newSNet.isEmpty());

					if (type == 0) {
						/*
						 * If the node selection is made deterministically, the
						 * path with the greatest weight that fulfills the
						 * demands will be chosen.
						 */
						mappedPath = greatest(srcSnode, candidatePaths, tmpl);
					}
					if (type == 1) {
						/*
						 * If the node selection is made probabilistically
						 * random, each path will have a probability equal to
						 * its weight to be chosen.
						 */
						mappedPath = randomizedElection(srcSnode,
								candidatePaths, tmpl);
					}

					if (mappedPath == null) {
						unMappedLinks.add(tmpl);
					} else {// if a path fulfilling the demand has been chosen,
						// link mapping is performed
						if (!NodeLinkAssignation.vlm(tmpl, mappedPath, sNet,
								srcSnode))
							throw new AssertionError("But we checked before!");
					}
				} else {
					// FIXME Hidden hops demand will be applied?
				}
			}
			// For the virtual links that could not be mapped. A
			// k-shortest path strategy is followed.
			if (!unMappedLinks.isEmpty()) {
				for (VirtualLink unMappedVl : unMappedLinks) {
					// Find the source and destiny of the current VirtualLink
					// (tmpl)
					VirtualNode srcVnode = vNet.getSource(unMappedVl);
					VirtualNode dstVnode = vNet.getDest(unMappedVl);

					// Find their mapped SubstrateNodes
					srcSnode = nodeMapping.get(srcVnode);
					dstSnode = nodeMapping.get(dstVnode);

					// Search for path in filtered substrate using
					// KShortestPaths
					LinkWeight linkWeight = new LinkWeight();
					EppsteinAlgorithm kshortestPaths = new EppsteinAlgorithm(
							sNet, linkWeight);

					// get the k shortest paths to the dstSnode in
					// increasing order of weight
					paths = kshortestPaths.getShortestPaths(srcSnode, dstSnode,
							k);
					mappedPath = null;
					for (List<SubstrateLink> path : paths) {

						// Verify if the path fulfills the demand
						if (NodeLinkAssignation.verifyPath(unMappedVl, path,
								srcSnode, sNet)) {
							// If the path has been verified, the path is
							// chosen if not,
							// the following shortest path is verified
							mappedPath = path;
							break;
						}
					}
					paths.clear();
					// if a path fulfilling the demand has been chosen, link
					// mapping is performed
					if (mappedPath != null) {
						// Perform virtual link mapping (VLM) for each link in
						// the path.
						if (!NodeLinkAssignation.vlm(unMappedVl, mappedPath,
								sNet, srcSnode))
							throw new AssertionError("But we checked before!");

					} else {// Not path available, link mapping can not be
						// performed
						processedLinks = vNet.getEdges().size();
						return false;
					}
				}
			}
		} else {
			processedLinks = vNet.getEdges().size();
			return false;
		}
		processedLinks = vNet.getEdges().size();
		return true;
	}

	/**
	 * 
	 * @param srcSNode
	 *            Source substrate node of the substrate path
	 * @param candidatePaths
	 *            set of candidate paths
	 * @param demBW
	 *            Bandwidth demanded by the virtual link being processed
	 * @param vl
	 *            virtual link
	 * @return a path with a probability equal to its weight (accumulated flow).
	 *         The method returns null if no path accomplishes the demands
	 */
	private List<SubstrateLink> randomizedElection(SubstrateNode srcSNode,
			Map<List<SubstrateLink>, Double> candidatePaths, VirtualLink vl) {
		double spareWeight = 0, newWeight;
		boolean isCandidate = true;
		List<SubstrateLink> greaterPath = null;
		List<SubstrateLink> tmpPath;

		List<SubstrateLink> tempCandiSPath;

		tempCandiSPath = discrete(candidatePaths);
		if (NodeLinkAssignation.verifyPath(vl, tempCandiSPath, srcSNode, sNet)) {
			greaterPath = tempCandiSPath;
		} else {
			while (isCandidate) {
				spareWeight = 0;
				candidatePaths.put(tempCandiSPath, 0.0);
				for (Iterator<List<SubstrateLink>> it = candidatePaths.keySet()
						.iterator(); it.hasNext();) {
					tmpPath = it.next();
					spareWeight += candidatePaths.get(tmpPath);
				}
				if (spareWeight != 0) {
					for (Iterator<List<SubstrateLink>> it = candidatePaths
							.keySet().iterator(); it.hasNext();) {
						tmpPath = it.next();
						newWeight = candidatePaths.get(tmpPath) / spareWeight;
						candidatePaths.put(tmpPath, newWeight);
					}
				}
				tempCandiSPath = discrete(candidatePaths);
				if (tempCandiSPath != null
						&& NodeLinkAssignation.verifyPath(vl, tempCandiSPath,
								srcSNode, sNet)) {
					greaterPath = tempCandiSPath;
					isCandidate = false;
				}
				if (isEmpty(candidatePaths)) {
					isCandidate = false;
				}
			}
		}
		return greaterPath;
	}

	/**
	 * 
	 * @param srcSNode
	 *            Source substrate node of the substrate path
	 * @param candidatePaths
	 *            set of candidate paths
	 * @param demBW
	 *            Bandwidth demanded by the virtual link being processed
	 * @param vl
	 *            virtual link
	 * @return the position of the great value of a set of paths. The method
	 *         returns null if no path accomplishes the demands.
	 */
	private List<SubstrateLink> greatest(SubstrateNode srcSNode,
			Map<List<SubstrateLink>, Double> candidatePaths, VirtualLink vl) {
		double greater = 0;
		List<SubstrateLink> tmpPath;
		List<SubstrateLink> greaterPath = null;

		for (Iterator<List<SubstrateLink>> it = candidatePaths.keySet()
				.iterator(); it.hasNext();) {
			tmpPath = it.next();
			if (candidatePaths.get(tmpPath) > greater
					&& NodeLinkAssignation.verifyPath(vl, tmpPath, srcSNode,
							sNet)) {
				greater = candidatePaths.get(tmpPath);
				greaterPath = tmpPath;
			}
		}
		return greaterPath;
	}

	/**
	 * @param candidatePaths
	 * @return A path with a probability equal to its weight
	 */
	private static List<SubstrateLink> discrete(
			Map<List<SubstrateLink>, Double> candidatePaths) {
		// precondition: sum of array entries equals 1
		List<SubstrateLink> tmpSPath;
		double r = Math.random();
		double sum = 0.0;
		for (Iterator<List<SubstrateLink>> it = candidatePaths.keySet()
				.iterator(); it.hasNext();) {
			tmpSPath = it.next();
			sum += candidatePaths.get(tmpSPath);
			if (sum >= r)
				return tmpSPath;
		}
		return null;
	}

	private static boolean isEmpty(
			Map<List<SubstrateLink>, Double> listPathsWeight) {
		for (Iterator<List<SubstrateLink>> it = listPathsWeight.keySet()
				.iterator(); it.hasNext();) {
			List<SubstrateLink> tmpSPath = it.next();
			if (listPathsWeight.get(tmpSPath) != 0.000)
				return false;
		}
		return true;
	}

}
