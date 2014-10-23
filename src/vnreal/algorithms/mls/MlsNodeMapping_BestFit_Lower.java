package vnreal.algorithms.mls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import vnreal.algorithms.AbstractNodeMapping;
import vnreal.algorithms.utils.NodeLinkAssignation;
import vnreal.algorithms.utils.mls.MLSUtils;
import vnreal.demands.AbstractDemand;
import vnreal.demands.CpuDemand;
import vnreal.demands.MLSDemand;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;
import vnreal.resources.CpuResource;
import vnreal.resources.MLSResource;

/**
 * This class creates Mappings based on {@link MLSDemand} and {@link MLSResource} with a BestFit approach with lowering. <br />
 * 
 * Alpha is is maximum number of {@link VirtualNode}'s of one {@link VirtualNetwork} mapped on one {@link SubstrateNode}.
 * 
 * @author Fabian Kokot
 *
 */
public class MlsNodeMapping_BestFit_Lower extends AbstractNodeMapping {
	
	//Some Stuff for statistics
	private int count_abort_analyze = 0;
	private int count_abort_premapping = 0;
	private int count_abort_mapping = 0;
	private int count_succesful_remap = 0;
	private int count_flexibilty = 0;
	
	
	String phase = "none";
	
	
	//a semaphore to prevent Multiple access in multithreaded environments
	private Semaphore semaphore = new Semaphore(1);
	
	
	private final int alpha;
	
	//Saves the number of Mapping for each node
	private LinkedHashMap<SubstrateNode, Integer> mapCounts;
	
	//The (sorted) list if VNodes
	private ArrayList<VirtualNode> vNodes;
	
	//A list if flexible use VNodes for Levelflexibility
	private ArrayList<VirtualNode> flexNodes;
	
	//The list Map of Candidates
	private LinkedHashMap<VirtualNode, ArrayList<SubstrateNode>> candidates;
	
	public MlsNodeMapping_BestFit_Lower(SubstrateNetwork sNet,
			boolean subsNodeOverload, int alpha) {
		super(sNet, subsNodeOverload);
		this.alpha = alpha;
		
		if(!MLSUtils.checkMLSResource(sNet)) {
			throw new Error("One or more SubstrateNode has no MLSResource");
		}
	}

	@Override
	public boolean nodeMapping(VirtualNetwork vNet) {		
		//0. Check wther we have MLS-Demands and clean up
		//Lock semaphore (prevent parallel access to object)
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			System.out.println("Waiting thread has been interuppted at NodeMapping");
			return false;
		}
		
		//Check whether we have MLSDemands
		if(!MLSUtils.checkMLSDemands(vNet)) {
			throw new Error("One or more VirtualNode has no MLSDemand");
		}
		mapCounts = new LinkedHashMap<SubstrateNode, Integer>();
		vNodes = new ArrayList<VirtualNode>();
		flexNodes = new ArrayList<VirtualNode>();
		candidates = new LinkedHashMap<VirtualNode, ArrayList<SubstrateNode>>();
		nodeMapping = new LinkedHashMap<VirtualNode, SubstrateNode>();
		
		//--------------------------------------BUILD CANDIDATESLIST---------------------------------
		
		//1. Look up the candidates for any VirtualNode
		if(!analyze(vNet)) {
			System.out.println("Mapping abortet due not mappable VirtualNode (Analyze Phase)");
			semaphore.release();
			return false;	//1.1 No Candidates for one -> ABORT
		}
		//--------------------------------------------PREMAPPING--------------------------------------
		
		//2. Map any Node with only one Candidate (PreMapping)
		if(!preMapping()) {
			System.out.println("Mapping abortet due not mappable VirtualNode (PreMapping Phase)");
			semaphore.release();
			return false;	//2.1 Mapping not possible -> ABORT
		}
		
		//----------------------SORTING----------------------------------------------------------------
		
		
		if(candidates.size() > 0) {
			//3. Sort the VNodes
			//Create the list of VNodes from the CandidatesMap
			for(VirtualNode vn : candidates.keySet()) {
				vNodes.add(vn);
			}
			
			//Sort the List
			Collections.sort(vNodes, new vnodeComperator());
			//System.out.println("VNodes"+vNodes);
			
			
			//-------------------------------MAPPING----------------------------------------------------
			
			if(!mapNetwork()) {
				System.out.println("Mapping abortet due not mappable VirtualNode (Mapping Phase)");
				semaphore.release();
				return false;	//(At least) one Node not mapped -> ABORT
			}
		}
		
