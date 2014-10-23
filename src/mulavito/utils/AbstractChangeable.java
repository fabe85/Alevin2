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
package mulavito.utils;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * @author Michael Duelli
 * @since 2009-05-09
 */
public abstract class AbstractChangeable {
	private final EventListenerList listenerList = new EventListenerList();

	public final boolean hasListeners() {
		return listenerList.getListenerCount() > 0;
	}

	public final void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
		fireStateChanged();
	}

	public final void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
		fireStateChanged();
	}

	protected final void fireStateChanged() {
		fireStateChanged(new ChangeEvent(this));
	}

	protected final void fireStateChanged(ChangeEvent event) {
		if (event == null)
			throw new IllegalArgumentException("event is null");
		// Code copied from AbstractButton.

		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ChangeListener.class) {
				// Lazily create the event:
				((ChangeListener) listeners[i + 1]).stateChanged(event);
			}
		}
	}
}
