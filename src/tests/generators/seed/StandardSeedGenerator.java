package tests.generators.seed;

import java.util.ArrayList;

/**
 * Easy seed generator which gives always seed with the same differences 0, 10, 20, ...
 * 
 * @author Fabian Kokot
 *
 */
public class StandardSeedGenerator extends AbstractSeedGenerator {

	private Long numSeed = 0l;
	
	@Override
	public Long generate(ArrayList<Object> parameters) {
		
		
		Long ret = numSeed;
		numSeed += 10;
		return ret;
	}

	@Override
	public void reset() {
	}

	@Override
	public void setStartSeed(Long seed) {
		numSeed = seed;
		
	}

}
