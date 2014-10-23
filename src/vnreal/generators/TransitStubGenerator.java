package vnreal.generators;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JPanel;

import vnreal.generators.transitstub.TransitStub;
import vnreal.generators.transitstub.graph.Edge;
import vnreal.generators.transitstub.graph.Graph;
import vnreal.generators.transitstub.graph.Vertex;
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
 * This class implements an {@link INetworkGenerator} that generates a
 * transit-stub topology. Further information can be found in Ch. 3.3.2 of
 * "Zegura et al '97: A Quantitative Comparison of Graph-based Models for
 * Internet Topology".
 * 
 * @author Philip Huppert
 */
public class TransitStubGenerator implements INetworkGenerator {

	private static final Dimension DIALOG_SIZE = new Dimension(512, 512);

	private static final double ALPHA_INCR = 0.1;
	private static final double ALPHA_MAX = 1000.0;
	private static final double ALPHA_MIN = Double.MIN_VALUE;

	private static final double BETA_INCR = 0.1;
	private static final double BETA_MAX = 1.0;
	private static final double BETA_MIN = -1000.0;

	private static final int NUM_INCR = 1;
	private static final int NUM_MAX = 1000000;
	private static final int NUM_MIN = 0;

	private static final int MEAN_INCR = 1;
	private static final int MEAN_MAX = 1000000;
	private static final int MEAN_MIN = 0;

	private static final String SEED_LBL = "Seed (s):";
	private static final String SEED_TIP = "<html>Seed for the algortihm's random number generator.<br>If the given seed is not numeric, the hashcode of the entered string will be used.</html>";

	private static final String DOMAIN_ALPHA_LBL = "Domain alpha (da):";
	private static final String DOMAIN_ALPHA_TIP = "<html>da &gt; 0<br>An increase in alpha will increase<br>the number of edges in the domain graph.</html>";

	private static final String DOMAIN_BETA_LBL = "Domain beta (db):";
	private static final String DOMAIN_BETA_TIP = "<html>db &lt;= 1<br>An increase in beta will increase<br>the ratio of long edges relative to short edges in the domain graph.</html>";

	private static final String TRANSIT_ALPHA_LBL = "Transit alpha (ta):";
	private static final String TRANSIT_ALPHA_TIP = "<html>ta &gt; 0<br>An increase in alpha will increase<br>the number of edges in transit graphs.</html>";

	private static final String TRANSIT_BETA_LBL = "Transit beta (tb):";
	private static final String TRANSIT_BETA_TIP = "<html>tb &lt;= 1<br>An increase in beta will increase<br>the ratio of long edges relative to short edges in transit graphs.</html>";

	private static final String STUB_ALPHA_LBL = "Stub alpha (sa):";
	private static final String STUB_ALPHA_TIP = "<html>sa &gt; 0<br>An increase in alpha will increase<br>the number of edges in stub graphs.</html>";

	private static final String STUB_BETA_LBL = "Stub beta (sb):";
	private static final String STUB_BETA_TIP = "<html>sb &lt;= 1<br>An increase in beta will increase<br>the ratio of long edges relative to short edges in stub graphs.</html>";

	private static final String NUM_DOMAINS_LBL = "Domain count (dc):";
	private static final String NUM_DOMAINS_TIP = "Number of transit domains, i.e. nodes in the domain graph.";

	private static final String MEAN_TRANSIT_NODES_LBL = "Mean transit count (tc):";
	private static final String MEAN_TRANSIT_NODES_TIP = "Mean number of nodes in each transit domain.";

	private static final String MEAN_STUBS_LBL = "Mean stub count (sc):";
	private static final String MEAN_STUBS_TIP = "Mean number of stub networks for each transit node.";

	private static final String MEAN_STUB_SIZE_LBL = "Mean stub size (ss):";
	private static final String MEAN_STUB_SIZE_TIP = "Mean number of nodes in each stub domain.";

	private static final String NUM_RND_TRANSIT_STUB_LBL = "Additional transit-stub edges (rts):";
	private static final String NUM_RND_TRANSIT_STUB_TIP = "Number of additional, random edges between transit and stub domains.";

	private static final String NUM_RND_STUB_STUB_LBL = "Stub-stub edges (rss):";
	private static final String NUM_RND_STUB_STUB_TIP = "Number of random edges between stub domains.";

	private long seed = new Random().nextLong();
	private int numDomains = 3;
	private int numMeanTransitVerts = 5;
	private int numMeanStubsPerTransitVert = 2;
	private int numMeanStubVerts = 3;
	private int numRandomTransitStubEdges = 4;
	private int numRandomStubStubEdges = 15;
	private double domainAlpha = 1.0;
	private double domainBeta = 0.5;
	private double transitAlpha = 1.0;
	private double transitBeta = 0.5;
	private double stubAlpha = 1.0;
	private double stubBeta = 0.5;

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public int getNumDomains() {
		return numDomains;
	}

	public void setNumDomains(int numDomains) {
		this.numDomains = numDomains;
	}

	public int getNumMeanTransitVerts() {
		return numMeanTransitVerts;
	}

