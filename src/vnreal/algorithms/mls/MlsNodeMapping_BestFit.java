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
import vnreal.demands.MLSDemand;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.MLSResource;

/**
 * This class creates Mappings based on {@link MLSDemand} and {@link MLSResource} with a BestFit approach. <br />
 * 
 * Alpha is is maximum number of {@link VirtualNode}'s of one {@link VirtualNetwork} mapped on one {@link SubstrateNode}.
 * 
 * @author Fabian Kokot
 *
 */
public class MlsNodeMapping_BestFit extends AbstractNodeMapping {
	
	//Some Stuff for statistics
	private int count_abort_analyze = 0;
	private int count_abort_premapping = 0;
	private int count_abort_mapping = 0;
	
	
	
	//a semaphore to prevent Multiple access in multithreaded environments
	private Semaphore semaphore = new Semaphore(1);
	
	
	private final int alpha;
	
	//Saves the number of Mapping for each node
	private LinkedHashMap<SubstrateNode, Integer> mapCounts;
	
	//The (sorted) list if VNodes
	private ArrayList<VirtualNode> vNodes;
	
	//The list Map of Candidates
	private LinkedHashMap<VirtualNode, ArrayList<SubstrateNode>> candidates;
	
	public MlsNodeMapping_BestFit(SubstrateNetwork sNet,
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
			//System.out.println("VNodes-alt:"+vNodes);
			Collections.sort(vNodes, new vnodeComperator());
			//System.out.println("VNodes-neu:"+vNodes);
			
			
			//-------------------------------MAPPING----------------------------------------------------
			
			if(!mapNetwork()) {
				System.out.println("Mapping abortet due not mappable VirtualNode (Mapping Phase)");
				semaphore.release();
				return false;	//(At least) one Node not mapped -> ABORT
			}
		}
		
		semaphore.release();
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
			ArrayList<SubstrateNode> tmpCandidates = new ArrayList<SubstrateNode>();
			for(SubstrateNode sn : super.sNet.getVertices()) {
				if(NodeLinkAssignation.isMappable(vn, sn)) {
					tmpCandidates.add(sn);
				}
			}
			
			//Check if the list is empty
			if(tmpCandidates.isEmpty()) {
				System.out.println("Fail(Analyze): "+vn.getName()+":"+MLSUtils.getMLSDemand(vn).toString());
				count_abort_analyze++;
				return false; //then  no candiates for this node
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
		//Create an array of virtual Nodes, because we get problem if we remove an object from a list that's
		//iteratet in the same loop (potentially)
		//VirtualNode[] arr_vn = (VirtualNode[]) candidates.keySet().toArray();
		ArrayList<VirtualNode> tmpList = new ArrayList<VirtualNode>();
		for(VirtualNode vn : candidates.keySet())
			tmpList.add(vn);
		
		for(VirtualNode vn : tmpList) {
			ArrayList<SubstrateNode> tmpCandidates = candidates.get(vn);
			//look if we have only one!
			if(tmpCandidates.size() == 1) {
				SubstrateNode sn = tmpCandidates.get(0);
				if(!mapNode(vn, sn)) {
					System.out.println("Fail(PreMapping): "+vn.getName()+":"+MLSUtils.getMLSDemand(vn).toString());
					count_abort_premapping++;
					//Mapping failed
					return false;
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
			
			if(!isMapped) {
				//All candidate sNodes are not suitable
				//could not mapped on any -> ABORT
				count_abort_mapping++;
				System.out.println("Fail(Mapping): "+vn.getName()+":"+MLSUtils.getMLSDemand(vn).toString());
				return false;
			}
		}
		
		return true;
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
		
		//If Mapping is successfull
		//System.out.println(vn+":"+MLSUtils.getMLSDemand(vn));
		//System.out.println(sn+":"+MLSUtils.getMLSResource(sn));
		nodeMapping.put(vn, sn); //Add it to the mappinglist
		incrementCounter(sn);
		
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
			//result = d.getDemand() + (d.getDemand() - d.getProvide()) + d.getCategories().size();
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

	@Override
	public Map<String, Double> exportEvaluationData() {
		
		//The whole Scenario needs to be mapped with one Object
		
		LinkedHashMap<String, Double> ret = new LinkedHashMap<String, Double>();
		ret.put("BestFitAbortAnalyze", new Double(count_abort_analyze));
		ret.put("BestFitAbortPreMapping", new Double(count_abort_premapping));
		ret.put("BestFitAbortMapping", new Double(count_abort_mapping));
		
		
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
		
		semaphore.release();
		
		return ret;
		

	}
	
	
	
	
}
