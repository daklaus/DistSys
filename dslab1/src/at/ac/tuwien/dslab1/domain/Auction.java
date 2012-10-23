/**
 * 
 */
package at.ac.tuwien.dslab1.domain;

import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.NavigableSet;

/**
 * @author klaus
 * 
 */
public class Auction {
	private Integer id;
	private String description;
	private User owner;
	private NavigableSet<Bid> bids;
	private Integer duration;
	private Date start;

	/**
	 * @param id
	 * @param description
	 * @param owner
	 * @param duration
	 */
	public Auction(Integer id, String description, User owner, Integer duration) {
		this.id = id;
		this.description = description;
		this.owner = owner;
		this.duration = duration;
		this.start = new Date();
	}

	public NavigableSet<Bid> getBids() {
		return this.bids;
	}

	public void setBids(NavigableSet<Bid> bids) {
		this.bids = bids;
	}

	public Integer getId() {
		return this.id;
	}

	public String getDescription() {
		return this.description;
	}

	public User getOwner() {
		return this.owner;
	}

	public Integer getDuration() {
		return this.duration;
	}

	public Bid getHighestBid() {
		if (bids == null || bids.isEmpty())
			return null;
		return bids.last();
	}

	public User getHighestBidder() {
		Bid highest = getHighestBid();
		if (highest == null)
			return null;
		return highest.getUser();
	}

	public Date getEndDate() {
		if (start == null)
			return null;
		if (duration == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.setTime(start);
		cal.add(Calendar.SECOND, duration);

		return cal.getTime();
	}
	
	public Boolean isExpired()
	{	
		Date now = new Date();
		Date end = getEndDate();
		
		return now.after(end);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Auction other = (Auction) obj;
		if (this.id == null) {
			if (other.id != null)
				return false;
		} else if (!this.id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT,
				DateFormat.LONG);

		return (this.id != null ? this.id + ". " : "")
				+ (this.description != null ? "'" + this.description + "' "
						: "")
				+ (this.owner != null ? this.owner + " " : "")
				+ (this.getEndDate() != null ? df.format(this.getEndDate())
						+ " " : "")
				+ (this.getHighestBid() != null ? this.getHighestBid() + " "
						: "0.00 ")
				+ (this.getHighestBidder() != null ? this.getHighestBidder()
						: "none");
	}
}
