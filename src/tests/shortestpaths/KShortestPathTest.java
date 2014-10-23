/* ***** BEGIN LICENSE BLOCK *****
 * Copyright (C) 2008-2011, The 100GET-E3-R3G Project Team.
 * 
 * This work has been funded by the Federal Ministry of Education
 * and Research of the Federal Republic of Germany
 * (BMBF Förderkennzeichen 01BP0775). It is part of the EUREKA project
 * "100 Gbit/s Carrier-Grade Ethernet Transport Technologies
 * (CELTIC CP4-001)". The authors alone are responsible for this work.
 *
 * See the file AUTHORS for details and contact information.
 * 
 * This file is part of MuLaViTo (Multi-Layer Visualization Tool).
 *
 * MuLaViTo is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License Version 3 or later
 * (the "GPL"), or the GNU Lesser General Public License Version 3 or later
 * (the "LGPL") as published by the Free Software Foundation.
 *
 * MuLaViTo is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * or the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and
 * GNU Lesser General Public License along with MuLaViTo; see the file
 * COPYING. If not, see <http://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ***** */
package tests.shortestpaths;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;
import mulavito.algorithms.shortestpath.ksp.Eppstein;
import mulavito.algorithms.shortestpath.ksp.KShortestPathAlgorithm;
import mulavito.algorithms.shortestpath.ksp.Yen;

import org.apache.commons.collections15.ListUtils;
import org.apache.commons.collections15.Transformer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import tests.shortestpaths.utils.KspTestScenario;
import tests.shortestpaths.utils.KspTestScenarios;
import tests.shortestpaths.utils.MyLink;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;

/**
 * Parameterized ksp test class. Use {@link KSPFactory} as parameter to create
 * your algorithm you want to test.
 * 
 * @author Thilo Müller
 * @author Michael Duelli
 * @author Julian Ott
 * @since 2010-11-26
 */
@RunWith(Parameterized.class)
public final class KShortestPathTest {
	public interface KSPFactory {
		KShortestPathAlgorithm<String, MyLink> create(
				Graph<String, MyLink> graph,
				Transformer<MyLink, Number> weightTrans);
	}

	Transformer<MyLink, Number> weightTrans = new Transformer<MyLink, Number>() {
		@Override
		public Number transform(MyLink link) {
			return link.getWeight();
		}
	};

	@Parameters
	public static Collection<KSPFactory[]> factories() {
		KSPFactory[][] data = new KSPFactory[][] {
		// Eppstein
				{ new KSPFactory() {
					@Override
					public KShortestPathAlgorithm<String, MyLink> create(
							Graph<String, MyLink> graph,
							Transformer<MyLink, Number> weightTrans) {
						return new Eppstein<String, MyLink>(graph, weightTrans);
					}
				} },
				// Yen
				{ new KSPFactory() {
					@Override
					public KShortestPathAlgorithm<String, MyLink> create(
							Graph<String, MyLink> graph,
							Transformer<MyLink, Number> weightTrans) {
						return new Yen<String, MyLink>(graph, weightTrans);
					}
				} },
		// ...
		};

		return Arrays.asList(data);
	}

	private KSPFactory factory;

	public KShortestPathTest(KSPFactory factory) {
		Assert.assertNotNull(factory);
		this.factory = factory;
	}

	@Test(expected = IllegalArgumentException.class)
	public void basics() {
		Graph<String, MyLink> graph = new DirectedOrderedSparseMultigraph<String, MyLink>();
		Transformer<MyLink, Number> weightTrans = null;

		factory.create(graph, weightTrans);
	}

	@Test
	public void eppsteinExample() {
		KspTestScenario<String, MyLink> scenario = KspTestScenarios
				.getEppsteinScenario();
		Graph<String, MyLink> graph = scenario.getGraph();
		List<List<MyLink>> solutions = scenario.getSolutions();
		String s = scenario.getSource();
		String t = scenario.getTarget();

		Eppstein<String, MyLink> kspAlgo = new Eppstein<String, MyLink>(graph,
				weightTrans);
		int k = solutions.size();
		List<List<MyLink>> result = kspAlgo.getShortestPaths(s, t, k);

		for (int i = 0; i < k; i++)
			assertTrue(comparePaths(solutions.get(i), result.get(i)));
	}

	@Test
	public void suurballeTarjanExample() {
		KspTestScenario<String, MyLink> scenario = KspTestScenarios
				.getSuurballeTarjanScenario();
		Graph<String, MyLink> graph = scenario.getGraph();
		List<List<MyLink>> solutions = scenario.getSolutions();
		String s = scenario.getSource();
		String t = scenario.getTarget();

		KShortestPathAlgorithm<String, MyLink> kspAlgo = factory.create(graph,
				weightTrans);
		int k = solutions.size();
		List<List<MyLink>> result = kspAlgo.getShortestPaths(s, t, k);

		for (int i = 0; i < k; i++)
			assertTrue(comparePaths(solutions.get(i), result.get(i)));
	}

