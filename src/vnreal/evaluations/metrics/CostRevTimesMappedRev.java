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
package vnreal.evaluations.metrics;

/**
 * Class to obtain the cost-revenue relationship multiplied bye the percentage
 * of mapped revenue. Cost and revenue are defined in their respective classes.
 * 
 * The cost-revenue relationship measures the proportion of cost spent in the
 * substrate network taking into account the revenue that has been mapped.
 * However, when a set of virtual network requests have been included in the
 * mapping process, probably the algorithms are not able to map all of them. In
 * this case, when the percentage of mapped revenue is not to high, the
 * cost-revenue metric per-se is not a good indicator of the network mapping.
 * Thats why this metric is modified, multiplying it by the percentage of mapped
 * revenue.
 * 
 * @author Juan Felipe Botero
 * @since 2011-04-12
 * 
 */

public class CostRevTimesMappedRev extends AbstractEvaluation {
	boolean isPathSplitting;

	public CostRevTimesMappedRev(boolean isPsAlgorithm) {
		this.isPathSplitting = isPsAlgorithm;
	}

	@Override
	public double calculate() {
		CostRevenue costRev = new CostRevenue(isPathSplitting);
		costRev.setStack(stack);
		RatioMappedRevenue mapRev = new RatioMappedRevenue(isPathSplitting);
		mapRev.setStack(stack);
		return (costRev.getValue() * mapRev.getValue());
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Cost-Revenue times Mapped Revenue";
	}

}
