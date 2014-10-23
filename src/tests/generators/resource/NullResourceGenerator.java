package tests.generators.resource;

import java.util.ArrayList;

import tests.generators.GeneratorParameter;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.resources.NullResource;

@GeneratorParameter(
		parameters = { "Networks:Networks"}
		)
public class NullResourceGenerator extends AbstractResourceGenerator<Integer> {

	@Override
	public Integer generate(ArrayList<Object> parameters) {
		NetworkStack ns = (NetworkStack)parameters.get(0);
		
		SubstrateNetwork sn = ns.getSubstrate();
		
		for(SubstrateNode node : sn.getVertices()) {
			NullResource nr = new NullResource(node);
			node.add(nr);
		}
		
		for(SubstrateLink link : sn.getEdges()) {
			NullResource nr = new NullResource(link);
			link.add(nr);
		}
		
		return 0;
	}

	@Override
	public void reset() {
		
	}

}
