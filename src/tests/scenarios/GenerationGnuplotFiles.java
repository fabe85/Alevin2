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
package tests.scenarios;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

public class GenerationGnuplotFiles {
	boolean hiddenHops, pathSplitting;
	String filePath = "/media/09A6-55A7/Evaluations/toPlot";
	String readPath = "/media/09A6-55A7/Evaluations/";
	int nodesPerVN;
	int numVNs;
	private File fFile;

	public GenerationGnuplotFiles(boolean hH, boolean isPs, int nodesPerVN,
			int numVNs) {
		this.hiddenHops = hH;
		this.pathSplitting = isPs;
		this.nodesPerVN = nodesPerVN;
		this.numVNs = numVNs;
		fFile = null;
		if (isPs) {
			if (hH) {
				filePath += "/PSAlgorithms/HiddenHopsAlgorithms/"
						+ Integer.toString(nodesPerVN) + "nodesPerVNet-"
						+ Integer.toString(numVNs) + "VNets/";
			} else {
				filePath += "/ResultsAllAlgorithms/"
						+ Integer.toString(nodesPerVN) + "nodesPerVNet-"
						+ Integer.toString(numVNs) + "VNets/";
			}
		} else {
			if (hH) {
				filePath += "/SPAlgorithms/HiddenHopsAlgorithms/"
						+ Integer.toString(nodesPerVN) + "nodesPerVNet-"
						+ Integer.toString(numVNs) + "VNets/";
			} else {
				filePath += "/ResultsAllAlgorithms/"
						+ Integer.toString(nodesPerVN) + "nodesPerVNet-"
						+ Integer.toString(numVNs) + "VNets/";
			}
		}
	}

	public void generateFiles(LinkedList<String> algorithms) throws IOException {
		Scanner scanner;
		int i;
		LinkedList<String> readPaths = new LinkedList<String>();
		String currAlgorithm;
		String[] metricValues;
		LinkedList<Double> costRev = new LinkedList<Double>();
		LinkedList<Double> cost = new LinkedList<Double>();
		LinkedList<Double> mappedRev = new LinkedList<Double>();
		LinkedList<Double> linkUti = new LinkedList<Double>();
		LinkedList<Double> nodeUti = new LinkedList<Double>();
		LinkedList<Double> maxLinkSt = new LinkedList<Double>();
		LinkedList<Double> maxNodeSt = new LinkedList<Double>();
		LinkedList<Double> VNaccepRatio = new LinkedList<Double>();
		LinkedList<Double> revRatio = new LinkedList<Double>();
		LinkedList<Double> remaLinkRes = new LinkedList<Double>();
		LinkedList<Double> avLinkCost = new LinkedList<Double>();
		LinkedList<Double> costRevTiMapRev = new LinkedList<Double>();
		for (i = 0; i < algorithms.size(); i++) {
			readPaths.add(readPath + algorithms.get(i) + "/toGraph/"
					+ "resultToDraw_" + Integer.toString(numVNs) + "_"
					+ Integer.toString(nodesPerVN) + ".dat");
		}
		Iterator<String> currAlgo = readPaths.iterator();
		while (currAlgo.hasNext()) {
			currAlgorithm = currAlgo.next().toString();
			fFile = new File(currAlgorithm);
			scanner = new Scanner(fFile);
			String nextLine;
			scanner.nextLine();
			scanner.nextLine();
			while (scanner.hasNextLine()) {
				nextLine = scanner.nextLine();
				metricValues = nextLine.split("		");
				for (i = 0; i < 10; i++) {
					costRev.add(Double.parseDouble(metricValues[1]));
					cost.add(Double.parseDouble(metricValues[2]));
					mappedRev.add(Double.parseDouble(metricValues[3]));
					linkUti.add(Double.parseDouble(metricValues[4]));
					nodeUti.add(Double.parseDouble(metricValues[5]));
					maxLinkSt.add(Double.parseDouble(metricValues[6]));
					maxNodeSt.add(Double.parseDouble(metricValues[7]));
					VNaccepRatio.add(Double.parseDouble(metricValues[8]));
					revRatio.add(Double.parseDouble(metricValues[9]));
					remaLinkRes.add(Double.parseDouble(metricValues[10]));
					avLinkCost.add(Double.parseDouble(metricValues[11]));
					costRevTiMapRev.add(Double.parseDouble(metricValues[12]));
				}
			}
		}

		writeFile("CostComparation", (minValue(cost) - minValue(cost) * 0.1),
				(maxValue(cost) + maxValue(cost) * 0.05), readPaths,
				algorithms, 3, "Cost");
		writeFile("CostRevenueComparation",
				(minValue(costRev) - minValue(costRev) * 0.1),
				(maxValue(costRev) + maxValue(costRev) * 0.05), readPaths,
				algorithms, 2, "C/R");
		writeFile("LinkUtilizationComparation",
				(minValue(linkUti) - minValue(linkUti) * 0.1),
				(maxValue(linkUti) + maxValue(linkUti) * 0.05), readPaths,
				algorithms, 5, "Link Utilization");
		writeFile("MappedRevenueComparation",
				(minValue(mappedRev) - minValue(mappedRev) * 0.1),
				(maxValue(mappedRev) + maxValue(mappedRev) * 0.05), readPaths,
				algorithms, 4, "Mapped Revenue");
		writeFile("MaxLinkStress",
				(minValue(maxLinkSt) - minValue(maxLinkSt) * 0.1),
				(maxValue(maxLinkSt) + maxValue(maxLinkSt) * 0.05), readPaths,
				algorithms, 7, "Max Link Stress");
		writeFile("MaxNodeStress",
				(minValue(maxNodeSt) - minValue(maxNodeSt) * 0.1),
				(maxValue(maxNodeSt) + maxValue(maxNodeSt) * 0.05), readPaths,
				algorithms, 8, "Max Node Stress");
		writeFile("NodeUtilizationComparation",
				(minValue(nodeUti) - minValue(nodeUti) * 0.1),
				(maxValue(nodeUti) + maxValue(nodeUti) * 0.05), readPaths,
				algorithms, 6, "Max Node Stress");
		writeFile("RevenueRatio",
				(minValue(revRatio) - minValue(revRatio) * 0.1),
				(maxValue(revRatio) + maxValue(revRatio) * 0.05), readPaths,
				algorithms, 10, "Percentage Accepted Revenue");
		writeFile("VNAcceptanceRatio",
				(minValue(VNaccepRatio) - minValue(VNaccepRatio) * 0.1),
				(maxValue(VNaccepRatio) + maxValue(VNaccepRatio) * 0.05),
				readPaths, algorithms, 9, "VNs Acceptance Ratio");
		writeFile("AvailableLinkResource",
				(minValue(remaLinkRes) - minValue(remaLinkRes) * 0.1),
				(maxValue(remaLinkRes) + maxValue(remaLinkRes) * 0.05),
				readPaths, algorithms, 11, "Remaining Link Resource");
		writeFile("LinkCostPerMappedVNR",
				(minValue(avLinkCost) - minValue(avLinkCost) * 0.1),
				(maxValue(avLinkCost) + maxValue(avLinkCost) * 0.05),
				readPaths, algorithms, 12, "Average Link Cost per Mapped VNR");
		writeFile("CostRevTiMapRev",
				(minValue(costRevTiMapRev) - minValue(costRevTiMapRev) * 0.1),
				(maxValue(costRevTiMapRev) + maxValue(costRevTiMapRev) * 0.05),
				readPaths, algorithms, 13, "Cost REvenue times MappeRev Ratio");

	}

