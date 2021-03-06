package at.ac.tuwien.dslab3.domain;

import java.io.Serializable;

class Interval<T extends Comparable<T>> implements Comparable<Interval<T>>,
		Serializable {
	private static final long serialVersionUID = 1L;
	protected final T min;
	protected final T max;

	/**
	 * <p>
	 * Constructs a new <code>Interval</code> with the specified minimum and
	 * maximum values (<i>min</i> is exclusive - left-open interval, e.g.
	 * '(<i>min</i> <i>max</i>]').
	 * </p>
	 * 
	 * @param min
	 *            first number that defines the edge of the interval (exclusive)
	 * @param max
	 *            second number that defines the edge of the interval
	 *            (inclusive)
	 */
	public Interval(T min, T max) {
		this.min = min;
		this.max = max;
	}

	/**
	 * <p>
	 * Gets the minimum value in this interval.
	 * </p>
	 * 
	 * @return the minimum value in this interval
	 */
	public T getMin() {
		return this.min;
	}

	/**
	 * <p>
	 * Gets the maximum value in this interval.
	 * </p>
	 * 
	 * @return the maximum value in this interval
	 */
	public T getMax() {
		return this.max;
	}

	/**
	 * <p>
	 * Tests whether the specified <code>T</code> occurs within this interval.
	 * </p>
	 * 
	 * @param value
	 *            the value to test
	 * @return <code>true</code> if the specified value occurs within this
	 *         interval
	 */
	public boolean contains(T value) {
		return value.compareTo(this.min) > 0 && value.compareTo(this.max) <= 0;
	}

	/**
	 * <p>
	 * Tests whether the specified interval occurs entirely within this
	 * interval.
	 * </p>
	 * 
	 * <p>
	 * <code>null</code> is handled and returns <code>false</code>.
	 * </p>
	 * 
	 * @param interval
	 *            the interval to test, may be <code>null</code>
	 * @return <code>true</code> if the specified interval occurs entirely
	 *         within this interval
	 */
	public boolean contains(Interval<T> interval) {
		if (interval == null)
			throw new NullPointerException("interval is null");

		return this.contains(interval.min) && this.contains(interval.max);
	}

	/**
	 * <p>
	 * Tests whether the specified interval overlaps with this interval.
	 * </p>
	 * 
	 * <p>
	 * <code>null</code> is handled and returns <code>false</code>.
	 * </p>
	 * 
	 * @param interval
	 *            the interval to test, may be <code>null</code>
	 * @return <code>true</code> if the specified interval overlaps with this
	 *         interval
	 */
	public boolean overlaps(Interval<T> interval) {
		if (interval == null)
			throw new NullPointerException("interval is null");

		return (this.contains(interval.min) && interval.contains(this.max))
				|| (this.contains(interval.max) && interval.contains(this.min))
				|| (interval.contains(this.min) && interval.contains(this.max))
				|| (this.contains(interval.min) && this.contains(interval.max))
				|| this.equals(interval);
	}

	@Override
	public int compareTo(Interval<T> o) {
		return this.min.compareTo(o.min);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Interval<?> other = (Interval<?>) obj;
		if (this.max == null) {
			if (other.max != null)
				return false;
		} else if (!this.max.equals(other.max))
			return false;
		if (this.min == null) {
			if (other.min != null)
				return false;
		} else if (!this.min.equals(other.min))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.max == null) ? 0 : this.max.hashCode());
		result = prime * result
				+ ((this.min == null) ? 0 : this.min.hashCode());
		return result;
	}

	/**
	 * <p>
	 * Gets the interval as a <code>String</code>.
	 * </p>
	 * <p>
	 * The format of the String is '(<i>min</i> <i>max</i>]'.
	 * </p>
	 * 
	 * @return the <code>String</code> representation of this interval
	 */
	@Override
	public String toString() {
		return "(" + this.min + " " + this.max + "]";
	}
}
