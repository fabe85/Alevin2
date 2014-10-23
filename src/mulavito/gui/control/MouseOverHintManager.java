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
package mulavito.gui.control;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * manages hints for components and general status {@link http
 * ://articles.techrepublic.com.com/5100-10878_11-5185994.html}
 * 
 * @author Julian Ott
 */
public class MouseOverHintManager extends MouseAdapter {
	private Map<Component, String> hintMap;
	private Stack<String> status;
	private JLabel hintLabel;

	public MouseOverHintManager(JLabel hintLabel) {
		hintMap = new WeakHashMap<Component, String>();
		status = new Stack<String>();
		this.hintLabel = hintLabel;
	}

	public void addHintFor(Component comp, String hintText) {
		hintMap.put(comp, hintText);
	}

	public void removeHint(Component comp) {
		hintMap.remove(comp);
		hintLabel.setText(getStatus());
	}

	public void enableHints(Component comp, boolean enabled) {
		if (enabled)
			comp.addMouseListener(this);
		else
			comp.removeMouseListener(this);
		if (comp instanceof Container) {
			Component[] components = ((Container) comp).getComponents();
			for (int i = 0; i < components.length; i++)
				enableHints(components[i], enabled);
		}
		if (comp instanceof MenuElement) {
			MenuElement[] elements = ((MenuElement) comp).getSubElements();
			for (int i = 0; i < elements.length; i++)
				enableHints(elements[i].getComponent(), enabled);
		}
	}

	private String getHintFor(Component comp) {
		String hint = hintMap.get(comp);
		if (hint == null) {
			if (comp instanceof JLabel)
				hint = hintMap.get(((JLabel) comp).getLabelFor());
			else if (comp instanceof JTableHeader)
				hint = hintMap.get(((JTableHeader) comp).getTable());
		}
		return hint;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		Component comp = (Component) e.getSource();
		String hint;
		do {
			hint = getHintFor(comp);
			comp = comp.getParent();
		} while ((hint == null) && (comp != null));
		if (hint != null)
			hintLabel.setText(hint);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		hintLabel.setText(getStatus());
	}

	public String getStatus() {
		if (status.isEmpty())
			return "";
		return status.peek();
	}

	public void enterStatus(String value) {
		if (value == null)
			throw new IllegalArgumentException("null not permitted, try \"\"");
		status.push(value);
		hintLabel.setText(getStatus());
	}

	public void exitStatus() {
		if (!status.empty())
			status.pop();
		hintLabel.setText(getStatus());
	}
}
