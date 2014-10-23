package vnreal.generators.transitstub.util;
import java.util.Arrays;
import java.util.Random;


public class MeanRNG {
	private int[] data;
	private int current;

	public MeanRNG(int size, int mean, int iterationFactor) {
		this.init(size, mean, iterationFactor, new Random());
	}

	public MeanRNG(int size, int mean, int iterationFactor, long seed) {
		this.init(size, mean, iterationFactor, new Random(seed));
	}

	private void init(int size, int mean, int iterationFactor, Random rnd) {
		if (size < 1) {
			throw new IllegalArgumentException("Size must be positive");
		}

		this.current = 0;
		this.data = new int[size];
		Arrays.fill(this.data, mean);

		if (size < 2) {
			return;
		}

		for (int it = 0; it < iterationFactor * size; it++) {
			int i = rnd.nextInt(size);
			if (this.data[i] > 1) {
				this.data[i]--;
				this.data[rnd.nextInt(size)]++;
			}
		}
	}

	public int nextInt() {
		int i = this.current;

		if (i < this.data.length) {
			++this.current;
			return this.data[i];
		} else {
			throw new RuntimeException("Generator exhausted");
		}
	}
}
