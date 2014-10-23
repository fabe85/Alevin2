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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mulavito.graph.IEdge;
import mulavito.graph.ILayer;
import mulavito.graph.IVertex;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import edu.uci.ics.jung.visualization.util.ChangeEventSupport;

/**
 * <p>
 * A subclass of {@link VisualizationViewer} which:
 * <ul>
 * <li>provides methods to visually mark a graph as being required or
 * unavailable via {@link BackgroundLabel},
 * <li>caches the graph bounds to correctly update scroll bars.
 * </ul>
 * </p>
 * 
 * @author Michael Duelli
 * @author Julian Ott
 * @since 2009-02-04
 * 
 * @param V
 *            The vertex type
 * @param E
 *            The edge type
 */
@SuppressWarnings("serial")
public class LayerViewer<V extends IVertex, E extends IEdge> extends
		VisualizationViewer<V, E> {
	public enum Directions {
		HORIZONTAL, VERTICAL, BOTH
	}

	/**
	 * Zoom (scale) and translate the graph so it perfectly fits into the
	 * {@link VisualizationViewer}.
	 * 
	 * @param vv
	 *            The viewer.
	 */
	public static void autoZoomViewer(LayerViewer<?, ?> vv) {
		autoZoomViewer(vv, vv, Directions.BOTH);
	}

	public static void autoZoomViewer(LayerViewer<?, ?> vv, Directions direction) {
		autoZoomViewer(vv, vv, direction);
	}

	/**
	 * wrapper method for different viewer and control hosting the graph
	 * 
	 * @param vv
	 *            viewer
	 * @param home
	 *            default viewer with layout, used to obtain cached graph bounds
	 */
	public static void autoZoomViewer(VisualizationViewer<?, ?> vv,
			LayerViewer<?, ?> home) {
		autoZoomViewer(vv, home, Directions.BOTH);
	}

	public static void autoZoomViewer(VisualizationViewer<?, ?> vv,
			LayerViewer<?, ?> home, Directions direction) {
		if (vv == null || home == null)
			return;

		// reset transforms
		MutableTransformer layoutTrans = vv.getRenderContext()
				.getMultiLayerTransformer()
				.getTransformer(edu.uci.ics.jung.visualization.Layer.LAYOUT);
		layoutTrans.setToIdentity();
		MutableTransformer viewTrans = vv.getRenderContext()
				.getMultiLayerTransformer()
				.getTransformer(edu.uci.ics.jung.visualization.Layer.VIEW);
		viewTrans.setToIdentity();

		Dimension dim = vv.getSize();
		Rectangle2D.Double graphBounds = home.getGraphBoundsCache();

		CrossoverScalingControl scaler = new CrossoverScalingControl();

		// Scale using crossover scaler, so vertices will not grow
		// larger than they are in original
		double factor = Double.POSITIVE_INFINITY;

		if (direction == Directions.HORIZONTAL || direction == Directions.BOTH)
			factor = dim.getWidth() / graphBounds.width;
		if (direction == Directions.VERTICAL || direction == Directions.BOTH
				|| Double.isInfinite(factor))
			factor = Math.min(factor, dim.getHeight() / graphBounds.height);
		scaler.scale(vv, (float) factor, vv.getCenter());

		// Translate center of graph to center of vv.
		Point2D lvc = vv.getRenderContext().getMultiLayerTransformer()
				.inverseTransform(vv.getCenter());
		double dx = (lvc.getX() - graphBounds.getCenterX());
		double dy = (lvc.getY() - graphBounds.getCenterY());
		layoutTrans.translate(dx, dy);
	}

	private ILayer<V, E> g;
	/** The extent/size of the graph. */
	private Rectangle2D.Double graphBoundsCache = null;

	public LayerViewer(Layout<V, E> layout, ILayer<V, E> g) {
		super(layout);
		this.g = g;
		if (layout.getGraph() != g)
			layout.setGraph(g);

		// Cache the graph size.
		updateGraphBoundsCache();
		if (getGraphLayout() instanceof ChangeEventSupport)
			((ChangeEventSupport) getGraphLayout())
					.addChangeListener(new ChangeListener() {
						@Override
						public void stateChanged(ChangeEvent e) {
							updateGraphBoundsCache();
						}
					});
	}

	/** @return the extent of the graph according to its vertices' location */
	public void updateGraphBoundsCache() {
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;

		for (V v : getGraphLayout().getGraph().getVertices()) {
			Point2D p = getGraphLayout().transform(v);

			minX = Math.min(minX, p.getX());
			minY = Math.min(minY, p.getY());

			maxX = Math.max(maxX, p.getX());
			maxY = Math.max(maxY, p.getY());
		}

		// no point
		if (minX > maxX || minY > maxY)
			minX = maxX = minY = maxY = 0;

		if (graphBoundsCache == null)
			graphBoundsCache = new Rectangle2D.Double();

		graphBoundsCache.setRect(minX, minY, maxX - minX, maxY - minY);

		double excess = Math.max(Math.max(graphBoundsCache.width * 0.1,
				graphBoundsCache.height * 0.1), 10);

		// include the given excess
		graphBoundsCache.setRect(graphBoundsCache.x - excess,
				graphBoundsCache.y - excess, graphBoundsCache.width + 2
						* excess, graphBoundsCache.height + 2 * excess);
	}

	public ILayer<V, E> getLayer() {
		return g;
	}

	/** gets the cached graphbounds */
	public final Rectangle2D.Double getGraphBoundsCache() {
		return graphBoundsCache;
	}

	public final void setEdgeDrawPaintTransformer(
			Transformer<E, Paint> paintTransformer) {
		getRenderContext().setArrowDrawPaintTransformer(paintTransformer);
		getRenderContext().setArrowFillPaintTransformer(paintTransformer);
		getRenderContext().setEdgeDrawPaintTransformer(paintTransformer);
	}

	public final void setEdgeStrokeTransformer(
			Transformer<E, Stroke> strokeTransformer) {
		getRenderContext().setEdgeArrowStrokeTransformer(strokeTransformer);
		getRenderContext().setEdgeStrokeTransformer(strokeTransformer);
	}

	public final void setBackgroundText(String text) {
		preRenderers.clear();
		if (text != null)
			addPreRenderPaintable(new BackgroundLabel(this, text));
	}

	@Override
	public String toString() {
		return g.getLabel();
	}

	/** Inner class that realizes the background painting. */
	private final class BackgroundLabel implements
			VisualizationViewer.Paintable {
		private String str;
		private LayerViewer<?, ?> vv;

		protected BackgroundLabel(LayerViewer<?, ?> vv, String label) {
			this.vv = vv;
			this.str = label;
		}

		@Override
		public void paint(Graphics g) {
			Dimension d = vv.getSize();
			FontRenderContext frc = ((Graphics2D) g).getFontRenderContext();
			Font f = new Font("Times", Font.BOLD, 30);

			TextLayout tl = new TextLayout(str, f, frc);
			AffineTransform transform = new AffineTransform();
			transform.setToTranslation(d.width / 2, d.height / 2);
			transform.rotate(Math.toRadians(315));
			Shape shape = tl.getOutline(transform);
			g.translate(-shape.getBounds().width / 2,
					shape.getBounds().height / 2);
			g.setColor(Color.lightGray);
			((Graphics2D) g).draw(shape);
		}

		@Override
		public boolean useTransform() {
			return false;
		}
	}
}
