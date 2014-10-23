package tests.generators.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tests.ConversionHelper;
import tests.generators.GeneratorParameter;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.resources.BandwidthResource;

/**
 * This generator generates {@link BandwidthResource} based on a plain Random Generator
 * 
 * @author Fabian Kokot
 *
 */
@GeneratorParameter(
		parameters = { "Networks:Networks", "TR:Min_BW_Res", "TR:Max_BW_Res" }
		)
public class RandomBandwidthResourceGenerator extends AbstractResourceGenerator<List<BandwidthResource>> {

	@Override
	public List<BandwidthResource> generate(ArrayList<Object> parameters) {
		ArrayList<BandwidthResource> resList = new ArrayList<BandwidthResource>();
		
		Random random = new Random();
		
		NetworkStack ns = (NetworkStack)parameters.get(0);
		Integer minBW = ConversionHelper.paramObjectToInteger(parameters.get(1));
		Integer maxBW = ConversionHelper.paramObjectToInteger(parameters.get(2));
		
		SubstrateNetwork sNet = ns.getSubstrate();
		
		for (SubstrateLink l : sNet.getEdges()) {
			BandwidthResource bw = new BandwidthResource(l);
			int value = (int) (minBW + (maxBW
					- minBW + 1)
					* random.nextDouble());
			bw.setBandwidth((double) value);
			l.add(bw);
			resList.add(bw);
		}
		
		return resList;
	}

	@Override
	public void reset() {
	}

}
