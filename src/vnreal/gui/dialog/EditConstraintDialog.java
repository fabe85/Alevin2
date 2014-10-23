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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTextField;

import vnreal.ExchangeParameter;
import vnreal.constraints.AbstractConstraint;
import vnreal.demands.AbstractDemand;
import vnreal.network.Link;
import vnreal.network.NetworkEntity;
import vnreal.network.Node;
import vnreal.resources.AbstractResource;

/**
 * Provides the dialog and the functionality needed to edit a
 * {@link AbstractResource} or {@link AbstractDemand} of a {@link Node} or
 * {@link Link}.
 * 
 * @author Vlad Singeorzan
 * @since 2010-11-08
 */
@SuppressWarnings("serial")
public class EditConstraintDialog extends AbstractAddEditConstraintDialog {
	@SuppressWarnings("unchecked")
	public EditConstraintDialog(NetworkEntity ne, int lyr, Window owner,
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

				// edit the parameters, as specified by the users.
				for (int i = 0; i < paramNames.size(); i++) {
					Method setter = getSetterIgnoreCase(cons.getClass()
							.getDeclaredMethods(), paramNames.get(i));
					Class<?>[] paramType = setter.getParameterTypes();
					setter.invoke(
							cons,
							getParamValue(paramType[0], paramValues.get(i)
									.getText()));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new AssertionError(
						"An error occured while trying to add a constraint.");
			}
		}
	}

	@Override
	protected void doComboBoxAction() {
		if (comboBox.getSelectedIndex() > 0) {
			// get params of the selected resource type
			paramNames = new ArrayList<String>();
			paramValues = new ArrayList<JTextField>();
			AbstractConstraint selectedCons = ((ComboBoxConstraint) comboBox
					.getSelectedItem()).getConstraint();
			for (final Method m : selectedCons.getClass().getDeclaredMethods()) {
				if (m.isAnnotationPresent(ExchangeParameter.class)
						&& m.getName().startsWith("set")) {
					// For every setter method get the name of the parameter to
					// set and display it as a label along with a text field for
					// setting the value.
					String name = m.getName().substring(3).toLowerCase();
					paramNames.add(name);
					JLabel paramName = new JLabel(name.replaceFirst("demanded",
							"demanded "));
					paramName.setVisible(true);
					paramsPanel.add(paramName);
					JTextField paramValue = new JTextField(3);
					try {
						paramValue
								.setText(selectedCons
										.getClass()
										.getMethod(
												"get"
														+ m.getName()
																.substring(3),
												new Class<?>[0])
										.invoke(selectedCons, new Object[0])
										.toString());
					} catch (Exception e) {
						e.printStackTrace();
						throw new AssertionError(
								"Error occurred while trying to access a get Metdod of the Object "
										+ entity);
					}
					paramValue.setEditable(true);
					paramValue.setVisible(true);
					paramValues.add(paramValue);
					paramsPanel.add(paramValue);
					paramValue.requestFocus();
				}
			}
		}
	}

	@Override
	protected String getButtonText() {
		return new String("Edit " + constraintStr);
	}

	@Override
	protected Object[] getComboBoxObjects() {
		// get the constraints of the network entity
		List<AbstractConstraint> cons = entity.get();
		ComboBoxConstraint[] constraints = new ComboBoxConstraint[cons.size() + 1];
		constraints[0] = new ComboBoxConstraint(null, "select " + constraintStr
				+ "...");
		// add them to the string array to be displayed in the combo box as well
		// as their classes to the classes array field.
		for (int i = 0; i < cons.size(); i++) {
			constraints[i + 1] = new ComboBoxConstraint(cons.get(i), cons
					.get(i).getClass().getSimpleName());
		}
		return constraints;
	}

	@Override
	protected String getComboBoxLabelText() {
		return new String("Select the " + constraintStr + " to edit:");
	}

	@Override
	protected String getTitleText() {
		return new String("Edit " + constraintStr + " of " + entity.toString());
	}

	/**
	 * Internal Class used for the Objects displayed in the JComboBox. This
	 * allows for setting a specific description, rather than using the Object's
	 * toString() method.
	 */
	private class ComboBoxConstraint {
		private String description;
		private AbstractConstraint constraint;

		public ComboBoxConstraint(AbstractConstraint cons, String desc) {
			description = desc;
			constraint = cons;
		}

		@Override
		public String toString() {
			return description;
		}

		public AbstractConstraint getConstraint() {
			return constraint;
		}
	}
}
