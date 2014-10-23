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
package mulavito.graph;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import mulavito.utils.AbstractChangeable;

import org.apache.commons.collections15.iterators.UnmodifiableIterator;

/**
 * The stack of multiple layers that is shown in the GUI.
 * 
 * @author Michael Duelli
 * @author Julian Ott
 * 
 * @param <L>
 *            The layers
 */
public abstract class AbstractLayerStack<L extends ILayer<? extends IVertex, ? extends IEdge>>
		extends AbstractChangeable implements Iterable<L> {
	/** A list of all layers in this stack. */
	protected final List<L> layers;

	protected AbstractLayerStack() {
		layers = new LinkedList<L>();
	}

	public boolean isEmpty() {
		return layers.isEmpty();
	}

	@Override
	public final Iterator<L> iterator() {
		return UnmodifiableIterator.decorate(layers.iterator());
	}

	/**
	 * Adds a given graph / layer and trigger GUI update.
	 * 
	 * @param layer
	 *            The given layer.
	 */
	public void addLayer(L layer) {
		// Adds the graph to graph list.
		if (layers.add(layer))
			fireStateChanged(new LayerChangedEvent<L>(this, layer, false));
		else
			throw new AssertionError("graph");
	}
}
