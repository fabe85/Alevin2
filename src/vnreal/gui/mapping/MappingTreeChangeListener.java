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

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.uci.ics.jung.graph.event.GraphEvent;
import edu.uci.ics.jung.graph.event.GraphEventListener;

/**
 * @author Vlad Singeorzan
 * @author Michael Duelli
 * @since 2010-11-08
 */
@SuppressWarnings("unchecked")
public final class MappingTreeChangeListener implements ChangeListener,
		GraphEventListener {
	private boolean isRunning = false;
	private final MappingPanel owner;

	public MappingTreeChangeListener(MappingPanel owner) {
		this.owner = owner;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// Check if there was a recent update to reduce event load!
		if (!isRunning) {
			isRunning = true;
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					owner.getTree().updateUI();

					if (owner.getParent() instanceof JTabbedPane)
						((JTabbedPane) owner.getParent())
								.setSelectedComponent(owner);

					isRunning = false;
				}
			});
		}
	}

	@Override
	public void handleGraphEvent(GraphEvent arg0) {
		stateChanged(null);
	}
}
