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
package vnreal.network.virtual;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.collections15.Factory;

import vnreal.demands.AbstractDemand;
import vnreal.network.Network;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * A virtual network built upon the physical substrate.
 * 
 * @author Michael Duelli
 */
@SuppressWarnings("serial")
public final class VirtualNetwork extends
		Network<AbstractDemand, VirtualNode, VirtualLink> {
	/** The layer resp. virtual network id which start from 0. */
	private final int layer;
	private String name = null;

	public VirtualNetwork(int layer, boolean autoUnregisterConstraints) {
		super(autoUnregisterConstraints);
		this.layer = layer;
	}

	public VirtualNetwork(int layer) {
		this(layer, true);
	}

	@Override
	public boolean addVertex(VirtualNode vertex) {
		if (vertex.getLayer() == getLayer())
			return super.addVertex(vertex);
		else
			return false;
	}

	@Override
	public boolean addEdge(VirtualLink edge, VirtualNode v, VirtualNode w) {
		if (edge.getLayer() == getLayer() && v.getLayer() == getLayer()
				&& w.getLayer() == getLayer())
			return super.addEdge(edge, new Pair<VirtualNode>(v, w));
		else
			return false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getLabel() {
		return "Virtual Network (" + layer + ")";
	}

	@Override
	public int getLayer() {
		return layer;
	}

	@Override
	public Factory<VirtualLink> getEdgeFactory() {
		return new Factory<VirtualLink>() {
			@Override
			public VirtualLink create() {
				return new VirtualLink(layer);
			}
		};
	}

	@Override
	public String toString() {
		String result = "NODES:\n";
		for (VirtualNode n : getVertices()) {
			result += n.getId() + "\n";
			for (AbstractDemand d : n.get()) {
				result += "  " + d.toString() + "\n";
			}
		}

		result += "\nEDGES:\n";
		for (VirtualLink l : getEdges()) {
			Pair<VirtualNode> pair = getEndpoints(l);
			result += l.getId() + "  (" + pair.getFirst().getId() + "<->"
					+ pair.getSecond().getId() + ")\n";
			for (AbstractDemand d : l.get()) {
				result += "  " + d.toString() + "\n";
			}
		}

		return result;
	}

	@Override
	public VirtualNetwork getInstance(boolean autoUnregister) {
		return new VirtualNetwork(getLayer(), autoUnregister);
	}

	@Override
	public VirtualNetwork getCopy(boolean autoUnregister) {
		VirtualNetwork copyVNetwork = new VirtualNetwork(getLayer(),
				autoUnregister);

		LinkedList<VirtualLink> originalLinks = new LinkedList<VirtualLink>(
				getEdges());
		VirtualNode tmpSNode, tmpDNode;
		VirtualLink tmpSLink;
		for (Iterator<VirtualNode> tempSubsNode = getVertices().iterator(); tempSubsNode
				.hasNext();) {
			tmpSNode = tempSubsNode.next();
			copyVNetwork.addVertex(tmpSNode);
		}
		for (Iterator<VirtualLink> tempItSubLink = originalLinks.iterator(); tempItSubLink
				.hasNext();) {
			tmpSLink = tempItSubLink.next();
			tmpSNode = getSource(tmpSLink);
			tmpDNode = getDest(tmpSLink);
			copyVNetwork.addEdge(tmpSLink, tmpSNode, tmpDNode);
		}

		return copyVNetwork;
	}
	
	
	public void generateDuplicateEdges() {
		Collection<VirtualLink> edges = new LinkedList<VirtualLink>(getEdges());
		for (VirtualLink edge : edges) {
			Pair<VirtualNode> nodes = getEndpoints(edge);

			boolean done = false;
			for (VirtualLink e : getOutEdges(nodes.getSecond())) {
				if (getEndpoints(e).getSecond() == nodes.getFirst()) {
					done = true;
					e.removeAll();
					for (AbstractDemand r : edge) {
						e.add(r.getCopy(e));
					}
					e.setName(edge.getName() + "_dup");
				}
			}

			if (!done) {
				VirtualLink newEdge = edge.getCopy();
				newEdge.setName(edge.getName() + "_dup");
				addEdge(newEdge, nodes.getSecond(), nodes.getFirst());
			}
		}
	}


}
