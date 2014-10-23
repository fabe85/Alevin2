package tests.topologies.embeddingalgos;

import vnreal.algorithms.AbstractAlgorithm;
import vnreal.algorithms.isomorphism.AdvancedSubgraphIsomorphismAlgorithm;
import vnreal.algorithms.isomorphism.SubgraphIsomorphismAlgorithm;
import vnreal.algorithms.isomorphism.SubgraphIsomorphismStackAlgorithm;
import vnreal.network.NetworkStack;

/**
 * Placeholder for {@link AdvancedSubgraphIsomorphismAlgorithm}.
 * 
 * @author Philip Huppert
 */
public class Isomorphism extends FutureEmbeddingAlgorithm {
	private static final boolean DEFAULT_USE_ENERGY_RESOURCE = false;

	private boolean useEnergyResource;

	public Isomorphism() {
		this(DEFAULT_USE_ENERGY_RESOURCE);
	}

	public Isomorphism(boolean useEnergyResource) {
		this.useEnergyResource = useEnergyResource;
	}

	@Override
	public AbstractAlgorithm getAlgorithm(NetworkStack nstack) {
		SubgraphIsomorphismAlgorithm algo = new AdvancedSubgraphIsomorphismAlgorithm(
				this.useEnergyResource);
		return new SubgraphIsomorphismStackAlgorithm(nstack, algo);
	}

	@Override
	public String toString() {
		return "Isomorphism [useEnergyResource=" + useEnergyResource + "]";
	}
}
