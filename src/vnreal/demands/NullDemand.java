package vnreal.demands;

import vnreal.constraints.AbstractConstraint;
import vnreal.constraints.ILinkConstraint;
import vnreal.constraints.INodeConstraint;
import vnreal.network.Link;
import vnreal.network.NetworkEntity;
import vnreal.network.Node;
import vnreal.resources.AbstractResource;
import vnreal.resources.NullResource;
import vnreal.resources.ResourceVisitorAdapter;

/**
 *  This Demand is only for testing purposes if we have a node or link without a demand and need one for some using some metrics
 *  It always returns true.
 *  
 * @author Fabian Kokot
 *
 */
public class NullDemand extends AbstractDemand implements ILinkConstraint,
		INodeConstraint {

	public NullDemand(Node<? extends AbstractConstraint> ne) {
		super(ne);
	}
	
	public NullDemand(Node<? extends AbstractConstraint> ne, String name) {
		super(ne, name);
	}
	
	public NullDemand(Link<? extends AbstractConstraint> ne) {
		super(ne);
	}
	
	public NullDemand(Link<? extends AbstractConstraint> ne, String name) {
		super(ne, name);
	}

	@Override
	protected ResourceVisitorAdapter createAcceptsVisitor() {
		return new ResourceVisitorAdapter() {
			@Override
			public boolean visit(NullResource res) {
				return true;
			}
		};
	}

	@Override
	protected ResourceVisitorAdapter createFulfillsVisitor() {
		return new ResourceVisitorAdapter() {
			@Override
			public boolean visit(NullResource res) {
				return true;
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
	public AbstractDemand getCopy(NetworkEntity<? extends AbstractDemand> owner) {
		if(owner.getClass().getSimpleName().contains("Node"))
			return new NullDemand((Node<? extends AbstractDemand>)owner, this.getName());
		else if(owner.getClass().getSimpleName().contains("Link"))
			return new NullDemand((Link<? extends AbstractDemand>)owner, this.getName());
		throw new Error("The class: "+owner.getClass()+" is invalid for NullResource");
	}

	@Override
	public String toString() {
		return "Null Demand on "+this.getOwner().getId();
	}

}
