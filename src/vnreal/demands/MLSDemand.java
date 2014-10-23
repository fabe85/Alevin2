package vnreal.demands;

import java.util.ArrayList;

import vnreal.AdditionalConstructParameter;
import vnreal.ExchangeParameter;
import vnreal.constraints.ILinkConstraint;
import vnreal.constraints.INodeConstraint;
import vnreal.network.NetworkEntity;
import vnreal.network.Node;
import vnreal.resources.AbstractResource;
import vnreal.resources.MLSResource;
import vnreal.resources.ResourceVisitorAdapter;

/**
 * This Demand is for labeling Nodes with MLS Labels
 * Technically this is a resource and a demand in one
 * @author Fabian Kokot
 * @since 2012-05-02
 */
@AdditionalConstructParameter(
		parameterNames = {"demand", "provide", "categories"},
		parameterGetters = {"getDemand", "getProvide", "getCategories"}
		)
public class MLSDemand extends AbstractDemand implements INodeConstraint, ILinkConstraint{

	//demanded and Provided Level
	private int mDemand, mProvide;
	//The Categories of the Node
	private ArrayList<String> mCategories;
	
	
	/**
	 * Creates a MLSDemand
	 * @param ne A Node
	 * @param demand Demanded level of Security
	 * @param provide Provided level of Security
	 * @param categories The categories
	 */
	public MLSDemand(Node<? extends AbstractDemand> ne, Integer demand, Integer provide, ArrayList<String> categories) {
		super(ne);
		this.mDemand = demand;
		this.mProvide = provide;
		this.mCategories = categories;
	}
	
	public MLSDemand(Node<? extends AbstractDemand> ne, Integer demand, Integer provide, ArrayList<String> categories, String name) {
		super(ne, name);
		this.mDemand = demand;
		this.mProvide = provide;
		this.mCategories = categories;
	}
	
	
	//Only some getter and setter 
	

	@ExchangeParameter
	public Integer getDemand() {
		return mDemand;
	}



	@ExchangeParameter
	public void setDemand(Integer demand) {
		this.mDemand = demand;
	}



	@ExchangeParameter
	public Integer getProvide() {
		return mProvide;
	}



	@ExchangeParameter
	public void setProvide(Integer provide) {
		this.mProvide = provide;
	}



	@SuppressWarnings("unchecked")
	@ExchangeParameter
	public ArrayList<String> getCategories() {
		return (ArrayList<String>) mCategories.clone();
	}



	@ExchangeParameter
	public void setCategories(ArrayList<String> categories) {
		this.mCategories = categories;
	}




	@Override
	protected ResourceVisitorAdapter createAcceptsVisitor() {
		return new ResourceVisitorAdapter() {
			@Override
			public boolean visit(MLSResource res) {
				return true;
			}
		};
	}

	@Override
	protected ResourceVisitorAdapter createFulfillsVisitor() {
		return new ResourceVisitorAdapter() {
			@Override
			public boolean visit(MLSResource res) {
				//if labels are not set, stop here
				if(mCategories == null)
					throw new NullPointerException("MLS Categories not set!");
				
				//Only ok when cross Checking is ok and the demanded categories are met
				if(mDemand <= res.getProvide()  && mProvide >= res.getDemand() && checkCategories(res.getCategories()))
					return true;
				else
					return false;
			}
		};
	}

	@Override
	public boolean occupy(AbstractResource res) {
		return res.getOccupyVisitor().visit(this);
	}

	@Override
	public boolean free(AbstractResource res) {
		return res.getFreeVisitor().visit(this);
	}

	@Override
	public String toString() {
		String out = "Demand: D="+mDemand+"; P="+mProvide+"; Categories=";
		for(String s : mCategories) {
			out += s+",";
		}
		
		return out;
	}
	
	/**
	 * Checks if the demanded categories are provided by the resource
	 * @param provided Provided Categories by the resource
	 * @return true if ok
	 */
	private boolean checkCategories(ArrayList<String> provided) {
		//Return false if one demand are not met
		for(String s : mCategories) {
			if(!provided.contains(s))
				return false;
		}
		return true;
	}


	@Override
	public AbstractDemand getCopy(NetworkEntity<? extends AbstractDemand> owner) {
		@SuppressWarnings("unchecked")
		MLSDemand clone = new MLSDemand((Node<? extends AbstractDemand>)owner, mDemand, mProvide, (ArrayList<String>) getCategories().clone(), this.getName());
		return clone;
	}
	

}
