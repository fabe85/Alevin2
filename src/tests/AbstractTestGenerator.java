package tests;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;


/**
 * This is the base class for a TestGenerator, which is generating the 
 * a {@link TestSeries} with all the necessary parameters 
 * 
 * @author Fabian Kokot
 *
 */
public abstract class AbstractTestGenerator {
	
	public final Class<? extends AbstractTestRunner> mRunnerClass;
	public final String mTestSeriesName;
	public final String mTestGenerator;

	
	/** # of runs with different random seeds if algorithm is non-deterministic */
	public final int numRunsPerScenario;
	protected ArrayList<SimpleEntry<String, Object[]>> mParams;
	
	
	/**
	 * 
	 * 
	 * @param runnerClass Class which will run the tests
	 * @param seriesName Name of the series
	 * @param generatorName Name of the test generator
	 * @param numRunsPerScenario Number of distinct runs with the same parameter (due seed or random each run is different)
	 */
	public AbstractTestGenerator(Class<? extends AbstractTestRunner> runnerClass, String seriesName, String generatorName, int numRunsPerScenario) {
		super();
		this.mRunnerClass = runnerClass;
		this.mTestSeriesName = seriesName;
		this.mTestGenerator = generatorName;
		this.numRunsPerScenario = numRunsPerScenario;
	}
	
	/**
	 * This Method generates the TestSeries
	 * 
	 * @return A {@link TestSeries} without results
	 */
	public TestSeries generateTests() {
		TestSeries ts = new TestSeries(mTestSeriesName, mTestGenerator);
		for(TestRun tr : generateTestRuns(mParams, null)) {
			//Create the needed number of tests per scenario
			for(int i = 1; i <= numRunsPerScenario; i++) {
				TestRun copy = tr.getCopy();
				copy.addParameter("NumberOfRun", (double)i);
				ts.addTestRun(copy);
			}
		}
		return ts;
	}
	
	/**
	 * This method is to generating the different {@link TestRun}s 
	 * 
	 * @return List of {@link TestRun}
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<TestRun> generateTestRuns(ArrayList<SimpleEntry<String, Object[]>> input, LinkedHashMap<String, Object> output) {
		ArrayList<TestRun> list = new ArrayList<TestRun>();
		//When first
		if(output == null)
			output = new LinkedHashMap<String, Object>();
		//as long as we have Elements left in list
		if(input.size() > 0) {
			ArrayList<String> paramNames = new ArrayList<String>();
			ArrayList<Object[]> paramValues = new ArrayList<Object[]>();
			//1. save values of first Entryset and remove it
			SimpleEntry<String, Object[]> se = input.get(0);
//			String paramName = se.getKey();
//			Double[] paramValues = se.getValue();
//			paramNames.add(se.getKey());
//			paramValues.add(se.getValue());
//			input.remove(0);
			
			//2. To get some a number of input Synchronized they need to be marked with the same marker before a ":"
			if(se.getKey().contains(":")) {
				String marker = se.getKey().split(":")[0];
				for(SimpleEntry<String, Double[]> se2 : (ArrayList<SimpleEntry<String, Double[]>>)input.clone()) { //Never work with the same List you are reading!
					if(se2.getKey().contains(marker+":")) {
						paramNames.add(se2.getKey().split(":")[1]);
						paramValues.add(se2.getValue());
						input.remove(0);
					}
				}
			} else {
				paramNames.add(se.getKey());
				paramValues.add(se.getValue());
				input.remove(0);
			}
			
			//3. Iterate through all possible Values 
			for(int valueCounter = 0; valueCounter < paramValues.get(0).length; valueCounter++) {	//Number of Values 
				for(int objectCounter = 0; objectCounter < paramNames.size(); objectCounter++) {		//Synced Objects
					//Add to list
					output.put(paramNames.get(objectCounter), paramValues.get(objectCounter)[valueCounter]);
					//System.out.println("Name: "+paramNames.get(objectCounter)+"; Value: "+paramValues.get(objectCounter)[valueCounter]);
				}
				//STart recursion
				list.addAll(generateTestRuns((ArrayList<SimpleEntry<String, Object[]>>)input.clone(), output));
			}
		} else {
			//When we have no Entries in input, we can create a new TestRun with the output Object
			TestRun tr = new TestRun();
			for(Entry<String, Object> e : output.entrySet()) {
				tr.addParameter(e.getKey(), e.getValue());
			}
			list.add(tr);
		}
		return list;
	}
	
	
	
}
