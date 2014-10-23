package vnreal.resources;

import vnreal.constraints.AbstractConstraint;
import vnreal.constraints.ILinkConstraint;
import vnreal.constraints.INodeConstraint;
import vnreal.demands.AbstractDemand;
import vnreal.demands.DemandVisitorAdapter;
import vnreal.demands.NullDemand;
import vnreal.mapping.Mapping;
import vnreal.network.Link;
import vnreal.network.NetworkEntity;
import vnreal.network.Node;


/**
 * This Ressource is only for Testing purpose, 
 * when we have no Ressource on Node or Link an need one to usse some Metrics
 * It always says is ok...
 * 
 * @author Fabian Kokot
 */
public class NullResource extends AbstractResource implements INodeConstraint,
		ILinkConstraint {

	public NullResource(Node<? extends AbstractResource> ne) {
		super(ne);
	}
	
	public NullResource(Node<? extends AbstractResource> ne, String name) {
		super(ne, name);
	}
	
	public NullResource(Link<? extends AbstractResource> ne) {
		super(ne);
	}
	
	public NullResource(Link<? extends AbstractResource> ne, String name) {
		super(ne, name);
	}
	

	@Override
	protected DemandVisitorAdapter createOccupyVisitor() {
		return new DemandVisitorAdapter() {
			@Override
			public boolean visit(NullDemand dem) {
				if (fulfills(dem)) {
					new Mapping(dem, getThis());
					return true;
				} else
					return false;
			}
		};
	}

	@Override
	protected DemandVisitorAdapter createFreeVisitor() {
		return new DemandVisitorAdapter() {
			@Override
			public boolean visit(NullDemand dem) {
				if (getMapping(dem) != null) {
					return getMapping(dem).unregister();
				} else
					return false;
			}
		};
	}

	@Override
	public boolean accepts(AbstractDemand dem) {
		return dem.getAcceptsVisitor().visit(this);
	}

	@Override
	public boolean fulfills(AbstractDemand dem) {
		return dem.getFulfillsVisitor().visit(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public AbstractResource getCopy(
			NetworkEntity<? extends AbstractConstraint> owner) {
		if(owner.getClass().getSimpleName().contains("Node"))
			return new NullResource((Node<? extends AbstractResource>)owner, this.getName());
		else if(owner.getClass().getSimpleName().contains("Link"))
			return new NullResource((Link<? extends AbstractResource>)owner, this.getName());
		throw new Error("The class: "+owner.getClass()+" is invalid for NullResource");
	}

	@Override
	public String toString() {
		return "Null Ressource on "+this.getOwner().getId();
	}

}
