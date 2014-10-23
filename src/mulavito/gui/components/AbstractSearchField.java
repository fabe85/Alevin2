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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import mulavito.utils.Resources;

/**
 * A search bar for finding edges or vertices given by their id or label.
 * 
 * @author Julian Ott
 * @author Michael Duelli
 */
@SuppressWarnings("serial")
public abstract class AbstractSearchField extends JTextField {
	/**
	 * A key listener that triggers the search for class {@link QuickSearchBar}.
	 * 
	 * @author Julian Ott
	 * @author Michael Duelli
	 */
	private class SearchFieldListener extends KeyAdapter {
		private Pattern pat;

		@Override
		public void keyPressed(KeyEvent ev) {
			if (ev.getKeyCode() != KeyEvent.VK_ENTER || "".equals(getText()))
				return;
			JRootPane root = SwingUtilities.getRootPane(getParent());
			if (root != null)
				root.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			try {
				// Create regex pattern
				pat = Pattern.compile(getText(), Pattern.CASE_INSENSITIVE);
				search(pat);
			} catch (PatternSyntaxException ex) {
				System.err.println(ex.getMessage());
			}

			if (root != null)
				root.setCursor(null); // turn off wait cursor
			// // Check if there was a recent update to reduce event load!
			// if (!isRunning) {
			// isRunning = true;
			// javax.swing.SwingUtilities.invokeLater(new Runnable() {
			// public void run() {
		}

	}

	private Image searchIcon;
	private Image clearIcon;

	public AbstractSearchField() {
		// super(20);
		searchIcon = Resources.getIconByName("/actions/system-search.png")
				.getImage();
		clearIcon = Resources.getIconByName("/actions/edit-clear.png")
				.getImage();
		// adjust settings
		this.setPreferredSize(new Dimension(180, 20));
		setMaximumSize(new Dimension(180, 20));
		// mac won't eat margins anyway
		if (System.getProperty("os.name").toLowerCase().indexOf("mac") == -1)
			setMargin(new Insets(0, 2, 0, 20));

		// Add listeners.
		addKeyListener(new SearchFieldListener());

		setToolTipText("<html>Search for elements<br>"
				+ "<strong>Hint:</strong> Use regular expressions!</html>");
	}

	private Composite alphaComposite = AlphaComposite.getInstance(
			AlphaComposite.SRC_OVER, 0.50f);

	// draw additions
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		Composite originalComposite = g2d.getComposite();

		boolean cond = this.getText().equals("")
				&& (!this.isFocusOwner() || !this.isEditable());

		if (!isEditable())
			g2d.setComposite(alphaComposite);

		// hack: assuming insets on all sites
		// are the same, because on mac margins are
		// not accepted...
		int mg = getInsets().top;
		// draw icons
		if (cond)
			g.drawImage(searchIcon, this.getWidth() - 18 - mg, 1 + mg, 16, 16,
					null);
		else
			g.drawImage(clearIcon, this.getWidth() - 18 - mg, 1 + mg, 16, 16,
					null);

		if (!isEditable())
			g2d.setComposite(originalComposite);

		// draw instruction
		if (cond) {
			g.setColor(Color.GRAY);
			g.drawString("Search for sites / edges...", 4 + mg, 14 + mg);
		}
	}

	// repaint on focus change
	@Override
	protected void processFocusEvent(FocusEvent e) {
		super.processFocusEvent(e);
		this.repaint();
	}

	// handle clicking clear-icon
	@Override
	protected void processMouseEvent(MouseEvent e) {
		if (isEditable()) {
			if (e.getID() == MouseEvent.MOUSE_ENTERED)
				this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			else if (e.getID() == MouseEvent.MOUSE_CLICKED
					&& e.getX() > this.getWidth() - 20 - getInsets().top) {
				this.setText("");
				return;
			}
		}
		super.processMouseEvent(e);
	}

	/**
	 * override this to search
	 * 
	 * @param pat
	 *            regex pattern
	 */
	protected abstract void search(Pattern pat);
}
