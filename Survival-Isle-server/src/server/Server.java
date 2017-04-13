package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class Server {
	
	private Game game = new Game();
	private ClientAccepter clientAccepter;
	private ServerUpdater serverUpdater;

	public void readConsoleInput() throws IOException {
		Scanner in = new Scanner(System.in);
		while (in.hasNext()) {
			switch(in.next().toLowerCase()) {
			case "help":
				System.out.println("Help is not yet implemented.");
				break;
			case "new":
				game = new Game();
				start();
				break;
			case "load":
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(readFilename(in))));
				try {
					game = (Game) ois.readObject();
					start();
				} catch (ClassNotFoundException e) {
					System.out.println("Failed to load game");
					e.printStackTrace();
				}
				break;
			case "save":
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(readFilename(in))));
				out.writeObject(game);
				out.close();
				System.out.println("Game saved");
				break;
			case "stop":
				clientAccepter.stop();
				serverUpdater.stop();
				game.stop();
				break;
			case "quit":
			case "exit":
				in.close();
				System.exit(0);
			default:
				break;
			}
		}
	}

	public void start() {
		clientAccepter = new ClientAccepter(game);
		new Thread(clientAccepter).start();
		
		serverUpdater = new ServerUpdater(game);
		new Thread(serverUpdater).start();
	}

	private String readFilename(Scanner in) {
		String filename = in.nextLine().trim();
		if (filename.equals(""))
			filename = "game.save";
		return filename;
	}
	
	public static void main(String[] args) throws IOException {
		new Server().readConsoleInput();
	}
}
