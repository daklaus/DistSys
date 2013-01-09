package at.ac.tuwien.dslab3.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

public class PriceSteps implements Iterable<PriceStep>, Serializable {
	private static final long serialVersionUID = 1L;
	private final SortedSet<PriceStep> priceSteps;

	public PriceSteps() {
		priceSteps = new ConcurrentSkipListSet<PriceStep>();
	}

	public void add(PriceStep ps) {
		if (ps == null)
			throw new IllegalArgumentException("Price step is null");
		priceSteps.add(ps);
	}

	public void add(double startPrice, double endPrice, double fixedPrice,
			double variablePricePercent) {
		priceSteps.add(new PriceStep(startPrice, endPrice, fixedPrice,
				variablePricePercent));
	}

	public void remove(double startPrice, double endPrice)
			throws IllegalStateException {
		priceSteps.remove(new Interval<Double>(startPrice, endPrice));
	}

	public boolean contains(double startPrice, double endPrice) {
		return priceSteps.contains(new Interval<Double>(startPrice, endPrice));
	}

	public boolean isEmpty() {
		return priceSteps.isEmpty();
	}

	@Override
	public Iterator<PriceStep> iterator() {
		return Collections.unmodifiableSortedSet(priceSteps).iterator();
	}

	@Override
	public String toString() {
		final String sFormat = "%-" + PriceStep.PADDING_SIZE + "s";

		StringBuilder builder = new StringBuilder();

		builder.append(String.format(sFormat, "Min_Price"));
		builder.append(String.format(sFormat, "Max_Price"));
		builder.append(String.format(sFormat, "Fee_Fixed"));
		builder.append(String.format(sFormat, "Fee_Variable"));
		builder.append("\n");
		for (Iterator<PriceStep> iterator = priceSteps.iterator(); iterator
				.hasNext();) {
			PriceStep ps = iterator.next();

			builder.append(ps.toString());

			if (iterator.hasNext())
				builder.append("\n");
		}
		return builder.toString();
	}
}
