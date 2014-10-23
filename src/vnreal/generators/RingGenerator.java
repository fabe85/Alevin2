package vnreal.generators;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JComboBox;
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
 * This class implements a simple {@link INetworkGenerator} for generating ring
 * topologies.
 * 
 * @author Philip Huppert
 */
public class RingGenerator implements INetworkGenerator {
	private static final int NODE_COUNT_INCR = 1;
	private static final int NODE_COUNT_MAX = 1000000;
	private static final int NODE_COUNT_MIN = 0;
	private static final Dimension DIALOG_SIZE = new Dimension(225, 100);
	private static final String LINK_DIRECTION_LBL = "Link direction (d):";
	private static final String NODE_COUNT_LBL = "Node count (n):";

	private int numNodes = 3;
	private Direction linkDirection = Direction.Clockwise;
	private JPanel dialog = null;

	public enum Direction {
		Clockwise,
		Counterclockwise,
		Both
	};

	public int getNumNodes() {
		return this.numNodes;
	}

	public void setNumNodes(int numNodes) {
		this.numNodes = numNodes;
	}

	public Direction getLinkDirection() {
		return this.linkDirection;
	}

	public void setLinkDirection(Direction linkDirection) {
		this.linkDirection = linkDirection;
	}

	@Override
	public String getName() {
		return "Ring Generator";
	}

	@Override
	public String toString() {
		return String.format("Ring Generator (n=%d; d=%s)",
				this.numNodes, this.linkDirection);
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
		}.generateGraph(this.numNodes, this.linkDirection);
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
		}.generateGraph(this.numNodes, this.linkDirection);
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

		JLabel lblLinkDirection = new JLabel(LINK_DIRECTION_LBL, JLabel.TRAILING);
		final JComboBox<Direction> valLinkDirection =
				new JComboBox<Direction>(Direction.values());
		valLinkDirection.setSelectedItem(this.linkDirection);
		valLinkDirection.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				linkDirection = (Direction) valLinkDirection.getSelectedItem();
			}
		});
		lblLinkDirection.setLabelFor(valLinkDirection);

		GroupLayout layout = new GroupLayout(dialog);
		dialog.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout
				.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblNumNodes)
						.addComponent(lblLinkDirection)
				)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup()
						.addComponent(valNumNodes)
						.addComponent(valLinkDirection)
				)
		);
		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lblNumNodes)
						.addComponent(valNumNodes)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lblLinkDirection)
						.addComponent(valLinkDirection)
				)
		);

		dialog.setPreferredSize(DIALOG_SIZE);

		this.dialog = dialog;
		return dialog;
	}

	@Override
	public INetworkGenerator clone() {
		RingGenerator res = new RingGenerator();
		res.linkDirection = this.linkDirection;
		res.numNodes = this.numNodes;
		return res;
	}

	private abstract class Generator<T extends Network, N extends Node, L extends Link> {
		protected abstract T createNetwork();
		protected abstract N createNode();
		protected abstract L createLink();

		public T generateGraph(int numNodes, Direction linkDirection) {
			T network = this.createNetwork();

			// Generate nodes.
			List<N> nodes = new ArrayList<N>(numNodes);
			for (int i = 0; i < numNodes; i++) {
				double j = Math.PI * 2 / numNodes * i;
				N node = this.createNode();
				node.setCoordinateX(Math.sin(j) * 100);
				node.setCoordinateY(Math.cos(j) * 100);
				nodes.add(node);
			}

			// Generate edges.
			N prev = nodes.get(numNodes - 1);
			for (N node : nodes) {
				if (node == prev) {
					continue;
				}

				switch (linkDirection) {
					case Clockwise:
					case Both:
						network.addEdge(this.createLink(), node, prev);
					default:
						break;
				}

				switch (linkDirection) {
					case Counterclockwise:
					case Both:
						network.addEdge(this.createLink(), prev, node);
					default:
						break;
				}

				prev = node;
			}

			return network;
		}
	}

}
