package vnreal.evaluations.metrics;

import vnreal.demands.MLSDemand;
import vnreal.mapping.Mapping;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;
import vnreal.resources.MLSResource;

/**
 * This Metric describes the maximum difference between SNode-Provide and  mapped VNode(s)-Demand
 * 
 * A positive value says that the best SNode delivers more security than VNode Needs
 * A negative value says that the best SNode delivers fewer security than VNode Needs
 * 
 * @author Fabian Kokot
 *
 */
public class MaxMLSSecDiffProvDem extends AbstractEvaluation {

	@Override
	public double calculate() {
		
		SubstrateNetwork sn = stack.getSubstrate();
		
		int max = 0;
		
		//Work through all SNodes
		for (SubstrateNode n : sn.getVertices()) {
			//Get the MLSResource
			for(AbstractResource ar : n.get()) {
				if(ar instanceof MLSResource) {
					int plevel = ((MLSResource)ar).getProvide();
					//Get the Mapping on this Resource
					for(Mapping m : ar.getMappings()) {
						//Check that the Mapping is from a Node (Links are handled in another Metric)
						if(m.getDemand().getOwner() instanceof VirtualNode) {
							//Calculate the difference from vNode demand minus snode provide
							int diff =  plevel - ((MLSDemand)m.getDemand()).getDemand(); 
							if (diff > max)
								max = diff;
						}
					}
				}
			}
		}
		
		return (max);
	}

	@Override
	public String toString() {
		return "MaxMLSSecSpreadProvDem";
	}

}
