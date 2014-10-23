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

import java.io.IOException;
import java.util.LinkedList;

import mulavito.algorithms.IAlgorithm;
import tests.scenarios.AbstractScenarioTest.ScenarioData;
import tests.scenarios.AbstractScenarioTest.TestConfiguration;
import vnreal.algorithms.AvailableResources;
import vnreal.algorithms.GenericMappingAlgorithm;
import vnreal.hiddenhopmapping.BandwidthCpuHiddenHopMapping;
import vnreal.hiddenhopmapping.IHiddenHopMapping;
import vnreal.network.NetworkStack;

public final class GARSPTest extends AbstractScenarioTest<ScenarioData, TestConfiguration> {

	private static final int k = 50, dist = 35;

	public GARSPTest(TestConfiguration c, String name) {
		super(c, name, false);
	}
	
	@Override
	protected void runAlgorithm(NetworkStack stack, ScenarioData data) {
		LinkedList<IHiddenHopMapping> hhMappings = new LinkedList<IHiddenHopMapping>();
		double hiddenHopsFactor = 0;
		hhMappings.add(new BandwidthCpuHiddenHopMapping(hiddenHopsFactor));

		IAlgorithm algo = new AvailableResources(stack, k, dist, false, false);
		if (algo instanceof GenericMappingAlgorithm)
			((GenericMappingAlgorithm) algo).setHhMappings(hhMappings);
		
		algo.performEvaluation();
	}

	@Override
	public LinkedList<ScenarioData> getParams(ScenarioData data) {
		LinkedList<ScenarioData> result = new LinkedList<ScenarioData>();
		result.add(data);
		return result;
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println("If you get any error, set -Djava.library.path=/usr/lib/x86_64-linux-gnu/jni");
		new GARSPTest(new TestConfiguration(), "AvailResourceKSP").executeTest();
	}
}