	private void writeFile(String file, double minValue, double maxValue,
			LinkedList<String> readPa, LinkedList<String> algo, int column,
			String name) throws IOException {
		FileWriter fstream;
		fstream = new FileWriter(filePath + file + ".gnuplot");
		BufferedWriter output = new BufferedWriter(fstream);
		output.write("set term postscript eps enhanced color\n");
		output.write("set output '" + file + ".eps'\n");
		output.write("set boxwidth 0.2 absolute\n");
		output.write("set title \"" + file + "\"\n");
		output.write("set xlabel 'rho'\n");
		output.write("set ylabel '" + name + "'\n");
		output.write("set xrange [ 0.1 : 0.9 ] noreverse nowriteback\n");
		output.write("set yrange [" + Double.toString(minValue) + " : "
				+ Double.toString(maxValue) + " ] noreverse nowriteback\n");
		output.write("plot '" + readPa.get(0) + "' using 1:"
				+ Integer.toString(column) + " title \"" + algo.get(0)
				+ "\"  with linespoint, \\\n");
		for (int i = 1; i < algo.size() - 1; i++) {
			output.write("     '" + readPa.get(i) + "' using 1:"
					+ Integer.toString(column) + " title \"" + algo.get(i)
					+ "\"  with linespoint, \\\n");
		}
		output.write("     '" + readPa.get(algo.size() - 1) + "' using 1:"
				+ Integer.toString(column) + " title \""
				+ algo.get(algo.size() - 1) + "\"  with linespoint \n");
		output.write("set output\n");
		output.write("quit");
		output.close();
	}

	private double maxValue(LinkedList<Double> list) {
		double max = 0.0;
		double currValue;
		int i = 0;
		Iterator<Double> ite = list.iterator();
		while (ite.hasNext()) {
			ite.next();
			currValue = list.get(i);
			i++;
			if (currValue > max)
				max = currValue;

		}
		return max;
	}

	private double minValue(LinkedList<Double> list) {
		double min = 0.0;
		double currValue;
		int i = 0;
		boolean flag = true;
		Iterator<Double> ite = list.iterator();
		while (ite.hasNext()) {
			ite.next();
			if (flag) {
				flag = false;
				min = list.get(i);
			}
			currValue = list.get(i);
			i++;
			if (currValue < min)
				min = currValue;

		}
		return min;
	}

}
