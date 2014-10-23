package tests;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import mulavito.algorithms.IAlgorithm;



import tests.generators.AbstractGenerator;
import tests.generators.GeneratorParameter;
import tests.generators.demand.AbstractDemandGenerator;
import tests.generators.demand.NullDemandGenerator;
import tests.generators.network.AbstractNetworkGenerator;
import tests.generators.resource.AbstractResourceGenerator;
import tests.generators.resource.NullResourceGenerator;
import tests.generators.seed.AbstractSeedGenerator;
import vnreal.Scenario;
import vnreal.evaluations.metrics.EvaluationMetric;
import vnreal.io.XMLExporter;
import vnreal.network.NetworkStack;
import vnreal.network.virtual.VirtualNetwork;

/**
 * This is the base class of a runner class which is running the real tests based 
 * on a {@link TestSeries}
 * 
 * @author Fabian Kokot
 *
 */
public abstract class AbstractTestRunner {
	
	
	protected ArrayList<AbstractResourceGenerator<? extends Object>> mResGens = new ArrayList<AbstractResourceGenerator<? extends Object>>();
	protected ArrayList<AbstractDemandGenerator<? extends Object>> mDemGens = new ArrayList<AbstractDemandGenerator<? extends Object>>();
	protected AbstractSeedGenerator mSeedGen;
	protected AbstractNetworkGenerator mNetGen;
	protected ArrayList<EvaluationMetric> mMetrics = new ArrayList<EvaluationMetric>();
	private final boolean mBidirectionalLinks;
	private final XMLExporter mExporter;
	
	
	/**
	 * 
	 * 
	 * @param mSeedGen seed generator (may null)
	 * @param mNetGen Network generator
	 * @param bidirectionalLinks true if links should be biderectional
	 * @param exporter {@link XMLExporter} which directly exports the TestRuns
	 */
	public AbstractTestRunner(
			AbstractSeedGenerator mSeedGen, AbstractNetworkGenerator mNetGen, boolean bidirectionalLinks, 
			XMLExporter exporter) {
		super();
		
		//We always have a default resource and demand
		mResGens.add(new NullResourceGenerator());
		mDemGens.add(new NullDemandGenerator());
		
		this.mSeedGen = mSeedGen;
		this.mNetGen = mNetGen;
		this.mBidirectionalLinks = bidirectionalLinks;
		this.mExporter = exporter;
	}


