package tests.generators.demand;

import java.util.ArrayList;
import java.util.Random;

import tests.ConversionHelper;
import tests.generators.GeneratorParameter;
import vnreal.demands.VirtualDemandedSecurity;
import vnreal.network.NetworkStack;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

/**
 * The random {@link VirtualDemandedSecurity} Generator 
 * 
 * @author Fabian Kokot
 *
 */
@GeneratorParameter(
		parameters = { "Networks:Networks", "TR:Min_PDS", "TR:Max_PDS" }
		)
public class RandomVirtualDemandedSecurityResourceGenerator extends AbstractDemandGenerator<Integer> {

	
	@Override
	public Integer generate(ArrayList<Object> parameters) {
		Random random = new Random();
		
		NetworkStack ns = (NetworkStack)parameters.get(0);
		Integer minPDS = ConversionHelper.paramObjectToInteger(parameters.get(1));
		Integer maxPDS = ConversionHelper.paramObjectToInteger(parameters.get(2));
		
		
		for(int u = 1; u < ns.size(); u++) {
			
			VirtualNetwork vNetwork = (VirtualNetwork)ns.getLayer(u);
		
			//Label all VirtualNodes (actual Random)
			for (VirtualNode n :  vNetwork.getVertices()) {
				VirtualDemandedSecurity res = new VirtualDemandedSecurity(n);
				int value = (int) (minPDS + (maxPDS
						- minPDS + 1)
						* random.nextDouble());
				res.setVirtualDemandedSecurityLevel((double) value);
				n.add(res);
				
			}
			
			for (VirtualLink n :  vNetwork.getEdges()) {
				VirtualDemandedSecurity res = new VirtualDemandedSecurity(n);
				int value = (int) (minPDS + (maxPDS
						- minPDS + 1)
						* random.nextDouble());
				res.setVirtualDemandedSecurityLevel((double) value);
				n.add(res);
				
			}
		}
		return 0;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

}
