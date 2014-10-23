package tests.topologies.embeddingalgos;

import vnreal.algorithms.AbstractAlgorithm;
import vnreal.network.NetworkStack;

/**
 * This class represents a placeholder for an embedding algorithm instance that
 * will be present in the future. The user may configure the embedding algorithm
 * by providing configuration data to a concrete implementation of this class.
 * Once the user has a {@link NetworkStack} he may retrieve an instance of the
 * embedding algorithm.
 * 
 * @author Philip Huppert
 */
public abstract class FutureEmbeddingAlgorithm {
	/**
	 * Retrieve the {@link AbstractAlgorithm} promised by this class.
	 * 
	 * @param nstack
	 *            to run {@link AbstractAlgorithm} on.
	 * @return the {@link AbstractAlgorithm} promised by this class.
	 */
	public abstract AbstractAlgorithm getAlgorithm(NetworkStack nstack);

	/**
	 * @return an array of objects that all implement
	 *         {@link FutureEmbeddingAlgorithm}.
	 */
	public static FutureEmbeddingAlgorithm[] getAll() {
		return new FutureEmbeddingAlgorithm[] {
				new Isomorphism(),
				new Pathsplitting(),
				new Stressbased()
		};
	}
}
