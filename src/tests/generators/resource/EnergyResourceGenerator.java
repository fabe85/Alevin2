package tests.generators.resource;

import java.util.ArrayList;

import tests.generators.GeneratorParameter;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.resources.PowerResource;

/**
 * This Resource generator generating {@link PowerResource} is using a random generator 
 *  
 * @author Fabian Kokot
 *
 */
@GeneratorParameter(
		parameters = { "Networks:Networks"}
		)
public class EnergyResourceGenerator extends AbstractResourceGenerator<Integer> {

	@Override
	public Integer generate(ArrayList<Object> parameters) {
		NetworkStack ns = (NetworkStack)parameters.get(0);
		
		SubstrateNetwork sn = ns.getSubstrate();
		
		for(SubstrateNode node : sn.getVertices()) {
			PowerResource nr = new PowerResource(node);
			node.add(nr);
		}
		
		return 0;
	}

	@Override
	public void reset() {
		
	}

}
