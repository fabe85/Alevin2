package vnreal.algorithms;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import vnreal.demands.AbstractDemand;
import vnreal.demands.MLSDemand;
import vnreal.network.Network;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;
import vnreal.resources.MLSResource;

import mulavito.algorithms.AbstractAlgorithmStatus;

public class MLSAlgorithm extends AbstractAlgorithm {

	private final NetworkStack stack;
	private final AbstractNodeMapping nodemapping;
	private final AbstractLinkMapping linkmapping;
	
	/**
	 * 
	 * @param stack
	 * @param nm NodeMapping Algo
	 * @param lm Linkmapping Algo
	 */
	public MLSAlgorithm(NetworkStack stack, AbstractNodeMapping nm, AbstractLinkMapping lm) {
		super();
		this.stack = stack;
		nodemapping = nm;
		linkmapping = lm;
	}

	@Override
	public List<AbstractAlgorithmStatus> getStati() {
		return null;
	}

	@Override
	protected boolean preRun() {
		return true;
	}

	@Override
	protected void evaluate() {
		Iterator<Network<?, ?, ?>> it = stack.iterator();

		while(it.hasNext()) {
			Network<?, ?, ?> net = it.next();
			if(net instanceof VirtualNetwork) {
				// If the Nodemapping failes
				if(!nodemapping.nodeMapping((VirtualNetwork)net)) {
					System.out.println("Vnet"+((VirtualNetwork)net).getLayer()+" couldn't mapped in Nodemappingphase");
					stack.clearVnrMappings((VirtualNetwork)net);
					continue;
				}
				if(!linkmapping.linkMapping((VirtualNetwork)net, nodemapping.getNodeMapping())) {
					System.out.println("Vnet"+((VirtualNetwork)net).getLayer()+" couldn't mapped in Linkmappingphase");
					stack.clearVnrMappings((VirtualNetwork)net);
					continue;
				}
					
			}
			
		}
	}

	@Override
	protected void postRun() {
		Map<String, Double> evData = nodemapping.exportEvaluationData();
		if(evData != null) {
			stack.addEvaluationData(evData);
		}
	}


	/**
	 * Returns the MLSResource from a SubstrateNode
	 * @param s
	 * @return
	 */
	@SuppressWarnings("unused")
	private MLSResource getMLSResource(SubstrateNode s) {
		for(AbstractResource r : s.get()) {
			if(r instanceof MLSResource)
				return (MLSResource) r;
		}
		
		throw new Error("MLSResource not found, but we checked it");
	}
	
	/**
	 * Returns the MLSDemand from a SubstrateNode
	 * @param s
	 * @return
	 */
	@SuppressWarnings("unused")
	private MLSDemand getMLSDemand(VirtualNode s) {
		for(AbstractDemand d : s.get()) {
			if(d instanceof MLSDemand)
				return (MLSDemand) d;
		}
		
		throw new Error("MLSDemand not found, but we checked it");
	}
	
}
