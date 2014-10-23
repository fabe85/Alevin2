/* ***** BEGIN LICENSE BLOCK *****
 * Copyright (C) 2010-2011, The VNREAL Project Team.
 * 
 * This work has been funded by the European FP7
 * Network of Excellence "Euro-NF" (grant agreement no. 216366)
 * through the Specific Joint Developments and Experiments Project
 * "Virtual Network Resource Embedding Algorithms" (VNREAL). 
 *
 * The VNREAL Project Team consists of members from:
 * - University of Wuerzburg, Germany
 * - Universitat Politecnica de Catalunya, Spain
 * - University of Passau, Germany
 * See the file AUTHORS for details and contact information.
 * 
 * This file is part of ALEVIN (ALgorithms for Embedding VIrtual Networks).
 *
 * ALEVIN is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License Version 3 or later
 * (the "GPL"), or the GNU Lesser General Public License Version 3 or later
 * (the "LGPL") as published by the Free Software Foundation.
 *
 * ALEVIN is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * or the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and
 * GNU Lesser General Public License along with ALEVIN; see the file
 * COPYING. If not, see <http://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ***** */
package vnreal.algorithms;

import mulavito.algorithms.IAlgorithm;

/**
 * Basis for all algorithms.
 * 
 * Implements design pattern "template method" in method {@link #run()}.
 * 
 * @author Michael Duelli
 * @since 2010-08-24
 */
public abstract class AbstractAlgorithm implements IAlgorithm {

	/**
	 * <p>
	 * Initializes all variables needed by the algorithm.
	 * </p>
	 * 
	 * <p>
	 * This is called before {@link #evaluate()} in {@link #run()}.
	 * </p>
	 * 
	 * @return true on successful initialization, false otherwise.
	 */
	abstract protected boolean preRun();

	/**
	 * <p>
	 * The only public method that is called to perform the routing algorithm.
	 * </p>
	 * 
	 * <p>
	 * <b>N.B.:</b> This method is intentionally marked {@code final} so it
	 * cannot be overwritten!
	 * </p>
	 * 
	 * <p>
	 * It implements the "template method" design pattern.
	 * </p>
	 */
	@Override
	public final void performEvaluation() {

		if (preRun()) {
			evaluate();
			postRun();
		} else
			throw new AssertionError("preRun failed!");
	}

	protected abstract void evaluate();

	/** This is called after {@link #process()} in {@link #run()}. */
	protected abstract void postRun();

}
