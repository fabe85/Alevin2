package vnreal.resources;

import vnreal.constraints.AbstractConstraint;
import vnreal.constraints.INodeConstraint;
import vnreal.demands.AbstractDemand;
import vnreal.demands.BaseStationDemand;
import vnreal.demands.DemandVisitorAdapter;
import vnreal.mapping.Mapping;
import vnreal.network.NetworkEntity;
import vnreal.network.Node;

public class BaseStationResource extends AbstractResource implements INodeConstraint {

	public BaseStationResource(Node<? extends AbstractConstraint> owner) {
		super(owner);
	}
	
	public BaseStationResource(Node<? extends AbstractConstraint> owner, String name) {
		super(owner, name);
	}

	@Override
	public boolean accepts(AbstractDemand dem) {
		return dem.getAcceptsVisitor().visit(this);
	}

	@Override
	public boolean fulfills(AbstractDemand dem) {
		return dem.getFulfillsVisitor().visit(this);
	}

	@Override
	protected DemandVisitorAdapter createOccupyVisitor() {
		return new DemandVisitorAdapter() {
			@Override
			public boolean visit(BaseStationDemand dem) {
				if (fulfills(dem)) {
					//occupiedCycles += MiscelFunctions.roundThreeDecimals(dem
					//		.getDemandedCycles());
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
			public boolean visit(BaseStationDemand dem) {
				if (getMapping(dem) != null) {
					//occupiedCycles -= MiscelFunctions.roundThreeDecimals(dem
					//		.getDemandedCycles());
					return getMapping(dem).unregister();
				} else
					return false;
			}
		};
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BaseStation resource");
		if (getMappings().size() > 0)
			sb.append(getMappingsString());
		return sb.toString();
	}

	@Override
	public AbstractResource getCopy(
			NetworkEntity<? extends AbstractConstraint> owner) {

		BaseStationResource clone = new BaseStationResource(
				(Node<? extends AbstractConstraint>) owner, this.getName());
		//clone.cycles = cycles;
		//clone.occupiedCycles = occupiedCycles;

		return clone;
	}

}
