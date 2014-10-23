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
package tests.algorithms.energy;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import vnreal.algorithms.isomorphism.SubgraphIsomorphismAlgorithm;
import vnreal.algorithms.isomorphism.SubgraphIsomorphismStackAlgorithm;
import vnreal.demands.BandwidthDemand;
import vnreal.demands.CpuDemand;
import vnreal.demands.PowerDemand;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.BandwidthResource;
import vnreal.resources.CpuResource;
import vnreal.resources.PowerResource;

public class SubgraphIsomorphismAlgorithmTest {

	@Test
	public void algorithmTest() {

		SubstrateNetwork sNetwork = createSubstrate();
		List<VirtualNetwork> vNetworks = createVNDemands();
		NetworkStack stack = new NetworkStack(sNetwork, vNetworks);

		SubgraphIsomorphismStackAlgorithm algorithm = new SubgraphIsomorphismStackAlgorithm(
				stack, new SubgraphIsomorphismAlgorithm(true));
		algorithm.performEvaluation();

		System.out.println(stack);

		// Mapping m = algorithm.getComputedMapping();

		// if (m == null) {
		// System.out.println("mapping not found.");
		// } else {
		// System.out.println("mapping " + m.hashCode() + ": =====\n" +
		// m.toString());
		// System.out.println();
		// System.out.println();
		// for (SubstrateNode sn : stack.getSubstrate().getVertices()) {
		// for (AbstractResource r : sn.get()) {
		// System.out.println(r);
		// }
		// }
		//
		//
		// EvaluationMetric metric = new CountRunningNodes();
		// metric.setStack(stack);
		// System.out.println(metric.getValue());
		// EvaluationMetric metric2 = new CountHiddenHops();
		// metric2.setStack(stack);
		// System.out.println(metric2.getValue());
		// }

	}

	private static SubstrateNetwork createSubstrate() {
		SubstrateNetwork subsNetwork = new SubstrateNetwork(false);
		CpuResource cpuRes;

		SubstrateNode subsNode1 = new SubstrateNode();
		cpuRes = new CpuResource(subsNode1);
		cpuRes.setCycles(1000.0);
		assertTrue(subsNode1.add(cpuRes));
		subsNode1.add(new PowerResource(subsNode1));
		assertTrue(subsNetwork.addVertex(subsNode1));

		SubstrateNode subsNode2 = new SubstrateNode();
		cpuRes = new CpuResource(subsNode2);
		cpuRes.setCycles(100.0);
		assertTrue(subsNode2.add(cpuRes));
		subsNode2.add(new PowerResource(subsNode2));
		assertTrue(subsNetwork.addVertex(subsNode2));

		SubstrateNode subsNode3 = new SubstrateNode();
		cpuRes = new CpuResource(subsNode3);
		cpuRes.setCycles(20.0);
		assertTrue(subsNode3.add(cpuRes));
		subsNode3.add(new PowerResource(subsNode3));
		assertTrue(subsNetwork.addVertex(subsNode3));

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

		SubstrateLink subsLink23 = new SubstrateLink();
		bwRes = new BandwidthResource(subsLink23);
		bwRes.setBandwidth(10.0);
		assertTrue(subsLink23.add(bwRes));
		assertTrue(subsNetwork.addEdge(subsLink23, subsNode2, subsNode3));

		SubstrateLink subsLink32 = new SubstrateLink();
		bwRes = new BandwidthResource(subsLink32);
		bwRes.setBandwidth(10.0);
		assertTrue(subsLink32.add(bwRes));
		assertTrue(subsNetwork.addEdge(subsLink32, subsNode3, subsNode2));

		return subsNetwork;
	}

	private static List<VirtualNetwork> createVNDemands() {
		List<VirtualNetwork> vns = new LinkedList<VirtualNetwork>();
		CpuDemand cpuDem;
		BandwidthDemand bwDem;

		// Network with Layer (x) MUST be instance before Network Layer (x+1)

		// Virtual network: Layer 1
		VirtualNetwork vn1 = new VirtualNetwork(1);
		vns.add(vn1);

		VirtualNode vC = new VirtualNode(1);
		cpuDem = new CpuDemand(vC);
		cpuDem.setDemandedCycles(990.0);
		vC.add(new PowerDemand(vC));
		assertTrue(vC.add(cpuDem));
		assertTrue(vn1.addVertex(vC));

		VirtualNode vD = new VirtualNode(1);
		cpuDem = new CpuDemand(vD);
		cpuDem.setDemandedCycles(25.0);
		vD.add(new PowerDemand(vD));
		assertTrue(vD.add(cpuDem));
		assertTrue(vn1.addVertex(vD));

		VirtualLink eCD = new VirtualLink(1);
		bwDem = new BandwidthDemand(eCD);
		bwDem.setDemandedBandwidth(10.0);
		assertTrue(eCD.add(bwDem));
		assertTrue(vn1.addEdge(eCD, vC, vD));

		return vns;
	}
}
