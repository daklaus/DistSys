package at.ac.tuwien.dslab2.domain;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

public class PriceSteps {
	private final SortedSet<PriceStep> priceSteps;

	public PriceSteps() {
		priceSteps = new ConcurrentSkipListSet<PriceStep>();
	}

	public void addPriceStep(double startPrice, double endPrice,
			double fixedPrice, double variablePricePercent) {
		priceSteps.add(new PriceStep(startPrice, endPrice, fixedPrice,
				variablePricePercent));
	}

	public void removePriceStep(double startPrice, double endPrice) {
		priceSteps.remove(new Interval<Double>(startPrice, endPrice,
				new DoubleComparator()));
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
		for (Iterator iterator = priceSteps.iterator(); iterator.hasNext();) {
			PriceStep ps = (PriceStep) iterator.next();

			builder.append(ps.toString());

			if (iterator.hasNext())
				builder.append("\n");
		}
		return builder.toString();
	}
}
