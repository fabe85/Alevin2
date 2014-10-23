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
package tests.scenarios;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import vnreal.demands.AbstractDemand;
import vnreal.demands.BandwidthDemand;
import vnreal.demands.CpuDemand;
import vnreal.demands.VirtualDemandedSecurity;
import vnreal.demands.VirtualProvidedSecurity;
import vnreal.evaluations.metrics.AcceptedVnrRatio;
import vnreal.evaluations.metrics.AvSecSpreadDemProv;
import vnreal.evaluations.metrics.AvSecSpreadProvDem;
import vnreal.evaluations.metrics.CostRevenue;
import vnreal.evaluations.metrics.EvaluationMetric;
import vnreal.evaluations.metrics.MaxSecSpreadDemProv;
import vnreal.evaluations.metrics.MaxSecSpreadProvDem;
import mulavito.graph.generators.WaxmanGraphGenerator;
import vnreal.network.Network;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;
import vnreal.resources.BandwidthResource;
import vnreal.resources.CpuResource;
import vnreal.resources.PhysicalDemandedSecurity;
import vnreal.resources.PhysicalProvidedSecurity;

/**
 * Help-Class for a scenario considering Security-Extensions. (copied from
 * another similar class) /JW
 * 
 * @author Michael Duelli
 * @author Daniel Schlosser
 * @author Vlad Singeorzan
 * @author Michael Till Beck
 * @author Julian Willerding
 */
@RunWith(Parameterized.class)
public abstract class SecurityScenarioTest {
	/** A flag which controls automated parallelization based on file checking */
	protected boolean allowParallelRuns = false;

	private final static int numScenarios = 20; // old: 10

	/** # of runs with different random seeds if algorithm is non-deterministic */
	protected int numRunsPerScenario = 1;

	private static final int[] numSNodesArray = { 200 }; // old: 100

	private static final int[] numVNetsArray = { 5 };

	protected static final int[] numVNodesPerVNetArray = { 5, 10, 15, 20 }; // old:
																			// 5,10,15,30,50

	private static final double[] alphaArray = { 0.5 };
	private static final double[] betaArray = { 0.5 };

	public static final int myminCPUResource = 100;
	public static final int mymaxCPUResource = 100;

	public static final int myminCPUDemand = 10;
	public static final int mymaxCPUDemand = 10;

	public static final int[] myBandwithResourceValues = { 100, 1000 };

	public static final int myminBandwidthDemand = 10;
	public static final int mymaxBandwidthDemand = 10;

	public static final int myminPhysicalDemandedSecurity = 0;
	public static final int mymaxPhysicalDemandedSecurity = 5;

	public static final int myminVirtualProvidedSecurity = 0;
	public static final int mymaxVirtualProvidedSecurity = 5;

	public static final int myminPhysicalProvidedSecurity = 0;
	public static final int mymaxPhysicalProvidedSecurity = 5;

	public static final int myminVirtualDemandedSecurity = 0;
	public static final int mymaxVirtualDemandedSecurity = 5;

	public NetworkStack stack = null;

	protected static class ScenarioData {
		public final int numSNodes;
		public final int numVNets;
		public final int numVNodesPerVNet;
		public final double alpha;
		public final double beta;

		ScenarioData(int numSNodes, int numVNets, int numVNodesPerVNet,
				double alpha, double beta) {
			this.numSNodes = numSNodes;
			this.numVNets = numVNets;
			this.numVNodesPerVNet = numVNodesPerVNet;
			this.alpha = alpha;
			this.beta = beta;
		}
	}

	@Parameters
	public static Collection<ScenarioData[]> data() {
		List<ScenarioData[]> data = new LinkedList<ScenarioData[]>();

		// Generate scenarios
		for (double alpha : alphaArray)
			for (double beta : betaArray)
				for (int numVNodesPerVNet : numVNodesPerVNetArray)
					for (int numVNets : numVNetsArray)
						for (int numSNodes : numSNodesArray)

							data.add(new ScenarioData[] { new ScenarioData(
									numSNodes, numVNets, numVNodesPerVNet,
									alpha, beta) });

		return data;
	}

	protected String scenario_suffix;

