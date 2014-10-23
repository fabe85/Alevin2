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
import vnreal.resources.PhysicalDemandedSecurity;

/**
 * The random {@link PhysicalDemandedSecurity} Generator 
 * 
 * @author Fabian Kokot
 *
 */
@GeneratorParameter(
		parameters = { "Networks:Networks", "TR:Min_PDS", "TR:Max_PDS" }
		)
public class RandomPhysicalDemandedSecurityResourceGenerator extends AbstractResourceGenerator<List<PhysicalDemandedSecurity>> {

	
	@Override
	public List<PhysicalDemandedSecurity> generate(ArrayList<Object> parameters) {
		Random random = new Random();
		
		NetworkStack ns = (NetworkStack)parameters.get(0);
		Integer minPDS = ConversionHelper.paramObjectToInteger(parameters.get(1));
		Integer maxPDS = ConversionHelper.paramObjectToInteger(parameters.get(2));
		
		SubstrateNetwork sNetwork = ns.getSubstrate();
		
		ArrayList<PhysicalDemandedSecurity> resList = new ArrayList<PhysicalDemandedSecurity>();
		
		//Label all Substratenodes (actual Random)
		for (SubstrateNode n :  sNetwork.getVertices()) {
			PhysicalDemandedSecurity res = new PhysicalDemandedSecurity(n);
			int value = (int) (minPDS + (maxPDS
					- minPDS + 1)
					* random.nextDouble());
			res.setPhysicalDemandedSecurityLevel((double) value);
			n.add(res);
			
			resList.add(res);
		}
		
		for (SubstrateLink n :  sNetwork.getEdges()) {
			PhysicalDemandedSecurity res = new PhysicalDemandedSecurity(n);
			int value = (int) (minPDS + (maxPDS
					- minPDS + 1)
					* random.nextDouble());
			res.setPhysicalDemandedSecurityLevel((double) value);
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
