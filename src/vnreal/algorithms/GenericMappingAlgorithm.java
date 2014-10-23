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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import mulavito.algorithms.AbstractAlgorithmStatus;
import vnreal.demands.AbstractDemand;
import vnreal.hiddenhopmapping.IHiddenHopMapping;
import vnreal.network.Network;
import vnreal.network.NetworkStack;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;

/**
 * This class integrates the node mapping and link mapping stages in an
 * independent manner. This Generic Mapping Algorithm sets out the virtual
 * network mapping by, in first place, performing the node mapping and then (if
 * it is successful), the link mapping.
 * 
 * Virtual network embedding algorithms of two stages should use this class to
 * realize. It permits to combine different node mapping algorithms with
 * different link mapping algorithms.
 * 
 * @author Juan Felipe Botero
 * @since 2010-12-10
 */
public abstract class GenericMappingAlgorithm extends
		AbstractSequentialAlgorithm<VirtualNetwork> {
	protected NetworkStack stack;
	protected Iterator<? extends Network<?, ?, ?>> curNetIt;
	protected Iterator<VirtualNetwork> curIt;
	protected int processedLinks, mappedLinks;
	protected AbstractNodeMapping nodeMappingAlgorithm;
	protected AbstractLinkMapping linkMappingAlgorithm;
	protected double startTime, runningTime;

	/**
	 * The constructor initializes the variable and introduce to the virtual
	 * links of every virtual network, the hidden hop demand.
	 * 
	 * @param stack
	 *            of networks VNRs and Substrate
	 * @param implemented
	 *            nodeMappingAlgorithm
	 * @param implemented
	 *            linkMappingAlgorithm
	 */
	protected GenericMappingAlgorithm(NetworkStack stack,
			AbstractNodeMapping nodeMappingAlgorithm,
			AbstractLinkMapping linkMappingAlgorithm) {
		this.stack = stack;
		this.nodeMappingAlgorithm = nodeMappingAlgorithm;
		this.linkMappingAlgorithm = linkMappingAlgorithm;
		this.curNetIt = stack.iterator();
	}

	public void setHhMappings(List<IHiddenHopMapping> hhs) {
		for (@SuppressWarnings("rawtypes")
		Network net : stack)
			if (net instanceof VirtualNetwork) {
				VirtualNetwork vNet = (VirtualNetwork) net;
				for (VirtualLink vLink : vNet.getEdges()) {
					// Clearing past hidden hops demands
					vLink.clearHiddenHopDemands();
					for (AbstractDemand dem : vLink) {
						for (IHiddenHopMapping hh : hhs)
							if (hh.accepts(dem))
								vLink.addHiddenHopDemand(hh.transform(dem));
					}
				}
			}
		// Adding the hiddenhops mapping to the link and node mapping algorithm
		linkMappingAlgorithm.setHhMappings(hhs);
		nodeMappingAlgorithm.setHhMappings(hhs);
	}

	@Override
	public List<AbstractAlgorithmStatus> getStati() {
		LinkedList<AbstractAlgorithmStatus> stati = new LinkedList<AbstractAlgorithmStatus>();

		stati.add(new AbstractAlgorithmStatus("Processed VN links") {
			private int max = -1;

			@Override
			public Integer getValue() {
				return processedLinks;
			}

			@Override
			public Integer getMaximum() {
				if (max == -1) {
					max = 0;
					for (@SuppressWarnings("rawtypes")
					Network n : stack)
						if (n instanceof VirtualNetwork)
							max += n.getEdgeCount();
				}
				return max;
			}
		});
		stati.add(new AbstractAlgorithmStatus("Mapped VN links") {
			private int max = -1;

			@Override
			public Integer getValue() {
				return mappedLinks;
			}

			@Override
			public Integer getMaximum() {
				if (max == -1) {
					max = 0;
					for (@SuppressWarnings("rawtypes")
					Network n : stack)
						if (n instanceof VirtualNetwork)
							max += n.getEdgeCount();
				}
				return max;
			}
		});

		return stati;
	}

	/**
	 * The postRun method counts the time spent by the algorithm to realize the
	 * mapping.
	 */
	@Override
	protected void postRun() {
		runningTime = (System.currentTimeMillis() - startTime) / 1000;
	}

	/**
	 * The preRun method starts the time counter of the algorithm
	 */
	@Override
	protected boolean preRun() {
		startTime = System.currentTimeMillis();
		return true;
	}

	/**
	 * getNext() method obtains the next virtual network request to be processed
	 */
	@Override
	protected VirtualNetwork getNext() {
		if (!hasNext())
			return null;
		else {
			return curIt.next();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean hasNext() {
		if (curIt == null || !curIt.hasNext()) {
			if (curNetIt.hasNext()) {
				curNetIt.next();
				curIt = (Iterator<VirtualNetwork>) curNetIt;
				return hasNext();
			} else
				return false;
		} else
			return true;
	}

	/**
	 * The process method performs the node mapping stage followed by the link
	 * mapping stage
	 */
	@Override
	protected boolean process(VirtualNetwork p) {
		// Node mapping stage
		if (nodeMappingAlgorithm.isPreNodeMappingFeasible(p)) {
			if (!nodeMappingAlgorithm.isPreNodeMappingComplete()) {
				if (!nodeMappingAlgorithm.nodeMapping(p)) {
					stack.clearVnrMappings(p);
					processedLinks += p.getEdges().size();
					return true;
				}
			}
		} else {
			stack.clearVnrMappings(p);
			processedLinks += p.getEdges().size();
			return true;
		}
		// Link Mapping stage
		if (!linkMappingAlgorithm.linkMapping(p, nodeMappingAlgorithm
				.getNodeMapping())) {
			stack.clearVnrMappings(p);
		} else {
			mappedLinks += linkMappingAlgorithm.getMappedLinks();
		}
		processedLinks += linkMappingAlgorithm.getProcessedLinks();
		return true;
	}

	public double getRunningTime() {
		return runningTime;
	}
}
