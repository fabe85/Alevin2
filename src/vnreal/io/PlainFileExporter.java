package vnreal.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Map;

import tests.TestRun;
import tests.TestSeries;

/**
 * This class exports the parameter and Results in a plain file format
 * 
 * @author Fabian Kokot
 *
 */
public class PlainFileExporter {
	public static  void export(String fileName, TestSeries series) {
		try {
			OutputStream outStream = new FileOutputStream(fileName);
			Writer writer = new OutputStreamWriter(outStream);
			
			//1. Get a list of Parameters
			ArrayList<String> paramList = new ArrayList<String>();
			paramList.addAll(series.getAllTestRuns().get(0).getParameters().keySet());
			
			//2. Get a list of all metrics
			ArrayList<String> metricList = new ArrayList<String>();
			metricList.addAll(series.getAllTestRuns().get(0).getResults().keySet());
			
			//3. Write out the first Line
			int counter = 1;
			writer.write("#"); //make it a commentary
			for(String s : paramList) {
				writer.write(counter+"-Param:"+s+"\t");
				counter++;
			}
			for(String s : metricList) {
				writer.write(counter+"-Metric:"+s+"\t");
				counter++;
			}
			writer.write("\n");
			
			//4. Write out the Data
			for(TestRun tr : series.getAllTestRuns()) {
				//4.1 Print all parameters in right order
				Map<String, Object> paramMap = tr.getParameters();
				for(String paramName : paramList) {
					writer.write(paramMap.get(paramName)+"\t");
				}
				//4.1 Print all parameters in right order
				Map<String, Double> metricMap = tr.getResults();
				for(String metricName : metricList) {
					writer.write(metricMap.get(metricName)+"\t");
				}
				writer.write("\n");
			}
			
			writer.flush();
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
