package server;
import java.io.IOException;
import java.net.Socket;

public class MessageHandler {
	private Socket socket;

	
	public MessageHandler(Socket socket) {
		this.socket = socket;
	}

	
//	public void sendCode(int code) throws ConnectionClosedException {
//		sendByte(code);
//	}

	public void sendInt(int value) {
		sendByte((value >> 24) & 0xFF);
		sendByte((value >> 16) & 0xFF);
		sendByte((value >> 8) & 0xFF);
		sendByte(value & 0xFF);
	}

	public void sendString(String string)
				throws ConnectionClosedException {
			sendInt(string.length());
			for (int i = 0; i < string.length(); i++) {
				sendByte(string.charAt(i));
			}
		}


	public void sendByte(int code) {
		try {
			socket.getOutputStream().write(code);
		} catch (java.io.IOException e) {
			throw new ConnectionClosedException();
		}
	}


//	public int receiveCode() throws ConnectionClosedException {
//		return receiveByte();
//	}


	public int receiveInt() {
		int b1 = receiveByte();
		int b2 = receiveByte();
		int b3 = receiveByte();
		int b4 = receiveByte();

		return b1 << 24 | b2 << 16 | b3 << 8 | b4;
	}

	public String receiveStringParameter() {
		int n = receiveInt();
		if (n < 0) {
			throw new RuntimeException("String length was less than zero: " + n);
		}
		StringBuffer result = new StringBuffer(n);
		for (int i = 1; i <= n; i++) {
			char ch = (char) receiveByte();
			result.append(ch);
		}
		return result.toString();
	}


	public int receiveByte() {
		try {
			int code = socket.getInputStream().read();
			if (code == -1) {
				throw new ConnectionClosedException();
			}
			return code;
		} catch (IOException e) {
			throw new ConnectionClosedException();
		}
	}
}
