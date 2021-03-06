/**
 * 
 */
package at.ac.tuwien.dslab3.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author klaus
 * 
 */
public class Bill implements Iterable<Bill.Auction>, Serializable {
	private static final long serialVersionUID = 1L;
	private final SortedSet<Auction> auctionBills;
	private final String user;

	public Bill(String user) {
		if (user == null)
			throw new IllegalArgumentException("user is null");
		this.user = user;
		auctionBills = new ConcurrentSkipListSet<Auction>();
	}

	public void addAuction(long auctionId, double price) {
		auctionBills.add(new Auction(user, auctionId, price));
	}

	public String getUser() {
		return this.user;
	}

	@Override
	public Iterator<Bill.Auction> iterator() {
		return Collections.unmodifiableSortedSet(auctionBills).iterator();
	}

	public class Auction implements Comparable<Auction>, Serializable {
		private static final long serialVersionUID = 1L;
		private final String user;
		private final long auctionId;
		private final double price;
		private double calculatedFixedFee;
		private double calculatedVariableFee;

		public Auction(String user, long auctionId, double price) {
			this.user = user;
			this.auctionId = auctionId;
			this.price = price;
		}

		public String getUser() {
			return this.user;
		}

		public long getAuctionId() {
			return this.auctionId;
		}

		public double getPrice() {
			return this.price;
		}

		public double getCalculatedFixedFee() {
			return this.calculatedFixedFee;
		}

		public void setCalculatedFixedFee(double calculatedFixedFee) {
			this.calculatedFixedFee = calculatedFixedFee;
		}

		public double getCalculatedVariableFee() {
			return this.calculatedVariableFee;
		}

		public void setCalculatedVariableFee(double calculatedVariableFee) {
			this.calculatedVariableFee = calculatedVariableFee;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ (int) (this.auctionId ^ (this.auctionId >>> 32));
			long temp;
			temp = Double.doubleToLongBits(this.price);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			result = prime * result
					+ ((this.user == null) ? 0 : this.user.hashCode());
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
			if (this.auctionId != other.auctionId)
				return false;
			if (Double.doubleToLongBits(this.price) != Double
					.doubleToLongBits(other.price))
				return false;
			if (this.user == null) {
				if (other.user != null)
					return false;
			} else if (!this.user.equals(other.user))
				return false;
			return true;
		}

		@Override
		public int compareTo(Auction o) {
			if (o == null)
				throw new IllegalArgumentException("o is null");

			Long auctionId = new Long(this.auctionId);
			int first = auctionId.compareTo(o.auctionId);
			if (first != 0)
				return first;

			return Double.compare(price, o.price);
		}
	}
}
