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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import mulavito.gui.Gui;
import mulavito.gui.components.AbstractSearchField;

/**
 * @author Michael Duelli
 * @author Julian Ott
 * @since 2011-02-09
 */
@SuppressWarnings("serial")
public final class SearchFieldDemo extends Gui implements ActionListener {
	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SearchFieldDemo main = new SearchFieldDemo();
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

	private final AbstractSearchField search;
	private JList<String> list;

	public SearchFieldDemo() {
		super("MuLaViTo SearchBar Demo");

		search = new AbstractSearchField() {
			@Override
			protected void search(Pattern pat) {
				List<Integer> find = new ArrayList<Integer>();
				for (int i = 0; i < list.getModel().getSize(); i++)
					if (pat.matcher(list.getModel().getElementAt(i).toString())
							.find())
						find.add(i);
				list.setSelectedIndices(toInt(find));
			}

			private int[] toInt(List<Integer> value) {
				int[] ret = new int[value.size()];
				for (int i = 0; i < value.size(); i++)
					ret[i] = value.get(i);
				return ret;
			}
		};

		JToolBar toolbar = new JToolBar();
		JButton btn = new JButton("About");
		btn.setActionCommand("about");
		btn.addActionListener(this);
		toolbar.add(btn);
		toolbar.add(Box.createHorizontalGlue());
		toolbar.add(search);
		getToolBarPane().add(toolbar);
	}

	@Override
	protected JComponent createCenterPane() {
		String[] strings = { "one", "two", "three", "four", "five", "six",
				"seven", "eight", "nine", "ten" };
		return list = new JList<String>(strings);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		if (cmd.equals("about")) {
			String html = "<html><h1>" + getTitle() + "</h1>";
			html += "WWW: http://mulavito.sf.net";
			html += "<h3>Demo Authors</h3>";
			html += "in alphabetical order:";
			html += "<ul>";
			html += "<li>Michael Duelli";
			html += "<li>Julian Ott";
			html += "</ul>";
			html += "</html>";
			JOptionPane.showMessageDialog(this, html);
		}
	}
}
