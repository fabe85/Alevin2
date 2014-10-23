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
package vnreal.algorithms.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualNetwork;

/**
 * Class to read results files from the output of Matlab software and generates
 * the substrate path for each virtual link mapping
 * 
 * @author Juan Felipe Botero
 * @since 2011-04-26
 */

public class ReadResultMatlabFiles {
	// PRIVATE//

	private static final String MAPPING_PERFORMED = "virtualSuccess_";
	private static final String RESULTING_PATH = "virtualAttend_";
	private static final String VIRTUAL_NETWORKS_ORDER = "requestOrder";

	private static final String MATLAB_PATH = "/home/jfb/PathsAlgebra/Files/";
	
	private static final String NODEMAPPING_PERFORMED_FILE = "virtualnodemapping_";

	private int virtualNetReqNumber;
	private File fFile;
	private static File fFile1, fFile2;

	public ReadResultMatlabFiles(int number) {
		this.virtualNetReqNumber = number;
	}

	/**
	 * Function that indicates if the virtual link mapping could be performed
	 * 
	 * @return boolean value indicating if the virtual link mapping could be
	 *         performed
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("static-access")
	public boolean isVnrMapped() throws FileNotFoundException {
		
		try{
			  Thread.currentThread().sleep(500);
			}catch(InterruptedException ie){
			//If this thread was interrupted by another thread 
			}
		String filePath = MATLAB_PATH + MAPPING_PERFORMED + virtualNetReqNumber;
		fFile = new File(filePath);
		Scanner scanner = new Scanner(fFile);
		String[] decision;
		while (scanner.hasNextLine()) {

			decision = scanner.nextLine().split("   ");
			if (((int) (Double.parseDouble(decision[1]))) == 1) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public boolean isMappingDone() throws FileNotFoundException {
		String filePath = MATLAB_PATH + MAPPING_PERFORMED + virtualNetReqNumber;
		File ffile = new File(filePath);
		return ffile.exists();
	}

	/**
	 * 
	 * @param sNet
	 *            (Substrate Network)
	 * @param virtualOrigin
	 *            (value with the id of the virtual origin node)
	 * @param virtualDestination
	 *            (value with the id of the virtual destination node)
	 * @return Path in the substrate network mapping the virtual link
	 * @throws FileNotFoundException
	 */
	public List<SubstrateLink> resultingPath(SubstrateNetwork sNet,
			int virtualOrigin, int virtualDestination)
			throws FileNotFoundException {
		String filePath = MATLAB_PATH + RESULTING_PATH + virtualNetReqNumber
				+ "_" + Integer.toString(virtualOrigin + 1) + "_"
				+ Integer.toString(virtualDestination + 1);
		String aLine;
		String[] substrateNodesPath;
		fFile = new File(filePath);
		List<SubstrateLink> mappedPath = new LinkedList<SubstrateLink>();
		Map<Long, SubstrateNode> subsNodesFromId = substrateNodes(sNet);
		Scanner scanner = new Scanner(fFile);
		while (scanner.hasNextLine()) {
			aLine = scanner.nextLine();
			substrateNodesPath = aLine.split("   ");
			for (int i = 1; i + 1 < substrateNodesPath.length; i++) {
				mappedPath
						.add(sNet
								.findEdge(
										subsNodesFromId
												.get(((int) (Double
														.parseDouble(substrateNodesPath[i]))) - 1),
										subsNodesFromId
												.get(((int) (Double
														.parseDouble(substrateNodesPath[i + 1]))) - 1)));
			}
		}
		return mappedPath;
	}

	private Map<Long, SubstrateNode> substrateNodes(SubstrateNetwork sNet) {
		Map<Long, SubstrateNode> idToSubstrate = new LinkedHashMap<Long, SubstrateNode>();
		for (Iterator<SubstrateNode> itt = sNet.getVertices().iterator(); itt
				.hasNext();) {
			SubstrateNode tempSubsNode = itt.next();
			idToSubstrate.put(tempSubsNode.getId(), tempSubsNode);
		}
		return idToSubstrate;
	}
	
	
	public static NetworkStack orderRequests(NetworkStack stack){
		NetworkStack newStack =null;
		List<VirtualNetwork> orderedVnets = new LinkedList<VirtualNetwork>();
		String filePath = MATLAB_PATH + VIRTUAL_NETWORKS_ORDER;
		fFile1 = new File(filePath);
		Scanner scanner;
		try {
			scanner = new Scanner(fFile1);
			String decision;
			int currVnetLayer;
			while (scanner.hasNextLine()) {
				decision = scanner.nextLine();
				currVnetLayer = (int) (Double.parseDouble(decision));
				orderedVnets.add((VirtualNetwork) stack.getLayer(currVnetLayer));
			}
			newStack = new NetworkStack(stack.getSubstrate(), orderedVnets);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newStack;		
	} 
	
	public static int getFirstVnetLayer(){
		fFile1 = new File(MATLAB_PATH + VIRTUAL_NETWORKS_ORDER);
		Scanner scanner, scanner1;
		try {
			scanner = new Scanner(fFile1);
			String decision;
			int currVnetLayer;
			while (scanner.hasNextLine()) {
				decision = scanner.nextLine();
				currVnetLayer = (int) (Double.parseDouble(decision));
				fFile2 = new File(MATLAB_PATH + NODEMAPPING_PERFORMED_FILE + Integer.toString(currVnetLayer) + ".dat");
				scanner1 = new Scanner(fFile2);
				decision = scanner1.nextLine();
				if((int) (Double.parseDouble(decision)) == 1)
					return currVnetLayer;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;		
	} 
	

}
