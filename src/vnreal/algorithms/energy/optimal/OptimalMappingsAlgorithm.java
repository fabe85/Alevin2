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
package vnreal.algorithms.energy.optimal;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import mulavito.algorithms.AbstractAlgorithmStatus;

import org.apache.commons.collections15.Transformer;

import vnreal.algorithms.AbstractAlgorithm;
import vnreal.algorithms.utils.SubgraphBasicVN.Mapping;
import vnreal.algorithms.utils.SubgraphBasicVN.MyDijkstraShortestPath;
import vnreal.algorithms.utils.SubgraphBasicVN.ResourceDemandEntry;
import vnreal.algorithms.utils.SubgraphBasicVN.Utils;
import vnreal.demands.AbstractDemand;
import vnreal.demands.PowerDemand;
import vnreal.network.Network;
import vnreal.network.NetworkEntity;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;
import vnreal.resources.PowerResource;

/**
 * @author Michael Till Beck
 */
public class OptimalMappingsAlgorithm extends AbstractAlgorithm {

	final int epsilon;

	NetworkStack stack;
	private Iterator<VirtualNetwork> curIt;
	private Iterator<? extends Network<?, ?, ?>> curNetIt;

	// allow mapping to the same sn multiple times
	private final boolean allowMultipleMappings;

	private List<OptimalMappingAlgorithmMappingResult> computedMappingResults = null;

	private boolean useEnergyDemand;

	/**
	 * @param allowMultipleMappings
	 *            allow mapping to the same sn multiple times?
	 */
	public OptimalMappingsAlgorithm(NetworkStack stack,
			boolean allowMultipleMappings, int epsilon, boolean useEnergyDemand) {
		this.stack = stack;
		this.curNetIt = stack.iterator();
		this.allowMultipleMappings = allowMultipleMappings;

		this.epsilon = epsilon;

		this.useEnergyDemand = useEnergyDemand;
	}

	public Collection<OptimalMappingAlgorithmMappingResult> getComputedMappings() {
		return computedMappingResults;
	}

	@SuppressWarnings("unchecked")
	protected boolean hasNext() {
		if (curIt == null || !curIt.hasNext()) {
			if (curNetIt.hasNext()) {
				@SuppressWarnings("unused")
				Network<?, ?, ?> tmp = curNetIt.next();

				curIt = (Iterator<VirtualNetwork>) curNetIt;
				return hasNext();
			} else
				return false;
		} else
			return true;
	}

	protected VirtualNetwork getNext() {
		if (!hasNext()) {
			return null;
		} else {
			return curIt.next();
		}
	}

	@Override
	protected void evaluate() {
		SubstrateNetwork sNetwork = stack.getSubstrate();
		List<OptimalMappingAlgorithmMappingResult> results = new LinkedList<OptimalMappingAlgorithmMappingResult>();
		results.add(new OptimalMappingAlgorithmMappingResult(
				new Mapping(false), null));

		while (hasNext()) {
			VirtualNetwork vNetwork = getNext();

			List<OptimalMappingAlgorithmMappingResult> netResults = new LinkedList<OptimalMappingAlgorithmMappingResult>();

			for (OptimalMappingAlgorithmMappingResult m : results) {
				Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> previousMappings = m.mapping
						.occupyAllResources(sNetwork);

				EnergyResourceChecker mappingChecker = new EnergyResourceChecker();
				vnmFlib(m.mapping, sNetwork.getVertices(),
						vNetwork.getVertices(), vNetwork, sNetwork,
						mappingChecker, netResults);

				Utils.freeResources(previousMappings);
			}
			results = netResults;
		}

		Mapping computedMapping = null;
		if (results.isEmpty()) {
			computedMappingResults = null;
		} else {
			Collections.sort(results, new EnergyResourceChecker());
			computedMappingResults = results;
			computedMapping = computedMappingResults.get(0).mapping;
			computedMapping.occupyAllResources(sNetwork);
		}
	}

	void vnmFlib(Mapping m, Collection<SubstrateNode> unmappedSNodes,
			Collection<VirtualNode> unmappedVNodes, VirtualNetwork vNetwork,
			SubstrateNetwork sNetwork, EnergyResourceChecker mappingChecker,
			Collection<OptimalMappingAlgorithmMappingResult> results) {

		int omega = 4 * vNetwork.getVertexCount();

		Collection<ResourceDemandEntry<PowerResource, PowerDemand>> demandedEnergyResources = new LinkedList<ResourceDemandEntry<PowerResource, PowerDemand>>();

		vnmFlib(m, unmappedSNodes, unmappedVNodes, vNetwork, sNetwork, omega,
				epsilon, 0, mappingChecker, results, demandedEnergyResources);
	}

