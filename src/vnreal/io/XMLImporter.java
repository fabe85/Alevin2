package vnreal.io;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


import tests.TestRun;
import tests.TestSeries;
import vnreal.Scenario;
import vnreal.algorithms.utils.NodeLinkAssignation;
import vnreal.demands.AbstractDemand;
import vnreal.demands.IdDemand;
import vnreal.network.NetworkEntity;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;
import vnreal.resources.IdResource;

/**
 * This class imports the results based on the Alevin.xsd
 * 
 * @author Fabian Kokot
 *
 */
public class XMLImporter {
	
	/**
	 * This method is importing results of the given xml-file
	 * 
	 * @param filename Path and Filename
	 * @return A {@link TestSeries}
	 */
	public static TestSeries  importResults(String filename) {
		try {
			
			/*
			 * 1. Validate the xml
			 */
			String schemaLang = "http://www.w3.org/2001/XMLSchema";
			SchemaFactory jaxp = SchemaFactory.newInstance(schemaLang);
			Schema schema = jaxp.newSchema(new StreamSource("src/XML/Alevin.xsd"));
			Validator validator = schema.newValidator();
			
			//Create the source
			SAXSource source = new SAXSource(new InputSource(filename));
	      
			
			// prepare SAX handler and SAX result receiving validate data:
			ImportHandler handler = new ImportHandler();
			SAXResult sax = new SAXResult(handler);
			
			//Validate the source and parse the contents
			validator.validate(source, sax);
			
			
			//Return the ready to use TestSeries
			return handler.series;
			
			
			
		} catch (SAXParseException e) {
			throw new Error("Validation failed on line: "+e.getLineNumber()+" with error "+e.getMessage());
		} catch (SAXException e) {
			throw new Error(e.getLocalizedMessage());
		} catch (IOException e) {
			throw new Error(e.getLocalizedMessage());
		}
	}
	
	/**
	 * This method is importing the {@link Scenario} of given xml-file
	 * 
	 * @param filename Path and Filename
	 * @return A {@link TestSeries}
	 */
	public static Scenario  importScenario(String filename) {
		try {
			
			/*
			 * 1. Validate the xml
			 */
			String schemaLang = "http://www.w3.org/2001/XMLSchema";
			SchemaFactory jaxp = SchemaFactory.newInstance(schemaLang);
			Schema schema = jaxp.newSchema(new StreamSource("src/XML/Alevin.xsd"));
			Validator validator = schema.newValidator();
			
			//Create the source
			SAXSource source = new SAXSource(new InputSource(filename));
	      
			
			// prepare SAX handler and SAX result receiving validate data:
			ImportHandler handler = new ImportHandler();
			SAXResult sax = new SAXResult(handler);
			
			//Validate the source and parse the contents
			validator.validate(source, sax);
			
			
			//Return the ready to use TestSeries
			return handler.currentScenario;
			
			
			
		} catch (SAXParseException e) {
			throw new Error("Validation failed on line: "+e.getLineNumber()+" with error "+e.getMessage());
		} catch (SAXException e) {
			throw new Error(e.getLocalizedMessage());
		} catch (IOException e) {
			throw new Error(e.getLocalizedMessage());
		}
	}
	
	
	/**
	 * This mothof imports the networks in the given ScenarioObject
	 * @param filename Path and name of the file
	 * @param scenario Given Scenario-Object
	 */
	public static void importScenario(String filename, Scenario scenario) {
		scenario.setNetworkStack(importScenario(filename).getNetworkStack());
	}
	
	
	private static final class ImportHandler extends DefaultHandler {
		private TestSeries series = null;
//		private String currentValue;
		private String seriesName;
		private TestRun currentRun;
		
		//Network Stuff
		private Scenario currentScenario;
		
		//SubstrateNetwork stuff
		private HashMap<Long, Long> idmap;
		private SubstrateNetwork currentSNet;
		private NetworkEntity<? extends AbstractResource> currentSEntity;
		private Class<? extends Object> currentClass;
		private LinkedHashMap<String, Object> currentConstructParameters;
		private LinkedHashMap<String, Object> currentParameters;
		private ArrayList<AbstractResource> currentResources;
		private long currentSource, currentDestination;
		
