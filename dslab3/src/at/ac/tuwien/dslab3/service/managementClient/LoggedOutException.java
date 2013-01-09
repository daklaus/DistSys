/**
 * 
 */
package at.ac.tuwien.dslab3.service.managementClient;

/**
 * @author klaus
 * 
 */
public class LoggedOutException extends Exception {
	private static final long serialVersionUID = 1L;

	public LoggedOutException() {
		super();
	}

	public LoggedOutException(String message) {
		super(message);
	}

}
