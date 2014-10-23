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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import vnreal.ExchangeParameter;
import vnreal.constraints.AbstractConstraint;
import vnreal.demands.AbstractDemand;
import vnreal.network.Link;
import vnreal.network.NetworkEntity;
import vnreal.network.Node;
import vnreal.resources.AbstractResource;

/**
 * Provides the dialog and the common functionality needed to add / edit a
 * {@link AbstractResource} or {@link AbstractDemand} of a {@link Node} or
 * {@link Link}.
 * 
 * @author Vlad Singeorzan
 * @since 2010-11-08
 */
@SuppressWarnings("serial")
public abstract class AbstractAddEditConstraintDialog extends JDialog implements
		ActionListener {
	/**
	 * The {@link NetworkEntity} object ({@link Link} or {@link Node}) to be
	 * edited.
	 */
	protected NetworkEntity<AbstractConstraint> entity;

	/** The layer of the {@link NetworkEntity} object */
	protected int layer;

	/**
	 * the {@link JComboBox} to select the {@link AbstractResource}s /
	 * {@link AbstractDemand}s to be added / edited from.
	 */
	protected JComboBox comboBox;

	/**
	 * The {@link JPanel} displaying the parameters to be set / edited for the
	 * selected Constraint.
	 */
	protected JPanel paramsPanel;

	/**
	 * A String description of the {@link AbstractConstraint}. Either "Resource"
	 * or "Demand".
	 */
	protected String constraintStr;

	/**
	 * the names and values of the parameters displayed.
	 */
	protected List<String> paramNames;
	protected List<JTextField> paramValues;

	protected AbstractAddEditConstraintDialog(
			NetworkEntity<AbstractConstraint> ne, int lyr, Window owner,
			Dimension dim) {
		super(owner);
		setLayout(new BorderLayout());
		setSize(dim);
		setModalityType(DEFAULT_MODALITY_TYPE);
		setResizable(false);
		setLocationRelativeTo(getOwner());
		entity = ne;
		layer = lyr;

		if (layer == 0) {
			constraintStr = "Resource";
		} else {
			constraintStr = "Demand";
		}
		// set the title after entity and constraintStr have been set
		setTitle(getTitleText());
		// Button panel.
		JPanel btnPanel = new JPanel();
		JButton actionBtn = new JButton(getButtonText());
		actionBtn.addActionListener(this);
		btnPanel.add(actionBtn);

		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.setActionCommand("cancel");
		cancelBtn.addActionListener(this);
		btnPanel.add(cancelBtn);

		// The content panel. It contains the ComboBox and the params panel.
		final JPanel content = new JPanel(new FlowLayout(FlowLayout.CENTER));
		content.add(new JLabel(getComboBoxLabelText()));
		comboBox = new JComboBox(getComboBoxObjects());
		content.add(comboBox);
		paramsPanel = new JPanel(new GridLayout(1, 2, 10, 10));
		content.add(paramsPanel);
		content.updateUI();
		comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// remove all elements from the panel, do the specified action
				// and update
				paramsPanel.removeAll();
				doComboBoxAction();
				paramsPanel.updateUI();
			}
		});
		add(content, BorderLayout.CENTER);
		add(btnPanel, BorderLayout.SOUTH);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("cancel")) {
			setVisible(false);
			dispose();
		} else {
			doAction();
			setVisible(false);
			dispose();
		}
	}

	/**
	 * Used to get the setter method for the specified field name from the
	 * method array.
	 * 
	 * @param methods
	 *            the method array.
	 * @param fieldName
	 *            the name of the field.
	 * @return the setter method for the specified field.
	 */
	protected static Method getSetterIgnoreCase(Method[] methods,
			String fieldName) {
		for (Method m : methods) {
			if (m.isAnnotationPresent(ExchangeParameter.class)
					&& m.getName().equalsIgnoreCase("set" + fieldName)) {
				return m;
			}
		}
		return null;
	}

	/**
	 * Defines the action button text.
	 * 
	 * @return the action button Text.
	 */
	protected abstract String getButtonText();

	/**
	 * Defines the Label to be displayed before the combo box.
	 * 
	 * @return the combo box label.
	 */
	protected abstract String getComboBoxLabelText();

	/**
	 * Defines the {@link String}s to be displayed in the combo box.
	 * 
	 * @return the combo box text.
	 */
	protected abstract Object[] getComboBoxObjects();

	/**
	 * Defines the action for the action button.
	 */
	protected abstract void doAction();

	/**
	 * Defines the action for combo box.
	 */
	protected abstract void doComboBoxAction();

	/** @return the title of the dialog */
	protected abstract String getTitleText();

	protected Object getParamValue(Class<?> type, String value) {
		if (type.equals(Integer.class)) {
			return Integer.parseInt(value);
		} else if (type.equals(Double.class)) {
			return Double.parseDouble(value);
		} else if (type.equals(String.class)) {
			return value;
		} else if (type.equals(Boolean.class)) {
			return Boolean.parseBoolean(value);
		} else {
			System.err.println("ERROR: Invalid parameter type.");
			throw new AssertionError();
		}
	}
}
