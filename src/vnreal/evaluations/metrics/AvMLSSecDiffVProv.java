package vnreal.evaluations.metrics;

import vnreal.demands.MLSDemand;
import vnreal.mapping.Mapping;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;
import vnreal.resources.MLSResource;

/**
 * This Metric describes the Average difference between VNodes-Provide
 * 
 * @author Fabian Kokot
 *
 */
public class AvMLSSecDiffVProv extends AbstractEvaluation {

	@Override
	public double calculate() {
		SubstrateNetwork sn = stack.getSubstrate();
		
		int count = 0;
		double result = 0.0;
		
		//Work through all SNodes
		for (SubstrateNode n : sn.getVertices()) {
			//First: Find the Min Level
			double max = 0.0;
			double min = 0.0;
			boolean first = true;
			//Get the MLSResource
			for(AbstractResource ar : n.get()) {
				if(ar instanceof MLSResource) {
					//Get the Mapping on this Resource
					for(Mapping m : ar.getMappings()) {
						//Check that the Mapping is from a Node (Links are handled in another Metric)
						if(m.getDemand().getOwner() instanceof VirtualNode) {
							int d = ((MLSDemand)m.getDemand()).getProvide(); 
							//init
							if(first) {
								min = d;
								max = d;
								count++;
								first = false;
							}
							else if (d < min) {
								min = d;
							}
							else if (d > max)
								max = d;
								
						}
					}
				}
			}
			result += (max - min);
		}	
			
		
		if(count != 0) {
			//System.out.println("Result: "+result+"; Count: "+count);
			return (result/count);
		}
		else
			return 0.0;
	}

	@Override
	public String toString() {
		return "AvMLSSecSpreadVProv";
	}

}
