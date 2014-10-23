package tests.parallel;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tests.scenarios.AbstractScenarioTest.TestConfiguration;

public class SubgraphRunner {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		//Run as many threads as we have CPU's
		//int cpus = (int) (Runtime.getRuntime().availableProcessors());
		
		//TODO: Make ALEVIN Thread save and remove all static objects and so on
		//As long as there are that much static attributes and so on we can only allow one thread per host
		int cpus = 1;
		ExecutorService executor = Executors.newFixedThreadPool(cpus);
		
		TestConfiguration c = new TestConfiguration();
		
		
		// Get ScenarioData
		int size = c.numSNodesToRemoveArray.length *
				c.numScenarios *
				c.numSNodesArray.length *
				c.numVNetsArray.length *
				c.numVNodesPerVNetArray.length *
				c.betaArray.length *
				c.alphaArray.length;
		
		int begin = 0;
		int end = size - 1;
		
		//IMPORTANT: One parameter means return number of tests -1 
		if(args.length == 1) {
			System.out.println(end);
			return;
		}
		else if(args.length != 2) {
			System.out.println("Usage: MetisRunner startNumber endNumber");
			return;
		}
		
		
		try {
			int cBegin = Integer.parseInt(args[0]);
			int cEnd = Integer.parseInt(args[1]);
			
			if(cBegin < 0 || cBegin > cEnd  || cEnd > end) {
				System.out.println("hello " +cBegin + " "+cEnd);
				throw new Exception();
			}
			
			begin = cBegin;
			end = cEnd;
		}
		catch (Exception e) {
			System.out.println("Both parameter have to be positiv integers (starts with 0) and endNumber has to be greater or out of range");
			return;
		}
		
		for(int i = begin; i <= end; i++) {
			executor.execute(new AdvancedParallelSubgraphTest(c, AdvancedParallelSubgraphTest.class.getName(), begin, end));
		}
		
		executor.shutdown();
	}

}