	@Test
	public void decisionOnLastHop() {
		KspTestScenario<String, MyLink> scenario = KspTestScenarios
				.getLoopScenario();
		Graph<String, MyLink> graph = scenario.getGraph();
		List<List<MyLink>> solutions = scenario.getSolutions();
		String s = scenario.getSource();
		String t = scenario.getTarget();

		KShortestPathAlgorithm<String, MyLink> kspAlgo = factory.create(graph,
				weightTrans);
		int k = solutions.size();
		List<List<MyLink>> result = kspAlgo.getShortestPaths(s, t, k);

		for (int i = 0; i < k; i++)
			assertTrue(comparePaths(solutions.get(i), result.get(i)));
	}

	@Test
	public void loopPrevention() {
		KspTestScenario<String, MyLink> scenario = KspTestScenarios
				.getLoopScenario();
		Graph<String, MyLink> graph = scenario.getGraph();
		List<List<MyLink>> solutions = scenario.getSolutions();
		String s = scenario.getSource();
		String t = scenario.getTarget();

		KShortestPathAlgorithm<String, MyLink> kspAlgo = factory.create(graph,
				weightTrans);
		int k = solutions.size();
		List<List<MyLink>> result = kspAlgo.getShortestPaths(s, t, k);

		for (int i = 0; i < k; i++)
			assertTrue(comparePaths(solutions.get(i), result.get(i)));
	}

	@Test
	public void unreachability() {
		KspTestScenario<String, MyLink> scenario = KspTestScenarios
				.getUnreachabilitiyScenario();
		Graph<String, MyLink> graph = scenario.getGraph();
		List<List<MyLink>> solutions = scenario.getSolutions();
		String s = scenario.getSource();
		String t = scenario.getTarget();

		KShortestPathAlgorithm<String, MyLink> kspAlgo = factory.create(graph,
				weightTrans);
		int k = solutions.size();
		List<List<MyLink>> result = kspAlgo.getShortestPaths(s, t, k);

		for (int i = 1; i < k; i++)
			assertTrue(comparePaths(solutions.get(i), result.get(i)));
	}

	@Test
	public void selfLoopUnreachable() {
		KspTestScenario<String, MyLink> scenario = KspTestScenarios
				.getSelfLoopUnreachableScenario();
		Graph<String, MyLink> graph = scenario.getGraph();
		String s = scenario.getSource();
		String t = scenario.getTarget();

		KShortestPathAlgorithm<String, MyLink> kspAlgo = factory.create(graph,
				weightTrans);

		assertTrue(kspAlgo.getShortestPaths(s, t, 4).isEmpty());
	}

	@Test
	public void increasingHops() {
		KspTestScenario<String, MyLink> scenario = KspTestScenarios
				.getIncreasingHopScenario();
		Graph<String, MyLink> graph = scenario.getGraph();
		List<List<MyLink>> solutions = scenario.getSolutions();
		String s = scenario.getSource();
		String t = scenario.getTarget();

		KShortestPathAlgorithm<String, MyLink> kspAlgo = factory.create(graph,
				weightTrans);
		int k = solutions.size();
		List<List<MyLink>> result = kspAlgo.getShortestPaths(s, t, k);

		for (int i = 0; i < k; i++)
			assertTrue(comparePaths(solutions.get(i), result.get(i)));
	}

	@Test
	public void increasingHopLength() {
		KspTestScenario<String, MyLink> scenario = KspTestScenarios
				.getIncreasingHopLengthScenario();
		Graph<String, MyLink> graph = scenario.getGraph();
		List<List<MyLink>> solutions = scenario.getSolutions();
		String s = scenario.getSource();
		String t = scenario.getTarget();

		KShortestPathAlgorithm<String, MyLink> kspAlgo = factory.create(graph,
				weightTrans);
		int k = solutions.size();
		List<List<MyLink>> result = kspAlgo.getShortestPaths(s, t, k);

		for (int i = 0; i < k; i++)
			assertTrue(comparePaths(solutions.get(i), result.get(i)));
	}

	@Test
	public void zigZag() {
		KspTestScenario<String, MyLink> scenario = KspTestScenarios
				.getZigZagScenario();
		Graph<String, MyLink> graph = scenario.getGraph();
		List<List<MyLink>> solutions = scenario.getSolutions();
		String s = scenario.getSource();
		String t = scenario.getTarget();

		KShortestPathAlgorithm<String, MyLink> kspAlgo = factory.create(graph,
				weightTrans);

		int k = solutions.size();
		List<List<MyLink>> result = kspAlgo.getShortestPaths(s, t, k);

		for (int i = 0; i < k; i++)
			assertTrue(comparePaths(solutions.get(i), result.get(i)));
	}

	private boolean comparePaths(List<MyLink> expected, List<MyLink> actual) {
		double w1 = 0.0;
		for (MyLink e : expected)
			w1 += e.getWeight();

		double w2 = 0.0;
		for (MyLink e : actual)
			w2 += e.getWeight();

		if (ListUtils.isEqualList(expected, actual))
			return true;
		else if (w1 == w2 && expected.size() == actual.size()) {
			System.out.println("Identical weight (" + w1 + ") and hop count ("
					+ expected.size() + ") but different solutions: "
					+ expected + " vs. " + actual);
			return true;
		}
		return false;
	}
}
