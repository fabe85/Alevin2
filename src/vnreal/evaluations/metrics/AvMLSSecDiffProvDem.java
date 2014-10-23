package vnreal.evaluations.metrics;

import vnreal.demands.MLSDemand;
import vnreal.mapping.Mapping;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;
import vnreal.resources.MLSResource;

/**
 * This metric measures the average difference between SNode-Provide and VNode(s)-Demands
 * 
 * @author Fabian Kokot
 *
 */
public class AvMLSSecDiffProvDem extends AbstractEvaluation {

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
					int plevel = ((MLSResource)ar).getProvide();
					//Get the Mapping on this Resource
					for(Mapping m : ar.getMappings()) {
						//Check that the Mapping is from a Node (Links are handled in another Metric)
						if(m.getDemand().getOwner() instanceof VirtualNode) {
							//Calculate the difference from snode provide minus vNode demand 
							diff += plevel - ((MLSDemand)m.getDemand()).getDemand(); 
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
		return "AvMLSSecSpreadProvDem";
	}

}