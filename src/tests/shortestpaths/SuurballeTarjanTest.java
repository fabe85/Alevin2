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

import static org.junit.Assert.assertEquals;
import mulavito.algorithms.shortestpath.disjoint.SuurballeTarjan;

import org.apache.commons.collections15.Transformer;
import org.junit.Test;

import tests.shortestpaths.utils.MyLink;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * @author Thilo Müller
 * @author Michael Duelli
 * @since 2010-09-15
 */
public class SuurballeTarjanTest {
	@Test(expected = IllegalArgumentException.class)
	public void constructorTest1() {
		Graph<String, MyLink> g = null;
		Transformer<MyLink, Number> weightTrans = new Transformer<MyLink, Number>() {
			@Override
			public Number transform(MyLink link) {
				return link.getWeight();
			}
		};
		new SuurballeTarjan<String, MyLink>(g, weightTrans);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructorTest2() {
		Graph<String, MyLink> g = new DirectedOrderedSparseMultigraph<String, MyLink>();
		Transformer<MyLink, Number> weightTrans = null;

		new SuurballeTarjan<String, MyLink>(g, weightTrans);
	}

	@Test
	public void testUnreachable() {
		Graph<String, MyLink> g = new DirectedOrderedSparseMultigraph<String, MyLink>();

		String n1 = new String("S"); // S
		g.addVertex(n1);
		String n2 = new String("A"); // A
		g.addVertex(n2);
		String n3 = new String("B"); // B -- not connected
		g.addVertex(n3);

		g.addEdge(new MyLink(1, "S-A"), n1, n2, EdgeType.DIRECTED); // S - A

		Transformer<MyLink, Number> weightTrans = new Transformer<MyLink, Number>() {
			@Override
			public Number transform(MyLink link) {
				return link.getWeight();
			}
		};
		SuurballeTarjan<String, MyLink> testMain = new SuurballeTarjan<String, MyLink>(
				g, weightTrans);
		assertEquals(null, testMain.getDisjointPaths(n1, n3));
	}

	@Test
	public void testSuurballe1() {
		Graph<String, MyLink> g = new DirectedOrderedSparseMultigraph<String, MyLink>();

		String n1 = new String("S"); // S
		g.addVertex(n1);
		String n2 = new String("A"); // A
		g.addVertex(n2);
		String n3 = new String("D"); // D
		g.addVertex(n3);
		String n4 = new String("C"); // C
		g.addVertex(n4);
		String n5 = new String("F"); // F
		g.addVertex(n5);
		String n6 = new String("B"); // B
		g.addVertex(n6);
		String n7 = new String("G"); // G
		g.addVertex(n7);
		String n8 = new String("E"); // E
		g.addVertex(n8);

		g.addEdge(new MyLink(3, "S-A"), n1, n2, EdgeType.DIRECTED); // S - A
		g.addEdge(new MyLink(2, "S-B"), n1, n6, EdgeType.DIRECTED); // S - B
		g.addEdge(new MyLink(8, "S-D"), n1, n3, EdgeType.DIRECTED); // S - D
		g.addEdge(new MyLink(1, "A-C"), n2, n4, EdgeType.DIRECTED); // A - C
		g.addEdge(new MyLink(4, "A-D"), n2, n3, EdgeType.DIRECTED); // A - D
		g.addEdge(new MyLink(1, "D-F"), n3, n5, EdgeType.DIRECTED); // D - F
		g.addEdge(new MyLink(5, "C-F"), n4, n5, EdgeType.DIRECTED); // C - F
		g.addEdge(new MyLink(5, "B-E"), n6, n8, EdgeType.DIRECTED); // B - E
		g.addEdge(new MyLink(2, "E-G"), n8, n7, EdgeType.DIRECTED); // E - G
		g.addEdge(new MyLink(7, "G-F"), n7, n5, EdgeType.DIRECTED); // G - F

		Transformer<MyLink, Number> weightTrans = new Transformer<MyLink, Number>() {
			@Override
			public Number transform(MyLink link) {
				return link.getWeight();
			}
		};
		SuurballeTarjan<String, MyLink> testMain = new SuurballeTarjan<String, MyLink>(
				g, weightTrans);
		assertEquals("[[S-A, A-C, C-F], [S-D, D-F]]", testMain
				.getDisjointPaths(n1, n5).toString());
	}

	@Test
	public void testTargetRemoval() {
		Graph<String, MyLink> g = new DirectedOrderedSparseMultigraph<String, MyLink>();

		String n1 = new String("S"); // S
		g.addVertex(n1);
		String n2 = new String("A"); // A
		g.addVertex(n2);
		String n3 = new String("B"); // B
		g.addVertex(n3);
		String n4 = new String("C"); // C
		g.addVertex(n4);
		String n5 = new String("D"); // D
		g.addVertex(n5);

		g.addEdge(new MyLink(3, "S-A"), n1, n2, EdgeType.DIRECTED); // S - A
		g.addEdge(new MyLink(1, "A-C"), n2, n4, EdgeType.DIRECTED); // A - C
		g.addEdge(new MyLink(3, "S-B"), n1, n3, EdgeType.DIRECTED); // S - B
		g.addEdge(new MyLink(3, "B-C"), n3, n4, EdgeType.DIRECTED); // B - C
		g.addEdge(new MyLink(3, "C-D"), n4, n5, EdgeType.DIRECTED); // C - D

		Transformer<MyLink, Number> weightTrans = new Transformer<MyLink, Number>() {
			@Override
			public Number transform(MyLink link) {
				return link.getWeight();
			}
		};
		SuurballeTarjan<String, MyLink> testMain = new SuurballeTarjan<String, MyLink>(
				g, weightTrans);
		assertEquals("[[S-A, A-C, C-D]]", testMain.getDisjointPaths(n1, n5)
				.toString());
	}

	@Test
	public void testTrapTopology() {
		Graph<String, MyLink> g = new DirectedOrderedSparseMultigraph<String, MyLink>();

		String nS = new String("S");
		g.addVertex(nS);
		String nA = new String("A");
		g.addVertex(nA);
		String nB = new String("B");
		g.addVertex(nB);
		String nD = new String("D");
		g.addVertex(nD);

		String nE = new String("E");
		g.addVertex(nE);
		String nF = new String("F");
		g.addVertex(nF);

		String nG = new String("G");
		g.addVertex(nG);
		String nH = new String("H");
		g.addVertex(nH);

		g.addEdge(new MyLink(1, "S-A"), nS, nA, EdgeType.DIRECTED);
		g.addEdge(new MyLink(1, "A-B"), nA, nB, EdgeType.DIRECTED);
		g.addEdge(new MyLink(1, "B-D"), nB, nD, EdgeType.DIRECTED);

		g.addEdge(new MyLink(2, "S-E"), nS, nE, EdgeType.DIRECTED);
		g.addEdge(new MyLink(2, "E-F"), nE, nF, EdgeType.DIRECTED);
		g.addEdge(new MyLink(2, "F-B"), nF, nB, EdgeType.DIRECTED);

		g.addEdge(new MyLink(2, "A-G"), nA, nG, EdgeType.DIRECTED);
		g.addEdge(new MyLink(2, "G-H"), nG, nH, EdgeType.DIRECTED);
		g.addEdge(new MyLink(2, "H-D"), nH, nD, EdgeType.DIRECTED);

		Transformer<MyLink, Number> weightTrans = new Transformer<MyLink, Number>() {
			@Override
			public Number transform(MyLink link) {
				return link.getWeight();
			}
		};
		SuurballeTarjan<String, MyLink> testMain = new SuurballeTarjan<String, MyLink>(
				g, weightTrans);
		assertEquals("[[S-A, A-G, G-H, H-D], [S-E, E-F, F-B, B-D]]", testMain
				.getDisjointPaths(nS, nD).toString());
	}

	@Test
	public void testSuurballe2() {
		Graph<String, MyLink> g = new DirectedOrderedSparseMultigraph<String, MyLink>();

		String n1 = new String("A"); // A
		g.addVertex(n1);
		String n2 = new String("D"); // D
		g.addVertex(n2);
		String n3 = new String("C"); // C
		g.addVertex(n3);
		String n4 = new String("F"); // F
		g.addVertex(n4);
		String n5 = new String("B"); // B
		g.addVertex(n5);
		String n6 = new String("G"); // G
		g.addVertex(n6);
		String n7 = new String("E"); // E
		g.addVertex(n7);

		g.addEdge(new MyLink(2, "A-D"), n1, n2, EdgeType.DIRECTED);
		g.addEdge(new MyLink(3, "A-C"), n1, n3, EdgeType.DIRECTED);
		g.addEdge(new MyLink(3, "A-F"), n1, n4, EdgeType.DIRECTED);
		g.addEdge(new MyLink(1, "D-E"), n2, n7, EdgeType.DIRECTED); // A - C
		g.addEdge(new MyLink(3, "D-C"), n2, n3, EdgeType.DIRECTED); // A - D
		g.addEdge(new MyLink(1, "C-E"), n3, n7, EdgeType.DIRECTED); // D - F
		g.addEdge(new MyLink(2, "C-B"), n3, n5, EdgeType.DIRECTED); // C - F
		g.addEdge(new MyLink(2, "C-G"), n3, n6, EdgeType.DIRECTED); // B - E
		g.addEdge(new MyLink(2, "F-G"), n4, n6, EdgeType.DIRECTED); // E - G
		g.addEdge(new MyLink(1, "F-C"), n4, n3, EdgeType.DIRECTED); // G - F
		g.addEdge(new MyLink(2, "E-B"), n7, n5, EdgeType.DIRECTED); // G - F
		g.addEdge(new MyLink(1, "G-B"), n6, n5, EdgeType.DIRECTED); // G - F

		Transformer<MyLink, Number> weightTrans = new Transformer<MyLink, Number>() {
			@Override
			public Number transform(MyLink link) {
				return link.getWeight();
			}
		};
		SuurballeTarjan<String, MyLink> testMain = new SuurballeTarjan<String, MyLink>(
				g, weightTrans);
		assertEquals("[[A-D, D-E, E-B], [A-C, C-B]]", testMain
				.getDisjointPaths(n1, n5).toString());
	}

	@Test
	public void testSuurballe3() {
		Graph<String, MyLink> g = new DirectedOrderedSparseMultigraph<String, MyLink>();

		String n1 = new String("A"); // A
		g.addVertex(n1);
		String n2 = new String("D"); // D
		g.addVertex(n2);
		String n3 = new String("C"); // C
		g.addVertex(n3);
		String n4 = new String("F"); // F
		g.addVertex(n4);
		String n5 = new String("B"); // B
		g.addVertex(n5);
		String n6 = new String("G"); // G
		g.addVertex(n6);
		String n7 = new String("E"); // E
		g.addVertex(n7);

		g.addEdge(new MyLink(2, "A-D"), n1, n2, EdgeType.DIRECTED);
		g.addEdge(new MyLink(3, "A-C"), n1, n3, EdgeType.DIRECTED);
		g.addEdge(new MyLink(3, "A-F"), n1, n4, EdgeType.DIRECTED);
		g.addEdge(new MyLink(3, "D-E"), n2, n7, EdgeType.DIRECTED); // A - C
		g.addEdge(new MyLink(0, "D-C"), n2, n3, EdgeType.DIRECTED); // A - D
		g.addEdge(new MyLink(1, "C-E"), n3, n7, EdgeType.DIRECTED); // D - F
		g.addEdge(new MyLink(2, "C-B"), n3, n5, EdgeType.DIRECTED); // C - F
		g.addEdge(new MyLink(2, "C-G"), n3, n6, EdgeType.DIRECTED); // B - E
		g.addEdge(new MyLink(2, "F-G"), n4, n6, EdgeType.DIRECTED); // E - G
		g.addEdge(new MyLink(1, "F-C"), n4, n3, EdgeType.DIRECTED); // G - F
		g.addEdge(new MyLink(2, "E-B"), n7, n5, EdgeType.DIRECTED); // G - F
		g.addEdge(new MyLink(1, "G-B"), n6, n5, EdgeType.DIRECTED); // G - F

		Transformer<MyLink, Number> weightTrans = new Transformer<MyLink, Number>() {
			@Override
			public Number transform(MyLink link) {
				return link.getWeight();
			}
		};
		SuurballeTarjan<String, MyLink> testMain = new SuurballeTarjan<String, MyLink>(
				g, weightTrans);
		assertEquals("[[A-D, D-C, C-B], [A-F, F-G, G-B]]", testMain
				.getDisjointPaths(n1, n5).toString());
	}

	@Test
	public void testNoDisjointSolution() {
		Graph<String, MyLink> g = new DirectedOrderedSparseMultigraph<String, MyLink>();

		String n1 = new String("A"); // A
		g.addVertex(n1);
		String n2 = new String("B"); // B
		g.addVertex(n2);

		g.addEdge(new MyLink(2, "A-B"), n1, n2, EdgeType.DIRECTED);

		Transformer<MyLink, Number> weightTrans = new Transformer<MyLink, Number>() {
			@Override
			public Number transform(MyLink link) {
				return link.getWeight();
			}
		};
		SuurballeTarjan<String, MyLink> testMain = new SuurballeTarjan<String, MyLink>(
				g, weightTrans);
		assertEquals("[[A-B]]", testMain.getDisjointPaths(n1, n2).toString());
	}
}
