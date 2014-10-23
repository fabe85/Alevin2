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
import tests.topologies.embeddingalgos.Stressbased;
import vnreal.generators.INetworkGenerator;
import vnreal.generators.RandomEdgeGenerator;

@RunWith(Parameterized.class)
public class DummyTest extends NewTopologiesExperimentRig {
	public DummyTest(ExperimentConfig data) {
		super(data);
	}

	@Parameters
	public static Collection<ExperimentConfig[]> data() {
		List<ExperimentConfig[]> data = new LinkedList<ExperimentConfig[]>();

		RandomEdgeGenerator sgen = new RandomEdgeGenerator();
		sgen.setNumNodes(30);
		sgen.setProbability(0.25);
		sgen.setSeed(0);

		INetworkGenerator[] vgens = new INetworkGenerator[5];
		for (int i = 0; i < vgens.length; i++) {
			RandomEdgeGenerator vgen = new RandomEdgeGenerator();
			vgen.setNumNodes(10+i);
			vgen.setProbability(0.75);
			vgen.setSeed(1234+i);
			vgens[i] = vgen;
		}

		FutureEmbeddingAlgorithm vneAlgo = new Stressbased();

		ConstraintConfig constraints = new ConstraintConfig(
				100.0, 100.0,
				10.0, 10.0);

		data.add(new ExperimentConfig[] {
				new ExperimentConfig(sgen, vgens, vneAlgo, constraints)
		});
		return data;
	}
}
