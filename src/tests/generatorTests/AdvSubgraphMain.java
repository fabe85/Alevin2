package tests.generatorTests;

import tests.TestSeries;
import vnreal.io.XMLExporter;

public class AdvSubgraphMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AdvSubgraphTestGenerator gen = new AdvSubgraphTestGenerator();
		
		TestSeries series = gen.generateTests();
		
		XMLExporter exporter = new XMLExporter(AdvSubgraphMain.class.getSimpleName(), series.getTestSeriesName(), series.getTestGenerator(), false);
		
		AdvSubgraphTestRunner run = new AdvSubgraphTestRunner(exporter);
		
		run.runAllTest(series.getAllTestRuns(), 1);
		
		
		//XMLExporter.exportResult("C:/Dokumente und Einstellungen/kokot/Desktop/Test_gen10.xml", series, false);
		
		//System.out.println(series.getAllTestRuns().size());
		
		
//		TestSeries series2 = XMLImporter.importResults("C:/Dokumente und Einstellungen/kokot/Desktop/Test_gen10.xml");
//		
//		System.out.println(series2.getAllTestRuns().size());

	}

}
