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

import java.util.List;
import java.util.Map;

import vnreal.hiddenhopmapping.IHiddenHopMapping;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

/**
 * 
 * Basic class for link mapping algorithms.
 * 
 * 
 * 
 * @author Juan Felipe Botero
 * @since 2010-12-20
 * 
 */
public abstract class AbstractLinkMapping {
	protected SubstrateNetwork sNet;
	protected int processedLinks, mappedLinks;
	protected List<IHiddenHopMapping> hhMappings;

	/**
	 * The constructor of the class initializes its variables
	 * 
	 * @param sNet
	 *            is the substrate network over which, the mapping will be
	 *            performed.
	 * 
	 *            proccesedLinks and mappedLinks are variables that should be
	 *            updated when a link is processed and when it is mapped in the
	 *            algorithm.
	 */
	protected AbstractLinkMapping(SubstrateNetwork sNet) {
		this.sNet = sNet;
		this.processedLinks = 0;
		this.mappedLinks = 0;
	}

	/**
	 * This method should be implemented by the link mapping algorithms.
	 * 
	 * @param vNet
	 *            Virtual network Request to map
	 * @param nodeMapping
	 *            Output of node mapping algorithm
	 * @return boolean indicating whether the link mapping was successful
	 */
	protected abstract boolean linkMapping(VirtualNetwork vNet,
			Map<VirtualNode, SubstrateNode> nodeMapping);

	public int getProcessedLinks() {
		return processedLinks;
	}

	public int getMappedLinks() {
		return mappedLinks;
	}

	/**
	 * Stores the hidden hops mappings in Virtual Links
	 * 
	 * @param hhMappings
	 */
	protected void setHhMappings(List<IHiddenHopMapping> hhMappings) {
		this.hhMappings = hhMappings;
	}
}
