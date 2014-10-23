package vnreal.algorithms.distributedAlg;

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

import java.io.IOException;
import java.util.LinkedList;

import mulavito.algorithms.IAlgorithm;
import tests.scenarios.AbstractScenarioTest;
import tests.scenarios.AbstractScenarioTest.ScenarioData;
import tests.scenarios.AbstractScenarioTest.TestConfiguration;
import vnreal.algorithms.distributedAlg.metriken.AverageMsgsReceivedPerNode;
import vnreal.algorithms.distributedAlg.metriken.AverageSentMsgsPerNode;
import vnreal.algorithms.distributedAlg.metriken.BFMessageCounter;
import vnreal.algorithms.distributedAlg.metriken.BFRunCounter;
import vnreal.algorithms.distributedAlg.metriken.ClusterCounter;
import vnreal.algorithms.distributedAlg.metriken.MessagesPerLink;
import vnreal.algorithms.distributedAlg.metriken.NodesUsedSolelyForForwarding;
import vnreal.algorithms.distributedAlg.metriken.NormalMessageCounter;
import vnreal.algorithms.distributedAlg.metriken.NotifyMessageCounter;
import vnreal.evaluations.metrics.EvaluationMetric;
import vnreal.network.NetworkStack;


public final class DistributedAlgTest extends AbstractScenarioTest<ScenarioData, TestConfiguration> {
	
	public DistributedAlgTest(TestConfiguration c, String name) {
		super(c, name, false);
	}
	
	@Override
	public LinkedList<ScenarioData> getParams(ScenarioData data) {
		LinkedList<ScenarioData> result = new LinkedList<ScenarioData>();
		result.add(data);
		return result;
	}
	
	@Override
	public LinkedList<EvaluationMetric> getMetrics(double elapsedTime) {
		LinkedList<EvaluationMetric> result = super.getMetrics(elapsedTime);

//		result.add(new AvgCommPathLength(counter));
//		result.add(new AvgNumMsgsPerCommPath(counter));

		result.add(new AverageMsgsReceivedPerNode());
		result.add(new AverageSentMsgsPerNode());
		result.add(new BFMessageCounter());
		result.add(new BFRunCounter());
		result.add(new ClusterCounter());
		result.add(new MessagesPerLink());
		result.add(new NodesUsedSolelyForForwarding());
		result.add(new NormalMessageCounter());
		result.add(new NotifyMessageCounter());
		
		return result;
	}

	@Override
	protected void runAlgorithm(NetworkStack stack, ScenarioData data) {
		IAlgorithm algo =
			new DistributedAlgorithm(stack);
		
		algo.performEvaluation();
	}

	public static void main(String[] args) throws IOException {
		new DistributedAlgTest(new TestConfiguration(), Class.class.getName()).executeTest();
	}

}
