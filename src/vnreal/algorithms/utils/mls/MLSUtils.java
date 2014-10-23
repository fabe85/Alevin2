package vnreal.algorithms.utils.mls;

import vnreal.demands.AbstractDemand;
import vnreal.demands.MLSDemand;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;
import vnreal.resources.MLSResource;

/**
 * This class contains some generic helper-methods for the MLS-Mappings-algorithms
 * 
 * @author Fabian Kokot
 *
 */
public class MLSUtils {

	/**
	 * Checks that every SubstrateNode have an MLSResource
	 * @param sNet
	 * @return true if ok
	 */
	public static boolean checkMLSResource(SubstrateNetwork sNet) {
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
	public static boolean checkMLSDemands(VirtualNetwork vNet) {
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
	 * This Method returns the {@link MLSDemand} of the given {@link VirtualNode} 
	 * 
	 * @param vn
	 * @return The Demand or null
	 */
	public static MLSDemand getMLSDemand(VirtualNode vn) {
		for(AbstractDemand ad : vn.get()) {
			if(ad instanceof MLSDemand) {
				return (MLSDemand)ad;
			}
		}
		return null;
	}
	
	
	/**
	 * This Method returns the {@link MLSResource} of the given {@link SubstrateNode} 
	 * 
	 * @param sn
	 * @return The Demand or null
	 */
	public static MLSResource getMLSResource(SubstrateNode sn) {
		for(AbstractResource ar : sn.get()) {
			if(ar instanceof MLSResource) {
				return (MLSResource)ar;
			}
		}
		return null;
	}
}
