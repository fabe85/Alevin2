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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.SatelliteVisualizationViewer;
import edu.uci.ics.jung.visualization.transform.shape.ShapeTransformer;

/**
 * A wrapper to work around an offset issue in
 * {@link SatelliteVisualizationViewer}.
 * 
 * @author Julian Ott
 * @since 2010-05-05
 */
@SuppressWarnings("serial")
public final class MySatelliteVisualizationViewer<V, E> extends
		SatelliteVisualizationViewer<V, E> {
	/**
	 * @param master
	 *            The original viewer whose miniature edition is shown
	 * @param preferredSize
	 *            The size of the miniature view
	 */
	public MySatelliteVisualizationViewer(VisualizationViewer<V, E> master,
			Dimension preferredSize) {
		super(master, preferredSize);

		// correct offset issue
		preRenderers.clear();
		addPreRenderPaintable(new ViewLens(this, master));
	}

	/**
	 * @param master
	 *            The original viewer whose miniature edition is shown
	 */
	public MySatelliteVisualizationViewer(VisualizationViewer<V, E> master) {
		super(master);

		// correct offset issue
		preRenderers.clear();
		addPreRenderPaintable(new ViewLens(this, master));
	}

	/** offset-corrected jung2 view lens */
	private class ViewLens implements Paintable {
		VisualizationViewer<V, E> master;
		VisualizationViewer<V, E> vv;

		public ViewLens(VisualizationViewer<V, E> vv,
				VisualizationViewer<V, E> master) {
			this.vv = vv;
			this.master = master;
		}

		@Override
		public void paint(Graphics g) {
			ShapeTransformer masterViewTransformer = master.getRenderContext()
					.getMultiLayerTransformer().getTransformer(Layer.VIEW);
			ShapeTransformer masterLayoutTransformer = master
					.getRenderContext().getMultiLayerTransformer()
					.getTransformer(Layer.LAYOUT);
			ShapeTransformer vvLayoutTransformer = vv.getRenderContext()
					.getMultiLayerTransformer().getTransformer(Layer.LAYOUT);

			// changed: bounds without offset
			Shape lens = new Rectangle(new Point(), master.getSize());

			lens = masterViewTransformer.inverseTransform(lens);
			lens = masterLayoutTransformer.inverseTransform(lens);
			lens = vvLayoutTransformer.transform(lens);
			Graphics2D g2d = (Graphics2D) g;
			Color old = g.getColor();
			Color lensColor = master.getBackground();
			vv.setBackground(lensColor.darker());
			g.setColor(lensColor);
			g2d.fill(lens);
			g.setColor(Color.gray);
			g2d.draw(lens);
			g.setColor(old);
		}

		@Override
		public boolean useTransform() {
			return true;
		}
	}
}
