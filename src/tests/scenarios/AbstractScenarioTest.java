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

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import mulavito.graph.generators.WaxmanGraphGenerator;
import tests.io.PrintWriterDataReceiver;
import tests.scenarios.AbstractScenarioTest.ScenarioData;
import tests.scenarios.AbstractScenarioTest.TestConfiguration;
import vnreal.algorithms.utils.mls.MLSLattice;
import vnreal.demands.BandwidthDemand;
import vnreal.demands.CpuDemand;
import vnreal.demands.MLSDemand;
import vnreal.demands.NullDemand;
import vnreal.evaluations.metrics.AcceptedVnrRatio;
import vnreal.evaluations.metrics.AvActiveLinkStress;
import vnreal.evaluations.metrics.AvActiveNodeStress;
import vnreal.evaluations.metrics.AvAllPathLength;
import vnreal.evaluations.metrics.AvLinkStress;
import vnreal.evaluations.metrics.AvNodeStress;
import vnreal.evaluations.metrics.AvPathLength;
import vnreal.evaluations.metrics.CostRevenue;
import vnreal.evaluations.metrics.EvaluationMetric;
import vnreal.evaluations.metrics.LinkCostPerVnr;
import vnreal.evaluations.metrics.LinkUtilization;
import vnreal.evaluations.metrics.NodeUtilization;
import vnreal.evaluations.metrics.RatioMappedRevenue;
import vnreal.evaluations.metrics.RejectedNetworksNumber;
import vnreal.evaluations.metrics.RevenueCost;
import vnreal.evaluations.metrics.Runtime;
import vnreal.evaluations.metrics.SolelyForwardingHops;
import vnreal.evaluations.metrics.runningTimePerMappedVN;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.BandwidthResource;
import vnreal.resources.CpuResource;
import vnreal.resources.MLSResource;
import vnreal.resources.MultiCoreEnergyResource;
import vnreal.resources.NullResource;
import vnreal.resources.StaticEnergyResource;

public abstract class AbstractScenarioTest<T extends ScenarioData, S extends TestConfiguration> {
	
	public final S c;
	private final String name;
	
	public long maxRuntimeInSeconds = 10*60;

	public final int myminCPUResource = 1;
	public final int mymaxCPUResource = 100;
	
	public final int myminCPUDemand = 1;
	public final int mymaxCPUDemand = 50;
	
	public final int myminBandwidthResource = 1;
	public final int mymaxBandwidthResource = 100;
	
	public final int myminBandwidthDemand = 1;
	public final int mymaxBandwidthDemand = 50;

	
	protected static HashMap<String, SubstrateNetwork> sCache = new HashMap<String, SubstrateNetwork>();
	public static boolean cacheEnabled = false;
	
	
	/** # of runs with different random seeds if algorithm is non-deterministic */
	public int numRunsPerScenario = 1;
	
	private final boolean isPathSplittingAlgorithm;

	
	protected AbstractScenarioTest(S c, String name, boolean isPathSplittingAlgorithm) {
		this.c = c;
		this.name = name;
		this.isPathSplittingAlgorithm = isPathSplittingAlgorithm;
	}
	
	
	public static class TestConfiguration {
		public int numScenarios = 30;

		public int[] numSNodesArray = { 1000 };
		public int[] numSNodesToRemoveArray = { 100, 100, 100, 100, 100, 100, 100, 100, 100, 50 };
		
		public int[] numVNetsArray = { 1 };

		public int[] numVNodesPerVNetArray = { 20 };

