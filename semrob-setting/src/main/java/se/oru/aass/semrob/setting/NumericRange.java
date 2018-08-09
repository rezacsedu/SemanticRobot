package se.oru.aass.semrob.setting;


public class NumericRange {
	private double minValue;
	private double maxValue;
	public NumericRange(double minValue, double maxValue) {
		if (minValue <= maxValue) {
			this.minValue = minValue;
			this.maxValue = maxValue;
		}
		else {
			this.minValue = maxValue;
			this.maxValue = minValue;
		}
	}
	
	public double getMaxValue() {
		return maxValue;
	}
	public double getMinValue() {
		return minValue;
	}
	
	public NumericRange addValueToRange(double offset) {
		return new NumericRange(this.minValue + offset, this.maxValue + offset);
	}
}
