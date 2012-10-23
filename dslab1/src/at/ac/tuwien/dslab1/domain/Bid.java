/**
 * 
 */
package at.ac.tuwien.dslab1.domain;

import java.text.Format;

/**
 * @author klaus
 * 
 */
public class Bid {
	private double amount;
	private User user;

	/**
	 * @param amount
	 * @param user
	 */
	public Bid(double amount, User user) {
		this.amount = amount;
		this.user = user;
	}

	public double getAmount() {
		return this.amount;
	}

	public User getUser() {
		return this.user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(this.amount);
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
		Bid other = (Bid) obj;
		if (Double.doubleToLongBits(this.amount) != Double
				.doubleToLongBits(other.amount))
			return false;
		if (this.user == null) {
			if (other.user != null)
				return false;
		} else if (!this.user.equals(other.user))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("%.2f", this.amount);
	}
}