	/**
	 * This method runs ALL tests in the series
	 * 
	 * @param series TestSeries which should be used
	 * @param numThreads Number of parallel threads
	 */
	public void runAllTest(ArrayList<TestRun> trList, int numThreads) {
		runTests(trList, numThreads, 0, trList.size()-1);
	}
	
	
	/**
	 * This Method will run the tests with the given range
	 * 
	 * @param series TestSeries which should be used
	 * @param numThreads Number of parallel threads
	 * @param startNumber Number of the first Test
	 * @param endNumber Number of the last test
	 */
	public void runTests(ArrayList<TestRun> trList, int numThreads, int startNumber, int endNumber) {
		ExecutorService executor = Executors.newFixedThreadPool(numThreads);
		
		//Create the tests and add them to the executor
		for(long actTest = startNumber; actTest <= endNumber; actTest++) {
			TestRun tr = trList.get((int)actTest);
			
			Runnable run = new TestRunnable(tr, this, actTest*1000l);
			
			executor.execute(run);
		}
		
		executor.shutdown();
		
		trList.clear();
		trList = null;
		
		
		try {
				
			//Should be enough to finish any run
			executor.awaitTermination(10000, TimeUnit.HOURS);
			
			mExporter.finishWriting();
				
				
		} catch (InterruptedException e) {
			e.printStackTrace();
			executor.shutdownNow();
			if(mExporter != null)
				mExporter.finishWriting();
			throw new Error("Execution of one test failed, STOP");
		}
		
	}
	
	
	/**
	 * This method prepares the TestRun
	 * 
	 * @param tr The {@link TestRun}
	 * @return {@link Runnable}
	 */
	public void prepareTestStage1(TestRun tr, Long startSeed) {
		synchronized (mNetGen) {
			if(mSeedGen != null)
				mSeedGen.setStartSeed(startSeed);
			LinkedHashMap<String, Object> genResults = new LinkedHashMap<String, Object>();
			
			//2. Generate Networks
			ArrayList<Object> params = genParamList(mNetGen, tr, null, genResults);
			NetworkStack ns = mNetGen.generate(params);
			
			//3. Generate resources
			for(AbstractResourceGenerator<? extends Object> arGen : mResGens) {
				genResults.put(arGen.getClass().getName(), arGen.generate(genParamList(arGen, tr, ns, genResults)));
			}
			//4. Generate demands
			for(AbstractDemandGenerator<? extends Object> adGen : mDemGens) {
				genResults.put(adGen.getClass().getName(), adGen.generate(genParamList(adGen, tr, ns, genResults)));
			}
			
			if(mBidirectionalLinks) {
				ns.getSubstrate().generateDuplicateEdges();
				
				for(int i = 1; i < ns.size(); i++) {
					VirtualNetwork n = (VirtualNetwork)ns.getLayer(i);
					n.generateDuplicateEdges();
					
				}
			}

			//add the Stack to the TestRun
			Scenario scen = new Scenario();
			scen.setNetworkStack(ns);
			tr.setScenario(scen);
			
			//last Reset seed generator and results
			if(mSeedGen != null)
				mSeedGen.reset();
			mNetGen.reset();
		}

	}

	
	
	

	/**
	 * This Method is to create the algorithm
	 * 
	 * @param ns The current {@link NetworkStack}
	 * @param tr the current {@link TestRun}
	 */
	abstract protected IAlgorithm prepareRunnerStage2(TestRun tr);


	/**
	 * This Method extracts the information from the annotations and creates the list of Parameters
	 * 
	 * @param generator Generator
	 * @param tr Current TestRun
	 * @return List of Parameters or null if none needed
	 */
	private ArrayList<Object> genParamList(AbstractGenerator<? extends Object> generator, TestRun tr, NetworkStack ns, LinkedHashMap<String, Object> genResults) {
		ArrayList<Object> outList = new ArrayList<Object>();
		
		//1. Look up whether there is an Annotation
		GeneratorParameter gp = generator.getClass().getAnnotation(GeneratorParameter.class);
		
		//We don't have a annotation -> no parameters needed
		if(gp == null)
			return null;
		
		//2. Extract the Parameters
		String[] anParameters = gp.parameters();
		for(String anParameter : anParameters) {
			String[] splitParam = anParameter.split(":");
			if(splitParam.length != 2){
				throw new Error("Annotation is no Valid: "+anParameter);
			}
			
			if(splitParam[0].equals("Seed")) {
				if(mSeedGen == null)
					throw new Error("A generator requests a seed, but no SeedGenerator is set, Aborting.");
				outList.add(mSeedGen.generate(genParamList(mSeedGen, tr, ns, genResults)));
			} else if(splitParam[0].equals("Networks")) {
				outList.add(ns);
			} else if(splitParam[0].equals("TR")) {
				outList.add(getParameterFromRun(tr, splitParam[1]));
			} else if(splitParam[0].equals("Result")) {
				outList.add(getParameterFromResult(splitParam[1], genResults));
			} else if(splitParam[0].equals("Method")) {
				outList.add(getParameterFromMethod(splitParam[1]));
			} else if(splitParam[0].equals("SMethod")) {
				outList.add(getParameterFromStaticMethod(splitParam[1]));
			} else
				throw new Error("Annotation syntax for generator "+generator.getClass().getName()+" is wrong:"+anParameter );
			
		}
		return outList;
	}




