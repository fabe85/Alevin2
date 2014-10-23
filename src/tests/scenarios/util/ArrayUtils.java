package tests.scenarios.util;

import java.util.Arrays;

public class ArrayUtils {
	private ArrayUtils() {}

	public static boolean isSingleValue(double[] data) {
		double v = data[0];
		for (double entry : data) {
			if (entry != v) {
				return false;
			}
		}
		return true;
	}

	public static double min(double[] data) {
		double min = data[0];
		for (double entry : data) {
			if (entry < min) {
				min = entry;
			}
		}
		return min;
	}

	public static double max(double[] data) {
		double max = data[0];
		for (double entry : data) {
			if (entry > max) {
				max = entry;
			}
		}
		return max;
	}

	public static double sum(double[] data) {
		double sum = 0;
		for (double entry : data) {
			sum += entry;
		}
		return sum;
	}

	public static double avg(double[] data) {
		return sum(data) / (double) data.length;
	}

	public static double median(double[] data) {
		double[] sorted = data.clone();
		Arrays.sort(sorted);
		if (sorted.length % 2 == 1) {
			return sorted[sorted.length / 2];
		} else {
			return 0.5 * (sorted[sorted.length / 2] + sorted[sorted.length / 2 - 1]);
		}
	}

	public static double variance(double[] data) {
		double avg = avg(data);
		double variance = 0;
		for (double entry : data) {
			variance += (entry - avg)*(entry - avg);
		}
		variance *= 1 / (double)(data.length - 1);
		return variance;
	}
}
