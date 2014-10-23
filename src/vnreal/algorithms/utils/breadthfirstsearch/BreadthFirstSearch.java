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
package vnreal.algorithms.utils.breadthfirstsearch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;

/**
 * Modification of the Breadth First search algorithm implementation from
 * http://blog.konem.net/java//index.php/2007/11/15/
 * breadth_first_search_algorithm_in_java_w?blog=1
 * 
 * To find a path inside a subgraph of the Substrate Network by:
 * 
 * @author Juan Felipe Botero
 * @author Lisset Diaz
 * @since 2011-2-15
 * 
 */

public class BreadthFirstSearch {
	private long targetNodeId;
	private NodesQueue queue;
	private List<SubstrateNode> resultHistory;
	private Iterable<SubstrateLink> substrateLinks;
	private boolean foundRelation;
	private SubstrateNetwork sNet;

	public BreadthFirstSearch(Set<SubstrateLink> substrateLinks,
			long sourceNodeId, long targetNodeId, SubstrateNetwork sNet) {
		this.targetNodeId = targetNodeId;
		this.queue = new NodesQueue();
		this.queue.addToQueue(new QueueItem(sourceNodeId,
				new ArrayList<SubstrateNode>()));
		this.foundRelation = false;
		this.substrateLinks = substrateLinks;
		this.sNet = sNet;
	}

	public List<SubstrateLink> search() {
		while (!foundRelation && queue.getSize() > 0) {
			QueueItem queueItem = queue.getFromQueue();
			SubstrateNode node = getNodeFromSubstrateNetwork(
					queueItem.getNodeId(), sNet);

			if (node != null)
				findRelation(node, queueItem);

			if (foundRelation) {
				List<SubstrateLink> path = getSubstratePath();
				return path;
			}
		}
		return null;
	}

	private List<SubstrateLink> getSubstratePath() {
		List<SubstrateLink> path = new ArrayList<SubstrateLink>();

		for (int i = 1; i < resultHistory.size(); i++) {
			SubstrateNode tSNode = resultHistory.get(i - 1);
			SubstrateNode tDNode = resultHistory.get(i);
			SubstrateLink link = sNet.findEdge(tSNode, tDNode);
			path.add(link);
		}
		return path;
	}

	private SubstrateNode getNodeFromSubstrateNetwork(long nodeId,
			SubstrateNetwork sNet) {
		SubstrateNode node = new SubstrateNode();
		for (SubstrateNode n : sNet.getVertices()) {
			if (nodeId == n.getId()) {
				node = n;
				break;
			}
		}
		return node;
	}

	/**
	 * All children are in stored in String and are separated by comma.
	 **/
	private void findRelation(SubstrateNode snode, QueueItem queueItem) {
		List<SubstrateNode> childrenNodes;
		if (!foundRelation) {
			childrenNodes = getChildrenFromList(snode);
			if (childrenNodes != null) {
				for (Iterator<SubstrateNode> itt = childrenNodes.iterator(); itt
						.hasNext();) {
					SubstrateNode child = itt.next();
					long childID = child.getId();
					List<SubstrateNode> history = new ArrayList<SubstrateNode>();
					history.addAll(queueItem.getHistoryPath());
					history.add(snode);
					resultHistory = history;
					queue.addToQueue(new QueueItem(childID, history));
					if (childID == targetNodeId) {
						SubstrateNode tNode = getNodeFromSubstrateNetwork(
								childID, sNet);
						if (tNode != null) {
							history.add(tNode);
							resultHistory = history;
						}
						foundRelation = true;
						break;
					}
				}
			}
		}
	}

	private List<SubstrateNode> getChildrenFromList(SubstrateNode snode) {
		List<SubstrateNode> children = new ArrayList<SubstrateNode>();
		for (SubstrateLink l : substrateLinks) {
			SubstrateNode sn = sNet.getSource(l);
			if (sn == snode) {
				children.add(sNet.getDest(l));
			}
		}
		if (children.isEmpty()) {
			return null;
		} else {
			return children;
		}

	}
}
