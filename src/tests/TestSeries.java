package tests;

import java.util.ArrayList;

/**
 * This class is representing a test series with a series of tests from one run and from one TestGenerator.
 * 
 * @author Fabian Kokot
 *
 */
public class TestSeries {
	private final String mTestSeriesName;
	private final ArrayList<TestRun> mTestRuns = new ArrayList<TestRun>();
	private final String mTestGenerator;
	
	/**
	 * 
	 * @param mTestSeriesName Name of the TestSeries
	 * @param mTestGenerator Path and ClassName of the TestGenerator
	 */
	public TestSeries(String mTestSeriesName, String mTestGenerator) {
		super();
		this.mTestSeriesName = mTestSeriesName;
		this.mTestGenerator = mTestGenerator;
	}
	
	
	/**
	 * Adds a {@link TestRun} to the series.
	 * 
	 * @param run A {@link TestRun}
	 */
	public void addTestRun(TestRun run) {
		if(!mTestRuns.contains(run))
			mTestRuns.add(run);
	}
	
	
	/**
	 * Returns a deepcopy of all {@link TestRun}
	 * 
	 * @return {@link ArrayList} of {@link TestRun}
	 */
	public ArrayList<TestRun> getAllTestRunsCopy() {
		ArrayList<TestRun> copy = new ArrayList<TestRun>();
		for(TestRun t : mTestRuns)
			copy.add(t.getCopy());
		
		return copy;
	}
	
	/**
	 * Returns the uncopied list of all TestRuns
	 * 
	 * @return
	 */
	public ArrayList<TestRun> getAllTestRuns() {
		return mTestRuns;
	}


	public String getTestSeriesName() {
		return mTestSeriesName;
	}


	public String getTestGenerator() {
		return mTestGenerator;
	}
	
}