		public double[] alphaArray = { 0.5 };
		public double[] betaArray = { 0.5 };
	}
	
	
	public void executeTest() throws IOException {
		
		PrintWriterDataReceiver writer = new PrintWriterDataReceiver(new PrintWriter(new FileWriter(
				name + "_out.txt", true), true));
		
		// FIXME UniformStream.setSeed(0);

		int total = c.alphaArray.length * c.betaArray.length * c.numVNodesPerVNetArray.length
				* c.numVNetsArray.length * c.numSNodesArray.length
				* c.numScenarios * (c.numSNodesToRemoveArray.length + 1);
		int pos = 0;

		int[] numSNodesToRemoveQueue = new int[c.numSNodesToRemoveArray.length + 1];
		System.arraycopy(c.numSNodesToRemoveArray, 0, numSNodesToRemoveQueue, 0, c.numSNodesToRemoveArray.length);
		
		int timeoutSNSize = -1;
		for (double alpha : c.alphaArray) {
			for (double beta : c.betaArray) {
				for (int numVNodesPerVNet : c.numVNodesPerVNetArray) {
					for (int numVNets : c.numVNetsArray) {
						for (int numSNodes : c.numSNodesArray) {

							for (int scenario = 0; scenario < c.numScenarios; scenario++) {
								if (timeoutSNSize != -1 && numSNodes >= timeoutSNSize) {
									break;
								}

								ScenarioData scenariodata = new ScenarioData(alpha, beta, numSNodes, numVNets, numVNodesPerVNet);
								final NetworkStack stack = generate(scenariodata, scenario);

								for (int numSNodesToRemove : numSNodesToRemoveQueue) {
									pos++;
									System.out.println("[" + new Date().toString() + "] " + name + ": Scenario " + pos + " / " + total + " (" + scenariodata.getSuffix(scenario) + ")");
									
									paramLoop: for (final T param : getParams(scenariodata)) {
										
										String suffix = param.getSuffix(scenario);
										for (int run = 0; run < numRunsPerScenario; run++) {
											String scenarioSuffix = suffix + "_" + run;
											if (numRunsPerScenario > 1)
												System.out.println("Run " + scenarioSuffix);
											
											long startTime = System.currentTimeMillis();
											ExecutorService service = Executors.newSingleThreadExecutor();
										    Future<?> future = service.submit(new Runnable() {
										        @Override
										        public void run() {
										        	runAlgorithm(stack, param);
										        }
										    });

										    boolean timeout = false;
										    try {
										        future.get(maxRuntimeInSeconds, TimeUnit.SECONDS);
										    }
										    catch(TimeoutException e) {
										        timeout = true;
										        timeoutSNSize = scenariodata.numSNodes;
										    } catch (InterruptedException e) {
												e.printStackTrace();
											} catch (ExecutionException e) {
												e.printStackTrace();
											}
											long elapsedTime = System.currentTimeMillis() - startTime;

											if (!timeout) {
												evaluate(writer, stack, scenarioSuffix, elapsedTime);
											}
											
											stack.clearMappings();
											
											if (timeout) {
												System.err.println("Timeout for " + timeoutSNSize + " snodes after " + (elapsedTime  / 1000) + " seconds");
												break paramLoop;
											}
										}

									}
									
									removeRandomNodes(stack, numSNodesToRemove);
									scenariodata.numSNodes -= numSNodesToRemove;
								}
							}
						}
					}

				}
			}
		}
		
		writer.finish();
	}

	protected void removeRandomNodes(NetworkStack stack, int n) {
		SubstrateNetwork snet = stack.getSubstrate();
		LinkedList<SubstrateNode> snodes = new LinkedList<SubstrateNode>(snet.getVertices());
		
		Collections.shuffle(snodes);
		
		for (int i = 0; i < n && !snodes.isEmpty(); ++i) {
			snet.removeVertex(snodes.pollFirst());
		}
	}
	
	public abstract LinkedList<T> getParams(ScenarioData data);

	
	public static class ScenarioData {
		public int numSNodes;
		public int numVNets;
		public int numVNodesPerVNet;
		public double alpha;
		public double beta;

		public ScenarioData(double alpha, double beta, int numSNodes, int numVNets, int numVNodesPerVNet) {
			this.alpha = alpha;
			this.beta = beta;
			this.numSNodes = numSNodes;
			this.numVNets = numVNets;
			this.numVNodesPerVNet = numVNodesPerVNet;
		}
		
		protected String getSuffix(int iteration) {
			return numSNodes + "_" + numVNets + "_"
					+ numVNodesPerVNet + "_" + iteration;
		}
	}
	
	public LinkedList<EvaluationMetric> getMetrics(double elapsedTime) {
		LinkedList<EvaluationMetric> result = new LinkedList<EvaluationMetric>();
		
		result.add(new AcceptedVnrRatio());
		result.add(new RatioMappedRevenue(isPathSplittingAlgorithm));
		result.add(new RejectedNetworksNumber());

		result.add(new AvAllPathLength());
		result.add(new AvPathLength());

		result.add(new AvActiveLinkStress());
		result.add(new AvActiveNodeStress());
		result.add(new AvLinkStress());
		result.add(new AvNodeStress());
//		result.add(new MaxLinkStress());
//		result.add(new MaxNodeStress());
//		result.add(new MaxPathLength());


//		result.add(new Cost());
		result.add(new CostRevenue(isPathSplittingAlgorithm));
//		result.add(new CostRevTimesMappedRev(isPathSplittingAlgorithm));
//		result.add(new MappedRevenue(isPathSplittingAlgorithm));

//		result.add(new EnergyConsumption());
//		result.add(new RunningNodes());

		result.add(new LinkCostPerVnr());
		result.add(new LinkUtilization());
		result.add(new NodeUtilization());
//		result.add(new RemainingLinkResource());
		result.add(new RevenueCost(isPathSplittingAlgorithm));
		result.add(new SolelyForwardingHops());
//		result.add(new TotalRevenue(isPathSplittingAlgorithm));
		
		result.add(new Runtime(elapsedTime));
		result.add(new runningTimePerMappedVN(elapsedTime));
		
		return result;
	}
	
