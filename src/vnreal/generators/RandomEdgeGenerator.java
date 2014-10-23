package vnreal.generators;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import mulavito.utils.distributions.UniformStream;
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
 * This class implements an {@link INetworkGenerator} that generates edges
 * randomly based on a constant, user-defined probability.
 * 
 * @author Philip Huppert
 */
public class RandomEdgeGenerator implements INetworkGenerator {

	private static final Dimension DIALOG_SIZE = new Dimension(250, 120);

	private static final String SEED_LBL = "Seed (s):";
	private static final String SEED_TIP = "<html>Seed for the algortihm's random number generator.<br>If the given seed is not numeric, the hashcode of the entered string will be used.</html>";

	private static final String PROBABILITY_LBL = "Probability (p):";
	private static final String PROBABILITY_TIP = "Probability an edge between any two nodes is created.";
	private static final double PROBABILITY_INCR = 0.1;
	private static final double PROBABILITY_MAX = 1.0;
	private static final double PROBABILITY_MIN = 0.0;

	private static final int NODE_COUNT_INCR = 1;
	private static final int NODE_COUNT_MAX = 1000000;
	private static final int NODE_COUNT_MIN = 0;
	private static final String NODE_COUNT_LBL = "Node count (n):";
	private static final String NODE_COUNT_TIP = "The number of nodes to generate.";

	private int numNodes = 1;
	private double probability = 0.5;
	private long seed = new Random().nextLong();
	private JPanel dialog = null;

	public int getNumNodes() {
		return numNodes;
	}

	public void setNumNodes(int numNodes) {
		this.numNodes = numNodes;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	@Override
	public String getName() {
		return "Random Edge Generator";
	}

	@Override
	public String toString() {
		return String.format("Random Edge Generator (n=%d; p=%f; s=%d)",
				this.numNodes, this.probability, this.seed);
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
		}.generateGraph(this.numNodes, this.probability, this.seed);
	}

	@Override
	public VirtualNetwork generateVirtualNetwork(final int level) {
		return new Generator<VirtualNetwork, VirtualNode, VirtualLink>() {
			
			@Override
			protected VirtualNode createNode() {
				return new VirtualNode(level);
			}
			
			@Override
			protected VirtualNetwork createNetwork() {
				return new VirtualNetwork(level);
			}
			
			@Override
			protected VirtualLink createLink() {
				return new VirtualLink(level);
			}
		}.generateGraph(this.numNodes, this.probability, this.seed);
	}

	@Override
	public JPanel getConfigurationDialog() {
		if (this.dialog != null) {
			return this.dialog;
		}
		JPanel dialog = new JPanel();

		// Node count
		JLabel lblNumNodes = new JLabel(NODE_COUNT_LBL);
		final JSpinner valNumNodes = new JSpinner(new SpinnerNumberModel(
						this.numNodes, NODE_COUNT_MIN, NODE_COUNT_MAX, NODE_COUNT_INCR));
		valNumNodes.setToolTipText(NODE_COUNT_TIP);
		valNumNodes.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				numNodes = (int) valNumNodes.getValue();
			}
		});
		lblNumNodes.setLabelFor(valNumNodes);

		// Probability
		JLabel lblProbability = new JLabel(PROBABILITY_LBL);
		final JSpinner valProbability = new JSpinner(new SpinnerNumberModel(
						this.probability, PROBABILITY_MIN, PROBABILITY_MAX, PROBABILITY_INCR));
		valProbability.setToolTipText(PROBABILITY_TIP);
		valProbability.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				probability = (double) valProbability.getValue();
			}
		});
		lblProbability.setLabelFor(valProbability);

		// Seed
		JLabel lblSeed = new JLabel(SEED_LBL);
		final JTextField valSeed = new JTextField(String.valueOf(this.seed));
		valSeed.setToolTipText(SEED_TIP);
		valSeed.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				this.update();
			}
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				this.update();
			}
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				this.update();
			}
			private void update() {
				try {
					seed = Long.parseLong(valSeed.getText());
				} catch (NumberFormatException ex) {
					seed = valSeed.getText().hashCode();
				}
			}
		});
		lblSeed.setLabelFor(valSeed);

		GroupLayout layout = new GroupLayout(dialog);
		dialog.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup()
						.addComponent(lblNumNodes, GroupLayout.Alignment.TRAILING)
						.addComponent(lblProbability, GroupLayout.Alignment.TRAILING)
						.addComponent(lblSeed, GroupLayout.Alignment.TRAILING)
				)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
						layout.createParallelGroup()
						.addComponent(valNumNodes, GroupLayout.Alignment.LEADING)
						.addComponent(valProbability, GroupLayout.Alignment.LEADING)
						.addComponent(valSeed, GroupLayout.Alignment.LEADING)
				)
		);
		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lblNumNodes)
						.addComponent(valNumNodes)
				)
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lblProbability)
						.addComponent(valProbability)
				)
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lblSeed)
						.addComponent(valSeed)
				)
		);

		dialog.setPreferredSize(DIALOG_SIZE);

		this.dialog = dialog;
		return dialog;
	}

	@Override
	public INetworkGenerator clone() {
		RandomEdgeGenerator res = new RandomEdgeGenerator();
		res.probability = this.probability;
		res.numNodes = this.numNodes;
		res.seed = this.seed;
		return res;
	}
	
	private abstract class Generator<T extends Network, N extends Node, L extends Link> {
		private static final double SCALE_Y = 100.0;
		private static final double SCALE_X = 100.0;

		protected abstract T createNetwork();
		protected abstract N createNode();
		protected abstract L createLink();
		
		public T generateGraph(int numNodes, double probability, long seed) {
			UniformStream rnd = new UniformStream();
			rnd.setSeed(seed);

			T network = this.createNetwork();

			// Add nodes
			List<N> nodes = new ArrayList<N>(numNodes);
			for (int i = 0; i < numNodes; i++) {
				N node = this.createNode();
				node.setCoordinateX(rnd.nextDouble() * SCALE_X);
				node.setCoordinateY(rnd.nextDouble() * SCALE_Y);
				nodes.add(node);
				network.addVertex(node);
			}

			// Add edges
			for (N a : nodes) {
				for (N b : nodes) {
					if (a == b) {
						continue;
					}
					if (rnd.nextDouble() < probability) {
						network.addEdge(this.createLink(), a, b);
					}
				}
			}

			return network;
		}
	}

}
