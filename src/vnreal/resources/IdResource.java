/* ***** BEGIN LICENSE BLOCK *****
 * Copyright (C) 2010-2011, The VNREAL Project Team.
 * 
 * This work has been funded by the European FP7
 * Network of Excellence "Euro-NF" (grant agreement no. 216366)
 * through the Specific Joint Developments and Experiments Project
 * "Virtual Network Resource Embedding Algorithms" (VNREAL). 
 *
 * The VNREAL Project Team consists of members from:
 * - University of Wuerzburg, Germany
 * - Universitat Politecnica de Catalunya, Spain
 * - University of Passau, Germany
 * See the file AUTHORS for details and contact information.
 * 
 * This file is part of ALEVIN (ALgorithms for Embedding VIrtual Networks).
 *
 * ALEVIN is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License Version 3 or later
 * (the "GPL"), or the GNU Lesser General Public License Version 3 or later
 * (the "LGPL") as published by the Free Software Foundation.
 *
 * ALEVIN is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * or the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and
 * GNU Lesser General Public License along with ALEVIN; see the file
 * COPYING. If not, see <http://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ***** */
package vnreal.resources;

import javax.swing.JOptionPane;

import vnreal.AdditionalConstructParameter;
import vnreal.ExchangeParameter;
import vnreal.constraints.AbstractConstraint;
import vnreal.constraints.INodeConstraint;
import vnreal.demands.AbstractDemand;
import vnreal.demands.DemandVisitorAdapter;
import vnreal.demands.IdDemand;
import vnreal.gui.GUI;
import vnreal.mapping.Mapping;
import vnreal.network.NetworkEntity;
import vnreal.network.Node;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;

/**
 * A resource for an identifier.
 * 
 * N.b.: This resource is applicable for links and nodes.
 * 
 * @author Michael Duelli
 * @author Vlad Singeorzan
 * @since 2010-09-10
 */
@AdditionalConstructParameter(
		parameterNames = {"sNetwork"},
		parameterGetters = {"getsNetwork"}
		)
public final class IdResource extends AbstractResource implements
		INodeConstraint {
	private String id;
	private final SubstrateNetwork sNetwork;


	public IdResource(Node<? extends AbstractConstraint> owner, SubstrateNetwork sNetwork) {
		super(owner);
		this.sNetwork = sNetwork;
	}
	
	public IdResource(Node<? extends AbstractConstraint> owner, SubstrateNetwork sNetwork, String name) {
		super(owner, name);
		this.sNetwork = sNetwork;
	}

	@ExchangeParameter
	public void setId(String id) {
		if (checkUniquness(id)) {
			this.id = id;
		} else {
			if (GUI.isInitialized())
				JOptionPane.showMessageDialog(GUI.getInstance(),
						"IdResource is not unique.", "Error",
						JOptionPane.ERROR_MESSAGE);
			System.err.println("IdResource: id is not unique.");
		}
	}

	@ExchangeParameter
	public String getId() {
		return id;
	}

	
	
	public SubstrateNetwork getsNetwork() {
		return sNetwork;
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
			public boolean visit(IdDemand dem) {
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
			public boolean visit(IdDemand dem) {
				if (getMapping(dem) != null) {
					return getMapping(dem).unregister();
				} else
					return false;
			}
		};
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("IdResource: id=");
		sb.append(getId());
		if (getMappings().size() > 0) {
			sb.append(" occupied by ");
			sb.append(getMappingsString());
		}
		return sb.toString();
	}

	private boolean checkUniquness(String id) {
		if (sNetwork != null)
			for (SubstrateNode sn : sNetwork.getVertices()) {
				for (AbstractResource res : sn.get()) {
					if (res instanceof IdResource) {
						if (((IdResource) res).getId().equals(id)
								&& res != this) {
							return false;
						}
					}
				}
			}
		return true;
	}

	@Override
	public AbstractResource getCopy(
			NetworkEntity<? extends AbstractConstraint> owner) {

		IdResource clone = new IdResource(
				(Node<? extends AbstractConstraint>) owner, sNetwork, this.getName());
		clone.id = id;

		return clone;
	}
}
