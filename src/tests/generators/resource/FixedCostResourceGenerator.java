package tests.generators.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tests.ConversionHelper;
import tests.generators.GeneratorParameter;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.resources.CostResource;

/**
 * This generator generates {@link CostResource} based on a random generator with a seed
 * 
 * @author Fabian Kokot
 *
 */
@GeneratorParameter(
		parameters = { "Networks:Networks", "TR:Min_LinkCost", "TR:Max_LinkCost", "Seed:Seed" }
		)
public class FixedCostResourceGenerator extends AbstractResourceGenerator<List<CostResource>> {

	@Override
	public List<CostResource> generate(ArrayList<Object> parameters) {
		ArrayList<CostResource> resList = new ArrayList<CostResource>();
		
		NetworkStack ns = (NetworkStack)parameters.get(0);
		Integer minLC = ConversionHelper.paramObjectToInteger(parameters.get(1));
		Integer maxLC = ConversionHelper.paramObjectToInteger(parameters.get(2));
		Long seed = ConversionHelper.paramObjectToLong(parameters.get(3));
		
		Random random = new Random();
		random.setSeed(seed);
		
		SubstrateNetwork sNet = ns.getSubstrate();
		
		for (SubstrateLink l : sNet.getEdges()) {
			CostResource bw = new CostResource(l);
			int value = (int) (minLC + (maxLC
					- minLC + 1)
					* random.nextDouble());
			bw.setCost((double) value);
			l.add(bw);
			resList.add(bw);
		}
		
		return resList;
	}

	@Override
	public void reset() {
	}

}
