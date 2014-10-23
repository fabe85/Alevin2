package tests.generators.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tests.ConversionHelper;
import tests.generators.GeneratorParameter;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.resources.StaticEnergyResource;

/**
 * This Resource generator generating {@link StaticEnergyResource} is using a random generator 
 *  
 * @author Fabian Kokot
 *
 */
@GeneratorParameter(
		parameters = { "Networks:Networks", "TR:Min_SE", "TR:Max_SE"}
		)
public class RandomStaticEnergyResourceGenerator extends AbstractResourceGenerator<List<StaticEnergyResource>> {

	@Override
	public List<StaticEnergyResource> generate(ArrayList<Object> parameters) {
		ArrayList<StaticEnergyResource> resList = new ArrayList<StaticEnergyResource>();
		
		NetworkStack ns = (NetworkStack)parameters.get(0);
		Integer minCPU = ConversionHelper.paramObjectToInteger(parameters.get(1));
		Integer maxCPU = ConversionHelper.paramObjectToInteger(parameters.get(2));
		
		Random random = new Random();
		
		
		SubstrateNetwork sn = ns.getSubstrate();
		
		for(SubstrateNode n : sn.getVertices()) {
			int value = (int) (minCPU + (maxCPU
					- minCPU + 1)
					* random.nextDouble());
			StaticEnergyResource cpu = new StaticEnergyResource(n, value);

			n.add(cpu);
			resList.add(cpu);
		}
		
		return resList;
	}

	@Override
	public void reset() {
		
	}

}
