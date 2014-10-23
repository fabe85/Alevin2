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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * {@link ItemListener} for updating {@link SelectionPanel}.
 * 
 * @author Julian Ott
 */
public class SelectionTreeItemListener implements ItemListener, ChangeListener {
	private final TreePath vPath;
	private final TreePath ePath;
	private boolean isRunning = false;
	private SelectionPanel owner;

	public SelectionTreeItemListener(SelectionPanel owner) {
		if (owner == null)
			throw new IllegalArgumentException();
		this.owner = owner;

		TreeModel model = owner.getTree().getModel();
		Object root = owner.getTree().getModel().getRoot();

		ePath = new TreePath(new Object[] { root, model.getChild(root, 0) });
		vPath = new TreePath(new Object[] { root, model.getChild(root, 1) });
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		update(false);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		update(e.getStateChange() == ItemEvent.SELECTED);
	}

	/**
	 * @param newitems
	 *            true if additional GUI effort has to be done (especially when
	 *            adding elements), false otherwise
	 */
	private void update(final boolean newitems) {
		// Check if there was a recent update to reduce event load!
		if (!isRunning) {
			isRunning = true;
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					owner.getTree().updateUI();
					JTabbedPane parent = null;
					if (owner.getParent() instanceof JTabbedPane)
						parent = (JTabbedPane) owner.getParent();
					if (newitems) {
						owner.getTree().expandPath(ePath); // edges
						owner.getTree().expandPath(vPath); // vertices

						// Show SelectionPanel when a new component is selected.
						if (parent != null)
							parent.setSelectedComponent(owner);
					}

					int es = owner.getTree().getModel()
							.getChildCount(ePath.getLastPathComponent());
					int vs = owner.getTree().getModel()
							.getChildCount(vPath.getLastPathComponent());

					// title
					owner.setCaption("Selection"
							+ (es + vs > 0 ? " (" + es + "/" + vs + ")" : ""));
					isRunning = false;
				}
			});
		}
	}
}
