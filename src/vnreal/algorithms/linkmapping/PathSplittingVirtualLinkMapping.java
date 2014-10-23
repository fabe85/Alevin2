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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import vnreal.algorithms.AbstractLinkMapping;
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
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

/**
 * This class implements the path splitting virtual link mapping algorithm
 * making use of the GLPK open source LP solver and its interface to work in
 * Java.
 * 
 * See: http://www.gnu.org/software/glpk/
 * http://sourceforge.net/projects/glpk-java/
 * 
 * This virtual link mapping algorithm has been used in different papers:
 * 
 * See: - Minlan Yu, Yung Yi, Jennifer Rexford, and Mung Chiang. Rethinking
 * virtual network embedding: Substrate support for path splitting and
 * migration. ACM SIGCOMM CCR, 38(2):17–29, April 2008.
 * 
 * - N. M. M. K. Chowdhury, Muntasir Raihnan Rahman, and Raouf Boutaba. Virtual
 * network embedding with coordinated node and link mapping. In Proc. IEEE
 * INFOCOM. IEEE Infocom, April 2009
 * 
 * @author Lisset Diaz
 * @author Juan Felipe Botero
 * @since 2010-10-15
 */
public class PathSplittingVirtualLinkMapping extends AbstractLinkMapping {

	// Private variables indicating the weights given to the CPU and BW in
	// the algorithm
	private double wBw, wCpu;

	public PathSplittingVirtualLinkMapping(SubstrateNetwork sNet,
			double cpuWeight, double bwWeight) {
		super(sNet);
		this.wBw = bwWeight;
		this.wCpu = cpuWeight;
	}

	/**
	 * Implementation of link mapping method. Resolution of a multi-commodity
	 * flow problem and mapping of each virtual link to several paths in the
	 * substrate network
	 */
	@Override
	protected boolean linkMapping(VirtualNetwork vNet,
			Map<VirtualNode, SubstrateNode> nodeMapping) {
		double hhFactor = 0;
		BandwidthDemand originalBwDem = null, newBwDem;
		Map<List<String>, Double> solverResult;
		SubstrateNode srcSnode = null;
		SubstrateNode dstSnode = null;
		SubstrateNode tSNode = new SubstrateNode();
		SubstrateNode tDNode = new SubstrateNode();
		SubstrateNode hiddenHop = null;
		SubstrateLink tSLink;
		CpuDemand tmpHhDemand = null;

		processedLinks = 0;
		mappedLinks = 0;

		Random intGenerator = new Random();// FIXME Temp solution for executing
		// at the same time different algorithms using
		// this link mapping that must accede to different data files
		LpSolver problemSolver = new LpSolver();

		String dataFileName = Consts.LP_SOLVER_DATAFILE
				+ Integer.toString(intGenerator.nextInt(2001)) + ".dat";

		dataSolverFile lpLinkMappingData = new dataSolverFile(
				Consts.LP_SOLVER_FOLDER + dataFileName);

		lpLinkMappingData.createDataSolverFile(sNet, null, vNet, nodeMapping,
				wBw, wCpu, false, 0); // Process all current VirtualNetworks

		problemSolver.solve(Consts.LP_SOLVER_FOLDER,
				Consts.LP_LINKMAPPING_MODEL_HIDDENHOPS, dataFileName);

		if (problemSolver.problemFeasible()) {
			// In model HHVNE-Model.mod in /ILP-LP-Models is easy to see
			// that lambda is the variable of the multi-commodity flow problem
			// indicating if a substrate node is part of the solution to
			// map a virtual node demand.
			solverResult = MiscelFunctions.processSolverResult(
					problemSolver.getSolverResult(), "lambda[]");

			// Iterate all VirtualLinks on the current VirtualNetwork
			for (Iterator<VirtualLink> links = vNet.getEdges().iterator(); links
					.hasNext();) {
				VirtualLink tmpl = links.next();
				mappedLinks++; // increase number of processed.

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
					// Get current VirtualLink demand
					for (AbstractDemand dem : tmpl) {
						if (dem instanceof BandwidthDemand) {
							originalBwDem = (BandwidthDemand) dem;
							break;
						}
					}

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
							// get the susbstrate link (part of the mapping path
							// to dstSnode from srcSnode)
							tSLink = sNet.findEdge(tSNode, tDNode);
							// Create the new bandwidth demand that corresponds
							// to the percentage of BW in the solution of the
							// solver. tSLink is part of the path mapping tmpl

							newBwDem = new BandwidthDemand(tmpl);
							newBwDem.setDemandedBandwidth(MiscelFunctions
									.roundThreeDecimals(vtmp
											* originalBwDem
													.getDemandedBandwidth()));

							tmpl.add(newBwDem);

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
							if (!tmpl.getHiddenHopDemands().isEmpty()) {
								// Hidden hops are considered, new hidden hop
								// demand should be created
								tmpHhDemand = (CpuDemand) new BandwidthCpuHiddenHopMapping(
										hhFactor).transform(newBwDem);
								tmpl.addHiddenHopDemand(tmpHhDemand);
								if (!sNet.getSource(tSLink).equals(srcSnode)) {
									hiddenHop = sNet.getSource(tSLink);
								} else {
									hiddenHop = null;
								}
							}
							if (!NodeLinkAssignation.vlmSingleLink(tmpl,
									newBwDem, tSLink, hiddenHop, tmpHhDemand))
								throw new AssertionError(
										"But we checked before!");
						}
					}
				} else {
					// FIXME To be discussed if hidden hop demand should be
					// applied here
				}
			}
		} else {
			// There is not feasible solution of the problem
			processedLinks = vNet.getEdges().size();
			return false;
		}
		processedLinks = vNet.getEdges().size();
		return true;
	}
}
