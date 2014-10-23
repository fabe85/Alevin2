package plot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import jsc.onesample.NormalMeanCI;

import tests.TestRun;
import tests.TestSeries;

/**
 * A simple plotter which creates a simple gnuplot-file and the corresponding 
 * data-files
 * 
 * @author Fabian Kokot
 *
 */
public class Plotter {

	private final ArrayList<TestRun> mRuns;
	
	
	
	/**
	 *
	 * @param series The {@link TestSeries} which contains all results
	 */
	public Plotter(TestSeries series) {
		this.mRuns = series.getAllTestRunsCopy();
		
		//Remove unnecessary informations (networks)
		for(TestRun tr : mRuns)
			tr.setScenario(null);
	}
	
	/*
	 * 1. Filtermethods
	 */
	
	
	/**
	 * This Method returns a Map with the {@link TestRun} and the value of the given fieldName
	 * 
	 * @param fieldName Name of the field
	 * @return Map of the values and Runs
	 */
	private Map<TestRun, Object> filterHelper(String fieldName) {
		
		HashMap<TestRun, Object> resMap = new HashMap<TestRun, Object>();
		
		for(TestRun tr : mRuns) {
			
			//Get the values for the specified field		
			Object rVal = tr.getParameters().get(fieldName);
			
			if(rVal == null) {
				rVal = tr.getResults().get(fieldName);
			}
			if(rVal == null)
				throw new Error("There is no such field in paramters or results");
			
			resMap.put(tr, rVal);
			
		}
		
		return resMap;
	}
	
	
	/**
	 * This Method removes all results ({@link TestRun}) which are equals (=)  the given value  
	 * 
	 * @param fieldName Name of the parameter or metric
	 * @param value The target-value which is matched matched
	 */
	public void applyFilterEquals(String fieldName, Object value) {
		
		Map<TestRun, Object> map = filterHelper(fieldName);
			
		//Now match and remove
		for(Entry<TestRun, Object> rVal : map.entrySet()) {
			if(rVal.getValue() instanceof Double && value instanceof Double) {
				Double dVal = (Double)rVal.getValue();
				if(dVal.doubleValue() == (Double)value)
					mRuns.remove(rVal.getKey());
			}
			else if (rVal.getValue() instanceof String && value instanceof String) {
				String sVal = (String)rVal.getValue();
				if(sVal.equals(value))
					mRuns.remove(rVal.getKey());
			}
		}
			
	}
	
	/**
	 * This Method removes all results ({@link TestRun}) which are not equals (!=)  the given value  
	 * 
	 * @param fieldName Name of the parameter or metric
	 * @param value The target-value which is matched matched
	 */
	public void applyFilterNotEquals(String fieldName, Object value) {
		
		Map<TestRun, Object> map = filterHelper(fieldName);
		
		//Now match and remove
		for(Entry<TestRun, Object> rVal : map.entrySet()) {
			if(rVal.getValue() instanceof Double && value instanceof Double) {
				Double dVal = (Double)rVal.getValue();
				if(dVal.doubleValue() != (Double)value)
					mRuns.remove(rVal.getKey());
			}
			else if (rVal.getValue() instanceof String && value instanceof String) {
				String sVal = (String)rVal.getValue();
				if(!sVal.equals(value))
					mRuns.remove(rVal.getKey());
			}
			else 
				throw new Error("Paramtertype "+rVal.getValue().getClass()+" not supported");
		}
			
	}
	
	/**
	 * This Method removes all results ({@link TestRun}) which are equal or greater (>=) than the given value 
	 * 
	 * @param fieldName Name of the parameter or metric
	 * @param value The target-value which is matched matched
	 */
	public void applyFilterEqualOrGreater(String fieldName, double value) {
		
		Map<TestRun, Object> map = filterHelper(fieldName);
		
		//Now match and remove
		for(Entry<TestRun, Object> rVal : map.entrySet()) {
			if(rVal.getValue() instanceof Double) {
				Double dVal = (Double)rVal.getValue();
				if(dVal.doubleValue() >= value)
					mRuns.remove(rVal.getKey());
			}
			else 
				throw new Error("Paramtertype "+rVal.getValue().getClass()+" not supported");
		}
			
	}
	
