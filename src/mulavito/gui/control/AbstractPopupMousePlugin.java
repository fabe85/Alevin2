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
package mulavito.gui.control;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.List;

import mulavito.graph.IEdge;
import mulavito.graph.ILayer;
import mulavito.graph.IVertex;
import mulavito.gui.components.LayerViewer;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.control.EditingPopupGraphMousePlugin;

/**
 * Base class for content-aware pop-up mouse plugin.
 * 
 * @author Julian Ott
 * @author Michael Duelli
 * 
 * @param <V>
 *            The vertices
 * @param <E>
 *            The edges
 * @param <L>
 *            The layers
 * @param <LV>
 *            The layer viewers
 */
public abstract class AbstractPopupMousePlugin<V extends IVertex, E extends IEdge, L extends ILayer<V, E>, LV extends LayerViewer<V, E>>
		extends EditingPopupGraphMousePlugin<V, E> {
	protected AbstractPopupMousePlugin(int modifiers, Factory<V> vertexFactory,
			Factory<E> edgeFactory) {
		super(vertexFactory, edgeFactory);

		setModifiers(modifiers);
	}

	/**
	 * Reduced functionality of super class method and extended by own
	 * content-aware editing.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected final void handlePopup(final MouseEvent e) {
		if (checkModifiers(e)) {
			final LV vv = (LV) e.getSource();
			final Layout<V, E> layout = vv.getGraphLayout();
			final L graph = (L) layout.getGraph();
			final Point p = e.getPoint();

			final List<V> sel_vertices = getSelectedVertices();
			final List<E> sel_edges = getSelectedEdges();

			GraphElementAccessor<V, E> pickSupport = vv.getPickSupport();
			if (pickSupport != null) {
				V vertex = pickSupport.getVertex(layout, p.getX(), p.getY());
				E edge = pickSupport.getEdge(layout, p.getX(), p.getY());

				if (vertex != null && !sel_vertices.contains(vertex))
					sel_vertices.add(vertex);
				if (edge != null && !sel_edges.contains(edge))
					sel_edges.add(edge);
			}

			popup.removeAll();
			if (sel_vertices.size() > 0)
				createVertexMenuEntries(p, graph, vv, sel_vertices);
			else if (sel_edges.size() > 0)
				createEdgeMenuEntries(p, graph, vv, sel_edges);
			else
				createGeneralMenuEntries(p, graph, vv);

			if (popup.getComponentCount() > 0)
				popup.show(vv, e.getX(), e.getY());
		}
	}

	protected abstract List<E> getSelectedEdges();

	protected abstract List<V> getSelectedVertices();

	protected abstract void createEdgeMenuEntries(Point point, final L graph,
			final LV vv, final List<E> edges);

	protected abstract void createVertexMenuEntries(Point point, final L graph,
			final LV vv, final List<V> vertices);

	protected abstract void createGeneralMenuEntries(Point point,
			final L graph, final LV vv);
}
