package at.ac.tuwien.dslab2.domain;

import java.util.Comparator;

public class PriceStep extends Interval<Double> {
	private final double fixedPrice;
	private final double variablePricePercent;

	public PriceStep(double startPrice, double endPrice, double fixedPrice,
			double variablePricePercent) {
		super(startPrice, endPrice, new DoubleComparator());

		if (startPrice < 0 || endPrice < 0)
			throw new IllegalArgumentException(
					"Both min and max have to be positive");

		this.fixedPrice = fixedPrice;
		this.variablePricePercent = variablePricePercent;
	}

	public double getFixedPrice() {
		return this.fixedPrice;
	}

	public double getVariablePricePercent() {
		return this.variablePricePercent;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits(this.fixedPrice);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(this.variablePricePercent);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PriceStep other = (PriceStep) obj;
		if (Double.doubleToLongBits(this.fixedPrice) != Double
				.doubleToLongBits(other.fixedPrice))
			return false;
		if (Double.doubleToLongBits(this.variablePricePercent) != Double
				.doubleToLongBits(other.variablePricePercent))
			return false;
		return true;
	}

	private static final class DoubleComparator implements Comparator<Double> {
		@Override
		public int compare(Double o1, Double o2) {
			return Double.compare(o1, o2);
		}
	}
}
