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
package vnreal.algorithms.utils.SubgraphBasicVN;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import vnreal.Scenario;
import vnreal.demands.AbstractDemand;
import vnreal.demands.BandwidthDemand;
import vnreal.demands.CpuDemand;
import vnreal.demands.PowerDemand;
import vnreal.demands.IdDemand;
import vnreal.hiddenhopmapping.HiddenHopEnergyDemand;
import vnreal.network.NetworkEntity;
import vnreal.network.NetworkStack;
import vnreal.network.Node;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;
import vnreal.resources.BandwidthResource;
import vnreal.resources.CpuResource;
import vnreal.resources.PowerResource;
import vnreal.resources.IdResource;
import vnreal.resources.StaticEnergyResource;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * TODO: move these methods somewhere else ... I collect them here until I don't
 * have to merge so much code every week ...
 */
public class Utils {

	private Utils() {
	}

	public static boolean fulfills(
			Collection<? extends AbstractDemand> demands,
			NetworkEntity<? extends AbstractResource> e) {

		if (demands != null) {
			for (AbstractDemand dem : demands) {
				boolean found = false;
				for (AbstractResource res : e) {
					if (res.accepts(dem) && res.fulfills(dem)) {
						found = true;
						break;
					}
				}
				if (!found) {
					return false;
				}
			}
		}

		return true;
	}

	public static boolean fulfills(
			NetworkEntity<? extends AbstractResource> sn,
			NetworkEntity<? extends AbstractDemand> vn) {
		return fulfills(vn.get(), sn);
	}

	public static boolean fulfills(List<SubstrateLink> path, VirtualLink vl) {
		return fulfills(path, vl.get());
	}
	
	//Fulfills taking into account hidden hops
	public static boolean fulfills(List<SubstrateLink> path, VirtualLink vl, SubstrateNode sourceSnode, SubstrateNetwork sNet) {
		return fulfills(path, vl.get(), sourceSnode, vl.getHiddenHopDemands(), sNet);
	}

	public static boolean fulfills(List<SubstrateLink> path,
			Collection<AbstractDemand> dem) {

		for (SubstrateLink sl : path) {
			if (!fulfills(dem, sl)) {
				return false;
			}
		}

		return true;
	}
	
	public static boolean fulfills(List<SubstrateLink> path,
			Collection<AbstractDemand> dem, SubstrateNode sourceSnode, List<AbstractDemand> HhDem, SubstrateNetwork sNet) {

		for (SubstrateLink sl : path) {
			if (!fulfills(dem, sl)) {
				return false;
			}
			if(!(sNet.getSource(sl).equals(sourceSnode)) && !fulfills(HhDem,sNet.getSource(sl))){
				return false;
			}
		}

		return true;
	}
	

	public static Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> occupyResources(
			Collection<AbstractDemand> n1,
			Collection<AbstractResource> n2) {

		Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> result = new LinkedList<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>>();

		for (AbstractDemand dem : n1) {

			boolean found = false;
			for (AbstractResource res : n2) {
			
				if (res.accepts(dem) && res.fulfills(dem) && dem.occupy(res)) {
					result
					.add(new ResourceDemandEntry<AbstractResource, AbstractDemand>(
							res, dem));
					found = true;
					break;
				}
			}
			assert (found);
		}
		
		return result;
	}
	
	//Hidden hops demands occupation
	public static Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> occupyResources(
			VirtualLink n1,
			SubstrateNode n2) {
		
		Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> resources =
				new LinkedList<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>>();

		for (AbstractDemand dem : n1.getHiddenHopDemands()) {

			boolean found = false;
			for (AbstractResource res : n2) {
			
				if (res.accepts(dem) && res.fulfills(dem) && dem.occupy(res)) {
					found = true;
					resources.add(new ResourceDemandEntry<AbstractResource, AbstractDemand>(res, dem));
					break;
				}
			}
			assert (found);
		}
		
		return resources;
	}