	protected void evaluate(PrintWriterDataReceiver writer, NetworkStack stack, String scenarioSuffix, long elapsedTime) {
		// AvailableResourcesPathSplitting.sortByRevenues(stack);

		for (EvaluationMetric metric : getMetrics(elapsedTime)) {
			metric.setStack(stack);
			double y = metric.getValue();

			writer.receive(stack, metric.getClass().getSimpleName(), scenarioSuffix, y);
		}

	}

	// N.B. Made abstract to be able to parallelize algorithms.
	protected abstract void runAlgorithm(NetworkStack stack, T data);

	/**
	 * This Method generates seeds based on given parameters
	 * @param numScenario Number of current scenario
	 * @param numVNodesPerVNet Number of virtual Nets 
	 * @return <code>null</code> when no fixed randoms (default) is nessecary, else {@link Long}
	 */
	protected Long getSeed(int numScenario, int numVNodesPerVNet, ScenarioData data) {
		return null;
	}
	
	/**
	 * A method to generate a scenario with the specified parameters
	 */
	public NetworkStack generate(ScenarioData data, int numScenario) {
		
		NetworkStack result = null;

		String sNetID = data.numSNodes + "_" + data.alpha + "_" + data.beta + "_" + numScenario;
		SubstrateNetwork sNetwork = null;
		if (cacheEnabled) {
			sNetwork = sCache.get(sNetID);
		}
		if (sNetwork == null) {

			WaxmanGraphGenerator<SubstrateNode, SubstrateLink> sGenerator;
			Long seed = getSeed(numScenario, data.numVNodesPerVNet, data);
			if(seed == null) {
				sGenerator = new WaxmanGraphGenerator<SubstrateNode, SubstrateLink>(
					data.alpha, data.beta, false);
			} else {
				sGenerator = new WaxmanGraphGenerator<SubstrateNode, SubstrateLink>(
						data.alpha, data.beta, false, seed);
			}
			sNetwork = new SubstrateNetwork(false);
			for (int i = 0; i < data.numSNodes; ++i) {
				SubstrateNode sn = new SubstrateNode();
				sn.setName(sn.getId() + "");
				sNetwork.addVertex(sn);
			}
			sGenerator.generate(sNetwork);
			
			generateCPUResources(
					sNetwork,
					myminCPUResource, mymaxCPUResource, getSeed(numScenario, data.numVNodesPerVNet, data));
			generateBandwidthResources(
					sNetwork,
					myminBandwidthResource, mymaxBandwidthResource, getSeed(numScenario, data.numVNodesPerVNet, data));
			
//			generateFixedStaticEnergyConsumptionResources(sNetwork, 100);
			
			generateAdditionalResources(sNetwork, getSeed(numScenario, data.numVNodesPerVNet, data));
			
			sNetwork.generateDuplicateEdges();
			
			
			
			result = new NetworkStack(sNetwork, generateVNetworks(data, numScenario));
			
			if (cacheEnabled) {
				sCache.put(sNetID, sNetwork);
			}
		} else {
			result = new NetworkStack(sNetwork, generateVNetworks(data, numScenario));
		}

		return result;
	}
	
