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
package vnreal.algorithms.utils;

import org.apache.commons.collections15.Predicate;

import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNode;
import edu.uci.ics.jung.algorithms.filters.Filter;
import edu.uci.ics.jung.graph.Graph;

/**
 * <p>
 * Apply given filter predicates on a graph given in {@link #transform(Graph)}
 * method to construct a new graph which only contains the vertices and edges
 * fulfilling the predicates.
 * </p>
 * 
 * <p>
 * The original graph is not changed.
 * </p>
 * 
 * @author Michael Duelli
 * @since 2010-09-14
 */
public final class GraphFilter implements Filter<SubstrateNode, SubstrateLink> {
	private final Predicate<SubstrateNode> nodePredicate;
	private final Predicate<SubstrateLink> linkPredicate;

	public GraphFilter(Predicate<SubstrateNode> nodePredicate,
			Predicate<SubstrateLink> linkPredicate) {
		this.nodePredicate = nodePredicate;
		this.linkPredicate = linkPredicate;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Graph<SubstrateNode, SubstrateLink> transform(
			Graph<SubstrateNode, SubstrateLink> g) {
		Graph<SubstrateNode, SubstrateLink> filtered;
		try {
			filtered = g.getClass().newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(
					"Unable to create copy of existing graph: ", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(
					"Unable to create copy of existing graph: ", e);
		}

		for (SubstrateNode v : g.getVertices())
			if (nodePredicate.evaluate(v))
				filtered.addVertex(v);

		for (SubstrateLink e : g.getEdges()) {
			if (linkPredicate.evaluate(e))
				filtered.addEdge(e, g.getIncidentVertices(e));
		}

		return filtered;
	}
}
