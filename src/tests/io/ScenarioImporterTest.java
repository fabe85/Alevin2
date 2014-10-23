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
package tests.io;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import vnreal.Scenario;
import vnreal.io.XMLImporter;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.virtual.VirtualNetwork;

/**
 * Test the {@link ScenarioImporter} with Daniel's exemplary scenario.
 * 
 * @author Vlad Singeorzan
 * @since 2010-10-01
 */
public final class ScenarioImporterTest {
	private static final String fileName = "src/XML/exemplary-scenario.xml";
	Scenario scenario = new Scenario();

	@Before
	public void importScenario() {
		try {
			XMLImporter.importScenario(fileName, scenario);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSubstrateNetworkImport() {
		SubstrateNetwork substrate = scenario.getNetworkStack().getSubstrate();

		assertEquals(5, substrate.getVertexCount());
		assertEquals(6, substrate.getEdgeCount());
	}

	@Test
	public void testVirtualNetworksImport() {
		VirtualNetwork layer0 = (VirtualNetwork) scenario.getNetworkStack().getLayer(1);
		assertEquals(2, layer0.getVertexCount());
		assertEquals(1, layer0.getEdgeCount());

		VirtualNetwork layer1 = (VirtualNetwork) scenario
				.getNetworkStack().getLayer(2);
		assertEquals(2, layer1.getVertexCount());
		assertEquals(1, layer1.getEdgeCount());
	}
}
