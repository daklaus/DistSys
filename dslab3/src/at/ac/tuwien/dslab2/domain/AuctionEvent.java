/**
 * 
 */
package at.ac.tuwien.dslab2.domain;

/**
 * @author klaus
 * 
 */
public class AuctionEvent extends Event {
	private static final long serialVersionUID = 1L;
	private final long auctionId;

	public AuctionEvent(EventType type, long auctionId) {
		super(type);
		this.auctionId = auctionId;
	}

	public long getAuctionId() {
		return this.auctionId;
	}

	@Override
	public String toString() {
		String appendix = " - auction " + this.auctionId + " ";
		switch (type) {
		case AUCTION_STARTED:
			appendix += "has started";
			break;
		case AUCTION_ENDED:
			appendix += "has ended";
			break;
		default:
			appendix = "(unknown event type)";
			break;
		}
		return super.toString() + appendix;
	}
}
