package vnreal.io;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;

import edu.uci.ics.jung.graph.util.Pair;

import tests.TestRun;
import tests.TestSeries;
import vnreal.AdditionalConstructParameter;
import vnreal.ExchangeParameter;
import vnreal.Scenario;
import vnreal.demands.AbstractDemand;
import vnreal.mapping.Mapping;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;

/**
 * This class is the exporter for {@link TestSeries} and {@link Scenario} in the the schema based export format <br />
 * <br />
 * This class provides two possibilities to export <br />
 * 1. Access via the static methods <br />
 * 2. Creating an object and write out the {@link TestSeries} when and then freeing the resources
 * 
 * 
 * @author Fabian Kokot
 * @since 26.12.2012
 *
 */
public class XMLExporter {
	
	private final Object syncObject = new Object();
	private XMLStreamWriter mWriter = null;
	private final boolean mExportNetworks;
	
	/**
	 * Prepares the XMLwriter to output a file.
	 * 
	 * @param filename path and name of the file
	 * @param seriesName Name of the TestSeries
	 * @param testGenerator Class-Name of the Testgenerator
	 * @param exportNetworks true if the 
	 */
	public XMLExporter(String filename, String seriesName, String testGenerator, boolean exportNetworks) {
		synchronized (syncObject) {
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			mExportNetworks = exportNetworks;
			try {
				
				FileOutputStream outStream = new FileOutputStream(filename);
				mWriter = new IndentingXMLStreamWriter(factory.createXMLStreamWriter(outStream, "UTF-8"));

				
				mWriter.writeStartDocument("UTF-8", "1.0");
				
				mWriter.setDefaultNamespace("http://sourceforge.net/projects/alevin/");
				mWriter.writeStartElement("MappingTest");
				mWriter.writeNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
				mWriter.writeAttribute("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation", 
						"http://sourceforge.net/projects/alevin/ ./Alevin.xsd");
				mWriter.writeNamespace("", "http://sourceforge.net/projects/alevin/");
				mWriter.writeAttribute("name", seriesName);
				
				mWriter.writeEmptyElement("TestGenerator");
				mWriter.writeAttribute("className", testGenerator);
				
			} catch (XMLStreamException e) {
				throw new Error("Exporter error: "+e.getLocalizedMessage());
			} catch (FileNotFoundException e) {
				throw new Error("Exporter error: "+e.getLocalizedMessage());
			}
		}
	}
	
	/**
	 * This method exports to the given TestRun
	 * 
	 * @param tr {@link TestRun} to export
	 */
	public void exportTestRun(TestRun tr) {
		synchronized (syncObject) {
			if(mWriter == null) {
				throw new Error("Cannot export TestRun without initialisation!");
			}
			
			try {
				mWriter.writeStartElement("ScenarioResult");
				//Output to all ScenarioParameters
				for(String param : tr.getParameters().keySet()) {
					mWriter.writeEmptyElement("ScenarioParameter");
					mWriter.writeAttribute("name", param);
					mWriter.writeAttribute("value", tr.getParameters().get(param).toString());
					//writer.writeEndElement(); //End of ScenarioParameter
				}
				
				//Output all Metrics with results
				for(String metric : tr.getResults().keySet()) {
					mWriter.writeEmptyElement("ScenarioMetric");
					mWriter.writeAttribute("name", metric);
					mWriter.writeAttribute("result", tr.getResults().get(metric).toString());
					//writer.writeEndElement();//End of ScenarioMetric
				}
				
				//if available and wanted export the Networks to the xml-file, too
				if(mExportNetworks && tr.getScenario() != null) {
					mWriter.writeStartElement("ScenarioNetworks");
					exportScenario(mWriter, tr.getScenario());
					mWriter.writeEndElement();
				}
				mWriter.writeEndElement();//End of ScenarioResult
				
				mWriter.flush();
			} catch (XMLStreamException e) {
				throw new Error("Exporter error while export TestRun: "+e.getLocalizedMessage());
			}
			
		}
	}
	
	
	public void finishWriting() {
		synchronized (syncObject) {
			if(mWriter == null) {
				throw new Error("Cannot export TestRun without initialisation!");
			}
			try {
				//End of MappingTest
				mWriter.writeEndElement();
				mWriter.writeEndDocument();
				
			    mWriter.flush();
			    mWriter.close();
			    
			    mWriter = null;
			} catch (XMLStreamException e) {
				throw new Error("Exporter error while finishing export: "+e.getLocalizedMessage());
			} 
		}
	}
	
