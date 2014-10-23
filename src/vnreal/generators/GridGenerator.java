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
 * This class implements a simple {@link INetworkGenerator} for generating grid
 * topologies.
 * 
 * @author Philip Huppert
 */
public class GridGenerator implements INetworkGenerator {
	private static final int DIMENSION_INCR = 1;
	private static final int DIMENSION_MAX = 1000000;
	private static final int DIMENSION_MIN = 1;
	private static final Dimension DIALOG_SIZE = new Dimension(225, 100);
	private static final String WIDTH_LBL = "Width (w):";
	private static final String HEIGHT_LBL = "Height (h):";

	private int width = 3;
	private int height = 3;
	private JPanel dialog = null;

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public String getName() {
		return "Grid Generator";
	}

	@Override
	public String toString() {
		return String.format("Grid Generator (w=%d; h=%d)",
				this.width, this.height);
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
		}.generateGraph(this.width, this.height);
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
		}.generateGraph(this.width, this.height);
	}

	@Override
	public JPanel getConfigurationDialog() {
		if (this.dialog != null) {
			return this.dialog;
		}
		JPanel dialog = new JPanel();

		JLabel lblWidth = new JLabel(WIDTH_LBL, JLabel.TRAILING);
		final JSpinner valWidth = new JSpinner(new SpinnerNumberModel(
				this.width, DIMENSION_MIN, DIMENSION_MAX, DIMENSION_INCR));
		valWidth.setValue(this.width);
		valWidth.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				width = (int) valWidth.getValue();
			}
		});
		lblWidth.setLabelFor(valWidth);

		JLabel lblHeight = new JLabel(HEIGHT_LBL, JLabel.TRAILING);
		final JSpinner valHeight = new JSpinner(new SpinnerNumberModel(
				this.height, DIMENSION_MIN, DIMENSION_MAX, DIMENSION_INCR));
		valHeight.setValue(this.height);
		valHeight.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				height = (int) valHeight.getValue();
			}
		});
		lblHeight.setLabelFor(valHeight);

		GroupLayout layout = new GroupLayout(dialog);
		dialog.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout
				.createParallelGroup()
				.addGroup(layout
						.createSequentialGroup()
						.addComponent(lblWidth)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(valWidth)
				)
				.addGroup(layout
						.createSequentialGroup()
						.addComponent(lblHeight)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(valHeight)
				)
		);
		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(layout
						.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lblWidth)
						.addComponent(valWidth)
				)
				.addGroup(layout
						.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lblHeight)
						.addComponent(valHeight)
				)
		);

		dialog.setPreferredSize(DIALOG_SIZE);

		this.dialog = dialog;
		return dialog;
	}

	@Override
	public INetworkGenerator clone() {
		GridGenerator res = new GridGenerator();
		res.width = this.width;
		res.height = this.height;
		return res;
	}

	private abstract class Generator<T extends Network, N extends Node, L extends Link> {
		private static final double SCALE_Y = 10.0;
		private static final double SCALE_X = 10.0;

		protected abstract T createNetwork();
		protected abstract N createNode();
		protected abstract L createLink();

		public T generateGraph(int width, int height) {
			T network = this.createNetwork();

			// Generate connect nodes.
			List<N> nodes = new ArrayList<N>(width * height);
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					N node = this.createNode();
					node.setCoordinateX(x * SCALE_X);
					node.setCoordinateY(y * SCALE_Y);
					nodes.add(node);
					if (y != 0) {
						N above = nodes.get((y-1) * width + x);
						network.addEdge(this.createLink(), node, above);
						network.addEdge(this.createLink(), above, node);
					}
					if (x != 0) {
						N left = nodes.get(y * width + (x-1));
						network.addEdge(this.createLink(), node, left);
						network.addEdge(this.createLink(), left, node);
					}
				}
			}

			return network;
		}
	}

}
