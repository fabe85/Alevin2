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
package vnreal.algorithms.basicVN;

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
import vnreal.algorithms.utils.energy.linkStressTransformers.SubstrateLinkStressTransformer;
import vnreal.demands.AbstractDemand;
import vnreal.network.Network;
import vnreal.network.NetworkStack;
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
 * "Algorithms for Assigning Substrate Network Resources to Virtual Network Components"
 * 
 * @author Michael Till Beck
 */
public class BasicVNAssignmentAlgorithm extends AbstractAlgorithm {
	private NetworkStack stack;

	final double delta_N;
	final double delta_L;
	private Iterator<VirtualNetwork> curIt = null;
	private Iterator<? extends Network<?, ?, ?>> curNetIt = null;

	Collection<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>> demandedResources = null;

	// Collection<ResourceDemandEntry<? extends AbstractResource, ? extends
	// AbstractDemand>> allDemandedResources = null;

	Double biggestLinkStress = null, biggestNodeStress = null;

	private boolean useEnergyDemand;

	/**
	 * @param delta_N
	 *            : default = 1
	 * @param delta_L
	 *            : default = 1
	 */
	public BasicVNAssignmentAlgorithm(NetworkStack stack, double delta_N,
			double delta_L, boolean useEnergyDemand) {

		this.delta_N = delta_N;
		this.delta_L = delta_L;

		this.stack = stack;
		this.curNetIt = stack.iterator();
		this.curIt = null;

		this.useEnergyDemand = useEnergyDemand;
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
		if (!hasNext())
			return null;
		else {
			return curIt.next();
		}
	}

	@Override
	protected void evaluate() {
		// allDemandedResources = new LinkedList<ResourceDemandEntry<? extends
		// AbstractResource, ? extends AbstractDemand>>();

		SubstrateNetwork sNetwork = stack.getSubstrate();
		Mapping mapping = new Mapping(useEnergyDemand);
		while (hasNext()) {
			demandedResources = new LinkedList<ResourceDemandEntry<? extends AbstractResource, ? extends AbstractDemand>>();
			if (!mapNetwork(sNetwork, getNext(), mapping)) {
				Utils.freeResources(demandedResources);
				// Utils.freeResources(allDemandedResources);
				// mapping = null;
				// break;
			}
			// allDemandedResources.addAll(demandedResources);
		}
		if (mapping != null) {
			mapping.setMappings(demandedResources);
		}
		demandedResources = null;
	}

	public double getNRValue(SubstrateNetwork sNetwork, SubstrateNode n) {
		double nodeS = biggestNodeStress - getStressLevel(sNetwork, n);

		double linkS = 0.0;
		for (SubstrateLink l : sNetwork.getOutEdges(n)) {
			linkS += biggestLinkStress - getStressLevel(sNetwork, n, l);
		}
		for (SubstrateLink l : sNetwork.getInEdges(n)) {
			linkS += biggestLinkStress - getStressLevel(sNetwork, n, l);
		}

		return nodeS * linkS;
	}

	public SubstrateNode getNRNode(SubstrateNetwork sNetwork) {
		SubstrateNode result = null;
		double resultValue = 0.0, tmp = 0.0;
		for (SubstrateNode n : sNetwork.getVertices()) {
			if ((result == null | resultValue < (tmp = getNRValue(sNetwork, n)))) {
				result = n;
				resultValue = tmp;
			}
		}

		return result;
	}

	public SubstrateNode getMinPiNode(SubstrateNetwork sNetwork,
			List<AbstractDemand> demands, Collection<SubstrateNode> v_a,
			Collection<SubstrateNode> ignoreNodes) {
		SubstrateNode result = null;
		double resultValue = 0.0, tmp = 0.0;
		for (SubstrateNode n : sNetwork.getVertices()) {
			if ((ignoreNodes == null || !ignoreNodes.contains(n))
					&& !v_a.contains(n)) {
				if (Utils.fulfills(demands, n)
						&& (result == null | (resultValue > (tmp = nodePotential(
								sNetwork, n, v_a))))) {

					result = n;
					resultValue = tmp;
				}
			}
		}

		return result;
	}

	DistanceEntry getDistance(SubstrateNetwork sNetwork, SubstrateNode from,
			SubstrateNode to, Transformer<SubstrateLink, Double> transformer) {

		if (from.getId() == to.getId()) {
			return new DistanceEntry(from, to, new LinkedList<SubstrateLink>(),
					0.0);
		}

		// we need a new instance here because we use other link weights
		// every time
		MyDijkstraShortestPath<SubstrateNode, SubstrateLink> dijkstra = new MyDijkstraShortestPath<SubstrateNode, SubstrateLink>(
				sNetwork, transformer, true);

		List<SubstrateLink> path = dijkstra.getPath(from, to);
		if (path == null) {
			return null;
		}
		Number distance = dijkstra.getDistance(from, to);

		return new DistanceEntry(from, to, path, distance.doubleValue());
	}