	@Test
	public void runScenario() {
		// Generate scenario
		// FIXME UniformStream.setSeed(0);
		for (int i = 0; i < numScenarios; i++) {
			// Create new empty network stack.

			final String suffix = data.numSNodes + "_" + data.numVNets + "_"
					+ data.numVNodesPerVNet + "_" + i;

			generate(data.numSNodes, data.numVNets, data.numVNodesPerVNet,
					data.alpha, data.beta);

			// Override abstract method to run algorithm.
			for (int j = 0; j < numRunsPerScenario; j++) {
				scenario_suffix = suffix + "_" + j;
				System.out.println("Run " + scenario_suffix);

				System.out
						.println("############### FIRST #####################");
				// Reset previous mappings
				stack.clearMappings();

				long startTime = System.currentTimeMillis();
				runAlgorithm(); // abstract method
				long elapsedTime = System.currentTimeMillis() - startTime;

				evaluate(scenario_suffix, elapsedTime, '0');

				// Second run with same scenario, but with SecurityConstraints
				System.out
						.println("############### SECOND #####################");

				stack.clearMappings();
				addSecurityConstraints('D');
				long startTime2 = System.currentTimeMillis();
				runAlgorithm(); // abstract method
				long elapsedTime2 = System.currentTimeMillis() - startTime2;

				evaluate(scenario_suffix, elapsedTime2, 'D');

				// Second run with same scenario, but with SecurityConstraints
				System.out
						.println("############### THIRD #####################");

				stack.clearMappings();
				removeSecurityConstraints();
				addSecurityConstraints('P');
				long startTime3 = System.currentTimeMillis();
				runAlgorithm(); // abstract method
				long elapsedTime3 = System.currentTimeMillis() - startTime3;

				evaluate(scenario_suffix, elapsedTime3, 'P');
			}
		}
	}

	protected void evaluate(String scenario_suffix, long elapsedTime,
			char security) {
		// default, does nothing
	}

	protected String getName() {
		return getClass().getSimpleName();
	}

	// N.B. Made abstract to be able to parallelize algorithms.
	protected abstract void runAlgorithm();

	protected final ScenarioData data;

	protected SecurityScenarioTest(ScenarioData data) {
		this.data = data;
	}

	/**
	 * A method to generate a scenario with the specified parameters
	 * 
	 * @param rho
	 *            The load of the substrate. 0 <= rho <= 1
	 * @param numSNodes
	 *            The number of substrate nodes.
	 * @param numVNets
	 *            The number of virtual networks.
	 * @param numVNodesPerVNet
	 *            The number of nodes per virtual network.
	 * @param maxCPUres
	 *            The maximum value for the CPU resources.
	 * @param maxBWres
	 *            The maximum value for the BW resources.
	 * @param alpha
	 *            Alpha parameter for the {@link WaxmanGraphGenerator}. alpha >
	 *            0
	 * @param beta
	 *            Beta parameter for the {@link WaxmanGraphGenerator}. beta <= 1
	 * @param suffix
	 */
	private void generate(int numSNodes, int numVNets, int numVNodesPerVNet,
			double alpha, double beta) {

		WaxmanGraphGenerator<SubstrateNode, SubstrateLink> sGenerator = new WaxmanGraphGenerator<SubstrateNode, SubstrateLink>(
				alpha, beta, false);
		WaxmanGraphGenerator<VirtualNode, VirtualLink> vGenerator = new WaxmanGraphGenerator<VirtualNode, VirtualLink>(
				alpha, beta, false);

		SubstrateNetwork sNetwork = new SubstrateNetwork(false);
		for (int i = 0; i < numSNodes; ++i) {
			SubstrateNode sn = new SubstrateNode();
			sNetwork.addVertex(sn);
		}
		sGenerator.generate(sNetwork);

		List<VirtualNetwork> vNetworks = new LinkedList<VirtualNetwork>();
		int layer = 1;
		for (int i = 0; i < numVNets; ++i) {
			VirtualNetwork vNetwork = new VirtualNetwork(layer);
			for (int n = 0; n < numVNodesPerVNet; ++n) {
				VirtualNode vn = new VirtualNode(layer);

				vNetwork.addVertex(vn);
			}
			vGenerator.generate(vNetwork);
			vNetworks.add(vNetwork);
		}

		NetworkStack stack = new NetworkStack(sNetwork, vNetworks);

		generateCPUResourcesAndDemands(stack, myminCPUResource,
				mymaxCPUResource, myminCPUDemand, mymaxCPUDemand);
		generateBandwidthResourceAndDemands(stack, myBandwithResourceValues,
				myminBandwidthDemand, mymaxBandwidthDemand);

	}

