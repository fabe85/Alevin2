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
import vnreal.generators.ExponentialGenerator;
import vnreal.generators.INetworkGenerator;

@RunWith(Parameterized.class)
public class FlatExponentialSubstrateTest extends NewTopologiesExperimentRig {
	private static final long SEED = 1234;

	private static final int NODES_MIN = 2;
	private static final int NODES_MAX = 15;

	private static final double ALPHA_MIN = 0.0;
	private static final double ALPHA_MAX = 1.0;
	private static final double ALPHA_STEP = 0.1;

	public FlatExponentialSubstrateTest(ExperimentConfig data) {
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
				for (double alpha = ALPHA_MIN; alpha <= ALPHA_MAX; alpha += ALPHA_STEP) {
					ExponentialGenerator sgen = new ExponentialGenerator();
					sgen.setNumNodes(nodes*nodes);
					sgen.setAlpha(alpha);
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
