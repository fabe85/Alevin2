package tests.generatorTests;


import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

import tests.AbstractTestGenerator;


public class AdvSubgraphTestGenerator extends AbstractTestGenerator {
	
	public AdvSubgraphTestGenerator() {
		super(GeneratorTestRunner.class, AdvSubgraphTestGenerator.class.getSimpleName(), "tests.generatorTests.AdvSubgraphTestRunner", 1000);
		
		mParams = new ArrayList<SimpleEntry<String, Object[]>>();
		//S1 is a marker, which lets these parameters treated in one round
		mParams.add(new SimpleEntry<String, Object[]>("S1:Waxman_alpha", new Double[] {0.5d})); //for Waxman Generators
		mParams.add(new SimpleEntry<String, Object[]>("S1:Waxman_beta", new Double[] {0.5d})); //for Waxman Generators
		mParams.add(new SimpleEntry<String, Object[]>("NumVNodesPerNet", new Double[] {10d})); //5d, 10d, 15d, 20d })); //For NetworkGenerators
		mParams.add(new SimpleEntry<String, Object[]>("NumVNets", new Double[] {5d})); //For NetworkGenerators
		mParams.add(new SimpleEntry<String, Object[]>("SNetSize", new Double[] {100d})); //For NetworkGenerators
		mParams.add(new SimpleEntry<String, Object[]>("Min_BW_Dem", new Double[] {1d})); //for Bandwidth Generators
		mParams.add(new SimpleEntry<String, Object[]>("Max_BW_Dem", new Double[] {100d})); //for Bandwidth Generators
		mParams.add(new SimpleEntry<String, Object[]>("Min_BW_Res", new Double[] {1d})); //for Bandwidth Generators
		mParams.add(new SimpleEntry<String, Object[]>("Max_BW_Res", new Double[] {100d})); //for Bandwidth Generators
		mParams.add(new SimpleEntry<String, Object[]>("Min_LinkCost", new Double[] {10d})); //Cost generators
		mParams.add(new SimpleEntry<String, Object[]>("Max_LinkCost", new Double[] {100d})); //Cost generators
		mParams.add(new SimpleEntry<String, Object[]>("Min_CPU_Dem", new Double[] {1d})); //For CPU generators
		mParams.add(new SimpleEntry<String, Object[]>("Max_CPU_Dem", new Double[] {100d})); //For CPU generators
		mParams.add(new SimpleEntry<String, Object[]>("Min_CPU_Res", new Double[] {1d})); //For CPU generators
		mParams.add(new SimpleEntry<String, Object[]>("Max_CPU_Res", new Double[] {100d})); //For CPU generators
		mParams.add(new SimpleEntry<String, Object[]>("Min_LCE_Base", new Double[] {10d})); //For Linear CPU Energy 
		mParams.add(new SimpleEntry<String, Object[]>("Max_LCE_Base", new Double[] {100d})); //For Linear CPU Energy 
		mParams.add(new SimpleEntry<String, Object[]>("Min_LCE_Factor", new Double[] {10d})); //For Linear CPU Energy 
		mParams.add(new SimpleEntry<String, Object[]>("Max_LCE_Factor", new Double[] {100d})); //For Linear CPU Energy 
		mParams.add(new SimpleEntry<String, Object[]>("Min_PDS", new Double[] {10d})); //For PhysicalDemanded and VirtualProvided
		mParams.add(new SimpleEntry<String, Object[]>("Max_PDS", new Double[] {100d})); //For PhysicalDemanded and VirtualProvided
		mParams.add(new SimpleEntry<String, Object[]>("Min_PPS", new Double[] {10d})); //For PhysicalProvided and VirtualDemanded
		mParams.add(new SimpleEntry<String, Object[]>("Max_PPS", new Double[] {100d})); //For PhysicalProvided and VirtualDemanded
		
		
		//mParams.add(new SimpleEntry<String, Double[]>("", min));
		//mParams.add(new SimpleEntry<String, Double[]>("", max));
		
	}

}