	private void addSecurityConstraints(char security) {

		if (security == 'D') {
			generatePhDem_VirtProvSecurityResourceAndDemands(stack,
					myminPhysicalDemandedSecurity,
					mymaxPhysicalDemandedSecurity,
					myminVirtualProvidedSecurity, mymaxVirtualProvidedSecurity);

		} else if (security == 'P') {
			generatePhProv_VirtDemSecurityResourceAndDemands(stack,
					myminPhysicalProvidedSecurity,
					mymaxPhysicalProvidedSecurity,
					myminVirtualDemandedSecurity, mymaxVirtualDemandedSecurity);
		}
	}

	private void removeSecurityConstraints() {
		removePhDem_VirtProvSecurityResourceAndDemands(stack);
	}

	public abstract void generateCPUResourcesAndDemands(NetworkStack stack,
			int minResourceCPU, int maxResourceCPU, int minDemandCPU,
			int maxDemandCPU);

	public static void generateRandomCPUResourcesAndDemands(NetworkStack stack,
			int minResourceCPU, int maxResourceCPU, int minDemandCPU,
			int maxDemandCPU) {
		Random random = new Random();

		for (SubstrateNode n : stack.getSubstrate().getVertices()) {
			CpuResource cpu = new CpuResource(n);
			int value = (int) (minResourceCPU + (maxResourceCPU
					- minResourceCPU + 1)
					* random.nextDouble());
			cpu.setCycles((double) value);
			n.add(cpu);
		}

		boolean substrate = true;
		for (Network<?, ?, ?> aNetwork : stack) {
			if (substrate) {
				substrate = false;
				continue;
			}

			VirtualNetwork vNetwork = (VirtualNetwork) aNetwork;
			for (VirtualNode n : vNetwork.getVertices()) {
				CpuDemand cpu = new CpuDemand(n);
				int value = (int) (minDemandCPU + (maxDemandCPU - minDemandCPU + 1)
						* random.nextDouble());
				cpu.setDemandedCycles((double) value);
				n.add(cpu);
			}
		}
	}

	public abstract void generateBandwidthResourceAndDemands(
			NetworkStack stack, int[] myBandwithResourceValues,
			int minDemandBandwidth, int maxDemandBandwidth);

	public static void generateRandomBandwidthResourceAndDemands(
			NetworkStack stack, int[] myBandwithResourceValues,
			int minDemandBandwidth, int maxDemandBandwidth) {
		Random random = new Random();

		for (SubstrateLink l : stack.getSubstrate().getEdges()) {
			BandwidthResource bw = new BandwidthResource(l);

			int position = (int) (Math.random() * (myBandwithResourceValues.length));
			int value = myBandwithResourceValues[position];

			bw.setBandwidth((double) value);
			l.add(bw);
		}

		boolean substrate = true;
		for (Network<?, ?, ?> aNetwork : stack) {
			if (substrate) {
				substrate = false;
				continue;
			}

			VirtualNetwork vNetwork = (VirtualNetwork) aNetwork;
			for (VirtualLink l : vNetwork.getEdges()) {
				BandwidthDemand bw = new BandwidthDemand(l);
				int value = (int) (minDemandBandwidth + (maxDemandBandwidth
						- minDemandBandwidth + 1)
						* random.nextDouble());
				bw.setDemandedBandwidth((double) value);
				l.add(bw);
			}
		}
	}

	// *******************************************************
	// *******************************************************
	// *******************************************************

	public abstract void generatePhDem_VirtProvSecurityResourceAndDemands(
			NetworkStack stack, int minPhysicalDemandedSecurity,
			int maxPhysicalDemandedSecurity, int minVirtualProvidedSecurity,
			int maxVirtualProvidedSecurity);

