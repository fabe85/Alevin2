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
import vnreal.generators.LocalityGenerator;

@RunWith(Parameterized.class)
public class FlatLocalitySubstrateTest extends NewTopologiesExperimentRig {
	private static final long SEED = 1234;

	private static final int NODES_MIN = 2;
	private static final int NODES_MAX = 15;

	private static final double ALPHA_MIN = 0.0;
	private static final double ALPHA_MAX = 1.0;
	private static final double ALPHA_STEP = 0.1;

	private static final double BETA_MIN = 0.0;
	private static final double BETA_MAX = 1.0;
	private static final double BETA_STEP = 0.1;
	
	private static final double DIST_MIN = 0.0;
	private static final double DIST_MAX = 1.0;
	private static final double DIST_STEP = 0.1;

	public FlatLocalitySubstrateTest(ExperimentConfig data) {
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
					for (double beta = BETA_MIN; beta <= BETA_MAX; beta += BETA_STEP) {
						for (double dist = DIST_MIN; dist <= DIST_MAX; dist += DIST_STEP) {
							LocalityGenerator sgen = new LocalityGenerator();
							sgen.setNumNodes(nodes*nodes);
							sgen.setAlpha(alpha);
							sgen.setBeta(beta);
							sgen.setR(dist);
							sgen.setSeed(SEED);

							data.add(new ExperimentConfig[] {
									new ExperimentConfig(sgen, vgens, vneAlgo, constraints)
							});
						}
					}
				}
			}
		}

		return data;
	}
}
