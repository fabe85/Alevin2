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
package tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import vnreal.demands.BandwidthDemand;
import vnreal.demands.CpuDemand;
import vnreal.demands.IdDemand;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.BandwidthResource;
import vnreal.resources.CpuResource;
import vnreal.resources.IdResource;

/**
 * @author Michael Duelli
 * @author Vlad Singeorzan
 * @since 2010-09-03
 */
public final class BasicNetworkTest {
	@Test
	public void substrateNetwork() {
		
		SubstrateNetwork subsNetwork = new SubstrateNetwork(false);
		
		// Substrate network
		BandwidthResource bwRes;
		CpuResource cpuRes;
		IdResource idRes;

		SubstrateLink subsLink = new SubstrateLink();
		bwRes = new BandwidthResource(subsLink);
		bwRes.setBandwidth(4.2);
		assertTrue(subsLink.add(bwRes));
		bwRes = new BandwidthResource(new SubstrateLink());
		bwRes.setBandwidth(4.2);
		assertFalse(subsLink.add(bwRes));
		cpuRes = new CpuResource(new SubstrateNode());
		cpuRes.setCycles(3.0);
		assertFalse(subsLink.add(cpuRes));
		idRes = new IdResource(new SubstrateNode(), subsNetwork);
		idRes.setId(Integer.toString(1));
		assertFalse(subsLink.add(idRes));

		SubstrateNode subsNodeA = new SubstrateNode();
		bwRes = new BandwidthResource(new SubstrateLink());
		bwRes.setBandwidth(4.2);
		assertFalse(subsNodeA.add(bwRes));
		cpuRes = new CpuResource(subsNodeA);
		cpuRes.setCycles(3.0);
		assertTrue(subsNodeA.add(cpuRes));
		cpuRes = new CpuResource(new SubstrateNode());
		cpuRes.setCycles(3.0);
		assertFalse(subsNodeA.add(cpuRes));
		idRes = new IdResource(subsNodeA, subsNetwork);
		idRes.setId(Integer.toString(6));
		assertTrue(subsNodeA.add(idRes));
		idRes = new IdResource(new SubstrateNode(), subsNetwork);
		idRes.setId(Integer.toString(6));
		assertFalse(subsNodeA.add(idRes));

		SubstrateNode subsNodeB = new SubstrateNode();
		bwRes = new BandwidthResource(new SubstrateLink());
		bwRes.setBandwidth(4.2);
		assertFalse(subsNodeB.add(bwRes));
		cpuRes = new CpuResource(subsNodeB);
		cpuRes.setCycles(3.0);
		assertTrue(subsNodeB.add(cpuRes));
		assertFalse(subsNodeB.add(bwRes));
		cpuRes = new CpuResource(subsNodeA);
		cpuRes.setCycles(3.0);
		assertFalse(subsNodeB.add(cpuRes));
		idRes = new IdResource(subsNodeB, subsNetwork);
		idRes.setId(Integer.toString(6));
		assertTrue(subsNodeB.add(idRes));
		idRes = new IdResource(subsNodeA, subsNetwork);
		idRes.setId(Integer.toString(6));
		assertFalse(subsNodeB.add(idRes));

		assertTrue(subsNetwork.addVertex(subsNodeA));
		assertTrue(subsNetwork.addVertex(subsNodeB));
		assertTrue(subsNetwork.addEdge(subsLink, subsNodeA, subsNodeB));

		// The Java generics ensure that you cannot add a virtual link to the
		// substrate network.
	}

	@Test
	public void virtualNetwork() {
		// Virtual network.
		BandwidthDemand bwDem;
		CpuDemand cpuDem;
		IdDemand idDem;

		VirtualLink virtLink = new VirtualLink(1);
		bwDem = new BandwidthDemand(virtLink);
		bwDem.setDemandedBandwidth(3.1);
		assertTrue(virtLink.add(bwDem));
		cpuDem = new CpuDemand(new VirtualNode(1));
		cpuDem.setDemandedCycles(2.3);
		assertFalse(virtLink.add(cpuDem));
		idDem = new IdDemand(new VirtualNode(1));
		idDem.setDemandedId(Integer.toString(6));
		assertFalse(virtLink.add(idDem));

		VirtualNode virtNodeA = new VirtualNode(1);
		bwDem = new BandwidthDemand(new VirtualLink(1));
		bwDem.setDemandedBandwidth(3.1);
		assertFalse(virtNodeA.add(bwDem));
		cpuDem = new CpuDemand(virtNodeA);
		cpuDem.setDemandedCycles(2.2);
		assertTrue(virtNodeA.add(cpuDem));
		cpuDem = new CpuDemand(new VirtualNode(1));
		cpuDem.setDemandedCycles(2.2);
		assertFalse(virtNodeA.add(cpuDem));
		idDem = new IdDemand(virtNodeA);
		idDem.setDemandedId(Integer.toString(6));
		assertTrue(virtNodeA.add(idDem));
		idDem = new IdDemand(new VirtualNode(1));
		idDem.setDemandedId(Integer.toString(6));
		assertFalse(virtNodeA.add(idDem));

		VirtualNode virtNodeB = new VirtualNode(1);
		bwDem = new BandwidthDemand(new VirtualLink(1));
		bwDem.setDemandedBandwidth(3.2);
		assertFalse(virtNodeB.add(bwDem));
		cpuDem = new CpuDemand(virtNodeB);
		cpuDem.setDemandedCycles(2.2);
		assertTrue(virtNodeB.add(cpuDem));
		cpuDem = new CpuDemand(virtNodeA);
		cpuDem.setDemandedCycles(2.2);
		assertFalse(virtNodeB.add(cpuDem));
		idDem = new IdDemand(virtNodeB);
		idDem.setDemandedId(Integer.toString(6));
		assertTrue(virtNodeB.add(idDem));
		idDem = new IdDemand(virtNodeA);
		idDem.setDemandedId(Integer.toString(6));
		assertFalse(virtNodeB.add(idDem));

		VirtualNetwork virtNetwork = new VirtualNetwork(1);
		assertTrue(virtNetwork.addVertex(virtNodeA));
		assertTrue(virtNetwork.addVertex(virtNodeB));
		assertTrue(virtNetwork.addEdge(virtLink, virtNodeA, virtNodeB));

		// Check layers.
		assertFalse(virtNetwork.addVertex(new VirtualNode(2)));
		assertFalse(virtNetwork.addEdge(new VirtualLink(2), virtNodeA,
				virtNodeB));
	}
}
