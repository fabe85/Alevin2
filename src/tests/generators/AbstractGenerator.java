package tests.generators;

import java.util.ArrayList;

/**
 * This is the base class for any generator
 * 
 * @author Fabian Kokot
 *
 * @param <T> Some Object 
 */
public abstract class AbstractGenerator<T extends Object> {
	
	/**
	 * This Method will generate the Object 
	 * 
	 * @param parameters List of Objects as parameters needed for the generation process
	 * @return A Object from given type <T>
	 */
	abstract public T generate(ArrayList<Object> parameters);
	
	/**
	 * Resets all counters values and so on for the next Scenario
	 */
	abstract public void reset();
	

	
	
}
