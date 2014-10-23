package vnreal.generators;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import vnreal.network.Link;
import vnreal.network.Network;
import vnreal.network.Node;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

/**
 * This class implements a {@link INetworkGenerator} for generating the
 * "fat-tree"/clos topology described in the paper
 * "Al-Fares, et al 2008: A Scalable, Commodity Data Center Network Architecture"
 * 
 * @author Philip Huppert
 */
public class AlFaresGenerator implements INetworkGenerator {
	private static final String GENERATE_CLIENTS_CLBL = "generate clients";
	private static final String GENERATE_CLIENTS_LBL = "Clients:";
	private static final int PODS_COUNT_MAX = 1000;
	private static final int PODS_COUNT_MIN = 2;
	private static final Dimension DIALOG_SIZE = new Dimension(225, 100);
	private static final String PODS_COUNT_LBL = "Pod count (p):";

	private int numPods = 4;
	private boolean clientsGenerated = false;
	private JPanel dialog = null;

	public int getNumPods() {
		return this.numPods;
	}

	public void setNumPods(int numPods) {
		this.numPods = numPods;
	}

	public boolean isClientsGenerated() {
		return clientsGenerated;
	}

	public void setClientsGenerated(boolean clientsGenerated) {
		this.clientsGenerated = clientsGenerated;
	}

	@Override
	public String getName() {
		return "Al-Fares Generator";
	}

	@Override
	public String toString() {
		return String.format("Al-Fares Generator (p=%d; with%s clients)",
				this.numPods, this.clientsGenerated ? "" : "out");
	}

	@Override
	public SubstrateNetwork generateSubstrateNetwork(
			final boolean autoUnregisterConstraints) {
		return new Generator<SubstrateNetwork, SubstrateNode, SubstrateLink>() {

			@Override
			protected SubstrateNetwork createNetwork() {
				return new SubstrateNetwork(autoUnregisterConstraints);
			}

			@Override
			protected SubstrateNode createNode() {
				return new SubstrateNode();
			}

			@Override
			protected SubstrateLink createLink() {
				return new SubstrateLink();
			}
		}.generateGraph(this.numPods, this.clientsGenerated);
	}

	@Override
	public VirtualNetwork generateVirtualNetwork(final int level) {
		return new Generator<VirtualNetwork, VirtualNode, VirtualLink>() {

			@Override
			protected VirtualNetwork createNetwork() {
				return new VirtualNetwork(level);
			}

			@Override
			protected VirtualNode createNode() {
				return new VirtualNode(level);
			}

			@Override
			protected VirtualLink createLink() {
				return new VirtualLink(level);
			}
		}.generateGraph(this.numPods, this.clientsGenerated);
	}
	
