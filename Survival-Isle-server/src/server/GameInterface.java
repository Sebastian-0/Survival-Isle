package server;

import world.Player;
import world.WallTile;

public interface GameInterface {

	void addObject(Player object);
	void updateObject(Player Object);
	
	WallTile getWallTileAtPosition(int x, int y);
}
