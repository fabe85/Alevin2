package vnreal.evaluations.metrics;

import vnreal.demands.MLSDemand;
import vnreal.mapping.Mapping;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;
import vnreal.resources.MLSResource;

/**
 * This metric measures the average difference between SNode-Demand and VNode(s)-Provide
 * 
 * @author Fabian Kokot
 *
 */
public class AvMLSSecDiffDemProv extends AbstractEvaluation {
	
	@Override
	public double calculate() {
		
		SubstrateNetwork sn = stack.getSubstrate();
		
		double diff = 0.0d;
		int counter = 0;
		
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
							diff += ((MLSDemand)m.getDemand()).getProvide() - dlevel; 
							counter++;
						}
					}
				}
			}
		}
		
		if(counter == 0)
			return 0.0;
		
		return (diff / counter);
	}

	@Override
	public String toString() {
		return "AvMLSSecSpreadDemProv";
	}

}
