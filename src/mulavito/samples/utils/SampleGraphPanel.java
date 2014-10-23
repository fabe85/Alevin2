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
package mulavito.samples.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Event;
import java.awt.Paint;
import java.awt.Stroke;

import mulavito.gui.components.GraphPanel;
import mulavito.gui.components.LayerViewer;
import mulavito.gui.control.ViewerContext;
import mulavito.samples.utils.SampleGraphDocument.MyV;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * simple graph panel for our graph document. it offers a zooming / translating
 * mouse
 * 
 * @author Julian Ott
 */
@SuppressWarnings("serial")
public class SampleGraphPanel extends GraphPanel<MyL, LayerViewer<MyV, MyE>> {
	private class MyMouseContext extends
			ViewerContext<MyL, LayerViewer<MyV, MyE>> {
		private PluggableGraphMouse mouse;

		public MyMouseContext(GraphPanel<MyL, LayerViewer<MyV, MyE>> owner) {
			super(owner);
			mouse = new PluggableGraphMouse();
			mouse.add(new ScalingGraphMousePlugin(
					new CrossoverScalingControl(), Event.CTRL_MASK, 1 / 1.1f,
					1.1f));
			mouse.add(new PickingGraphMousePlugin<MyV, MyE>());
		}

		@Override
		public void clear() {
		}

		@Override
		public void configVisualizationViewer(LayerViewer<MyV, MyE> vv) {
			vv.setGraphMouse(mouse);

			// picked vertices appear yellow
			vv.getRenderContext().setVertexFillPaintTransformer(
					new Transformer<MyV, Paint>() {
						private PickedState<MyV> picked;

						@Override
						public Paint transform(MyV input) {
							if (picked.isPicked(input))
								return Color.YELLOW;
							return Color.RED;
						}

						// constructor drop-in for anonymous types
						public Transformer<MyV, Paint> init(
								PickedState<MyV> picked) {
							this.picked = picked;
							return this;
						}
					}.init(vv.getPickedVertexState()));
			vv.getRenderContext().setEdgeStrokeTransformer(
					new Transformer<MyE, Stroke>() {

						@Override
						public Stroke transform(MyE input) {
							if (input instanceof MyEA)
								return RenderContext.DASHED;
							return new BasicStroke();
						}
					});
		}

		@Override
		public void deconfigVisualizationViewer(LayerViewer<MyV, MyE> vv) {
			vv.getRenderContext().setVertexFillPaintTransformer(null);
			vv.getRenderContext().setEdgeStrokeTransformer(null);
			vv.getPickedVertexState().clear();
			vv.getPickedEdgeState().clear();
			// vv.setPickedVertexState(null); // not allowed
			vv.setGraphMouse(null);
		}
	}

	public SampleGraphPanel() {
		new MyMouseContext(this);
	}

	@Override
	protected LayerViewer<MyV, MyE> createLayerViewer(MyL g) {
		KKLayout<MyV, MyE> layout = new KKLayout<MyV, MyE>(g);
		layout.setMaxIterations(100);
		return new LayerViewer<MyV, MyE>(layout, g);
	}
}
