/**
 * 
 */
package at.ac.tuwien.dslab2.service.managementClient;

/**
 * @author klaus
 * 
 */
public class AlreadyLoggedInException extends Exception {
	private static final long serialVersionUID = 1L;

	public AlreadyLoggedInException() {
		super();
	}

	public AlreadyLoggedInException(String message) {
		super(message);
	}

}
