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

import java.io.IOException;

import mulavito.algorithms.IAlgorithm;
import tests.io.PrintWriterDataReceiver;
import vnreal.algorithms.isomorphism.AdvancedSubgraphIsomorphismAlgorithm;
import vnreal.algorithms.isomorphism.SubgraphIsomorphismStackAlgorithm;
import vnreal.network.NetworkStack;

public final class AdvancedParallelSubgraphTest extends AbstractParallelScenarioTest {

	private PrintWriterDataReceiver writer;

	public AdvancedParallelSubgraphTest(TestConfiguration c, String name, int start, int end) throws IOException {
		super(c, name, false, start, end);
	}

	@Override
	public void run() {
		super.run();
		writer.finish();
	}

	@Override
	protected void runAlgorithm(NetworkStack stack, ScenarioData data) {
		IAlgorithm algo = new SubgraphIsomorphismStackAlgorithm(stack,
				new AdvancedSubgraphIsomorphismAlgorithm(true));

		algo.performEvaluation();
	}
}
