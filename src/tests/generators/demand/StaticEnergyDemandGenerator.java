package tests.generators.demand;

import java.util.ArrayList;

import tests.generators.GeneratorParameter;
import vnreal.demands.StaticPowerDemand;
import vnreal.network.NetworkStack;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

/**
 * This Resource generator generating {@link StaticPowerDemand} is using a random generator 
 *  
 * @author Fabian Kokot
 *
 */
@GeneratorParameter(
		parameters = { "Networks:Networks" }
		)
public class StaticEnergyDemandGenerator extends AbstractDemandGenerator<Integer> {

	@Override
	public Integer generate(ArrayList<Object> parameters) {
		NetworkStack ns = (NetworkStack)parameters.get(0);
		
		for(int u = 1; u < ns.size(); u++) {
			
			VirtualNetwork vNetwork = (VirtualNetwork)ns.getLayer(u);
		
			for(VirtualNode node : vNetwork.getVertices()) {
				StaticPowerDemand nr = new StaticPowerDemand(node);
				node.add(nr);
			}
		}
		return 0;
	}

	@Override
	public void reset() {
		
	}

}