	/**
	 * This method is to export the {@link TestSeries} to a file
	 * 
	 * @param filename Path and filename of output file
	 * @param series {@link TestSeries} which has to be exported
	 * @param exportNetworks if true, the Networks will be exported 
	 */
	public static void exportResult(String filename, TestSeries series, boolean exportNetworks) {
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		try {
			FileOutputStream xmlout = new FileOutputStream(filename);
			XMLStreamWriter writer = new IndentingXMLStreamWriter(factory.createXMLStreamWriter(xmlout, "UTF-8"));
			
			writer.writeStartDocument("UTF-8", "1.0");
			
			writer.setDefaultNamespace("http://sourceforge.net/projects/alevin/");
			writer.writeStartElement("MappingTest");
			writer.writeNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
			writer.writeAttribute("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation", 
					"http://sourceforge.net/projects/alevin/ ./Alevin.xsd");
			writer.writeNamespace("", "http://sourceforge.net/projects/alevin/");
			writer.writeAttribute("name", series.getTestSeriesName());
			
			writer.writeEmptyElement("TestGenerator");
			writer.writeAttribute("className", series.getTestGenerator());
			//writer.writeEndElement();//End of TestGenerator
			
			//Output all Tests
			for(TestRun tr : series.getAllTestRuns()) {
				writer.writeStartElement("ScenarioResult");
				//Output to all ScenarioParameters
				for(String param : tr.getParameters().keySet()) {
					writer.writeEmptyElement("ScenarioParameter");
					writer.writeAttribute("name", param);
					writer.writeAttribute("value", tr.getParameters().get(param).toString());
					//writer.writeEndElement(); //End of ScenarioParameter
				}
				
				//Output all Metrics with results
				for(String metric : tr.getResults().keySet()) {
					writer.writeEmptyElement("ScenarioMetric");
					writer.writeAttribute("name", metric);
					writer.writeAttribute("result", tr.getResults().get(metric).toString());
					//writer.writeEndElement();//End of ScenarioMetric
				}
				
				//if available and wanted export the Networks to the xml-file, too
				if(exportNetworks && tr.getScenario() != null) {
					writer.writeStartElement("ScenarioNetworks");
					exportScenario(writer, tr.getScenario());
					writer.writeEndElement();
				}
				writer.writeEndElement();//End of ScenarioResult
			}
			
			writer.writeEndElement(); //End of MappingTest
			writer.writeEndDocument();
			
		    writer.flush();
		    writer.close();
			
		} catch (XMLStreamException e) {
			throw new Error("Exporter error: "+e.getLocalizedMessage());
		} catch (FileNotFoundException e) {
			throw new Error("Exporter error: "+e.getLocalizedMessage());
		}
		
	}
	
	
	public static void exportScenario(String filename, Scenario scen) {
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		try {
			FileOutputStream xmlout = new FileOutputStream(filename);
			XMLStreamWriter writer = new IndentingXMLStreamWriter(factory.createXMLStreamWriter(xmlout, "UTF-8"));
			
			writer.writeStartDocument("UTF-8", "1.0");
			
			writer.setDefaultNamespace("http://sourceforge.net/projects/alevin/");
			writer.writeStartElement("Scenario");
			writer.writeNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
			writer.writeAttribute("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation", 
					"http://sourceforge.net/projects/alevin/ ./Alevin.xsd");
			writer.writeNamespace("", "http://sourceforge.net/projects/alevin/");
			
			exportScenario(writer,scen);
			
			writer.writeEndElement(); //End of Scenario
			writer.writeEndDocument();
			
		    writer.flush();
		    writer.close();
			
		} catch (XMLStreamException e) {
			throw new Error("Exporter error: "+e.getLocalizedMessage());
		} catch (FileNotFoundException e) {
			throw new Error("Exporter error: "+e.getLocalizedMessage());
		}
			
	}
	