	public static Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> occupyPathResources(
			VirtualLink vl,
			List<SubstrateLink> path, SubstrateNetwork sNetwork, boolean useEnergyDemand) {

		Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> resources =
			new LinkedList<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>>();

		int i = 1;
		for (SubstrateLink e : path) {
			resources.addAll(occupyResources(vl.get(), e.get()));

			if (i != path.size()) {
				Pair<SubstrateNode> endpoints = sNetwork.getEndpoints(e);
				Collection<ResourceDemandEntry<AbstractResource, AbstractDemand>> entry =
					occupyHiddenHop(endpoints.getSecond(), vl.getHiddenHopDemands(), useEnergyDemand);
				resources.addAll(entry);
			}
			++i;
		}
		
		return resources;
	}
	
//	public static Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> occupyBackupPathResources(
//			VirtualLink vl,
//			List<SubstrateLink> path, SubstrateNetwork sNetwork, boolean useEnergyDemand) {
//
//		Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> resources =
//			new LinkedList<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>>();
//
//		Collection<AbstractDemand> rDems = new LinkedList<AbstractDemand>(vl.get());
//		rDems.add(new ResilienceDemand(vl));
//		
//		int i = 1;
//		for (SubstrateLink e : path) {
//			resources.addAll(occupyResources(rDems, e.get()));
//
//			if (i != path.size()) {
//				Pair<SubstrateNode> endpoints = sNetwork.getEndpoints(e);
//				Collection<ResourceDemandEntry<AbstractResource, AbstractDemand>> entry =
//					occupyHiddenHop(endpoints.getSecond(), vl.getHiddenHopDemands(), useEnergyDemand);
//				resources.addAll(entry);
//			}
//			++i;
//		}
//		
//		return resources;
//	}
	
	//Including hidden hops cpu demand
	public static Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> occupyPathResources(
			VirtualLink vl, List<SubstrateLink> path, SubstrateNetwork sNetwork, SubstrateNode sourceSnode, boolean useEnergyDemand) {

		Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> resources =
			new LinkedList<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>>();

		int i = 1;
		for (SubstrateLink e : path) {
			resources.addAll(occupyResources(vl.get(), e.get()));
			
			if(!(sNetwork.getSource(e).equals(sourceSnode))) {
				Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> resources2 =
						occupyResources(vl, sNetwork.getSource(e));
				if (resources2 == null) {
					throw new AssertionError();
				}
				resources.addAll(resources2);
			}

			if (i != path.size()) {
				Pair<SubstrateNode> endpoints = sNetwork.getEndpoints(e);
				Collection<ResourceDemandEntry<AbstractResource, AbstractDemand>> entry =
					occupyHiddenHop(endpoints.getSecond(), vl.getHiddenHopDemands(), useEnergyDemand);
				resources.addAll(entry);
			}
			++i;
		}
		
		return resources;
	}

	public static void freeResource(
			ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand> m) {
		m.dem.free(m.res);
	}

	public static void freeResources(
			Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> mappings) {

		for (ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand> m : mappings) {
			freeResource(m);
		}
	}

	private static Collection<ResourceDemandEntry<AbstractResource, AbstractDemand>> occupyHiddenHop(
			SubstrateNode s, List<AbstractDemand> hiddenHopCPUDemands, boolean useEnergyResource) {
		
		LinkedList<ResourceDemandEntry<AbstractResource, AbstractDemand>> result =
			new LinkedList<ResourceDemandEntry<AbstractResource, AbstractDemand>>();
		
		if (useEnergyResource) {
			PowerResource er = Utils.getEnergyResource(s);
			HiddenHopEnergyDemand demand = new HiddenHopEnergyDemand(s);
			demand.occupy(er);
			
			result.add(new ResourceDemandEntry<AbstractResource, AbstractDemand>(er, demand));
		}
		
		for (AbstractDemand hd : hiddenHopCPUDemands) {
			for (AbstractResource r : s.get()) {
				if (r.accepts(hd) && r.fulfills(hd) && hd.occupy(r)) {
					result.add(new ResourceDemandEntry<AbstractResource, AbstractDemand>(r, hd));
					continue;
				}
			}
			
			throw new AssertionError();
		}
		
		return result;
	}

	public static PowerResource getEnergyResource(SubstrateNode sn) {
		for (AbstractResource r : sn.get()) {
			if (r instanceof PowerResource) {
				return (PowerResource) r;
			}
		}

		System.out.println("no energy resource found!");
		return null;
	}

