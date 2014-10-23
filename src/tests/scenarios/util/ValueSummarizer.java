package tests.scenarios.util;

public class ValueSummarizer {
	private String title = "Generic summary:";

	public ValueSummarizer() {
	}

	public ValueSummarizer(String title) {
		this.title = title;
	}

	public String summarize(double[] data) {
		StringBuilder summary = new StringBuilder();
		summary.append(this.getSummaryTitle() + "\n");

		boolean single = ArrayUtils.isSingleValue(data);
		if (single) {
			summary.append(" " + this.singleValue(data[0]) + "\n");
			return summary.toString();
		}

		double min = ArrayUtils.min(data);
		double max = ArrayUtils.max(data);
		double average = ArrayUtils.avg(data);
		double median = ArrayUtils.median(data);
		double variance = ArrayUtils.variance(data);

		summary.append(" " + this.average(average) + "\n");
		summary.append(" " + this.median(median) + "\n");
		summary.append(" " + this.variance(variance) + "\n");
		summary.append(" " + this.minimum(min) + "\n");
		summary.append(" " + this.maximum(max) + "\n");
		summary.append(" " + this.listLabel() + " ");
		for (int i = 0; i < data.length; i++) {
			double value = data[i];
			summary.append(this.listItem(value));
			if (i < data.length - 1) {
				summary.append("; ");
			}
		}
		summary.append("\n");

		return summary.toString();
	}

	protected String listItem(double value) {
		return String.format("%.6f", value);
	}
	protected String listLabel() {
		return "List of values:";
	}
	protected String maximum(double max) {
		return String.format("Max value: %.6f", max);
	}
	protected String minimum(double min) {
		return String.format("Min value: %.6f", min);
	}
	protected String variance(double variance) {
		return String.format("Value variance: %.6f", variance);
	}
	protected String median(double median) {
		return String.format("Median value: %.6f", median);
	}
	protected String average(double average) {
		return String.format("Average value: %.6f", average);
	}
	protected String singleValue(double value) {
		return String.format("Single value: %.6f", value);
	}
	protected String getSummaryTitle() {
		return this.title;
	}
}
