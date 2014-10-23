package tests.generatorTests;

import tests.TestSeries;
import vnreal.io.XMLExporter;
import vnreal.network.NetworkStack;

/**
 * This class is the main class of the test procedure and initializes the
 * generation of the modified virtual networks and the calculation of the
 * resilience metrics after having chosen a virtual network embedding to render
 * the mapping.
 * 
 * @author baskefab
 *
 */
public class AdvSubgraphMain2 {

    /**
     * @param args
     */
    public static void main(String[] args) {
        AdvSubgraphTestGenerator2 gen = new AdvSubgraphTestGenerator2();

        TestSeries series = gen.generateTests();

        XMLExporter exporter = new XMLExporter(
                AdvSubgraphMain2.class.getSimpleName(),
                series.getTestSeriesName(), series.getTestGenerator(), false);

        AdvSubgraphTestRunner2 run = new AdvSubgraphTestRunner2(exporter);

        run.runAllTest(series.getAllTestRuns(), 1);

    }

}
