package tests.topologies;

import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import mulavito.graph.IEdge;
import mulavito.graph.IVertex;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import tests.scenarios.util.GraphMetrics;
import tests.scenarios.util.StackSummarizer;
import tests.scenarios.util.ValueSummarizer;
import tests.topologies.ExperimentConfig.ConstraintConfig;
import tests.topologies.embeddingalgos.FutureEmbeddingAlgorithm;
import tests.topologies.embeddingalgos.Pathsplitting;
import vnreal.algorithms.AbstractAlgorithm;
import vnreal.demands.BandwidthDemand;
import vnreal.demands.CpuDemand;
import vnreal.evaluations.metrics.AcceptedVnrRatio;
import vnreal.evaluations.metrics.CostRevenue;
import vnreal.evaluations.metrics.EvaluationMetric;
import vnreal.evaluations.metrics.NodeUtilization;
import vnreal.generators.INetworkGenerator;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.BandwidthResource;
import vnreal.resources.CpuResource;
import edu.uci.ics.jung.algorithms.shortestpath.DistanceStatistics;
import edu.uci.ics.jung.graph.Graph;

@RunWith(Parameterized.class)
public abstract class NewTopologiesExperimentRig {

	private static final String LOGFILE = String.format("%%h/alevin_philip.%d.log", System.currentTimeMillis() / 1000);
	private static final double NANOS_IN_SECOND = 1000000000.0;
	//private static final int NUM_REPETITIONS = 31;
	private static final int NUM_REPETITIONS = 1;

	// Summarizers
	private static final ValueSummarizer runtimeSummarizer = new ValueSummarizer("Runtime summary:");
	private static final ValueSummarizer acceptanceSummarizer = new ValueSummarizer("Acceptance summary:");
	private static final ValueSummarizer costRevenueSummarizer = new ValueSummarizer("Cost/Revenue summary:");
	private static final ValueSummarizer nodeUtilizationSummarizer = new ValueSummarizer("Node utilization summary:");

	private static final StackSummarizer outdegreeSummarizer = new StackSummarizer() {
		@Override
		protected String getSummaryTitle() {
			return "Outdegree distribution summary:";
		}

		@Override
		protected String evaluateGraph(Graph<IVertex, IEdge> graph) {
			return GraphMetrics.outdegreeDistribution(graph).toString();
		}
	};

	private static final StackSummarizer diameterSummarizer = new StackSummarizer() {
		@Override
		protected String getSummaryTitle() {
			return "Diameter summary:";
		}

		@Override
		protected String evaluateGraph(Graph<IVertex, IEdge> graph) {
			return String.format("%.0f", DistanceStatistics.diameter(graph));
		}
	};

	private static final StackSummarizer centerSummarizer = new StackSummarizer() {
		@Override
		protected String getSummaryTitle() {
			return "Center summary:";
		}

		@Override
		protected String evaluateGraph(Graph<IVertex, IEdge> graph) {
			return String.format("%d", GraphMetrics.numCenters(graph));
		}
	};

	private static final StackSummarizer connectedSummarizer = new StackSummarizer() {
		@Override
		protected String getSummaryTitle() {
			return "Connected summary:";
		}

		@Override
		protected String evaluateGraph(Graph<IVertex, IEdge> graph) {
			return String.format("%b", GraphMetrics.connected(graph));
		}
	};

	private static final StackSummarizer countSummarizer = new StackSummarizer() {
		@Override
		protected String getSummaryTitle() {
			return "Count summary:";
		}

		@Override
		protected String evaluateGraph(Graph<IVertex, IEdge> graph) {
			return String.format("numVertices=%d; numEdges=%d",
					graph.getVertexCount(),
					graph.getEdgeCount());
		}
	};

	private ExperimentConfig data;

	// Logging
	private static final Logger log;

