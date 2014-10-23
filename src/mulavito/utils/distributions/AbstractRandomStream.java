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
package mulavito.utils.distributions;

import java.util.Random;

/**
 * <p>
 * An abstract class for a stream of number of a random variable which follows a
 * certain probability distribution function (PDF).
 * </p>
 * 
 * <p>
 * To that end, the inverse PDF is implemented in the derived classes and
 * <i>inverse transform sampling</i> is used to get a value of this PDF from a
 * random number generator for uniformly distributed random numbers.
 * </p>
 * 
 * @author Michael Duelli
 * 
 * @see http://en.wikipedia.org/wiki/Inverse_transform_sampling
 */
public abstract class AbstractRandomStream {
	/**
	 * Uniform distribution of random numbers used by ALL derived classes!
	 * 
	 * N.B. This is the only place where we should use class {@link Random}
	 * directly
	 */
	private static final Random unif = new Random(0l); // default seed!

	/**
	 * Call this to explicitly reset seed for all random stream.
	 * 
	 * @param seed
	 *            the new seed value.
	 */
	public final static void setSeed(long seed) {
		unif.setSeed(seed);
	}

	/**
	 * @param A_t
	 *            The value of A(t)
	 * @return the inverse PDF at point A(t)
	 */
	protected abstract double inversePDF(double A_t);

	/** @return the next random number */
	public final double nextDouble() {
		return inversePDF(unif.nextDouble() /* uniformly within [0.0; 1.0) */);
	}
}