	void vnmFlib(
			Mapping m,
			Collection<SubstrateNode> unmappedSNodes,
			Collection<VirtualNode> unmappedVNodes,
			VirtualNetwork vNetwork,
			SubstrateNetwork sNetwork,
			int omega,
			int epsilon,
			int depthOfRecursion,
			EnergyResourceChecker mappingChecker,
			Collection<OptimalMappingAlgorithmMappingResult> results,
			Collection<ResourceDemandEntry<PowerResource, PowerDemand>> demandedNodeEnergyResources) {

		if (omega != -1 && depthOfRecursion > omega) {
			return;
		}

		if (unmappedVNodes.isEmpty()) {
			OptimalMappingAlgorithmMappingResult newResult = new OptimalMappingAlgorithmMappingResult(
					m, demandedNodeEnergyResources);
			results.add(newResult);
			if (mappingChecker.check(demandedNodeEnergyResources)) {
				mappingChecker.setBest(demandedNodeEnergyResources);
			}
			return;
		}
		if (!mappingChecker.check(demandedNodeEnergyResources)) {
			return;
		}

		List<MappingCandidate<VirtualNode, SubstrateNode>> c = genneigh(
				sNetwork.getVertices(), unmappedSNodes, unmappedVNodes);

		for (MappingCandidate<VirtualNode, SubstrateNode> candidate : c) {
			Mapping m2 = new Mapping(m);
			m2.add(candidate.t, candidate.u);
			Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> resourceMapping = Utils
					.occupyResources(candidate.t.get(), candidate.u.get());

			Collection<ResourceDemandEntry<PowerResource, PowerDemand>> newDemandedNodeEnergyResources = new LinkedList<ResourceDemandEntry<PowerResource, PowerDemand>>(
					demandedNodeEnergyResources);

			Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> edges = mapEdges(
					sNetwork, vNetwork, candidate, m2,
					newDemandedNodeEnergyResources, epsilon);

			if (edges != null) {
				Collection<SubstrateNode> newUnmappedSNodes = new LinkedList<SubstrateNode>(
						unmappedSNodes);
				Collection<VirtualNode> newUnmappedVNodes = new LinkedList<VirtualNode>(
						unmappedVNodes);
				newUnmappedSNodes.remove(candidate.u);
				newUnmappedVNodes.remove(candidate.t);

				boolean found = false;
				for (ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand> demEntry : resourceMapping) {
					if (demEntry.res instanceof PowerResource) {
						found = true;
						newDemandedNodeEnergyResources
								.add(new ResourceDemandEntry<PowerResource, PowerDemand>(
										(PowerResource) demEntry.res,
										(PowerDemand) demEntry.dem));
					}
				}
				assert (found);

				vnmFlib(m2, newUnmappedSNodes, newUnmappedVNodes, vNetwork,
						sNetwork, omega, epsilon, depthOfRecursion + 1,
						mappingChecker, results, newDemandedNodeEnergyResources);

				Utils.freeResources(edges);
			}

			Utils.freeResources(resourceMapping);
		}
	}