		semaphore.release();
		//System.out.println(nodeMapping);
		return true;
	}

	/**
	 * This method creates the candidates list
	 * 
	 * @return true if everything is ok, false if there is one node without a candidate
	 */
	private boolean analyze(VirtualNetwork vNet) {
		//Walk through any vNode and check the substrate for fitting nodes
		for(VirtualNode vn : vNet.getVertices()) {
			ArrayList<SubstrateNode> tmpCandidates = findCandidates(vn, false);
			
			//Check if the list is empty
			if(tmpCandidates.isEmpty()) {
				tmpCandidates = findCandidates(vn, true); //Check whether flexibility helps
				if(tmpCandidates.isEmpty()) {
					System.out.println("Fail(Analyze): "+vn.getName()+":"+MLSUtils.getMLSDemand(vn).toString());
					count_abort_analyze++;
					return false; //then  no candiates for this node
				}
			}
			
			//Add the list to our global list
			candidates.put(vn, tmpCandidates);
		}
		return true;
	}
	

	/**
	 * Maps all 1-to-1 mappings
	 * 
	 * @return true if ok, false otherwise
	 */
	private boolean preMapping(){
		this.phase = "PreMapping";
		//Create an array of virtual Nodes, because we get problem if we remove an object from a list that's
		//iteratet in the same loop (potentially)
		//VirtualNode[] arr_vn = (VirtualNode[]) candidates.keySet().toArray();
		ArrayList<VirtualNode> tmpList = new ArrayList<VirtualNode>();
		for(VirtualNode vn : candidates.keySet())
			tmpList.add(vn);
		
		for(VirtualNode vn : tmpList) {
			ArrayList<SubstrateNode> tmpCandidates = candidates.get(vn);
			//look if we have only one!
			if(tmpCandidates.size() == 1 || flexNodes.contains(vn)) {
				//Sort the list
				snodeComperator sComp = new snodeComperator();
				sComp.setDemand(MLSUtils.getMLSDemand(vn));	//Set the Demand (needed for sorting)
				Collections.sort(tmpCandidates, sComp); //Sort the list
				
				SubstrateNode sn = tmpCandidates.get(0);
				if(!mapNode(vn, sn)) {
					//Mapping failed? Try flex!
					if(!map_flex(vn)) {
						System.out.println("Fail(PreMapping): "+vn.getName()+":"+MLSUtils.getMLSDemand(vn).toString());
						count_abort_premapping++;
						//Mapping failed
						return false;
					}
				}
			}
		}
		
		//Remove all mapped VNodes from the Candidateslist
		for(VirtualNode vn : nodeMapping.keySet()) {
			candidates.remove(vn);
		}
		
		return true;
	}
	
	
	/**
	 * This Method tries to map all remaining {@link VirtualNode}'s
	 * 
	 * @return true if successful, false otherwise
	 */
	private boolean mapNetwork() {
		this.phase = "Mapping";
		snodeComperator sComp = new snodeComperator();
		
		//4. For every VNode in List
		for(VirtualNode vn : vNodes) {
			boolean isMapped = false;
			//4.1 Sort the candidates
			sComp.setDemand(MLSUtils.getMLSDemand(vn));	//Set the Demand (needed for sorting)
			ArrayList<SubstrateNode> tmpCandidates = candidates.get(vn); //Get the list
			Collections.sort(tmpCandidates, sComp); //Sort the list
			//System.out.println("SNodes"+tmpCandidates);
			
			
			//4.2 Walk through list of candidates and try to map	
			for(SubstrateNode sn : tmpCandidates) {
				if(mapNode(vn, sn)) {
					//could mapped -> Next VNode
					isMapped = true;
					break;
				}
				//could not mapped on this -> Next sNode candidate
			}
			//4.3 Try to map the VNode with remapping another
			if(!isMapped && map_remapping(vn, tmpCandidates)) {
				isMapped = true;
			}
			
			//4.4 Try with flexibility 
			this.phase = "FlexMapping";
			if(!isMapped && map_flex(vn)) {
				isMapped = true;
			}
			
			if(!isMapped) {
				//All candidate sNodes are not suitable
				//could not mapped on any -> Abort
				count_abort_mapping++;
				System.out.println("Fail(Mapping): "+vn.getName()+":"+MLSUtils.getMLSDemand(vn).toString());
				return false;
			}
		}
		
		return true;
	}
	

	/**
	 * The method tries to map the vnode with remapping of another
	 * 
	 * @param vn {@link VirtualNode}
	 * @param localCandidates List of Candidate {@link SubstrateNode}
	 * @return true if ok otherwise false
	 */
	private boolean map_remapping(VirtualNode vnode, ArrayList<SubstrateNode> localCandidates) {
		this.phase = "ReMapping";
		ArrayList<VirtualNode>  relevantMappings = new ArrayList<VirtualNode>();
		
		//1. find all Mappings for the candidates of the new vnode
		for(SubstrateNode sn : localCandidates) {	//go through all candidates for the new vnode
			for(VirtualNode vn : nodeMapping.keySet()) {	//Go through all mappings 
				//the list of global candidates no more contains vnodes from premapping
				if(this.candidates.containsKey(vn)) {
					SubstrateNode act_snode = nodeMapping.get(vn);	//Actual test snode from mappings
					//check if names Match
					if(sn.getName().equals(act_snode.getName())) {
						//put it to the relevant Mappings
						relevantMappings.add(vn);
					}
				}
			}
		}
		
		//2. walk through all relevant mappings
		for(VirtualNode vn : relevantMappings) {
			//save the old Mapping for this vnode (for recovery)
			SubstrateNode old_sn = nodeMapping.get(vn);
			SubstrateNode new_sn = null;
			//2.1 look wether the vnode frees enough resources
			if(!checkEnoughCPU(vnode, vn, old_sn)) {
				// --> No, next mapping
				continue;
			}
			//2.2 unmap the the old vnode
			if(!unmapNode(vn, old_sn)) {
				throw new Error("Unmapping of VNode:"+vn.getName()+" on SNode:"+old_sn.getName()+" failed. Can't happen! STOP");
			}
			
			//2.3 Try remap the old VNode
			boolean isRemapped = false;
			for(SubstrateNode remap_sn : this.candidates.get(vn)) {
				//Exclude the SNode we want to free
				if(!remap_sn.getName().equals(old_sn.getName())) {
					if(mapNode(vn, remap_sn)) {
						//Remap worked
						isRemapped = true;
						new_sn = remap_sn;	//Save it for undo
						System.out.println("Remapping_old: v:"+vn.getName()+" on s:"+remap_sn.getName());
						break;
					}
				}
			}
			
			
			if(!isRemapped) {	//If remapping of the node not possible, undo unmap
				System.out.println("Remapping_undo_noremap: v:"+vn.getName()+" on s:"+old_sn.getName());
				if(!mapNode(vn, old_sn)) {
					throw new Error("(UNDO) Mapping of VNode:"+vn.getName()+" on SNode:"+old_sn.getName()+" failed. Can't happen! STOP");
				}
			} if(isRemapped) { //2.4 Try do map new vnode
				//if(true) {
				if(mapNode(vnode, old_sn)) {
					//Mapping worked :)
					System.out.println("Remapping_new: v:"+vnode.getName()+" on s:"+old_sn.getName());
					count_succesful_remap++;
					return true;
				}
				else {
					//Failed -> Undo Changes
					System.out.println("Remapping_undo_new: v:"+vn.getName()+" on s:"+new_sn.getName());
					if(!unmapNode(vn, new_sn)) {
						throw new Error("(UNDO) Unmapping of VNode:"+vn.getName()+" on SNode:"+new_sn.getName()+" failed. Can't happen! STOP");
					}
					System.out.println("Remapping_undo_old: v:"+vn.getName()+" on s:"+old_sn.getName());
					if(!mapNode(vn, old_sn)) {
						throw new Error("(UNDO) Mapping of VNode:"+vn.getName()+" on SNode:"+old_sn.getName()+" failed. Can't happen! STOP");
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Try to map a node with Flexibility option
	 * @param vn {@link VirtualNode}
	 * @return true if ok, otherwise false
	 */
	private boolean map_flex(VirtualNode vn) {
		ArrayList<SubstrateNode> tmpCandidates = findCandidates(vn, true);
		if(!tmpCandidates.isEmpty()) {
			//Sort the new Candidates
			snodeComperator sComp = new snodeComperator();
			sComp.setDemand(MLSUtils.getMLSDemand(vn));	//Set the Demand (needed for sorting)
			Collections.sort(tmpCandidates, sComp); //Sort the list
			
			
			for(SubstrateNode sn : tmpCandidates) {
				if(!mapNode(vn, sn)) {
					//If we got here something impossible (maybe parallel access to scenario) is happend!
					//System.out.println("Got: "+MLSUtils.getMLSDemand(vn).getDemand());
					throw new Error("Mapping("+this.phase+") "+vn+" on "+sn+" with Flexibilty failed! This Can't happen at this Point! ");
				}
				return true;
			}
		}
		
		//IsEmpty can happen: If Flex is already used or even with decreased SecDemand no Candidate found
		return false;
	}

	
	/**
	 * Returns a list of possible Candidates for this Node
	 * @param vn {@link VirtualNode}
	 * @param flexibility if Levelflexibility should be used
	 * @return {@link ArrayList} of {@link SubstrateNode}
	 */
	private ArrayList<SubstrateNode> findCandidates(VirtualNode vn, boolean flexibility) {
		ArrayList<SubstrateNode> tmpCandidates = new ArrayList<SubstrateNode>();
		//If flexibility is wanted decrement the level, only allow one decrement per node
		if(flexibility && !flexNodes.contains(vn)) {
			//decrement the demanded Security
			MLSDemand dem = MLSUtils.getMLSDemand(vn);
			int demLevel = dem.getDemand();
			
			//If the level is 0, stop, we can't decrement it
			if(demLevel != 0) {
				demLevel--;
				//set the new Level to the MLSDemand
				dem.setDemand(demLevel);
				//System.out.println("Set: "+demLevel);
				flexNodes.add(vn);	//mark the node as flexible
			}
		}
		
		for(SubstrateNode sn : super.sNet.getVertices()) {
			if(NodeLinkAssignation.isMappable(vn, sn) && isAvailable(sn)) {
				tmpCandidates.add(sn);
			}
		}
		
		
		return tmpCandidates;
	}
	

	/**
	 * This Method tries to map the given {@link VirtualNode} on given {@link SubstrateNode}
	 * @param vn {@link VirtualNode}
	 * @param sn {@link SubstrateNode}
	 * @return true if mapping ok, false otherwise
	 */
	private boolean mapNode(VirtualNode vn,  SubstrateNode sn) {
		//Check if the Node is still available for mapping
		if(!isAvailable(sn))
			return false; //Mapping not possible
		
		//Try map it
		if(!NodeLinkAssignation.vnm(vn, sn)) 
			return false; //Mapping failed or not possible
		
		if(flexNodes.contains(vn)) {
			count_flexibilty++;
			System.out.println("Mapped("+this.phase+") VNode "+vn+" on SNode "+sn+" with felexibility.");
		}
		//If Mapping is successfull
		//System.out.println(vn+":"+MLSUtils.getMLSDemand(vn));
		//System.out.println(sn+":"+MLSUtils.getMLSResource(sn));
		nodeMapping.put(vn, sn); //Add it to the mappinglist
		incrementCounter(sn);
		
		return true;
	}
	
	/**
	 * This Method tries to unmap the given {@link VirtualNode} on given {@link SubstrateNode}
	 * @param vn {@link VirtualNode}
	 * @param sn {@link SubstrateNode}
	 * @return true if mapping ok, false otherwise
	 */
	private boolean unmapNode(VirtualNode vn, SubstrateNode sn) {
		for(AbstractDemand ad : vn) {
			for(AbstractResource ar : sn) {
				if(ar.accepts(ad))
					if(!ad.free(ar))
						//if free failed stop and return false
						return false;
			}
		}
		
		nodeMapping.remove(vn);
		decrementCounter(sn);
		return true;
	}
	


	/**
	 * Checks if the {@link SubstrateNode} is available for mapping
	 * 
	 * @param sn Given {@link SubstrateNode}
	 * @return true if mapping is possible
	 */
	private boolean isAvailable(SubstrateNode sn) {
		Integer c = mapCounts.get(sn);
		
		//if undefined, there is no mapping
		if(c == null)
			return true;
		
		//check if counter has already reached alpha 
		if(c >= alpha) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * This Method increments the mappingcounter for the given {@link SubstrateNode}
	 * 
	 * @param sn {@link SubstrateNode}
	 */
	private void incrementCounter(SubstrateNode sn) {
		Integer c = mapCounts.get(sn);

		//if undefined, there is no mapping
		if(c == null) {
			mapCounts.put(sn, new Integer(1));
		} else {
			mapCounts.put(sn, new Integer(c+1));
		}
					
	}
	
	/**
	 * This Method decrements the mappingcounter for the given {@link SubstrateNode}
	 * 
	 * @param sn {@link SubstrateNode}
	 */
	private void decrementCounter(SubstrateNode sn) {
		Integer c = mapCounts.get(sn);

		//if undefined, there is no mapping
		if(c == null) {
			throw new Error("Decrementing a counter with no mapping is not passobile for SNode:"+sn.getName());
		} else {
			mapCounts.put(sn, new Integer(c-1));
		}
		
	}
	
	/**
	 * The Comparator for Sorting the {@link VirtualNode}'s
	 * 
	 * @author Fabian Kokot
	 *
	 */
	private class vnodeComperator implements Comparator<VirtualNode>{

		@Override
		public int compare(VirtualNode o1, VirtualNode o2) {
			int result1 = calculate(MLSUtils.getMLSDemand(o1));
			int result2 = calculate(MLSUtils.getMLSDemand(o2));
			
			if(result1 > result2)
				return -1;
			if(result1 < result2)
				return 1;
			
			return 0;
		}
		
		private int calculate(MLSDemand d) {
			int result;
			result = d.getDemand() - d.getProvide() + d.getCategories().size();
			return result;
		}
		
	}

	/**
	 * The Comparator for Sorting the {@link SubstrateNode}'s
	 * 
	 * @author Fabian Kokot
	 *
	 */
	private class snodeComperator implements Comparator<SubstrateNode>{

		private MLSDemand d;
		

		@Override
		public int compare(SubstrateNode o1, SubstrateNode o2) {
			int result1 = calculate(MLSUtils.getMLSResource(o1));
			int result2 = calculate(MLSUtils.getMLSResource(o2));
			
			if(result1 > result2)
				return 1;
			if(result1 < result2)
				return -1;
			
			return 0;
		}
		
		private int calculate(MLSResource s) {
			int result = 0;
			result += (s.getProvide() - d.getDemand()); //First Term
			result += (d.getProvide() - s.getDemand()); //Second Term
			result += (s.getCategories().size() - d.getCategories().size()); //last term
			return result;
		}

		public void setDemand(MLSDemand d) {
			this.d = d;
		}
		
	}

	/**
	 * This method determinate, based on CPU, whether the old VNode frees enough resources 
	 * @param vn_new {@link VirtualNode} that should be mapped
	 * @param vn_old {@link VirtualNode} that should be remapped to another SNode
	 * @param sn {@link SubstrateNode} where the new should mapped und the old removed
	 * @return true if determination is ok, otherwise false
	 */
	private boolean checkEnoughCPU(VirtualNode vn_new, VirtualNode vn_old, SubstrateNode sn) {
		//only if we have CPUResources
		double cpu_new = getDemandedCPU(vn_new);
		double cpu_old = getDemandedCPU(vn_old);
		double cpu_snode = getFreeCPU(sn);
		
		//Check if the old node frees enough CPU
		if((cpu_snode + cpu_old) >= cpu_new)
			return true;
		
		return false;
	}
	
	
	/**
	 * Returns the demanded CPU from a given {@link VirtualNode}
	 * @param vn {@link VirtualNode}
	 * @return the Demanded CPU-Value or 0.0d if no {@link CpuDemand} found
	 */
	private double getDemandedCPU(VirtualNode vn) {
		//default is 0.0
		double demCPU = 0.0;
		for(AbstractDemand ad : vn.get()) {
			if(ad instanceof CpuDemand) {
				demCPU = ((CpuDemand)ad).getDemandedCycles();
				break;
			}
		}
		return demCPU;
	}
	
	/**
	 * Returns the free CPU from a given {@link SubstrateNode}
	 * @param sn {@link SubstrateNode}
	 * @return the free CPU-Value or 0.0d if no {@link CpuResource} found
	 */
	private double getFreeCPU(SubstrateNode sn) {
		double freeCPU = 0.0;
		for(AbstractResource ar : sn.get()) {
			if(ar instanceof CpuResource) {
				freeCPU = ((CpuResource)ar).getAvailableCycles();
			}
		}
		return freeCPU;
	}
	
	@Override
	public Map<String, Double> exportEvaluationData() {
		
		//The whole Scenario needs to be mapped with one Object
		
		LinkedHashMap<String, Double> ret = new LinkedHashMap<String, Double>();
		ret.put("BestFitAbortAnalyze", new Double(count_abort_analyze));
		ret.put("BestFitAbortPreMapping", new Double(count_abort_premapping));
		ret.put("BestFitAbortMapping", new Double(count_abort_mapping));
		ret.put("BestFitSuccessfulRemap", new Double(count_succesful_remap));
		ret.put("BestFitSuccessfulFlex", new Double(count_flexibilty));
		
		
		//reset the counter
		//TODO: Make the really thread safe!!! (No it isn't)
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			System.out.println("Waiting thread has been interuppted at NodeMapping");
			return null;
		}
		
		count_abort_analyze = 0;
		count_abort_premapping = 0;
		count_abort_mapping = 0;
		count_succesful_remap = 0;
		count_flexibilty = 0;
		
		semaphore.release();
		
		return ret;
		

	}
	
	
	
	
}
