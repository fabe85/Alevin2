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

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import mulavito.algorithms.IAlgorithm;

import org.junit.Test;

import tests.io.PrintWriterDataReceiver;
import vnreal.algorithms.isomorphism.AdvancedSubgraphIsomorphismAlgorithm;
import vnreal.algorithms.isomorphism.SubgraphIsomorphismStackAlgorithm;
import vnreal.evaluations.metrics.EvaluationMetric;
import vnreal.network.NetworkStack;

/**
 * Class to run as JUnit-Test to calculate some metrics considering
 * Security-Extensions. (class probably copied from SubgraphTest)
 * 
 * @author Julian Willerding
 */
public final class SecuritySubgraphTest extends SecurityScenarioTest {

	private final PrintWriterDataReceiver writer;
	private final PrintWriterDataReceiver writerDemProv;
	private final PrintWriterDataReceiver writerProvDem;

	public SecuritySubgraphTest(ScenarioData data) throws IOException {
		super(data);

		writer = new PrintWriterDataReceiver(new PrintWriter(new FileWriter(
				getName() + "_out.txt", true), true));
		writerDemProv = new PrintWriterDataReceiver(new PrintWriter(new FileWriter(
				getName() + "_out_DemProv.txt", true), true));
		writerProvDem = new PrintWriterDataReceiver(new PrintWriter(new FileWriter(
				getName() + "_out_ProvDem.txt", true), true));

		numRunsPerScenario = 1;
	}

	@Override
	public void generateCPUResourcesAndDemands(NetworkStack stack,
			int minResourceCPU, int maxResourceCPU, int minDemandCPU,
			int maxDemandCPU) {
		SecurityScenarioTest.generateRandomCPUResourcesAndDemands(stack,
				minResourceCPU, maxResourceCPU, minDemandCPU, maxDemandCPU);
	}

	@Override
	public void generateBandwidthResourceAndDemands(NetworkStack stack,
			int[] myBandwithResourceValues, int minDemandBandwidth,
			int maxDemandBandwidth) {
		SecurityScenarioTest.generateRandomBandwidthResourceAndDemands(stack,
				myBandwithResourceValues, minDemandBandwidth,
				maxDemandBandwidth);
	}

	@Override
	public void generatePhDem_VirtProvSecurityResourceAndDemands(NetworkStack stack,
			int minPhysicalDemandedSecurity, int maxPhysicalDemandedSecurity,
			int minVirtualProvidedSecurity, int maxVirtualProvidedSecurity) {
		SecurityScenarioTest.generateRandomPhDem_VirtProvSecurityResourceAndDemands(
				stack, minPhysicalDemandedSecurity,
				maxPhysicalDemandedSecurity, minVirtualProvidedSecurity,
				maxVirtualProvidedSecurity);
	}

        @Override
        public void removePhDem_VirtProvSecurityResourceAndDemands(NetworkStack stack) {
    		SecurityScenarioTest
    			.removeRandomPhDem_VirtProvSecurityResourceAndDemands(stack);
        }

	@Override
	public void generatePhProv_VirtDemSecurityResourceAndDemands(NetworkStack stack,
			int minPhysicalProvidedSecurity, int maxPhysicalProvidedSecurity,
			int minVirtualDemandSecurity, int maxVirtualDemandedSecurity) {
		SecurityScenarioTest.generateRandomPhProv_VirtDemSecurityResourceAndDemands(
				stack, minPhysicalProvidedSecurity,
				maxPhysicalProvidedSecurity, minVirtualDemandSecurity,
				maxVirtualDemandedSecurity);
	}

	@Override
	@Test
	public void runScenario() {
		super.runScenario();
		writer.finish();
	}

	@Override
	protected void evaluate(String scenarioSuffix, long elapsedTime,
			char security) {

		// AvailableResourcesPathSplitting.sortByRevenues(stack);

		for (EvaluationMetric metric : SecurityScenarioTest
				.getSecurityMetrics()) {
			metric.setStack(stack);
			double y = metric.getValue();

			if (y != 0.0 && !Double.isNaN(y)) {
				// First File without SecurityConstraints and SecondFile with them
				if (security == '0') {
				    writer.receive(stack, metric.getClass().getSimpleName(), scenario_suffix, y);
				} else if (security == 'D') {
				    writerDemProv.receive(stack, metric.getClass().getSimpleName(), scenario_suffix, y);
				} else if (security == 'P') {
				    writerProvDem.receive(stack, metric.getClass().getSimpleName(), scenario_suffix, y);
				}			    
			}

		}
	}

	@Override
	protected void runAlgorithm() {
		IAlgorithm algo = new SubgraphIsomorphismStackAlgorithm(stack,
				new AdvancedSubgraphIsomorphismAlgorithm(false));

		algo.performEvaluation();
	}
}
