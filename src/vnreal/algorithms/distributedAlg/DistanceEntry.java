package vnreal.algorithms.distributedAlg;

import java.util.List;

import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNode;

public class DistanceEntry {
	private SubstrateNode n1;
	private SubstrateNode n2;
	private List<SubstrateLink> path;
	private double distance;

	public DistanceEntry(SubstrateNode n1, SubstrateNode n2,
			List<SubstrateLink> path, double distance) {
		this.setN1(n1);
		this.setN2(n2);
		this.setPath(path);
		this.setDistance(distance);
	}

	public SubstrateNode getN1() {
		return n1;
	}

	public void setN1(SubstrateNode n1) {
		this.n1 = n1;
	}

	public SubstrateNode getN2() {
		return n2;
	}

	public void setN2(SubstrateNode n2) {
		this.n2 = n2;
	}

	public List<SubstrateLink> getPath() {
		return path;
	}

	public void setPath(List<SubstrateLink> path) {
		this.path = path;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
}