	/**
	 * This method is generating the VNetworks
	 * @param data Parameters of the scenario from type T
	 * @param numScenario Number of the Scenario
	 * @return List of Virtual Networks
	 */
	private List<VirtualNetwork> generateVNetworks(ScenarioData data, int numScenario) {
		WaxmanGraphGenerator<VirtualNode, VirtualLink> vGenerator;
		Long seed = getSeed(numScenario, data.numVNodesPerVNet, data);
		if(seed == null) {
			vGenerator = new WaxmanGraphGenerator<VirtualNode, VirtualLink>(
				data.alpha, data.beta, false);
		} else {
			vGenerator = new WaxmanGraphGenerator<VirtualNode, VirtualLink>(
					data.alpha, data.beta, false, seed);
		}
		
		List<VirtualNetwork> vNetworks = new LinkedList<VirtualNetwork>();
		for (int i = 1; i <= data.numVNets; ++i) {
			VirtualNetwork vNetwork = new VirtualNetwork(i);
			vNetwork.setName(i + "");
			for (int n = 0; n < data.numVNodesPerVNet; ++n) {
				VirtualNode vn = new VirtualNode(i);

				vNetwork.addVertex(vn);
			}
			vGenerator.generate(vNetwork);
			
			generateCPUDemands(
					vNetwork,
					myminCPUDemand, mymaxCPUDemand, getSeed(numScenario, data.numVNodesPerVNet, data));
			generateBandwidthDemands(
					vNetwork,
					myminBandwidthDemand, mymaxBandwidthDemand, getSeed(numScenario, data.numVNodesPerVNet, data));
			
			generateAdditionalDemands(vNetwork, getSeed(numScenario, data.numVNodesPerVNet, data));
			
//			Utils.generateEnergyDemands(vNetwork);
			
			vNetwork.generateDuplicateEdges();
			
			vNetworks.add(vNetwork);
		}
		
		return vNetworks;
	}
	
	

	public void generateCPUResources(SubstrateNetwork sNetwork,
			int minResourceCPU, int maxResourceCPU, Long seed) {
		
		generateRandomCPUResources(
			sNetwork, minResourceCPU, maxResourceCPU, seed);
	}
	
	public void generateCPUDemands(VirtualNetwork vNetwork,
			int minDemandCPU, int maxDemandCPU, Long seed) {

		generateRandomCPUDemands(
				vNetwork, minDemandCPU, maxDemandCPU, seed);
	}


	public void generateBandwidthResources(SubstrateNetwork sNetwork,
			int minResourceBandwidth, int maxResourceBandwidth, Long seed) {
		
		generateRandomBandwidthResources(
			sNetwork, minResourceBandwidth, maxResourceBandwidth, seed);
	}
	
	public void generateBandwidthDemands(VirtualNetwork vNetwork,
			int minDemandBandwidth, int maxDemandBandwidth, Long seed) {

		generateRandomBandwidthDemands(
				vNetwork, minDemandBandwidth, maxDemandBandwidth, seed);
	}


	public static void generateRandomCPUResources(SubstrateNetwork sNetwork,
			int minResourceCPU, int maxResourceCPU, Long seed) {
		Random random = new Random();
		if(seed != null) 
			random.setSeed(seed);

		for (SubstrateNode n : sNetwork.getVertices()) {
			CpuResource cpu = new CpuResource(n);
			int value = (int) (minResourceCPU + (maxResourceCPU
					- minResourceCPU + 1)
					* random.nextDouble());
			cpu.setCycles((double) value);
			n.add(cpu);
		}
	}

	protected void generateAdditionalResources(SubstrateNetwork sNetwork, Long seed) {

	}
	
	protected void generateAdditionalDemands(VirtualNetwork vNetwork, Long seed) {
		
	}
	
	public static void generateRandomCPUDemands(VirtualNetwork vNetwork,
			int minDemandCPU, int maxDemandCPU, Long seed) {
		Random random = new Random();
		if(seed != null) 
			random.setSeed(seed);

		
		for (VirtualNode n : vNetwork.getVertices()) {
			CpuDemand cpu = new CpuDemand(n);
			int value = (int) (minDemandCPU + (maxDemandCPU - minDemandCPU + 1)
					* random.nextDouble());
			cpu.setDemandedCycles((double) value);
			n.add(cpu);
		}
	}

	public static void generateRandomBandwidthResources(
			SubstrateNetwork sNetwork, int minResourceBandwidth,
			int maxResourceBandwidth, Long seed) {
		Random random = new Random();
		if(seed != null) 
			random.setSeed(seed);

		
		for (SubstrateLink l : sNetwork.getEdges()) {
			BandwidthResource bw = new BandwidthResource(l);
			int value = (int) (minResourceBandwidth + (maxResourceBandwidth
					- minResourceBandwidth + 1)
					* random.nextDouble());
			bw.setBandwidth((double) value);
			l.add(bw);
		}
	}
	
