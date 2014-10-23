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
package vnreal.gui.mapping;

import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import mulavito.graph.AbstractLayerStack;
import mulavito.gui.components.GraphPanel;
import mulavito.gui.components.LayerViewer;
import mulavito.gui.components.TreePanel;
import vnreal.Scenario;
import edu.uci.ics.jung.graph.ObservableGraph;

/**
 * @author Vlad Singeorzan
 * @author Michael Duelli
 * @since 2010-10-22
 */
@SuppressWarnings("serial")
public final class MappingPanel extends TreePanel {
	private MappingTreeChangeListener listener;
	private final Scenario scenario;

	public MappingPanel(GraphPanel<?, ?> graphpanel, Scenario scenario) {
		super(graphpanel);

		// createTreeModel requires this.scenario,
		// therefore it cannot be called from super constructor.
		this.scenario = scenario;
		_createTreeModel();

		// Allow multiple roots, the virtual networks.
		getTree().setRootVisible(false);
		getTree().setShowsRootHandles(true);

		listener = new MappingTreeChangeListener(this);

		// Add context menu.
		getTree().addMouseListener(new MappingPanelMouseAdapter());
		setToolTipText("Right click to open context menu");

		// Selection.
		getTree().getSelectionModel().setSelectionMode(
				DefaultTreeSelectionModel.SINGLE_TREE_SELECTION);
		getTree().addTreeSelectionListener(
				new MappingTreeSelectionListener(this));

		// Color highlighting of current selection.
		getTree().setCellRenderer(new MappingTreeCellRenderer());
	}

	//@Override
	protected TreeModel _createTreeModel() {
		return new MappingTreeModel(scenario);
	}

	@Override
	protected void onLayerStackChange(AbstractLayerStack<?> oldValue,
			AbstractLayerStack<?> newValue) {
		if (oldValue != null)
			oldValue.removeChangeListener(listener);
		if (newValue != null)
			newValue.addChangeListener(listener);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onLayerViewerAdd(LayerViewer<?, ?> vv) {
		vv.addChangeListener(listener);
		((ObservableGraph) vv.getLayer()).addGraphEventListener(listener);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onLayerViewerRemove(LayerViewer<?, ?> vv) {
		vv.removeChangeListener(listener);
		((ObservableGraph) vv.getLayer()).removeGraphEventListener(listener);
	}

	@Override
	protected void onGraphPanelChange(GraphPanel<?, ?> oldValue,
			GraphPanel<?, ?> newValue) {
	}

	@Override
	public void update() {
		TreePath[] paths = getTree().getSelectionPaths();
		getTree().clearSelection();
		getTree().setSelectionPaths(paths);
		getTree().updateUI();
	}
}
