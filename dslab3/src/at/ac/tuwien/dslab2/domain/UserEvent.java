/**
 * 
 */
package at.ac.tuwien.dslab2.domain;

/**
 * @author klaus
 * 
 */
public class UserEvent extends Event {
	private static final long serialVersionUID = 1L;
	private final String userName;

	public UserEvent(EventType type, String userName) {
		super(type);
		if (userName == null)
			throw new IllegalArgumentException("userName is null");

		this.userName = userName;
	}

	public String getUserName() {
		return this.userName;
	}

	@Override
	public String toString() {
		String appendix = " - user " + this.userName + " ";
		switch (type) {
		case USER_LOGIN:
			appendix += "logged in";
			break;
		case USER_LOGOUT:
			appendix += "logged out";
			break;
		case USER_DISCONNECT:
			appendix += "disconnected";
			break;
		default:
			appendix = "(unknown event type)";
			break;
		}
		return super.toString() + appendix;
	}
}