		//VirtualNetwork stuff
		private ArrayList<VirtualNetwork> currentVNets;
		private VirtualNetwork currentVNetwork;
		private NetworkEntity<? extends AbstractDemand> currentVEntity;
		private ArrayList<AbstractDemand> currentDemands;
		private ArrayList<AbstractDemand> currentHiddenHopDemands;
		private LinkedHashMap<Long, String> currentMappings;
		
			
//		@Override
//		public void characters(char[] ch, int start, int length)
//				throws SAXException {
//			 currentValue = new String(ch, start, length);
//		}

		
		
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			
			//Start "MappingTest"
			if(localName.equals("MappingTest")) {
				seriesName = attributes.getValue("name");
			}
			
			//Start "TestGenerator"
			if(localName.equals("TestGenerator")) {
				String generatorClass = attributes.getValue("className");
				//Now we have all data to create the TestSeries
				series = new TestSeries(seriesName, generatorClass);
			}
			
			//Start "ScenarioResult"
			if(localName.equals("ScenarioResult")) {
				//Begin of a new TestRun
				currentRun = new TestRun();
			}
			
			//Start "ScenarioParameter"
			if(localName.equals("ScenarioParameter")) {
				String param = attributes.getValue("name");
				Object value;
				try {
					value = Double.parseDouble(attributes.getValue("value"));
				} catch (NumberFormatException e) {
					value = attributes.getValue("value");
				}
				
				currentRun.addParameter(param, value);
			}
			
			//Start "ScenarioMetric"
			if(localName.equals("ScenarioMetric")) {
				String metric = attributes.getValue("name");
				Double result = Double.parseDouble(attributes.getValue("result"));
				currentRun.addResult(metric, result);
			}
			
			
			
			//Treat the embedded Network for the current run
			
			if(localName.equals("ScenarioNetworks") || localName.equals("Scenario") ) {
				currentScenario = new Scenario();
				idmap = new HashMap<Long, Long>();
			}
			
			if(localName.equals("SubstrateNetwork")) {
				currentSNet = new SubstrateNetwork(false);
			}
			
			if(localName.equals("SubstrateNode")) {
				long id = Long.parseLong(attributes.getValue("id"));
				SubstrateNode lNode = new SubstrateNode();
				//Put the old and new id into the map
				idmap.put(id, lNode.getId());
				
				double x = Double.parseDouble(attributes.getValue("coordinateX"));
				double y = Double.parseDouble(attributes.getValue("coordinateY"));
				lNode.setCoordinateX(x);
				lNode.setCoordinateY(y);
				
				currentResources = new ArrayList<AbstractResource>();
				currentSEntity = lNode;
				
			}
			
			if(localName.equals("Resource")) {
				try {
					String resName = attributes.getValue("type");
					currentClass = Class.forName("vnreal.resources."+resName);
					currentConstructParameters = new LinkedHashMap<String, Object>();
					currentParameters = new LinkedHashMap<String, Object>();

					// Constructor of id resource requires substrate network. 
					if (resName.equals("IdResource")) {
						currentConstructParameters.put("sNetwork", currentSNet);
					}
				} catch (ClassNotFoundException | SecurityException e) {
					throw new Error("Ressource from XML not found: "+e.getMessage());
				}
			}
			
			
			if(localName.equals("ConstructParameter")) {
				
				Object ob = importParameter(attributes.getValue("type"), attributes.getValue("value"));
				
				currentConstructParameters.put(attributes.getValue("name"), ob);
			}
			
			
			if(localName.equals("Parameter")) {
				Object ob = importParameter(attributes.getValue("type"), attributes.getValue("value"));
				
				currentParameters.put(attributes.getValue("name"), ob);
			}
			
			if(localName.equals("SubstrateLink")) {
				long id = Long.parseLong(attributes.getValue("id"));
				currentSEntity = new SubstrateLink();
				idmap.put(id, currentSEntity.getId());
				currentResources = new ArrayList<AbstractResource>();
				
				currentDestination = getNewID(Long.parseLong(attributes.getValue("destination")));
				currentSource = getNewID(Long.parseLong(attributes.getValue("source")));
				
			}
			
			if(localName.equals("VirtualNetworks")) {
				currentVNets = new ArrayList<VirtualNetwork>();
			}
			
			if(localName.equals("VirtualNetwork")) {
				int layer = Integer.parseInt(attributes.getValue("layer"));
				currentVNetwork = new VirtualNetwork(layer);
			}
			