	public static void generateRandomPhDem_VirtProvSecurityResourceAndDemands(
			NetworkStack stack, int minPhysicalDemandedSecurity,
			int maxPhysicalDemandedSecurity, int minVirtualProvidedSecurity,
			int maxVirtualProvidedSecurity) {
		Random random = new Random();

		// Verkn�pfung von Nodes *******************************
		for (SubstrateNode node : stack.getSubstrate().getVertices()) {
			PhysicalDemandedSecurity msr = new PhysicalDemandedSecurity(node);
			int value = (int) (minPhysicalDemandedSecurity + (maxPhysicalDemandedSecurity
					- minPhysicalDemandedSecurity + 1)
					* random.nextDouble());
			msr.setPhysicalDemandedSecurityLevel((double) value);
			node.add(msr);
		}

		boolean substrateNode = true;
		for (Network<?, ?, ?> aNetwork : stack) {
			if (substrateNode) {
				substrateNode = false;
				continue;
			}

			VirtualNetwork vNetwork = (VirtualNetwork) aNetwork;
			for (VirtualNode vNode : vNetwork.getVertices()) {
				VirtualProvidedSecurity psd = new VirtualProvidedSecurity(vNode);
				int value = (int) (minVirtualProvidedSecurity + (maxVirtualProvidedSecurity
						- minVirtualProvidedSecurity + 1)
						* random.nextDouble());
				psd.setVirtualProvidedSecurityLevel((double) value);
				vNode.add(psd);
			}
		}

		// Verkn�pfung von Links *******************************
		for (SubstrateLink link : stack.getSubstrate().getEdges()) {
			PhysicalDemandedSecurity msr = new PhysicalDemandedSecurity(link);
			int value = (int) (minPhysicalDemandedSecurity + (maxPhysicalDemandedSecurity
					- minPhysicalDemandedSecurity + 1)
					* random.nextDouble());
			msr.setPhysicalDemandedSecurityLevel((double) value);
			link.add(msr);
		}

		boolean substrate = true;
		for (Network<?, ?, ?> aNetwork : stack) {
			if (substrate) {
				substrate = false;
				continue;
			}

			VirtualNetwork vNetwork = (VirtualNetwork) aNetwork;
			for (VirtualLink vLink : vNetwork.getEdges()) {
				VirtualProvidedSecurity psd = new VirtualProvidedSecurity(vLink);
				int value = (int) (minVirtualProvidedSecurity + (maxVirtualProvidedSecurity
						- minVirtualProvidedSecurity + 1)
						* random.nextDouble());
				psd.setVirtualProvidedSecurityLevel((double) value);
				vLink.add(psd);
			}
		}
	}

	// *******************************************************
	// *******************************************************
	// *******************************************************

	public abstract void removePhDem_VirtProvSecurityResourceAndDemands(
			NetworkStack stack);

	public static void removeRandomPhDem_VirtProvSecurityResourceAndDemands(
			NetworkStack stack) {

		// Verkn�pfung von Nodes *******************************
		for (SubstrateNode node : stack.getSubstrate().getVertices()) {
			for (AbstractResource nodeConstraint : node.get()) {
				if (nodeConstraint instanceof PhysicalDemandedSecurity) {
					node.remove(nodeConstraint);
					break;
				}
			}
		}

		boolean substrateNode = true;
		for (Network<?, ?, ?> aNetwork : stack) {
			if (substrateNode) {
				substrateNode = false;
				continue;
			}

			VirtualNetwork vNetwork = (VirtualNetwork) aNetwork;
			for (VirtualNode vNode : vNetwork.getVertices()) {
				for (AbstractDemand vNodeConstraint : vNode.get()) {
					if (vNodeConstraint instanceof VirtualProvidedSecurity) {
						vNode.remove(vNodeConstraint);
						break;
					}
				}
			}
		}

		// Verkn�pfung von Links *******************************
		for (SubstrateLink link : stack.getSubstrate().getEdges()) {
			for (AbstractResource linkConstraint : link.get()) {
				if (linkConstraint instanceof PhysicalDemandedSecurity) {
					link.remove(linkConstraint);
					break;
				}
			}
		}

		boolean substrate = true;
		for (Network<?, ?, ?> aNetwork : stack) {
			if (substrate) {
				substrate = false;
				continue;
			}

			VirtualNetwork vNetwork = (VirtualNetwork) aNetwork;
			for (VirtualLink vLink : vNetwork.getEdges()) {
				for (AbstractDemand vLinkConstraint : vLink.get()) {
					if (vLinkConstraint instanceof VirtualProvidedSecurity) {
						vLink.remove(vLinkConstraint);
						break;
					}
				}
			}
		}
	}

	public abstract void generatePhProv_VirtDemSecurityResourceAndDemands(
			NetworkStack stack, int minPhysicalProvidedSecurity,
			int maxPhysicalProvidedSecurity, int minVirtualDemandSecurity,
			int maxVirtualDemandedSecurity);

