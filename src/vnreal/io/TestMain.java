package vnreal.io;

import tests.TestSeries;

public class TestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		TestSeries moep = XMLImporter.importResults("src/XML/Test_new.xml");
//
//		System.out.println("Name: "+moep.getTestSeriesName()+"; Generator: "+moep.getTestGenerator());
//		System.out.println("Number Results: "+moep.getAllTestRuns().size());
//		for(TestRun t : moep.getAllTestRuns()) {
//			System.out.println(t.getParameters());
//			System.out.println(t.getResults());
//			System.out.println("---------------------");
//			
//			Scenario scen = t.getScenario();
//			if(scen != null) {
//				NetworkStack stack = scen.getNetworkStack();
//				System.out.println(stack);
//			}
//		}
		
//		Scenario pScen = XMLImporter.importScenario("src/XML/exemplary-scenario-mappings-and-hiddenhops.xml");
//		System.out.println(pScen.getNetworkStack());
		
//		XMLExporter.exportResult("src/XML/Test_export.xml", moep, true);
//		
//		
//		XMLExporter.exportScenario("src/XML/Test_export2.xml", pScen);

//		Class c = MLSDemand.class;
//		Method[] mets = c.getMethods();
//		for(Method m : mets) {
//			System.out.println(m.getName()+": "+m.getReturnType().toString());
//		}
//		
		TestSeries series = XMLImporter.importResults("C:/Dokumente und Einstellungen/kokot/Desktop/Test_gen9.xml");
		PlainFileExporter.export("C:/Dokumente und Einstellungen/kokot/Desktop/Test_gen11", series);
	}

}
