package ss.project.networking.exceptions;

public class PortNotAvailableException extends Exception {
	private static final long serialVersionUID = 1L;

	public PortNotAvailableException(String port) {
		super(port + " not available");
	}
}
