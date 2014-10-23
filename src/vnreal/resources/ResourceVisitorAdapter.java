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
package vnreal.resources;

/**
 * <p>
 * This class uses the Visitor Design Pattern to enable the access for any class
 * derived from {@link AbstractResource} without the use of casts or instanceof.
 * </p>
 * 
 * <p>
 * Derived classes use the Adapter design pattern.
 * </p>
 * 
 * <p>
 * Extend this class by a "visit" method for each available class derived from
 * {@link AbstractResource}.
 * </p>
 * 
 * <p>
 * Examples:
 * 
 * <pre>
 * public boolean visit(MyNewResource res) {
 * 	return false;
 * }
 * </pre>
 * 
 * </p>
 * 
 * @see http://en.wikipedia.org/wiki/Visitor_pattern
 * @see http://en.wikipedia.org/wiki/Adapter_pattern
 * 
 * @author Michael Duelli
 * @since 2010-09-03
 */
public abstract class ResourceVisitorAdapter {
	public boolean visit(BandwidthResource res) {
		return false;
	}
	
	public boolean visit(BaseStationResource res) {
		return false;
	}

	public boolean visit(CpuResource res) {
		return false;
	}

	public boolean visit(IdResource res) {
		return false;
	}
	
	public boolean visit(SelectionResource res) {
		return false;
	}

	public boolean visit(PowerResource res) {
		return false;
	}

	public boolean visit(MultiCoreEnergyResource res) {
		return false;
	}

	public boolean visit(StaticEnergyResource res) {
		return false;
	}

	public boolean visit(PhysicalProvidedSecurity res) {
		return false;
	}

	public boolean visit(PhysicalDemandedSecurity res) {
		return false;
	}
	
	public boolean visit(MLSResource res) {
		return false;
	}
	
	public boolean visit(NullResource res) {
		return false;
	}
	
	public boolean visit(CostResource res) {
	    return false;
	}

}
