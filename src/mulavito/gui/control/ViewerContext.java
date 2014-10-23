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
package mulavito.gui.control;

import mulavito.graph.IEdge;
import mulavito.graph.ILayer;
import mulavito.graph.IVertex;
import mulavito.gui.components.GraphPanel;
import mulavito.gui.components.LayerViewer;

/**
 * Abstract adapter class for connecting a {@link GraphPanel} with the data
 * layer. It is automatically added to the given owner {@link GraphPanel}.
 * 
 * @author Julian Ott
 * 
 * @param <L>
 *            layer
 * @param <LV>
 *            layer viewer
 */
public abstract class ViewerContext<L extends ILayer<? extends IVertex, ? extends IEdge>, LV extends LayerViewer<? extends IVertex, ? extends IEdge>> {
	private final GraphPanel<L, LV> owner;

	public ViewerContext(GraphPanel<L, LV> owner) {
		if (owner == null)
			throw new IllegalArgumentException();
		this.owner = owner;
		owner.addContext(this);
	}

	public abstract void configVisualizationViewer(LV vv);

	public abstract void deconfigVisualizationViewer(LV vv);

	public abstract void clear();

	public final GraphPanel<L, LV> getOwner() {
		return owner;
	}
}
