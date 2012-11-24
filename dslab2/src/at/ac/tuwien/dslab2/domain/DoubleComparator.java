package at.ac.tuwien.dslab2.domain;

import java.util.Comparator;

class DoubleComparator implements Comparator<Double> {
	@Override
	public int compare(Double o1, Double o2) {
		return Double.compare(o1, o2);
	}
}