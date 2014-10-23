package tests.generators.demand;

import java.util.ArrayList;

import tests.generators.GeneratorParameter;
import vnreal.demands.NullDemand;
import vnreal.network.NetworkStack;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

/**
 * This Generator creates NUllDemands
 * @author Fab-Ko
 *
 */
@GeneratorParameter(
		parameters = { "Networks:Networks"}
		)
public class NullDemandGenerator extends AbstractDemandGenerator<Integer> {

	@Override
	public Integer generate(ArrayList<Object> parameters) {
		NetworkStack ns = (NetworkStack)parameters.get(0);
		
		for(int u = 1; u < ns.size(); u++) {
			
			VirtualNetwork vNetwork = (VirtualNetwork)ns.getLayer(u);
			
			for (VirtualNode n : vNetwork.getVertices()) {
				NullDemand nd = new NullDemand(n);
				n.add(nd);
			}
			
			for(VirtualLink vl : vNetwork.getEdges()) {
				NullDemand nd = new NullDemand(vl);
				vl.add(nd);
			}
			
		}
		return 0;
	}

	@Override
	public void reset() {
	}

}
