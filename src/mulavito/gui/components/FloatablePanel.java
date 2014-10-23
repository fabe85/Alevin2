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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import mulavito.utils.Resources;

/**
 * @author Michael Duelli
 * @since 2011-04-10
 */
@SuppressWarnings("serial")
public abstract class FloatablePanel extends JPanel {
	private class TabPage {
		final String title;
		final String tip;
		final Icon icon;
		final Component cmp;
		final int index;

		public TabPage(int index) {
			this.index = index;
			title = owner.getTitleAt(index);
			tip = owner.getToolTipTextAt(index);
			icon = owner.getIconAt(index);
			cmp = owner.getComponentAt(index);
		}
	}

	/** frame floating the tab when undocked */
	private class FloatingFrame extends JDialog {
		public FloatingFrame(int index) {
			super(getFrame(), "", false);

			final TabPage page = new TabPage(index);
			this.setTitle(page.title);
			this.setLocationRelativeTo(page.cmp.getParent());
			this.setPreferredSize(new Dimension(500, 300));
			add(page.cmp);

			// bring back tab when closing dialog
			setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(final WindowEvent e) {
					insertTab(page);

					undockBtn.setVisible(true);
					closeBtn.setVisible(true);
					glue.setVisible(true);

					setVisible(false);
					dispose();
				}
			});

			setVisible(true);
			pack();
		}
	}

	private final JToolBar tb;
	private final String title;
	private final FloatingTabbedPane owner;
	private final JButton closeBtn;
	private final JButton undockBtn;
	private final Component glue;

	protected FloatablePanel(String title, FloatingTabbedPane owner) {
		super(new BorderLayout());
		setPreferredSize(new Dimension(300, 100));

		this.title = title;
		this.owner = owner;

		tb = new JToolBar(JToolBar.VERTICAL);
		tb.setFloatable(false);
		add(tb, BorderLayout.EAST);

		// undockBtn removes tab and puts it to floating frame
		undockBtn = new JButton(
				Resources
						.getIconByName("/img/icons/16x16/actions/window-new.png"));
		undockBtn.setToolTipText("undock panel");
		undockBtn.setMargin(new Insets(0, 0, 0, 0));
		undockBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				undock();
			}
		});
		addButton(undockBtn);

		closeBtn = new JButton(
				Resources
						.getIconByName("/img/icons/16x16/status/image-missing.png"));
		closeBtn.setToolTipText("hide/close panel");
		closeBtn.setMargin(new Insets(0, 0, 0, 0));
		closeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		addButton(closeBtn);

		glue = Box.createVerticalGlue();
		tb.add(glue);
	}

	private FloatingTabbedPane getOwner() {
		return owner;
	}

	private void insertTab(TabPage page) {
		int index = Math.min(page.index, owner.getTabCount());
		owner.insertTab(page.title, page.icon, page.cmp, page.tip, index);
		owner.setSelectedIndex(index);
	}

	protected final void addButton(AbstractButton btn) {
		tb.add(btn);
	}

	public final String getTitle() {
		return title;
	}

	private void close() {
		int index = getOwner().indexOfTab(getTitle());

		// add to hidden menu
		final TabPage page = new TabPage(index);
		final JMenuItem item = new JMenuItem(page.title, page.icon);
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				insertTab(page);
				getOwner().getHiddenTabs().remove(item);
				getOwner().getHiddenTabs().setEnabled(
						getOwner().getHiddenTabs().getItemCount() > 0);
			}
		});
		getOwner().getHiddenTabs().add(item);
		getOwner().getHiddenTabs().setEnabled(true);

		getOwner().removeTabAt(index);

		// hide close and undock
		closeBtn.setVisible(true);
		undockBtn.setVisible(true);
		glue.setVisible(true);
	}

	private void undock() {
		new FloatingFrame(getOwner().getSelectedIndex()).setVisible(true);

		// hide close and undock
		closeBtn.setVisible(false);
		undockBtn.setVisible(false);
		glue.setVisible(false);
	}

	/** @return the owning frame */
	private JFrame getFrame() {
		for (Container p = this; p != null; p = p.getParent())
			if (p instanceof JFrame)
				return (JFrame) p;
		return null;
	}
}
