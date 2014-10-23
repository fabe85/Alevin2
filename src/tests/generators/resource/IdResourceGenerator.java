package tests.generators.resource;

import java.util.ArrayList;

import tests.generators.GeneratorParameter;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.resources.IdResource;

/**
 * This Resource generator generating {@link IdResource} is using a random generator 
 *  
 * @author Fabian Kokot
 *
 */
@GeneratorParameter(
		parameters = { "Networks:Networks"}
		)
public class IdResourceGenerator extends AbstractResourceGenerator<Integer> {

	@Override
	public Integer generate(ArrayList<Object> parameters) {
		NetworkStack ns = (NetworkStack)parameters.get(0);
		
		SubstrateNetwork sn = ns.getSubstrate();
		
		for(SubstrateNode node : sn.getVertices()) {
			IdResource nr = new IdResource(node, sn);
			nr.setId(node.getId()+"");
			node.add(nr);
		}
		
		return 0;
	}

	@Override
	public void reset() {
		
	}

}
