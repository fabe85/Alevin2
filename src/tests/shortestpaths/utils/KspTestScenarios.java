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
package tests.shortestpaths.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * @author Michael Duelli
 * @author Thilo Müller
 * @since 2010-11-29
 */
public final class KspTestScenarios {
	/**
	 * @return The example from the Eppstein paper.
	 */
	public static KspTestScenario<String, MyLink> getEppsteinScenario() {
		Graph<String, MyLink> graph = new DirectedOrderedSparseMultigraph<String, MyLink>();

		String s = new String("S");
		graph.addVertex(s);
		String b = new String("B");
		graph.addVertex(b);
		String c = new String("C");
		graph.addVertex(c);
		String d = new String("D");
		graph.addVertex(d);
		String e = new String("E");
		graph.addVertex(e);
		String f = new String("F");
		graph.addVertex(f);
		String g = new String("G");
		graph.addVertex(g);
		String h = new String("H");
		graph.addVertex(h);
		String i = new String("I");
		graph.addVertex(i);
		String j = new String("J");
		graph.addVertex(j);
		String k = new String("K");
		graph.addVertex(k);
		String t = new String("T");
		graph.addVertex(t);

		graph.addEdge(new MyLink(2, "S-B"), s, b, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(20, "B-C"), b, c, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(14, "C-D"), c, d, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(9, "E-F"), e, f, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(10, "F-G"), f, g, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(25, "G-H"), g, h, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(18, "I-J"), i, j, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(8, "J-K"), j, k, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(11, "K-T"), k, t, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(13, "S-E"), s, e, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(15, "E-I"), e, i, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(27, "B-F"), b, f, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(20, "F-J"), f, j, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(14, "C-G"), c, g, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(12, "G-K"), g, k, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(15, "D-H"), d, h, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(7, "H-T"), h, t, EdgeType.DIRECTED);

		List<MyLink> path = new ArrayList<MyLink>();
		path.add(graph.findEdge(s, e));
		path.add(graph.findEdge(e, f));
		path.add(graph.findEdge(f, g));
		path.add(graph.findEdge(g, k));
		path.add(graph.findEdge(k, t));

		List<MyLink> path2 = new ArrayList<MyLink>();
		path2.add(graph.findEdge(s, b));
		path2.add(graph.findEdge(b, c));
		path2.add(graph.findEdge(c, d));
		path2.add(graph.findEdge(d, h));
		path2.add(graph.findEdge(h, t));

		List<MyLink> path3 = new ArrayList<MyLink>();
		path3.add(graph.findEdge(s, b));
		path3.add(graph.findEdge(b, c));
		path3.add(graph.findEdge(c, g));
		path3.add(graph.findEdge(g, k));
		path3.add(graph.findEdge(k, t));

		List<MyLink> path4 = new ArrayList<MyLink>();
		path4.add(graph.findEdge(s, e));
		path4.add(graph.findEdge(e, f));
		path4.add(graph.findEdge(f, j));
		path4.add(graph.findEdge(j, k));
		path4.add(graph.findEdge(k, t));

		List<List<MyLink>> temp = new LinkedList<List<MyLink>>();
		temp.add(path);
		temp.add(path2);
		temp.add(path3);
		temp.add(path4);

		return new KspTestScenario<String, MyLink>(graph, temp, s, t);
	}

