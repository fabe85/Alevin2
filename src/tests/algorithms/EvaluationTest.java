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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import mulavito.algorithms.IAlgorithm;

import org.junit.Test;

import vnreal.algorithms.AvailableResources;
import vnreal.demands.BandwidthDemand;
import vnreal.demands.CpuDemand;
import vnreal.evaluations.metrics.Cost;
import vnreal.evaluations.metrics.CostRevenue;
import vnreal.evaluations.metrics.EvaluationMetric;
import vnreal.evaluations.metrics.MappedRevenue;
import vnreal.evaluations.metrics.RejectedNetworksNumber;
import vnreal.evaluations.metrics.TotalRevenue;
import vnreal.evaluations.utils.EvaluationFileGeneration;
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

public class EvaluationTest {
	@Test
	public void EvaluationTests() {
		SubstrateNetwork subsNetwork = createSubstrate();
		List<VirtualNetwork> vndemands = createVNDemands();
		NetworkStack stack = new NetworkStack(subsNetwork, vndemands);

		IAlgorithm algo1 = new AvailableResources(stack, 3, 30, false, false);

		algo1.performEvaluation(); // performEvaluation() implemented in
									// AbstractAlgorithm.java
		assertNotNull(algo1);

		// Printing the lines of evaluation parameters resulting of algorithm
		// execution with the second calculation method.
		String path = "ILP-LP-Models/testFile.cvs";
		List<EvaluationMetric> metrics = new LinkedList<EvaluationMetric>();
		metrics.add(new CostRevenue(false));
		metrics.add(new Cost());
		metrics.add(new MappedRevenue(false));
		metrics.add(new RejectedNetworksNumber());
		metrics.add(new TotalRevenue(false));
		for (Iterator<EvaluationMetric> tmpMetric = metrics.iterator(); tmpMetric
				.hasNext();) {
			EvaluationMetric currMetric = tmpMetric.next();
			currMetric.setStack(stack);
		}
		EvaluationFileGeneration outputFile = new EvaluationFileGeneration(path);
		outputFile.createEvaluationFile(metrics);
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
		cpuRes.setCycles(30000.0);
		assertTrue(subsNode1.add(cpuRes));
		assertTrue(subsNetwork.addVertex(subsNode1));

		SubstrateNode subsNode2 = new SubstrateNode();
		idRes = new IdResource(subsNode2, subsNetwork);
		idRes.setId(Integer.toString(2));
		assertTrue(subsNode2.add(idRes));
		cpuRes = new CpuResource(subsNode2);
		cpuRes.setCycles(100.0);
		assertTrue(subsNode2.add(cpuRes));
		assertTrue(subsNetwork.addVertex(subsNode2));

		SubstrateNode subsNode3 = new SubstrateNode();
		idRes = new IdResource(subsNode3, subsNetwork);
		idRes.setId(Integer.toString(3));
		assertTrue(subsNode3.add(idRes));
		cpuRes = new CpuResource(subsNode3);
		cpuRes.setCycles(100.0);
		assertTrue(subsNode3.add(cpuRes));
		assertTrue(subsNetwork.addVertex(subsNode3));

		SubstrateNode subsNode4 = new SubstrateNode();
		idRes = new IdResource(subsNode4, subsNetwork);
		idRes.setId(Integer.toString(4));
		assertTrue(subsNode4.add(idRes));
		cpuRes = new CpuResource(subsNode4);
		cpuRes.setCycles(100.0);
		assertTrue(subsNode4.add(cpuRes));
		assertTrue(subsNetwork.addVertex(subsNode4));

		SubstrateNode subsNode5 = new SubstrateNode();
		idRes = new IdResource(subsNode5, subsNetwork);
		idRes.setId(Integer.toString(5));
		assertTrue(subsNode5.add(idRes));
		cpuRes = new CpuResource(subsNode5);
		cpuRes.setCycles(100.0);
		assertTrue(subsNode5.add(cpuRes));
		assertTrue(subsNetwork.addVertex(subsNode5));

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

		SubstrateLink subsLink34 = new SubstrateLink();
		bwRes = new BandwidthResource(subsLink34);
		bwRes.setBandwidth(10.0);
		assertTrue(subsLink34.add(bwRes));
		assertTrue(subsNetwork.addEdge(subsLink34, subsNode3, subsNode4));

		SubstrateLink subsLink43 = new SubstrateLink();
		bwRes = new BandwidthResource(subsLink43);
		bwRes.setBandwidth(10.0);
		assertTrue(subsLink43.add(bwRes));
		assertTrue(subsNetwork.addEdge(subsLink43, subsNode4, subsNode3));

		SubstrateLink subsLink45 = new SubstrateLink();
		bwRes = new BandwidthResource(subsLink45);
		bwRes.setBandwidth(10.0);
		assertTrue(subsLink45.add(bwRes));
		assertTrue(subsNetwork.addEdge(subsLink45, subsNode4, subsNode5));

		SubstrateLink subsLink54 = new SubstrateLink();
		bwRes = new BandwidthResource(subsLink54);
		bwRes.setBandwidth(10.0);
		assertTrue(subsLink54.add(bwRes));
		assertTrue(subsNetwork.addEdge(subsLink54, subsNode5, subsNode4));

		SubstrateLink subsLink15 = new SubstrateLink();
		bwRes = new BandwidthResource(subsLink15);
		bwRes.setBandwidth(10.0);
		assertTrue(subsLink15.add(bwRes));
		assertTrue(subsNetwork.addEdge(subsLink15, subsNode1, subsNode5));

		SubstrateLink subsLink51 = new SubstrateLink();
		bwRes = new BandwidthResource(subsLink51);
		bwRes.setBandwidth(10.0);
		assertTrue(subsLink51.add(bwRes));
		assertTrue(subsNetwork.addEdge(subsLink51, subsNode5, subsNode1));

		SubstrateLink subsLink24 = new SubstrateLink();
		bwRes = new BandwidthResource(subsLink24);
		bwRes.setBandwidth(10.0);
		assertTrue(subsLink24.add(bwRes));
		assertTrue(subsNetwork.addEdge(subsLink24, subsNode2, subsNode4));

		SubstrateLink subsLink42 = new SubstrateLink();
		bwRes = new BandwidthResource(subsLink42);
		bwRes.setBandwidth(9.0);
		assertTrue(subsLink42.add(bwRes));
		assertTrue(subsNetwork.addEdge(subsLink42, subsNode4, subsNode2));

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
		// idDem = new IdDemand(vC);
		// idDem.setDemandedId(Integer.toString(5));
		// assertTrue(vC.add(idDem));
		cpuDem = new CpuDemand(vC);
		cpuDem.setDemandedCycles((double) 25);
		assertTrue(vC.add(cpuDem));
		assertTrue(vn1.addVertex(vC));

		VirtualNode vD = new VirtualNode(1);
		// idDem = new IdDemand(vD);
		// idDem.setDemandedId(Integer.toString(2));
		// assertTrue(vD.add(idDem));
		cpuDem = new CpuDemand(vD);
		cpuDem.setDemandedCycles((double) 25);
		assertTrue(vD.add(cpuDem));
		assertTrue(vn1.addVertex(vD));

		VirtualLink eCD = new VirtualLink(1);
		bwDem = new BandwidthDemand(eCD);
		bwDem.setDemandedBandwidth(10.0);
		// FIXME I'M HERE NOW IMPLEMENTING THE TEST!!!

		assertTrue(eCD.add(bwDem));
		assertTrue(vn1.addEdge(eCD, vC, vD));

		// Virtual network layer 2
		VirtualNetwork vn0 = new VirtualNetwork(2);
		vns.add(vn0);

		VirtualNode vA = new VirtualNode(2);
		// idDem = new IdDemand(vA);
		// idDem.setDemandedId(Integer.toString(1));
		// assertTrue(vA.add(idDem));
		cpuDem = new CpuDemand(vA);
		cpuDem.setDemandedCycles((double) 25);
		assertTrue(vA.add(cpuDem));
		assertTrue(vn0.addVertex(vA));

		VirtualNode vB = new VirtualNode(2);
		// idDem = new IdDemand(vB);
		// idDem.setDemandedId(Integer.toString(3));
		// assertTrue(vB.add(idDem));
		cpuDem = new CpuDemand(vB);
		cpuDem.setDemandedCycles((double) 50);
		assertTrue(vB.add(cpuDem));
		assertTrue(vn0.addVertex(vB));

		VirtualNode vE = new VirtualNode(2);
		// idDem = new IdDemand(vE);
		// idDem.setDemandedId(Integer.toString(4));
		// assertTrue(vE.add(idDem));
		cpuDem = new CpuDemand(vE);
		cpuDem.setDemandedCycles((double) 10);
		assertTrue(vE.add(cpuDem));
		assertTrue(vn0.addVertex(vE));

		VirtualNode vF = new VirtualNode(2);
		cpuDem = new CpuDemand(vF);
		cpuDem.setDemandedCycles((double) 10);
		assertTrue(vF.add(cpuDem));
		assertTrue(vn0.addVertex(vF));

		VirtualLink eAB = new VirtualLink(2);
		bwDem = new BandwidthDemand(eAB);
		bwDem.setDemandedBandwidth(10.0);
		assertTrue(eAB.add(bwDem));
		assertTrue(vn0.addEdge(eAB, vA, vB));

		VirtualLink eBE = new VirtualLink(2);
		bwDem = new BandwidthDemand(eBE);
		bwDem.setDemandedBandwidth(10.0);
		assertTrue(eBE.add(bwDem));
		assertTrue(vn0.addEdge(eBE, vB, vE));

		return vns;
	}
}
