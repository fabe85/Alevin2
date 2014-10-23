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


/**
 * This is the class of the virtual embedding algorithm that calculates the exact energy-aware 
 * virtual network embedding in the substrate network, including hidden hops demands.
 * 
 * 
 * This algorithm was proposed in:
 *  
 *  @ARTICLE{6168871, 
 *  author={Botero, J.F. and Hesselbach, X. and Duelli, M. and Schlosser, D. and Fischer, A. and de Meer, H.}, 
 *  journal={Communications Letters, IEEE}, 
 *  title={Energy Efficient Virtual Network Embedding}, 
 *  year={2012}, 
 *  month={may }, 
 *  volume={16}, 
 *  number={5}, 
 *  pages={756 -759}, 
 *  keywords={Bandwidth;Energy consumption;Europe;Green products;Substrates;Switches;Tin;Internet;energy consumption;integer programming;resource allocation;ISP;Internet service providers;energy consumption reduction;energy efficient virtual network embedding;energy saving;energy waste;mixed integer program;network infrastructures;network virtualization based architectures;resource consolidation;virtual network embedding problem;Network virtualization;energy efficiency;green networking;mixed integer programming;virtual network embedding;}, 
 *  doi={10.1109/LCOMM.2012.030912.120082}, 
 *  ISSN={1089-7798},}
 }

 * 
 * @author Juan Felipe Botero
 * @since 2011-01-10
 */


package vnreal.algorithms;

import vnreal.algorithms.linkmapping.FoolLinkMapping;
import vnreal.algorithms.nodemapping.CompleteNodeLinkMappingHiddenHop;
import vnreal.algorithms.utils.MiscelFunctions;
import vnreal.network.NetworkStack;

public class ExactMipAlgorithmWithHiddenHops extends GenericMappingAlgorithm {

	public ExactMipAlgorithmWithHiddenHops(NetworkStack stack, double wCpu,
			double wBw, int dist, boolean withDist, boolean nodeOverload) {
		super(MiscelFunctions.sortByRevenues(stack), new CompleteNodeLinkMappingHiddenHop(stack.getSubstrate(),
				dist, wCpu, wBw, nodeOverload, withDist, stack),
				new FoolLinkMapping(stack.getSubstrate()));

	}

}
