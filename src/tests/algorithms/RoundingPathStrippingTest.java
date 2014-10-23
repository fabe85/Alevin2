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
package tests.algorithms;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import vnreal.algorithms.CoordinatedMappingRoundingPathStripping;
import vnreal.demands.BandwidthDemand;
import vnreal.demands.CpuDemand;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.BandwidthResource;
import vnreal.resources.CpuResource;
import vnreal.resources.IdResource;

public class RoundingPathStrippingTest {
	@Test
	public void algorithmTest() {
		SubstrateNetwork subsNetwork = createSubstrate();
		List<VirtualNetwork> vndemands = createVNDemands();
		NetworkStack stack = new NetworkStack(subsNetwork, vndemands);

		CoordinatedMappingRoundingPathStripping algo1 = new CoordinatedMappingRoundingPathStripping(
				stack, 20, 1, 1, 1, 3, false);

		algo1.performEvaluation(); // performEvaluation() implemented in
		// AbstractAlgorithm.java
		assertNotNull(algo1);
	}

	private static SubstrateNetwork createSubstrate() {
		SubstrateNetwork subsNetwork = new SubstrateNetwork(true);
		IdResource idRes;
		CpuResource cpuRes;

		SubstrateNode subsNode1 = new SubstrateNode();
		idRes = new IdResource(subsNode1, subsNetwork);
		idRes.setId(Integer.toString(1));
		assertTrue(subsNode1.add(idRes));
		cpuRes = new CpuResource(subsNode1);
		cpuRes.setCycles(100.0);
		assertTrue(subsNode1.add(cpuRes));
		assertTrue(subsNetwork.addVertex(subsNode1));
		subsNode1.setCoordinateX(-5.0);
		subsNode1.setCoordinateY(5.0);

		SubstrateNode subsNode2 = new SubstrateNode();
		idRes = new IdResource(subsNode2, subsNetwork);
		idRes.setId(Integer.toString(2));
		assertTrue(subsNode2.add(idRes));
		cpuRes = new CpuResource(subsNode2);
		cpuRes.setCycles(100.0);
		assertTrue(subsNode2.add(cpuRes));
		assertTrue(subsNetwork.addVertex(subsNode2));
		subsNode2.setCoordinateX(5.0);
		subsNode2.setCoordinateY(5.0);

		SubstrateNode subsNode3 = new SubstrateNode();
		idRes = new IdResource(subsNode3, subsNetwork);
		idRes.setId(Integer.toString(3));
		assertTrue(subsNode3.add(idRes));
		cpuRes = new CpuResource(subsNode3);
		cpuRes.setCycles(100.0);
		assertTrue(subsNode3.add(cpuRes));
		assertTrue(subsNetwork.addVertex(subsNode3));
		subsNode3.setCoordinateX(-10.0);
		subsNode3.setCoordinateY(-5.0);

		SubstrateNode subsNode4 = new SubstrateNode();
		idRes = new IdResource(subsNode4, subsNetwork);
		idRes.setId(Integer.toString(4));
		assertTrue(subsNode4.add(idRes));
		cpuRes = new CpuResource(subsNode4);
		cpuRes.setCycles(100.0);
		assertTrue(subsNode4.add(cpuRes));
		assertTrue(subsNetwork.addVertex(subsNode4));
		subsNode4.setCoordinateX(10.0);
		subsNode4.setCoordinateY(-5.0);

		SubstrateNode subsNode5 = new SubstrateNode();
		idRes = new IdResource(subsNode5, subsNetwork);
		idRes.setId(Integer.toString(5));
		assertTrue(subsNode5.add(idRes));
		cpuRes = new CpuResource(subsNode5);
		cpuRes.setCycles(100.0);
		assertTrue(subsNode5.add(cpuRes));
		assertTrue(subsNetwork.addVertex(subsNode5));
		subsNode5.setCoordinateX(0);
		subsNode5.setCoordinateY(-5.0);

		SubstrateNode subsNode6 = new SubstrateNode();
		idRes = new IdResource(subsNode6, subsNetwork);
		idRes.setId(Integer.toString(6));
		assertTrue(subsNode6.add(idRes));
		cpuRes = new CpuResource(subsNode6);
		cpuRes.setCycles(100.0);
		assertTrue(subsNode6.add(cpuRes));
		assertTrue(subsNetwork.addVertex(subsNode6));
		subsNode6.setCoordinateX(0);
		subsNode6.setCoordinateY(-5.0);

		SubstrateNode subsNode7 = new SubstrateNode();
		idRes = new IdResource(subsNode7, subsNetwork);
		idRes.setId(Integer.toString(7));
		assertTrue(subsNode7.add(idRes));
		cpuRes = new CpuResource(subsNode7);
		cpuRes.setCycles(100.0);
		assertTrue(subsNode7.add(cpuRes));
		assertTrue(subsNetwork.addVertex(subsNode7));
		subsNode7.setCoordinateX(0);
		subsNode7.setCoordinateY(-5.0);

		SubstrateNode subsNode8 = new SubstrateNode();
		idRes = new IdResource(subsNode8, subsNetwork);
		idRes.setId(Integer.toString(8));
		assertTrue(subsNode8.add(idRes));
		cpuRes = new CpuResource(subsNode8);
		cpuRes.setCycles(100.0);
		assertTrue(subsNode8.add(cpuRes));
		assertTrue(subsNetwork.addVertex(subsNode8));
		subsNode8.setCoordinateX(0);
		subsNode8.setCoordinateY(-5.0);

		// Create links
		BandwidthResource bwRes;
		SubstrateLink subsLink12 = new SubstrateLink();
		bwRes = new BandwidthResource(subsLink12);
		bwRes.setBandwidth(10.0);
		assertTrue(subsLink12.add(bwRes));
		assertTrue(subsNetwork.addEdge(subsLink12, subsNode1, subsNode2));

		SubstrateLink subsLink21 = new SubstrateLink();
		bwRes = new BandwidthResource(subsLink21);
		bwRes.setBandwidth(9.0);
		assertTrue(subsLink21.add(bwRes));
		assertTrue(subsNetwork.addEdge(subsLink21, subsNode2, subsNode1));

		SubstrateLink subsLink24 = new SubstrateLink();
		bwRes = new BandwidthResource(subsLink24);
		bwRes.setBandwidth(10.0);
		assertTrue(subsLink24.add(bwRes));
		assertTrue(subsNetwork.addEdge(subsLink24, subsNode2, subsNode4));

		SubstrateLink subsLink42 = new SubstrateLink();
		bwRes = new BandwidthResource(subsLink42);
		bwRes.setBandwidth(10.0);
		assertTrue(subsLink42.add(bwRes));
		assertTrue(subsNetwork.addEdge(subsLink42, subsNode4, subsNode2));

		SubstrateLink subsLink25 = new SubstrateLink();
		bwRes = new BandwidthResource(subsLink25);
		bwRes.setBandwidth(10.0);
		assertTrue(subsLink25.add(bwRes));
		assertTrue(subsNetwork.addEdge(subsLink25, subsNode2, subsNode5));

		SubstrateLink subsLink52 = new SubstrateLink();
		bwRes = new BandwidthResource(subsLink52);
		bwRes.setBandwidth(10.0);
		assertTrue(subsLink52.add(bwRes));
		assertTrue(subsNetwork.addEdge(subsLink52, subsNode5, subsNode2));

		SubstrateLink subsLink13 = new SubstrateLink();
		bwRes = new BandwidthResource(subsLink13);
		bwRes.setBandwidth(10.0);
		assertTrue(subsLink13.add(bwRes));
		assertTrue(subsNetwork.addEdge(subsLink13, subsNode1, subsNode3));

		SubstrateLink subsLink31 = new SubstrateLink();
		bwRes = new BandwidthResource(subsLink31);
		bwRes.setBandwidth(10.0);
		assertTrue(subsLink31.add(bwRes));
		assertTrue(subsNetwork.addEdge(subsLink31, subsNode3, subsNode1));

		SubstrateLink subsLink36 = new SubstrateLink();
		bwRes = new BandwidthResource(subsLink36);
		bwRes.setBandwidth(10.0);
		assertTrue(subsLink36.add(bwRes));
		assertTrue(subsNetwork.addEdge(subsLink36, subsNode3, subsNode6));

		SubstrateLink subsLink63 = new SubstrateLink();
		bwRes = new BandwidthResource(subsLink63);
		bwRes.setBandwidth(10.0);
		assertTrue(subsLink63.add(bwRes));
		assertTrue(subsNetwork.addEdge(subsLink63, subsNode6, subsNode3));

		SubstrateLink subsLink37 = new SubstrateLink();
		bwRes = new BandwidthResource(subsLink37);
		bwRes.setBandwidth(10.0);
		assertTrue(subsLink37.add(bwRes));
		assertTrue(subsNetwork.addEdge(subsLink37, subsNode3, subsNode7));

		SubstrateLink subsLink73 = new SubstrateLink();
		bwRes = new BandwidthResource(subsLink73);
		bwRes.setBandwidth(10.0);
		assertTrue(subsLink73.add(bwRes));
		assertTrue(subsNetwork.addEdge(subsLink73, subsNode7, subsNode3));

		SubstrateLink subsLink78 = new SubstrateLink();
		bwRes = new BandwidthResource(subsLink78);
		bwRes.setBandwidth(10.0);
		assertTrue(subsLink78.add(bwRes));
		assertTrue(subsNetwork.addEdge(subsLink78, subsNode7, subsNode8));

		SubstrateLink subsLink87 = new SubstrateLink();
		bwRes = new BandwidthResource(subsLink87);
		bwRes.setBandwidth(10.0);
		assertTrue(subsLink87.add(bwRes));
		assertTrue(subsNetwork.addEdge(subsLink87, subsNode8, subsNode7));

		return subsNetwork;
	}

