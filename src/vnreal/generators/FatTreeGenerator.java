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
 * This class implements a {@link INetworkGenerator} for generating universal
 * fat-trees as described in the paper
 * "Leiserson, Fat-Trees: Universal Networks for Hardware-Efficient
 * Supercomputing".
 * 
 * @author Philip Huppert
 */
public class FatTreeGenerator implements INetworkGenerator {
	private static final int NODE_COUNT_INCR = 1;
	private static final int NODE_COUNT_MAX = 1000000;
	private static final int NODE_COUNT_MIN = 0;
	private static final Dimension DIALOG_SIZE = new Dimension(225, 100);
	private static final String NODE_COUNT_LBL = "Node count (n):";
	private static final String ROOT_CAPACITY_LBL = "Root capacity (c):";
	private static final int ROOT_CAPACITY_MIN = 1;
	private static final int ROOT_CAPACITY_MAX = 1000000;
	private static final Number ROOT_CAPACITY_INCR = 1;

	private int numNodes = 16;
	private int rootCapacity = 4;
	private JPanel dialog = null;

	public int getNumNodes() {
		return this.numNodes;
	}

	public void setNumNodes(int numNodes) {
		this.numNodes = numNodes;
	}

	public int getRootCapacity() {
		return this.rootCapacity;
	}

	public void setRootCapacity(int rootCapacity) {
		this.rootCapacity = rootCapacity;
	}

	@Override
	public String getName() {
		return "Fat Tree Generator";
	}

	@Override
	public String toString() {
		return String.format("Fat Tree Generator (n=%d; c=%d)",
				this.numNodes, this.rootCapacity);
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
		}.generateGraph(this.numNodes, this.rootCapacity);
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
		}.generateGraph(this.numNodes, this.rootCapacity);
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

		JLabel lblRootCapacity = new JLabel(ROOT_CAPACITY_LBL, JLabel.TRAILING);
		final JSpinner valRootCapacity = new JSpinner(new SpinnerNumberModel(
				this.numNodes, ROOT_CAPACITY_MIN, ROOT_CAPACITY_MAX, ROOT_CAPACITY_INCR));
		valRootCapacity.setValue(this.rootCapacity);
		valRootCapacity.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				rootCapacity = (int) valRootCapacity.getValue();
			}
		});
		lblRootCapacity.setLabelFor(valRootCapacity);

		GroupLayout layout = new GroupLayout(dialog);
		dialog.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout
				.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblNumNodes)
						.addComponent(lblRootCapacity)
				)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup()
						.addComponent(valNumNodes)
						.addComponent(valRootCapacity)
				)
		);
		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lblNumNodes)
						.addComponent(valNumNodes)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lblRootCapacity)
						.addComponent(valRootCapacity)
				)
		);

		dialog.setPreferredSize(DIALOG_SIZE);

		this.dialog = dialog;
		return dialog;
	}

	@Override
	public INetworkGenerator clone() {
		FatTreeGenerator res = new FatTreeGenerator();
		res.rootCapacity = this.rootCapacity;
		res.numNodes = this.numNodes;
		return res;
	}

	private abstract class Generator<T extends Network, N extends Node, L extends Link> {
		private static final double COORDINATE_SCALE = 100.0;

		protected abstract T createNetwork();
		protected abstract N createNode();
		protected abstract L createLink();

		public T generateGraph(int numNodes, int rootCapacity) {
			T network = this.createNetwork();
			int depth = (int) Math.ceil(Math.log(numNodes)/Math.log(2));
			List<N> nodes = new ArrayList<N>();

			// Create internal nodes (switches).
			for (int level = 0; level < depth; level++) {
				for (int n = 0; n < Math.pow(2, level); n++) {
					N node = this.createNode();
					node.setCoordinateY(this.calculateY(level, depth) * COORDINATE_SCALE);
					node.setCoordinateX(this.calculateX(level, n) * COORDINATE_SCALE);
					nodes.add(node);
					network.addVertex(node);
				}
			}

			// Create leaves (CPUs).
			for (int n = 0; n < numNodes; n++) {
				N node = this.createNode();
				node.setCoordinateY(this.calculateY(depth, depth) * COORDINATE_SCALE);
				node.setCoordinateX(this.calculateX(depth, n) * COORDINATE_SCALE);
				nodes.add(node);
				network.addVertex(node);
			}

			// Create edges (links).
			for (int iNode = 0; iNode < nodes.size(); iNode++) {
				N node = nodes.get(iNode);

				int iLeftChild = iNode * 2 + 1;
				N leftChild = (iLeftChild < nodes.size()) ? nodes.get(iLeftChild) : null;

				int iRightChild = iNode * 2 + 2;
				N rightChild = (iRightChild < nodes.size()) ? nodes.get(iRightChild) : null;

				int level = (int) Math.floor(Math.log(iNode + 1) / Math.log(2));

				int k = level + 1;
				int n = numNodes;
				int w = rootCapacity;

				int a = (int) Math.ceil(n / Math.pow(2, k));
				int b = (int) Math.ceil(w / Math.pow(2, 2*k/3));
				int numLinks = Math.min(a, b);
				System.err.printf("k=%d; a=%d; b=%d%n", k, a, b);

				for (int l = 0; l < numLinks; l++) {
					if (leftChild != null) {
						network.addEdge(this.createLink(), node, leftChild);
						network.addEdge(this.createLink(), leftChild, node);
					}
					if (rightChild != null) {
						network.addEdge(this.createLink(), node, rightChild);
						network.addEdge(this.createLink(), rightChild, node);
					}
				}
			}

			return network;
		}

		private double calculateY(int currentLevel, int treeDepth) {
			// root at y=0, leaves at y=1
			return (1.0 / (treeDepth + 1)) * currentLevel;
		}

		private double calculateX(int currentLevel, int currentNode) {
			// root at x=0.5, children of root at x=0.33 and x=0.66
			return (1.0 / (Math.pow(2, currentLevel) + 1)) * (currentNode + 1);
		}
	}
}
