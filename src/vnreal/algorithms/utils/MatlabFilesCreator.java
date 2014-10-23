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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Class that helps to create the files to have an interface with the Matlab
 * program of paths algebra
 * 
 * @author Juan Felipe Botero
 * @since 2011-04-18
 */

public class MatlabFilesCreator {
	// PRIVATE //
	private static FileWriter fstream, fstream1, fstream2, fstream3, fstream4;
	private static final String TOPOLOGY_FILE_NAME = "substrateNetwork.dat";
	private static final String BANDWIDTH_RESOURCE_FILE_NAME = "substrateMetric1.dat";
	private static final String CPU_RESOURCE_FILE_NAME = "substrateMetric2.dat";
	private static final String PATHS_ORDER_CRITERIA_FILE_NAME = "virtualStart.dat";
	private static final String VIRTUAL_NETWORK_REQUEST = "virtualRequest_";
	private static final String NODE_MAPPING = "virtual_to_Real_";
	private static final String HIDDEN_HOPS_DEMAND = "virtualHidden_";
	private static final String BANDWIDTH_DEMAND_FILE_NAME = "virtualMetric1_";
	private static final String CPU_DEMAND_FILE_NAME = "virtualMetric2_";
	private static final String FINISHING_FILE_NAME = "virtualFinish.dat";
	private static final String DELETESCRIPT = "script3.sh";
	private static final String VIRTUAL_NODE_MAPPING_SUCCESS = "virtualnodemapping_";

	private static final String MATLAB_PATH = "/home/jfb/PathsAlgebra/Files/";

	// Empty constructor
	public MatlabFilesCreator() {

	}

	/**
	 * This function creates in the MATLAB_PATH folder the needed files for the
	 * matlab program to start working (substrate topology, substrate Bandwidth
	 * Resource and substrate CPU resource)
	 * 
	 * @param sNetTopo
	 * @param sNetBw
	 * @param sNetCpu
	 */
	public static void createSubstrateFiles(int[][] sNetTopo,
			double[][] sNetBw, double[] sNetCpu, int networksOrder,
			int pathOrder, int numberOfRequests) {
		try {
			fstream = new FileWriter(MATLAB_PATH + TOPOLOGY_FILE_NAME);
			fstream1 = new FileWriter(MATLAB_PATH
					+ BANDWIDTH_RESOURCE_FILE_NAME);
			fstream2 = new FileWriter(MATLAB_PATH + CPU_RESOURCE_FILE_NAME);
			fstream3 = new FileWriter(MATLAB_PATH
					+ PATHS_ORDER_CRITERIA_FILE_NAME);

			// Create file SubstrateNetwork.dat file (file with a matrix with
			// the topology of the substrate)
			BufferedWriter out = new BufferedWriter(fstream);
			BufferedWriter out1 = new BufferedWriter(fstream1);
			BufferedWriter out2 = new BufferedWriter(fstream2);
			BufferedWriter out3 = new BufferedWriter(fstream3);
			out3.write(networksOrder + " " + pathOrder + " " + numberOfRequests
					+ "\n");

			for (int i = 0; i < sNetCpu.length; i++) {
				out2.write(sNetCpu[i] + " ");
				out.write("\n");
				out1.write("\n");
				for (int j = 0; j < sNetCpu.length; j++) {
					out.write(sNetTopo[i][j] + " ");
					out1.write(sNetBw[i][j] + " ");
				}
			}
			out.close();
			out1.close();
			out2.close();
			out3.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void removeVirtualFiles() {

	}

	public static void createVirtualFiles(int[][] vNetTopo, double[][] vNetBw,
			double[] vNetCpu, /* int[] vNetMapping, */double hhDemand[][],
			int vNetId) {
		try {
			fstream = new FileWriter(MATLAB_PATH + VIRTUAL_NETWORK_REQUEST
					+ vNetId);
			// fstream1 = new FileWriter(MATLAB_PATH + NODE_MAPPING + vNetId);
			fstream2 = new FileWriter(MATLAB_PATH + HIDDEN_HOPS_DEMAND + vNetId);
			fstream3 = new FileWriter(MATLAB_PATH + BANDWIDTH_DEMAND_FILE_NAME
					+ vNetId);
			fstream4 = new FileWriter(MATLAB_PATH + CPU_DEMAND_FILE_NAME
					+ vNetId);

			// Create file SubstrateNetwork.dat file (file with a matrix with
			// the topology of the substrate)
			BufferedWriter out = new BufferedWriter(fstream);
			// BufferedWriter out1 = new BufferedWriter(fstream1);
			BufferedWriter out2 = new BufferedWriter(fstream2);
			BufferedWriter out3 = new BufferedWriter(fstream3);
			BufferedWriter out4 = new BufferedWriter(fstream4);

			for (int i = 0; i < vNetCpu.length; i++) {
				// Matlab identifiers starts in i+1;
				// out1.write(Integer.toString(i + 1) + " " +
				// Integer.toString(vNetMapping[i] +1) + "\n");
				out4.write(vNetCpu[i] + " ");
				out.write("\n");
				out2.write("\n");
				out3.write("\n");
				for (int j = 0; j < vNetCpu.length; j++) {
					out.write(vNetTopo[i][j] + " ");
					out2.write(hhDemand[i][j] + " ");
					out3.write(vNetBw[i][j] + " ");
				}
			}
			out.close();
			// out1.close();
			out2.close();
			out3.close();
			out4.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void createVirtualFiles(int[] vNetMapping, int vNetId) {
		try {
			fstream1 = new FileWriter(MATLAB_PATH + NODE_MAPPING + vNetId);
			BufferedWriter out1 = new BufferedWriter(fstream1);
			for (int i = 0; i < vNetMapping.length; i++)
				out1.write(Integer.toString(i + 1) + " "
						+ Integer.toString(vNetMapping[i] + 1) + "\n");

			out1.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void createFinishFile() {
		try {
			fstream = new FileWriter(MATLAB_PATH + FINISHING_FILE_NAME);
			// Create file SubstrateNetwork.dat file (file with a matrix with
			// the topology of the substrate)
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("1\n");
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void deleteAllFiles() {
		Process pr;
		try {
			Runtime run = Runtime.getRuntime(); // For matlab scripts execution
			pr = run.exec(MATLAB_PATH + DELETESCRIPT);
			// // Lines to test the execution of commands
			String line;
			pr.waitFor();
			BufferedReader buf = new BufferedReader(new InputStreamReader(pr
					.getInputStream()));
			while ((line = buf.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void createSuccessNodeMappingFile(int vNetId, int result) {
		try {
			fstream = new FileWriter(MATLAB_PATH + VIRTUAL_NODE_MAPPING_SUCCESS + vNetId + ".dat");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(Integer.toString(result));
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	

}
