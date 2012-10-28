/**
 * 
 */
package at.ac.tuwien.dslab1.domain;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author klaus
 * 
 */
public class User {
	private Boolean loggedIn;
	private String name;
	private Client client;
	private BlockingQueue<String> notifications;

	/**
	 * @param loggedIn
	 * @param name
	 * @param client
	 * @param notifications
	 */
	public User(Boolean loggedIn, String name, Client client) {
		this.loggedIn = loggedIn;
		this.name = name;
		this.client = client;
		this.notifications = new LinkedBlockingQueue<String>();
	}

	/**
	 * @param name
	 * @param client
	 */
	public User(String name, Client client) {
		this(false, name, client);
	}

	/**
	 * @param name
	 */
	public User(String name) {
		this(name, null);
	}

	public Boolean getLoggedIn() {
		return this.loggedIn;
	}

	public void setLoggedIn(Boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public String getName() {
		return this.name;
	}

	public Client getClient() {
		return this.client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public BlockingQueue<String> getNotifications() {
		return this.notifications;
	}

	public void addNotification(String notification) {
		if (notification == null)
			throw new IllegalArgumentException("notification is null");

		this.notifications.offer(notification);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.name == null) ? 0 : this.name.hashCode());
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
		User other = (User) obj;
		if (this.name == null) {
			if (other.name != null)
				return false;
		} else if (!this.name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
