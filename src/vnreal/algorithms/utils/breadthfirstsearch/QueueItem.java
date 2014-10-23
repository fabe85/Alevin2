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
import java.util.List;

import vnreal.network.substrate.SubstrateNode;

/**
 * QueueItem goes in the list. It holds nodeId and the history of previously
 * visited nodes.
 * 
 * @author Nemanja Kostic <nemanja.kostic@gmail.com>
 */
public class QueueItem {

	private long nodeId;
	private List<SubstrateNode> historyPath;

	public QueueItem(long sourceNodeId, List<SubstrateNode> list) {
		this.nodeId = sourceNodeId;
		this.historyPath = list;
	}

	public long getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public List<SubstrateNode> getHistoryPath() {
		return historyPath;
	}

	public void setHistoryPath(ArrayList<SubstrateNode> historyPath) {
		this.historyPath = historyPath;
	}

}
