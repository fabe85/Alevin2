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
import vnreal.generators.StarGenerator;

@RunWith(Parameterized.class)
public class RegularStarSubstrateTest extends NewTopologiesExperimentRig {
	private static final int NODES_MAX = 15;
	private static final int NODES_MIN= 2;

	public RegularStarSubstrateTest(ExperimentConfig data) {
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
				StarGenerator sgen = new StarGenerator();
				sgen.setNumNodes(nodes*nodes);

				data.add(new ExperimentConfig[] {
						new ExperimentConfig(sgen, vgens, vneAlgo, constraints)
				});
			}
		}

		return data;
	}
}