			if(localName.equals("VirtualNode")) {
				long id = Long.parseLong(attributes.getValue("id"));
				VirtualNode lNode = new VirtualNode(currentVNetwork.getLayer());
				idmap.put(id, lNode.getId());
				
				double x = Double.parseDouble(attributes.getValue("coordinateX"));
				double y = Double.parseDouble(attributes.getValue("coordinateY"));
				lNode.setCoordinateX(x);
				lNode.setCoordinateY(y);
				
				currentDemands = new ArrayList<AbstractDemand>();
				currentVEntity = lNode;
			}
			
			if(localName.equals("Demand") || localName.equals("HiddenHopDemand")) {
				try {
					String resName = attributes.getValue("type");
					currentClass = Class.forName("vnreal.demands."+resName);	
					currentConstructParameters = new LinkedHashMap<String, Object>();
					currentParameters = new LinkedHashMap<String, Object>();
				} catch (ClassNotFoundException | SecurityException e) {
					throw new Error("Demand from XML not found: "+e.getMessage());
				}
			}
			
			if(localName.equals("Mapping")) {
				if(currentMappings == null)
					currentMappings = new LinkedHashMap<Long, String>();
				
				long id = getNewID(Long.parseLong(attributes.getValue("substrateEntity")));
				String resName = attributes.getValue("resourceType");
				currentMappings.put(id, resName);
			}
			
			if(localName.equals("VirtualLink")) {
				long id = Long.parseLong(attributes.getValue("id"));
				currentVEntity = new VirtualLink(currentVNetwork.getLayer());
				idmap.put(id, currentVEntity.getId());
				currentDemands = new ArrayList<AbstractDemand>();
				currentHiddenHopDemands = new ArrayList<AbstractDemand>();
				
				currentDestination = getNewID(Long.parseLong(attributes.getValue("destination")));
				currentSource = getNewID(Long.parseLong(attributes.getValue("source")));
				
			}
			
			
		}




		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			
			//Treat the plain network
			if(localName.equals("Scenario")) {
				NetworkStack stack = new NetworkStack(currentSNet, currentVNets);
				currentScenario.setNetworkStack(stack);
				currentSNet = null;
				currentVNets = null;
				//DEBUG
				System.out.println(idmap);
				idmap = null;
			}
			
			
			
			//End "ScenarioResult"
			if(localName.equals("ScenarioResult")) {
				//End of the current TestRun
				series.addTestRun(currentRun);
				currentRun = null;
			}
			
			//Treat the embedded network
			if(localName.equals("ScenarioNetworks")) {
				NetworkStack stack = new NetworkStack(currentSNet, currentVNets);
				currentScenario.setNetworkStack(stack);
				currentRun.setScenario(currentScenario);
				currentSNet = null;
				currentVNets = null;
				currentScenario = null;
				idmap = null;
			}
			

			if(localName.equals("SubstrateNetwork")) {
			}
			
			
			if(localName.equals("SubstrateNode")) {
				SubstrateNode sn = (SubstrateNode) currentSEntity;
				for(AbstractResource ar : currentResources) {
					sn.add(ar);
				}
				currentSNet.addVertex((SubstrateNode) currentSEntity);
				currentSEntity = null;
				currentResources = null;
			}
			
