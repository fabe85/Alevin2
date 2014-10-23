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
package vnreal.algorithms.isomorphism;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import vnreal.algorithms.SingleNetworkMappingAlgorithm;
import vnreal.algorithms.utils.SubgraphBasicVN.Mapping;
import vnreal.algorithms.utils.SubgraphBasicVN.MyDijkstraShortestPath;
import vnreal.algorithms.utils.SubgraphBasicVN.ResourceDemandEntry;
import vnreal.algorithms.utils.SubgraphBasicVN.Utils;
import vnreal.algorithms.utils.energy.linkStressTransformers.SubstrateLinkDemandTransformer;
import vnreal.demands.AbstractDemand;
import vnreal.network.NetworkEntity;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * See
 * "A Virtual Network Mapping Algorithm based on Subgraph Isomorphism Detection"
 * .
 * 
 * epsilon ~ 10 omega ~ 4 * |VN|
 * 
 * @author Michael Till Beck
 */
public class SubgraphIsomorphismAlgorithm implements
		SingleNetworkMappingAlgorithm {
	public static final boolean debug = false;

	boolean useEnergyResource;

	private int numberOfTries;

	/**
	 * 
	 * @param useEnergyResource
	 *            True, if EnergyResource should be considered, otherwise false
	 */
	public SubgraphIsomorphismAlgorithm(boolean useEnergyResource) {
		this.useEnergyResource = useEnergyResource;
	}

	Mapping vnmFlib(Mapping m, VirtualNetwork g_V, SubstrateNetwork g_P,
			SubstrateNetwork orig_g_P, int omega, int epsilon) {

		if (debug) {
			System.out.println("numberOfTries: " + numberOfTries);
		}

		if (isMappingComplete(m, g_V, orig_g_P)) {
			return m;
		}

		if (debug) {
			System.out.println();
			System.out.println();
			System.out.println("g_sub_V:");
			for (VirtualNode n : m.getNodeEntries().keySet()) {
				System.out.println(n);
			}
			System.out.println();
		}

		List<MappingCandidate<VirtualNode, SubstrateNode>> c = genneigh(
				orig_g_P, g_P, g_V, m);

		for (MappingCandidate<VirtualNode, SubstrateNode> candidate : c) {

			++numberOfTries;
			if (numberOfTries > omega) {
				return null;
			}

			Mapping m2 = new Mapping(m);
			m2.add(candidate.t, candidate.u);
			Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> resourceMapping = Utils
					.occupyResources(candidate.t.get(), candidate.u.get());

			Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> edges = mapEdges(
					g_V, orig_g_P, candidate, m2, epsilon);

			if (edges != null) {

				if (debug) {
					System.out.println("Mapped nodes " + candidate.t + " <-> "
							+ candidate.u);
				}

				SubstrateNetwork g_res = get_g_res(g_P, candidate.u);
				Mapping result = vnmFlib(m2, g_V, g_res, orig_g_P, omega,
						epsilon);

				if (result != null) {
					if (debug) {
						System.out.println("returning m3");
					}
					return result;
				}

				Utils.freeResources(edges);
			}

			Utils.freeResources(resourceMapping);
		}

		if (debug) {
			System.out.println("returning null");
		}
		return null;
	}

	SubstrateNetwork get_g_res(SubstrateNetwork g_P, SubstrateNode sn) {
		SubstrateNetwork g_res = g_P.getCopy(false, false);
		boolean removed = g_res.removeVertex(sn);
		assert (removed);

		return g_res;
	}

	boolean isMappingComplete(Mapping m, VirtualNetwork g_V,
			SubstrateNetwork orig_g_P) {

		return m.getNodeEntries().entrySet().size() == g_V.getVertexCount();
	}

	LinkedList<SubstrateLink> findShortestPath(SubstrateNetwork sNetwork,
			SubstrateNode n1, SubstrateNode n2,
			Collection<AbstractDemand> vldemands, int epsilon) {

		if (n1.getId() == n2.getId()) {
			return new LinkedList<SubstrateLink>();
		}

		MyDijkstraShortestPath<SubstrateNode, SubstrateLink> dijkstra = new MyDijkstraShortestPath<SubstrateNode, SubstrateLink>(
				sNetwork, new SubstrateLinkDemandTransformer(vldemands), true);

		LinkedList<SubstrateLink> path = dijkstra.getPath(n1, n2, epsilon);
		assert (path == null || Utils.fulfills(path, vldemands));

		return path;
	}

	Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> mapEdges(
			VirtualNetwork g_V, SubstrateNetwork orig_g_P,
			MappingCandidate<VirtualNode, SubstrateNode> candidate, Mapping m,
			int epsilon) {

		Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> out = mapOutEdges(
				g_V, orig_g_P, candidate, m, epsilon);
		if (out == null) {
			return null;
		}
		Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> in = mapInEdges(
				g_V, orig_g_P, candidate, m, epsilon);
		if (in == null) {
			Utils.freeResources(out);
			return null;
		}

		out.addAll(in);
		return out;
	}

	Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> mapOutEdges(
			VirtualNetwork g_V, SubstrateNetwork orig_g_P,
			MappingCandidate<VirtualNode, SubstrateNode> candidate, Mapping m,
			int epsilon) {

		Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> result = new LinkedList<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>>();

		for (VirtualLink vl : g_V.getOutEdges(candidate.t)) {
			VirtualNode opposite = g_V.getOpposite(candidate.t, vl);
			SubstrateNode sOpposite = m.getSubstrateNode(opposite);

			if (sOpposite != null && sOpposite.getId() != candidate.u.getId()) {
				List<SubstrateLink> path = findShortestPath(orig_g_P,
						candidate.u, sOpposite, vl.get(), epsilon);

				if (path == null) {
					Utils.freeResources(result);
					return null;
				}

				result.addAll(Utils.occupyPathResources(vl, path, orig_g_P,
						useEnergyResource));
				m.add(vl, path);
			}
		}
		return result;
	}

	Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> mapInEdges(
			VirtualNetwork g_V, SubstrateNetwork orig_g_P,
			MappingCandidate<VirtualNode, SubstrateNode> candidate, Mapping m,
			int epsilon) {

		Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> result = new LinkedList<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>>();

		for (VirtualLink vl : g_V.getInEdges(candidate.t)) {
			VirtualNode opposite = g_V.getOpposite(candidate.t, vl);
			SubstrateNode sOpposite = m.getSubstrateNode(opposite);

			if (sOpposite != null && sOpposite.getId() != candidate.u.getId()) {
				List<SubstrateLink> path = findShortestPath(orig_g_P,
						sOpposite, candidate.u, vl.get(), epsilon);

				if (path == null) {
					Utils.freeResources(result);
					return null;
				}

				result.addAll(Utils.occupyPathResources(vl, path, orig_g_P,
						useEnergyResource));
				m.add(vl, path);
			}
		}
		return result;
	}

	List<MappingCandidate<VirtualNode, SubstrateNode>> genneigh(
			SubstrateNetwork orig_g_P, SubstrateNetwork g_P,
			VirtualNetwork g_V, Mapping m) {
		List<MappingCandidate<VirtualNode, SubstrateNode>> c = null;

		Collection<VirtualNode> f_G_sub_V = f(m.getNodeEntries().keySet(), g_V);

		if (debug) {
			System.out.println("f_G_sub_V");
			for (VirtualNode n : f_G_sub_V) {
				System.out.println(n);
			}
			System.out.println();
		}

		Collection<VirtualNode> vns = null;
		if (f_G_sub_V.isEmpty()) {
			vns = g_V.getVertices();
		} else {
			vns = f_G_sub_V;
		}
		vns = Utils.minus(vns, m.getNodeEntries().keySet());

		c = cartesian(vns, g_P.getVertices());
		if (debug) {
			System.out.println("c");
			for (MappingCandidate<VirtualNode, SubstrateNode> cn : c) {
				System.out.println(cn.t + " <-> " + cn.u);
			}
			System.out.println();
		}

		optimize(c);
		sort(c);
		return c;
	}

	<T extends NetworkEntity<? extends AbstractDemand>, U extends NetworkEntity<? extends AbstractResource>> List<MappingCandidate<T, U>> cartesian(
			Collection<T> ts, Collection<U> us) {
		List<MappingCandidate<T, U>> result = new LinkedList<MappingCandidate<T, U>>();

		for (T t : ts) {
			for (U u : us) {
				if (Utils.fulfills(u, t)) {
					result.add(new MappingCandidate<T, U>(t, u));
				}
			}
		}

		return result;
	}

	void optimize(List<MappingCandidate<VirtualNode, SubstrateNode>> c) {
		// we've already optimized here, but maybe we will need this in
		// subclasses ...
	}

	void sort(List<MappingCandidate<VirtualNode, SubstrateNode>> c) {
		Collections.sort(c,
				new Comparator<MappingCandidate<VirtualNode, SubstrateNode>>() {
					public int compare(
							MappingCandidate<VirtualNode, SubstrateNode> o1,
							MappingCandidate<VirtualNode, SubstrateNode> o2) {

						// if (o1.u.getId() < o2.u.getId())
						// return -1;
						// else if (o1.u.getId() > o2.u.getId())
						// return 1;

						double tmp = Utils.getCpuDemand(o1.t).getDemandedCycles()
								- Utils.getCpuDemand(o2.t).getDemandedCycles();

						if (tmp < 0.0) { // NOTE: not really a good way
							// comparing doubles ...
							return 1;
						}
						if (tmp > 0.0) {
							return -1;
						}

						return 0;
					}
				});
	}

	int compare(MappingCandidate<VirtualNode, SubstrateNode> o1,
			MappingCandidate<VirtualNode, SubstrateNode> o2) {

		// TODO: should we use Utils.getCpuRemaining instead?
		double tmp = Utils.getCpuResource(o1.u).getAvailableCycles() - Utils.getCpuResource(o2.u).getAvailableCycles();

		if (tmp < 0.0) { // NOTE: not really a good way comparing doubles ...
			return -1;
		}
		if (tmp > 0.0) {
			return 1;
		}
		return 0;
	}

	Collection<VirtualNode> f(Set<VirtualNode> vns, VirtualNetwork g) {
		Collection<VirtualNode> result = new LinkedList<VirtualNode>();

		for (VirtualLink vl : g.getEdges()) {
			Pair<VirtualNode> endpoints = g.getEndpoints(vl);
			VirtualNode n_i = endpoints.getFirst();
			VirtualNode n_j = endpoints.getSecond();
			if (Utils.contains(n_j, vns)) {
				if (!Utils.contains(n_i, vns)) {
					if (!Utils.contains(n_i, result)) {
						result.add(n_i);
					}
				}
			} else {
				if (Utils.contains(n_i, vns)) {
					if (!Utils.contains(n_j, result)) {
						result.add(n_j);
					}
				}
			}
		}

		return result;
	}

	public boolean mapNetwork(SubstrateNetwork sNetwork, VirtualNetwork vNetwork) {
		int epsilon = 10;
		int omega = 4 * vNetwork.getVertexCount();
		return (mapNetwork(sNetwork, vNetwork, epsilon, omega) != null);
	}

	public Mapping mapNetwork(SubstrateNetwork sNetwork,
			VirtualNetwork vNetwork, int epsilon, int omega) {
		numberOfTries = 0;
		return vnmFlib(new Mapping(useEnergyResource), vNetwork, sNetwork,
				sNetwork, omega, epsilon);
	}

	static class MappingCandidate<T, U> {
		T t;
		U u;

		public MappingCandidate(T t, U u) {
			this.t = t;
			this.u = u;
		}
	}

	static class GenneighResult {
		VirtualNode lastNode;
		Collection<VirtualLink> lastPath;

		public GenneighResult(VirtualNode lastNode,
				Collection<VirtualLink> lastPath) {
			this.lastNode = lastNode;
			this.lastPath = lastPath;
		}
	}

}