	public static void generateRandomBandwidthDemands(
			VirtualNetwork vNetwork, int minDemandBandwidth,
			int maxDemandBandwidth, Long seed) {
		Random random = new Random();
		if(seed != null) 
			random.setSeed(seed);


		for (VirtualLink l : vNetwork.getEdges()) {
			BandwidthDemand bw = new BandwidthDemand(l);
			int value = (int) (minDemandBandwidth + (maxDemandBandwidth
					- minDemandBandwidth + 1)
					* random.nextDouble());
			bw.setDemandedBandwidth((double) value);
			l.add(bw);
		}
	}

	public static void generateFixedStaticEnergyConsumptionResources(
			SubstrateNetwork sNetwork, int consumption) {
		for (SubstrateNode n : sNetwork.getVertices()) {
			StaticEnergyResource r = new StaticEnergyResource(n, consumption);

			n.add(r);
		}
	}
	
	public static void generateRandomStaticEnergyConsumptionResources(
			SubstrateNetwork sNetwork,
			int myminConsumption, int mymaxConsumption, Long seed) {
		Random random = new Random();
		if(seed != null) 
			random.setSeed(seed);


		for (SubstrateNode n : sNetwork.getVertices()) {
			int value = (int) (myminConsumption + (mymaxConsumption
					- myminConsumption + 1)
					* random.nextDouble());
			StaticEnergyResource r = new StaticEnergyResource(n, value);

			n.add(r);
		}
	}

	public static void generateMultiCoreEnergyConsumptionResources(
			SubstrateNetwork sNetwork, int idleConsumption,
			int additionalConsumptionPerCore, int numberOfCores) {

		for (SubstrateNode n : sNetwork.getVertices()) {
			MultiCoreEnergyResource r = new MultiCoreEnergyResource(n,
					idleConsumption, additionalConsumptionPerCore,
					numberOfCores);

			n.add(r);
		}
	}
	
	/**
	 * This method generates MLS Labels for every node 
	 * @param sNetwork Substrate Network
	 * @param lattice A MLS Label lattice
	 */
	public static void generateMLSResources(SubstrateNetwork sNetwork, MLSLattice lattice, Long seed) {
		Random random = new Random();
		if(seed != null) 
			random.setSeed(seed);

		
		int maxLevel = lattice.getNumberOfLevels() - 1;
		ArrayList<String> cats = lattice.getCategories();
		
		//Label all Substratenodes (actual Random)
		for (SubstrateNode n :  sNetwork.getVertices()) {
			//Create Random resource
			int resDem = (int)((maxLevel + 1) * random.nextDouble());
			int resProv = (int)((maxLevel + 1) * random.nextDouble());
			
			//we need at least one Category
			int countCat;
			do {
				countCat = (int)((cats.size()+1) * random.nextDouble());
			} while (countCat < 0);
			ArrayList<String> chosenCats = new ArrayList<String>();
			for (int c = 0; c < countCat; c++) {
				String cat;
				do {
					cat = cats.get((int)(cats.size() * random.nextDouble()));
				} while (chosenCats.contains(cat));
				chosenCats.add(cat);
			}
			
			MLSResource res = new MLSResource(n, resDem, resProv, chosenCats);
			n.add(res);
		}
	}
	
	/**
	 * This method generates MLS Labels for every node 
	 * @param vNetwork Virtual Network
	 * @param lattice A MLS Label lattice
	 */
	public static void generateMLSDemands(VirtualNetwork vNetwork, MLSLattice lattice, Long seed) {
		Random random = new Random();
		if(seed != null) 
			random.setSeed(seed);

		
		int maxLevel = lattice.getNumberOfLevels() - 1;
		ArrayList<String> cats = lattice.getCategories();
		
		for (VirtualNode n : vNetwork.getVertices()) {
			//Create Random resource
			int resDem = (int)((maxLevel + 1) * random.nextDouble());
			int resProv = (int)((maxLevel + 1) * random.nextDouble());

			//we need at least one Category
			int countCat;
			do {
				countCat = (int)((cats.size()+1) * random.nextDouble());
			} while (countCat < 1);
			ArrayList<String> chosenCats = new ArrayList<String>();
			for (int c = 0; c < countCat; c++) {
				String cat;
				do {
					cat = cats.get((int)(cats.size() * random.nextDouble()));
				} while (chosenCats.contains(cat));
				chosenCats.add(cat);
			}

			MLSDemand res = new MLSDemand(n, resDem, resProv, chosenCats);
			n.add(res);
		}
	}
	
	
	
