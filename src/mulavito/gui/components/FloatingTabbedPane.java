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
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

/**
 * {@link JTabbedPane} with ability to close or undock tabs.
 * 
 * @author Julian Ott
 * @author Michael Duelli
 * @since 2011-02-06
 */
@SuppressWarnings("serial")
public final class FloatingTabbedPane extends JTabbedPane {
	private JMenu hiddenMenu;

	public FloatingTabbedPane() {
		hiddenMenu = new JMenu("Hidden Panels");
		hiddenMenu.setEnabled(false);
	}

	@Override
	public void insertTab(String title, Icon icon, final Component component,
			String tip, int index) {
		super.insertTab(title, icon, component, tip, index);
		setTabComponentAt(index, createTab(title, icon, component));
	}

	/**
	 * Creates a tabpage title component with close button and registers it to
	 * hidden tabs
	 * 
	 * @return the new tab
	 */
	private JPanel createTab(String title, Icon icon, final Component component) {
		JPanel tab = new JPanel(new BorderLayout(0, 0));
		tab.setOpaque(false);

		JLabel lbl = new JLabel(title, icon, SwingConstants.LEFT);
		tab.add(lbl, BorderLayout.WEST);

		return tab;
	}

	/** @return the tabs that have been removed by the close button */
	public JMenu getHiddenTabs() {
		return hiddenMenu;
	}
}
