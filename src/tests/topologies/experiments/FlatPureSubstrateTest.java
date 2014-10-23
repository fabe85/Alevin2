package tests.topologies.experiments;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import tests.topologies.ExperimentConfig;
import tests.topologies.NewTopologiesExperimentRig;
import tests.topologies.ExperimentConfig.ConstraintConfig;
import tests.topologies.embeddingalgos.FutureEmbeddingAlgorithm;
import vnreal.generators.INetworkGenerator;
import vnreal.generators.RandomEdgeGenerator;

@RunWith(Parameterized.class)
public class FlatPureSubstrateTest extends NewTopologiesExperimentRig {
	private static final long SEED = 1234;

	private static final int NODES_MIN = 2;
	private static final int NODES_MAX = 15;

	private static final double PROBABILITY_MIN = 0.0;
	private static final double PROBABILITY_MAX = 1.0;
	private static final double PROBABILITY_STEP = 0.1;

	public FlatPureSubstrateTest(ExperimentConfig data) {
		super(data);
	}

	@Parameters
	public static Collection<ExperimentConfig[]> data() {
		List<ExperimentConfig[]> data = new LinkedList<ExperimentConfig[]>();

		INetworkGenerator[] vgens = StaticTopologies.connectedWaxman();
		FutureEmbeddingAlgorithm[] vneAlgos = FutureEmbeddingAlgorithm.getAll();
		ConstraintConfig constraints = ConstraintConfig.tenPercent();

		for (FutureEmbeddingAlgorithm vneAlgo : vneAlgos) {
			for (int nodes = NODES_MIN; nodes <= NODES_MAX; nodes++) {
				for (double probability = PROBABILITY_MIN; probability <= PROBABILITY_MAX; probability += PROBABILITY_STEP) {
					RandomEdgeGenerator sgen = new RandomEdgeGenerator();
					sgen.setNumNodes(nodes*nodes);
					sgen.setProbability(probability);
					sgen.setSeed(SEED);

					data.add(new ExperimentConfig[] {
							new ExperimentConfig(sgen, vgens, vneAlgo, constraints)
					});
				}
			}
		}

		return data;
	}
}
