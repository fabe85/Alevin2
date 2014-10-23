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
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import vnreal.Scenario;
import vnreal.algorithms.utils.MiscelFunctions;
import vnreal.evaluations.metrics.EvaluationMetric;
import vnreal.gui.GUI;

@SuppressWarnings("serial")
public class MetricsDialog extends JDialog {
	
	public MetricsDialog(EvaluationMetric eval, Scenario scenario) {
		super(GUI.getInstance());
		setTitle(eval.toString());
		setLayout(new BorderLayout());
		setSize(new Dimension(250, 100));

		setLocationRelativeTo(getOwner());

		JPanel content = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel label = new JLabel();
		label.setText("Evaluation result");
		content.add(label);
		final JTextField textField = new JTextField();
		textField.setColumns(6);
		content.add(textField);
		textField.setEditable(false);
		eval.setStack(scenario.getNetworkStack());
		textField.setText(Double.toString(MiscelFunctions
				.roundThreeDecimals(eval.calculate())));

		// Button panel.
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton copyBtn = new JButton("Copy");
		copyBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Toolkit.getDefaultToolkit()
						.getSystemClipboard()
						.setContents(new StringSelection(textField.getText()),
								new ClipboardOwner() {

									@Override
									public void lostOwnership(Clipboard arg0,
											Transferable arg1) {
										// nothing.
									}
								});
			}
		});

		JButton closeBtn = new JButton("Close");
		closeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});

		btnPanel.add(copyBtn);
		btnPanel.add(closeBtn);

		add(content, BorderLayout.CENTER);
		add(btnPanel, BorderLayout.SOUTH);
		setVisible(true);
	}
}
