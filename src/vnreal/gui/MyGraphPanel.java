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
package vnreal.gui;

import java.awt.Event;

import mulavito.gui.components.GraphPanel;
import mulavito.gui.components.LayerViewer;
import mulavito.gui.control.ViewerContext;
import vnreal.Scenario;
import vnreal.constraints.AbstractConstraint;
import vnreal.gui.control.MyEditingPopupGraphMousePlugin;
import vnreal.gui.control.MyGraphLayout;
import vnreal.gui.transformer.LinkDrawPaintTransformer;
import vnreal.gui.transformer.LinkLabelTransformer;
import vnreal.gui.transformer.LinkToolTipTransformer;
import vnreal.gui.transformer.NodeFillPaintTransformer;
import vnreal.gui.transformer.NodeLabelTransformer;
import vnreal.gui.transformer.NodeToolTipTransformer;
import vnreal.network.Link;
import vnreal.network.Network;
import vnreal.network.Node;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

/**
 * A rudimentary multi-layer graph panel
 * 
 * @author Vlad Singeorzan
 * @author Michael Duelli
 * @since 2010-10-04
 */
@SuppressWarnings("serial")
public final class MyGraphPanel<T extends AbstractConstraint, V extends Node<T>, E extends Link<T>, N extends Network<T, V, E>>
		extends GraphPanel<N, LayerViewer<V, E>> {

	private final Scenario scenario;
	
	private class MyMouseContext extends ViewerContext<N, LayerViewer<V, E>> {
		
		public MyMouseContext(GraphPanel<N, LayerViewer<V, E>> owner) {
			super(owner);
		}

		@Override
		public void clear() {
		}

		@Override
		public void configVisualizationViewer(final LayerViewer<V, E> vv) {
			PluggableGraphMouse mouse = new PluggableGraphMouse();
			mouse.add(new ScalingGraphMousePlugin(
					new CrossoverScalingControl(), Event.CTRL_MASK, 1 / 1.1f,
					1.1f));
			mouse.add(new PickingGraphMousePlugin<V, E>());
			mouse.add(new MyEditingPopupGraphMousePlugin<T, V, E, N>(vv, scenario));
			vv.setGraphMouse(mouse);

			vv.getRenderContext().setVertexFillPaintTransformer(
					new NodeFillPaintTransformer<V>(vv.getPickedVertexState()));
			vv.setVertexToolTipTransformer(nodeTTT);
			vv.getRenderContext().setVertexLabelTransformer(nodeLT);
			vv.getRenderer().getVertexLabelRenderer()
					.setPosition(Position.AUTO);

			vv.setEdgeToolTipTransformer(linkTTT);
			vv.getRenderContext().setEdgeLabelTransformer(linkLT);
			vv.getRenderContext().getEdgeLabelRenderer()
					.setRotateEdgeLabels(true);
			vv.getRenderContext().setEdgeDrawPaintTransformer(
					new LinkDrawPaintTransformer<E>(vv.getPickedEdgeState()));
		}

		@Override
		public void deconfigVisualizationViewer(LayerViewer<V, E> vv) {
			vv.getRenderContext().setVertexFillPaintTransformer(null);
			vv.getPickedVertexState().clear();
			vv.getPickedEdgeState().clear();
			// vv.setPickedVertexState(null); // not allowed
			vv.setGraphMouse(null);
		}
	}

	private final NodeToolTipTransformer<V> nodeTTT;
	private final NodeLabelTransformer<V> nodeLT;
	private final LinkToolTipTransformer<E> linkTTT;
	private final LinkLabelTransformer<E> linkLT;

	public MyGraphPanel(Scenario scenario) {
		this.scenario = scenario;
		new MyMouseContext(this);

		nodeTTT = new NodeToolTipTransformer<V>();
		nodeTTT.setEnabled(true);
		nodeLT = new NodeLabelTransformer<V>();

		linkTTT = new LinkToolTipTransformer<E>();
		linkTTT.setEnabled(true);
		linkLT = new LinkLabelTransformer<E>();
	}

	public NodeLabelTransformer<V> getNodeLT() {
		return nodeLT;
	}

	public NodeToolTipTransformer<V> getNodeTTT() {
		return nodeTTT;
	}

	public LinkLabelTransformer<E> getLinkLT() {
		return linkLT;
	}

	public LinkToolTipTransformer<E> getLinkTTT() {
		return linkTTT;
	}

	@Override
	protected LayerViewer<V, E> createLayerViewer(N g) {
		return new LayerViewer<V, E>(new MyGraphLayout<T, V, E>(g), g);
	}
}