	/**
	 * This method generates MLS Labels for every node so that all demands are fulfillable
	 * @param sNetwork Substrate Network
	 * @param lattice A MLS Label lattice
	 * @param seed Seed for random, null if fully random
	 */
	public static ArrayList<MLSResource> generateMLSResourcesReasonable(SubstrateNetwork sNetwork, MLSLattice lattice, Long seed) {
		
		ArrayList<MLSResource> resList = new ArrayList<MLSResource>();
		
		Random random = new Random();
		if(seed != null) 
			random.setSeed(seed);

		
		int maxLevel = lattice.getNumberOfLevels() - 1;
		ArrayList<String> cats = lattice.getCategories();
		
		//Label all Substratenodes (actual Random)
		for (SubstrateNode n : sNetwork.getVertices()) {
			//Create Random resource
			int resDem = (int)((maxLevel + 1) * random.nextDouble());
			int resProv = (int)((maxLevel + 1) * random.nextDouble());
			
			//we need at least one Category
			int countCat;
			do {
				countCat = (int)((cats.size()+1) * random.nextDouble());
			} while (countCat < 0);
			ArrayList<String> chosenCats = new ArrayList<String>();
			for (int c = 0; c < countCat; c++) {
				String cat;
				do {
					cat = cats.get((int)(cats.size() * random.nextDouble()));
				} while (chosenCats.contains(cat));
				chosenCats.add(cat);
			}
			
			MLSResource res = new MLSResource(n, resDem, resProv, chosenCats);
			resList.add(res);
			n.add(res);
		}
		
		return resList;

	}

	/**
	 * This method generates MLS Labels for every node so that all demands are fulfillable
	 * @param vNetwork Virtual Network
	 * @param lattice A MLS Label lattice
	 * @param resList ArrayList of MLSRessources
	 * @param seed Seed for random, null if fully random
	 */
	public static void generateMLSDemandsReasonable(VirtualNetwork vNetwork, MLSLattice lattice, ArrayList<MLSResource> resList, Long seed) {

		Random random = new Random();
		if(seed != null) 
			random.setSeed(seed);

		int maxLevel = lattice.getNumberOfLevels() - 1;
		ArrayList<String> cats = lattice.getCategories();

		for (VirtualNode n : vNetwork.getVertices()) {

			MLSDemand dem;
			do {
				//Create Random resource
				int resDem = (int)((maxLevel + 1) * random.nextDouble());
				int resProv = (int)((maxLevel + 1) * random.nextDouble());

				//we need at least one Category
				int countCat;
				do {
					countCat = (int)((cats.size()+1) * random.nextDouble());
				} while (countCat < 1);
				ArrayList<String> chosenCats = new ArrayList<String>();
				for (int c = 0; c < countCat; c++) {
					String cat;
					do {
						cat = cats.get((int)(cats.size() * random.nextDouble()));
					} while (chosenCats.contains(cat));
					chosenCats.add(cat);
				}

				dem = new MLSDemand(n, resDem, resProv, chosenCats);
			}while(!checkMLSWorking(resList, dem));


			n.add(dem);
		}
	}
	
	/**
	 * Checks if the given demand is mappable on at least one Resource
	 * @param resList List of {@link MLSResource}
	 * @param dem {@link MLSDemand}
	 * @return true if mappable on at least one resource, false otherwise
	 */
	private static boolean checkMLSWorking(ArrayList<MLSResource> resList, MLSDemand dem) {
		for(MLSResource res : resList) {
			if(dem.getAcceptsVisitor().visit(res) && dem.getFulfillsVisitor().visit(res))
				return true;
		}
		return false;
	}
	
	
	/**
	 * This method generates Null Labels for every SubstrateNode 
	 * Only needed when you don't have other Demands or Resources, and metrics need them
	 * @param stack The Networkstack
	 */
	public static void generateNullResources(SubstrateNetwork sNetwork) {
		//Label all Substratenodes
		for (SubstrateNode n : sNetwork.getVertices()) {
			NullResource res = new NullResource(n);
			n.add(res);
		}
		
		for(SubstrateLink l : sNetwork.getEdges()) {
			NullResource res = new NullResource(l);
			l.add(res);
		}
		
	}

	/**
	 * This method generates Null Labels for every VirtualNode of teh given network
	 * Only needed when you don't have other Demands or Resources, and metrics need them
	 * @param stack The Networkstack
	 */
	public static void generateNullDemands(VirtualNetwork vNetwork) {
		for (VirtualNode n : vNetwork.getVertices()) {				
			NullDemand res = new NullDemand(n);
			n.add(res);
		}
		
		for (VirtualLink l : vNetwork.getEdges()) {
			NullDemand res = new NullDemand(l);
			l.add(res);
		}
	}


}