	public static boolean isSolelyForwardingHop(SubstrateNode sn) {
		boolean isUsed = false;
		
		for (AbstractResource r : sn.get()) {
			if (r instanceof PowerResource) {
				for (vnreal.mapping.Mapping m : r.getMappings()) {
					if (!(m.getDemand() instanceof HiddenHopEnergyDemand)) {
						return false;
					}
				}
				if (((PowerResource) r).isUsed()) {
					isUsed = true;
				}
			} else {
				if (!r.getMappings().isEmpty()) {
					return false;
				}
			}
		}

		return isUsed;
	}

	/**
	 * map nodes which have an ID demand; add those nodes to v_a
	 * 
	 * @param vNetwork
	 * @param sNetwork
	 * @param result
	 * @param v_a
	 */
	public static List<SubstrateNode> mapIdDemands(VirtualNetwork vNetwork,
			SubstrateNetwork sNetwork, vnreal.algorithms.utils.SubgraphBasicVN.Mapping m) {
		List<SubstrateNode> result = new LinkedList<SubstrateNode>();

		vnodes: for (VirtualNode vn : vNetwork.getVertices()) {
			Collection<ResourceDemandEntry<AbstractResource, AbstractDemand>> demandMappings = new LinkedList<ResourceDemandEntry<AbstractResource, AbstractDemand>>();
			for (AbstractDemand d : vn.get()) {
				if (d instanceof IdDemand) {

					for (SubstrateNode sn : sNetwork.getVertices()) {
						for (AbstractResource r : sn.get()) {
							if (r instanceof IdResource) {
								IdDemand idDemand = (IdDemand) d;
								IdResource idResource = (IdResource) r;
								if (idDemand.getDemandedId() == idResource
										.getId()) {
									if (Utils.fulfills(sn, vn)) {
										m.add(vn, sn);
										idDemand.occupy(idResource);
										demandMappings
												.add(new ResourceDemandEntry<AbstractResource, AbstractDemand>(
														idResource, idDemand));
										result.add(sn);
										continue vnodes;
									}
								}
							}
						}
					}

					for (ResourceDemandEntry<AbstractResource, AbstractDemand> dm : demandMappings) {
						dm.dem.free(dm.res);
					}

					// no valid id mapping possible
					result.clear();
					return null;
				}
			}
		}

		return result;
	}

	public static int getStressLevel(NetworkEntity<? extends AbstractResource> e) {
		List<NetworkEntity<?>> vEntities = new LinkedList<NetworkEntity<?>>();
		
		for (AbstractResource r : e.get()) {
			for (vnreal.mapping.Mapping m : r.getMappings()) {
				AbstractDemand d = m.getDemand();

				if (!(d instanceof HiddenHopEnergyDemand)) {

					NetworkEntity<?> owner = d.getOwner();

					if (!vEntities.contains(owner)) {
						vEntities.add(owner);
					}

				}
			}
		}
		
		return vEntities.size();
	}
	
	public static double getCpuAvailable(
			NetworkEntity<? extends AbstractResource> n1) {
		for (AbstractResource res : n1.get()) {
			if (res instanceof CpuResource) {
				return ((CpuResource) res).getAvailableCycles();
			}
		}
		return 0.0;
	}
	
	public static double getBandwidthAvailable(
			NetworkEntity<? extends AbstractResource> n1) {
		for (AbstractResource res : n1.get()) {
			if (res instanceof BandwidthResource) {
				return ((BandwidthResource) res).getAvailableBandwidth();
			}
		}
		return 0.0;
	}
	
	
	public static double getBandwidthDemand(NetworkEntity<? extends AbstractDemand> n) {
		for (AbstractDemand dem : n.get()) {
			if (dem instanceof BandwidthDemand) {
				return ((BandwidthDemand) dem).getDemandedBandwidth();
			}
		}
		return 0.0;
	}

	public static double getCpuRemaining(
			NetworkEntity<? extends AbstractResource> n1,
			NetworkEntity<? extends AbstractDemand> n2) {
		for (AbstractResource res : n1.get()) {
			if (res instanceof CpuResource) {
				for (AbstractDemand dem : n2.get()) {
					if (dem instanceof CpuDemand) {
						return ((CpuResource) res).getAvailableCycles()
								- (((CpuDemand) dem)).getDemandedCycles();
					}
				}
			}
		}
		return 0.0;
	}

	public static CpuResource getCpuResource(
			NetworkEntity<? extends AbstractResource> n) {
		for (AbstractResource res : n.get()) {
			if (res instanceof CpuResource) {
				return (CpuResource) res;
			}
		}
		return null;
	}

