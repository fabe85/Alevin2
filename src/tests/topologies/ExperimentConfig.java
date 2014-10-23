package tests.topologies;

import tests.topologies.embeddingalgos.FutureEmbeddingAlgorithm;
import vnreal.generators.INetworkGenerator;

/**
 * Description of an experiment in {@link NewTopologiesExperimentRig}.
 */
public class ExperimentConfig {
	public final INetworkGenerator substrateGenerator;
	public final INetworkGenerator[] virtualGenerators;
	public final FutureEmbeddingAlgorithm embeddingAlgorithm;
	public final ConstraintConfig constraintConfig;

	public ExperimentConfig(
			INetworkGenerator substrateGenerator,
			INetworkGenerator[] virtualGenerators,
			FutureEmbeddingAlgorithm embeddingAlgorithm,
			ConstraintConfig constraintConfig) {
		this.substrateGenerator = substrateGenerator;
		this.virtualGenerators = virtualGenerators;
		this.embeddingAlgorithm = embeddingAlgorithm;
		this.constraintConfig = constraintConfig;
	}

	@Override
	public String toString() {
		StringBuilder vgens = new StringBuilder();
		vgens.append("[\n");
		for (INetworkGenerator vgen : this.virtualGenerators) {
			vgens.append(String.format("  %s%n", vgen));
		}
		vgens.append(" ]");

		return String.format(
				"ExperimentConfig [%n sGen=%s%n vGens=%s%n vne=%s%n constr=%s%n]",
				this.substrateGenerator,
				vgens,
				this.embeddingAlgorithm,
				this.constraintConfig);
	}

	public static class ConstraintConfig {
		public final double virtualBandwidthDemand;
		public final double virtualCpuDemand;
		public final double substrateBandwidthResource;
		public final double substrateCpuResource;

		public ConstraintConfig(
				double substrateCpuResource,
				double substrateBandwidthResource,
				double virtualCpuDemand,
				double virtualBandwidthDemand) {
			this.virtualBandwidthDemand = virtualBandwidthDemand;
			this.virtualCpuDemand = virtualCpuDemand;
			this.substrateBandwidthResource = substrateBandwidthResource;
			this.substrateCpuResource = substrateCpuResource;
		}

		public static ConstraintConfig tenPercent() {
			return new ConstraintConfig(100.0, 100.0, 10.0, 10.0);
		}

		@Override
		public String toString() {
			return String.format(
					"ConstraintConfig [rCPU=%f; rBW=%f; dCPU=%f; dBW=%f]",
					this.substrateCpuResource,
					this.substrateBandwidthResource,
					this.virtualCpuDemand,
					this.virtualBandwidthDemand);
		}
	}
}