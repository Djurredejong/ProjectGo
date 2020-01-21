package exceptions;

public class ServerUnavailableException extends Exception {

	/**
	 * default
	 */
	private static final long serialVersionUID = 1L;

	public ServerUnavailableException(String msg) {
		super(msg);
	}

}