	/**
	 * @return The example from the Suurballe-Tarjan paper.
	 */
	public static KspTestScenario<String, MyLink> getSuurballeTarjanScenario() {
		Graph<String, MyLink> graph = new DirectedOrderedSparseMultigraph<String, MyLink>();

		String s = new String("S"); // S
		graph.addVertex(s);
		String a = new String("A"); // A
		graph.addVertex(a);
		String d = new String("D"); // D
		graph.addVertex(d);
		String c = new String("C"); // C
		graph.addVertex(c);
		String f = new String("F"); // F
		graph.addVertex(f);
		String b = new String("B"); // B
		graph.addVertex(b);
		String g = new String("G"); // G
		graph.addVertex(g);
		String e = new String("E"); // E
		graph.addVertex(e);

		graph.addEdge(new MyLink(3, "S-A"), s, a, EdgeType.DIRECTED); // S - A
		graph.addEdge(new MyLink(2, "S-B"), s, b, EdgeType.DIRECTED); // S - B
		graph.addEdge(new MyLink(8, "S-D"), s, d, EdgeType.DIRECTED); // S - D
		graph.addEdge(new MyLink(1, "A-C"), a, c, EdgeType.DIRECTED); // A - C
		graph.addEdge(new MyLink(4, "A-D"), a, d, EdgeType.DIRECTED); // A - D
		graph.addEdge(new MyLink(1, "D-F"), d, f, EdgeType.DIRECTED); // D - F
		graph.addEdge(new MyLink(5, "C-F"), c, f, EdgeType.DIRECTED); // C - F
		graph.addEdge(new MyLink(5, "B-E"), b, e, EdgeType.DIRECTED); // B - E
		graph.addEdge(new MyLink(2, "E-G"), e, g, EdgeType.DIRECTED); // E - G
		graph.addEdge(new MyLink(7, "G-F"), g, f, EdgeType.DIRECTED); // G - F

		List<MyLink> path = new ArrayList<MyLink>();
		path.add(graph.findEdge(s, a));
		path.add(graph.findEdge(a, d));
		path.add(graph.findEdge(d, f));

		List<MyLink> path2 = new ArrayList<MyLink>();
		path2.add(graph.findEdge(s, d));
		path2.add(graph.findEdge(d, f));

		List<MyLink> path3 = new ArrayList<MyLink>();
		path3.add(graph.findEdge(s, a));
		path3.add(graph.findEdge(a, c));
		path3.add(graph.findEdge(c, f));

		List<MyLink> path4 = new ArrayList<MyLink>();
		path4.add(graph.findEdge(s, b));
		path4.add(graph.findEdge(b, e));
		path4.add(graph.findEdge(e, g));
		path4.add(graph.findEdge(g, f));

		List<List<MyLink>> temp = new LinkedList<List<MyLink>>();
		temp.add(path);
		temp.add(path2);
		temp.add(path3);
		temp.add(path4);

		return new KspTestScenario<String, MyLink>(graph, temp, s, f);
	}

	/**
	 * @return An example in which all candidate paths have equal length in the
	 *         first step.
	 */
	public static KspTestScenario<String, MyLink> getDecisionOnLastHopScenario() {
		Graph<String, MyLink> graph = new DirectedOrderedSparseMultigraph<String, MyLink>();

		String s = new String("S"); // the source
		graph.addVertex(s);
		String a = new String("A");
		graph.addVertex(a);
		String b = new String("B");
		graph.addVertex(b);
		String c = new String("C");
		graph.addVertex(c);
		String d = new String("D");
		graph.addVertex(d);
		String t = new String("T"); // the target
		graph.addVertex(t);

		graph.addEdge(new MyLink(1, "S-B"), s, b, EdgeType.DIRECTED); // S - B
		graph.addEdge(new MyLink(1, "S-C"), s, c, EdgeType.DIRECTED); // S - C
		graph.addEdge(new MyLink(1, "S-D"), s, d, EdgeType.DIRECTED); // S - D
		graph.addEdge(new MyLink(1, "S-A"), s, a, EdgeType.DIRECTED); // S - A

		graph.addEdge(new MyLink(2, "B-T"), b, t, EdgeType.DIRECTED); // B - T
		graph.addEdge(new MyLink(3, "C-T"), c, t, EdgeType.DIRECTED); // C - T
		graph.addEdge(new MyLink(1, "A-T"), a, t, EdgeType.DIRECTED); // A - T
		graph.addEdge(new MyLink(4, "D-T"), d, t, EdgeType.DIRECTED); // D - T

		List<MyLink> path = new ArrayList<MyLink>();
		path.add(graph.findEdge(s, a));
		path.add(graph.findEdge(a, t));

		List<MyLink> path2 = new ArrayList<MyLink>();
		path2.add(graph.findEdge(s, b));
		path2.add(graph.findEdge(b, t));

		List<MyLink> path3 = new ArrayList<MyLink>();
		path3.add(graph.findEdge(s, c));
		path3.add(graph.findEdge(c, t));

		List<MyLink> path4 = new ArrayList<MyLink>();
		path4.add(graph.findEdge(s, d));
		path4.add(graph.findEdge(d, t));

		List<List<MyLink>> temp = new LinkedList<List<MyLink>>();
		temp.add(path);
		temp.add(path2);
		temp.add(path3);
		temp.add(path4);

		return new KspTestScenario<String, MyLink>(graph, temp, s, t);
	}