	/**
	 * This Method removes all results ({@link TestRun}) which are equal or smaller (<=) than the given value 
	 * 
	 * @param fieldName Name of the parameter or metric
	 * @param value The target-value which is matched matched
	 */
	public void applyFilterEqualOrSmaller(String fieldName, double value) {
		
		
		Map<TestRun, Object> map = filterHelper(fieldName);
		
		//Now match and remove
		for(Entry<TestRun, Object> rVal : map.entrySet()) {
			if(rVal.getValue() instanceof Double) {
				Double dVal = (Double)rVal.getValue();
				if(dVal.doubleValue() <= value)
					mRuns.remove(rVal.getKey());
			}
			else 
				throw new Error("Paramtertype "+rVal.getValue().getClass()+" not supported");
		}
	}
	
	/**
	 * This Method removes all results ({@link TestRun}) which are between lower and upper bound <br>
	 * The bounds are included (x >= lowerBound && x <= upperBound)
	 * 
	 * @param fieldName Name of the parameter or metric
	 * @param lowerBound The lower bound to match
	 * @param upperBound The upper bound to match
	 */
	public void applyFilterInRange(String fieldName, double lowerBound, double upperBound) {
		
		if(lowerBound >= upperBound)
			throw new Error("The lower bound must be smaller and not equal to the upper bound!");
		
		Map<TestRun, Object> map = filterHelper(fieldName);
		
		//Now match and remove
		for(Entry<TestRun, Object> rVal : map.entrySet()) {
			if(rVal.getValue() instanceof Double) {
				Double dVal = (Double)rVal.getValue();
				if(dVal.doubleValue() >= lowerBound && dVal.doubleValue() <= upperBound)
					mRuns.remove(rVal.getKey());
			}
			else 
				throw new Error("Paramtertype "+rVal.getValue().getClass()+" not supported");
		}
			
	}
	
