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
import java.util.Scanner;

import vnreal.algorithms.utils.MiscelFunctions;

public class GraphsFileGeneration {
	String filePath = "/media/09A6-55A7/Evaluations/testGARPS/";
	boolean isRandom;
	protected int numRunsPerScenario = 1;
	private final static int numScenarios = 10;
	private File fFile;

	private static final int[] numSNodesArray = { 50 };

	private static final int[] numVNetsArray = { 10 };

	protected static final int[] numVNodesPerVNetArray = { 20 };

	private static final double[] rhoArray = { 0.2, 0.3, 0.4, 0.5, 0.6, 0.7 };

	String algoName;

	public GraphsFileGeneration(String algoName, boolean isRandom) {
		filePath = filePath + algoName;
		this.isRandom = isRandom;
		this.algoName = algoName;
		fFile = null;
		if (isRandom)
			numRunsPerScenario = 10;
	}

	public void generateFiles() throws IOException {
		double costRev, cost, mappedRev, linkUtilization, nodeUtilization, perAcceptedVNR, perMappedRevenue;
		double maxLinkStress, maxNodeStress, maxPerCostRevTiMapRev, remaLinkRes, avLinkCost, costRevTimesMapRev;
		double currCostRev = 700, currCost = 0, currMappedRev = 0, currLinkUtilization = 0, currNodeUtilization = 0, currCostRevTimesMapRev = 0;
		double currPerAcceptedVNR = 0, currPerMappedRevenue = 0, currMaxLinkStress = 0, currMaxNodeStress = 0, currRemaLinkRes = 0, currAvLinkCost = 0;
		Scanner scanner;
		FileWriter fstream;
		String readPath;
		boolean flag = true;
		BufferedWriter output = null;

		for (int numVNodesPerVNet : numVNodesPerVNetArray)
			for (int numVNets : numVNetsArray) {
				flag = true;
				for (int numSNodes : numSNodesArray)
					for (double rho : rhoArray) {
						costRev = 0;
						cost = 0;
						mappedRev = 0;
						linkUtilization = 0;
						nodeUtilization = 0;
						perAcceptedVNR = 0;
						perMappedRevenue = 0;
						maxLinkStress = 0;
						maxNodeStress = 0;
						remaLinkRes = 0;
						avLinkCost = 0;
						costRevTimesMapRev = 0;
						for (int scenarioNumber = 0; scenarioNumber < numScenarios; scenarioNumber++) {
							maxPerCostRevTiMapRev = 0;
							for (int scenarioRun = 0; scenarioRun < numRunsPerScenario; scenarioRun++) {
								readPath = filePath + "/Evaluation-results_"
										+ Integer.toString(numSNodes) + "_"
										+ Integer.toString(numVNets) + "_"
										+ Integer.toString(numVNodesPerVNet)
										+ "_"
										+ Integer.toString(scenarioNumber)
										+ "_" + Double.toString(rho) + "_"
										+ Integer.toString(scenarioRun) + "-"
										+ algoName + ".csv";
								if (!isRandom) {
									fFile = new File(readPath);
									scanner = new Scanner(fFile);
									String nextLine;
									String[] metrics;
									try {
										while (scanner.hasNextLine()) {
											nextLine = scanner.nextLine();
											if (!scanner.hasNextLine()) {
												metrics = nextLine.split(";");

												if (Double
														.parseDouble(metrics[2]) != 0) {
													costRev += Double
															.parseDouble(metrics[0]);
													costRevTimesMapRev += Double
															.parseDouble(metrics[3]);
												}

												cost += Double
														.parseDouble(metrics[1]);
												mappedRev += Double
														.parseDouble(metrics[2]);
												linkUtilization += Double
														.parseDouble(metrics[4]);
												nodeUtilization += Double
														.parseDouble(metrics[5]);
												maxLinkStress += Double
														.parseDouble(metrics[6]);
												maxNodeStress += Double
														.parseDouble(metrics[7]);
												perAcceptedVNR += Double
														.parseDouble(metrics[8]);
												perMappedRevenue += Double
														.parseDouble(metrics[9]);
												remaLinkRes += Double
														.parseDouble(metrics[10]);
												if (Double
														.parseDouble(metrics[1]) == 0) {
													avLinkCost += 0;
												} else {
													avLinkCost += Double
															.parseDouble(metrics[11]);
												}
											}
										}

									} finally {
										// ensure the underlying stream is
										// always closed
										scanner.close();
									}

								} else {
									// FIXME apply the code with Randomized
									// algorithms
									fFile = new File(readPath);
									scanner = new Scanner(fFile);
									String nextLine;
									String[] metrics;
									try {
										while (scanner.hasNextLine()) {
											nextLine = scanner.nextLine();
											if (!scanner.hasNextLine()) {
												metrics = nextLine.split(";");

												currCostRevTimesMapRev = Double
														.parseDouble(metrics[3]);
												if (currCostRevTimesMapRev >= maxPerCostRevTiMapRev) {
													if (currCostRevTimesMapRev != maxPerCostRevTiMapRev) {
														maxPerCostRevTiMapRev = currCostRevTimesMapRev;
														currCost = Double
																.parseDouble(metrics[1]);
														currMappedRev = Double
																.parseDouble(metrics[2]);
														currLinkUtilization = Double
																.parseDouble(metrics[4]);
														currNodeUtilization = Double
																.parseDouble(metrics[5]);
														currMaxLinkStress = Double
																.parseDouble(metrics[6]);
														currMaxNodeStress = Double
																.parseDouble(metrics[7]);
														currPerAcceptedVNR = Double
																.parseDouble(metrics[8]);
														currPerMappedRevenue = Double
																.parseDouble(metrics[9]);
														currCostRev = Double
																.parseDouble(metrics[0]);
														currRemaLinkRes = Double
																.parseDouble(metrics[10]);
														currAvLinkCost = Double
																.parseDouble(metrics[11]);

													} else {
														if (Double
																.parseDouble(metrics[0]) < currCostRev) {
															maxPerCostRevTiMapRev = currCostRevTimesMapRev;
															currCost = Double
																	.parseDouble(metrics[1]);
															currMappedRev = Double
																	.parseDouble(metrics[2]);
															currLinkUtilization = Double
																	.parseDouble(metrics[4]);
															currNodeUtilization = Double
																	.parseDouble(metrics[5]);
															currMaxLinkStress = Double
																	.parseDouble(metrics[6]);
															currMaxNodeStress = Double
																	.parseDouble(metrics[7]);
															currPerAcceptedVNR = Double
																	.parseDouble(metrics[8]);
															currPerMappedRevenue = Double
																	.parseDouble(metrics[9]);
															currCostRev = Double
																	.parseDouble(metrics[0]);
															currRemaLinkRes = Double
																	.parseDouble(metrics[10]);
															currAvLinkCost = Double
																	.parseDouble(metrics[11]);
														}
													}
												}
											}
										}

									} finally {
										// ensure the underlying stream is
										// always closed
										scanner.close();
									}

								}
							}
							if (isRandom) {
								if (currMappedRev != 0)
									costRev += currCostRev;

								cost += currCost;
								mappedRev += currMappedRev;
								linkUtilization += currLinkUtilization;
								nodeUtilization += currNodeUtilization;
								maxLinkStress += currMaxLinkStress;
								maxNodeStress += currMaxNodeStress;
								perAcceptedVNR += currPerAcceptedVNR;
								perMappedRevenue += currPerMappedRevenue;
								remaLinkRes += currRemaLinkRes;
								avLinkCost += currAvLinkCost;
								costRevTimesMapRev += maxPerCostRevTiMapRev;

							}
						}
						cost = cost / numScenarios;
						mappedRev = mappedRev / numScenarios;
						costRev = cost / mappedRev;
						linkUtilization = linkUtilization / numScenarios;
						nodeUtilization = nodeUtilization / numScenarios;
						maxLinkStress = maxLinkStress / numScenarios;
						maxNodeStress = maxNodeStress / numScenarios;
						perAcceptedVNR = perAcceptedVNR / numScenarios;
						perMappedRevenue = perMappedRevenue / numScenarios;
						remaLinkRes = remaLinkRes / numScenarios;
						avLinkCost = avLinkCost / numScenarios;
						costRevTimesMapRev = costRevTimesMapRev / numScenarios;

						String writingFilePath = filePath
								+ "/toGraph/resultToDraw_"
								+ Integer.toString(numVNets) + "_"
								+ Integer.toString(numVNodesPerVNet) + ".dat";

						fstream = new FileWriter(writingFilePath, true);
						output = new BufferedWriter(fstream);
						if (flag) {
							flag = false;
							output.write("#algorithm: " + algoName
									+ "NodesPerVN: "
									+ Integer.toString(numVNodesPerVNet)
									+ "Number of VNRs: "
									+ Integer.toString(numVNets));
							output.write("\n#load factor \t\tCost/Rev \t\tCost \t\tmappedRev \t\tlinkUti \t\tNodeUti \t\tmaxLinkStress \t\tmaxNodeStress \t\tVNRaccRatio \t\tMapRevRatio \t\tRemaLinkRes \t\tAvLinkCost \t\tCostRevTiMapRev\n");
						}
						output.write(Double.toString(MiscelFunctions
								.roundThreeDecimals(rho))
								+ "\t\t"
								+ Double.toString(MiscelFunctions
										.roundThreeDecimals(costRev))
								+ "\t\t"
								+ Double.toString(MiscelFunctions
										.roundThreeDecimals(cost))
								+ "\t\t"
								+ Double.toString(MiscelFunctions
										.roundThreeDecimals(mappedRev))
								+ "\t\t"
								+ Double.toString(MiscelFunctions
										.roundThreeDecimals(linkUtilization))
								+ "\t\t"
								+ Double.toString(MiscelFunctions
										.roundThreeDecimals(nodeUtilization))
								+ "\t\t"
								+ Double.toString(MiscelFunctions
										.roundThreeDecimals(maxLinkStress))
								+ "\t\t"
								+ Double.toString(MiscelFunctions
										.roundThreeDecimals(maxNodeStress))
								+ "\t\t"
								+ Double.toString(MiscelFunctions
										.roundThreeDecimals(perAcceptedVNR))
								+ "\t\t"
								+ Double.toString(MiscelFunctions
										.roundThreeDecimals(perMappedRevenue))
								+ "\t\t"
								+ Double.toString(MiscelFunctions
										.roundThreeDecimals(remaLinkRes))
								+ "\t\t"
								+ Double.toString(MiscelFunctions
										.roundThreeDecimals(avLinkCost))
								+ "\t\t"
								+ Double.toString(MiscelFunctions
										.roundThreeDecimals(costRevTimesMapRev))
								+ "\n");
						output.close();
					}
			}
	}
}
