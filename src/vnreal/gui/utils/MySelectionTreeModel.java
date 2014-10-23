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
package vnreal.gui.utils;

import mulavito.gui.components.selectionpanel.DefaultSelectionTreeModel;
import vnreal.network.Link;
import vnreal.network.Node;

/**
 * @author Michael Duelli
 * @since 2010-10-18
 */
public final class MySelectionTreeModel extends DefaultSelectionTreeModel {
	private class MyVertexSelectionProxy extends VertexSelectionProxy {
		@Override
		protected String getObjectName() {
			return "Nodes";
		}
	}

	private class MyEdgeSelectionProxy extends EdgeSelectionProxy {
		@Override
		protected String getObjectName() {
			return "Links";
		}
	}

	@Override
	protected IProxy createRoot() {
		return new RootProxy(new MyVertexSelectionProxy(),
				new MyEdgeSelectionProxy());
	};

	@Override
	public Object getChild(Object parent, int index) {
		if (parent instanceof Node) {
			return ((Node<?>) parent).get().get(index);
		} else if (parent instanceof Link) {
			return ((Link<?>) parent).get().get(index);
		}
		return super.getChild(parent, index);
	}

	@Override
	public int getChildCount(Object parent) {
		if (parent instanceof Node) {
			return ((Node<?>) parent).get().size();
		} else if (parent instanceof Link) {
			return ((Link<?>) parent).get().size();
		}
		return super.getChildCount(parent);
	}
}
