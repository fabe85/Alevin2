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
package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import vnreal.demands.AbstractDemand;
import vnreal.demands.BandwidthDemand;
import vnreal.demands.CpuDemand;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;
import vnreal.resources.BandwidthResource;
import vnreal.resources.CpuResource;

/**
 * Test the interplay of {@link AbstractResource} and {@link AbstractDemand}
 * using accepts, fulfills, occupy, and free methods.
 * 
 * @author Michael Duelli
 * @author Vlad Singeorzan
 * @since 2010-08-19
 */
public final class BasicInterplayTest {
	private List<AbstractResource> resources;
	private List<AbstractDemand> demands;

	@Before
	public void create() {

		resources = new LinkedList<AbstractResource>();
		BandwidthResource bwRes = new BandwidthResource(new SubstrateLink());
		bwRes.setBandwidth(4.2);
		resources.add(bwRes);
		CpuResource cpuRes = new CpuResource(new SubstrateNode());
		cpuRes.setCycles(3.0);
		resources.add(cpuRes);

		demands = new LinkedList<AbstractDemand>();
		BandwidthDemand bwDem = new BandwidthDemand(new VirtualLink(1));
		bwDem.setDemandedBandwidth(3.1);
		demands.add(bwDem);
		CpuDemand cpuDem = new CpuDemand(new VirtualNode(1));
		cpuDem.setDemandedCycles(2.0);
		demands.add(cpuDem);
	}

	@Test
	public void accepts() {
		AbstractResource res;

		res = resources.get(0); // link resource
		assertTrue(res.accepts(demands.get(0)));
		assertFalse(res.accepts(demands.get(1)));

		res = resources.get(1); // node resource
		assertFalse(res.accepts(demands.get(0)));
		assertTrue(res.accepts(demands.get(1)));
	}

	@Test
	public void fulfills() {
		AbstractResource res;

		res = resources.get(0); // link resource
		assertTrue(res.fulfills(demands.get(0)));
		assertFalse(res.fulfills(demands.get(1)));

		res = resources.get(1); // node resource
		assertFalse(res.fulfills(demands.get(0)));
		assertTrue(res.fulfills(demands.get(1)));
	}

	@Test
	public void occupyAndFree() {
		AbstractResource res;

		res = resources.get(0); // link resource
		assertTrue(demands.get(0).occupy(res));
		assertFalse(demands.get(1).occupy(res));

		assertEquals(1, res.getMappings().size());
		assertEquals(1, demands.get(0).getMappings().size());
		assertEquals(0, demands.get(1).getMappings().size());

		assertSame(res.getMappings().get(0), demands.get(0).getMappings()
				.get(0));

		assertTrue(demands.get(0).free(res));
		assertEquals(0, res.getMappings().size());
		assertEquals(0, demands.get(0).getMappings().size());

		assertFalse(demands.get(0).free(res));
		assertFalse(demands.get(1).free(res));

		res = resources.get(1);
		assertFalse(demands.get(0).occupy(res));
		assertTrue(demands.get(1).occupy(res));

		assertEquals(res.getMappings().size(), 1);
		assertEquals(0, demands.get(0).getMappings().size());
		assertEquals(1, demands.get(1).getMappings().size());

		assertSame(res.getMappings().get(0), demands.get(1).getMappings()
				.get(0));

		assertFalse(demands.get(0).free(res));
		assertTrue(demands.get(1).free(res));

		assertEquals(0, res.getMappings().size());
		assertEquals(0, demands.get(1).getMappings().size());

		assertFalse(demands.get(1).free(res));
	}
}
