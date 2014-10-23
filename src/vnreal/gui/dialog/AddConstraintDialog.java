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

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import mulavito.utils.ClassScanner;
import vnreal.ExchangeParameter;
import vnreal.constraints.AbstractConstraint;
import vnreal.demands.AbstractDemand;
import vnreal.network.Link;
import vnreal.network.NetworkEntity;
import vnreal.network.Node;
import vnreal.resources.AbstractResource;

/**
 * Provides the dialog and the functionality needed to add a
 * {@link AbstractResource} or {@link AbstractDemand} of a {@link Node} or
 * {@link Link}.
 * 
 * @author Vlad Singeorzan
 * @author Michael Duelli
 * @since 2010-11-08
 */
@SuppressWarnings("serial")
public class AddConstraintDialog extends AbstractAddEditConstraintDialog {
	@SuppressWarnings("unchecked")
	public AddConstraintDialog(NetworkEntity ne, int lyr, Window owner,
			Dimension dim) {
		super(ne, lyr, owner, dim);
	}

	@Override
	protected void doAction() {
		if (comboBox.getSelectedIndex() > 0) {
			try {
				// create an object according to the combo box selection.
				Object cons = ((ComboBoxClass) comboBox.getSelectedItem())
						.getClasss()
						.getConstructor(entity.getClass().getSuperclass())
						.newInstance(entity);
				// set the parameters specified by the user.
				for (int i = 0; i < paramNames.size(); i++) {
					Method setter = getSetterIgnoreCase(cons.getClass()
							.getDeclaredMethods(), paramNames.get(i));
					Class<?>[] paramType = setter.getParameterTypes();

					setter.invoke(
							cons,
							getParamValue(paramType[0], paramValues.get(i)
									.getText()));
				}
				if (!entity.add((AbstractConstraint) cons)) {
					JOptionPane
							.showMessageDialog(
									this,
									"Cannot add a constraint to this entity because it already has one of this type.",
									"Error", JOptionPane.ERROR_MESSAGE);
				}
			} catch (Exception ex) {
				System.err
						.println("An error occured while trying to add a constraint.");
				ex.printStackTrace();
			}
		}
	}

	@Override
	protected void doComboBoxAction() {
		if (comboBox.getSelectedIndex() > 0) {
			// get the params of the selected resource type.
			paramNames = new ArrayList<String>();
			paramValues = new ArrayList<JTextField>();
			for (final Method m : ((ComboBoxClass) comboBox.getSelectedItem())
					.getClasss().getDeclaredMethods()) {
				// for every setter method get the name of the parameter to set
				// and display it as a label along with a text field for setting
				// the value.
				if (m.isAnnotationPresent(ExchangeParameter.class)
						&& m.getName().startsWith("set")) {
					String name = m.getName().substring(3).toLowerCase();
					paramNames.add(name);
					JLabel paramName = new JLabel(name.replaceFirst("demanded",
							"demanded "));
					paramName.setVisible(true);
					paramsPanel.add(paramName);
					JTextField paramValue = new JTextField(3);
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
		return new String("Add " + constraintStr);
	}

	@Override
	protected Object[] getComboBoxObjects() {
		// determine the needed package.
		String pckgname;
		if (layer == 0) {
			pckgname = "vnreal.resources";
		} else {
			pckgname = "vnreal.demands";
		}

		try {
			// determine the interface that needs to be implemented.
			String intfaceName = new String();
			if (entity instanceof Node) {
				intfaceName = "vnreal.constraints.INodeConstraint";
			} else if (entity instanceof Link) {
				intfaceName = "vnreal.constraints.ILinkConstraint";
			}

			// get the classes and set the corresponding fields as well as
			// the combo box text.
			Class<?>[] classes = ClassScanner.getClassesImplementing(
					this.getClass(), pckgname, Class.forName(intfaceName));
			ComboBoxClass[] cbObj = new ComboBoxClass[classes.length + 1];
			cbObj[0] = new ComboBoxClass(null, "select " + constraintStr
					+ "...");
			for (int i = 0; i < classes.length; i++) {
				cbObj[i + 1] = new ComboBoxClass(classes[i],
						classes[i].getSimpleName());
			}
			return cbObj;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new AssertionError("Class not found.");
		}
	}

	@Override
	protected String getComboBoxLabelText() {
		return new String("Select the " + constraintStr + " to add:");
	}

	@Override
	protected String getTitleText() {
		return new String("Add " + constraintStr + " to " + entity.toString());
	}

	/**
	 * Internal Class used for the Objects displayed in the JComboBox. This
	 * allows for setting a specific description, rather than using the Object's
	 * toString() method.
	 */
	private class ComboBoxClass {
		private Class<?> cls;
		String description;

		public ComboBoxClass(Class<?> c, String desc) {
			cls = c;
			description = desc;
		}

		@Override
		public String toString() {
			return description;
		}

		public Class<?> getClasss() {
			return cls;
		}
	}
}
