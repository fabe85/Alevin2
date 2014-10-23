package tests.generators.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tests.ConversionHelper;
import tests.generators.GeneratorParameter;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.resources.LinearCpuEnergyResource;

/**
 * This Resource generator generating {@link LinearCpuEnergyResource} is using a random generator 
 *  
 * @author Fabian Kokot
 *
 */
@GeneratorParameter(
		parameters = { "Networks:Networks", "TR:Min_LCE_Base", "TR:Max_LCE_Base", "TR:Min_LCE_Factor", "TR:Max_LCE_Factor", "Seed:Seed"}
		)
public class FixedLinearCpuEnergyResourceGenerator extends AbstractResourceGenerator<List<LinearCpuEnergyResource>> {

	@Override
	public List<LinearCpuEnergyResource> generate(ArrayList<Object> parameters) {
		ArrayList<LinearCpuEnergyResource> resList = new ArrayList<LinearCpuEnergyResource>();
		
		NetworkStack ns = (NetworkStack)parameters.get(0);
		Integer minBase = ConversionHelper.paramObjectToInteger(parameters.get(1));
		Integer maxBase = ConversionHelper.paramObjectToInteger(parameters.get(2));
		Integer minFactor = ConversionHelper.paramObjectToInteger(parameters.get(3));
		Integer maxFactor = ConversionHelper.paramObjectToInteger(parameters.get(4));
		Long seed = ConversionHelper.paramObjectToLong(parameters.get(5));
		
		Random random = new Random();
		random.setSeed(seed);
		
		
		SubstrateNetwork sn = ns.getSubstrate();
		
		for(SubstrateNode n : sn.getVertices()) {
			int valuebase = (int) (minBase + (maxBase
					- minBase + 1)
					* random.nextDouble());
			int valuefactor = (int) (minFactor + (maxFactor
					- minFactor + 1)
					* random.nextDouble());
			LinearCpuEnergyResource cpu = new LinearCpuEnergyResource(n, valuebase, valuefactor);
			n.add(cpu);
			resList.add(cpu);
		}
		
		return resList;
	}

	@Override
	public void reset() {
		
	}

}
