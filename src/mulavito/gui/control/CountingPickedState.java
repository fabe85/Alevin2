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

import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.bag.HashBag;

import edu.uci.ics.jung.visualization.picking.AbstractPickedState;
import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * Uses a {@link HashBag} to allow multiply picking of a graph element.
 * 
 * To completely unpick an object it must be unpicked as often as it was picked.
 * 
 * @author Michael Duelli
 * @since 2009-05-17
 * 
 * @param <T>
 *            The type that is picked, i.e. either vertices or edges.
 */
public final class CountingPickedState<T> extends AbstractPickedState<T>
		implements PickedState<T> {
	/** the 'picked' graph elements */
	private HashBag<T> picked = new HashBag<T>();

	@Override
	public boolean pick(T v, boolean b) {
		boolean prior_state = picked.contains(v);
		if (b) {
			if (prior_state == false)
				fireItemStateChanged(new ItemEvent(this,
						ItemEvent.ITEM_STATE_CHANGED, v, ItemEvent.SELECTED));

			picked.add(v);
		} else {
			if (prior_state == true) {
				picked.remove(v, 1);

				if (!picked.contains(v))
					fireItemStateChanged(new ItemEvent(this,
							ItemEvent.ITEM_STATE_CHANGED, v,
							ItemEvent.DESELECTED));
			}
		}
		return prior_state;
	}

	@Override
	public void clear() {
		List<T> unpicks = new ArrayList<T>(picked);

		for (T v : unpicks)
			pick(v, false);

		if (!picked.isEmpty())
			throw new AssertionError("BUG");
	}

	@Override
	public Set<T> getPicked() {
		return picked.uniqueSet(); // unmodifiable due to uniqueSet
	}

	@Override
	public boolean isPicked(T v) {
		return picked.contains(v);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T[] getSelectedObjects() {
		if (picked.isEmpty())
			return null; // demanded by interface
		else
			return (T[]) picked.uniqueSet().toArray();
	}
}
