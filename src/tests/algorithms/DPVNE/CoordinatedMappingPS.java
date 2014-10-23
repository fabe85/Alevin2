package tests.algorithms.DPVNE;

import java.util.LinkedList;
import java.util.List;

import mulavito.algorithms.IAlgorithm;
import vnreal.algorithms.CoordinatedMappingPathSplitting;
import vnreal.algorithms.GenericMappingAlgorithm;
import vnreal.algorithms.SingleNetworkMappingAlgorithm;
import vnreal.evaluations.utils.VnrUtils;
import vnreal.hiddenhopmapping.BandwidthCpuHiddenHopMapping;
import vnreal.hiddenhopmapping.IHiddenHopMapping;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.virtual.VirtualNetwork;

public class CoordinatedMappingPS implements SingleNetworkMappingAlgorithm {

	private static final int wCpu = 0, wBw = 0, type = 0, dist = 35;

	@Override
	public boolean mapNetwork(SubstrateNetwork network, VirtualNetwork vNetwork) {
		List<VirtualNetwork> vns = new LinkedList<VirtualNetwork>();
		vns.add(vNetwork);
		NetworkStack stack = new NetworkStack(network, vns);
		LinkedList<IHiddenHopMapping> hhMappings = new LinkedList<IHiddenHopMapping>();
		double hiddenHopsFactor = 0;
		hhMappings.add(new BandwidthCpuHiddenHopMapping(hiddenHopsFactor));

		IAlgorithm algo = new CoordinatedMappingPathSplitting(stack, dist,
				wCpu, wBw, type, false);
		if (algo instanceof GenericMappingAlgorithm)
			((GenericMappingAlgorithm) algo).setHhMappings(hhMappings);

		algo.performEvaluation();
		return VnrUtils.isMapped(vNetwork);

	}

}