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
package vnreal.network.substrate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.collections15.Factory;

import vnreal.network.Network;
import vnreal.resources.AbstractResource;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * The physical substrate underlying all virtual networks.
 * 
 * @author Michael Duelli
 */
@SuppressWarnings("serial")
public class SubstrateNetwork extends
		Network<AbstractResource, SubstrateNode, SubstrateLink> {

	public SubstrateNetwork(boolean autoUnregisterConstraints) {
		super(autoUnregisterConstraints);
	}
	
	public SubstrateNetwork(boolean autoUnregisterConstraints, boolean directed) {
		super(autoUnregisterConstraints, directed);
	}

	@Override
	public String getLabel() {
		return "Substrate Network";
	}

	@Override
	public int getLayer() {
		return 0;
	}

	@Override
	public Factory<SubstrateLink> getEdgeFactory() {
		return new Factory<SubstrateLink>() {
			@Override
			public SubstrateLink create() {
				return new SubstrateLink();
			}
		};
	}

	@Override
	public String toString() {
		String result = "NODES:\n";
		for (SubstrateNode n : getVertices()) {
			result += n.getId() + "\n";
			for (AbstractResource r : n.get()) {
				result += "  " + r.toString() + "\n";
			}
		}

		result += "\nEDGES:\n";
		for (SubstrateLink l : getEdges()) {
			Pair<SubstrateNode> pair = getEndpoints(l);
			result += l.getId() + "  (" + pair.getFirst().getId() + "<->"
					+ pair.getSecond().getId() + ") \n";
			for (AbstractResource r : l.get()) {
				result += "  " + r.toString() + "\n";
			}
		}

		return result;
	}

	@Override
	public SubstrateNetwork getInstance(boolean autoUnregister) {
		return new SubstrateNetwork(autoUnregister);
	}

	@Override
	public SubstrateNetwork getCopy(boolean autoUnregister) {
		SubstrateNetwork result = new SubstrateNetwork(autoUnregister);
		getCopy(false, result);
		return result;
	}
	
	public void getCopy(SubstrateNetwork sNetwork) {
		getCopy(false, sNetwork);
	}


	public SubstrateNetwork getCopy(boolean autoUnregister, boolean deepCopy) {
		SubstrateNetwork result = new SubstrateNetwork(autoUnregister);
		getCopy(deepCopy, result);
		return result;
	}
	
	public void getCopy(boolean deepCopy, SubstrateNetwork result) {

		HashMap<String, SubstrateNode> map = new HashMap<String, SubstrateNode>();

		LinkedList<SubstrateLink> originalLinks = new LinkedList<SubstrateLink>(
				getEdges());
		SubstrateNode tmpSNode, tmpDNode;
		SubstrateLink tmpSLink;
		for (Iterator<SubstrateNode> tempSubsNode = getVertices().iterator(); tempSubsNode
				.hasNext();) {
			tmpSNode = tempSubsNode.next();
			if (deepCopy) {
				SubstrateNode clone = tmpSNode.getCopy();
				result.addVertex(clone);
				map.put(tmpSNode.getName(), clone);
			} else {
				result.addVertex(tmpSNode);
			}
		}
		for (Iterator<SubstrateLink> tempItSubLink = originalLinks.iterator(); tempItSubLink
				.hasNext();) {
			tmpSLink = tempItSubLink.next();
			tmpSNode = getSource(tmpSLink);
			tmpDNode = getDest(tmpSLink);

			if (deepCopy) {
				result.addEdge(tmpSLink.getCopy(),
						map.get(tmpSNode.getName()),
						map.get(tmpDNode.getName()));
			} else {
				result.addEdge(tmpSLink, tmpSNode, tmpDNode);
			}
		}

	}

	public void generateDuplicateEdges() {
		Collection<SubstrateLink> edges = new LinkedList<SubstrateLink>(getEdges());
		for (SubstrateLink edge : edges) {
			Pair<SubstrateNode> nodes = getEndpoints(edge);

			boolean done = false;
			for (SubstrateLink e : getOutEdges(nodes.getSecond())) {
				if (getEndpoints(e).getSecond() == nodes.getFirst()) {
					done = true;
					e.removeAll();
					for (AbstractResource r : edge) {
						e.add(r.getCopy(e));
					}
					e.setName(edge.getName() + "_dup");
				}
			}

			if (!done) {
				SubstrateLink newEdge = edge.getCopy();
				newEdge.setName(edge.getName() + "_dup");
				addEdge(newEdge, nodes.getSecond(), nodes.getFirst());
			}
		}
	}
	
}
