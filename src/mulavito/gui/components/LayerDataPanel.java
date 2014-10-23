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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import mulavito.graph.IEdge;
import mulavito.graph.ILayer;
import mulavito.graph.IVertex;
import mulavito.utils.Resources;

import org.apache.commons.collections15.Transformer;

/**
 * A GUI element showing information about layers, vertices, and edges.
 * 
 * It uses a {@link MouseListener} to show the data of a certain
 * {@link LayerViewer}.
 * 
 * @author Michael Duelli
 * @author Julian Ott (mulavito integration)
 * @since 2008-10-21
 */
@SuppressWarnings("serial")
public final class LayerDataPanel<V extends IVertex, E extends IEdge> extends
		FloatablePanel {
	private class InfoPanelData {
		protected int minDegree = Integer.MAX_VALUE;
		protected double avgDegree = 0.0;
		protected int maxDegree = Integer.MIN_VALUE;
	}

	private JTextArea textArea;
	private final String defaultText = "Hover above a layer for details...\n";
	private GraphPanel<? extends ILayer<V, E>, ?> graphPanel;
	private ILayer<V, E> current;

	// listeners
	private final PropertyChangeListener graphPanelListener;
	private final MouseAdapter mouseListener;

	public LayerDataPanel(FloatingTabbedPane owner) {
		super("Layer Data", owner);

		textArea = new JTextArea(defaultText);
		textArea.setEditable(false);
		textArea.setCaretPosition(0); // Scroll up the text area.
		JScrollPane textPane = new JScrollPane(textArea);

		add(textPane, BorderLayout.CENTER);

		// updates the data
		mouseListener = new MouseAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void mouseEntered(MouseEvent e) {
				if (e.getSource() instanceof LayerViewer<?, ?>) {
					LayerViewer<V, E> vv = (LayerViewer<V, E>) e.getSource();
					showData(vv.getLayer());
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				showData(null);
			}
		};

		// adds/removes focus listeners on layers
		graphPanelListener = new PropertyChangeListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("Viewers")) {
					if (evt.getOldValue() instanceof LayerViewer<?, ?>
							&& evt.getNewValue() == null) {
						// GraphPanel#removeLayer
						((LayerViewer<?, ?>) evt.getOldValue())
								.removeMouseListener(mouseListener);
					} else if (evt.getOldValue() instanceof List<?>) {
						System.out.println("Replace");
						for (LayerViewer<?, ?> vv : ((List<LayerViewer<?, ?>>) evt
								.getOldValue()))
							vv.removeMouseListener(mouseListener);
					}

					if (evt.getNewValue() instanceof LayerViewer<?, ?>
							&& evt.getOldValue() == null) {
						// GraphPanel#addLayer
						((LayerViewer<?, ?>) evt.getNewValue())
								.addMouseListener(mouseListener);
					} else if (evt.getNewValue() instanceof List<?>) {
						// New Layer List on start up
						for (LayerViewer<?, ?> vv : ((List<LayerViewer<?, ?>>) evt
								.getNewValue()))
							vv.addMouseListener(mouseListener);
					}

					showData(current);
				}
			}
		};
	}

	private void showData(ILayer<V, E> value) {
		if (value == null)
			textArea.setText(defaultText);
		else
			textArea.setText(calculateData(value));

		current = value;
	}

	private String calculateData(ILayer<V, E> graph) {
		StringBuilder ret = new StringBuilder();
		InfoPanelData cur;

		ret.append("Layer " + graph.getLayer() + "\n");
		ret.append("  #vertices: " + graph.getVertexCount() + "\n");
		ret.append("  #edges (directed): " + graph.getEdgeCount() + "\n");

		// Connection degrees.
		cur = calcLayerProperty(graph, degreeTrans);
		ret.append("    incident degree (min/avg/max): " + cur.minDegree + "/"
				+ Resources.nf.format(cur.avgDegree) + "/" + cur.maxDegree
				+ "\n");

		// Incoming edge node degree.
		cur = calcLayerProperty(graph, inDegreeTrans);
		ret.append("    node incoming degree (min/avg/max): " + cur.minDegree
				+ "/" + Resources.nf.format(cur.avgDegree) + "/"
				+ cur.maxDegree + "\n");

		// Outgoing edge node degree.
		cur = calcLayerProperty(graph, outDegreeTrans);
		ret.append("    node outgoing degree (min/avg/max): " + cur.minDegree
				+ "/" + Resources.nf.format(cur.avgDegree) + "/"
				+ cur.maxDegree + "\n");

		return ret.toString();
	}

	private InfoPanelData calcLayerProperty(ILayer<V, E> graph,
			LayerPropertyTransformer lpt) {
		InfoPanelData cur = new InfoPanelData();
		int degree;
		int cumSum = 0;

		lpt.setLayer(graph);

		for (V v : graph.getVertices()) {
			degree = lpt.transform(v);

			cur.minDegree = Math.min(cur.minDegree, degree);// Min.
			cumSum += degree; // Sum up for building avg.
			cur.maxDegree = Math.max(cur.maxDegree, degree); // Max.
		}
		cur.avgDegree = (double) cumSum / graph.getVertexCount(); // Avg.
		return cur;
	}

	public void setGraphPanel(GraphPanel<? extends ILayer<V, E>, ?> value) {
		if (value == graphPanel)
			return;

		if (graphPanel != null) {
			graphPanel.removePropertyChangeListener(graphPanelListener);
			graphPanelListener.propertyChange(new PropertyChangeEvent(
					graphPanel, "Viewers", graphPanel.getViewers(), null));
		}

		if (value != null) {
			value.addPropertyChangeListener(graphPanelListener);
			graphPanelListener.propertyChange(new PropertyChangeEvent(value,
					"Viewers", null, value.getViewers()));
		}

		graphPanel = value;
	}

	private LayerPropertyTransformer degreeTrans = new LayerPropertyTransformer() {
		@Override
		public Integer transform(V v) {
			return g.degree(v);
		}
	};

	private LayerPropertyTransformer inDegreeTrans = new LayerPropertyTransformer() {
		@Override
		public Integer transform(V v) {
			return g.inDegree(v);
		}
	};

	private LayerPropertyTransformer outDegreeTrans = new LayerPropertyTransformer() {
		@Override
		public Integer transform(V v) {
			return g.outDegree(v);
		}
	};

	private abstract class LayerPropertyTransformer implements
			Transformer<V, Integer> {
		protected ILayer<V, E> g;

		public void setLayer(ILayer<V, E> g) {
			this.g = g;
		}
	}
}
