package tests;


import java.util.LinkedHashMap;

import vnreal.Scenario;

/**
 * This class represents one run of a test series 
 * @author Fabian Kokot
 *
 */
public class TestRun {

	private final LinkedHashMap<String, Object> mParameters;
	private final LinkedHashMap<String, Double> mResults;
	private Scenario mScenario;
	
	
	/**
	 * Creates a run with no scenario, parameters and results
	 */
	public TestRun() {
		mParameters = new LinkedHashMap<String, Object>();
		mResults = new LinkedHashMap<String, Double>();
		mScenario = null;
	}
	
	
	

	/**
	 * Creates a run with the given scenario, parameters and results
	 * 
	 * @param mParameters {@link LinkedHashMap} with {@link String} as Key and {@link Double} as value 
	 * @param mResults {@link LinkedHashMap} with {@link String} as Key and {@link Double} as value 
	 * @param mScenario A {@link Scenario}, can be <code>null</code> 
	 */
	public TestRun(LinkedHashMap<String, Object> parameters,
			LinkedHashMap<String, Double> results, Scenario scenario) {
		super();
		if(parameters == null || results == null)
			throw new Error("Error while creating TestRun: parameters or results are null!");
		this.mParameters = parameters;
		this.mResults = results;
		this.mScenario = scenario;
	}


	/**
	 * Adds the Pair to the parameter list.
	 * If key is already contained in the map, the old value will be replaced 
	 * 
	 * @param name Name of the parameter
	 * @param value Value of the Parameter
	 */
	public void addParameter(String name, Object value) {
		mParameters.put(name, value);
	}

	
	/**
	 * Adds the Pair to the result list.
	 * If key is already contained in the map, the old value will be replaced 
	 * 
	 * @param name Name of the metric
	 * @param value Result of the metric
	 */
	public void addResult(String name, Double value){
		mResults.put(name, value);
	}
	
	
	
	public Scenario getScenario() {
		//TODO: return a copy
		return mScenario;
	}


	public void setScenario(Scenario scenario) {
		this.mScenario = scenario;
	}




	public LinkedHashMap<String, Object> getParameters() {
		LinkedHashMap<String, Object> paramCopy = new LinkedHashMap<String, Object>();
		for(String p : mParameters.keySet()) {
			if(mParameters.get(p) instanceof Double)
				paramCopy.put(p, new Double((Double)mParameters.get(p)));
			else if(mParameters.get(p) instanceof String)
				paramCopy.put(p, mParameters.get(p));
			else
				throw new Error("Only Double or String are allowed");
		}
		
		return paramCopy;
	}




	public LinkedHashMap<String, Double> getResults() {
		LinkedHashMap<String, Double> resultCopy = new LinkedHashMap<String, Double>();
		for(String p : mResults.keySet()) {
			resultCopy.put(p, new Double(mResults.get(p)));
		}
		
		return resultCopy;
	}
	
	public void clearResults() {
		mResults.clear();
	}




	/**
	 * Creates a deep copy of the Object.
	 * WARNING: The Scenario-Object ISN'T a copy.
	 * 
	 * @return {@link TestRun}
	 */
	public TestRun getCopy() {

		

		
		//TODO: Create a deep copy a the Scenario...
		TestRun copy = new TestRun(this.getParameters(), this.getResults(), mScenario);
		return copy;
	}
	
}
