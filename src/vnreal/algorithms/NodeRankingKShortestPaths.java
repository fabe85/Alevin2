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
package vnreal.algorithms;

/**
 * This is the class of the virtual embedding algorithm that calculates the coordinated VNE
 * based on node ranking and K-sshortest paths
 * 
 * This algorithm was proposed in:
 * 
 * X. Cheng, S. Su, Z. Zhang, H. Wang, F. Yang, Y. Luo, J. Wang, Virtual network embedding 
 * through topology-aware node ranking, SIGCOMM Comput. Commun. Rev. 41 (2011) 38--47.
 * 
 * @author Juan Felipe Botero
 * @since 2011-02-10
 */

import vnreal.algorithms.linkmapping.kShortestPathLinkMapping;
import vnreal.algorithms.nodemapping.NodeRankingBasedAlgorithm;
import vnreal.algorithms.utils.MiscelFunctions;
import vnreal.network.NetworkStack;

public class NodeRankingKShortestPaths extends GenericMappingAlgorithm {
	public NodeRankingKShortestPaths(NetworkStack stack, int k, int distance,
			boolean isDistance, boolean nodeOverload) {
		super(MiscelFunctions.sortByRevenues(stack),
				new NodeRankingBasedAlgorithm(stack.getSubstrate(),
						nodeOverload, 0.0001, isDistance, distance),
				new kShortestPathLinkMapping(stack.getSubstrate(), k));
	}
}