	@Override
	public JPanel getConfigurationDialog() {
		if (this.dialog != null) {
			return this.dialog;
		}
		JPanel dialog = new JPanel();

		JLabel lblNumPods = new JLabel(PODS_COUNT_LBL, JLabel.TRAILING);
		final JSpinner valNumPods = new JSpinner(new EvenSpinner(
				this.numPods, PODS_COUNT_MIN, PODS_COUNT_MAX));
		valNumPods.setValue(this.numPods);
		valNumPods.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				numPods = (int) valNumPods.getValue();
			}
		});
		lblNumPods.setLabelFor(valNumPods);

		JLabel lblClientsGenerated = new JLabel(
				GENERATE_CLIENTS_LBL, JLabel.TRAILING);
		final JCheckBox valClientsGenerated = new JCheckBox(
				GENERATE_CLIENTS_CLBL, this.clientsGenerated);
		valClientsGenerated.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				clientsGenerated = valClientsGenerated.isSelected();
			}
		});
		lblClientsGenerated.setLabelFor(valClientsGenerated);

		GroupLayout layout = new GroupLayout(dialog);
		dialog.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout
				.createSequentialGroup()
				.addGroup(layout
						.createParallelGroup()
						.addComponent(lblNumPods)
						.addComponent(lblClientsGenerated)
						)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout
						.createParallelGroup()
						.addComponent(valNumPods)
						.addComponent(valClientsGenerated)
				)
		);
		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(layout
						.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lblNumPods)
						.addComponent(valNumPods)
				)
				.addGroup(layout
						.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lblClientsGenerated)
						.addComponent(valClientsGenerated)
				)
		);

		dialog.setPreferredSize(DIALOG_SIZE);

		this.dialog = dialog;
		return dialog;
	}

	@Override
	public INetworkGenerator clone() {
		AlFaresGenerator res = new AlFaresGenerator();
		res.numPods = this.numPods;
		res.clientsGenerated = this.clientsGenerated;
		return res;
	}

	private class EvenSpinner implements SpinnerModel {
		private Set<ChangeListener> listener = new HashSet<ChangeListener>();
		private int max;
		private int min;
		private int value;
	
		public EvenSpinner(int value, int min, int max) {
			if (value % 2 != 0) {
				throw new IllegalArgumentException("Uneven value provided.");
			}
	
			if (min % 2 != 0 || max % 2 != 0) {
				throw new IllegalArgumentException("Uneven bounds provided.");
			}
	
			this.min = min;
			this.max = max;
			this.value = value;
		}
	
		@Override
		public void addChangeListener(ChangeListener l) {
			this.listener.add(l);
		}
	
		@Override
		public Object getNextValue() {
			if (this.value < this.max) {
				this.value += 2;
			}
			return this.value;
		}
	
		@Override
		public Object getPreviousValue() {
			if (this.value > this.min) {
				this.value -= 2;
			}
			return this.value;
		}
	
		@Override
		public Object getValue() {
			return this.value;
		}
	
		@Override
		public void removeChangeListener(ChangeListener l) {
			this.listener.remove(l);
		}
	
		@Override
		public void setValue(Object value) {
			int newValue = (int) value;
			if (newValue % 2 != 0) {
				throw new IllegalArgumentException("Uneven value provided.");
			}
			this.value = newValue;
	
			ChangeEvent e = new ChangeEvent(this);
			for (ChangeListener l : this.listener) {
				l.stateChanged(e);
			}
		}
	}

	private abstract class Generator<T extends Network, N extends Node, L extends Link> {
		private static final double CLIENT_Y = 1.0;
		private static final double EDGE_Y = 0.66;
		private static final double AGGREGATION_Y = 0.33;
		private static final double COORDINATE_SCALE = 100.0;

		protected abstract T createNetwork();
		protected abstract N createNode();
		protected abstract L createLink();

		public T generateGraph(int numPods, boolean clientsGenerated) {
			if (numPods % 2 != 0) {
				throw new IllegalArgumentException("Uneven number of pods provided.");
			}

			int numClientsPerEdgeSwitch = numPods / 2;
			int numSwitchesPerPod = numPods / 2;
			int numCoreSwitches = (numPods / 2) * (numPods / 2);

			T network = this.createNetwork();

			// Create core switches.
			List<N> coreSwitches = new ArrayList<N>(numCoreSwitches);
			for (int n = 0; n < numCoreSwitches; n++) {
				N node = this.createNode();
				node.setCoordinateY(0);
				node.setCoordinateX((n + 1) * (1.0 / (numCoreSwitches + 1)) * COORDINATE_SCALE);
				network.addVertex(node);
				coreSwitches.add(node);
			}

			// Create pods.
			double podWidth = (1.0 / numPods);
			for (int p = 0; p < numPods; p++) {
				double podPos = p * podWidth;

				List<N> aggregationSwitches = new ArrayList<N>(numSwitchesPerPod);
				List<N> edgeSwitches = new ArrayList<N>(numSwitchesPerPod);

				// Create pod switches.
				double switchWidth = podWidth / (numSwitchesPerPod + 1);
				for (int s = 0; s < numSwitchesPerPod; s++) {
					double switchPos = podPos + (s + 1) * switchWidth;

					// Create aggregation switch.
					N aggregationSwitch = this.createNode();
					aggregationSwitch.setCoordinateY(AGGREGATION_Y * COORDINATE_SCALE);
					aggregationSwitch.setCoordinateX(switchPos * COORDINATE_SCALE);
					network.addVertex(aggregationSwitch);
					aggregationSwitches.add(aggregationSwitch);

					// Connect aggregation to core switch.
					for (int c = s; c < numCoreSwitches; c += numSwitchesPerPod) {
						N coreSwitch = coreSwitches.get(c);
						network.addEdge(this.createLink(), coreSwitch, aggregationSwitch);
						network.addEdge(this.createLink(), aggregationSwitch, coreSwitch);
					}

					// Create edge switch.
					N edgeSwitch = this.createNode();
					edgeSwitch.setCoordinateY(EDGE_Y * COORDINATE_SCALE);
					edgeSwitch.setCoordinateX(switchPos * COORDINATE_SCALE);
					network.addVertex(edgeSwitch);
					edgeSwitches.add(edgeSwitch);

					if (clientsGenerated) {
						// Create clients for each edge switch.
						double clientHeight = 1.0 / numClientsPerEdgeSwitch;
						for (int c = 0; c < numClientsPerEdgeSwitch; c++) {
							N client = this.createNode();
							client.setCoordinateY((CLIENT_Y + c * clientHeight) * COORDINATE_SCALE);
							client.setCoordinateX(switchPos * COORDINATE_SCALE);
							network.addVertex(client);
	
							// Connect client to edge switch.
							network.addEdge(this.createLink(), edgeSwitch, client);
							network.addEdge(this.createLink(), client, edgeSwitch);
						}
					}
				}

				// Interconnect edge with aggregation.
				for (N aggregationSwitch : aggregationSwitches) {
					for (N edgeSwitch : edgeSwitches) {
						network.addEdge(this.createLink(), aggregationSwitch, edgeSwitch);
						network.addEdge(this.createLink(), edgeSwitch, aggregationSwitch);
					}
				}
			}

			return network;
		}
	}
}
