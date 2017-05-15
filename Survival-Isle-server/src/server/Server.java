package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Scanner;

import util.Point;

public class Server {
	
	private final int port;
	private Game game;
	private ClientAccepter clientAccepter;
	private ServerUpdater serverUpdater;
	private boolean running;

	public Server(int port) {
		this.port = port;
	}

	public void readConsoleInput() {
		Scanner in = new Scanner(System.in);
		while (in.hasNext()) {
			switch(in.next().toLowerCase()) {
			case "help":
				System.out.println("Help is not yet implemented.");
				break;
			case "new":
				Point size = readSize(in);
				if (!running) {
					newGame((int) size.x, (int) size.y);
					System.out.println("Starting game...");
					start();
				} else {
					System.out.println("Can not start game if already started");
				}
				break;
			case "load":
				String filename = readFilename(in);
				if (!running) {
					System.out.println("Loading game...");
					load(filename);
					start();
				} else {
					System.out.println("Can not load game if already started");
				}
				break;
			case "save":
				save(in);
				break;
			case "list":
				List<String> players = game.getPlayerNames();
				System.out.println("Currently there are " + players.size() + " player(s) online:");
				players.stream().forEach(p->System.out.println(p));
				break;
			case "stop":
				stop();
				break;
			case "quit":
			case "exit":
				stop();
				in.close();
				System.exit(0);
			default:
				break;
			}
		}
	}

	private Point readSize(Scanner in) {
		String[] sizes = in.nextLine().trim().split("\\s");
		Point size = new Point();
		try {
			switch (sizes.length) {
			case 2:
				size.x = Integer.parseInt(sizes[0]);
				size.y = Integer.parseInt(sizes[1]);
				break;
			case 1:
				if (!sizes[0].isEmpty()) {
					size.x = size.y = Integer.parseInt(sizes[0]);
					break;
				}
			default:
				size.x = 200;
				size.y = 150;
				break;
			}
			
			if (size.x < 50 || size.y < 50)
				System.out.println("Size too small");
		} catch (NumberFormatException e) {
			System.out.println("Invalid world size");
		}
		
		if (size.x < 50)
			size.x = 50;
		if (size.y < 50)
			size.y = 50;
		
		System.out.printf("Creating world of size: (%.0f, %.0f)\n", size.x, size.y);
		
		return size;
	}

	public void newGame(int width, int height) {
		game = new Game(width, height);
	}

	public void load(String filename) {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(filename)))) {
			game = (Game) ois.readObject();
		} catch (ClassNotFoundException | IOException e) {
			game = null;
			System.out.println("Failed to load game");
			System.out.println(e.getMessage());
		}
	}

	public void save(Scanner in) {
		if (game == null) {
			System.out.println("No game has been run yet");
		} else {
			try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(readFilename(in))))) {
				out.writeObject(game);
				System.out.println("Game saved");
			} catch (IOException e) {
				System.out.println("Failed to save game");
				System.out.println(e.getMessage());
			}
		}
	}

	public void start() {
		if (game != null) {
			running = true;

			clientAccepter = new ClientAccepter(game, port);
			if (!clientAccepter.isPortAvailable()) {
				System.out.println("The port " + port + " is already in use, start failed!");
				game = null;
				running = false;
			} else {
				clientAccepter.start();
				
				serverUpdater = new ServerUpdater(game, ()->stop());
				serverUpdater.start();
				
				System.out.println("Game started");
			}
		}
	}

	public void stop() {
		if (running) {
			running = false;
			clientAccepter.close();
			serverUpdater.close();
			game.stop();
			System.out.println("Game stopped");
		}
	}

	private String readFilename(Scanner in) {
		String filename = in.nextLine().trim();
		if (filename.equals(""))
			filename = "game.save";
		return filename;
	}
	
	public static void main(String[] args) {
		int port = 1337;
		if (args.length >= 1)
			port = Integer.parseInt(args[0]);
		new Server(port).readConsoleInput();
	}
}
