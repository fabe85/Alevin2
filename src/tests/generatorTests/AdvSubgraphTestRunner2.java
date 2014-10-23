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
import tests.generators.network.FixedWaxmanNetworkGenerator2;
import tests.generators.resource.EnergyResourceGenerator;
import tests.generators.resource.FixedBandwidthResourceGenerator;
import tests.generators.resource.FixedCostResourceGenerator;
import tests.generators.resource.FixedCpuResourceGenerator;
import tests.generators.resource.FixedLinearCpuEnergyResourceGenerator;
import tests.generators.resource.IdResourceGenerator;
import tests.generators.seed.StandardSeedGenerator;
import vnreal.algorithms.AvailableResources;
import vnreal.algorithms.CoordinatedMappingkShortestPath;
import vnreal.algorithms.isomorphism.AdvancedSubgraphIsomorphismAlgorithm;
import vnreal.algorithms.isomorphism.SubgraphIsomorphismStackAlgorithm;
import vnreal.evaluations.metrics.AcceptedVnrRatio;
import vnreal.evaluations.metrics.AvActiveNodeStress;
import vnreal.evaluations.metrics.AvPathLength;
import vnreal.evaluations.metrics.AlgebraicConnectivity;
import vnreal.evaluations.metrics.AlgebraicConnectivity0;
import vnreal.evaluations.metrics.AlgebraicConnectivity2;
import vnreal.evaluations.metrics.EdgeConnectivity;
import vnreal.evaluations.metrics.EdgeConnectivity0;
import vnreal.evaluations.metrics.EdgeConnectivity2;
import vnreal.evaluations.metrics.FabianianCoefficient;
import vnreal.evaluations.metrics.FabianianCoefficient0;
import vnreal.evaluations.metrics.FabianianCoefficient2;
import vnreal.evaluations.metrics.ResilienceFactor;
import vnreal.evaluations.metrics.ResilienceFactor0;
import vnreal.evaluations.metrics.ResilienceFactor2;
import vnreal.evaluations.metrics.SpectralGap;
import vnreal.evaluations.metrics.SpectralGap0;
import vnreal.evaluations.metrics.SpectralGap2;
import vnreal.evaluations.metrics.StochasticMetric;
import vnreal.evaluations.metrics.StochasticMetric0;
import vnreal.evaluations.metrics.StochasticMetric2;
import vnreal.evaluations.metrics.VertexConnectivity;
import vnreal.evaluations.metrics.VertexConnectivity0;
import vnreal.evaluations.metrics.VertexConnectivity2;
import vnreal.evaluations.metrics.MaxNodeStress;
import vnreal.evaluations.metrics.RejectedNetworksNumber;
import vnreal.evaluations.metrics.CostRevenue;
import vnreal.io.XMLExporter;
import vnreal.network.NetworkStack;

/**
 * This class initializes the respective virtual network embedding algorithm and
 * determines which metrics are to be calculated.
 * 
 * @author baskefab
 *
 */
public class AdvSubgraphTestRunner2 extends AbstractTestRunner {

    public AdvSubgraphTestRunner2(XMLExporter exporter) {

        super(new StandardSeedGenerator(), new FixedWaxmanNetworkGenerator2(),
                true, exporter);
        
        
        mResGens.add(new FixedBandwidthResourceGenerator());
        
        mResGens.add(new FixedCpuResourceGenerator());
        
        mResGens.add(new IdResourceGenerator());

        
        mDemGens.add(new FixedBandwidthDemandGenerator());
        
        mDemGens.add(new FixedCpuDemandGenerator());
        

        mMetrics.add(new AcceptedVnrRatio());
        mMetrics.add(new RejectedNetworksNumber());
        mMetrics.add(new AvActiveNodeStress());
        mMetrics.add(new MaxNodeStress());
        mMetrics.add(new AvPathLength());
        mMetrics.add(new CostRevenue(false));

        //results of the mapped and modified virtual network
        mMetrics.add(new VertexConnectivity2());
        mMetrics.add(new AlgebraicConnectivity2());
        mMetrics.add(new EdgeConnectivity2());
        mMetrics.add(new FabianianCoefficient2());
        mMetrics.add(new ResilienceFactor2());
        mMetrics.add(new SpectralGap2());
        mMetrics.add(new StochasticMetric2());

        /*
         * mMetrics.add(new VertexConnectivity()); mMetrics.add(new
         * AlgebraicConnectivity()); mMetrics.add(new EdgeConnectivity());
         * mMetrics.add(new FabianianCoefficient()); mMetrics.add(new
         * ResilienceFactor()); mMetrics.add(new SpectralGap());
         * mMetrics.add(new StochasticMetric());
         */

        //results of the original virtual network without mapping 
        mMetrics.add(new VertexConnectivity0());
        mMetrics.add(new AlgebraicConnectivity0());
        mMetrics.add(new EdgeConnectivity0());
        mMetrics.add(new FabianianCoefficient0());
        mMetrics.add(new ResilienceFactor0());
        mMetrics.add(new SpectralGap0());
        mMetrics.add(new StochasticMetric0());
    }

    @Override
    protected IAlgorithm prepareRunnerStage2(TestRun tr) {
        NetworkStack ns = tr.getScenario().getNetworkStack();

        IAlgorithm algo = new SubgraphIsomorphismStackAlgorithm(ns,
                new AdvancedSubgraphIsomorphismAlgorithm(false));

        IAlgorithm algo2 = new CoordinatedMappingkShortestPath(ns, 1, 100, 100,
                0, 1, false);

        IAlgorithm algo3 = new AvailableResources(ns, 3, 1, false, false);

        return algo2; //three types of virtual network embedding algorithms can be chosen

    }

}
