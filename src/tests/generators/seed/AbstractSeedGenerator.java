package tests.generators.seed;

import tests.generators.AbstractGenerator;

public abstract class AbstractSeedGenerator extends AbstractGenerator<Long>{
	
	/**
	 * This method can set the actual start seed of the generator
	 * @param seed
	 */
	public abstract void setStartSeed(Long seed);
}
