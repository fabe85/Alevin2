package vnreal.generators;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Map;
import java.util.Map.Entry;
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

import mulavito.graph.generators.WaxmanGraphGenerator;
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
 * This class implements an {@link INetworkGenerator} that uses the
 * {@link WaxmanGraphGenerator} to generate a topology.
 * 
 * @author Philip Huppert
 */
public class WaxmanGenerator implements INetworkGenerator {

	private static final Dimension DIALOG_SIZE = new Dimension(250, 155);

	private static final String SEED_LBL = "Seed (s):";
	private static final String SEED_TIP = "<html>Seed for the algortihm's random number generator.<br>If the given seed is not numeric, the hashcode of the entered string will be used.</html>";

	private static final double BETA_MIN = -1000.0;
	private static final double BETA_INCR = 0.1;
	private static final double BETA_MAX = 1.0;
	private static final String BETA_LBL = "Beta (\u03B2):";
	private static final String BETA_TIP = "<html>beta &lt;= 1<br>An increase in beta will increase<br>the ratio of long edges relative to short edges.</html>";

	private static final String ALPHA_LBL = "Alpha (\u03B1):";
	private static final String ALPHA_TIP = "<html>alpha &gt; 0<br>An increase in alpha will increase<br>the number of edges in the graph.</html>";
	private static final double ALPHA_INCR = 0.1;
	private static final double ALPHA_MAX = 1000.0;
	private static final double ALPHA_MIN = Double.MIN_VALUE;

	private static final int NODE_COUNT_INCR = 1;
	private static final int NODE_COUNT_MAX = 1000000;
	private static final int NODE_COUNT_MIN = 0;
	private static final String NODE_COUNT_LBL = "Node count (n):";
	private static final String NODE_COUNT_TIP = "The number of nodes to generate.";

	private int numNodes = 1;
	private double alpha = 1.0;
	private double beta = 0.5;
	private long seed = new Random().nextLong();
	private JPanel dialog = null;

	public int getNumNodes() {
		return numNodes;
	}

	public void setNumNodes(int numNodes) {
		this.numNodes = numNodes;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public double getBeta() {
		return beta;
	}

	public void setBeta(double beta) {
		this.beta = beta;
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	@Override
	public String getName() {
		return "Waxman Generator";
	}

	@Override
	public String toString() {
		return String.format("Waxman Generator (n=%d; alpha=%f; beta=%f, s=%d)",
				this.numNodes, this.alpha, this.beta, this.seed);
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
		}.generateGraph(this.numNodes, this.alpha, this.beta, this.seed);
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
		}.generateGraph(this.numNodes, this.alpha, this.beta, this.seed);
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

		// Alpha
		JLabel lblAlpha = new JLabel(ALPHA_LBL);
		final JSpinner valAlpha = new JSpinner(new SpinnerNumberModel(
						this.alpha, ALPHA_MIN, ALPHA_MAX, ALPHA_INCR));
		valAlpha.setToolTipText(ALPHA_TIP);
		valAlpha.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				alpha = (double) valAlpha.getValue();
			}
		});
		lblAlpha.setLabelFor(valAlpha);

		// Beta
		JLabel lblBeta = new JLabel(BETA_LBL);
		final JSpinner valBeta = new JSpinner(new SpinnerNumberModel(
						this.beta, BETA_MIN, BETA_MAX, BETA_INCR));
		valBeta.setToolTipText(BETA_TIP);
		valBeta.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				beta = (double) valBeta.getValue();
			}
		});
		lblBeta.setLabelFor(valBeta);

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
						.addComponent(lblAlpha, GroupLayout.Alignment.TRAILING)
						.addComponent(lblBeta, GroupLayout.Alignment.TRAILING)
						.addComponent(lblSeed, GroupLayout.Alignment.TRAILING)
				)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
						layout.createParallelGroup()
						.addComponent(valNumNodes, GroupLayout.Alignment.LEADING)
						.addComponent(valAlpha, GroupLayout.Alignment.LEADING)
						.addComponent(valBeta, GroupLayout.Alignment.LEADING)
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
						.addComponent(lblAlpha)
						.addComponent(valAlpha)
				)
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lblBeta)
						.addComponent(valBeta)
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
		WaxmanGenerator res = new WaxmanGenerator();
		res.alpha = this.alpha;
		res.beta = this.beta;
		res.numNodes = this.numNodes;
		res.seed = this.seed;
		return res;
	}
	
	private abstract class Generator<T extends Network, N extends Node, L extends Link> {
		protected abstract T createNetwork();
		protected abstract N createNode();
		protected abstract L createLink();
		
		public T generateGraph(int numNodes, double alpha, double beta, long seed) {
			T network = this.createNetwork();

			for (int i = 0; i < numNodes; i++) {
				network.addVertex(this.createNode());
			}

			WaxmanGraphGenerator<N, L> sgg =
					new WaxmanGraphGenerator<N, L>(alpha, beta, false, seed);
			sgg.generate(network);

			Map<N, Point2D> spos = sgg.getPositions();
			for (Entry<N, Point2D> e : spos.entrySet()) {
				N node = e.getKey();
				Point2D point = e.getValue();
				node.setCoordinateX(100.0 * point.getX());
				node.setCoordinateY(100.0 * point.getY());
			}

			return network;
		}
	}

}
