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
package vnreal.gui.dialog;

import java.awt.Dimension;
import java.awt.Window;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import vnreal.constraints.AbstractConstraint;
import vnreal.demands.AbstractDemand;
import vnreal.network.Link;
import vnreal.network.NetworkEntity;
import vnreal.network.Node;
import vnreal.resources.AbstractResource;

/**
 * Provides the dialog and the functionality needed to remove a
 * {@link AbstractResource} or {@link AbstractDemand} from a {@link Node} or
 * {@link Link}.
 * 
 * @author Vlad Singeorzan
 * @since 2010-11-15
 */
@SuppressWarnings("serial")
public class RemoveConstraintDialog extends EditConstraintDialog {
	public RemoveConstraintDialog(NetworkEntity<AbstractConstraint> ne, int lyr, Window owner,
			Dimension dim) {
		super(ne, lyr, owner, dim);
	}

	@Override
	protected void doAction() {
		if (comboBox.getSelectedIndex() > 0) {
			try {
				// get the selected constraint.
				AbstractConstraint cons = entity.get().get(
						comboBox.getSelectedIndex() - 1);
				if (!entity.remove(cons)) {
					JOptionPane.showMessageDialog(this,
							"Cannot remove the last constraint of an entity.",
							"Error", JOptionPane.ERROR_MESSAGE);
					throw new AssertionError("Removing constraint failed.");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new AssertionError(
						"An error occured while trying to remove a constraint.");
			}
		}
	}

	@Override
	protected String getButtonText() {
		return new String("Remove " + constraintStr);
	}

	@Override
	protected String getComboBoxLabelText() {
		return new String("Select the " + constraintStr + " to edit:");
	}

	@Override
	protected String getTitleText() {
		return new String("Remove " + constraintStr + " from "
				+ entity.toString());
	}

	/**
	 * same as in {@link EditConstraintDialog}, just that the JTextField is set
	 * not editable.
	 */
	@Override
	protected void doComboBoxAction() {
		super.doComboBoxAction();
		for (JTextField tf : paramValues) {
			tf.setEditable(false);
		}
	}
}
