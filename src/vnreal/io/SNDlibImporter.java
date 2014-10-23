/* ***** BEGIN LICENSE BLOCK *****
 * Copyright (C) 2010-2011, The VNREAL Project Team.
 * 
 * This work has been funded by the European FP7
 * Network of Excellence "Euro-NF" (grant agreement no. 216366)
 * through the Specific Joint Developments and Experiments Project
 * "Virtual Network Resource Embedding Algorithms" (VNREAL). 
 *
 * The VNREAL Project Team consists of members from:
 * - University of Wuerzburg, Germany
 * - Universitat Politecnica de Catalunya, Spain
 * - University of Passau, Germany
 * See the file AUTHORS for details and contact information.
 * 
 * This file is part of ALEVIN (ALgorithms for Embedding VIrtual Networks).
 *
 * ALEVIN is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License Version 3 or later
 * (the "GPL"), or the GNU Lesser General Public License Version 3 or later
 * (the "LGPL") as published by the Free Software Foundation.
 *
 * ALEVIN is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * or the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and
 * GNU Lesser General Public License along with ALEVIN; see the file
 * COPYING. If not, see <http://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ***** */
package vnreal.io;

import static vnreal.io.SNDlibImporter.State.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vnreal.Scenario;
import vnreal.demands.BandwidthDemand;
import vnreal.demands.CostDemand;
import vnreal.demands.NullDemand;
import vnreal.io.SNDlibImporter.Link.Capacity;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.BandwidthResource;
import vnreal.resources.CostResource;
import vnreal.resources.NullResource;

/**
 * This class can read the SNDlib native network format and tries to map its
 * contents to Alevin.
 * 
 * @author Alexander Findeis
 */
public final class SNDlibImporter {

    /**
     * Represents a node read from SNDlib.
     */
    private class Node {
        String id;
        double x;
        double y;
    }

    /**
     * Represents a link read from SNDlib.
     */
    public static class Link {
        /**
         * Represents a link capacity upgrade module
         */
        public static class Capacity {
            /** How often this upgrade is used */
            public int count = 0;
            /** Capacity of this upgrade */
            public double cap;
            /** Cost of a single use of this upgrade */
            public double cost;
        }
        
        /** The name of this link */
        public String id;
        /** The name of the source node */
        public String src;
        /** The name of the destination node */
        public String dest;
        /** Preinstalled capacity on this link */
        public Capacity preinstCap;
//        public double setupCost;
        /** List of capacity upgrade modules */
        public List<Capacity> upgrades;
    }

    /**
     * States the SNDlib parser can be in.
     */
    public static enum State {
        /** Awaiting the file header */
        AWAIT_HEADER,
        /** Awaiting any section definition */
        AWAIT_SECTION,
        /** Parsing nodes section */
        IN_NODES,
        /** Parsing links section */
        IN_LINKS,
        /** Parsing demands section */
        IN_DEMANDS,
        /** Parsing admissible paths section */
        IN_ADMISSIBLE_PATHS,
        /** Parsing meta data section */
        IN_META
    }

    private final static Matcher NODE_LINE = Pattern.compile("(\\S+)\\s+\\(\\s*(\\S+)\\s+(\\S+)\\s*\\)").matcher("");
    private final static Matcher LINK_LINE = Pattern.compile("(\\S+)\\s+\\(\\s*(\\S+)\\s+(\\S+)\\s*\\)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+\\(\\s*(.*)\\s*\\)").matcher("");
//    private final static Matcher DEMAND_LINE = Pattern.compile("(\\S+)\\s+\\(\\s*(\\S+)\\s+(\\S+)\\s*\\)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)").matcher("");
//    private final static Matcher ADMISSIBLE_PATH_LINE = Pattern.compile("").matcher("");

    Map<String, Node> nodes = new HashMap<>();
    Map<String, Link> links = new HashMap<>();

    private boolean initialized = false;
    private boolean typeSet = false;

    private boolean substrate;

    /**
     * Creates a new importer instance.
     * 
     * @param filename SNDlib native network file to import
     * @throws FileNotFoundException if the given file is not found
     */
	public SNDlibImporter(String filename) throws FileNotFoundException {
		this(new FileInputStream(new File(filename)));
	}

	/**
	 * Creates a new importer instane.
	 * 
	 * @param file SNDlib native network file to import
	 */
	public SNDlibImporter(InputStream file) {
		init(file);
	}
	
