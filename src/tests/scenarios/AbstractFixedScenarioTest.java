package tests.scenarios;

import tests.scenarios.AbstractScenarioTest.ScenarioData;
import tests.scenarios.AbstractScenarioTest.TestConfiguration;

public abstract class AbstractFixedScenarioTest<T extends ScenarioData, S extends TestConfiguration> extends
		AbstractScenarioTest<T, S> {

	private int seedMultiplier = 100;
	
	protected AbstractFixedScenarioTest(S c, String name, boolean isPathSplittingAlgorithm) {
		super(c, name, isPathSplittingAlgorithm);
	}

	@Override
	protected Long getSeed(int numScenario, int numVNodesPerVNet, ScenarioData data) {
		Long ret = new Long(((seedMultiplier + numScenario) * data.numVNodesPerVNet));
		//System.out.println("Seed: "+ret+"; Multi: "+seedMultiplier);
		seedMultiplier += 100;
		return ret;
	}
	
	
	
}