	/**
	 * This Method exports the scenario using a given XMLStreamWriter	
	 * 
	 * @param writer Given writer
	 * @param scenario The scenario to export
	 * @throws XMLStreamException 
	 */
	private static void exportScenario(XMLStreamWriter writer, Scenario scenario) throws XMLStreamException {
		writer.writeStartElement("SubstrateNetwork");
		writer.writeStartElement("SubstrateNodes");
		for(SubstrateNode sn : scenario.getNetworkStack().getSubstrate().getVertices()) {
			writer.writeStartElement("SubstrateNode");
			writer.writeAttribute("coordinateX", sn.getCoordinateX()+"");
			writer.writeAttribute("coordinateY", sn.getCoordinateY()+"");
			writer.writeAttribute("id", sn.getId()+"");
			//Export resources
			for(AbstractResource ar : sn.get()) {
				writer.writeStartElement("Resource");
				writer.writeAttribute("type", ar.getClass().getSimpleName());
				//Export additional construction parameters
				exportConstructParameters(writer, ar);
				exportParameters(writer, ar);
				writer.writeEndElement();//End of Resource
			}
			writer.writeEndElement();//End of SubstrateNode
		}
		writer.writeEndElement();//End of SubstrateNodes
		
		writer.writeStartElement("SubstrateLinks");
		for(SubstrateLink sl : scenario.getNetworkStack().getSubstrate().getEdges()) {
			writer.writeStartElement("SubstrateLink");
			Pair<SubstrateNode> p = scenario.getNetworkStack().getSubstrate().getEndpoints(sl);
			writer.writeAttribute("source", p.getFirst().getId()+"");
			writer.writeAttribute("destination", p.getSecond().getId()+"");
			writer.writeAttribute("id", sl.getId()+"");
			
			//Export resources
			for(AbstractResource ar : sl.get()) {
				writer.writeStartElement("Resource");
				writer.writeAttribute("type", ar.getClass().getSimpleName());
				//Export additional construction parameters
				exportConstructParameters(writer, ar);
				exportParameters(writer, ar);
				writer.writeEndElement();//End of Resource
			}
			
			writer.writeEndElement();//End of SubstrateLink
		}
		writer.writeEndElement();//End of SubstrateLinks
		writer.writeEndElement();//End of SubstrateNetwork
		
		
		
		
		
		writer.writeStartElement("VirtualNetworks");
		for(int i = 1; i < scenario.getNetworkStack().size(); i++) {
			writer.writeStartElement("VirtualNetwork");
			writer.writeAttribute("layer", i+"");
			VirtualNetwork vNet = (VirtualNetwork)scenario.getNetworkStack().getLayer(i);
			writer.writeStartElement("VirtualNodes");
			for(VirtualNode vn : vNet.getVertices()) {
				writer.writeStartElement("VirtualNode");
				writer.writeAttribute("coordinateX", vn.getCoordinateX()+"");
				writer.writeAttribute("coordinateY", vn.getCoordinateY()+"");
				writer.writeAttribute("id", vn.getId()+"");
				//Export demands
				for(AbstractDemand ad : vn.get()) {
					writer.writeStartElement("Demand");
					writer.writeAttribute("type", ad.getClass().getSimpleName());
					//Export additional construction parameters
					exportConstructParameters(writer, ad);
					exportParameters(writer, ad);
					exportMappings(writer, ad);
					writer.writeEndElement();//End of Demand
				}
				
				
				writer.writeEndElement();//End of VirtualNode
			}
			writer.writeEndElement();//End of VirtualNodes
			
			writer.writeStartElement("VirtualLinks");
			for(VirtualLink vl : vNet.getEdges()) {
				writer.writeStartElement("VirtualLink");
				Pair<VirtualNode> p = vNet.getEndpoints(vl);
				writer.writeAttribute("source", p.getFirst().getId()+"");
				writer.writeAttribute("destination", p.getSecond().getId()+"");
				writer.writeAttribute("id", vl.getId()+"");
				
				//Export demands
				for(AbstractDemand ad : vl.get()) {
					writer.writeStartElement("Demand");
					writer.writeAttribute("type", ad.getClass().getSimpleName());
					//Export additional construction parameters
					exportConstructParameters(writer, ad);
					exportParameters(writer, ad);
					exportMappings(writer, ad);
					writer.writeEndElement();//End of Demand
				}
				
				//Export HiddenHopDemands
				for(AbstractDemand ad : vl.getHiddenHopDemands()) {
					writer.writeStartElement("HiddenHopDemand");
					writer.writeAttribute("type", ad.getClass().getSimpleName());
					//Export additional construction parameters
					exportConstructParameters(writer, ad);
					exportParameters(writer, ad);
					exportMappings(writer, ad);
					writer.writeEndElement();//End of HiddenHopDemand
				}
				
				
				writer.writeEndElement();//End of VirtualLink
			}
			
			
			writer.writeEndElement(); //End of VirtualLinks
			writer.writeEndElement(); //End of VirtualNetwork
		}
		writer.writeEndElement(); //End of VirtualNetworks
		
	}