	/**
	 * @return There is only one shortest path! We have to prevent endless
	 *         looping!
	 */
	public static KspTestScenario<String, MyLink> getUnreachabilitiyScenario() {
		Graph<String, MyLink> graph = new DirectedOrderedSparseMultigraph<String, MyLink>();

		String s = new String("S"); // the source
		graph.addVertex(s);
		String v = new String("V"); // the unreachable vertex!
		graph.addVertex(v);
		String w = new String("W"); // the intermediate vertex
		graph.addVertex(w);
		String t = new String("T"); // the target
		graph.addVertex(t);

		graph.addEdge(new MyLink(1, "S-V"), s, v, EdgeType.DIRECTED); // dead-end
		graph.addEdge(new MyLink(1, "S-W"), s, w, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(1, "W-T"), w, t, EdgeType.DIRECTED);

		List<MyLink> path = new ArrayList<MyLink>();
		path.add(graph.findEdge(s, w));
		path.add(graph.findEdge(w, t));

		List<List<MyLink>> temp = new LinkedList<List<MyLink>>();
		temp.add(path);

		return new KspTestScenario<String, MyLink>(graph, temp, s, t);
	}

	/**
	 * @return There is only one shortest path! We have to prevent endless
	 *         looping!
	 */
	public static KspTestScenario<String, MyLink> getLoopScenario() {
		Graph<String, MyLink> graph = new DirectedOrderedSparseMultigraph<String, MyLink>();

		String s = new String("S"); // the source
		graph.addVertex(s);
		String a = new String("A"); // the intermediate vertex 1
		graph.addVertex(a);
		String b = new String("B"); // the intermediate vertex 2
		graph.addVertex(b);
		String t = new String("T"); // the target
		graph.addVertex(t);

		graph.addEdge(new MyLink(1, "S-A"), s, a, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(1, "A-B"), a, b, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(1, "B-A"), b, a, EdgeType.DIRECTED); // loop!
		graph.addEdge(new MyLink(1, "B-T"), b, t, EdgeType.DIRECTED);

		List<MyLink> path = new ArrayList<MyLink>();
		path.add(graph.findEdge(s, a));
		path.add(graph.findEdge(a, b));
		path.add(graph.findEdge(b, t));

		List<List<MyLink>> temp = new LinkedList<List<MyLink>>();
		temp.add(path);

		return new KspTestScenario<String, MyLink>(graph, temp, s, t);
	}

	/**
	 * @return Self-loop at start and unreachable target.
	 */
	public static KspTestScenario<String, MyLink> getSelfLoopUnreachableScenario() {
		Graph<String, MyLink> graph = new DirectedOrderedSparseMultigraph<String, MyLink>();

		String s = new String("S"); // the source
		graph.addVertex(s);
		String t = new String("T");
		graph.addVertex(t);

		graph.addEdge(new MyLink(20, "S-S"), s, s, EdgeType.DIRECTED); // loop

		List<List<MyLink>> temp = new LinkedList<List<MyLink>>();

		return new KspTestScenario<String, MyLink>(graph, temp, s, t);
	}

