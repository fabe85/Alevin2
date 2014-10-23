package plot;

import tests.TestSeries;
import vnreal.io.XMLImporter;

public class PlotMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestSeries series2 = XMLImporter.importResults("C:/Dokumente und Einstellungen/kokot/Desktop/Test_gen9.xml");
		
		Plotter p = new Plotter(series2);
		//p.applyFilterEqualOrGreater("MLS_Theta", 1.1);
		//p.applyFilterEquals("MLS_NumCategories", 6);
		p.applyFilterEquals("AcceptedVnrRatio", 0.0);
		
		p.output2D("Plot2d", "MLS_Theta", "AvMLSSecDiffProvDem");
		p.output3D("Plot3d", "MLS_Theta", "MLS_SecLevels", "AvMLSSecDiffProvDem");
	}

}
