package tests.generatorTests;



import mulavito.algorithms.IAlgorithm;
import tests.AbstractTestRunner;
import tests.TestRun;
import tests.generators.demand.EnergyDemandGenerator;
import tests.generators.demand.FixedBandwidthDemandGenerator;
import tests.generators.demand.FixedCostDemandGenerator;
import tests.generators.demand.FixedCpuDemandGenerator;
import tests.generators.demand.StaticEnergyDemandGenerator;
import tests.generators.network.FixedWaxmanNetworkGenerator;
import tests.generators.resource.EnergyResourceGenerator;
import tests.generators.resource.FixedBandwidthResourceGenerator;
import tests.generators.resource.FixedCostResourceGenerator;
import tests.generators.resource.FixedCpuResourceGenerator;
import tests.generators.resource.FixedLinearCpuEnergyResourceGenerator;
import tests.generators.resource.IdResourceGenerator;
import tests.generators.seed.StandardSeedGenerator;
import vnreal.algorithms.isomorphism.AdvancedSubgraphIsomorphismAlgorithm;
import vnreal.algorithms.isomorphism.SubgraphIsomorphismStackAlgorithm;
import vnreal.evaluations.metrics.AcceptedVnrRatio;
import vnreal.evaluations.metrics.AvActiveNodeStress;
import vnreal.evaluations.metrics.AvPathLength;
import vnreal.evaluations.metrics.MaxNodeStress;
import vnreal.evaluations.metrics.RejectedNetworksNumber;
import vnreal.evaluations.metrics.CostRevenue;
import vnreal.io.XMLExporter;
import vnreal.network.NetworkStack;

public class AdvSubgraphTestRunner extends AbstractTestRunner {

	

	public AdvSubgraphTestRunner(XMLExporter exporter) {
		
		super(new StandardSeedGenerator(), new FixedWaxmanNetworkGenerator(), true, exporter);
		// TODO Insert after implementation
		mResGens.add(new EnergyResourceGenerator());
		mResGens.add(new FixedBandwidthResourceGenerator());
		mResGens.add(new FixedCostResourceGenerator());
		mResGens.add(new FixedCpuResourceGenerator());
		mResGens.add(new FixedLinearCpuEnergyResourceGenerator());
		mResGens.add(new IdResourceGenerator());
		
		mDemGens.add(new EnergyDemandGenerator());
		mDemGens.add(new FixedBandwidthDemandGenerator());
		mDemGens.add(new FixedCostDemandGenerator());
		mDemGens.add(new FixedCpuDemandGenerator());
		mDemGens.add(new StaticEnergyDemandGenerator());
		
		//mDemGens.add();
		
		mMetrics.add(new AcceptedVnrRatio());
		mMetrics.add(new RejectedNetworksNumber());
		mMetrics.add(new AvActiveNodeStress());
		mMetrics.add(new MaxNodeStress());
		mMetrics.add(new AvPathLength());
		mMetrics.add(new CostRevenue(false));	
		
	}

	@Override
	protected IAlgorithm prepareRunnerStage2(TestRun tr) {
		NetworkStack ns = tr.getScenario().getNetworkStack();
		
		IAlgorithm algo = new SubgraphIsomorphismStackAlgorithm(ns, 
				new AdvancedSubgraphIsomorphismAlgorithm(false)); 

		return algo;
		
	}

}
