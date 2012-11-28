package at.ac.tuwien.dslab2.domain;

import java.io.Serializable;

public class PriceStep extends Interval<Double> implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * The size in characters to which each value will be padded within the
	 * toString method
	 */
	public static final int PADDING_SIZE = 20;

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

	public String getInterval() {
		return super.toString();
	}

	@Override
	public String toString() {
		final String nbrFormat = "%-" + PADDING_SIZE + ".2f";
		return String.format(nbrFormat + nbrFormat + nbrFormat + nbrFormat,
				this.min, this.max, this.fixedPrice, this.variablePricePercent);
	}

}