			if(localName.equals("Resource")) {
				//FIXME: Construction with Parameters only working when the parameters are in the right order in the XML
				//TODO: make this code better
				
				//Prepare the Array ob Objects for the Construction of the Resource
				@SuppressWarnings("rawtypes")
				Class[] clArr;
				Object[] obArr;
				if(currentConstructParameters != null) {
					clArr = new Class[currentConstructParameters.size()+1];
					obArr = new Object[currentConstructParameters.size()+1];
				} else {
					clArr = new Class[1];
					obArr = new Object[1];
				}
 				
				clArr[0] = currentSEntity.getClass().getSuperclass();
				obArr[0] = currentSEntity;
				
				if(currentConstructParameters != null) {
					Object[] tmpObArr = currentConstructParameters.values().toArray();
					for(int i = 1; i <currentConstructParameters.size()+1; i++) {
						clArr[i] = tmpObArr[i-1].getClass();
						obArr[i] = tmpObArr[i-1];
					}
				}
				
				AbstractResource ar;
				try {
//					System.out.println(currentClass);
//					System.out.print("Classes: ");
//					for(Class c : clArr) {
//						System.out.print(c.getName()+",");
//					}
//					System.out.println();
//					System.out.print("Objects: ");
//					for(Object o : obArr) {
//						System.out.println(o.getClass()+",");
//					}
					ar = (AbstractResource) currentClass.getConstructor(clArr).newInstance(obArr);
					
					//Call setter
					Method[] methods = ar.getClass().getMethods();
					for(String name : currentParameters.keySet()) {
						Method setter = XMLUtils.getSetterIgnoreCase(methods, name);
						setter.invoke(ar, currentParameters.get(name));
					}
					
					//Treat the IDResource to contain the right ID
					if(ar.getClass().toString().equals("class vnreal.resources.IdResource")) {
						IdResource idr = (IdResource) ar;
						//Set the new ID
						idr.setId(getNewID(Long.parseLong(idr.getId()))+"");
					}
				} catch (InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
					throw new Error("Creation of resource failed: "+e.getLocalizedMessage());
				}
				
				currentResources.add(ar);
				currentConstructParameters = null;
				currentParameters = null;
			}
			
			if(localName.equals("SubstrateLink")) {
				SubstrateNode dstNode = XMLUtils.getSubstrateNodeForId(currentDestination, currentSNet);
				SubstrateNode srcNode = XMLUtils.getSubstrateNodeForId(currentSource, currentSNet);
				
				if(dstNode == null || srcNode == null) {
					throw new Error("SubstrateNetwork not conatains nodes with ids "+currentDestination+" and "+currentSource);
				}
				
				SubstrateLink sl = (SubstrateLink) currentSEntity;
				
				for(AbstractResource ar : currentResources) {
					sl.add(ar);
				}
				
				currentSEntity = null;
				currentResources = null;
				currentDestination = -1;
				currentSource = -1;
				
				currentSNet.addEdge(sl, srcNode, dstNode);
			}
			
			
			
			if(localName.equals("Demand") || localName.equals("HiddenHopDemand")) {
				//FIXME: Construction with Parameters only working when the parameters are in the right order in the XML
				//TODO: make this code better
				
				//Prepare the Array ob Objects for the Construction of the Resource
				@SuppressWarnings("rawtypes")
				Class[] clArr;
				Object[] obArr;
				if(currentConstructParameters != null) {
					clArr = new Class[currentConstructParameters.size()+1];
					obArr = new Object[currentConstructParameters.size()+1];
				} else {
					clArr = new Class[1];
					obArr = new Object[1];
				}
 				
				clArr[0] = currentVEntity.getClass().getSuperclass();
				obArr[0] = currentVEntity;
				
				if(currentConstructParameters != null) {
					Object[] tmpObArr = currentConstructParameters.values().toArray();
					for(int i = 1; i <currentConstructParameters.size()+1; i++) {
						clArr[i] = tmpObArr[i-1].getClass();
						obArr[i] = tmpObArr[i-1];
					}
				}
				
				AbstractDemand ad;
				try {
//					System.out.println(currentClass);
//					System.out.print("Classes: ");
//					for(@SuppressWarnings("rawtypes") Class c : clArr) {
//						System.out.print(c.getName()+",");
//					}
//					System.out.println();
//					System.out.print("Objects: ");
//					for(Object o : obArr) {
//						System.out.println(o.getClass()+",");
//					}
					ad = (AbstractDemand) currentClass.getConstructor(clArr).newInstance(obArr);
										
					//Call setter
					Method[] methods = ad.getClass().getMethods();		
					for(String name : currentParameters.keySet()) {
						Method setter = XMLUtils.getSetterIgnoreCase(methods, name);
						setter.invoke(ad, currentParameters.get(name));
					}

					//Treat the IDDemand to contain the right ID
					if(ad.getClass().toString().equals("class vnreal.demands.IdDemand")) {
						IdDemand idd = (IdDemand) ad;
						//Set the new ID
						idd.setDemandedId(getNewID(Long.parseLong(idd.getDemandedId()))+"");
				
					}
					
					//Add Mappings
					if(currentMappings != null) {
						for(long id : currentMappings.keySet()) {
							AbstractResource ar = null;
							NetworkEntity<? extends AbstractResource> se = XMLUtils.getSubstrateEntityForId(id, currentSNet);
							@SuppressWarnings("rawtypes")
							Class tClass = Class.forName("vnreal.resources."+currentMappings.get(id));
							if(se != null) {
								for(AbstractResource tmpAr : se) {
									if(tmpAr.getClass() == tClass)
										ar = tmpAr;
								}
								if(ar == null)
									throw new Error("Referenced resource type "+tClass.getName()+
											" in node "+currentVEntity.getId()+" not found");
							}
							boolean b = NodeLinkAssignation.occupy(ad, ar);
							if(!b) {
								throw new Error("Demand "+ad.getClass().getSimpleName()+" from VNode "+currentVEntity.getId()
										+" could not mapped.");
								
							}

						}
					}
				} catch (InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
					throw new Error("Creation of demand failed: "+e.getLocalizedMessage());
				} catch (ClassNotFoundException e) {
					throw new Error("Creation of Mapping failed: "+e.getLocalizedMessage());
				}
				
				if(localName.equals("Demand"))
					currentDemands.add(ad);
				else
					currentHiddenHopDemands.add(ad);
				currentConstructParameters = null;
				currentParameters = null;
				currentMappings = null;
			}
			