	static {
		log = Logger.getLogger(NewTopologiesExperimentRig.class.getName());
		Handler fh = null;
		try {
			fh = new FileHandler(LOGFILE, true);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		fh.setFormatter(new SimpleFormatter());
		log.addHandler(fh);
		log.info("Logging initialized.");
	}

	@BeforeClass
	public static void beforeClass() {
		log.info("beforeClass()");
	}

	@AfterClass
	public static void afterClass() {
		log.info("afterClass()");
	}

	public NewTopologiesExperimentRig(ExperimentConfig data) {
		this.data = data;
	}

	@Test
	public void runScenario() {
		log.info("Running scenario:\n" + this.data.toString());

		// Build network stack from scenario configuration.
		NetworkStack nstack = buildNetworkStack(this.data);
		log.info("Network stack was built.");

		// Log metrics of generated graphs:
		// Node and edge count.
		log.info(countSummarizer.summarize(nstack));
		// Outdegree distribution.
		log.info(outdegreeSummarizer.summarize(nstack));
		// Diameter.
		log.info(diameterSummarizer.summarize(nstack));
		// Number of centers.
		log.info(centerSummarizer.summarize(nstack));
		// Connectivity.
		log.info(connectedSummarizer.summarize(nstack));

		// Run scenario multiple times to collect embedding metrics.
		double[] runtimes = new double[NUM_REPETITIONS];
		double[] acceptances = new double[NUM_REPETITIONS];
		double[] costrevenues = new double[NUM_REPETITIONS];
		double[] utilizations = new double[NUM_REPETITIONS];

		for (int i = 0; i < NUM_REPETITIONS; i++) {
			log.info(String.format("Embedding #%d.", i));
			nstack.clearMappings();

			FutureEmbeddingAlgorithm fEmbAlgo = this.data.embeddingAlgorithm;
			AbstractAlgorithm embAlgo = fEmbAlgo.getAlgorithm(nstack);
			boolean isPathsplitting = fEmbAlgo instanceof Pathsplitting;

			// Perform embedding.
			long startTime = System.nanoTime();
			embAlgo.performEvaluation();
			long stopTime = System.nanoTime();

			// Log embedding metrics:

			// Algorithm runtime.
			long elapsedNanos = stopTime - startTime;
			double elapsedSeconds = elapsedNanos / NANOS_IN_SECOND;
			runtimes[i] = elapsedSeconds;
			log.info(String.format("Embedding took %.6f seconds.",
					elapsedSeconds));

			// Acceptance ratio. (acceptedVnets/rejectedVnets*100)
			EvaluationMetric acceptance = new AcceptedVnrRatio();
			acceptance.setStack(nstack);
			double acp = acceptance.getValue();
			acceptances[i] = acp;
			log.info(String.format("AcceptedVnrRatio: %.6f.", acp));
			assertNull(acceptance.getStat());

			// Cost and revenue.
			EvaluationMetric costrevenue = new CostRevenue(isPathsplitting);
			costrevenue.setStack(nstack);
			double crv = costrevenue.getValue();
			costrevenues[i] = crv;
			log.info(String.format("CostRevenue(%sPS): %.6f.", isPathsplitting ? "" : "no", crv));
			assertNull(costrevenue.getStat());

			// Node utilization. (usedCpuCycles/numNodes)
			EvaluationMetric utilization = new NodeUtilization();
			utilization.setStack(nstack);
			double utl = utilization.getValue();
			utilizations[i] = utl;
			log.info(String.format("NodeUtilization: %.6f.", utl));

			// TODO running nodes?
		}

		// Summarize collected embedding metrics:
		log.info(runtimeSummarizer.summarize(runtimes));
		log.info(acceptanceSummarizer.summarize(acceptances));
		log.info(costRevenueSummarizer.summarize(costrevenues));
		log.info(nodeUtilizationSummarizer.summarize(utilizations));

		log.info("Scenario complete.");
	}

	private static NetworkStack buildNetworkStack(ExperimentConfig config) {
		ConstraintConfig constraints = config.constraintConfig;

		// Invoke specified generator to create substrate network.
		SubstrateNetwork snet = config.substrateGenerator.generateSubstrateNetwork(false);

		// Assign specified resources for substrate network.
		for (SubstrateNode sn : snet.getVertices()) {
			CpuResource cpu = new CpuResource(sn);
			cpu.setCycles(constraints.substrateCpuResource);
			sn.add(cpu);
		}
		for (SubstrateLink sl : snet.getEdges()) {
			BandwidthResource bw = new BandwidthResource(sl);
			bw.setBandwidth(constraints.substrateBandwidthResource);
			sl.add(bw);
		}

		List<VirtualNetwork> vnets = new ArrayList<VirtualNetwork>();
		for (int i = 0; i < config.virtualGenerators.length; i++) {
			INetworkGenerator vgen = config.virtualGenerators[i];

			// Invoke specified generator for each virtual network.
			VirtualNetwork vnet = vgen.generateVirtualNetwork(i+1);

			// Assign specified resources for each virtual network.
			for (VirtualNode vn : vnet.getVertices()) {
				CpuDemand cpu = new CpuDemand(vn);
				cpu.setDemandedCycles(constraints.virtualCpuDemand);
				vn.add(cpu);
			}
			for (VirtualLink vl : vnet.getEdges()) {
				BandwidthDemand bw = new BandwidthDemand(vl);
				bw.setDemandedBandwidth(constraints.virtualBandwidthDemand);
				vl.add(bw);
			}

			vnets.add(vnet);
		}

		return new NetworkStack(snet, vnets);
	}
}