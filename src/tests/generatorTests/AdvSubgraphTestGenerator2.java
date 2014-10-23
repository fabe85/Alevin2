package tests.generatorTests;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

import tests.AbstractTestGenerator;

/**
 * This class is to set the resources of the substrate network, i.e. the cpu
 * resources for the substrate nodes and the bandwidth resources for the
 * substrate links, and it is supposed to set the demands of the virtual
 * network(s), i.e. the cpu demands for the virtual nodes and the bandwidth
 * resources for the virtual links.
 * 
 * @author baskefab
 *
 */
public class AdvSubgraphTestGenerator2 extends AbstractTestGenerator {

    public AdvSubgraphTestGenerator2() {
        super(GeneratorTestRunner.class, AdvSubgraphTestGenerator2.class
                .getSimpleName(), "tests.generatorTests.AdvSubgraphTestRunner", 1);

        mParams = new ArrayList<SimpleEntry<String, Object[]>>();

        mParams.add(new SimpleEntry<String, Object[]>("S1:Waxman_alpha",
                new Double[] { 0.5d })); // for Waxman Generators
        mParams.add(new SimpleEntry<String, Object[]>("S1:Waxman_beta",
                new Double[] { 0.5d })); // for Waxman Generators
        mParams.add(new SimpleEntry<String, Object[]>("NumVNodesPerNet",
                new Double[] { 5d })); // 5d, 10d, 15d, 20d })); //For
                                       // NetworkGenerators
        mParams.add(new SimpleEntry<String, Object[]>("NumVNets",
                new Double[] { 1d })); // For NetworkGenerators
        mParams.add(new SimpleEntry<String, Object[]>("SNetSize",
                new Double[] { 14d })); // For NetworkGenerators
        mParams.add(new SimpleEntry<String, Object[]>("Min_BW_Dem",
                new Double[] { 10d })); // for Bandwidth Generators
        mParams.add(new SimpleEntry<String, Object[]>("Max_BW_Dem",
                new Double[] { 50d })); // for Bandwidth Generators
        mParams.add(new SimpleEntry<String, Object[]>("Min_BW_Res",
                new Double[] { 10d })); // for Bandwidth Generators
        mParams.add(new SimpleEntry<String, Object[]>("Max_BW_Res",
                new Double[] { 100d })); // for Bandwidth Generators
        
        mParams.add(new SimpleEntry<String, Object[]>("Min_CPU_Dem",
                new Double[] { 10d })); // For CPU generators
        mParams.add(new SimpleEntry<String, Object[]>("Max_CPU_Dem",
                new Double[] { 50d })); // For CPU generators
        mParams.add(new SimpleEntry<String, Object[]>("Min_CPU_Res",
                new Double[] { 10d })); // For CPU generators
        mParams.add(new SimpleEntry<String, Object[]>("Max_CPU_Res",
                new Double[] { 100d })); // For CPU generators
        

    }

}
