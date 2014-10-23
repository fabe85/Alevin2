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
package mulavito.algorithms.shortestpath.ksp;

import java.util.ArrayList;
import java.util.List;

import mulavito.algorithms.shortestpath.ShortestPathAlgorithm;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Graph;

/**
 * @author Xiaohua Qin
 * @author Michael Duelli (partial rewrite, generics)
 * 
 * @param <V>
 *            The parameter for vertices
 * @param <E>
 *            The parameter for edges
 */
public abstract class KShortestPathAlgorithm<V, E> extends
		ShortestPathAlgorithm<V, E> {
	protected KShortestPathAlgorithm(Graph<V, E> graph,
			Transformer<E, Number> nev) {
		super(graph, nev);
	}

	/**
	 * @param source
	 *            The source
	 * @param target
	 *            The destination
	 * @param k
	 *            The number of shortest paths to calculate
	 * @return a list with up to <code>k</code> shortest paths from source to
	 *         target or an EMPTY list if target is not reachable from source
	 */
	protected abstract List<List<E>> getShortestPathsIntern(final V source,
			final V target, int k);

	/**
	 * @param source
	 *            The source
	 * @param target
	 *            The destination
	 * @param k
	 *            The number of shortest paths to calculate
	 * @return a list with up to <code>k</code> shortest paths from source to
	 *         target or an EMPTY list if target is not reachable from source
	 */
	public final List<List<E>> getShortestPaths(final V source, final V target,
			int k) {
		if (k < 1)
			throw new AssertionError();

		if (check(source, target))
			return new ArrayList<List<E>>();

		// Let concrete implementation calculate paths.
		return getShortestPathsIntern(source, target, k);
	}

	/** Test whether the source is equal to the target */
	private boolean check(V source, V target) {
		if (!graph.containsVertex(source) || !graph.containsVertex(target))
			throw new AssertionError(
					"The source or the target node does not exist!");

		return source.equals(target);
	}
}
