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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

import mulavito.algorithms.AbstractAlgorithmStatus;
import mulavito.algorithms.IAlgorithm;
import mulavito.utils.Resources;

/**
 * Show a progress bar and runs an {@link IAlgorithm} within a separate Java
 * {@link Thread} which allows to interrupt a running algorithm and keeps the
 * GUI reactive.
 * 
 * @author Michael Duelli
 * @author Julian Ott
 * @since 2008-12-07
 */
@SuppressWarnings("serial")
public abstract class ProgressBarDialog extends JDialog {
	private final Map<String, JLabel> labelMap;

	private final Map<String, JProgressBar> barMap;

	private JButton cancelBtn;

	public ProgressBarDialog(Window owner, final IAlgorithm algo) {
		super(owner);

		labelMap = new HashMap<String, JLabel>();
		for (AbstractAlgorithmStatus status : algo.getStati())
			labelMap.put(status.getLabel(), new JLabel(status.getLabel()));

		barMap = new HashMap<String, JProgressBar>();
		for (AbstractAlgorithmStatus status : algo.getStati()) {
			JProgressBar bar = new JProgressBar(0, 100);
			bar.setValue(0);
			bar.setStringPainted(true);
			barMap.put(status.getLabel(), bar);
		}

		setTitle("Progress ...");
		// setModalityType(DEFAULT_MODALITY_TYPE);

		// Set dialog size.
		Dimension dim = new Dimension(400, 300);
		setSize(dim);
		setResizable(false);

		setLocationRelativeTo(getOwner());
		// --------------------------------------------------------------
		// Create a GroupLayout.
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		SequentialGroup columns = layout.createSequentialGroup();
		layout.setHorizontalGroup(columns);
		SequentialGroup rows = layout.createSequentialGroup();
		layout.setVerticalGroup(rows);
		// Left column.
		ParallelGroup leftCol = layout
				.createParallelGroup(GroupLayout.Alignment.LEADING);
		columns.addGroup(leftCol);
		// Middle column.
		ParallelGroup midCol = layout
				.createParallelGroup(GroupLayout.Alignment.LEADING);
		columns.addGroup(midCol);
		// Add status rows.
		for (AbstractAlgorithmStatus status : algo.getStati()) {
			ParallelGroup row = layout
					.createParallelGroup(GroupLayout.Alignment.BASELINE);
			rows.addGroup(row);
			//
			row.addComponent(labelMap.get(status.getLabel()));
			leftCol.addComponent(labelMap.get(status.getLabel()));
			row.addComponent(barMap.get(status.getLabel()));
			midCol.addComponent(barMap.get(status.getLabel()));
		}

		// Row with show progress check.
		ParallelGroup row3 = layout
				.createParallelGroup(GroupLayout.Alignment.BASELINE);
		rows.addGroup(row3);
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 0));
		row3.addComponent(btnPanel);
		midCol.addComponent(btnPanel);
		// checkbox
		final JCheckBox routingProgressCheck = new JCheckBox("Show progress");
		btnPanel.add(routingProgressCheck);
		cancelBtn = new JButton("Stop");
		btnPanel.add(cancelBtn);
		JButton closeBtn = new JButton("Close");
		btnPanel.add(closeBtn);

		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		// Instances of javax.swing.SwingWorker are not reusuable, so
		// we create new instances as needed.
		final ProgressTask task = new ProgressTask(algo);
		onBegin(algo);
		task.execute();

		Thread log = new Thread() {
			private final long sleepInterval = 500;

			@Override
			public final void run() {
				while (!task.isDone()) {
					try {
						update();
						Thread.sleep(sleepInterval);
					} catch (InterruptedException e) {
					}
				}

				// Finally update bars when task has finished.
				update();
			}

			private void update() {
				for (AbstractAlgorithmStatus status : algo.getStati())
					barMap.get(status.getLabel()).setValue(status.getRatio());

				if (routingProgressCheck.isSelected())
					onUpdate();
			}
		};
		log.start();

		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				task.cancel(true);
				cancelBtn.setEnabled(false);
			}
		});

		closeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cancelBtn.isEnabled())
					cancelBtn.doClick();

				setVisible(false);
				dispose();
			}
		});

		pack();
		setVisible(true);
	}

	/** Called when the algorithm has finished or was aborted. */
	protected abstract void onDone(IAlgorithm algo);

	/**
	 * Called regularly (together with stati update) if checkbox for showing
	 * routing progress is selected.
	 */
	protected abstract void onUpdate();

	protected abstract void onBegin(IAlgorithm algo);

	/** Main task. Executed in background thread. */
	private class ProgressTask extends SwingWorker<Void, Void> {
		/** The algorithm to run in background. */
		private final IAlgorithm algo;

		private long startTime = -1;
		private long stopTime = -1;

		/**
		 * {@link Thread}s / {@link SwingWorker}s need special treatment of
		 * {@link Exception}s and {@link Error}s which are both derived from
		 * more general class {@link Throwable}.<br>
		 * Otherwise, all throwables will be absorbed!
		 */
		private Throwable throwable;

		protected ProgressTask(final IAlgorithm algo) {
			this.algo = algo;
			this.throwable = null;
		}

		@Override
		public Void doInBackground() {
			try {
				// Put action which should be manually interrupted in here.
				startTime = System.currentTimeMillis();
				algo.performEvaluation();
				stopTime = System.currentTimeMillis();
			} catch (Throwable e) {
				this.throwable = e;
			}

			return null;
		}

		/**
		 * Executed in event dispatching thread
		 */
		@Override
		public void done() {
			cancelBtn.setEnabled(false);

			setCursor(null); // turn off the wait cursor

			if (throwable == null) {
				if (this.isCancelled())
					System.err.println("Algorithm was canceled.\n");
				else
					System.out.println("Algorithm finished normally.\n");
			} else {
				System.err
						.println("Inside of this thread an exception was caused saying:");
				throwable.printStackTrace();
			}

			// Print status.
			System.out.println();
			for (AbstractAlgorithmStatus status : algo.getStati())
				System.out.println(status.getLabel() + ": "
						+ Resources.nf.format(status.getValue()) + "("
						+ status.getRatio() + "%)");

			if (stopTime == -1)
				stopTime = System.currentTimeMillis();

			System.out.println("Ellapsed time: "
					+ Resources.nf.format((stopTime - startTime) / 1000.0)
					+ " s");

			onDone(algo);
		}
	}
}
