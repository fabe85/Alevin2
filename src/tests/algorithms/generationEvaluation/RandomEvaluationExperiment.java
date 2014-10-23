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
package tests.algorithms.generationEvaluation;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import tests.io.PrintWriterDataReceiver;
import vnreal.Scenario;
import vnreal.io.XMLImporter;

public class RandomEvaluationExperiment {
	public static void main(String[] args) throws IOException {
		/*
		 * 
		 * Algorithm Options: 1-GreedyAR-KSP 2-GreedyAR-PS 3-DvINE-KSP
		 * 4-DvINE-SP 5-RvINE-KSP 6-RvINE-SP" Just an Example
		 */
		/*
		 * /* GenerationGnuplotFiles test; LinkedList<String> algorithms = new
		 * LinkedList<String>(); algorithms.add("DViNESP");
		 * algorithms.add("GARSP"); algorithms.add("RViNESP");
		 * algorithms.add("RoundingMP");
		 * 
		 * test = new GenerationGnuplotFiles(false, false, 15, 15); try {
		 * test.generateFiles(algorithms); } catch (IOException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */

		/*
		 * final PrintWriterDataReceiver writer; final int[] numSNodesArray = {
		 * 50 }; final int[] numVNetsArray = { 10, 15, 20 }; final int[]
		 * numVNodesPerVNetArray = { 20 }; final double[] rhoArray = { 0.2, 0.3,
		 * 0.4, 0.5, 0.6, 0.7 }; int numRunsPerScenario = 1; final int
		 * numScenarios = 10; String algoName = "GARSP"; String scenarioSuffix;
		 * 
		 * String filepath = "/home/jfb/Evaluations/" + algoName + "/";
		 * 
		 * writer = new PrintWriterDataReceiver(new PrintWriter(new FileWriter(
		 * filepath + algoName + "_out.txt"), true));
		 * 
		 * for (int numVNodesPerVNet : numVNodesPerVNetArray) for (int numVNets
		 * : numVNetsArray) for (int numSNodes : numSNodesArray) for (double rho
		 * : rhoArray) for (int scenarioNumber = 0; scenarioNumber <
		 * numScenarios; scenarioNumber++) for (int scenarioRun = 0; scenarioRun
		 * < numRunsPerScenario; scenarioRun++) { scenarioSuffix = numSNodes +
		 * "_" + numVNets + "_" + numVNodesPerVNet + "_" + scenarioNumber + "_"
		 * + rho + "_" + scenarioRun; new ScenarioImporter(filepath +
		 * "Scenario-mapped_" + scenarioSuffix + ".xml") .setNetworkStack();
		 * NetworkStack<?,?,?,?> stack =
		 * ToolKit.getScenario().getNetworkStack();
		 * 
		 * for (EvaluationMetric metric :
		 * DefaultExperiments.getDefaultMetrics()) { metric.setStack(stack);
		 * double y = metric.getValue();
		 * 
		 * writer.receive( stack, metric, scenarioSuffix, y, -1); }
		 * 
		 * 
		 * }
		 * 
		 * String path = "ILP-LP-Models/testFile.cvs"; List<EvaluationMetric>
		 * metrics = new LinkedList<EvaluationMetric>(); metrics.add(new
		 * CostRevenue(false)); metrics.add(new Cost()); metrics.add(new
		 * MappedRevenue(false)); metrics.add(new LinkUtilization());
		 * metrics.add(new NodeUtilization()); metrics.add(new MaxLinkStress());
		 * metrics.add(new MaxNodeStress()); metrics.add(new
		 * AcceptedVnrRatio()); metrics.add(new RatioMappedRevenue(false)); for
		 * (Iterator<EvaluationMetric> tmpMetric = metrics.iterator(); tmpMetric
		 * .hasNext();) { EvaluationMetric currMetric = tmpMetric.next();
		 * currMetric.setStack(ToolKit.getScenario().getNetworkStack()); }
		 * EvaluationFileGeneration outputFile = new
		 * EvaluationFileGeneration(path);
		 * outputFile.createEvaluationFile(metrics);
		 */

		/*
		 * final int[] numSNodesArray = { 50 }; final int[] numVNetsArray = {
		 * 10,15,20,50 }; final int[] numVNodesPerVNetArray = { 20 }; final
		 * double[] rhoArray = { 0.2, 0.3, 0.4, 0.5, 0.6, 0.7 }; int
		 * numRunsPerScenario = 1; final int numScenarios = 10; String algoName
		 * = "GARSP"; String scenarioSuffix;
		 * 
		 * String filepath = "/media/09A6-55A7/Evaluations/" + algoName + "/";
		 * 
		 * for (int numVNodesPerVNet : numVNodesPerVNetArray) for (int numVNets
		 * : numVNetsArray) for (int numSNodes : numSNodesArray) for (double rho
		 * : rhoArray) for (int scenarioNumber = 0; scenarioNumber <
		 * numScenarios; scenarioNumber++) for (int scenarioRun = 0; scenarioRun
		 * < numRunsPerScenario; scenarioRun++) { scenarioSuffix = numSNodes +
		 * "_" + numVNets + "_" + numVNodesPerVNet + "_" + scenarioNumber + "_"
		 * + rho + "_" + scenarioRun; new ScenarioImporter(filepath +
		 * "Scenario-mapped_" + scenarioSuffix + ".xml").setNetworkStack();
		 * NetworkStack stack = ToolKit .getScenario().getNetworkStack();
		 * 
		 * String path = filepath + "Evaluation-results_" + scenarioSuffix + "-"
		 * + algoName + ".csv"; List<EvaluationMetric> metrics = new
		 * LinkedList<EvaluationMetric>(); metrics.add(new CostRevenue(false));
		 * metrics.add(new Cost()); metrics.add(new MappedRevenue(false));
		 * metrics.add(new CostRevTimesMappedRev(false)); metrics.add(new
		 * LinkUtilization()); metrics.add(new NodeUtilization());
		 * metrics.add(new MaxLinkStress()); metrics.add(new MaxNodeStress());
		 * metrics.add(new AcceptedVnrRatio()); metrics.add(new
		 * RatioMappedRevenue(false)); metrics.add(new RemainingLinkResource());
		 * metrics.add(new LinkCostPerVnr()); for (Iterator<EvaluationMetric>
		 * tmpMetric = metrics .iterator(); tmpMetric.hasNext();) {
		 * EvaluationMetric currMetric = tmpMetric .next();
		 * currMetric.setStack(stack); } EvaluationFileGeneration outputFile =
		 * new EvaluationFileGeneration( path);
		 * outputFile.createEvaluationFile(metrics);
		 * 
		 * }
		 */

		/*
		 * GenerationGnuplotFiles test; LinkedList<String> algorithms = new
		 * LinkedList<String>(); algorithms.add("RViNEPS");
		 * algorithms.add("RoundingMP"); algorithms.add("DViNESP");
		 * algorithms.add("DViNEPS"); algorithms.add("RViNESP");
		 * algorithms.add("GARPS"); algorithms.add("GARSP"); test = new
		 * GenerationGnuplotFiles(false, false, 20, 10); try {
		 * test.generateFiles(algorithms); } catch (IOException e) {
		 * e.printStackTrace(); } test = new GenerationGnuplotFiles(false,
		 * false, 20, 15); try { test.generateFiles(algorithms); } catch
		 * (IOException e) { e.printStackTrace(); } test = new
		 * GenerationGnuplotFiles(false, false, 20, 20); try {
		 * test.generateFiles(algorithms); } catch (IOException e) {
		 * e.printStackTrace(); } test = new GenerationGnuplotFiles(false,
		 * false, 20, 50); try { test.generateFiles(algorithms); } catch
		 * (IOException e) { e.printStackTrace(); }
		 */

		final int[] numSNodesArray = { 50 };
		final int[] numVNetsArray = { 50 };
		final int[] numVNodesPerVNetArray = { 20 };
		final double[] rhoArray = { 0.2, 0.3, 0.4, 0.5, 0.6, 0.7 };
		int numRunsPerScenario = 1;
		final int numScenarios = 10;
		String algoName = "GARPS";
		String scenarioSuffix;
		PrintWriterDataReceiver writer;
		writer = new PrintWriterDataReceiver(new PrintWriter(new FileWriter(
				algoName + "_out_50.txt", true), true));

		String filepath = "/media/09A6-55A7/Evaluations/" + algoName + "/";

		for (int numVNodesPerVNet : numVNodesPerVNetArray)
			for (int numVNets : numVNetsArray)
				for (int numSNodes : numSNodesArray)
					for (double rho : rhoArray)
						for (int scenarioNumber = 0; scenarioNumber < numScenarios; scenarioNumber++)
							for (int scenarioRun = 0; scenarioRun < numRunsPerScenario; scenarioRun++) {
								scenarioSuffix = numSNodes + "_" + numVNets
										+ "_" + numVNodesPerVNet + "_"
										+ scenarioNumber + "_" + rho + "_"
										+ scenarioRun;
								Scenario scenario = new Scenario();
								XMLImporter.importScenario(filepath
										+ "Scenario-mapped_" + scenarioSuffix
										+ ".xml", scenario);
								
//								NetworkStack stack = scenario.getNetworkStack();
//								for (EvaluationMetric metric : getMetrics()) {
//									metric.setStack(stack);
//									double y = metric.getValue();
//
//									writer.receive(stack, metric.getClass().getSimpleName(),
//											scenarioSuffix, y);
//								}

							}

		/*
		 * GraphsFileGeneration test; test = new GraphsFileGeneration("GARSP",
		 * false); test.generateFiles(); test = new
		 * GraphsFileGeneration("GARPS", false); test.generateFiles(); test =
		 * new GraphsFileGeneration("DViNEPS", false); test.generateFiles();
		 * test = new GraphsFileGeneration("DViNESP", false);
		 * test.generateFiles(); test = new GraphsFileGeneration("RViNEPS",
		 * true); test.generateFiles(); test = new
		 * GraphsFileGeneration("RViNESP", true); test.generateFiles(); test =
		 * new GraphsFileGeneration("RoundingMP", true); test.generateFiles();
		 */

	}
}
