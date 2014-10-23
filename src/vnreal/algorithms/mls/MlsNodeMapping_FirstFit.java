package vnreal.algorithms.mls;

import vnreal.algorithms.AbstractNodeMapping;
import vnreal.algorithms.utils.NodeLinkAssignation;
import vnreal.demands.AbstractDemand;
import vnreal.demands.MLSDemand;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;
import vnreal.resources.MLSResource;

/**
 * In this class only MLSLabels are checked, all other demands and resources are ignored
 * @author Fabian Kokot
 * @since 12-05-2012
 *
 */
public class MlsNodeMapping_FirstFit extends AbstractNodeMapping {

	public MlsNodeMapping_FirstFit(SubstrateNetwork sNet, boolean subsNodeOverload) {
		super(sNet, subsNodeOverload);
		if(!checkMLSResource(sNet)) {
			throw new Error("One or more SubstrateNode has no MLSResource");
		}
	}

	@Override
	protected boolean nodeMapping(VirtualNetwork vNet) {
		//First we try to Map based on ID's
		if(!isPreNodeMappingFeasible(vNet))
			throw new Error("PreNodeMapping based on IDs failed");
		
		
		//Check whether we have MLSDemands
		if(!checkMLSDemands(vNet)) {
			throw new Error("One or more VirtualNode has no MLSDemand");
		}
		
		
		for(VirtualNode v : getUnmappedvNodes()) {
			for(AbstractDemand d : v.get()) {
				//We are looking only for MLSDemands
				if(d instanceof MLSDemand) {
					if(mapMLS((MLSDemand)d))
						//Next VirtualNode
						break;
					else {
						System.out.println("Not Mapped: "+d);
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Checks that every SubstrateNode have an MLSResource
	 * @param sNet
	 * @return true if ok
	 */
	private boolean checkMLSResource(SubstrateNetwork sNet) {
		boolean hasMLS;
		for(SubstrateNode n : sNet.getVertices()) {
			hasMLS = false;
			for(AbstractResource r : n.get()){
				if (r instanceof MLSResource)
					hasMLS = true;
			}
			if(!hasMLS)
				return false;
		}
		return true;
	}
	
	/**
	 * Checks that every VirtualNode have an MLSResource
	 * @param vNet
	 * @return true if ok
	 */
	private boolean checkMLSDemands(VirtualNetwork vNet) {
		boolean hasMLS;
		for(VirtualNode n : vNet.getVertices()) {
			hasMLS = false;
			for(AbstractDemand r : n.get()){
				if (r instanceof MLSDemand)
					hasMLS = true;
			}
			if(!hasMLS)
				return false;
		}
		return true;
	}

	/**
	 * This Method tries to map the demand on the SubstrateNetwork
	 * Tries to optimize for MLS Labels
	 * @param d
	 * @return true if mapping has worked
	 */
	private boolean mapMLS(MLSDemand d) {
		for (SubstrateNode s : getUnmappedsNodes()) {
			for (AbstractResource r : s.get() ){
				if (r instanceof MLSResource){
					if (r.accepts(d)&&r.fulfills(d)) {
						NodeLinkAssignation.occupy(d, r);
						nodeMapping.put((VirtualNode) d.getOwner(), s);
						return true;
					}		
				}
			}
		}
		
		return false;
	}
}
