/**
 * 
 */
package at.ac.tuwien.dslab3.domain;

import java.util.regex.Pattern;

import at.ac.tuwien.dslab3.service.managementClient.MgmtClientCallback;

/**
 * @author klaus
 * 
 */
public class Subscription implements Comparable<Subscription> {
	private final long id;
	private final Pattern regex;
	private final MgmtClientCallback cb;

	public Subscription(long id, Pattern regex, MgmtClientCallback cb) {
		if (regex == null)
			throw new IllegalArgumentException("regex is null");

		this.id = id;
		this.regex = regex;
		this.cb = cb;
	}

	public long getId() {
		return this.id;
	}

	public Pattern getRegex() {
		return this.regex;
	}

	public MgmtClientCallback getCb() {
		return this.cb;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (this.id ^ (this.id >>> 32));
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
		Subscription other = (Subscription) obj;
		if (this.id != other.id)
			return false;
		return true;
	}

	@Override
	public int compareTo(Subscription o) {
		Long id = new Long(this.id);
		return id.compareTo(o.id);
	}

}
