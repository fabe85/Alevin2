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
package mulavito.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import mulavito.utils.Resources;

/**
 * Toggle button for fitting into the lower right corner of a {@link GraphPanel}
 * or {@link MyGraphZoomScrollPane} to display and navigate a miniature view of
 * the given graph
 * 
 * @author Julian Ott
 */
@SuppressWarnings("serial")
public class SatelliteViewerCorner extends JToggleButton {
	/**
	 * The miniature view of the graph. N.B.: Using JDialog here as JWindow was
	 * not focusable.
	 */
	private JDialog satellite;
	private MySatelliteVisualizationViewer<?, ?> satelliteViewer;

	private LayerViewer<?, ?> vv;

	public SatelliteViewerCorner() {
		super(Resources.getIconByName("/actions/view-fullscreen.png"));
		setToolTipText("Click here to see a minimized view of the graph");
		// satellite window
		satellite = new JDialog();
		satellite.setUndecorated(true);

		satellite.setSize(new Dimension(105, 105));
		satellite.addWindowFocusListener(new WindowFocusListener() {
			@Override
			public void windowGainedFocus(WindowEvent e) {
				// Used to scale the graph to the viewer
				// only zoom if satellite is open
				autoZoomSatellite();
			}

			@Override
			public void windowLostFocus(WindowEvent e) {
				// if (satellite.isVisible())
				doClick();
			}
		});

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		satellite.setContentPane(contentPane);
		/*
		 * Hide satellite view on move or resize of main window.
		 * 
		 * Also update graph size correctly on resize, so we only have to
		 * autoZoomToFit once.
		 */
		final ComponentAdapter adapter = new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent e) {
				if (isSelected())
					doClick();
			}

			@Override
			public void componentResized(ComponentEvent e) {
				if (isSelected())
					doClick();
			}
		};
		addHierarchyListener(new HierarchyListener() {
			private Component owner;

			@Override
			public void hierarchyChanged(HierarchyEvent e) {
				if (owner != null)
					owner.removeComponentListener(adapter);
				owner = SwingUtilities.getRoot(e.getComponent());
				if (owner != null)
					owner.addComponentListener(adapter);
			}
		});
		addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean change = (e.getStateChange() == ItemEvent.SELECTED);

				if (change) {
					Point loc = getLocationOnScreen();

					satellite.setLocation(loc.x - satellite.getWidth() - 5,
							loc.y - satellite.getHeight() - 5);
				}

				satellite.setVisible(change);
			}
		});
	}

	public SatelliteViewerCorner(LayerViewer<?, ?> vv) {
		this();
		setViewer(vv);
	}

	private void autoZoomSatellite() {
		if (vv != null && satelliteViewer != null && satellite.isVisible())
			LayerViewer.autoZoomViewer(satelliteViewer, vv);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void setViewer(LayerViewer vv) {
		if (this.vv == vv)
			return;
		this.vv = vv;
		Container contentPane = satellite.getContentPane();

		if (satelliteViewer != null)
			contentPane.remove(satelliteViewer);
		if (vv != null) {
			satelliteViewer = new MySatelliteVisualizationViewer(vv,
					new Dimension(100, 100));

			satelliteViewer.getRenderContext().setVertexFillPaintTransformer(
					vv.getRenderContext().getVertexFillPaintTransformer());

			vv.addChangeListener(satelliteViewer);

			contentPane.add(satelliteViewer);
		} else {
			satelliteViewer = null;
		}
		satellite.pack();
		autoZoomSatellite();
	}

	protected JDialog getSatellite() {
		return satellite;
	}
}