	/**
	 * @return Solutions have increasing hop count, but longest hop path is the
	 *         shortest. Other solutions have equal length of 10 and are sorted
	 *         by increasing hops.
	 */
	public static KspTestScenario<String, MyLink> getIncreasingHopScenario() {
		Graph<String, MyLink> graph = new DirectedOrderedSparseMultigraph<String, MyLink>();

		String s = new String("S"); // the source
		graph.addVertex(s);
		String t = new String("T");
		graph.addVertex(t);
		String a = new String("A");
		graph.addVertex(a);
		String b = new String("B");
		graph.addVertex(b);
		String c = new String("C");
		graph.addVertex(c);
		String d = new String("D");
		graph.addVertex(d);
		String e = new String("E");
		graph.addVertex(e);
		String f = new String("F");
		graph.addVertex(f);

		graph.addEdge(new MyLink(10, "S-T"), s, t, EdgeType.DIRECTED); // 1 hop

		graph.addEdge(new MyLink(5, "S-A"), s, a, EdgeType.DIRECTED); // 2 hops
		graph.addEdge(new MyLink(5, "A-T"), a, t, EdgeType.DIRECTED); // l=10

		graph.addEdge(new MyLink(3, "S-B"), s, b, EdgeType.DIRECTED); // 3 hops
		graph.addEdge(new MyLink(3, "B-C"), b, c, EdgeType.DIRECTED); // l=10
		graph.addEdge(new MyLink(4, "C-T"), c, t, EdgeType.DIRECTED);

		graph.addEdge(new MyLink(2, "S-D"), s, d, EdgeType.DIRECTED); // 4 hops
		graph.addEdge(new MyLink(2, "D-E"), d, e, EdgeType.DIRECTED); // l=8
		graph.addEdge(new MyLink(2, "E-F"), e, f, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(2, "F-T"), f, t, EdgeType.DIRECTED);

		List<MyLink> path = new ArrayList<MyLink>();
		path.add(graph.findEdge(s, t));

		List<MyLink> path2 = new ArrayList<MyLink>();
		path2.add(graph.findEdge(s, a));
		path2.add(graph.findEdge(a, t));

		List<MyLink> path3 = new ArrayList<MyLink>();
		path3.add(graph.findEdge(s, b));
		path3.add(graph.findEdge(b, c));
		path3.add(graph.findEdge(c, t));

		List<MyLink> path4 = new ArrayList<MyLink>();
		path4.add(graph.findEdge(s, d));
		path4.add(graph.findEdge(d, e));
		path4.add(graph.findEdge(e, f));
		path4.add(graph.findEdge(f, t));

		List<List<MyLink>> temp = new LinkedList<List<MyLink>>();
		temp.add(path4);
		temp.add(path);
		temp.add(path2);
		temp.add(path3);

		return new KspTestScenario<String, MyLink>(graph, temp, s, t);
	}

	/**
	 * @return Solutions have increasing hop count and increasing length.
	 */
	public static KspTestScenario<String, MyLink> getIncreasingHopLengthScenario() {
		Graph<String, MyLink> graph = new DirectedOrderedSparseMultigraph<String, MyLink>();

		String s = new String("S"); // the source
		graph.addVertex(s);
		String a = new String("A");
		graph.addVertex(a);
		String b = new String("B");
		graph.addVertex(b);
		String c = new String("C");
		graph.addVertex(c);
		String d = new String("D");
		graph.addVertex(d);
		String e = new String("E");
		graph.addVertex(e);
		String t = new String("T");
		graph.addVertex(t);

		graph.addEdge(new MyLink(20, "S-T"), s, t, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(1, "S-A"), s, a, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(1, "S-B"), s, b, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(6, "A-D"), a, d, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(3, "A-C"), a, c, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(2, "B-C"), b, c, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(5, "B-E"), b, e, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(2, "C-D"), c, d, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(4, "C-T"), c, t, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(2, "C-E"), c, e, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(3, "D-T"), d, t, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(2, "E-T"), e, t, EdgeType.DIRECTED);

		List<MyLink> path1 = new ArrayList<MyLink>(); // length 7, 3 hops
		path1.add(graph.findEdge(s, b));
		path1.add(graph.findEdge(b, c));
		path1.add(graph.findEdge(c, t));

		List<MyLink> path2 = new ArrayList<MyLink>(); // length 7, 4 hops
		path2.add(graph.findEdge(s, b));
		path2.add(graph.findEdge(b, c));
		path2.add(graph.findEdge(c, e));
		path2.add(graph.findEdge(e, t));

		List<MyLink> path3 = new ArrayList<MyLink>(); // length 8, 3 hops
		path3.add(graph.findEdge(s, b));
		path3.add(graph.findEdge(b, e));
		path3.add(graph.findEdge(e, t));

		List<MyLink> path4 = new ArrayList<MyLink>(); // length 8, 3 hops
		path4.add(graph.findEdge(s, a));
		path4.add(graph.findEdge(a, c));
		path4.add(graph.findEdge(c, t));

		List<MyLink> path5 = new ArrayList<MyLink>(); // length 8, 4 hops
		path5.add(graph.findEdge(s, b));
		path5.add(graph.findEdge(b, c));
		path5.add(graph.findEdge(c, d));
		path5.add(graph.findEdge(d, t));

		List<MyLink> path6 = new ArrayList<MyLink>(); // length 8, 4 hops
		path6.add(graph.findEdge(s, a));
		path6.add(graph.findEdge(a, c));
		path6.add(graph.findEdge(c, e));
		path6.add(graph.findEdge(e, t));

		List<MyLink> path7 = new ArrayList<MyLink>(); // length 9, d hops
		path7.add(graph.findEdge(s, a));
		path7.add(graph.findEdge(a, c));
		path7.add(graph.findEdge(c, d));
		path7.add(graph.findEdge(d, t));

		List<MyLink> path8 = new ArrayList<MyLink>(); // length 10, 3 hops
		path8.add(graph.findEdge(s, a));
		path8.add(graph.findEdge(a, d));
		path8.add(graph.findEdge(d, t));

		List<MyLink> path9 = new ArrayList<MyLink>(); // length 20, 1 hops
		path9.add(graph.findEdge(s, t));

		List<List<MyLink>> temp = new LinkedList<List<MyLink>>();
		temp.add(path1);
		temp.add(path2);
		temp.add(path3);
		temp.add(path4);
		temp.add(path5);
		temp.add(path6);
		temp.add(path7);
		temp.add(path8);
		temp.add(path9);

		return new KspTestScenario<String, MyLink>(graph, temp, s, t);
	}

