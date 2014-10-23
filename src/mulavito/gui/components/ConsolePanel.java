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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import mulavito.utils.Resources;

/**
 * A basic Java console replacement.
 * 
 * @author Michael Duelli
 * @author Julian Ott
 * @since 2008-12-03
 */
@SuppressWarnings("serial")
public final class ConsolePanel extends FloatablePanel {
	// output stream for this console
	private class ConsolePanelStream extends OutputStream {
		private String style;
		private PrintStream base;
		public boolean enablebase = true;

		public ConsolePanelStream(String style, PrintStream base) {
			if (style == null)
				throw new IllegalArgumentException("style");
			this.style = style;
			this.base = base;
		}

		@Override
		public void write(int b) throws IOException {
			if (enablebase && base != null)
				base.write(b);
			// very slow, but anyways it's never used
			addText(new String(new byte[] { (byte) (b & 0xff) }), style);
		}

		@Override
		public void write(byte b[], int off, int len) throws IOException {
			if (enablebase && base != null)
				base.write(b, off, len);
			addText(new String(b, off, len), style);
		}
	}

	private final StyledDocument doc;
	private final JTextPane textPane;
	private final JButton clearBtn;
	private ConsolePanelStream out = null;
	private ConsolePanelStream err = null;

	public ConsolePanel(FloatingTabbedPane owner) {
		super("Console", owner);

		textPane = new JTextPane();
		textPane.setEditable(false);

		doc = textPane.getStyledDocument();
		// When setting this, text pane will not scroll with content.
		// textPane.setCaretPosition(0);.

		addStyles();

		JScrollPane scrollPane = new JScrollPane(textPane);

		add(scrollPane, BorderLayout.CENTER);

		clearBtn = new JButton(Resources
				.getIconByName("/img/icons/16x16/actions/edit-clear.png"));
		clearBtn.setToolTipText("Clear internal console");
		clearBtn.setMargin(new Insets(0, 0, 0, 0));
		clearBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					doc.remove(0, doc.getLength());
					clearBtn.setEnabled(false);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		clearBtn.setEnabled(false); // there is no text initially.
		addButton(clearBtn);

		final JToggleButton extBtn = new JToggleButton(Resources
				.getIconByName("/img/icons/16x16/apps/utilities-terminal.png"));
		extBtn.setToolTipText("Output to external console");
		extBtn.setMargin(new Insets(0, 0, 0, 0));
		extBtn.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean selected = (e.getStateChange() == ItemEvent.SELECTED);

				reassignStdOut(!selected);

				// clearing is not possible on external console.
				clearBtn.setEnabled(!selected);
				textPane.setEnabled(!selected);

				if (selected)
					extBtn.setToolTipText("Switch output to internal console");
				else
					extBtn.setToolTipText("Switch output to external console");

			}
		});
		addButton(extBtn);

		// The default behavior is to have output in ConsolePanel, but only
		// after it has shown up on the screen, so we do not miss exceptions on
		// start-up.
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				if (out != null && err != null)
					out.enablebase = err.enablebase = false;
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				if (out != null && err != null)
					out.enablebase = err.enablebase = true;
			}
		});
		reassignStdOut(true);
	}

	private void reassignStdOut(boolean value) {
		try {
			if (value) {
				if (out == null || err == null) {
					out = new ConsolePanelStream("regular", System.out);
					err = new ConsolePanelStream("warn", System.err);
				}
				// cache sysouts
				System.setOut(new PrintStream(out));
				System.setErr(new PrintStream(err));
			} else if (out != null && err != null) {
				System.setErr(err.base);
				System.setOut(out.base);
			}
		} catch (SecurityException e) {
		}
	}

	private void addStyles() {
		Style s;
		Style def = StyleContext.getDefaultStyleContext().getStyle(
				StyleContext.DEFAULT_STYLE);

		Style regular = doc.addStyle("regular", def);
		// StyleConstants.setForeground(regular, Color.BLACK);
		StyleConstants.setFontFamily(regular, "SansSerif");
		// StyleConstants.setFontSize(regular, 10)
		// StyleConstants.setBold(regular, true);
		// StyleConstants.setItalic(regular, true);
		// StyleConstants.setAlignment(regular, StyleConstants.ALIGN_CENTER);

		s = doc.addStyle("debug", regular);
		StyleConstants.setForeground(s, Color.ORANGE);
		StyleConstants.setFontFamily(s, "SansSerif");
		// StyleConstants.setFontSize(s, 10)
		// StyleConstants.setBold(s, true);
		// StyleConstants.setItalic(s, true);
		// StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);

		s = doc.addStyle("warn", regular);
		StyleConstants.setForeground(s, Color.RED);
		StyleConstants.setFontFamily(s, "SansSerif");
		// StyleConstants.setFontSize(s, 10)
		StyleConstants.setBold(s, true);
		// StyleConstants.setItalic(s, true);
		// StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);

		s = doc.addStyle("notify", regular);
		StyleConstants.setForeground(s, Color.GREEN);
		StyleConstants.setFontFamily(s, "SansSerif");
		// StyleConstants.setFontSize(s, 10)
		StyleConstants.setBold(s, true);
		// StyleConstants.setItalic(s, true);
		// StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
	}

	public void addText(String text, String style) {
		try {
			doc.insertString(doc.getLength(), text, doc.getStyle(style));

			// Scroll JScrollPane to bottom as recommended in
			// http://java.sun.com/docs/books/tutorial/uiswing/components/textarea.html
			textPane.setCaretPosition(textPane.getDocument().getLength());

			if (doc.getLength() > 0) {
				clearBtn.setEnabled(true);
			}
		} catch (Exception e) {
			System.err.println("Could not insert text into ConsolePanel.");
		}
	}
}
