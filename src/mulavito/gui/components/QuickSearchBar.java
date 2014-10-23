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
package mulavito.gui.components;

import java.util.List;
import java.util.regex.Pattern;

import mulavito.graph.IEdge;
import mulavito.graph.ILayer;
import mulavito.graph.IVertex;
import mulavito.gui.utils.GraphPanelChangeSupport;

/**
 * A search bar for finding edges or vertices given by their id or label.
 * 
 * @author Julian Ott
 * @author Michael Duelli
 * @since 2009-11-16
 */
@SuppressWarnings("serial")
public final class QuickSearchBar<L extends ILayer<? extends IVertex, ? extends IEdge>, LV extends LayerViewer<? extends IVertex, ? extends IEdge>>
		extends AbstractSearchField {
	private GraphPanelChangeSupport<L, LV> supp;

	public QuickSearchBar() {
		supp = new GraphPanelChangeSupport<L, LV>() {
			@Override
			protected void onLayerViewerAdd(LV vv) {
				setEditable(getLayerStack() != null
						&& !getLayerStack().isEmpty());
			}

			@Override
			protected void onLayerViewerRemove(LV vv) {
				setEditable(getLayerStack() != null
						&& !getLayerStack().isEmpty());
			}
		};
		//
		setEditable(false);

		setToolTipText("<html>Search for vertices and/or edges, e.g.:<br>"
				+ "- <strong>V13</strong> searches for vertex V13<br>"
				+ "- <strong>V*</strong> searches for all vertices<br>"
				+ "- <strong>V[1|2][2-5]</strong> searches for vertices 12-15 and 22-25<br>"
				+ "- <strong>V3|E7</strong> searches for vertex V3 and edge E7<br>"
				+ "<strong>Hint:</strong> Use regular expressions!</html>");
	}

	@Override
	protected void search(Pattern pat) {
		@SuppressWarnings("unchecked")
		List<LayerViewer<IVertex, IEdge>> vvs = (List<LayerViewer<IVertex, IEdge>>) supp
				.getGraphPanel().getViewers();

		for (L layer : supp.getLayerStack()) {
			// search edges
			for (IEdge e : layer.getEdges()) {
				boolean match = pat.matcher(e.toString()).find();

				// select or deselect edge
				vvs.get(layer.getLayer()).getPickedEdgeState().pick(e, match);
			}
			// search vertices
			for (IVertex v : layer.getVertices()) {
				boolean match = pat.matcher(v.getLabel()).find()
						|| pat.matcher(v.toString()).find();

				// select or deselect vertex
				vvs.get(layer.getLayer()).getPickedVertexState().pick(v, match);
			}
		}
	}

	/**
	 * gets the graphpanel with the layer stack that is to be searched and
	 * hosting the layerviewers to highlight it
	 */
	public GraphPanel<L, LV> getGraphPanel() {
		return supp.getGraphPanel();
	}

	/**
	 * sets the graphpanel with the layer stack that is to be searched and
	 * hosting the layerviewers to highlight it
	 */
	public void setGraphPanel(GraphPanel<L, LV> graphpanel) {
		supp.setGraphPanel(graphpanel);
	}
}