	/**
	 * @return Solutions have increasing hop count and increasing length.
	 */
	public static KspTestScenario<String, MyLink> getZigZagScenario() {
		Graph<String, MyLink> graph = new DirectedOrderedSparseMultigraph<String, MyLink>();

		String s = new String("S");
		graph.addVertex(s);
		String a = new String("A");
		graph.addVertex(a);
		String b = new String("B");
		graph.addVertex(b);
		String c = new String("C");
		graph.addVertex(c);
		String d = new String("D");
		graph.addVertex(d);
		String e = new String("E");
		graph.addVertex(e);
		String z = new String("Z");
		graph.addVertex(z);

		graph.addEdge(new MyLink(2, "S-A"), s, a, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(1, "S-B"), s, b, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(1, "B-C"), b, c, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(2, "B-D"), b, d, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(2, "A-C"), a, c, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(1, "A-B"), a, b, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(2, "C-E"), c, e, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(1, "C-D"), c, d, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(1, "D-Z"), d, z, EdgeType.DIRECTED);
		graph.addEdge(new MyLink(1, "E-Z"), e, z, EdgeType.DIRECTED);

		List<MyLink> path = new ArrayList<MyLink>();
		path.add(graph.findEdge(s, b));
		path.add(graph.findEdge(b, d));
		path.add(graph.findEdge(d, z));

		List<MyLink> path2 = new ArrayList<MyLink>();
		path2.add(graph.findEdge(s, b));
		path2.add(graph.findEdge(b, c));
		path2.add(graph.findEdge(c, d));
		path2.add(graph.findEdge(d, z));

		List<MyLink> path3 = new ArrayList<MyLink>();
		path3.add(graph.findEdge(s, b));
		path3.add(graph.findEdge(b, c));
		path3.add(graph.findEdge(c, e));
		path3.add(graph.findEdge(e, z));

		List<MyLink> path4 = new ArrayList<MyLink>();
		path4.add(graph.findEdge(s, a));
		path4.add(graph.findEdge(a, c));
		path4.add(graph.findEdge(c, d));
		path4.add(graph.findEdge(d, z));

		List<MyLink> path5 = new ArrayList<MyLink>();
		path5.add(graph.findEdge(s, a));
		path5.add(graph.findEdge(a, b));
		path5.add(graph.findEdge(b, d));
		path5.add(graph.findEdge(d, z));

		List<MyLink> path6 = new ArrayList<MyLink>();
		path6.add(graph.findEdge(s, a));
		path6.add(graph.findEdge(a, b));
		path6.add(graph.findEdge(b, c));
		path6.add(graph.findEdge(c, d));
		path6.add(graph.findEdge(d, z));

		List<MyLink> path7 = new ArrayList<MyLink>();
		path7.add(graph.findEdge(s, a));
		path7.add(graph.findEdge(a, c));
		path7.add(graph.findEdge(c, e));
		path7.add(graph.findEdge(e, z));

		List<MyLink> path8 = new ArrayList<MyLink>();
		path8.add(graph.findEdge(s, a));
		path8.add(graph.findEdge(a, b));
		path8.add(graph.findEdge(b, c));
		path8.add(graph.findEdge(c, e));
		path8.add(graph.findEdge(e, z));

		List<List<MyLink>> temp = new LinkedList<List<MyLink>>();
		temp.add(path);
		temp.add(path2);
		temp.add(path3);
		temp.add(path4);
		temp.add(path5);
		temp.add(path6);
		temp.add(path7);
		temp.add(path8);

		return new KspTestScenario<String, MyLink>(graph, temp, s, z);
	}
}
