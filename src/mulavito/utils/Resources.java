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
package mulavito.utils;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashSet;
import java.util.Locale;

import javax.swing.ImageIcon;

/**
 * A wrapper to access image icons.
 * 
 * @author Julian Ott
 * @since 2010-08-23
 */
public final class Resources {
	private static final String iconBase = "/img/icons/16x16";
	private static HashSet<Class<?>> clazzes;

	/**
	 * adds the classloader of the specified class to the search paths for
	 * resources
	 * 
	 * @return false=invalid value given or already in the set
	 */
	public static boolean addResourceClass(Class<?> value) {
		if (value == null)
			return false;
		if (clazzes == null)
			clazzes = new HashSet<Class<?>>();
		return clazzes.add(value);
	}


	// add own class loader
	static {
		addResourceClass(Resources.class);
	}

	private Resources() {
		// to prevent creation of any object.
	}

	public static ImageIcon getIconByName(String icon) {
		if (icon == null)
			return null;

		try {
			if (!icon.startsWith("/img"))
				icon = iconBase + icon;
			URL url;
			// search all class clazzes
			for (Class<?> clazz : clazzes)
				if ((url = clazz.getResource(icon)) != null)
					return new ImageIcon(url);
			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static final DecimalFormat nf = new DecimalFormat("0.####",
			DecimalFormatSymbols.getInstance(Locale.ENGLISH));
}
