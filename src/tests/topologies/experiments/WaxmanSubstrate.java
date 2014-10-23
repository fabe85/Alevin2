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
import vnreal.generators.WaxmanGenerator;

/**
 * 5 fixed, virtual networks, generated with Waxman:
 *  - nodes=10
 *  - alpha=1
 *  - beta=0.25
 *  - seed=1234, 1235, ...
 * 
 * substrate network, generated with Waxman:
 *  - nodes=10, 20, ..., 100
 *  - alpha=0.5, 0.6, ..., 1.5
 *  - beta=0.0, 0.1, ..., 1.0
 *  - seed=4321, 4322, ...
 * 
 * static demands/resources:
 *  - cpu/link rsc: 100.0
 *  - cpu/link dmd: 10.0
 * 
 * @author Philip Huppert
 */
@RunWith(Parameterized.class)
public class WaxmanSubstrate extends NewTopologiesExperimentRig {

	public WaxmanSubstrate(ExperimentConfig data) {
		super(data);
	}

	@Parameters
	public static Collection<ExperimentConfig[]> data() {
		List<ExperimentConfig[]> data = new LinkedList<ExperimentConfig[]>();

		ConstraintConfig constraints = new ConstraintConfig(
				100.0, 100.0,
				10.0, 10.0);

		FutureEmbeddingAlgorithm vneAlgo = new Stressbased();

		INetworkGenerator[] vgens = new INetworkGenerator[5];
		for (int i = 0; i < vgens.length; i++) {
			WaxmanGenerator vgen = new WaxmanGenerator();
			vgen.setNumNodes(10);
			vgen.setAlpha(1);
			vgen.setBeta(0.25);
			vgen.setSeed(1234+i);
			vgens[i] = vgen;
		}

		int seed = 4321;
		for (int numNodes = 10; numNodes <= 100; numNodes += 10) {
			for (double alpha = 0.5; alpha <= 1.5; alpha += 0.1) {
				for (double beta = 0.0; beta <= 1.0; beta += 0.1) {
					WaxmanGenerator sgen = new WaxmanGenerator();
					sgen.setNumNodes(numNodes);
					sgen.setAlpha(alpha);
					sgen.setBeta(beta);
					sgen.setSeed(seed++);

					data.add(new ExperimentConfig[] {
							new ExperimentConfig(sgen, vgens, vneAlgo, constraints)
					});
				}
			}
		}

		return data;
	}
}
