package vnreal.generators;

import javax.swing.JPanel;

import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.virtual.VirtualNetwork;

/**
 * This interface describes the functionality that has to be implemented by a
 * network topology generator in order to be usable from the scenario wizard.
 * 
 * A generator implementing this interface must be constructible using the
 * default constructor.
 * 
 * @author Philip Huppert
 */
public interface INetworkGenerator {
	/**
	 * @return the name of the network generator.
	 */
	public String getName();

	/**
	 * @return a string describing the network generator and its current
	 *         state/configuration.
	 */
	public String toString();

	/**
	 * Generate a {@link SubstrateNetwork} using the network generator's current
	 * configuration.
	 * 
	 * @param autoUnregisterConstraints
	 * @return the generated {@link SubstrateNetwork}.
	 */
	public SubstrateNetwork generateSubstrateNetwork(
			boolean autoUnregisterConstraints);

	/**
	 * Generate a {@link VirtualNetwork} using the network generator's current
	 * configuration.
	 * 
	 * @param level
	 *            of the {@link VirtualNetwork}.
	 * @return the generated {@link VirtualNetwork}.
	 */
	public VirtualNetwork generateVirtualNetwork(int level);

	/**
	 * @return a {@link JPanel} that allows the user to configure the network
	 *         generator. It will be displayed in its preferred size.
	 */
	public JPanel getConfigurationDialog();

	/**
	 * @return a cloned instance of the current {@link INetworkGenerator}.
	 */
	public INetworkGenerator clone();
}
