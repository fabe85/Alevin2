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
import vnreal.generators.GridGenerator;
import vnreal.generators.INetworkGenerator;

@RunWith(Parameterized.class)
public class RegularMeshSubstrateTest extends NewTopologiesExperimentRig {
	private static final int HEIGHT_MAX = 15;
	private static final int HEIGHT_MIN = 2;

	private static final int WIDTH_MAX = 15;
	private static final int WIDTH_MIN = 2;

	public RegularMeshSubstrateTest(ExperimentConfig data) {
		super(data);
	}

	@Parameters
	public static Collection<ExperimentConfig[]> data() {
		List<ExperimentConfig[]> data = new LinkedList<ExperimentConfig[]>();

		INetworkGenerator[] vgens = StaticTopologies.connectedWaxman();
		FutureEmbeddingAlgorithm[] vneAlgos = FutureEmbeddingAlgorithm.getAll();
		ConstraintConfig constraints = ConstraintConfig.tenPercent();

		for (FutureEmbeddingAlgorithm vneAlgo : vneAlgos) {
			for (int width = WIDTH_MIN; width <= WIDTH_MAX; width++) {
				for (int height = HEIGHT_MIN; height <= HEIGHT_MAX; height++) {
					GridGenerator sgen = new GridGenerator();
					sgen.setHeight(height);
					sgen.setWidth(width);

					data.add(new ExperimentConfig[] {
							new ExperimentConfig(sgen, vgens, vneAlgo, constraints)
					});
				}
			}
		}

		return data;
	}
}
