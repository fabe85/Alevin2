package tests.generators.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tests.ConversionHelper;
import tests.generators.GeneratorParameter;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.resources.PhysicalProvidedSecurity;

/**
 * The random {@link PhysicalProvidedSecurity} Generator 
 * 
 * @author Fabian Kokot
 *
 */
@GeneratorParameter(
		parameters = { "Networks:Networks", "TR:Min_PPS", "TR:Max_PPS", "Seed:Seed" }
		)
public class FixedPhysicalProvidedSecurityResourceGenerator extends AbstractResourceGenerator<List<PhysicalProvidedSecurity>> {

	
	@Override
	public List<PhysicalProvidedSecurity> generate(ArrayList<Object> parameters) {
		
		NetworkStack ns = (NetworkStack)parameters.get(0);
		Integer minPPS = ConversionHelper.paramObjectToInteger(parameters.get(1));
		Integer maxPPS = ConversionHelper.paramObjectToInteger(parameters.get(2));
		Long seed = ConversionHelper.paramObjectToLong(parameters.get(3));
		
		Random random = new Random();
		random.setSeed(seed);
		
		SubstrateNetwork sNetwork = ns.getSubstrate();
		
		ArrayList<PhysicalProvidedSecurity> resList = new ArrayList<PhysicalProvidedSecurity>();
		
		//Label all Substratenodes (actual Random)
		for (SubstrateNode n :  sNetwork.getVertices()) {
			PhysicalProvidedSecurity res = new PhysicalProvidedSecurity(n);
			int value = (int) (minPPS + (maxPPS
					- minPPS + 1)
					* random.nextDouble());
			res.setPhysicalProvidedSecurityLevel((double) value);
			n.add(res);
			
			resList.add(res);
		}
		
		for (SubstrateLink n :  sNetwork.getEdges()) {
			PhysicalProvidedSecurity res = new PhysicalProvidedSecurity(n);
			int value = (int) (minPPS + (maxPPS
					- minPPS + 1)
					* random.nextDouble());
			res.setPhysicalProvidedSecurityLevel((double) value);
			n.add(res);
			
			resList.add(res);
		}
		
		return resList;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

}
