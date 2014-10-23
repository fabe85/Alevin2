package vnreal.algorithms.utils.breadthfirstsearch;

import vnreal.network.Node;

/**
 * QueueItem goes in the list. It holds nodeId and the history of previously
 * visited nodes.
 * 
 * @author Nemanja Kostic <nemanja.kostic@gmail.com>
 */
public class QueueItemGeneral {

	private Node<?> node;

	public QueueItemGeneral(Node<?> sourceNodeId) {
		this.node = sourceNodeId;
	}

	public Node<?> getNode() {
		return node;
	}

	public void setNode(Node<?> node) {
		this.node = node;
	}
	
	public String toString(){
		return node.toString();
	}

}
