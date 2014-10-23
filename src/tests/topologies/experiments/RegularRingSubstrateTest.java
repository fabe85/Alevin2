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
import vnreal.generators.RingGenerator;

@RunWith(Parameterized.class)
public class RegularRingSubstrateTest extends NewTopologiesExperimentRig {
	private static final int NODES_MAX = 15;
	private static final int NODES_MIN= 2;

	public RegularRingSubstrateTest(ExperimentConfig data) {
		super(data);
	}

	@Parameters
	public static Collection<ExperimentConfig[]> data() {
		List<ExperimentConfig[]> data = new LinkedList<ExperimentConfig[]>();

		INetworkGenerator[] vgens = StaticTopologies.connectedWaxman();
		FutureEmbeddingAlgorithm[] vneAlgos = FutureEmbeddingAlgorithm.getAll();
		ConstraintConfig constraints = ConstraintConfig.tenPercent();

		for (FutureEmbeddingAlgorithm vneAlgo : vneAlgos) {
			for (RingGenerator.Direction dir : RingGenerator.Direction.values()) {
				for (int nodes = NODES_MIN; nodes <= NODES_MAX; nodes++) {
					RingGenerator sgen = new RingGenerator();
					sgen.setNumNodes(nodes*nodes);
					sgen.setLinkDirection(dir);

					data.add(new ExperimentConfig[] {
							new ExperimentConfig(sgen, vgens, vneAlgo, constraints)
					});
				}
			}
		}

		return data;
	}
}
