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

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import vnreal.Scenario;
import vnreal.io.XMLExporter;
import vnreal.io.XMLImporter;

/**
 * @author Vlad Singeorzan
 */
public final class ScenarioImporterExporterTest {
	
	Scenario scenario;

	@Before
	public void setLocale() {
		Locale.setDefault(Locale.US);
	}

	@Test
	public void importExportImportAgainTest() {
		try {
			XMLImporter.importScenario("src/XML/exemplary-scenario.xml", scenario);

			XMLExporter.exportScenario("src/XML/exemplary-scenario-test.xml", scenario);

			XMLImporter.importScenario("src/XML/exemplary-scenario-test.xml", scenario);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void importExportImportAgainTestWithMappings() {
		try {
			XMLImporter.importScenario("src/XML/exemplary-scenario-mappings.xml", scenario);

			XMLExporter.exportScenario("src/XML/exemplary-scenario-mappings-test.xml", scenario);
			
			XMLImporter.importScenario("src/XML/exemplary-scenario.mappings-test.xml", scenario);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void importExportImportAgainTestWithMappingsAndHiddenHops() {
		try {
			
			XMLImporter.importScenario("src/XML/exemplary-scenario-mappings-and-hiddenhops.xml", scenario);

			XMLExporter.exportScenario("src/XML/exemplary-scenario-mappings-and-hiddenhops-test.xml", scenario);
			
			XMLImporter.importScenario("src/XML/exemplary-scenario.mappings-and-hiddenhops-test.xml", scenario);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
