package vnreal.algorithms;

import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.virtual.VirtualNetwork;

public interface SingleNetworkMappingAlgorithm {

	public boolean mapNetwork(SubstrateNetwork network, VirtualNetwork vNetwork);

}
