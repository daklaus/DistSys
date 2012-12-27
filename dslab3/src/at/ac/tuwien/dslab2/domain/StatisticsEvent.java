/**
 * 
 */
package at.ac.tuwien.dslab2.domain;

/**
 * @author klaus
 * 
 */
public class StatisticsEvent extends Event {
	private static final long serialVersionUID = 1L;
	private final double value;

	public StatisticsEvent(EventType type, double value) {
		super(type);
		this.value = value;
	}

	public double getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		String value = String.format("%.2f", this.value);

		String appendix = " - ";
		String unit = "";
		switch (type) {
		case USER_SESSIONTIME_MIN:
			appendix += "minimum session time";
			unit = "s";
			break;
		case USER_SESSIONTIME_MAX:
			appendix += "maximum session time";
			unit = "s";
			break;
		case USER_SESSIONTIME_AVG:
			appendix += "average session time";
			unit = "s";
			break;
		case BID_PRICE_MAX:
			appendix += "maximum bid price seen so far";
			break;
		case BID_COUNT_PER_MINUTE:
			appendix += "current bids per minute";
			value = String.format("%.4f", this.value);
			break;
		case AUCTION_SUCCESS_RATIO:
			appendix += "current auction success ratio";
			value = String.format("%.4f", this.value);
			break;
		case AUCTION_TIME_AVG:
			appendix += "current average auction duration";
			unit = "s";
			break;
		default:
			appendix = "(unknown event type)";
			break;
		}
		appendix += " is " + value + unit;
		return super.toString() + appendix;
	}
}