	public double nodePotential(SubstrateNetwork sNetwork, SubstrateNode v,
			Collection<SubstrateNode> v_a) {

		SubstrateLinkStressTransformer transformer = new SubstrateLinkStressTransformer(
				this, sNetwork, null, biggestLinkStress, delta_L);

		double sum = 0.0;
		for (SubstrateNode n : v_a) {
			DistanceEntry distance = getDistance(sNetwork, v, n, transformer);
			if (distance != null) {
				sum += distance.distance;
			}
		}

		double tmp = biggestNodeStress - getStressLevel(sNetwork, v);

		return tmp == 0 ? sum : (sum / tmp);
	}

	public boolean mapNetwork(SubstrateNetwork sNetwork,
			VirtualNetwork vNetwork, Mapping result) {

		biggestNodeStress = findBiggestNodeStress(sNetwork,
				sNetwork.getVertices());
		biggestLinkStress = findBiggestLinkStress(sNetwork, sNetwork.getEdges());

		Collection<SubstrateNode> v_a = fill_v_a(vNetwork, sNetwork);

		if (!mapNodes(v_a, sNetwork, vNetwork, result)) {
			return false;
		}
		if (!mapLinks(vNetwork, sNetwork, result)) {
			return false;
		}

		return true;
	}

	double findBiggestLinkStress(SubstrateNetwork sNetwork,
			Collection<SubstrateLink> links) {
		double maxNumber = 0.0, tmp = 0.0;
		SubstrateLink maxLink = null;

		for (SubstrateLink sl : links) {
			Pair<SubstrateNode> endpoints = sNetwork.getEndpoints(sl);

			if (maxLink == null
					| (tmp = getStressLevel(sNetwork, endpoints.getFirst(), sl)) > maxNumber) {
				maxNumber = tmp;
				maxLink = sl;
			}
			if (maxLink == null
					| (tmp = getStressLevel(sNetwork, endpoints.getSecond(), sl)) > maxNumber) {
				maxNumber = tmp;
				maxLink = sl;
			}
		}

		return maxNumber;
	}

	double findBiggestNodeStress(SubstrateNetwork sNetwork,
			Collection<SubstrateNode> nodes) {
		double maxNumber = 0.0, tmp = 0.0;
		SubstrateNode maxNode = null;

		for (SubstrateNode sn : nodes) {
			if (maxNode == null
					| (tmp = getStressLevel(sNetwork, sn)) > maxNumber) {
				maxNumber = tmp;
				maxNode = sn;
			}
		}

		return maxNumber;
	}

	Collection<SubstrateNode> fill_v_a(VirtualNetwork vNetwork,
			SubstrateNetwork sNetwork) {
		Collection<SubstrateNode> v_a = new LinkedList<SubstrateNode>();

		SubstrateNode nrNode = getNRNode(sNetwork);
		v_a.add(nrNode);

		for (int i = 0; i < vNetwork.getVertexCount() - 1; ++i) {
			SubstrateNode minPotentialNode = getMinPiNode(sNetwork, null, v_a,
					null);
			if (minPotentialNode == null) {
				break;
			}
			v_a.add(minPotentialNode);
		}

		return v_a;
	}

	LinkedList<Entry<SubstrateNode, Double>> get_nrValuesList(
			SubstrateNetwork sNetwork, Collection<SubstrateNode> v_a) {

		LinkedList<Entry<SubstrateNode, Double>> nrValues = new LinkedList<Entry<SubstrateNode, Double>>();
		for (SubstrateNode n : v_a) {
			nrValues.add(new Entry<SubstrateNode, Double>(n, getNRValue(
					sNetwork, n)));
		}
		Collections.sort(nrValues,
				new Comparator<Entry<SubstrateNode, Double>>() {
					public int compare(Entry<SubstrateNode, Double> o1,
							Entry<SubstrateNode, Double> o2) {
						return o2.value.compareTo(o1.value);
					}
				});

		return nrValues;
	}

	List<Entry<VirtualNode, Integer>> get_degreesList(VirtualNetwork vNetwork) {
		List<Entry<VirtualNode, Integer>> degrees = new LinkedList<Entry<VirtualNode, Integer>>();
		for (VirtualNode n : vNetwork.getVertices()) {
			int degree = vNetwork.getInEdges(n).size()
					+ vNetwork.getOutEdges(n).size(); // NOTE: we include
			// incoming edges here!
			degrees.add(new Entry<VirtualNode, Integer>(n, degree));
		}
		Collections.sort(degrees,
				new Comparator<Entry<VirtualNode, Integer>>() {
					public int compare(Entry<VirtualNode, Integer> o1,
							Entry<VirtualNode, Integer> o2) {
						return o2.value.compareTo(o1.value);
					}
				});
		return degrees;
	}