	public void setNumMeanTransitVerts(int numMeanTransitVerts) {
		this.numMeanTransitVerts = numMeanTransitVerts;
	}

	public int getNumMeanStubsPerTransitVert() {
		return numMeanStubsPerTransitVert;
	}

	public void setNumMeanStubsPerTransitVert(int numMeanStubsPerTransitVert) {
		this.numMeanStubsPerTransitVert = numMeanStubsPerTransitVert;
	}

	public int getNumMeanStubVerts() {
		return numMeanStubVerts;
	}

	public void setNumMeanStubVerts(int numMeanStubVerts) {
		this.numMeanStubVerts = numMeanStubVerts;
	}

	public int getNumRandomTransitStubEdges() {
		return numRandomTransitStubEdges;
	}

	public void setNumRandomTransitStubEdges(int numRandomTransitStubEdges) {
		this.numRandomTransitStubEdges = numRandomTransitStubEdges;
	}

	public int getNumRandomStubStubEdges() {
		return numRandomStubStubEdges;
	}

	public void setNumRandomStubStubEdges(int numRandomStubStubEdges) {
		this.numRandomStubStubEdges = numRandomStubStubEdges;
	}

	public double getDomainAlpha() {
		return domainAlpha;
	}

	public void setDomainAlpha(double domainAlpha) {
		this.domainAlpha = domainAlpha;
	}

	public double getDomainBeta() {
		return domainBeta;
	}

	public void setDomainBeta(double domainBeta) {
		this.domainBeta = domainBeta;
	}

	public double getTransitAlpha() {
		return transitAlpha;
	}

	public void setTransitAlpha(double transitAlpha) {
		this.transitAlpha = transitAlpha;
	}

	public double getTransitBeta() {
		return transitBeta;
	}

	public void setTransitBeta(double transitBeta) {
		this.transitBeta = transitBeta;
	}

	public double getStubAlpha() {
		return stubAlpha;
	}

	public void setStubAlpha(double stubAlpha) {
		this.stubAlpha = stubAlpha;
	}

	public double getStubBeta() {
		return stubBeta;
	}

	public void setStubBeta(double stubBeta) {
		this.stubBeta = stubBeta;
	}

	@Override
	public String getName() {
		return "Transit Stub Generator";
	}

	@Override
	public String toString() {
		// TODO toString
		return String.format("Transit Stub Generator (TODO)");
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
		}.generateGraph(this);
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
		}.generateGraph(this);
	}

	@Override
	public JPanel getConfigurationDialog() {
		// TODO config dialog
		throw new RuntimeException("not implemented");
	}

	@Override
	public INetworkGenerator clone() {
		TransitStubGenerator res = new TransitStubGenerator();
		res.domainAlpha = this.domainAlpha;
		res.domainBeta = this.domainBeta;
		res.numDomains = this.numDomains;
		res.numMeanStubsPerTransitVert = this.numMeanStubsPerTransitVert;
		res.numMeanStubVerts = this.numMeanStubVerts;
		res.numMeanTransitVerts = this.numMeanTransitVerts;
		res.numRandomStubStubEdges = this.numRandomStubStubEdges;
		res.numRandomTransitStubEdges = this.numRandomTransitStubEdges;
		res.seed = this.seed;
		res.stubAlpha = this.stubAlpha;
		res.stubBeta = this.stubBeta;
		res.transitAlpha = this.transitAlpha;
		res.transitBeta = this.transitBeta;
		res.stubAlpha = this.stubAlpha;
		res.stubBeta = this.stubBeta;
		return res;
	}
	
	private abstract class Generator<T extends Network, N extends Node, L extends Link> {
		private static final double SCALE = 1000.0;

		protected abstract T createNetwork();
		protected abstract N createNode();
		protected abstract L createLink();
		
		public T generateGraph(TransitStubGenerator parent) {
			Graph graph = TransitStub.generateWithWaxman(
					parent.seed,
					parent.numDomains,
					parent.numMeanTransitVerts,
					parent.numMeanStubsPerTransitVert,
					parent.numMeanStubVerts,
					parent.numRandomTransitStubEdges,
					parent.numRandomStubStubEdges,
					parent.domainAlpha,
					parent.domainBeta,
					parent.transitAlpha,
					parent.transitBeta,
					parent.stubAlpha,
					parent.stubBeta);

			T network = this.createNetwork();
			Map<Vertex, N> vertexMap = new HashMap<Vertex, N>();
			for (Vertex v : graph.getVertices()) {
				N n = this.createNode();
				n.setCoordinateX(v.getXpos() * SCALE);
				n.setCoordinateY(v.getYpos() * SCALE);
				vertexMap.put(v, n);
				network.addVertex(n);
			}

			for (Edge e : graph.getEdges()) {
				Vertex[] vs = graph.getVertices(e);
				N na = vertexMap.get(vs[0]);
				N nb = vertexMap.get(vs[1]);
				L la = this.createLink();
				L lb = this.createLink();
				network.addEdge(la, na, nb);
				network.addEdge(lb, nb, na);
			}

			return network;
		}
	}

}
