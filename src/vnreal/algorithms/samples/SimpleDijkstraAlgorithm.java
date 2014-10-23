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
package vnreal.algorithms.samples;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import mulavito.algorithms.AbstractAlgorithmStatus;

import org.apache.commons.collections15.ListUtils;

import vnreal.algorithms.AbstractSequentialAlgorithm;
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
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;

/**
 * Maps {@link VirtualLink}s using the Dijkstra algorithm to find a path in the
 * filtered {@link SubstrateNetwork}.
 * 
 * @author Michael Duelli
 * @since 2010-09-14
 */
public final class SimpleDijkstraAlgorithm extends
		AbstractSequentialAlgorithm<VirtualLink> {
	private final NetworkStack stack;
	private Iterator<? extends Network<?, ?, ?>> curNetIt;
	private Iterator<VirtualLink> curIt;
	private int processedLinks = 0;
	private int mappedLinks = 0;

	public SimpleDijkstraAlgorithm(NetworkStack stack) {
		this.stack = stack;
		this.curNetIt = stack.iterator();
	}

	@Override
	protected VirtualLink getNext() {
		if (!hasNext())
			return null;

		return curIt.next();
	}

	@Override
	protected boolean hasNext() {
		if (curIt == null || !curIt.hasNext()) {
			if (curNetIt.hasNext()) {
				@SuppressWarnings("rawtypes")
				Network tmp = curNetIt.next();

				if (tmp instanceof SubstrateNetwork)
					tmp = curNetIt.next();

				curIt = ((VirtualNetwork) tmp).getEdges().iterator();
				return hasNext();
			} else
				return false;
		} else
			return true;
	}

	/**
	 * @param dem
	 *            The considered demand.
	 * @return A list of {@link SubstrateNode}s that fulfill the given
	 *         requirement.
	 */
	private List<SubstrateNode> findFulfillingNodes(AbstractDemand dem) {
		List<SubstrateNode> nodes = new LinkedList<SubstrateNode>();
		for (SubstrateNode n : stack.getSubstrate().getVertices())
			for (AbstractResource res : n)
				if (res.accepts(dem) && res.fulfills(dem)) {
					nodes.add(n);
					break; // Continue with next node.
				}

		return nodes;
	}

	@Override
	protected boolean process(final VirtualLink p) {
		final SubstrateNode srcSnode;
		final SubstrateNode dstSnode;

		processedLinks++; // increase number of processed.

		VirtualNode srcVnode = ((VirtualNetwork) stack.getLayer(p.getLayer()))
				.getSource(p);
		VirtualNode dstVnode = ((VirtualNetwork) stack.getLayer(p.getLayer()))
				.getDest(p);

		// 1. Map source node.
		List<SubstrateNode> srcCandidates = null;
		for (AbstractDemand dem : srcVnode) {
			if (srcCandidates == null)
				srcCandidates = findFulfillingNodes(dem);
			else
				// Nodes that fulfill all requirements!
				srcCandidates = ListUtils.intersection(srcCandidates,
						findFulfillingNodes(dem));
		}

		if (srcCandidates.isEmpty()) {
			System.out.println("Cannot find source for " + p);
			return true;
		} else {
			srcSnode = srcCandidates.get(0);
		}

		// 2. Map destination node.
		List<SubstrateNode> dstCandidates = null;
		for (AbstractDemand dem : dstVnode) {
			if (dstCandidates == null)
				dstCandidates = findFulfillingNodes(dem);
			else
				// Nodes that fulfill all requirements!
				dstCandidates = ListUtils.intersection(dstCandidates,
						findFulfillingNodes(dem));
		}

		if (dstCandidates.isEmpty()) {
			System.out.println("Cannot find destination for " + p);
			return true;
		} else {
			dstSnode = dstCandidates.get(0);
		}

		// 3. Build node filter predicate
		// Predicate<SubstrateNode> nodePredicate = new
		// Predicate<SubstrateNode>() {
		// @Override
		// public boolean evaluate(SubstrateNode n) {
		// if (n.equals(srcSnode) || n.equals(dstSnode))
		// return true; // Always include these!

		// FIXME For now, we ignore requirements on intermediate nodes!
		// for (AbstractRequest req : p) {
		// if (req instanceof INodeConstraint) {
		// if (!findNodeResource(req, n))
		// return false;
		// // Exclude nodes that do not fulfill
		// // all requirements.
		// }
		// }

		// return true;
		// }

		// private boolean findNodeResource(AbstractRequest req,
		// SubstrateNode n) {
		// for (AbstractResource res : n)
		// if (res.accepts(req) && res.fulfills(req))
		// return true;
		//
		// return false;
		// }
		// };

		// 3. Build link filter predicate
		// Predicate<SubstrateLink> linkPredicate = new
		// Predicate<SubstrateLink>() {
		// @Override
		// public boolean evaluate(SubstrateLink l) {
		// for (AbstractDemand dem : p) {
		//
		// if (!findLinkResource(dem, l))
		// return false;
		// // Exclude links that do not fulfill
		// // all requirements.
		// }
		// return true;
		// }
		//
		// private boolean findLinkResource(AbstractDemand dem, SubstrateLink l)
		// {
		// for (AbstractResource res : l)
		// if (res.accepts(dem) && res.fulfills(dem))
		// return true;
		//
		// return false;
		// }
		// };

		// 4. Do the filtering
		// GraphFilter filter = new GraphFilter(nodePredicate, linkPredicate);

		// 5. Search for path in filtered substrate using Dijkstra
		DijkstraShortestPath<SubstrateNode, SubstrateLink> shortestPath;
		shortestPath = new DijkstraShortestPath<SubstrateNode, SubstrateLink>(
		// filter.transform(
				stack.getSubstrate()
		// )
		);
		List<SubstrateLink> path = shortestPath.getPath(srcSnode, dstSnode);

		// No path was found
		if (path.isEmpty()) {
			System.out.println("No path found for " + p);
			return true;
		}

		// 6. Perform virtual node mapping (VNM) for source.
		vnm(srcVnode, srcSnode);

		// 7. Perform virtual node mapping (VNM) for destination.
		vnm(dstVnode, dstSnode);

		// 8. Perform virtual link mapping (VLM) for each link in the path.
		vlm(p, path);

		// If we get here, we succeeded with this link.
		mappedLinks++;

		return true; // Only return false to interrupt the algorithm!!
	}

	private void vnm(VirtualNode vn, SubstrateNode sn) {
		for (AbstractDemand dem : vn) {
			boolean fulfilled = false;
			for (AbstractResource res : sn)
				if (res.accepts(dem) && res.fulfills(dem) && dem.occupy(res)) {
					fulfilled = true;
					break;
				}

			if (!fulfilled)
				throw new AssertionError("But we checked before!");
		}
	}

	private void vlm(VirtualLink vl, List<SubstrateLink> spath) {
		for (SubstrateLink sl : spath)
			// ... a resource to each link demand must be assigned.
			for (AbstractDemand dem : vl) {
				boolean fulfilled = false;

				// FIXME Consider resources of intermediate nodes.
				// if (req instanceof INodeConstraint)

				for (AbstractResource res : sl)
					if (res.accepts(dem) && res.fulfills(dem)
							&& dem.occupy(res)) {
						fulfilled = true;
						break;
					}

				if (!fulfilled)
					throw new AssertionError("But we checked before!");
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
		LinkedList<AbstractAlgorithmStatus> stati = new LinkedList<AbstractAlgorithmStatus>();

		stati.add(new AbstractAlgorithmStatus("Processed VN links") {
			private int max = -1;

			@Override
			public Integer getValue() {
				return processedLinks;
			}

			@Override
			public Integer getMaximum() {
				if (max == -1) {
					max = 0;
					int size = stack.size();
					for (int i = 1 /* skip substrate */; i < size; i++)
						max += stack
								.getLayer(i).getEdgeCount();
				}
				return max;
			}
		});
		stati.add(new AbstractAlgorithmStatus("Mapped VN links") {
			private int max = -1;

			@Override
			public Integer getValue() {
				return mappedLinks;
			}

			@Override
			public Integer getMaximum() {
				if (max == -1) {
					max = 0;
					int size = stack.size();
					for (int i = 1 /* skip substrate */; i < size; i++)
						max += stack
								.getLayer(i).getEdgeCount();
				}
				return max;
			}
		});

		return stati;
	}
}
