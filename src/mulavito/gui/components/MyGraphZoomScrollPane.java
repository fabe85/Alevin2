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

import java.awt.Rectangle;
import java.awt.event.AdjustmentListener;
import java.awt.geom.Point2D;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.transform.BidirectionalTransformer;

/**
 * A custom wrapper for {@link GraphZoomScrollPane} correcting an offset issue
 * which made scrollbars messed up and unusable.
 * 
 * @author Julian Ott
 * @since 2010-05-05
 */
@SuppressWarnings("serial")
public final class MyGraphZoomScrollPane extends GraphZoomScrollPane {
	private JScrollBar oldhscroll, oldvscroll;
	private LayerViewer<?, ?> vv;

	public MyGraphZoomScrollPane(LayerViewer<?, ?> vv) {
		super(vv);
		this.vv = vv;
		// save internal scrollbar data
		oldhscroll = horizontalScrollBar;
		oldvscroll = verticalScrollBar;
		// make corner small
		setCorner(null);
	}

	/**
	 * Corrects the scrollbar sync issue with JUNG 2.0.1
	 * 
	 * recalculated at every scroll/translate/addvertex operation
	 */
	@Override
	protected void setScrollBarValues(Rectangle rectangle, Point2D h0,
			Point2D h1, Point2D v0, Point2D v1) {
		// inject real graph bounds
		rectangle.setRect(vv.getGraphBoundsCache());
		// inject offset
		BidirectionalTransformer viewTransformer = vv.getRenderContext()
				.getMultiLayerTransformer().getTransformer(Layer.VIEW);
		BidirectionalTransformer layoutTransformer = vv.getRenderContext()
				.getMultiLayerTransformer().getTransformer(Layer.LAYOUT);
		Point2D location = layoutTransformer.inverseTransform(viewTransformer
				.inverseTransform(new Point2D.Double()));
		double xoff = h0.getX() - location.getX();
		double yoff = v0.getY() - location.getY();
		// offset
		h0.setLocation(h0.getX() - xoff, h0.getY() - yoff);
		h1.setLocation(h1.getX() - xoff, h1.getY() - yoff);
		v0.setLocation(v0.getX() - xoff, v0.getY() - yoff);
		v1.setLocation(v1.getX() - xoff, v1.getY() - yoff);
		//
		super.setScrollBarValues(rectangle, h0, h1, v0, v1);
	}

	protected void resetExternalScrollBars() {
		setExternalScrollBars(oldhscroll, oldvscroll);
	}

	/**
	 * sets external scrollbars, but if they already have an adjustment
	 * listener, they are passive, i.e. they show the current scroll state but
	 * cannot be dragged. this is to prevent deadlocks with multiple scrollbar
	 * usage.
	 */
	protected void setExternalScrollBars(JScrollBar hBar, JScrollBar vBar) {
		// vertical
		// null means: no visible scrollbars
		if (vBar == null) {
			oldvscroll.setVisible(false);
			vBar = oldvscroll;
		} else
			oldvscroll.setVisible(vBar == oldvscroll);

		// add listeners, but only if no listeners applied,
		// otherwise deadlock may appear
		if (vBar != oldvscroll && vBar.getAdjustmentListeners().length < 1)
			for (AdjustmentListener l : oldvscroll.getAdjustmentListeners())
				vBar.addAdjustmentListener(l);

		if (verticalScrollBar != oldvscroll)
			for (AdjustmentListener l : oldvscroll.getAdjustmentListeners())
				verticalScrollBar.removeAdjustmentListener(l);

		// set scrollbar
		verticalScrollBar = vBar;

		// horizontal
		// null means: no visible scrollbars
		if (hBar == null) {
			oldhscroll.setVisible(false);
			hBar = oldhscroll;
		} else
			oldhscroll.setVisible(hBar == oldhscroll);

		// add listeners, but only if no listeners applied,
		// otherwise deadlock may appear
		if (hBar != oldhscroll && hBar.getAdjustmentListeners().length < 1)
			for (AdjustmentListener l : oldhscroll.getAdjustmentListeners())
				hBar.addAdjustmentListener(l);

		if (horizontalScrollBar != oldhscroll)
			for (AdjustmentListener l : oldhscroll.getAdjustmentListeners())
				horizontalScrollBar.removeAdjustmentListener(l);

		// set scrollbar
		horizontalScrollBar = hBar;
	}

	/**
	 * now allowing NULL values, which get replaced by a non visible label
	 */
	@Override
	public void setCorner(JComponent corner) {
		if (corner == null) {
			super.setCorner(new JPanel());
			super.getCorner().setVisible(false);
			// super.getCorner().setPreferredSize(new Dimension(0, 0));
		} else
			super.setCorner(corner);
	}
}
