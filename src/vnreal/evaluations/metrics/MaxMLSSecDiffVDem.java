package vnreal.evaluations.metrics;

import vnreal.demands.MLSDemand;
import vnreal.mapping.Mapping;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;
import vnreal.resources.MLSResource;

/**
 * This Metric describes the maximum difference between VNodes-Demand
 * 
 * @author Fabian Kokot
 *
 */
public class MaxMLSSecDiffVDem extends AbstractEvaluation {

	@Override
	public double calculate() {
		SubstrateNetwork sn = stack.getSubstrate();
		
		double max = 0.0d;
		double min = 0.0d;

		boolean first = true;
		//Work through all SNodes
		for (SubstrateNode n : sn.getVertices()) {
			//Get the MLSResource
			for(AbstractResource ar : n.get()) {
				if(ar instanceof MLSResource) {
					//Get the Mapping on this Resource
					for(Mapping m : ar.getMappings()) {
						//Check that the Mapping is from a Node (Links are handled in another Metric)
						if(m.getDemand().getOwner() instanceof VirtualNode) {
							int d = ((MLSDemand)m.getDemand()).getDemand(); 
							if(first) {
								max = d;
								min = d;
								first = false;
							}
							
							if (d > max)
								max = d;
							else if (d < min)
								min = d;
						}
					}
				}
			}
		}
		
		return (max-min);
	}

	@Override
	public String toString() {
		return "MaxMLSSecSpreadVDem";
	}

}