	/**
	 * mapping of nodes
	 * 
	 * @param degrees
	 * @param nrValues
	 * @param result
	 * @return
	 */
	boolean mapNodes(Collection<SubstrateNode> v_a, SubstrateNetwork sNetwork,
			VirtualNetwork vNetwork, Mapping result) {

		List<Entry<SubstrateNode, Double>> nrValues = get_nrValuesList(
				sNetwork, v_a);
		List<Entry<VirtualNode, Integer>> degrees = get_degreesList(vNetwork);

		for (Entry<VirtualNode, Integer> e : degrees) {
			VirtualNode vn = e.key;

			boolean fulfilled = false;
			for (Entry<SubstrateNode, Double> nrValue : nrValues) {
				if (Utils.fulfills(nrValue.key, vn)) {

					nrValues.remove(nrValue);
					result.add(vn, nrValue.key);
					demandedResources.addAll(Utils.occupyResources(vn.get(),
							nrValue.key.get()));

					double stress = getStressLevel(sNetwork, nrValue.key);
					if (stress > biggestNodeStress) {
						biggestNodeStress = stress;
					}

					fulfilled = true;
					break;
				}
			}

			if (!fulfilled) {
				SubstrateNode minPiNode = getMinPiNode(sNetwork, vn.get(), v_a,
						null);

				if (minPiNode == null) {
					return false;
				}

				// nrValues.add(new Entry<SubstrateNode, Integer>(minPiNode,
				// getNRValue(sNetwork, minPiNode, biggestNodeStress,
				// biggestLinkStress)));
				v_a.add(minPiNode);
				result.add(vn, minPiNode);
				demandedResources.addAll(Utils.occupyResources(vn.get(),
						minPiNode.get()));

				double stress = getStressLevel(sNetwork, minPiNode);
				if (stress > biggestNodeStress) {
					biggestNodeStress = stress;
				}

			}
		}
		return true;
	}

	/**
	 * mapping of links
	 */
	boolean mapLinks(VirtualNetwork vNetwork, SubstrateNetwork sNetwork,
			Mapping result) {

		for (VirtualLink vl : vNetwork.getEdges()) {
			Pair<VirtualNode> nodes = vNetwork.getEndpoints(vl);

			SubstrateNode from = result.getSubstrateNode(nodes.getFirst());
			SubstrateNode to = result.getSubstrateNode(nodes.getSecond());

			if (from.getId() == to.getId()) {
				continue;
			}

			SubstrateLinkStressTransformer transformer = new SubstrateLinkStressTransformer(
					this, sNetwork, vl.get(), biggestLinkStress, delta_L);
			DistanceEntry distance = getDistance(sNetwork, from, to,
					transformer);
			assert (distance == null || Utils.fulfills(distance.path, vl.get()));

			if (distance == null) {
				return false;
			}

			result.add(vl, distance.path);
			demandedResources.addAll(Utils.occupyPathResources(vl,
					distance.path, sNetwork, useEnergyDemand));

			SubstrateNode currentNode = from;
			for (SubstrateLink sl : distance.path) {
				double stress = getStressLevel(sNetwork, currentNode, sl);
				if (stress > biggestLinkStress) {
					biggestLinkStress = stress;
				}
				currentNode = sNetwork.getOpposite(currentNode, sl);
			}
		}
		return true;
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

	// this methods make it easier to modify the behavior of the algorithm by
	// overwriting them in a subclass

	public double getStressLevel(SubstrateNetwork sNetwork, SubstrateNode n) {
		return Utils.getStressLevel(n);
	}

	public double getStressLevel(SubstrateNetwork sNetwork, SubstrateNode node,
			SubstrateLink l) {
		return Utils.getStressLevel(l);
	}

	static class Entry<T, U> {
		public T key;
		public U value;

		public Entry(T key, U value) {
			this.key = key;
			this.value = value;
		}
	}

	static class DistanceEntry {
		public SubstrateNode n1;
		public SubstrateNode n2;
		public List<SubstrateLink> path;
		public double distance;

		public DistanceEntry(SubstrateNode n1, SubstrateNode n2,
				List<SubstrateLink> path, double distance) {
			this.n1 = n1;
			this.n2 = n2;
			this.path = path;
			this.distance = distance;
		}
	}

}
