package tests.generatorTests;

import tests.TestSeries;
import vnreal.io.XMLExporter;
import vnreal.io.XMLImporter;

public class TestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GeneratorTestGenerator gen = new GeneratorTestGenerator("lalala");
		
		TestSeries series = gen.generateTests();
		
		XMLExporter exporter = new XMLExporter("C:/Dokumente und Einstellungen/kokot/Desktop/Test_gen10.xml", series.getTestSeriesName(), series.getTestGenerator(), false);
		
		GeneratorTestRunner run = new GeneratorTestRunner(exporter);
		
		run.runAllTest(series.getAllTestRuns(), 5);
		
		//XMLExporter.exportResult("C:/Dokumente und Einstellungen/kokot/Desktop/Test_gen10.xml", series, false);
		
		//System.out.println(series.getAllTestRuns().size());
		
		
//		TestSeries series2 = XMLImporter.importResults("C:/Dokumente und Einstellungen/kokot/Desktop/Test_gen10.xml");
//		
//		System.out.println(series2.getAllTestRuns().size());

	}

}
