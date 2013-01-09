/**
 * 
 */
package at.ac.tuwien.dslab3.domain;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author klaus
 * 
 */
public abstract class Event implements Comparable<Event>, Serializable {
	private static final long serialVersionUID = 1L;
	protected final UUID id;
	protected final EventType type;
	protected final long timestamp;

	public Event(EventType type) {
		this.timestamp = System.currentTimeMillis();
		this.id = UUID.randomUUID();
		this.type = type;
	}

	public UUID getId() {
		return this.id;
	}

	public EventType getType() {
		return this.type;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		result = prime * result
				+ (int) (this.timestamp ^ (this.timestamp >>> 32));
		result = prime * result
				+ ((this.type == null) ? 0 : this.type.hashCode());
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
		Event other = (Event) obj;
		if (this.id == null) {
			if (other.id != null)
				return false;
		} else if (!this.id.equals(other.id))
			return false;
		if (this.timestamp != other.timestamp)
			return false;
		if (this.type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT,
				DateFormat.LONG);
		Date date = new Date(this.timestamp);

		return this.type + ": " + df.format(date);
	}

	@Override
	public int compareTo(Event o) {
		// The newer one is the "greater" one
		Long timestamp = new Long(this.timestamp);
		int first = timestamp.compareTo(o.timestamp);
		if (first != 0)
			return first;

		int second = this.type.compareTo(o.type);
		if (second != 0)
			return second;

		return this.id.compareTo(o.id);
	}

}
