/**
 * 
 */
package at.ac.tuwien.dslab2.domain;

/**
 * @author klaus
 * 
 */
public class BidEvent extends Event {
	private static final long serialVersionUID = 1L;
	private final String userName;
	private final long auctionId;
	private final double price;

	public BidEvent(EventType type, String userName, long auctionId,
			double price) {
		super(type);
		if (userName == null)
			throw new IllegalArgumentException("userName is null");

		this.userName = userName;
		this.auctionId = auctionId;
		this.price = price;
	}

	public String getUserName() {
		return this.userName;
	}

	public long getAuctionId() {
		return this.auctionId;
	}

	public double getPrice() {
		return this.price;
	}

	@Override
	public String toString() {
		String price = String.format("%.2f", this.price);
		String appendix = " - user " + this.userName + " ";
		switch (type) {
		case BID_PLACED:
			appendix += "placed bid " + price + " on auction " + this.auctionId;
			break;
		case BID_OVERBID:
			appendix += "was overbidden with " + price + " on auction "
					+ this.auctionId;
			break;
		case BID_WON:
			appendix += "won auction " + this.auctionId + " with " + price;
			break;
		default:
			appendix = "(unknown event type)";
			break;
		}
		return super.toString() + appendix;
	}
}
