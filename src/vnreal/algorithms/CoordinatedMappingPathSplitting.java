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
 * This is the class of the virtual embedding algorithm that mixes 
 * the coordinated node mapping in the node mapping stage and path splitting
 * in link mapping
 * 
 * 
 * This node mapping algorithm was proposed in:
 *  
 *  - N. M. M. K. Chowdhury, Muntasir Raihnan Rahman, and Raouf Boutaba. Virtual
 * network embedding with coordinated node and link mapping. In Proc. IEEE
 * INFOCOM. IEEE Infocom, April 2009
 * 
 * @author Juan Felipe Botero
 * @author Lisset Diaz
 * @since 2011-01-10
 */

import vnreal.algorithms.linkmapping.PathSplittingVirtualLinkMapping;
import vnreal.algorithms.nodemapping.CoordinatedVirtualNodeMapping;
import vnreal.algorithms.utils.MiscelFunctions;
import vnreal.network.NetworkStack;

public class CoordinatedMappingPathSplitting extends GenericMappingAlgorithm {
	public CoordinatedMappingPathSplitting(NetworkStack stack, int dist,
			double wCpu, double wBw, int type, boolean nodeOverload) {
		super(MiscelFunctions.sortByRevenues(stack),
				new CoordinatedVirtualNodeMapping(stack.getSubstrate(), dist,
						wCpu, wBw, type, nodeOverload),
				new PathSplittingVirtualLinkMapping(stack.getSubstrate(), wCpu,
						wBw));
	}

}
