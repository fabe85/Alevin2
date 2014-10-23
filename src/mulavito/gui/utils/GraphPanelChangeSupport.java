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
package mulavito.gui.utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import mulavito.graph.AbstractLayerStack;
import mulavito.graph.IEdge;
import mulavito.graph.ILayer;
import mulavito.graph.IVertex;
import mulavito.gui.components.GraphPanel;
import mulavito.gui.components.LayerViewer;

/**
 * helper class to listen to {@link GraphPanel} change events
 * 
 * @author Julian Ott
 * @since 2011-02-09
 */
public abstract class GraphPanelChangeSupport<L extends ILayer<? extends IVertex, ? extends IEdge>, LV extends LayerViewer<? extends IVertex, ? extends IEdge>> {
	private GraphPanel<L, LV> graphPanel;
	private final PropertyChangeListener graphPanelListener;

	public GraphPanelChangeSupport() {
		graphPanelListener = new PropertyChangeListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("Viewers")) {
					if (evt.getOldValue() instanceof LayerViewer<?, ?>
							&& evt.getNewValue() == null)
						onLayerViewerRemove((LV) evt.getOldValue());
					else if (evt.getNewValue() instanceof LayerViewer<?, ?>
							&& evt.getOldValue() == null)
						onLayerViewerAdd((LV) evt.getNewValue());
				} else if (evt.getPropertyName().equals("LayerStack")) {
					onLayerStackChange(
							(AbstractLayerStack<L>) evt.getOldValue(),
							(AbstractLayerStack<L>) evt.getNewValue());
				}
			}
		};
	}

	/**
	 * free for you to override. triggered before the change appears.
	 */
	protected void onGraphPanelChange(GraphPanel<L, LV> oldValue,
			GraphPanel<L, LV> newValue) {
	}

	/**
	 * free for you to override. triggered before the change appears.
	 */
	protected void onLayerStackChange(AbstractLayerStack<? extends L> oldValue,
			AbstractLayerStack<? extends L> newValue) {
	}

	/**
	 * free for you to override. triggered before the change appears.
	 */
	protected void onLayerViewerAdd(LV vv) {
	}

	/**
	 * free for you to override. triggered before the change appears.
	 */
	protected void onLayerViewerRemove(LV vv) {
	}

	public final AbstractLayerStack<L> getLayerStack() {
		return graphPanel == null ? null : graphPanel.getLayerStack();
	}

	/*
	 * properties
	 */

	public final GraphPanel<L, LV> getGraphPanel() {
		return graphPanel;
	}

	public final void setGraphPanel(GraphPanel<L, LV> graphpanel2) {
		if (graphpanel2 == graphPanel)
			return;
		if (graphPanel != null) {
			graphPanel.removePropertyChangeListener(graphPanelListener);
			for (LV vv : graphPanel.getViewers())
				onLayerViewerRemove(vv);
			onLayerStackChange(graphPanel.getLayerStack(), null);
		}
		if (graphpanel2 != null) {
			onLayerStackChange(null, graphpanel2.getLayerStack());
			for (LV vv : graphpanel2.getViewers())
				onLayerViewerAdd(vv);
			graphpanel2.addPropertyChangeListener(graphPanelListener);
		}
		onGraphPanelChange(graphPanel, graphpanel2);
		//
		graphPanel = graphpanel2;
	}
}
