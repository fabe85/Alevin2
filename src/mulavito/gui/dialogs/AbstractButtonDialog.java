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
package mulavito.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * An abstract template for dialogs with two buttons: 1. to cancel and 2. to do
 * some action.
 * 
 * Action and content is defined by the derived child class.
 * 
 * @author Michael Duelli
 */
@SuppressWarnings("serial")
public abstract class AbstractButtonDialog extends JDialog {
	protected JButton actionBtn;

	/**
	 * @param owner
	 *            The parental window. Sets icon.
	 * @param title
	 *            The title
	 * @param action
	 *            The label of the action button
	 * @param dim
	 *            The size of the dialog.
	 */
	protected AbstractButtonDialog(Window owner, String title, String action,
			Dimension dim) {
		super(owner);

		setTitle(title);
		setLayout(new BorderLayout());
		setSize(dim);
		setModalityType(DEFAULT_MODALITY_TYPE);
		setResizable(false);

		setLocationRelativeTo(getOwner());
		// Button panel.
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		// Title and action to be defined in child class.
		actionBtn = new JButton(action);
		actionBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doAction();

				setVisible(false);
				dispose();
			}
		});
		btnPanel.add(actionBtn);

		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClose();

				setVisible(false);
				dispose();
			}
		});
		btnPanel.add(cancelBtn);

		// Create content in child class.
		add(createContent(), BorderLayout.CENTER);
		add(btnPanel, BorderLayout.SOUTH);
	}

	/**
	 * Define further content in this dialog.
	 * 
	 * @return The content besides the buttons.
	 */
	protected abstract JPanel createContent();

	/**
	 * Define the action that is associated with the action button in this
	 * dialog.
	 */
	protected abstract void doAction();

	protected void onClose() {
		// does nothing here, but could in derived classes.
	}
}