	/**
	 * This Method removes all results ({@link TestRun}) which are NOT between lower and upper bound <br>
	 * The bounds are included !(x >= lowerBound && x <= upperBound)
	 * 
	 * @param fieldName Name of the parameter or metric
	 * @param lowerBound The lower bound to match
	 * @param upperBound The upper bound to match
	 */
	public void applyFilterNotInRange(String fieldName, double lowerBound, double upperBound) {
		
		if(lowerBound >= upperBound)
			throw new Error("The lower bound must be smaller and not equal to the upper bound!");
		
		
		Map<TestRun, Object> map = filterHelper(fieldName);
		
		//Now match and remove
		for(Entry<TestRun, Object> rVal : map.entrySet()) {
			if(rVal.getValue() instanceof Double) {
				Double dVal = (Double)rVal.getValue();
				if(!(dVal.doubleValue() >= lowerBound && dVal.doubleValue() <= upperBound))
					mRuns.remove(rVal.getKey());
			}else 
				throw new Error("Paramtertype "+rVal.getValue().getClass()+" not supported");
		}
			
	}
	
	
	
	
	/**
	 * This method return a map of the parameters as key and the value of the materic as value
	 * 
	 * @param parameter1 Name of the first parameter
	 * @param parameter2 Name of the second parameter, may null of only 2d 
	 * @param metric Name of the metric
	 * @return A map with a array of doubles as key (2D the array has only size of one, 3D size of 2) and the Value of the metric 
	 */
	private HashMap<Double[], Double> createMap(String parameter1, String parameter2, String metric) {
		HashMap<Double[], Double> sortedData = new HashMap<Double[], Double>();
		
		if(!(mRuns.get(0).getParameters().get(parameter1) instanceof Double || 
				mRuns.get(0).getParameters().get(parameter2) instanceof Double))
			throw new Error("At least one Parameter has a String as Value-type. Not Supported");
		
		//Fill the map
		for(TestRun tr : mRuns) {
			Double[] valueParams;
			if(parameter2 != null) {
				valueParams = new Double[2]; //{ tr.getParameters().get(parameter1), tr.getParameters().get(parameter2)};
				valueParams[0] = (Double) tr.getParameters().get(parameter1);
				valueParams[1] = (Double) tr.getParameters().get(parameter2);
				if(valueParams[0] == null || valueParams[1] == null)
					throw new Error("The paramtername is not found in one TestRun.");
			}
			else {
				valueParams = new Double[1];
				valueParams[0] = (Double) tr.getParameters().get(parameter1);
				if(valueParams[0] == null)
					throw new Error("The paramtername is not found in one TestRun.");
			}
				
			
			Double valueMetric = tr.getResults().get(metric);
			
			if(valueMetric == null)
				throw new Error("The metric name is not found in one TestRun.");
	
			sortedData.put(valueParams, valueMetric);

		}

		
		return sortedData;
	}
	
	
	/**
	 * Outputs data for 2D Plot
	 * 
	 * @param directoryName Name of the directory
	 * @param parameter1 Name of the parameter on x-axis
	 * @param metric Name of the Metric 
	 */
	public void output2D(String directoryName, String parameter1, String metric) {
		HashMap<Double[], Double> sortedMap = createMap(parameter1, null, metric);
		
		//write datafile
		try {
			Writer writer = createWriter(directoryName, metric+".data");
			writer.write("#"+parameter1+"\tmin\tmax\taverage\tci95\n");
			for(Entry<Double[], Double[]> d : aggregate(sortedMap).entrySet()) {
				//System.out.println("Key: "+d.getKey()[0]+"; Value: "+d.getValue());
				writer.write(d.getKey()[0]+"\t"+d.getValue()[0]+"\t"+d.getValue()[1]+"\t"+d.getValue()[2]+"\t"+d.getValue()[3]+"\t"+"\n");
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			throw new Error("Write of data failed: "+e);
		}
		
		//write gnuplot file
		Writer writer = createWriter(directoryName, metric+".gnuplot");
		try {
			writer.write("set terminal svg noenhanced dashed size 350,350\n");
			writer.write("set output \""+directoryName+"/"+metric+".svg\"\n");
			writer.write("set xlabel '"+parameter1+"' \n");
			writer.write("set ylabel '"+metric+"' rotate left\n");
			writer.write("plot '"+directoryName+"/"+metric+".data' using 1:2 title 'min', \\\n");
			writer.write("'"+directoryName+"/"+metric+".data' using 1:3 title 'max', \\\n");
			writer.write("'"+directoryName+"/"+metric+".data' using 1:4 title 'average', \\\n");
			writer.write("'"+directoryName+"/"+metric+".data' using 1:4:5 with errorbars title 'CI95'\n");
			writer.flush();
			writer.close();
			
		} catch (IOException e) {
			throw new Error("Write of data failed: "+e);
		}
		
		try {
			//TODO: Check if it has worked
			Runtime.getRuntime().exec("gnuplot "+directoryName+"/"+metric+".gnuplot");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}
	
	/**
	 * Outputs data for 3D Plot
	 * 
	 * @param directoryName Name of the directory
	 * @param parameter1 Name of the parameter on x-axis
	 * @param parameter2 Name of the parameter on z-axis
	 * @param metric Name of the Metric 
	 */
	public void output3D(String directoryName, String parameter1, String parameter2, String metric) {
		HashMap<Double[], Double> sortedMap = createMap(parameter1, parameter2, metric);
		
		try {
			Writer writer = createWriter(directoryName, metric+".data");
			writer.write("#"+parameter1+"\t"+parameter2+"\tmin\tmax\taverage\tci95\n");
			Double lastVal = 0d;
			boolean first = true;
			for(Entry<Double[], Double[]> d : aggregate(sortedMap).entrySet()) {
				if(first) {
					first = false;
					lastVal = d.getKey()[0];
				}	
				
				if(lastVal.doubleValue() != d.getKey()[0].doubleValue()) {
					writer.write("\n");
					System.out.println(lastVal+"/"+d.getKey()[0]);
				}
				lastVal = d.getKey()[0];
				
				//System.out.println("Key: "+d.getKey()[0]+"; Value: "+d.getValue());
				writer.write(d.getKey()[0]+"\t"+d.getKey()[1]+"\t"+d.getValue()[0]+"\t"+d.getValue()[1]+"\t"+d.getValue()[2]+"\t"+d.getValue()[3]+"\t"+"\n");
				
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			throw new Error("Write of data failed: "+e);
		}
		
		//write gnuplot file
		Writer writer = createWriter(directoryName, metric+".gnuplot");
		try {
			writer.write("set terminal svg noenhanced dashed size 350,350\n");
			writer.write("set output \""+directoryName+"/"+metric+".svg\"\n");
			writer.write("set xlabel '"+parameter1+"' \n");
			writer.write("set ylabel '"+parameter2+"' \n");
			writer.write("set zlabel '"+metric+"' rotate left\n");
			writer.write("splot '"+directoryName+"/"+metric+".data' using 1:2:3 with lines title 'min', \\\n");
			writer.write("'"+directoryName+"/"+metric+".data' using 1:2:4 with lines title 'max', \\\n");
			writer.write("'"+directoryName+"/"+metric+".data' using 1:2:5 with lines title 'average', \\\n");
			writer.write("'"+directoryName+"/"+metric+".data' using 1:2:5:6 with errorbars title 'CI95'\n");
			writer.flush();
			writer.close();
			
		} catch (IOException e) {
			throw new Error("Write of data failed: "+e);
		}
		
		try {
			//TODO: Check if it has worked
			Runtime.getRuntime().exec("gnuplot "+directoryName+"/"+metric+".gnuplot");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a writer object
	 * 
	 * @param directoryName Name and path of the directory
	 * @param metric
	 */
	private Writer createWriter(String directoryName, String metric) {
		File dir = new File(directoryName);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		File datafile = new File(directoryName+"/"+metric);
		if(datafile.exists())
			datafile.delete();
		
		OutputStream outStream;
		try {
			outStream = new FileOutputStream(datafile);
			Writer writer = new OutputStreamWriter(outStream);
			return writer;
		} catch (FileNotFoundException e) {
			throw new Error("File "+ datafile.getName()+ "couldn't be opened!" + e);
		}
	}
	
	
	/**
	 * This method aggregates the data and returns them
	 * 
	 * @param data The raw data 
	 * @return a map of the Original ParameterList and an Array of the aggregated data
	 */
	private TreeMap<Double[], Double[]> aggregate(HashMap<Double[], Double> data) {
		TreeMap<Double[], Double[]> aggregatedData = new TreeMap<Double[], Double[]>(new Comparator<Double[]>() {
				//this comparator gives us the list if all distinct pairs
					@Override
					public int compare(Double[] o1, Double[] o2) {
						if(o1[0].doubleValue() < o2[0].doubleValue()) {
							return -1;
						}
						else if(o1[0].doubleValue() > o2[0].doubleValue()) {
							return 1;
						}
						else if (o1[0].doubleValue() == o2[0].doubleValue()) {
							if(o1.length == 1)
								return 0;
							else if(o1[1].doubleValue() < o2[1].doubleValue())
								return -1;
							else if(o1[1].doubleValue() > o2[1].doubleValue())
								return 1;
							else
								return 0;
						}
						else
							return 0;
					}
		});
		
		//Add the Keyset to the list, due the comparator we get a list with all distinct parameter pairs
		for(Double[] dArray : data.keySet())
			aggregatedData.put(dArray, null);
		
		
		//Now do the aggregation
		
		for(Double[] base : aggregatedData.keySet()) {
			Comparator<? super Double[]> comp = aggregatedData.comparator();
			ArrayList<Double> valList = new ArrayList<Double>();
			for(Entry<Double[], Double> act : data.entrySet()) {
				if(comp.compare(act.getKey(), base) == 0) {
					//Only matching rows here
					valList.add(act.getValue());
				}
			}
			
			Double min = minimum(valList);
			Double max = maximum(valList); 
			Double average = average(valList);
			Double confidence = normalCI95(valList);
			
			Double[] aggregated = {min, max, average, confidence};
			
			aggregatedData.put(base, aggregated);
			
		}
		
		return aggregatedData;
	}
	
	
	/**
	 * 
	 * @param values The List of Values
	 * @return maximum of all values
	 */
	private Double maximum(List<Double> values) {
		double max = 0;
		boolean first = true;
		for(double d : values) {
			if(first) {
				max = d;
				first = false;
			}
			if(d > max )
				max = d;
		}
		return max;
	}
	
	/**
	 * 
	 * @param values The List of Values
	 * @return minimum of all values
	 */
	private Double minimum(List<Double> values) {
		double max = 0;
		boolean first = true;
		for(double d : values) {
			if(first) {
				max = d;
				first = false;
			}
			if(d < max )
				max = d;
		}
		return max;
	}
	
	/**
	 * 
	 * @param values The List of Values
	 * @return average of all values
	 */
	public double average(List<Double> values) {
		if(values.size() == 0)
			return 0;
		
		double result = 0;
		for(Double d : values) {
			result += d;
		}
		result = result/values.size();
		
		return result;
	}
	
	
	/**
	 * 
	 * @param values The List of Values
	 * @return returns the Confidence interval
	 */
	public double normalCI95(List<Double> values) {
		double[] arr = convertToArray(values);
		if(values.size() == 0)
			return 0;
		
		if(values.size() == 1)
			return values.get(0);
		
		NormalMeanCI ci = new NormalMeanCI(arr, 0.95);
		
		double result = ci.getUpperLimit() - ci.getMean();
		return result;
	}
	
	/**
	 * Converts the List of Double in an array of Double
	 * 
	 * @param values List of values
	 * @return Array of values
	 */
	protected static double[] convertToArray(List<Double> values) {
		double[] result = new double[values.size()];
		
		for(int i = 0; i < values.size(); i++) {
			result[i] = values.get(i);
		}
		
		return  result;
	}
}
	