	/**
	 * Initializes the Importer
	 * 
	 * @param file SNDlib native network file to import
	 * @return true if the initialization was successful, false otherwise.
	 */
	private boolean init(InputStream file) {
        try {
		    BufferedReader in = new BufferedReader(new InputStreamReader(file));

		    State state = AWAIT_HEADER;
		    boolean nodes_finished = false;
		    boolean links_finished = false;
		    boolean demands_finished = false;
		    boolean admissiblePaths_finished = false;
		    boolean meta_finished = false; // not mandatory in file
		    
		    while (!nodes_finished || !links_finished || !demands_finished || !admissiblePaths_finished) {
		        String line = in.readLine().trim();
		        
		        if (line.isEmpty() || line.startsWith("#")) continue; // skip comments and empty lines

		        switch(state) {
		        case AWAIT_HEADER:
		            if (!line.equals("?SNDlib native format; type: network; version: 1.0")) {
		                System.err.println("ERROR: Chosen file is not a valid SNDlib network.");
		                return false;
		            } else {
		                state = AWAIT_SECTION;
		            }
		            break;
		            
		        case AWAIT_SECTION:
		            if (line.equals("NODES (") && !nodes_finished) {
		                state = IN_NODES;
		            } else if (line.equals("LINKS (") && !links_finished) {
                        state = IN_LINKS;
                    } else if (line.equals("DEMANDS (") && !demands_finished) {
                        state = IN_DEMANDS;
                    } else if (line.equals("ADMISSIBLE_PATHS (") && !admissiblePaths_finished) {
                        state = IN_ADMISSIBLE_PATHS;
                    } else if (line.equals("META (") && !meta_finished) {
                        state = IN_META;
                    } else {
                        System.err.println("ERROR: Error while parsing SNDlib network. Current state: " + state);
                        return false;
                    }
		            break;
		            
		        case IN_NODES:
		            if (line.equals(")")) {
		                nodes_finished = true;
		                state = AWAIT_SECTION;
		            } else {
		                NODE_LINE.reset(line);
		                
		                if (NODE_LINE.matches()) {
		                    String id = NODE_LINE.group(1);
		                    double x = Double.parseDouble(NODE_LINE.group(2));
		                    double y = Double.parseDouble(NODE_LINE.group(3));
		                    
		                    Node node = new Node();
		                    node.id = id;
		                    node.x = x;
		                    node.y = y;
		                    nodes.put(id, node);
		                } else {
		                    System.err.println("ERROR: Error while parsing SNDlib network. Current state: " + state);
		                    return false;
		                }
		            }
		            break;
		            
		        case IN_LINKS:
                    if (line.equals(")")) {
                        links_finished = true;
                        state = AWAIT_SECTION;
                    } else {
                        LINK_LINE.reset(line);
                        
                        if (LINK_LINE.matches()) {
                            String id = LINK_LINE.group(1);
                            String src = LINK_LINE.group(2);
                            String dest = LINK_LINE.group(3);
                            double preinstCapCap = Double.parseDouble(LINK_LINE.group(4));
                            double preinstCapCost = Double.parseDouble(LINK_LINE.group(5));
                            // routing cost: group(6)
//                            double setupCost = Double.parseDouble(LINK_LINE.group(7));
                            String[] upgradeList = LINK_LINE.group(8).split("\\s+");
                            
                            List<Capacity> upgrades = new LinkedList<>();
                            for (int i = 0; i < upgradeList.length; i = i + 2) {
                                Capacity c = new Capacity();
                                c.cap = Double.parseDouble(upgradeList[i]);
                                c.cost = Double.parseDouble(upgradeList[i + 1]);
                                upgrades.add(c);
                            }
                            
                            Link link = new Link();
                            link.id = id;
                            link.src = src;
                            link.dest = dest;
                            link.preinstCap = new Capacity();
                            link.preinstCap.cap = preinstCapCap;
                            link.preinstCap.cost = preinstCapCost;
//                            link.setupCost = setupCost;
                            link.upgrades = upgrades;
                            links.put(id, link);
                        } else {
                            System.err.println("ERROR: Error while parsing SNDlib network. Current state: " + state);
                            return false;
                        }                        
                    }
                    break;

		        case IN_DEMANDS:
                    if (line.equals(")")) {
                        demands_finished = true;
                        state = AWAIT_SECTION;
                    }
                    // else: do nothing, demands will not be parsed                       
		            break;

		        case IN_ADMISSIBLE_PATHS:
                    if (line.equals(")")) {
                        admissiblePaths_finished = true;
                        state = AWAIT_SECTION;
                    }
                    // else: do nothing, admissible paths will not be parsed
		            break;
		            
		        case IN_META:
                    if (line.equals(")")) {
                        meta_finished = true;
                        state = AWAIT_SECTION;
                    }
                    // else: do nothing, meta data will not be parsed
                    break;
		            
		        default:
		            System.err.println("ERROR: Something went terribly wrong here...");
		        }
		    }
		    
		    initialized = true;
			return true;
		} catch (IOException e) {
			System.err.println("ERROR: Error while reading from file.");
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Returns a collection of all parsed links.
	 * 
	 * @return a collection of all parsed links
	 */
	public Collection<Link> getLinks() {
	    return links.values();
	}

	/**
	 * Sets if the imported network will be a substrate net or a virtual net.
	 * 
	 * @param substrate true, if the network will be a substrate net, false, if
	 *                  it will be a virtual net
	 */
    public void setType(boolean substrate) {
        this.substrate = substrate;
        typeSet = true;
    }

	// TODO: revert to old NetworkStack might cause problems with ID uniqueness ( ID-reset on .setNetworkStack() )
	/**
	 * Method used to return the {@link NetworkStack} read from the XML file.
	 * 
	 * @param scenario the current scenario
	 */
	public void setNetworkStack(Scenario scenario) {
		if (initialized && typeSet) {
//		    NetworkStack oldStack = null;
		    NetworkStack newStack = null;
		    VirtualNetwork virtNet = null;
		    
            if (substrate) {
//                oldStack = ToolKit.getScenario().getNetworkStack();
                scenario.setNetworkStack(null);

                newStack = new NetworkStack(new SubstrateNetwork(false), new ArrayList<VirtualNetwork>());
            } else {
                int layer = scenario.getNetworkStack().size();
                virtNet = new VirtualNetwork(layer);
            }

            Map<String, SubstrateNode> substNodes = new HashMap<>();
//            Map<String, SubstrateLink> substLinks = new HashMap<>();
            Map<String, VirtualNode> virtNodes = new HashMap<>();
//            Map<String, VirtualLink> virtLinks = new HashMap<>();

            for (Node n : nodes.values()) {
                if (substrate) {
                    SubstrateNode node = new SubstrateNode();
                    node.setName(n.id);
                    node.setCoordinateX(n.x);
                    node.setCoordinateY(-n.y); // in Lat/Long y decreases from north to south, in ALEVIN y increases from top to bottom
        
                    NullResource null_res = new NullResource(node);
                    node.add(null_res);
                    
                    substNodes.put(n.id, node);
                    
                    if (!newStack.getSubstrate().addVertex(node)) {
//                        ToolKit.getScenario().setNetworkStack(oldStack);
                        throw new AssertionError("Failed to add Node " + n.id + " to substrate network");
                    }
                } else {
                      VirtualNode node = new VirtualNode(virtNet.getLayer());
                      node.setName(n.id);
                      node.setCoordinateX(n.x);
                      node.setCoordinateY(-n.y); // in Lat/Long y decreases from north to south, in ALEVIN y increases from top to bottom

                      NullDemand null_dem = new NullDemand(node);
                      node.add(null_dem);

                      virtNodes.put(n.id, node);

                      if (!virtNet.addVertex(node)) {
                          throw new AssertionError("Failed to add Node " + n.id + " to virtual network " + virtNet.getLayer());
                      }
                }                
            }
            
		    for (Link l : links.values()) {
                if (substrate) {
                    SubstrateLink link = new SubstrateLink();

                    double bw = l.preinstCap.cap;
                    double cost = l.preinstCap.cost;
                    
                    for (Capacity c : l.upgrades) {
                        bw += c.count * c.cap;
                        cost += c.count * c.cost;
                    }
                    
                    BandwidthResource bw_res = new BandwidthResource(link);
                    bw_res.setBandwidth(bw);

                    CostResource cost_res = new CostResource(link);
                    cost_res.setCost(cost);

                    link.add(bw_res);
                    link.add(cost_res);

                    if (!newStack.getSubstrate().addEdge(link, substNodes.get(l.src), substNodes.get(l.dest))) {
                        throw new AssertionError("Failed to add Edge " + l.id + " to substrate network");
                    }
                } else {
                    VirtualLink link = new VirtualLink(virtNet.getLayer());

                    double bw = l.preinstCap.cap;
                    double cost = l.preinstCap.cost;
                    
                    for (Capacity c : l.upgrades) {
                        bw += c.count * c.cap;
                        cost += c.count * c.cost;
                    }
                                        
                    BandwidthDemand bw_dem = new BandwidthDemand(link);
                    bw_dem.setDemandedBandwidth(bw);

                    CostDemand cost_dem = new CostDemand(link);
                    cost_dem.setCost(cost);

                    link.add(bw_dem);
                    link.add(cost_dem);

                    if (!virtNet.addEdge(link, virtNodes.get(l.src), virtNodes.get(l.dest))) {
                        throw new AssertionError("Failed to add Edge " + l.id + " to virtual network " + virtNet.getLayer());
                    }
                }
		    }

		    if (substrate) {
		        scenario.setNetworkStack(newStack);
		    } else {
		        scenario.getNetworkStack().addLayer(virtNet);
		    }
		} else {
			System.err.println("ERROR: SNDlib Importer initialization failed.");
			throw new Error();
		}
	}

	/**
	 * Returns if this importer instance was successfully initialized.
	 * @return true, if initialization was successful, false, if not
	 */
	public boolean isInitialized() {
	    return initialized;
	}
	
    /**
     * Returns if the resulting network type is already set.
     * @return true, if type is set, false, if not
     */
	public boolean isTypeSet() {
	    return typeSet;
	}
	
    /**
     * Returns if the resulting network will be a substrate net.
     * @return true, if the net will be a substrate net, false, if it will be a
     *         virtual net
     */
	public boolean isSubstrate() {
	    return substrate;
	}
	
}
