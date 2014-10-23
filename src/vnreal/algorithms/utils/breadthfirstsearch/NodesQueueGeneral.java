package vnreal.algorithms.utils.breadthfirstsearch;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import vnreal.network.Node;

/**
*   Holds a list of QueueItems.
*
* @author Nemanja Kostic <nemanja.kostic@gmail.com>
*/
public class NodesQueueGeneral {

	private Queue<Node<?>> graphQueue;
		
	public NodesQueueGeneral() {		
		this.graphQueue = new LinkedList<Node<?>>();
	}
	
	public boolean addToQueue(Node<?> item) {
		return graphQueue.offer(item);
	}
	
	public int getSize() {
		return graphQueue.size();
	}
	
	public Node<?> getFromQueue() {
		Object pull = graphQueue.peek();
		if (pull!=null) {
			return graphQueue.poll();
		}
		return null;
	}
	public boolean containsNode(Node<?> node){
		return (graphQueue.contains(node));		
	}

	public Queue<Node<?>> getGraphQueue() {
		return graphQueue;
	}
	
	public String toString(){
		String answer ="";
		for(Iterator<Node<?>> itt = graphQueue.iterator(); itt
		.hasNext();)
			answer += itt.next().toString() + " ";
		
		return answer;
	}

}