			if(localName.equals("VirtualNode")) {
				VirtualNode vn = (VirtualNode) currentVEntity;
				for(AbstractDemand ad : currentDemands) {
					vn.add(ad);
				}
				currentVNetwork.addVertex((VirtualNode) currentVEntity);
				currentVEntity = null;
				currentDemands = null;
			}
			
			
			if(localName.equals("VirtualLink")) {
				VirtualNode dstNode = XMLUtils.getVirtualNodeForId(currentDestination, currentVNetwork);
				VirtualNode srcNode = XMLUtils.getVirtualNodeForId(currentSource, currentVNetwork);
				
				if(dstNode == null || srcNode == null) {
					throw new Error("SVirtualNetwork "+currentVNetwork.getLayer()+" not conatains nodes with " +
							"ids "+currentDestination+" and "+currentSource);
				}
				
				VirtualLink vl = (VirtualLink) currentVEntity;
				
				for(AbstractDemand ad : currentDemands) {
					vl.add(ad);
				}
				
				for(AbstractDemand ad : currentHiddenHopDemands) {
					vl.addHiddenHopDemand(ad);
				}
				
				currentVEntity = null;
				currentDemands = null;
				currentHiddenHopDemands = null;
				currentDestination = -1;
				currentSource = -1;
				
				currentVNetwork.addEdge(vl, srcNode, dstNode);				
			}
			
			if(localName.equals("VirtualNetwork")) {
				currentVNets.add(currentVNetwork);
				currentVNetwork = null;
			}
			
		}
		
		
		/**
		 * Creates a object from a specified data type and returns it as a object <br />
		 * Supported: {@link Integer}, Long @ {@link Long}, link String}, {@link Double}, {@link Boolean} and "localSNet"
		 * 
		 * @param type Type from XML as {@link String}
		 * @param value Value from XML as {@link String}
		 * 
		 * @return Object
		 */
		private Object importParameter(String type, String value) {
			Object ob;
			if(type.equals("Integer")) {
				ob = new Integer(value);
			} else if (type.equals("Long"))
				ob = new Long(value);
			else if (type.equals("String"))
				ob = value;
			else if (type.equals("Double"))
				ob = new Double(value);
			else if (type.equals("Boolean"))
				ob = new Boolean(value);
			else if (type.equals("localSNet"))
				ob = currentSNet;
			else if(type.equals("Collection_String")) {
				ArrayList<String> list = new ArrayList<String>();
				String[] vals = value.split(",");
				for(String s : vals) {
					list.add(s);
				}
				ob = list;
			}
			else 
				throw new Error("Datatype "+type+" not supported!");
			
			return ob;
		}
		
		/**
		 * Find the corresponding new ID, or Error
		 * 
		 * @param oldID Old IT from file
		 * @return new ID
		 */
		private long getNewID(long oldID) {
			Long newID = idmap.get(oldID);
			if(newID != null ) {
				return newID;
			}
			
			throw new Error("Error while importing networks id "+oldID+" not found.");
		}
		
		
		
		
	}
	
}
