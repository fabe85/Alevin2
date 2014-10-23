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
package mulavito.samples;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import mulavito.algorithms.AbstractAlgorithmStatus;
import mulavito.algorithms.IAlgorithm;
import mulavito.gui.Gui;
import mulavito.gui.dialogs.ProgressBarDialog;

/**
 * @author Michael Duelli
 * @since 2011-04-18
 */
@SuppressWarnings("serial")
public final class ProgressBarDemo extends Gui implements ActionListener {
	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ProgressBarDemo main = new ProgressBarDemo();
				main.pack();
				main.setVisible(true);
				main.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						System.exit(0);
					}
				});
			}
		});
	}

	public ProgressBarDemo() {
		super("MuLaViTo ProgressBar Demo");

		JToolBar toolbar = new JToolBar();
		JButton btn = new JButton("Run algorithm");
		btn.setActionCommand("algo");
		btn.addActionListener(this);
		toolbar.add(btn);
		getToolBarPane().add(toolbar);

		btn = new JButton("About");
		btn.setActionCommand("about");
		btn.addActionListener(this);
		toolbar.add(btn);

		pack();
		setVisible(true);

		normalOutput("Welcome to MuLaViTo demonstrator.\n");
		debugOutput("Click on \"Run algorithm\".\n");
		notifyOutput("Have fun!!!\n");
	}

	@Override
	protected JComponent createCenterPane() {
		return new JLabel("<html><h1>Press the \"Run algorithm\" button</h1></html>");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		if (cmd.equals("algo")) {
			new MyProgressBarDialog(this, new TestAlgorithm());
		} else if (cmd.equals("about")) {
			String html = "<html><h1>" + getTitle() + "</h1>";
			html += "WWW: http://mulavito.sf.net";
			html += "<h3>Demo Authors</h3>";
			html += "<ul>";
			html += "<li>Michael Duelli";
			html += "</ul>";
			html += "</html>";
			JOptionPane.showMessageDialog(this, html);
		}
	}

	private class MyProgressBarDialog extends ProgressBarDialog {
		public MyProgressBarDialog(Window owner, IAlgorithm algo) {
			super(owner, algo);
		}

		@Override
		protected void onUpdate() {
			debugOutput("\"Show progress\" is active and will output this line on every GUI update.\n");
		}

		@Override
		protected void onBegin(IAlgorithm algo) {
			warnOutput("You started the algorithm. Click the \"Show Progress\" check box.\n");
		}

		@Override
		protected void onDone(IAlgorithm algo) {
			notifyOutput("That's all folks ... so far :-)\n");
		}
	}

	private class TestAlgorithm implements IAlgorithm {
		private int status1 = 0;
		private int status2 = 0;
		private int status3 = 0;

		@Override
		public void performEvaluation() {
			try {
				while (status1 < 100 || status2 < 100 || status3 < 100) {
					if (status1 < 100)
						status1 += 1;
					if (status2 < 100)
						status2 += 2;
					if (status3 < 100)
						status3 += 5;

					Thread.sleep(100);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		private List<AbstractAlgorithmStatus> stati;

		@Override
		public List<AbstractAlgorithmStatus> getStati() {
			if (stati == null) {
				stati = new LinkedList<AbstractAlgorithmStatus>();

				stati.add(new AbstractAlgorithmStatus("Status 1") {
					@Override
					public Number getValue() {
						return status1;
					}

					@Override
					public Number getMaximum() {
						return 100;
					}
				});
				stati.add(new AbstractAlgorithmStatus("Status 2") {
					@Override
					public Number getValue() {
						return status2;
					}

					@Override
					public Number getMaximum() {
						return 100;
					}
				});
				stati.add(new AbstractAlgorithmStatus("Status 3") {
					@Override
					public Number getValue() {
						return status3;
					}

					@Override
					public Number getMaximum() {
						return 100;
					}
				});
			}

			return stati;
		}
	}
}
