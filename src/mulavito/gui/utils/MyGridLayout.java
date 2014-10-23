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
package mulavito.gui.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;

/**
 * Derived from {@link GridLayout}. Takes care of invisible components.
 * 
 * Source partly copied from Java SDK.
 * 
 * @author Julian Ott
 */
@SuppressWarnings("serial")
public class MyGridLayout extends GridLayout {
	public MyGridLayout(int rows, int cols) {
		super(rows, cols);
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		return evalSize(parent, false);
	}

	private Dimension evalSize(Container parent, boolean min) {
		synchronized (parent.getTreeLock()) {
			int w = 0, h = 0;
			int nrows = 1, ncols = 1;
			int row = 0, col = 0;
			// eval max size and rows/cols
			for (Component cmp : parent.getComponents())
				if (cmp.isVisible()) {
					ncols = col + 1;
					if (row >= nrows)
						nrows = row + 1;
					// eval size
					Dimension d = min ? cmp.getMinimumSize() : cmp
							.getPreferredSize();
					if (w < d.width)
						w = d.width;
					if (h < d.height)
						h = d.height;
					// eval matrix
					if (++row >= getRows()) {
						row = 0;
						col++;
					}
				}
			// add insets
			Insets insets = parent.getInsets();
			return new Dimension(insets.left + insets.right + ncols * w
					+ (ncols - 1) * getHgap(), insets.top + insets.bottom
					+ nrows * h + (nrows - 1) * getVgap());
		}
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return evalSize(parent, true);
	}

	@Override
	public void layoutContainer(Container parent) {
		synchronized (parent.getTreeLock()) {
			// eval rows/cols
			int nrows = 0, ncols = 0;
			int row = 0, col = 0;
			for (Component cmp : parent.getComponents())
				if (cmp.isVisible()) {
					// eval matrix
					ncols = col + 1;
					if (row >= nrows)
						nrows = row + 1;
					if (++row >= getRows()) {
						row = 0;
						col++;
					}
				}
			if (nrows == 0 || ncols == 0)
				return;

			Insets insets = parent.getInsets();
			int w = parent.getWidth() - (insets.left + insets.right);
			int h = parent.getHeight() - (insets.top + insets.bottom);
			w = (w - (ncols - 1) * getHgap()) / ncols;
			h = (h - (nrows - 1) * getVgap()) / nrows;
			boolean ltr = parent.getComponentOrientation().isLeftToRight();
			// compute
			int x = ltr ? insets.left : parent.getWidth() - insets.right - w;
			int y = insets.top;
			col = 0;
			for (Component cmp : parent.getComponents()) {
				if (!cmp.isVisible())
					continue;
				cmp.setBounds(x, y, w, h);
				if (++col >= ncols) {
					col = 0;
					x = ltr ? insets.left : parent.getWidth() - insets.right
							- w;
					y += h + getVgap();
				} else if (ltr)
					x += w + getHgap();
				else
					x -= w + getHgap();
			}
		}
	}
}
