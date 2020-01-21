package exceptions;

public class ExitProgram extends Exception {

	/**
	 * default
	 */
	private static final long serialVersionUID = 1L;

	public ExitProgram(String msg) {
		super(msg);
	}

}