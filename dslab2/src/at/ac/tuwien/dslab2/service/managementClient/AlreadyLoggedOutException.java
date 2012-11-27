/**
 * 
 */
package at.ac.tuwien.dslab2.service.managementClient;

/**
 * @author klaus
 * 
 */
public class AlreadyLoggedOutException extends Exception {
	private static final long serialVersionUID = 1L;

	public AlreadyLoggedOutException() {
		super();
	}

	public AlreadyLoggedOutException(String message) {
		super(message);
	}

}
