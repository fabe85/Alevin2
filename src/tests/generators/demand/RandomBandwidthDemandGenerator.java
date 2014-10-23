package tests.generators.demand;

import java.util.ArrayList;
import java.util.Random;

import tests.ConversionHelper;
import tests.generators.GeneratorParameter;
import vnreal.demands.BandwidthDemand;
import vnreal.network.NetworkStack;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;

/**
 * This generator generates {@link BandwidthDemand} based on a plain Random Generator
 * 
 * @author Fabian Kokot
 *
 */
@GeneratorParameter(
		parameters = { "Networks:Networks", "TR:Min_BW_Dem", "TR:Max_BW_Dem" }
		)
public class RandomBandwidthDemandGenerator extends AbstractDemandGenerator<Integer> {

	@Override
	public Integer generate(ArrayList<Object> parameters) {
		Random random = new Random();
		
		NetworkStack ns = (NetworkStack)parameters.get(0);
		Integer minBW = ConversionHelper.paramObjectToInteger(parameters.get(1));
		Integer maxBW = ConversionHelper.paramObjectToInteger(parameters.get(2));
		
		
		for(int u = 1; u < ns.size(); u++) {
			
			VirtualNetwork vNetwork = (VirtualNetwork)ns.getLayer(u);
			
			for (VirtualLink l : vNetwork.getEdges()) {

				BandwidthDemand bw = new BandwidthDemand(l);
				int value = (int) (minBW + (maxBW
						- minBW + 1)
						* random.nextDouble());
				bw.setDemandedBandwidth((double) value);
				l.add(bw);

			}
		}
		
		return 0;
	}

	@Override
	public void reset() {
	}

}
