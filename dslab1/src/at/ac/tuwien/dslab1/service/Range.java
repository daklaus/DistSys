package at.ac.tuwien.dslab1.service;

import java.util.Comparator;

public class Range<T> {
    private final T min;
    private final T max;
    private final Comparator<T> comparator;

    /**
     * <p>Constructs a new <code>Range</code> with the specified
     * minimum and maximum values (both inclusive) using the
     * <code>Comparator</code> for comparison .</p>
     *
     * <p>The arguments may be passed in the order (min,max) or (max,min). The
     * getMinimum and getMaximum methods will return the correct values.</p>
     *
     * @param t1  first number that defines the edge of the range, inclusive
     * @param t2  second number that defines the edge of the range, inclusive
     * @param comparator  comparator which is used for comparing the former values
     */
    public Range(T t1, T t2, Comparator<T> comparator) {
        this.comparator = comparator;
        if (this.comparator.compare(t1, t2) > 0) {
            this.min = t2;
            this.max = t1;
        } else {
            this.min = t1;
            this.max = t2;
        }
    }

    /**
     * <p>Gets the minimum value in this range.</p>
     *
     * @return the minimum value in this range
     */
    public T getMin() {
        return this.min;
    }

    /**
     * <p>Gets the maximum value in this range.</p>
     *
     * @return the maximum value in this range
     */
    public T getMax() {
        return this.max;
    }


    /**
     * <p>Tests whether the specified <code>T</code> occurs within
     * this range using Comparators <code>compareTo</code>.</p>
     *
     * @param value the value to test
     * @return <code>true</code> if the specified value occurs within this
     *         range by Comparators <code>compareTo</code>
     */
    public boolean containsValue(T value) {
        return this.comparator.compare(value, this.min) >= 0
                && this.comparator.compare(value, this.max) <= 0;
    }

    /**
     * <p>Tests whether the specified range occurs entirely within this range
     * using Comparators <code>compareTo</code>.</p>
     * <p/>
     * <p><code>null</code> is handled and returns <code>false</code>.</p>
     *
     * @param range the range to test, may be <code>null</code>
     * @return <code>true</code> if the specified range occurs entirely within this range
     */
    public boolean containsRange(Range<T> range) {
        if (range == null) {
            return false;
        }
        return containsValue(range.getMin()) &&
                containsValue(range.getMax());
    }

    /**
     * <p>Tests whether the specified range overlaps with this range
     * using Comparators <code>compareTo</code>.</p>
     * <p/>
     * <p><code>null</code> is handled and returns <code>false</code>.</p>
     *
     * @param range the range to test, may be <code>null</code>
     * @return <code>true</code> if the specified range overlaps with this range
     */
    public boolean overlapsRange(Range<T> range) {
        if (range == null) {
            return false;
        }
        return range.containsValue(this.min) ||
                range.containsValue(this.max) ||
                containsValue(range.getMin());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != Range.class) {
            return false;
        }
        Range range = (Range) obj;
        return min == range.min && max == range.max;
    }

    public int hashCode() {
        int hashCode = 17;
        hashCode = 37 * hashCode + getClass().hashCode();
        hashCode = 37 * hashCode + min.hashCode();
        hashCode = 37 * hashCode + max.hashCode();
        return hashCode;
    }

    /**
     * <p>Gets the range as a <code>String</code>.</p>
     * <p/>
     * <p>The format of the String is 'Range[<i>min</i>,<i>max</i>]'.</p>
     *
     * @return the <code>String</code> representation of this range
     */
    public String toString() {
        StringBuffer buf = new StringBuffer(32);
        buf.append("Range[");
        buf.append(this.min);
        buf.append(',');
        buf.append(this.max);
        buf.append(']');
        return buf.toString();
    }
}