	/**
	 * Returns a list of Metrics to be considered. Excepts Energy-related
	 * Metrics.
	 * 
	 * Copied method getDefaultMetrics().
	 * 
	 * @author Julian Willerding
	 * 
	 * @return List of Metrics
	 */
	public static List<EvaluationMetric> getSecurityMetrics() {
		List<EvaluationMetric> result = new LinkedList<EvaluationMetric>();

		// Security-Metrics
		result.add(new AvSecSpreadDemProv());
		result.add(new MaxSecSpreadDemProv());
		result.add(new AvSecSpreadProvDem());
		result.add(new MaxSecSpreadProvDem());

		// Default-Metrics (without Energy-related ones)
		result.add(new AcceptedVnrRatio());
		// result.add(new AvActiveLinkStress());
		// result.add(new AvActiveNodeStress());
		// result.add(new AvAllPathLength());
		// result.add(new AvLinkStress());
		// result.add(new AvNodeStress());
		// result.add(new AvPathLength());
		// result.add(new Cost());
		result.add(new CostRevenue(false));
		// result.add(new CostRevTimesMappedRev(false));
		// result.add(new LinkCostPerVnr());
		// result.add(new LinkUtilization());
		// result.add(new MappedRevenue(false));
		// result.add(new MaxLinkStress());
		// result.add(new MaxNodeStress());
		// result.add(new MaxPathLength());
		// result.add(new NodeUtilization());
		// result.add(new RatioMappedRevenue(false));
		// result.add(new RejectedNetworksNumber());
		// result.add(new RemainingLinkResource());
		// result.add(new RevenueCost(false));
		// result.add(new SolelyForwardingHops());
		// result.add(new TotalRevenue(false));

		return result;
	}

	public static void generateRandomPhProv_VirtDemSecurityResourceAndDemands(
			NetworkStack stack, int minPhysicalProvidedSecurity,
			int maxPhysicalProvidedSecurity, int minVirtualDemandSecurity,
			int maxVirtualDemandedSecurity) {
		Random random = new Random();

		// Verkn�pfung von Nodes *******************************
		for (SubstrateNode node : stack.getSubstrate().getVertices()) {
			PhysicalProvidedSecurity psr = new PhysicalProvidedSecurity(node);
			int value = (int) (minPhysicalProvidedSecurity + (maxPhysicalProvidedSecurity
					- minPhysicalProvidedSecurity + 1)
					* random.nextDouble());
			psr.setPhysicalProvidedSecurityLevel((double) value);
			node.add(psr);
		}

		boolean substrateNode = true;
		for (Network<?, ?, ?> aNetwork : stack) {
			if (substrateNode) {
				substrateNode = false;
				continue;
			}

			VirtualNetwork vNetwork = (VirtualNetwork) aNetwork;
			for (VirtualNode vNode : vNetwork.getVertices()) {
				VirtualDemandedSecurity msd = new VirtualDemandedSecurity(vNode);
				int value = (int) (minVirtualDemandSecurity + (maxVirtualDemandedSecurity
						- minVirtualDemandSecurity + 1)
						* random.nextDouble());
				msd.setVirtualDemandedSecurityLevel((double) value);
				vNode.add(msd);
			}
		}

		// Verkn�pfung von Links *******************************
		for (SubstrateLink link : stack.getSubstrate().getEdges()) {
			PhysicalProvidedSecurity psr = new PhysicalProvidedSecurity(link);
			int value = (int) (minPhysicalProvidedSecurity + (maxPhysicalProvidedSecurity
					- minPhysicalProvidedSecurity + 1)
					* random.nextDouble());
			psr.setPhysicalProvidedSecurityLevel((double) value);
			link.add(psr);
		}

		boolean substrate = true;
		for (Network<?, ?, ?> aNetwork : stack) {
			if (substrate) {
				substrate = false;
				continue;
			}

			VirtualNetwork vNetwork = (VirtualNetwork) aNetwork;
			for (VirtualLink vLink : vNetwork.getEdges()) {
				VirtualDemandedSecurity msd = new VirtualDemandedSecurity(vLink);
				int value = (int) (minVirtualDemandSecurity + (maxVirtualDemandedSecurity
						- minVirtualDemandSecurity + 1)
						* random.nextDouble());
				msd.setVirtualDemandedSecurityLevel((double) value);
				vLink.add(msd);
			}
		}
	}
}
