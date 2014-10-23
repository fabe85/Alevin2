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
package mulavito.graph.generators;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import mulavito.graph.IEdge;
import mulavito.graph.ILayer;
import mulavito.graph.IVertex;
import mulavito.utils.distributions.UniformStream;

/**
 * Creates edges within a 1x1 square with probability
 * 
 * <pre>
 * P(v,w) = alpha * exp(-d(v,w) / (beta * L)).
 * </pre>
 * 
 * where 0<alpha, beta<=1, d is the Euclidian distance between vertex v and
 * vertex w, and L is the maximum distance between any two nodes. An increase in
 * the parameter alpha increases the probability of edges between any nodes in
 * the graph, while an increase in beta yields a larger ratio of long edges to
 * short edges.
 * 
 * <pre>
 * How to Model an Internetwork
 * Ellen W. Zegura, Kenneth L. Calvert, Samrat Bhattacharjee
 * </pre>
 * 
 * @see http://www.math.uu.se/research/telecom/software/stgraphs.html
 * 
 *      <pre>
 * @article{Waxman88,
 *  author = {Waxman, Bernard M.},
 *  title = {Routing of Multipoint Connections},
 *  journal = {IEEE Journal of Selected Areas in Communication},
 *  pages = {1617--1622},
 *  number = {9},
 *  volume = {6},
 *  month = dec,
 *  year = {1988},
 * }
 * </pre>
 * 
 * @author Julian Ott
 * @author Michael Duelli
 * @since 2010-07-03
 */
public final class WaxmanGraphGenerator<V extends IVertex, E extends IEdge>
		implements IEdgeGenerator<V, E> {
	/** An increase in alpha will increase the number of edges in the graph */
	private final double alpha;

	/**
	 * An increase in beta will increase the ratio of long edges relative to
	 * short edges
	 */
	private final double beta;

	/** All edges are ensured to be bidirectional */
	private final boolean bidirectional;

	private HashMap<V, Point2D> pos;
	private static final UniformStream rnd = new UniformStream();

	public WaxmanGraphGenerator(double alpha, double beta, boolean bidirectional) {
		if (Double.isNaN(alpha) || Double.isInfinite(alpha)
				|| Double.isNaN(beta) || Double.isInfinite(beta))
			throw new IllegalArgumentException();
		if (!(0 < alpha && beta <= 1))
			throw new IllegalArgumentException();

		this.alpha = alpha;
		this.beta = beta;
		this.bidirectional = bidirectional;
	}

 	@SuppressWarnings("static-access")
	public WaxmanGraphGenerator(double alpha, double beta, boolean bidirectional, long fixedSeed) {
        if (Double.isNaN(alpha) || Double.isInfinite(alpha)
                        || Double.isNaN(beta) || Double.isInfinite(beta))
                throw new IllegalArgumentException();
        if (!(0 < alpha && beta <= 1))
                throw new IllegalArgumentException();

        this.alpha = alpha;
        this.beta = beta;
        this.bidirectional = bidirectional;

        rnd.setSeed(fixedSeed);
 	}
	
	@Override
	public void generate(ILayer<V, E> g) {
		Rectangle2D.Double bounds = new Rectangle2D.Double(0.0, 0.0, 1.0, 1.0);

		// Distribute all vertices over the 1x1/GUI area.
		pos = new HashMap<V, Point2D>(g.getVertexCount());
		for (V v : g.getVertices())
			pos.put(v, new Point2D.Double(rnd.nextDouble(), rnd.nextDouble()));

		// max distance = diagonal
		final double L = Math.sqrt(bounds.width * bounds.width + bounds.height
				* bounds.height); // sqrt(2)

		for (V v : g.getVertices())
			for (V w : g.getVertices()) {
				if (v == w)
					continue;

				// Euclidian distance
				final double d = pos.get(v).distance(pos.get(w));

				final double p = rnd.nextDouble();
				final double p_waxman = alpha * Math.exp(-d / (beta * L));

				if (p < p_waxman) {
					if (g.findEdge(v, w) == null)
						g.addEdge(g.getEdgeFactory().create(), v, w);

					if (bidirectional && g.findEdge(w, v) == null)
						g.addEdge(g.getEdgeFactory().create(), w, v);
				}
			}
	}

	/** @return A map with the positions for the last graph generation. */
	public HashMap<V, Point2D> getPositions() {
		return pos;
	}
}
