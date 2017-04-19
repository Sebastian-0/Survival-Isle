package server;

public class ConnectionClosedException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public ConnectionClosedException() {
		this ("");
	}
	public ConnectionClosedException(String message) {
		super (message);
	}
}
