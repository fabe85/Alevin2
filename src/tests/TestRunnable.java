package tests;

import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.virtual.VirtualNetwork;
import mulavito.algorithms.IAlgorithm;


/**
 * This a basic Runnable class to run tests
 * 
 * @author Fabian Kokot
 *
 */
public class TestRunnable implements Runnable {

	
	private IAlgorithm mAlgo;
	private final TestRun mRun;
	private final AbstractTestRunner mRunner;
	private final Long mStartSeed;

	/**
	 * 
	 * @param run The actual {@link TestRun}
	 * @param runner The TestRunner which is used
	 * @param startSeed The seed which is used 
	 */
	public TestRunnable(TestRun run, AbstractTestRunner runner, long startSeed) {
		mRun = run;
		mRunner = runner;
		mStartSeed = startSeed;
		
	}

	@Override
	public void run() {
		
		mRun.clearResults();
		
		mRunner.prepareTestStage1(mRun, mStartSeed);
		
		mAlgo = mRunner.prepareRunnerStage2(mRun);
		
		System.out.println("Test startet on Thread "+Thread.currentThread().getId());
		
		long startTime = System.currentTimeMillis();
		mAlgo.performEvaluation();
		long elapsedTime = System.currentTimeMillis() - startTime;
		VirtualNetwork stack = mRun.getScenario().getNetworkStack().getVirtualNetworks().get(0);
		SubstrateNetwork snee = mRun.getScenario().getNetworkStack().getSubstrate();
		
		boolean s = mRun.getScenario().getNetworkStack().hasMappings();
		
		mRun.addResult("Runtime", (double)elapsedTime);
		
		mRunner.export(mRun);
	}

}