	List<SubstrateLink> findShortestPath(SubstrateNetwork sNetwork,
			SubstrateNode n1, SubstrateNode n2,
			Collection<AbstractDemand> vldemands, int epsilon) {
		MyDijkstraShortestPath<SubstrateNode, SubstrateLink> dijkstra = new MyDijkstraShortestPath<SubstrateNode, SubstrateLink>(
				sNetwork, new SubstrateLinkStressTransformer(vldemands), true);

		List<SubstrateLink> path = dijkstra.getPath(n1, n2, epsilon);
		assert (path == null || Utils.fulfills(path, vldemands));

		return path;
	}

	Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> mapEdges(
			SubstrateNetwork sNetwork,
			VirtualNetwork vNetwork,
			MappingCandidate<VirtualNode, SubstrateNode> candidate,
			Mapping m,
			Collection<ResourceDemandEntry<PowerResource, PowerDemand>> demandedNodeEnergyResource,
			int epsilon) {

		Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> out = mapOutEdges(
				sNetwork, vNetwork, candidate, m, epsilon);
		if (out == null) {
			return null;
		}
		Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> in = mapInEdges(
				sNetwork, vNetwork, candidate, m, epsilon);
		if (in == null) {
			Utils.freeResources(out);
			return null;
		}

		out.addAll(in);
		return out;
	}

	Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> mapOutEdges(
			SubstrateNetwork sNetwork, VirtualNetwork vNetwork,
			MappingCandidate<VirtualNode, SubstrateNode> candidate, Mapping m,
			int epsilon) {

		Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> result = new LinkedList<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>>();

		for (VirtualLink vl : vNetwork.getOutEdges(candidate.t)) {
			VirtualNode opposite = vNetwork.getOpposite(candidate.t, vl);
			SubstrateNode sOpposite = m.getSubstrateNode(opposite);

			if (sOpposite != null && sOpposite.getId() != candidate.u.getId()) {
				List<SubstrateLink> path = findShortestPath(sNetwork,
						candidate.u, sOpposite, vl.get(), epsilon);

				if (path == null) {
					Utils.freeResources(result);
					return null;
				}

				result.addAll(Utils.occupyPathResources(vl, path, sNetwork,
						useEnergyDemand));
				m.add(vl, path);
			}
		}
		return result;
	}

	Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> mapInEdges(
			SubstrateNetwork sNetwork, VirtualNetwork vNetwork,
			MappingCandidate<VirtualNode, SubstrateNode> candidate, Mapping m,
			int epsilon) {

		Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> result = new LinkedList<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>>();

		for (VirtualLink vl : vNetwork.getInEdges(candidate.t)) {
			VirtualNode opposite = vNetwork.getOpposite(candidate.t, vl);
			SubstrateNode sOpposite = m.getSubstrateNode(opposite);

			if (sOpposite != null && sOpposite.getId() != candidate.u.getId()) {
				List<SubstrateLink> path = findShortestPath(sNetwork,
						sOpposite, candidate.u, vl.get(), epsilon);

				if (path == null) {
					Utils.freeResources(result);
					return null;
				}

				result.addAll(Utils.occupyPathResources(vl, path, sNetwork,
						useEnergyDemand));
				m.add(vl, path);
			}
		}
		return result;
	}

	List<MappingCandidate<VirtualNode, SubstrateNode>> genneigh(
			Collection<SubstrateNode> allSNodes,
			Collection<SubstrateNode> unmappedSNodes,
			Collection<VirtualNode> unmappedVNodes) {

		List<MappingCandidate<VirtualNode, SubstrateNode>> c;
		if (allowMultipleMappings) {
			c = cartesian(unmappedVNodes, allSNodes);
		} else {
			c = cartesian(unmappedVNodes, unmappedSNodes);
		}

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

	void sort(List<MappingCandidate<VirtualNode, SubstrateNode>> c) {
		Collections.sort(c,
				new Comparator<MappingCandidate<VirtualNode, SubstrateNode>>() {
					public int compare(
							MappingCandidate<VirtualNode, SubstrateNode> o1,
							MappingCandidate<VirtualNode, SubstrateNode> o2) {

						double tmp = Utils.getCpuRemaining(o1.u, o1.t)
								- Utils.getCpuRemaining(o2.u, o2.t);

						if (tmp < 0.0) { // NOTE: not really a good way
							// comparing doubles ...
							return -1;
						}
						if (tmp > 0.0) {
							return 1;
						}
						return 0;
					}
				});
	}

	static class MappingCandidate<T, U> {
		T t;
		U u;

		public MappingCandidate(T t, U u) {
			this.t = t;
			this.u = u;
		}
	}

	@Override
	protected void postRun() {
	}

	@Override
	protected boolean preRun() {
		return true;
	}

	@Override
	public List<AbstractAlgorithmStatus> getStati() {
		return null;
	}

	static class SubstrateLinkStressTransformer implements
			Transformer<SubstrateLink, Double> {
		private Collection<AbstractDemand> vldemands;

		public SubstrateLinkStressTransformer(
				Collection<AbstractDemand> vldemands) {
			this.vldemands = vldemands;
		}

		public Double transform(SubstrateLink l) {
			if (!Utils.fulfills(vldemands, l)) {
				return Double.POSITIVE_INFINITY;
			}
			return (double) Utils.getStressLevel(l);
		}
	}
}
