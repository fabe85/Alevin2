package tests.generatorTests;



import mulavito.algorithms.IAlgorithm;

import tests.AbstractTestRunner;
import tests.ConversionHelper;
import tests.TestRun;
import tests.generators.demand.EnergyDemandGenerator;
import tests.generators.demand.FixedBandwidthDemandGenerator;
import tests.generators.demand.FixedCostDemandGenerator;
import tests.generators.demand.FixedCpuDemandGenerator;
import tests.generators.demand.FixedVirtualDemandedSecurityResourceGenerator;
import tests.generators.demand.FixedVirtualProvidedSecurityResourceGenerator;
import tests.generators.demand.ReasonableFixedMLSDemandGenerator;
import tests.generators.demand.StaticEnergyDemandGenerator;
import tests.generators.network.FixedWaxmanNetworkGenerator;
import tests.generators.resource.EnergyResourceGenerator;
import tests.generators.resource.FixedBandwidthResourceGenerator;
import tests.generators.resource.FixedCostResourceGenerator;
import tests.generators.resource.FixedCpuResourceGenerator;
import tests.generators.resource.FixedLinearCpuEnergyResourceGenerator;
import tests.generators.resource.FixedMLSResourceGenerator;
import tests.generators.resource.FixedMultiCoreEnergyResourceGenerator;
import tests.generators.resource.FixedPhysicalDemandedSecurityResourceGenerator;
import tests.generators.resource.FixedPhysicalProvidedSecurityResourceGenerator;
import tests.generators.resource.IdResourceGenerator;
import tests.generators.seed.StandardSeedGenerator;
import vnreal.algorithms.MLSAlgorithm;
import vnreal.algorithms.linkmapping.kShortestPathLinkMapping;
import vnreal.algorithms.mls.MlsNodeMapping_BestFit;
import vnreal.evaluations.metrics.AcceptedVnrRatio;
import vnreal.evaluations.metrics.AvActiveNodeStress;
import vnreal.evaluations.metrics.AvMLSSecDiffDemProv;
import vnreal.evaluations.metrics.AvMLSSecDiffProvDem;
import vnreal.evaluations.metrics.AvMLSSecDiffVDem;
import vnreal.evaluations.metrics.AvMLSSecDiffVProv;
import vnreal.evaluations.metrics.AvPathLength;
import vnreal.evaluations.metrics.MaxMLSSecDiffDemProv;
import vnreal.evaluations.metrics.MaxMLSSecDiffProvDem;
import vnreal.evaluations.metrics.MaxMLSSecDiffVDem;
import vnreal.evaluations.metrics.MaxMLSSecDiffVProv;
import vnreal.evaluations.metrics.MaxNodeStress;
import vnreal.evaluations.metrics.RejectedNetworksNumber;
import vnreal.io.XMLExporter;
import vnreal.network.NetworkStack;

public class GeneratorTestRunner extends AbstractTestRunner {

	

	public GeneratorTestRunner(XMLExporter exporter) {
		
		super(new StandardSeedGenerator(), new FixedWaxmanNetworkGenerator(), true, exporter);
		// TODO Insert after implementation
		mResGens.add(new FixedMLSResourceGenerator());
		mResGens.add(new EnergyResourceGenerator());
		mResGens.add(new FixedBandwidthResourceGenerator());
		mResGens.add(new FixedCostResourceGenerator());
		mResGens.add(new FixedCpuResourceGenerator());
		mResGens.add(new FixedLinearCpuEnergyResourceGenerator());
		mResGens.add(new FixedMultiCoreEnergyResourceGenerator());
		mResGens.add(new FixedPhysicalDemandedSecurityResourceGenerator());
		mResGens.add(new FixedPhysicalProvidedSecurityResourceGenerator());
		mResGens.add(new IdResourceGenerator());
		
		mDemGens.add(new ReasonableFixedMLSDemandGenerator());
		mDemGens.add(new EnergyDemandGenerator());
		mDemGens.add(new FixedBandwidthDemandGenerator());
		mDemGens.add(new FixedCostDemandGenerator());
		mDemGens.add(new FixedCpuDemandGenerator());
		mDemGens.add(new FixedVirtualDemandedSecurityResourceGenerator());
		mDemGens.add(new FixedVirtualProvidedSecurityResourceGenerator());
		mDemGens.add(new StaticEnergyDemandGenerator());
		
		//mDemGens.add();
		
		mMetrics.add(new AcceptedVnrRatio());
		mMetrics.add(new RejectedNetworksNumber());
		mMetrics.add(new AvActiveNodeStress());
		mMetrics.add(new MaxNodeStress());
		mMetrics.add(new AvPathLength());
		mMetrics.add(new AvMLSSecDiffDemProv());
		mMetrics.add(new MaxMLSSecDiffDemProv());
		mMetrics.add(new AvMLSSecDiffProvDem());
		mMetrics.add(new MaxMLSSecDiffProvDem());
		mMetrics.add(new MaxMLSSecDiffVDem());
		mMetrics.add(new MaxMLSSecDiffVProv());
		mMetrics.add(new AvMLSSecDiffVDem());
		mMetrics.add(new AvMLSSecDiffVProv());
		
		
	}

	@Override
	protected IAlgorithm prepareRunnerStage2(TestRun tr) {
		NetworkStack ns = tr.getScenario().getNetworkStack();
		
		IAlgorithm algo = new MLSAlgorithm(ns, 
				new MlsNodeMapping_BestFit(ns.getSubstrate(), false, ConversionHelper.paramObjectToInteger(getParameterFromRun(tr, "MLS_Theta"))), 
				new kShortestPathLinkMapping(ns.getSubstrate(), ConversionHelper.paramObjectToInteger(getParameterFromRun(tr, "kShortestPath"))));

		return algo;
		
	}

}
