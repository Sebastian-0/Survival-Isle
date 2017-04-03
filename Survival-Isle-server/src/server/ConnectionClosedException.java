package server;

public class ConnectionClosedException extends RuntimeException {
	public ConnectionClosedException() {
		this ("");
	}
	public ConnectionClosedException(String message) {
		super (message);
	}
}
