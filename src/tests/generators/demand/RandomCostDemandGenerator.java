package tests.generators.demand;

import java.util.ArrayList;
import java.util.Random;

import tests.ConversionHelper;
import tests.generators.GeneratorParameter;
import vnreal.demands.CostDemand;
import vnreal.network.NetworkStack;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;

/**
 * This generator generates {@link CostDemand} based on a plain Random Generator
 * 
 * @author Fabian Kokot
 *
 */
@GeneratorParameter(
		parameters = { "Networks:Networks", "TR:Min_LinkCost", "TR:Max_LinkCost" }
		)
public class RandomCostDemandGenerator extends AbstractDemandGenerator<Integer> {

	@Override
	public Integer generate(ArrayList<Object> parameters) {
		Random random = new Random();
		
		NetworkStack ns = (NetworkStack)parameters.get(0);
		Integer minCost = ConversionHelper.paramObjectToInteger(parameters.get(1));
		Integer maxCost = ConversionHelper.paramObjectToInteger(parameters.get(2));
		
		
		for(int u = 1; u < ns.size(); u++) {
			
			VirtualNetwork vNetwork = (VirtualNetwork)ns.getLayer(u);
			
			for (VirtualLink l : vNetwork.getEdges()) {

				CostDemand bw = new CostDemand(l);
				int value = (int) (minCost + (maxCost
						- minCost + 1)
						* random.nextDouble());
				bw.setCost((double) value);
				l.add(bw);

			}
		}
		
		return 0;
	}

	@Override
	public void reset() {
	}

}
