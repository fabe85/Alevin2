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

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import mulavito.algorithms.IAlgorithm;
import tests.scenarios.AbstractScenarioTest.ScenarioData;
import tests.scenarios.AbstractScenarioTest.TestConfiguration;
import vnreal.algorithms.CoordinatedMappingRoundingPathStripping;
import vnreal.algorithms.GenericMappingAlgorithm;
import vnreal.evaluations.metrics.AcceptedVnrRatio;
import vnreal.evaluations.metrics.Cost;
import vnreal.evaluations.metrics.CostRevenue;
import vnreal.evaluations.metrics.EvaluationMetric;
import vnreal.evaluations.metrics.LinkUtilization;
import vnreal.evaluations.metrics.MappedRevenue;
import vnreal.evaluations.metrics.MaxLinkStress;
import vnreal.evaluations.metrics.MaxNodeStress;
import vnreal.evaluations.metrics.NodeUtilization;
import vnreal.evaluations.metrics.RatioMappedRevenue;
import vnreal.evaluations.metrics.Runtime;
import vnreal.evaluations.utils.EvaluationFileGeneration;
import vnreal.network.NetworkStack;

public class RoundingMP extends AbstractScenarioTest<ScenarioData, TestConfiguration> {

	public RoundingMP(TestConfiguration c, String name) {
		super(c, name, false);

		numRunsPerScenario = 1;
	}
	
	@Override
	public LinkedList<ScenarioData> getParams(ScenarioData data) {
		LinkedList<ScenarioData> result = new LinkedList<ScenarioData>();
		result.add(data);
		return result;
	}

	@Override
	public void runAlgorithm(NetworkStack stack, ScenarioData data) {

		/*
		 * Coordinated node and link mapping with Rounding Multipath Important
		 * Parameters Rounding Type: Deterministic rounding (More combinations
		 * can be used) Maximum number of shortest path to calculate: 20 Hidden
		 * Hops Considered: False Distance Value : 40
		 */

		String path = System.getProperty("user.home") + File.separator
				+ "Evaluation-results_" + data.getSuffix(0) + "-RoundingMP.csv";
		List<EvaluationMetric> metrics = new LinkedList<EvaluationMetric>();
		IAlgorithm algo = new CoordinatedMappingRoundingPathStripping(stack, 40, 1, 1, 0, 20, false);
		algo.performEvaluation();
		metrics.add(new CostRevenue(false));
		metrics.add(new Cost());
		metrics.add(new MappedRevenue(false));
		metrics.add(new LinkUtilization());
		metrics.add(new NodeUtilization());
		metrics.add(new MaxLinkStress());
		metrics.add(new MaxNodeStress());
		metrics.add(new AcceptedVnrRatio());
		metrics.add(new RatioMappedRevenue(false));
		GenericMappingAlgorithm mappingAlgo = (GenericMappingAlgorithm) algo;
		metrics.add(new Runtime(mappingAlgo.getRunningTime()));
		for (Iterator<EvaluationMetric> tmpMetric = metrics.iterator(); tmpMetric
				.hasNext();) {
			EvaluationMetric currMetric = tmpMetric.next();
			currMetric.setStack(stack);
		}
		EvaluationFileGeneration outputFile = new EvaluationFileGeneration(path);
		outputFile.createEvaluationFile(metrics);
	}

}
