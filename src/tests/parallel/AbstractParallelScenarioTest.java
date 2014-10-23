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
package tests.parallel;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

import tests.io.PrintWriterDataReceiver;
import tests.scenarios.AbstractScenarioTest;
import tests.scenarios.AbstractScenarioTest.ScenarioData;
import tests.scenarios.AbstractScenarioTest.TestConfiguration;
import vnreal.network.NetworkStack;

/**
 * @author Michael Duelli
 * @author Daniel Schlosser
 * @author Vlad Singeorzan
 * @author Michael Till Beck
 */
public abstract class AbstractParallelScenarioTest extends
		AbstractScenarioTest<ScenarioData, TestConfiguration> implements Runnable {
	// CHANGES TO JUNIT: "implements Runnable"
	// CHANGES TO JUNIT: Removed JUnit entries
	
	PrintWriterDataReceiver writer = null;

	protected AbstractParallelScenarioTest(TestConfiguration c, String name, boolean isPathSplittingAlgorithm, int start, int end) throws IOException {
		super(c, name, isPathSplittingAlgorithm);
		this.startNumber = start;
		this.endNumber = end;
		
		this.writer = new PrintWriterDataReceiver(new PrintWriter(new FileWriter(
				name + "_out.txt", true), true));
	}
	
	@Override
	public LinkedList<ScenarioData> getParams(ScenarioData data) {
		LinkedList<ScenarioData> result = new LinkedList<ScenarioData>();
		result.add(data);
		return result;
	}

	// CHANGES TO JUNIT: ADded start and endnumber
	private int startNumber;
	private int endNumber;

	// CHANGES TO JUNIT: renamed runAlgorithm to run
	@Override
	public void run() {

		int[] numSNodesToRemoveQueue = new int[c.numSNodesToRemoveArray.length + 1];
		System.arraycopy(c.numSNodesToRemoveArray, 0, numSNodesToRemoveQueue, 0, c.numSNodesToRemoveArray.length);
		
		int pos = 0;
		for (double alpha : c.alphaArray) {
			for (double beta : c.betaArray) {
				for (int numVNodesPerVNet : c.numVNodesPerVNetArray) {
					for (int numVNets : c.numVNetsArray) {
						for (int numSNodes : c.numSNodesArray) {

							for (int scenario = 0; scenario < c.numScenarios; scenario++) {

								ScenarioData scenariodata = new ScenarioData(
										alpha, beta, numSNodes, numVNets,
										numVNodesPerVNet);
								NetworkStack stack = generate(scenariodata,
										scenario);

								for (int numSNodesToRemove : numSNodesToRemoveQueue) {

									pos++;
									// Generate scenario
									// FIXME UniformStream.setSeed(0);
									// CHANGES TO JUNIT: Start and endnumber
									if (startNumber <= pos && endNumber >= pos) {

										// Create new empty network stack.
										final String suffix = numSNodes
												+ "_" + numVNets + "_"
												+ numVNodesPerVNet + "_"
												+ scenario;

										// Override abstract method to run
										// algorithm.
										for (int j = 0; j < numRunsPerScenario; j++) {
											String scenario_suffix = suffix
													+ "_" + j;
											// CHANGES TO JUNIT: Added ThreadID
											// to output
											System.out.println("Thread: "
													+ Thread.currentThread().getId() + " Run "
													+ scenario_suffix);

											// Reset previous mappings
											stack.clearMappings();

											long startTime = System.currentTimeMillis();
											runAlgorithm(stack, scenariodata);
											long elapsedTime = System.currentTimeMillis() - startTime;

											evaluate(writer, stack, scenario_suffix, elapsedTime);
										}
									}
									
									removeRandomNodes(stack, numSNodesToRemove);
									scenariodata.numSNodes -= numSNodesToRemove;
								}
							}
						}
					}
				}
			}
		}
	}

}