	/**
	 * Returns a Value from the TestRun by name
	 * 
	 * @param tr {@link TestRun}
	 * @param paramName Name of the Parameter
	 * @return Object from Type {@link Double}
	 */
	protected Object getParameterFromRun(TestRun tr, String paramName) {
		for(Entry<String, Object> se : tr.getParameters().entrySet()) {
			if(se.getKey().equals(paramName)) {
				return se.getValue();
			}
		}
		throw new Error("The parameter "+paramName+" does no exist in the current TestRun.");
	}
	
	
	/**
	 * Get the result from the specified generator
	 * 
	 * @param generatorName
	 * @return
	 */
	private Object getParameterFromResult(String generatorName, LinkedHashMap<String, Object> genResults) {
		for(String s : genResults.keySet()) {
			if(s.equals(generatorName)) {
				return genResults.get(s);
			}
		}
		throw new Error("There are no results for the "+generatorName);
	}
	

	/**
	 * Get the result from a method of a generator object <br/>
	 * DANGER: This may lead to a wrong result when old values are used
	 * 
	 * @param combOption Combined String 
	 * @return Object
	 */
	private Object getParameterFromMethod(String combOption) {
		String[] option = combOption.split("|");
		if(option.length != 2){
			throw new Error("The input from for get by Method is wrong: "+combOption);
		}
		
		if(mSeedGen.getClass().getName().equals("class "+option[0]))
			return callMethod(mSeedGen, option[1]);
		
		if(mNetGen.getClass().getName().equals("class "+option[0]))
			return callMethod(mNetGen, option[1]);
		
		for(Object o : mResGens) {
			if(o.getClass().getName().equals("class "+option[0])) {
				return callMethod(o, option[1]);
			}
		}
		for(Object o : mDemGens) {
			if(o.getClass().getName().equals("class "+option[0])) {
				return callMethod(o, option[1]);
			}
		}
		
		throw new Error("Can't find a object from class "+option[0]);
	}


	/**
	 * This Method simply tries to call a method
	 * 
	 * @param o Object
	 * @param methodName Name of the method
	 * @return 
	 */
	private Object callMethod(Object o, String methodName) {
		//Get the Methods
		Method[] mets = o.getClass().getMethods();
		for(Method m : mets) {
			if(m.getName().equals(methodName)) {
				try {
					return m.invoke((Object[])null, (Object[])null);
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					throw new Error("The method "+methodName+" on class "+o.getClass().getName()+" is not call able");
				}
			}
		}
		throw new Error("There is no such Method with the name "+methodName+" in "+o.getClass().getName());
	}
	
	
	/**
	 * This Method is able to call all Static Methods available..
	 * (Maybe Dangerous?)
	 * 
	 * @param options Input from the Annotation
	 * @return Object
	 */
	private Object getParameterFromStaticMethod(String combOption) {
		String[] option = combOption.split("|");
		if(option.length != 2){
			throw new Error("The input from for get by static method is wrong: "+combOption);
		}
		
		try {
			Class<? extends Object> c = Class.forName(option[0]);
			for(Method m : c.getMethods()) {
				if(m.getName().equals(option[1])) {
					return m.invoke((Object[])null, (Object[])null);
				}
			}
			throw new Error("There is no such Method with the name "+option[1]+" in "+c.getName());
		} catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			throw new Error("The method "+option[1]+" on class "+option[0]+" is not call able");
		}
	}
	
	/**
	 * This method exports the TestRun
	 * 
	 * @param tr
	 */
	public void export(TestRun tr) {
		NetworkStack ns = tr.getScenario().getNetworkStack();
		
		synchronized (mMetrics) {
			for (EvaluationMetric metric : mMetrics) {
				metric.setStack(ns);
				double y = metric.getValue();
				tr.addResult(metric.getClass().getSimpleName(), y);
			}
		}
		
		HashMap<String, Double> algoData = tr.getScenario().getNetworkStack().getEvaluationData();
		for(Entry<String, Double> e : algoData.entrySet()) {
			tr.addResult(e.getKey(), e.getValue());
		}
		
		mExporter.exportTestRun(tr);
	}

}