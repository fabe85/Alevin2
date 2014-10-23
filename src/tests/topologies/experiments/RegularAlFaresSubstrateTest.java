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
import vnreal.generators.AlFaresGenerator;
import vnreal.generators.INetworkGenerator;

@RunWith(Parameterized.class)
public class RegularAlFaresSubstrateTest extends NewTopologiesExperimentRig {
	private static final int PODS_MAX = 16;
	private static final int PODS_MIN= 2;

	public RegularAlFaresSubstrateTest(ExperimentConfig data) {
		super(data);
	}

	@Parameters
	public static Collection<ExperimentConfig[]> data() {
		List<ExperimentConfig[]> data = new LinkedList<ExperimentConfig[]>();

		INetworkGenerator[] vgens = StaticTopologies.connectedWaxman();
		FutureEmbeddingAlgorithm[] vneAlgos = FutureEmbeddingAlgorithm.getAll();
		ConstraintConfig constraints = ConstraintConfig.tenPercent();

		for (FutureEmbeddingAlgorithm vneAlgo : vneAlgos) {
			for (int pods = PODS_MIN; pods <= PODS_MAX; pods++) {
				if (pods % 2 == 1) {
					continue;
				}

				AlFaresGenerator sgen = new AlFaresGenerator();
				sgen.setNumPods(pods);
				sgen.setClientsGenerated(true);

				data.add(new ExperimentConfig[] {
						new ExperimentConfig(sgen, vgens, vneAlgo, constraints)
				});
			}
		}

		return data;
	}
}
