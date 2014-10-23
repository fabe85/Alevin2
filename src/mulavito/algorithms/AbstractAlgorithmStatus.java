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
package mulavito.algorithms;

/**
 * An abstract class to allow the easy access of any numeric status information
 * of an {@link IAlgorithm}.
 * 
 * @author Michael Duelli
 * @since 2010-08-26
 */
public abstract class AbstractAlgorithmStatus {
	/**
	 * A label that must be unique within all stati of an {@link IAlgorithm}. It
	 * is shown in the GUI and used to distinguish stati.
	 */
	private final String label;

	public AbstractAlgorithmStatus(String label) {
		this.label = label;
	}

	public final String getLabel() {
		return label;
	}

	public abstract Number getValue();

	public abstract Number getMaximum();

	public final int getRatio() {
		return (int) Math.round(getValue().doubleValue()
				/ getMaximum().doubleValue() * 100.0);
	}
}
