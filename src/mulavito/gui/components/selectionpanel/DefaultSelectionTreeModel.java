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

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import mulavito.gui.components.LayerViewer;

/**
 * @author Julian Ott
 * @since 2010-10-07
 */
public class DefaultSelectionTreeModel implements TreeModel {
	protected interface IProxy {
		Object getChildAt(int index);

		int getChildCount();
	}

	/** Proxy for {@link LayerViewer#getPickedEdgeState()} */
	private abstract class ObjectSelectionProxy implements IProxy {
		protected abstract Object[] extractSelectedObjects(LayerViewer<?, ?> vv);

		protected abstract String getObjectName();

		@Override
		public Object getChildAt(int index) {
			if (owner == null || owner.getGraphPanel() == null || index < 0)
				return null;
			// search pickedstate of each viewer
			for (LayerViewer<?, ?> vv : owner.getGraphPanel().getViewers()) {
				Object[] sel = extractSelectedObjects(vv);
				if (sel == null)
					continue;
				if (index < sel.length)
					return sel[index];
				index -= sel.length;
			}
			// not found or index out of bounds
			return null;
		}

		@Override
		public int getChildCount() {
			int length = 0;
			if (owner != null && owner.getGraphPanel() != null) {
				// add pickedstate selections of each viewer
				for (LayerViewer<?, ?> vv : owner.getGraphPanel().getViewers()) {
					Object[] sel = extractSelectedObjects(vv);
					length += (sel == null) ? 0 : sel.length;
				}
			}
			return length;
		}

		@Override
		public String toString() {
			return getObjectName() + " (" + getChildCount() + ")";
		}
	}

	public class EdgeSelectionProxy extends ObjectSelectionProxy {
		@Override
		protected String getObjectName() {
			return "Edges";
		}

		@Override
		protected Object[] extractSelectedObjects(LayerViewer<?, ?> vv) {
			return vv.getPickedEdgeState().getSelectedObjects();
		}
	}

	public class VertexSelectionProxy extends ObjectSelectionProxy {
		@Override
		protected String getObjectName() {
			return "Vertices";
		}

		@Override
		protected Object[] extractSelectedObjects(LayerViewer<?, ?> vv) {
			return vv.getPickedVertexState().getSelectedObjects();
		}
	}

	public class RootProxy implements IProxy {
		private IProxy[] selectionModules;

		public RootProxy(IProxy vertexsel, IProxy edgesel) {
			this(new IProxy[] { vertexsel, edgesel });
		}

		public RootProxy(IProxy[] selectionModules) {
			if (selectionModules == null)
				throw new IllegalArgumentException();
			for (IProxy proxy : selectionModules)
				if (proxy == null)
					throw new IllegalArgumentException();
			this.selectionModules = selectionModules;
		}

		@Override
		public int getChildCount() {
			return selectionModules.length;
		}

		@Override
		public Object getChildAt(int index) {
			return selectionModules[index];
		}
	}

	private SelectionPanel owner;
	private IProxy root;

	public DefaultSelectionTreeModel() {
		root = createRoot();
	}

	protected IProxy createRoot() {
		return new RootProxy(new VertexSelectionProxy(),
				new EdgeSelectionProxy());
	}

	public SelectionPanel getOwner() {
		return owner;
	}

	public void setOwner(SelectionPanel owner) {
		this.owner = owner;
	}

	@Override
	public Object getChild(Object parent, int index) {
		if (parent instanceof IProxy)
			return ((IProxy) parent).getChildAt(index);
		else if (parent instanceof TreeNode)
			return ((TreeNode) parent).getChildAt(index);
		else
			return null;
	}

	@Override
	public int getChildCount(Object parent) {
		if (parent instanceof IProxy)
			return ((IProxy) parent).getChildCount();
		else if (parent instanceof TreeNode)
			return ((TreeNode) parent).getChildCount();
		else
			return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if (parent == null || child == null)
			return -1;
		else {
			int num = getChildCount(parent);

			for (int i = 0; i < num; i++)
				if (getChild(parent, i).equals(child))
					return i;

			return -1;
		}
	}

	@Override
	public Object getRoot() {
		return root;
	}

	@Override
	public boolean isLeaf(Object node) {
		return getChildCount(node) == 0;
	}

	// listeners, not used

	@Override
	public void addTreeModelListener(TreeModelListener l) {
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
	}
}
