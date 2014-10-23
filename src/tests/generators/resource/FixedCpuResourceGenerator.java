package tests.generators.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tests.ConversionHelper;
import tests.generators.GeneratorParameter;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.resources.CpuResource;

/**
 * This Resource generator generating {@link CpuResource} is using a random generator with a seed
 *  
 * @author Fabian Kokot
 *
 */
@GeneratorParameter(
		parameters = { "Networks:Networks", "TR:Min_CPU_Res", "TR:Max_CPU_Res", "Seed:Seed"}
		)
public class FixedCpuResourceGenerator extends AbstractResourceGenerator<List<CpuResource>> {

	@Override
	public List<CpuResource> generate(ArrayList<Object> parameters) {
		ArrayList<CpuResource> resList = new ArrayList<CpuResource>();
		
		NetworkStack ns = (NetworkStack)parameters.get(0);
		Integer minCPU = ConversionHelper.paramObjectToInteger(parameters.get(1));
		Integer maxCPU = ConversionHelper.paramObjectToInteger(parameters.get(2));
		Long seed = ConversionHelper.paramObjectToLong(parameters.get(3));
		
		Random random = new Random();
		random.setSeed(seed);
		
		
		SubstrateNetwork sn = ns.getSubstrate();
		
		for(SubstrateNode n : sn.getVertices()) {
			CpuResource cpu = new CpuResource(n);
			int value = (int) (minCPU + (maxCPU
					- minCPU + 1)
					* random.nextDouble());
			cpu.setCycles((double) value);
			n.add(cpu);
			resList.add(cpu);
		}
		
		return resList;
	}

	@Override
	public void reset() {
		
	}

}
