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
package mulavito.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.lang.reflect.Method;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import mulavito.gui.components.ConsolePanel;
import mulavito.gui.components.FloatingTabbedPane;
import mulavito.gui.control.MouseOverHintManager;
import mulavito.utils.Resources;

/**
 * Base class for a paneled GUI.
 * 
 * @author Julian Ott
 * @author Michael Duelli
 * @since 2010-08-24
 */
@SuppressWarnings("serial")
public class Gui extends JFrame {
	static {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		// Prevent renaming within file choosers.
		UIManager.put("FileChooser.readOnly", true);
	}

	private static boolean isApple() {
		return System.getProperty("os.name").toLowerCase().indexOf("mac") != -1;
	}

	/**
	 * sets apple parameters
	 */
	public static void setAppleParameters(String aboutName, ImageIcon dockIcon) {
		if (aboutName == null || dockIcon == null || !isApple())
			return;
		System.setProperty("com.apple.mrj.application.apple.menu.about.name",
				aboutName);
		try {
			// Try to load Apple extension class for Java.
			Class<?> appc = Class.forName("com.apple.eawt.Application");
			Object app = appc.newInstance();

			// Set dock icon image.
			Method m = appc.getMethod("setDockIconImage", java.awt.Image.class);
			m.invoke(app, dockIcon.getImage());
		} catch (Exception e) {
			System.err.println("Oops! Seems like Apple support is buggy.");
		}
	}

	/**
	 * little helper class for default splitting
	 */
	private class MySplit extends JSplitPane {
		public MySplit(int newOrientation, JComponent left, JComponent right,
				double weight) {
			super(newOrientation, left, right);
			setOneTouchExpandable(true);
			setResizeWeight(weight);
		}
	}

	private JPanel toolBarPane, toolBoxPane;
	private JLabel statusBar;
	private MouseOverHintManager hints;
	//
	private FloatingTabbedPane bottomPanel;
	private ConsolePanel consolePanel;

	public Gui(String title) {
		super(title);

		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Set the frame icon.
		ImageIcon icon = Resources.getIconByName("/img/mulavito-logo.png");
		if (icon != null)
			setIconImage(icon.getImage());

		setLayout(new BorderLayout());

		toolBarPane = new JPanel();
		toolBarPane.setLayout(new BoxLayout(toolBarPane, BoxLayout.PAGE_AXIS));
		add(toolBarPane, BorderLayout.NORTH);

		toolBoxPane = new JPanel();
		toolBoxPane.setLayout(new BoxLayout(toolBoxPane, BoxLayout.Y_AXIS));
		add(toolBoxPane, BorderLayout.WEST);

		statusBar = new JLabel();
		statusBar.setPreferredSize(new Dimension(16, 16));
		statusBar.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		add(statusBar, BorderLayout.SOUTH);
		//
		hints = new MouseOverHintManager(statusBar);
		hints.enterStatus("Ready");

		// layout of content with split panes
		JComponent content;
		// vertical group
		JComponent bottom = createBottomPane();
		JComponent center = createCenterPane();
		// if (bottom != null)
		// add(bottom, BorderLayout.SOUTH);
		if (center != null && bottom != null)
			content = new MySplit(JSplitPane.VERTICAL_SPLIT, center, bottom,
					0.9);
		else if (center != null)
			content = center;
		else
			content = bottom;
		// horizontal group
		JComponent right = createRightPane(), left = createLeftPane();
		if (content != null && right != null)
			content = new MySplit(JSplitPane.HORIZONTAL_SPLIT, content, right,
					0.9);
		else if (right != null)
			content = right;
		//
		if (content != null && left != null)
			content = new MySplit(JSplitPane.HORIZONTAL_SPLIT, left, content,
					0.1);
		else if (left != null)
			content = left;
		//
		if (content != null)
			add(content, BorderLayout.CENTER);
	}

	private JComponent createBottomPane() {
		bottomPanel = new FloatingTabbedPane();

		consolePanel = new ConsolePanel(bottomPanel);
		bottomPanel.addTab(consolePanel.getTitle(), null, consolePanel,
				"Shows console output");

		return bottomPanel;
	}

	protected JComponent createRightPane() {
		return null;
	}

	protected JComponent createLeftPane() {
		return null;
	}

	protected JComponent createCenterPane() {
		return null;
	}

	public void debugOutput(String text) {
		consolePanel.addText("DEBUG: " + text, "debug");
	}

	public void normalOutput(String text) {
		consolePanel.addText(text, "regular");
	}

	public void warnOutput(String text) {
		consolePanel.addText(text, "warn");
	}

	public void notifyOutput(String text) {
		consolePanel.addText(text, "notify");
	}

	/**
	 * @return the toolBarPane
	 */
	public JPanel getToolBarPane() {
		return toolBarPane;
	}

	public JPanel getToolBoxPane() {
		return toolBoxPane;
	}

	public FloatingTabbedPane getBottomPane() {
		return bottomPanel;
	}

	public MouseOverHintManager getStatusManager() {
		return hints;
	}
}
