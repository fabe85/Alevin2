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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mulavito.graph.AbstractLayerStack;
import mulavito.graph.IEdge;
import mulavito.graph.ILayer;
import mulavito.graph.IVertex;
import mulavito.graph.LayerChangedEvent;
import mulavito.gui.control.ViewerContext;
import mulavito.gui.utils.MyGridLayout;

import org.apache.commons.collections15.list.UnmodifiableList;

import edu.uci.ics.jung.visualization.BasicTransformer;
import edu.uci.ics.jung.visualization.MultiLayerTransformer;

/**
 * A panel showing a number of layers.
 * 
 * @author Michael Duelli
 * @author Julian Ott (split-up)
 */
@SuppressWarnings("serial")
public abstract class GraphPanel<L extends ILayer<? extends IVertex, ? extends IEdge>, LV extends LayerViewer<? extends IVertex, ? extends IEdge>>
		extends JPanel {
	/**
	 * The layout / arrangement of the layers.
	 * 
	 * @see #autoAdjustGraphSize
	 */
	private MyGridLayout layout;

	/** A list of viewers. */
	private List<LV> vvs;

	private JScrollBar verticalScrollBar, horizontalScrollBar;
	private JPanel contentPane, south;
	private JComponent corner;
	//
	private AbstractLayerStack<L> stack;
	private ChangeListener stackListener;
	// rendering
	private List<ViewerContext<L, LV>> contexts;
	private boolean synced = true;

	protected GraphPanel() {
		super(new BorderLayout());
		layout = new MyGridLayout(1, 1);

		contentPane = new JPanel();
		add(contentPane);
		contentPane.setLayout(layout);

		vvs = new LinkedList<LV>();

		verticalScrollBar = new JScrollBar(JScrollBar.VERTICAL);
		verticalScrollBar.setValues(0, 0, 0, 0);
		add(verticalScrollBar, BorderLayout.EAST);

		horizontalScrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
		horizontalScrollBar.setValues(0, 0, 0, 0);

		south = new JPanel(new BorderLayout());
		south.add(horizontalScrollBar);
		add(south, BorderLayout.SOUTH);
		setCorner(new SatelliteMultiViewerCorner());

		// for layer stack
		stackListener = new ChangeListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void stateChanged(ChangeEvent e) {
				// handle layer change
				if (e instanceof LayerChangedEvent<?>) {
					LayerChangedEvent<L> lce = (LayerChangedEvent<L>) e;
					if (lce.remove) {
						if (lce.g != null)// remove layer
							removeLayer(getViewer(lce.g));
						else
							// clear layers
							while (vvs.size() > 0)
								removeLayer(vvs.get(0));
					} else if (lce.g != null) // add layer
						addLayer(lce.g);

					javax.swing.SwingUtilities.invokeLater(new Runnable() {
						// To ensure correct behavior when several layers are
						// added at the same time, we slightly delay these
						// calls.
						@Override
						public void run() {
							configureScrollBars();
							configureSpinner();
							arrangeViewers();
							autoZoomToFit();
						}
					});
				}
				// else removed vertices

				updateUI();
			}
		};
		// configure contexts
		contexts = new LinkedList<ViewerContext<L, LV>>();
		//
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				arrangeViewers();
				autoZoomToFit();
			}
		});
	}

	protected abstract LV createLayerViewer(L layer);

	private void addLayer(L layer) {
		// Create and add the vv.
		LV vv = createLayerViewer(layer);
		vvs.add(vv);

		// Do all settings for the vv.
		configViewerTransform(vv);
		for (ViewerContext<L, LV> ctx : contexts)
			ctx.configVisualizationViewer(vv);
		// Do GUI stuff from here on.
		MyGraphZoomScrollPane zsp = new MyGraphZoomScrollPane(vv);
		zsp.setBorder(BorderFactory.createTitledBorder(layer.getLabel()));
		// zsp.setPreferredSize(getGraphSize());
		// zsp.setMaximumSize(getGraphSize());
		contentPane.add(zsp);
		firePropertyChange("Viewers", null, vv);
	}

	private void removeLayer(LV vv) {
		firePropertyChange("Viewers", vv, null);
		MyGraphZoomScrollPane zsp = (MyGraphZoomScrollPane) vv.getParent();
		zsp.resetExternalScrollBars();
		contentPane.remove(zsp);
		deconfigViewerTransform(vv);

		// clear highlights
		for (ViewerContext<L, LV> ctx : contexts)
			ctx.deconfigVisualizationViewer(vv);

		vvs.remove(vv);
	}

	public LV getViewer(L layer) {
		for (LV vv : vvs)
			if (vv.getLayer() == layer)
				return vv;
		return null;
	}

	private void configViewerTransform(LV vv) {
		if (synced && vvs.size() > 0 && vv.getLayer().getLayer() > 0) {
			MultiLayerTransformer master = vvs.get(0).getRenderContext()
					.getMultiLayerTransformer();
			// Synchronize all transformations
			vv.getRenderContext().getMultiLayerTransformer()
					.removeChangeListener(vv);
			vv.getRenderContext().setMultiLayerTransformer(master);
			master.addChangeListener(vv);
		}
	}

	private void deconfigViewerTransform(LV vv) {
		if (vvs.size() > 0 && vv.getLayer().getLayer() > 0) {
			MultiLayerTransformer master = vvs.get(0).getRenderContext()
					.getMultiLayerTransformer();
			// desync all transformations
			if (vv.getRenderContext().getMultiLayerTransformer() == master) {
				master.removeChangeListener(vv);
				vv.getRenderContext().setMultiLayerTransformer(
						new BasicTransformer());
				vv.getRenderContext().getMultiLayerTransformer()
						.addChangeListener(vv);
			}

		}
	}

	public final List<LV> getViewers() {
		return UnmodifiableList.decorate(vvs);
	}

	/**
	 * Auto-resize and auto-rearrange the {@link LayerViewer}s such that all fit
	 * best into the window.
	 * 
	 * Mathematically spoken: The minimum of width and height of the
	 * {@link LayerViewer}s is maximized.
	 */
	private void arrangeViewers() {
		// Get width from the parental JScrollPane.
		int width = contentPane.getWidth();
		int height = contentPane.getHeight();

		int numLayers = 0;
		for (Component cmp : contentPane.getComponents())
			if (cmp.isVisible())
				numLayers++;

		if (numLayers == 0)
			return;

		int newSize = 0;
		int colLayout = numLayers;
		int rowLayout = 1;

		for (int cols = numLayers; cols > 0; cols--) {
			int rows = (int) Math.ceil((double) numLayers / (double) cols);

			// The divisions are automatically floored, such that
			// cols * size <= width and rows * size <= height hold.
			int size = Math.min(width / cols, height / rows);

			if (size > newSize) {
				newSize = size;
				colLayout = cols;
				rowLayout = rows;
			}
		}

		// assign layout information
		layout.setRows(rowLayout);
		layout.setColumns(colLayout);
	}

	/**
	 * Zoom (scale) and translate the stack so it perfectly fits into the
	 * {@link LayerViewer}.
	 */
	public final void autoZoomToFit() {
		validate();
		for (LV vv : vvs) {
			vv.updateGraphBoundsCache();
			if (vv.getParent().isVisible()) {
				LayerViewer.autoZoomViewer(vv);
				if (synced)
					break;
			}
		}
	}

	public final LV getMaximized() {
		if (getViewCount() == 1)
			for (LV vv : vvs)
				if (vv.getParent() != null && vv.getParent().isVisible())
					return vv;

		return null;
	}

	public final void setMaximized(LV value) {
		for (LV vv : vvs)
			if (vv.getParent() != null)
				vv.getParent().setVisible(value == null || value == vv);
		autoZoomToFit();
		configureScrollBars();
		configureSpinner();
	}

	public final int getViewCount() {
		int ret = 0;

		for (LV vv : vvs)
			if (vv.getParent() != null && vv.getParent().isVisible())
				ret++;

		return ret;
	}

	/**
	 * sets the external master scrollbar for the first visible viewer.
	 */
	private void configureScrollBars() {
		if (!synced)
			return;
		// remove all, removing possible listeners.
		for (LV vv : vvs)
			if (vv.getParent() != null)
				((MyGraphZoomScrollPane) vv.getParent()).setExternalScrollBars(
						null, null);
		// reset scrollbars
		horizontalScrollBar.setValues(0, 0, 0, 0);
		verticalScrollBar.setValues(0, 0, 0, 0);
		// search first visible
		for (LV vv : vvs)
			if (vv.getParent() != null && vv.getParent().isVisible()) {
				((MyGraphZoomScrollPane) vv.getParent()).setExternalScrollBars(
						horizontalScrollBar, verticalScrollBar);
				break;
			}
	}

	// sets the component in the corner
	protected void setCorner(JComponent cmp) {
		if (corner != null)
			south.remove(corner);
		corner = cmp;
		if (cmp != null) {
			corner.setPreferredSize(new Dimension(verticalScrollBar
					.getPreferredSize().width, horizontalScrollBar
					.getPreferredSize().height));
			south.add(this.corner, BorderLayout.EAST);
		}
	}

	// if synced, add layers to satellite here
	private void configureSpinner() {
		if (!(corner instanceof SatelliteMultiViewerCorner))
			return;
		List<Object> layers = new LinkedList<Object>();
		for (LV vv : vvs)
			if (vv.getParent() != null && vv.getParent().isVisible())
				layers.add(vv);
		((SatelliteMultiViewerCorner) corner).setLayerList(layers);
	}

	/**
	 * internal method for adding a context.
	 * 
	 * This is only to be used in constructor of {@link ViewerContext}.
	 */
	public final void addContext(ViewerContext<L, LV> ctx) {
		if (ctx == null || ctx.getOwner() != this)
			throw new IllegalArgumentException("owner not correct");
		if (contexts.contains(ctx))
			return;
		contexts.add(ctx);
	}

	/**
	 * gets the displayed layerstack (formerly multilayergraph)
	 */
	public final AbstractLayerStack<L> getLayerStack() {
		return stack;
	}

	/**
	 * sets the layerstack (formerly multilayergraph) to display
	 */
	public final void setLayerStack(AbstractLayerStack<L> stack) {
		if (stack == this.stack)
			return;

		firePropertyChange("LayerStack", this.stack, stack);
		// unload old stack
		if (this.stack != null) {
			setMaximized(null);
			// clear viewers
			while (vvs.size() > 0)
				removeLayer(vvs.get(0));
			for (ViewerContext<L, LV> ctx : contexts)
				ctx.clear();
			//
			this.stack.removeChangeListener(stackListener);
		}

		// load new stack
		this.stack = stack;
		if (stack != null) {
			for (L g : stack)
				addLayer(g);
			stack.addChangeListener(stackListener);
		}

		configureScrollBars();
		configureSpinner();
		arrangeViewers();
		autoZoomToFit();
		updateUI();
	}

	/**
	 * gets if layerviewer transforms are synced
	 */
	public boolean isSynced() {
		return synced;
	}

	/**
	 * sets if layerviewer transforms are synced and performs an autoadjust
	 * cycle.
	 */
	public void setSynced(boolean value) {
		if (value == synced)
			return;
		synced = value;
		// update
		if (value) {
			for (LV vv : vvs) {
				if (vv.getParent() != null)
					((MyGraphZoomScrollPane) vv.getParent()).setCorner(null);
				configViewerTransform(vv);
			}
			//
			horizontalScrollBar.setVisible(true);
			verticalScrollBar.setVisible(true);
			configureScrollBars();
			setCorner(new SatelliteMultiViewerCorner());
			configureSpinner();
		} else {
			setCorner(null);
			horizontalScrollBar.setVisible(false);
			verticalScrollBar.setVisible(false);
			for (LV vv : vvs) {
				if (vv.getParent() != null) {
					MyGraphZoomScrollPane parent = (MyGraphZoomScrollPane) vv
							.getParent();
					parent.resetExternalScrollBars();
					parent.setCorner(new SatelliteViewerCorner(vv));
				}
				deconfigViewerTransform(vv);
			}
		}
		autoZoomToFit();
	}
}
