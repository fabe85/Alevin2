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
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import mulavito.graph.AbstractLayerStack;

/**
 * Display a {@link JTree} with specific model and listeners.
 * 
 * @author Michael Duelli
 * @author Julian Ott
 * @since 2011-04-27
 */
@SuppressWarnings("serial")
public class TreePanel extends JPanel {
	private GraphPanel<?, ?> graphPanel;
	private final JTree tree;
	private String caption;
	private final PropertyChangeListener graphPanelListener;

	public TreePanel(GraphPanel<?, ?> graphpanel) {
		super(new BorderLayout());

		tree = new JTree(createTreeModel());
		tree.setEditable(false);

		add(new JScrollPane(tree), BorderLayout.CENTER);

		// Prevents interception of F2 Keystroke.
		// getInputMap().getParent() seems never to be null
		tree.getInputMap().getParent().remove(
				KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));

		graphPanelListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("Viewers")) {
					if (evt.getOldValue() instanceof LayerViewer<?, ?>
							&& evt.getNewValue() == null)
						onLayerViewerRemove((LayerViewer<?, ?>) evt
								.getOldValue());
					else if (evt.getNewValue() instanceof LayerViewer<?, ?>
							&& evt.getOldValue() == null)
						onLayerViewerAdd((LayerViewer<?, ?>) evt.getNewValue());
				} else if (evt.getPropertyName().equals("LayerStack")) {
					onLayerStackChange((AbstractLayerStack<?>) evt
							.getOldValue(), (AbstractLayerStack<?>) evt
							.getNewValue());
				}
			}
		};
		setGraphPanel(graphpanel);
	}

	/** called when default tree model needs to bee created */
	protected TreeModel createTreeModel() {
		return new DefaultTreeModel(new DefaultMutableTreeNode());
	}

	/**
	 * free for you to override. triggered before the change appears.
	 */
	protected void onGraphPanelChange(GraphPanel<?, ?> oldValue,
			GraphPanel<?, ?> newValue) {
	}

	/**
	 * free for you to override. triggered before the change appears.
	 */
	protected void onLayerStackChange(AbstractLayerStack<?> oldValue,
			AbstractLayerStack<?> newValue) {
	}

	/**
	 * free for you to override. triggered before the change appears.
	 */
	protected void onLayerViewerAdd(LayerViewer<?, ?> vv) {
	}

	/**
	 * free for you to override. triggered before the change appears.
	 */
	protected void onLayerViewerRemove(LayerViewer<?, ?> vv) {
	}

	/*
	 * properties
	 */

	/** @return the tree in this component, e.g. to access its model. */
	public final JTree getTree() {
		return tree;
	}

	public final String getCaption() {
		return caption;
	}

	public final void setCaption(String value) {
		if (caption == value || (caption != null && caption.equals(value)))
			return;
		firePropertyChange("Caption", caption, value);
		caption = value;
	}

	public GraphPanel<?, ?> getGraphPanel() {
		return graphPanel;
	}

	public void setGraphPanel(GraphPanel<?, ?> value) {
		if (value == graphPanel)
			return;
		if (graphPanel != null) {
			graphPanel.removePropertyChangeListener(graphPanelListener);
			for (LayerViewer<?, ?> vv : graphPanel.getViewers())
				onLayerViewerRemove(vv);
			onLayerStackChange(graphPanel.getLayerStack(), null);
		}
		if (value != null) {
			onLayerStackChange(null, value.getLayerStack());
			for (LayerViewer<?, ?> vv : value.getViewers())
				onLayerViewerAdd(vv);
			value.addPropertyChangeListener(graphPanelListener);
		}
		onGraphPanelChange(graphPanel, value);
		//
		graphPanel = value;
	}

	public void update() {
	}
}
