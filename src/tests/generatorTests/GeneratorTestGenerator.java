package tests.generatorTests;


import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

import tests.AbstractTestGenerator;


public class GeneratorTestGenerator extends AbstractTestGenerator {
	
	public static final Double[] numSNodesArray = { 100d };
	protected static final Double[] numVNodesPerVNetArray = { 5d };
	//protected static final Double[] numVNodesPerVNetArray = { 15d };
	public static final Double[] numVNetsArray = { 5d};
	public static final Double[] secLevels = {5d, 5d, 17d};
	public static final Double[] secCategories = {2d, 6d, 2d};
	public static final Double[] thetaArray = {1d};
	public static final Double[] kShortestPath = {3d};
	public static final Double[] min = {10d};
	public static final Double[] max = {100d};
	
	
	
	public GeneratorTestGenerator(String seriesName) {
		super(GeneratorTestRunner.class, seriesName, "tests.generatorTests.GeneratorTestRunner", 7);
		
		mParams = new ArrayList<SimpleEntry<String, Object[]>>();
		//S1 is a marker, which lets these parameters treated in one round
		mParams.add(new SimpleEntry<String, Object[]>("S1:Waxman_alpha", new Double[] {0.5d})); //for Waxman Generators
		mParams.add(new SimpleEntry<String, Object[]>("S1:Waxman_beta", new Double[] {0.5d})); //for Waxman Generators
		mParams.add(new SimpleEntry<String, Object[]>("kShortestPath", kShortestPath)); //for kShortesPath
		mParams.add(new SimpleEntry<String, Object[]>("NumVNodesPerNet", numVNodesPerVNetArray)); //For NetworkGenerators
		mParams.add(new SimpleEntry<String, Object[]>("NumVNets", numVNetsArray)); //For NetworkGenerators
		mParams.add(new SimpleEntry<String, Object[]>("SNetSize", numSNodesArray)); //For NetworkGenerators
		mParams.add(new SimpleEntry<String, Object[]>("MLS_Theta", thetaArray)); //For MLS Generators
		//S2 is a marker too, so only the pairs (levels, cats) -> (5,2),(5,6),(17,2) will be generated
		mParams.add(new SimpleEntry<String, Object[]>("S2:MLS_SecLevels", secLevels)); //For MLS Generators
		mParams.add(new SimpleEntry<String, Object[]>("S2:MLS_NumCategories", secCategories)); //For MLS Generators
		mParams.add(new SimpleEntry<String, Object[]>("Min_BW_Dem", min)); //for Bandwith Generators
		mParams.add(new SimpleEntry<String, Object[]>("Max_BW_Dem", max)); //for Bandwith Generators
		mParams.add(new SimpleEntry<String, Object[]>("Min_BW_Res", min)); //for Bandwith Generators
		mParams.add(new SimpleEntry<String, Object[]>("Max_BW_Res", max)); //for Bandwith Generators
		mParams.add(new SimpleEntry<String, Object[]>("Min_LinkCost", min)); //Cost generatorts
		mParams.add(new SimpleEntry<String, Object[]>("Max_LinkCost", max)); //Cost generatorts
		mParams.add(new SimpleEntry<String, Object[]>("Min_CPU_Dem", min)); //For CPU generators
		mParams.add(new SimpleEntry<String, Object[]>("Max_CPU_Dem", max)); //For CPU generators
		mParams.add(new SimpleEntry<String, Object[]>("Min_CPU_Res", min)); //For CPU generators
		mParams.add(new SimpleEntry<String, Object[]>("Max_CPU_Res", max)); //For CPU generators
		mParams.add(new SimpleEntry<String, Object[]>("Min_LCE_Base", min)); //For Linear CPU Energy 
		mParams.add(new SimpleEntry<String, Object[]>("Max_LCE_Base", max)); //For Linear CPU Energy 
		mParams.add(new SimpleEntry<String, Object[]>("Min_LCE_Factor", min)); //For Linear CPU Energy 
		mParams.add(new SimpleEntry<String, Object[]>("Max_LCE_Factor", max)); //For Linear CPU Energy 
		mParams.add(new SimpleEntry<String, Object[]>("Min_MCE_Idle", min)); //For MultoCoreEnergy
		mParams.add(new SimpleEntry<String, Object[]>("Max_MCE_Idle", max)); //For MultoCoreEnergy
		mParams.add(new SimpleEntry<String, Object[]>("Min_MCE_Additional", min)); //For MultoCoreEnergy
		mParams.add(new SimpleEntry<String, Object[]>("Max_MCE_Additional", max)); //For MultoCoreEnergy
		mParams.add(new SimpleEntry<String, Object[]>("Min_MCE_Cores", min)); //For MultoCoreEnergy
		mParams.add(new SimpleEntry<String, Object[]>("Max_MCE_Cores", max)); //For MultoCoreEnergy
		mParams.add(new SimpleEntry<String, Object[]>("Min_PDS", min)); //For PhysicalDemanded and VirtualProvided
		mParams.add(new SimpleEntry<String, Object[]>("Max_PDS", max)); //For PhysicalDemanded and VirtualProvided
		mParams.add(new SimpleEntry<String, Object[]>("Min_PPS", min)); //For PhysicalProvided and VirtualDemanded
		mParams.add(new SimpleEntry<String, Object[]>("Max_PPS", max)); //For PhysicalProvided and VirtualDemanded
		
		
		//mParams.add(new SimpleEntry<String, Double[]>("", min));
		//mParams.add(new SimpleEntry<String, Double[]>("", max));
		
	}

}
