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
package mulavito.gui.components.selectionpanel;

import mulavito.gui.components.GraphPanel;
import mulavito.gui.components.LayerViewer;
import mulavito.gui.components.TreePanel;

/**
 * <p>
 * A GUI element showing information about selected sites and edges. the
 * selection is listened from the given graph panel.
 * </p>
 * 
 * <p>
 * To use this class with your own selection model, you have to implement
 * something like:
 * 
 * <pre>
 * SelectionPanel selPanel = new SelectionPanel(new MySelectionTreeModel());
 * selPanel.getTree().setSelectionModel(new MySelectionTreeSelectionModel());
 * </pre>
 * 
 * where <i>MySelectionTreeModel</i> extends {@link DefaultSelectionTreeModel}.
 * </p>
 * 
 * @author Michael Duelli
 * @author Julian Ott (tree model)
 */
@SuppressWarnings("serial")
public final class SelectionPanel extends TreePanel {
	private SelectionTreeItemListener listener;

	public SelectionPanel(GraphPanel<?, ?> graphpanel) {
		this(new DefaultSelectionTreeModel(), graphpanel);
	}

	public SelectionPanel(DefaultSelectionTreeModel model,
			GraphPanel<?, ?> graphpanel) {
		super(graphpanel);

		getTree().setModel(model);
		model.setOwner(this);

		// Allow multiple roots (vertices and edges)
		getTree().setRootVisible(false);
		// tree.setShowsRootHandles(true);

		listener = new SelectionTreeItemListener(this);

		// Add context menu.
		getTree().addMouseListener(new SelectionPanelMouseAdapter(this));
	}

	@Override
	protected void onLayerViewerAdd(LayerViewer<?, ?> vv) {
		vv.getPickedVertexState().addItemListener(listener);
		vv.getPickedEdgeState().addItemListener(listener);
	}

	@Override
	protected void onLayerViewerRemove(LayerViewer<?, ?> vv) {
		vv.getPickedVertexState().removeItemListener(listener);
		vv.getPickedEdgeState().removeItemListener(listener);
	}

	@Override
	public void update() {
		getTree().updateUI();
	}
}
