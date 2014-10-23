package vnreal.generators;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle;
import javax.swing.SpinnerNumberModel;
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
 * This class implements a simple {@link INetworkGenerator} for generating star
 * topologies.
 * 
 * @author Philip Huppert
 */
public class StarGenerator implements INetworkGenerator {
	private static final int NODE_COUNT_INCR = 1;
	private static final int NODE_COUNT_MAX = 1000000;
	private static final int NODE_COUNT_MIN = 0;
	private static final Dimension DIALOG_SIZE = new Dimension(225, 100);
	private static final String NODE_COUNT_LBL = "Node count (n):";

	private int numNodes = 3;
	private JPanel dialog = null;

	public int getNumNodes() {
		return this.numNodes;
	}

	public void setNumNodes(int numNodes) {
		this.numNodes = numNodes;
	}

	@Override
	public String getName() {
		return "Star Generator";
	}

	@Override
	public String toString() {
		return String.format("Star Generator (n=%d)",
				this.numNodes);
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
		}.generateGraph(this.numNodes);
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
		}.generateGraph(this.numNodes);
	}

	@Override
	public JPanel getConfigurationDialog() {
		if (this.dialog != null) {
			return this.dialog;
		}
		JPanel dialog = new JPanel();

		JLabel lblNumNodes = new JLabel(NODE_COUNT_LBL, JLabel.TRAILING);
		final JSpinner valNumNodes = new JSpinner(new SpinnerNumberModel(
				this.numNodes, NODE_COUNT_MIN, NODE_COUNT_MAX, NODE_COUNT_INCR));
		valNumNodes.setValue(this.numNodes);
		valNumNodes.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				numNodes = (int) valNumNodes.getValue();
			}
		});
		lblNumNodes.setLabelFor(valNumNodes);

		GroupLayout layout = new GroupLayout(dialog);
		dialog.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout
				.createSequentialGroup()
				.addComponent(lblNumNodes)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(valNumNodes)
		);
		layout.setVerticalGroup(layout
				.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(lblNumNodes)
				.addComponent(valNumNodes)
		);

		dialog.setPreferredSize(DIALOG_SIZE);

		this.dialog = dialog;
		return dialog;
	}

	@Override
	public INetworkGenerator clone() {
		StarGenerator res = new StarGenerator();
		res.numNodes = this.numNodes;
		return res;
	}

	private abstract class Generator<T extends Network, N extends Node, L extends Link> {
		protected abstract T createNetwork();
		protected abstract N createNode();
		protected abstract L createLink();

		public T generateGraph(int numNodes) {
			T network = this.createNetwork();
			// Generate center.
			N center = this.createNode();
			center.setCoordinateX(0);
			center.setCoordinateY(0);

			// Generate and connect nodes/leaves.
			List<N> nodes = new ArrayList<N>(numNodes);
			for (int i = 0; i < numNodes; i++) {
				double j = Math.PI * 2 / numNodes * i;
				N node = this.createNode();
				node.setCoordinateX(Math.sin(j) * 100);
				node.setCoordinateY(Math.cos(j) * 100);
				nodes.add(node);

				network.addEdge(this.createLink(), node, center);
				network.addEdge(this.createLink(), center, node);
			}

			return network;
		}
	}

}