	public static CpuDemand getCpuDemand(NetworkEntity<? extends AbstractDemand> n) {
		for (AbstractDemand dem : n.get()) {
			if (dem instanceof CpuDemand) {
				return (CpuDemand) dem;
			}
		}
		return null;
	}

	public static <T extends NetworkEntity<?>> boolean contains(T n,
			Collection<T> ns) {
		for (NetworkEntity<?> v : ns) {
			if (v.getId() == n.getId()) {
				return true;
			}
		}
		return false;
	}

	public static <T extends NetworkEntity<?>> boolean equals(Collection<T> ts,
			Collection<T> us) {
		if (ts.size() != us.size()) {
			return false;
		}
		for (T t : ts) {
			if (!contains(t, us)) {
				return false;
			}
		}
		return true;
	}

	public static <T extends Node<?>> Collection<T> minus(Collection<T> c1,
			Collection<T> c2) {
		Collection<T> result = new LinkedList<T>();
		for (T n : c1) {
			if (!Utils.contains(n, c2)) {
				result.add(n);
			}
		}
		return result;
	}

	public static Collection<List<SubstrateLink>> findAllPaths(
			SubstrateNetwork sNetwork, SubstrateNode n1, SubstrateNode n2,
			Collection<AbstractDemand> vldemands) {
		Collection<List<SubstrateLink>> result = new LinkedList<List<SubstrateLink>>();
		findAllPaths(sNetwork, n1, n2, vldemands,
				new LinkedList<SubstrateNode>(),
				new LinkedList<SubstrateLink>(), result);

		return result;
	}

	public static void findAllPaths(SubstrateNetwork sNetwork,
			SubstrateNode n1, SubstrateNode n2,
			Collection<AbstractDemand> vldemands,
			Collection<SubstrateNode> visited, List<SubstrateLink> currentPath,
			Collection<List<SubstrateLink>> result) {
		if (n1 == n2) {
			if (fulfills(vldemands, n1)) {
				result.add(currentPath);
			}
			return;
		}
		if (visited.contains(n1)) {
			return;
		}
		visited.add(n1);

		for (SubstrateLink l : sNetwork.getOutEdges(n1)) {
			if (fulfills(vldemands, l)) {
				List<SubstrateLink> newPath = new LinkedList<SubstrateLink>(
						currentPath);
				newPath.add(l);
				findAllPaths(sNetwork, sNetwork.getOpposite(n1, l), n2,
						vldemands, visited, newPath, result);
			}
		}
	}

	public static boolean hasResourceMappings(SubstrateNetwork sNetwork) {
		for (SubstrateNode n : sNetwork.getVertices()) {
			for (AbstractResource r : n.get()) {
				if (!r.getMappings().isEmpty()) {
					return true;
				}
			}
		}
		return false;
	}

	public static void printResourceMappings(SubstrateNetwork sNetwork) {
		for (SubstrateNode n : sNetwork.getVertices()) {
			for (AbstractResource r : n.get()) {
				if (!r.getMappings().isEmpty()) {
					System.out.println(r.getMappings().size()
							+ " mappings for node " + n);
					for (vnreal.mapping.Mapping as : r.getMappings())
						System.out.println("  " + as.getDemand() + "  "
								+ as.getResource());
				}
			}
		}
	}

	public static void generateEnergyDemands(VirtualNetwork vNetwork) {
		for (VirtualNode vn : vNetwork.getVertices()) {
			vn.add(new PowerDemand(vn));
		}
	}
	
	public static void generateHetEnergyConstraints(NetworkStack stack, int min, int max, Scenario scen) {
		
		SubstrateNetwork sNetwork = stack.getSubstrate();
		for (SubstrateNode sn : sNetwork.getVertices()) {
			int consumption = (int) (Math.random() * (max - min) + 1) + min;
			sn.add(new StaticEnergyResource(sn, consumption));
		}
		
		for (int i = 1; scen.getNetworkStack().getLayer(i) != null; i++) {
			VirtualNetwork vNetwork = (VirtualNetwork) scen
					.getNetworkStack().getLayer(i);
			
			for (VirtualNode vn : vNetwork.getVertices()) {
				vn.add(new PowerDemand(vn));
			}
		}
	}
	
}
