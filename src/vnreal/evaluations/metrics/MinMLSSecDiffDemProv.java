package vnreal.evaluations.metrics;

import vnreal.demands.MLSDemand;
import vnreal.mapping.Mapping;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;
import vnreal.resources.MLSResource;

/**
 * This Metric describes the minimum difference between SNode-Demand and  mapped VNode(s)-Provide
 * 
 * A positive value says that the worst VNode delivers more security than needed
 * A negative value says that the worst VNode delivers fewer security than needed
 * 
 * @author Fabian Kokot
 *
 */
public class MinMLSSecDiffDemProv extends AbstractEvaluation {

	@Override
	public double calculate() {
		SubstrateNetwork sn = stack.getSubstrate();
		
		double min = 0.0d;

		
		//Work through all SNodes
		for (SubstrateNode n : sn.getVertices()) {
			//Get the MLSResource
			for(AbstractResource ar : n.get()) {
				if(ar instanceof MLSResource) {
					int dlevel = ((MLSResource)ar).getDemand();
					//Get the Mapping on this Resource
					for(Mapping m : ar.getMappings()) {
						//Check that the Mapping is from a Node (Links are handled in another Metric)
						if(m.getDemand().getOwner() instanceof VirtualNode) {
							//Calculate the difference from vNode Provide minus snode demand
							int diff = ((MLSDemand)m.getDemand()).getProvide() - dlevel; 
							if (diff < min)
								min = diff;
						}
					}
				}
			}
		}
		
		return (min);
	}

	@Override
	public String toString() {
		return "MinMLSSecSpreadDemProv";
	}

}
