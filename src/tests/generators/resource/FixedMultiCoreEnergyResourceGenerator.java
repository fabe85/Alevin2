package tests.generators.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tests.ConversionHelper;
import tests.generators.GeneratorParameter;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.resources.MultiCoreEnergyResource;

/**
 * This Resource generator generating {@link MultiCoreEnergyResource} is using a random generator 
 *  
 * @author Fabian Kokot
 *
 */
@GeneratorParameter(
		parameters = { "Networks:Networks", "TR:Min_MCE_Idle", "TR:Max_MCE_Idle", "TR:Min_MCE_Additional", "TR:Max_MCE_Additional", 
				"TR:Min_MCE_Cores", "TR:Max_MCE_Cores", "Seed:Seed"}
		)
public class FixedMultiCoreEnergyResourceGenerator extends AbstractResourceGenerator<List<MultiCoreEnergyResource>> {

	@Override
	public List<MultiCoreEnergyResource> generate(ArrayList<Object> parameters) {
		ArrayList<MultiCoreEnergyResource> resList = new ArrayList<MultiCoreEnergyResource>();
		
		NetworkStack ns = (NetworkStack)parameters.get(0);
		Integer minIdle = ConversionHelper.paramObjectToInteger(parameters.get(1));
		Integer maxIdle = ConversionHelper.paramObjectToInteger(parameters.get(2));
		Integer minAdditional= ConversionHelper.paramObjectToInteger(parameters.get(3));
		Integer maxAdditional = ConversionHelper.paramObjectToInteger(parameters.get(4));
		Integer minCores= ConversionHelper.paramObjectToInteger(parameters.get(5));
		Integer maxCores = ConversionHelper.paramObjectToInteger(parameters.get(6));
		Long seed = ConversionHelper.paramObjectToLong(parameters.get(7));
		
		Random random = new Random();
		random.setSeed(seed);
		
		
		SubstrateNetwork sn = ns.getSubstrate();
		
		for(SubstrateNode n : sn.getVertices()) {
			int valueIdle = (int) (minIdle + (maxIdle
					- minIdle + 1)
					* random.nextDouble());
			int valueAdditional = (int) (minAdditional + (maxAdditional
					- minAdditional + 1)
					* random.nextDouble());
			int valueCores = (int) (minCores + (maxCores
					- minCores + 1)
					* random.nextDouble());
			MultiCoreEnergyResource cpu = new MultiCoreEnergyResource(n, valueIdle, valueAdditional, valueCores);
			n.add(cpu);
			resList.add(cpu);
		}
		
		return resList;
	}

	@Override
	public void reset() {
		
	}

}