	/**
	 * This Method exports the Mappings
	 * 
	 * @param writer WriterObject
	 * @param ad Demand
	 * @throws XMLStreamException 
	 */
	private static void exportMappings(XMLStreamWriter writer, AbstractDemand ad) throws XMLStreamException {
		for(Mapping map : ad.getMappings()) {
			String type = map.getResource().getClass().getSimpleName();
			long entityID = map.getResource().getOwner().getId();
			
			writer.writeEmptyElement("Mapping");
			writer.writeAttribute("resourceType", type);
			writer.writeAttribute("substrateEntity", entityID+"");
		}
		
	}

	/**
	 * Extracts and exports normal parameters
	 * 
	 * @param writer Writer object
	 * @param ob Object 
	 * @throws XMLStreamException 
	 */
	private static void exportParameters(XMLStreamWriter writer,
			Object ob) throws XMLStreamException {
		Method[] mets = ob.getClass().getMethods();
		for(Method m : mets) {
			//lookup if the Method is marked as ExchangeParameter and NOT a setter
			ExchangeParameter ep = m.getAnnotation(ExchangeParameter.class);
			if(ep != null && m.getName().contains("get")) {
				writer.writeEmptyElement("Parameter");
				String paramName = m.getName().substring(3);
				SimpleEntry<String, String> se = exportParamType(ob, m.getName());
				writer.writeAttribute("name", paramName);
				writer.writeAttribute("type", se.getKey());
				writer.writeAttribute("value", se.getValue());
			}
		}
		
	}

	/**
	 * Extracts and write out the AdditionalConstructionParameters to the writer
	 * 
	 * @param writer Given Writer
	 * @param ob Object with annotations from AdditionalConstructParameter.class
	 * @throws XMLStreamException 
	 */
	private static void exportConstructParameters(XMLStreamWriter writer,
			Object ob) throws XMLStreamException {
		//Get the Construction Parameters
		AdditionalConstructParameter acp = ob.getClass().getAnnotation(AdditionalConstructParameter.class);
		if(acp != null) {
			for(int i = 0; i < acp.parameterGetters().length; i++) {
				writer.writeEmptyElement("ConstructParameter");
				writer.writeAttribute("name", acp.parameterNames()[i]);
				SimpleEntry<String, String> se = exportParamType(ob, acp.parameterGetters()[i]);
				writer.writeAttribute("type", se.getKey());
				writer.writeAttribute("value", se.getValue());
			}
		}
		
		
	}

	/**
	 * Exports the Parameter Type as a String
	 * 
	 * @param ob Resource or Demand
	 * @param getterName Name of the getter
	 * @return Name of the type for document
	 */
	private static SimpleEntry<String, String> exportParamType(Object ob, String getterName){
		//Try to get the getter
		try {
			SimpleEntry<String, String> e;
			Method[] mets = ob.getClass().getMethods();
			for(Method met: mets) {
				if(met.getName().equals(getterName)) {
					//Get the return type
					Type t = met.getGenericReturnType();
					String retName = t.toString();
					if(retName.equals("class vnreal.network.substrate.SubstrateNetwork")) {
						e = new SimpleEntry<String, String>("localSNet", "");
						return e;
					} else if(retName.equals("int") || retName.equals("class java.lang.Integer")) {
						int val = (int)met.invoke(ob);
						e = new SimpleEntry<String, String>("Integer", val+"");
						return e;
					} else if(retName.equals("class java.lang.String")) {
						String val = (String)met.invoke(ob);
						e = new SimpleEntry<String, String>("String", val);
						return e;
					} else if(retName.equals("double") || retName.equals("class java.lang.Double")) {
						double val = (double)met.invoke(ob);
						e = new SimpleEntry<String, String>("Double", val+"");
						return e;
					} else if(retName.equals("boolean") || retName.equals("class java.lang.Boolean")) {
						int val = (int)met.invoke(ob);
						e = new SimpleEntry<String, String>("Boolean", val+"");
						return e;
					} else if(retName.equals("java.util.ArrayList<java.lang.String>")) {
						//FIXME: UNTESTED CODE!!!!!
						String out = "";
						@SuppressWarnings({ "unchecked" })
						ArrayList<String> list = (ArrayList<String>)met.invoke(ob);
						for(String s2 : list) {
							String s = s2;
							out += s+",";
						}
						e = new SimpleEntry<String, String>("Collection_String", out);
						return e;
					} else {
						throw new Error("Cannot export datatype: "+retName);
					}
					//TODO: Test all data types and add more (ArrayList)
				}
			}
			throw new Error("Cannot find any getter for: "+getterName+" for class "+ob.getClass().getName()+"! Stop Exporting!");
		} catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new Error("Somethinh went wrong while exporting data types: "+e.getLocalizedMessage());
		}
	}

}
