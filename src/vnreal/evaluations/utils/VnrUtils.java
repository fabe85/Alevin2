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
package vnreal.evaluations.utils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import vnreal.demands.AbstractDemand;
import vnreal.demands.CpuDemand;
import vnreal.demands.VirtualDemandedSecurity;
import vnreal.demands.VirtualProvidedSecurity;
import vnreal.mapping.Mapping;
import vnreal.network.Network;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;
import vnreal.resources.CpuResource;
import vnreal.resources.PhysicalDemandedSecurity;
import vnreal.resources.PhysicalProvidedSecurity;

public class VnrUtils {
	private VnrUtils() {
		// prevent object creation
	}

	public static Map<VirtualNetwork, Boolean> calculateMappedVnr(
			NetworkStack stack) {
		VirtualNetwork tempVnr;
		boolean isMapped;
		Map<VirtualNetwork, Boolean> isMappedVnr = new LinkedHashMap<VirtualNetwork, Boolean>();
		for (Iterator<Network<?, ?, ?>> net = stack.iterator(); net.hasNext();) {
			Network<?, ?, ?> tmpN = net.next();
			if (tmpN.getLayer() != 0) {
				tempVnr = (VirtualNetwork) tmpN;
				isMapped = true;
				for (VirtualNode tmpVNode : tempVnr.getVertices()) {
					for (AbstractDemand dem : tmpVNode) {
						// if (dem instanceof CpuDemand) {
						if (dem.getMappings().isEmpty()) {
							isMapped = false;
						}
						break;
						// }
					}
				}
				isMappedVnr.put(tempVnr, isMapped);
			}
		}
		return isMappedVnr;
	}

	public static int numberMappedVnr(Map<VirtualNetwork, Boolean> isMappedVnr) {
		int numberMappedVnets = 0;
		VirtualNetwork tempVNet;

		for (Iterator<VirtualNetwork> itt = isMappedVnr.keySet().iterator(); itt
				.hasNext();) {
			tempVNet = itt.next();
			if (isMappedVnr.get(tempVNet))
				numberMappedVnets++;
		}
		return numberMappedVnets;
	}

	public static int getStressLevel(SubstrateNode n) {
		int max = 0;
		int stress;
		for (AbstractResource r : n.get()) {
			stress = 0;
			if (r instanceof CpuResource) {
				for (Mapping f : r.getMappings()) {
					if (f.getDemand().getOwner() instanceof VirtualNode)
						stress++;
				}
			} else {
				stress = r.getMappings().size();
			}
			if (stress > max) {
				max = stress;
			}
		}
		return max;
	}

	/**
	 * Calculates the maximum Spread/Difference of the Security-Levels between
	 * all mapped hosts to a given node. This method is normally called one time
	 * for each substrate-node.
	 * 
	 * @param sn
	 *            The Substrate-Node
	 * @return The maximum difference, or -1 if no mapping occurred
	 */
	public static double getSecuritySpreadDemProv(SubstrateNode n) {
		// Initial values = -1, to enable Security-Levels of '0'
		double minimum = -1;
		double maximum = -1;
		boolean nodesMapped = false;

		// System.out.println("##############" + n.getLabel());

		// Iterate over all mapped resources (CPU, Bandwidth, ...)
		for (AbstractResource r : n.get()) {
			// System.out.println("----" + r.toString());

			// If the resource is a MinimumSecurityResource
			if (r instanceof PhysicalDemandedSecurity) {

				// Iterate over all mappings (all VMs)
				for (Mapping f : r.getMappings()) {

					// If it is a VirtualNode (and not a VirtualLink)
					if (f.getDemand().getOwner() instanceof VirtualNode) {

						// System.out.println("Demand:_" + f.getDemand());
						// System.out.println("Resource:_" + f.getResource());
						nodesMapped = true;

						VirtualProvidedSecurity virtProvSecurity = (VirtualProvidedSecurity) f
								.getDemand();
						Double secDemand = virtProvSecurity
								.getVirtualProvidedSecurityLevel();
						// System.out.println("SecDemand:_" + secDemand);

						// Initial change
						if (minimum == -1 && maximum == -1) {
							minimum = secDemand;
							maximum = secDemand;
						}

						// Set maximum/minimum of occurring levels
						if (secDemand >= maximum) {
							maximum = secDemand;
							// System.out.println("A");
						} else if (secDemand <= minimum) {
							minimum = secDemand;
							// System.out.println("B");
						}
					}
				}
			}
		}

		// System.out.println("Result:_" + (maximum - minimum));
		if (!nodesMapped) {
			return -1;
		} else {
			return maximum - minimum;
		}
	}

	/**
	 * Calculates the maximum Spread/Difference of the Security-Levels between
	 * all mapped hosts to a given node. This method is normally called one time
	 * for each substrate-node.
	 * 
	 * @param sn
	 *            The Substrate-Node
	 * @return The maximum difference, or -1 if no mapping occurred
	 */
	public static double getSecuritySpreadProvDem(SubstrateNode n) {
		// Initial values = -1, to enable Security-Levels of '0'
		double minimum = -1;
		double maximum = -1;
		boolean nodesMapped = false;

		// System.out.println("##############" + n.getLabel());

		// Iterate over all mapped resources (CPU, Bandwidth, ...)
		for (AbstractResource r : n.get()) {
			// System.out.println("----" + r.toString());

			// If the resource is a MinimumSecurityResource
			if (r instanceof PhysicalProvidedSecurity) {

				// Iterate over all mappings (all VMs)
				for (Mapping f : r.getMappings()) {

					// If it is a VirtualNode (and not a VirtualLink)
					if (f.getDemand().getOwner() instanceof VirtualNode) {

						// System.out.println("Demand:_" + f.getDemand());
						// System.out.println("Resource:_" + f.getResource());
						nodesMapped = true;

						VirtualDemandedSecurity virtDemSecurity = (VirtualDemandedSecurity) f
								.getDemand();
						Double secDemand = virtDemSecurity
								.getVirtualDemandedSecurityLevel();
						// System.out.println("SecDemand:_" + secDemand);

						// Initial change
						if (minimum == -1 && maximum == -1) {
							minimum = secDemand;
							maximum = secDemand;
						}

						// Set maximum/minimum of occurring levels
						if (secDemand >= maximum) {
							maximum = secDemand;
							// System.out.println("A");
						} else if (secDemand <= minimum) {
							minimum = secDemand;
							// System.out.println("B");
						}
					}
				}
			}
		}

		// System.out.println("Result:_" + (maximum - minimum));
		if (!nodesMapped) {
			return -1;
		} else {
			return maximum - minimum;
		}
	}

	/**
	 * 
	 * @param vNet
	 * @return if the VNR is mapped in the SN
	 */
	public static boolean isMapped(VirtualNetwork vNet) {
		for (VirtualNode tmpVNode : vNet.getVertices())
			for (AbstractDemand dem : tmpVNode)
				if (dem instanceof CpuDemand)
					if (dem.getMappings().isEmpty())
						return false;

		return true;
	}

}