	private static List<VirtualNetwork> createVNDemands() {
		List<VirtualNetwork> vns = new LinkedList<VirtualNetwork>();
		// IdDemand idDem;
		CpuDemand cpuDem;
		BandwidthDemand bwDem;

		// Network with Layer (x) MUST be instance before Network Layer (x+1)

		// Virtual network: Layer 1
		VirtualNetwork vn1 = new VirtualNetwork(1);
		vns.add(vn1);

		VirtualNode vC = new VirtualNode(1);
		/*
		 * idDem = new IdDemand(vC); idDem.setDemandedId(Integer.toString(5));
		 * assertTrue(vC.add(idDem));
		 */
		cpuDem = new CpuDemand(vC);
		cpuDem.setDemandedCycles((double) 25);
		assertTrue(vC.add(cpuDem));
		assertTrue(vn1.addVertex(vC));
		vC.setCoordinateX(2);
		vC.setCoordinateY(5.0);

		VirtualNode vD = new VirtualNode(1);
		/*
		 * idDem = new IdDemand(vD); idDem.setDemandedId(Integer.toString(2));
		 * assertTrue(vD.add(idDem));
		 */
		cpuDem = new CpuDemand(vD);
		cpuDem.setDemandedCycles((double) 25);
		assertTrue(vD.add(cpuDem));
		assertTrue(vn1.addVertex(vD));
		vD.setCoordinateX(-2);
		vD.setCoordinateY(5.0);

		VirtualLink eCD = new VirtualLink(1);
		bwDem = new BandwidthDemand(eCD);
		bwDem.setDemandedBandwidth((double) 10);

		assertTrue(eCD.add(bwDem));
		assertTrue(vn1.addEdge(eCD, vC, vD));

		// Virtual network layer 2
		VirtualNetwork vn0 = new VirtualNetwork(2);
		vns.add(vn0);

		VirtualNode vA = new VirtualNode(2);
		/*
		 * idDem = new IdDemand(vA); idDem.setDemandedId(Integer.toString(1));
		 * assertTrue(vA.add(idDem));
		 */
		cpuDem = new CpuDemand(vA);
		cpuDem.setDemandedCycles((double) 25);
		assertTrue(vA.add(cpuDem));
		assertTrue(vn0.addVertex(vA));
		vA.setCoordinateX(-2);
		vA.setCoordinateY(-5.0);

		VirtualNode vB = new VirtualNode(2);
		/*
		 * idDem = new IdDemand(vB); idDem.setDemandedId(Integer.toString(3));
		 * assertTrue(vB.add(idDem));
		 */
		cpuDem = new CpuDemand(vB);
		cpuDem.setDemandedCycles((double) 50);
		assertTrue(vB.add(cpuDem));
		assertTrue(vn0.addVertex(vB));
		vB.setCoordinateX(2);
		vB.setCoordinateY(-5.0);

		VirtualNode vE = new VirtualNode(2);
		/*
		 * idDem = new IdDemand(vE); idDem.setDemandedId(Integer.toString(4));
		 * assertTrue(vE.add(idDem));
		 */
		cpuDem = new CpuDemand(vE);
		cpuDem.setDemandedCycles((double) 10);
		assertTrue(vE.add(cpuDem));
		assertTrue(vn0.addVertex(vE));
		vE.setCoordinateX(0);
		vE.setCoordinateY(0);

		VirtualLink eAB = new VirtualLink(2);
		bwDem = new BandwidthDemand(eAB);
		bwDem.setDemandedBandwidth((double) 10);
		assertTrue(eAB.add(bwDem));
		assertTrue(vn0.addEdge(eAB, vA, vB));

		VirtualLink eBE = new VirtualLink(2);
		bwDem = new BandwidthDemand(eBE);
		bwDem.setDemandedBandwidth((double) 10);
		assertTrue(eBE.add(bwDem));
		assertTrue(vn0.addEdge(eBE, vB, vE));

		return vns;
	}
}
