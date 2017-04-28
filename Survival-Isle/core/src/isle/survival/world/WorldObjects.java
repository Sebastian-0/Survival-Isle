package isle.survival.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import server.Connection;
import world.GameObject.AnimationState;
import world.World;

public class WorldObjects {
	private TextureBase textureBase;
	private SpriteBatch spriteBatch;
	
	private Array<NetworkObject> objects;
	private NetworkObject player;
	
	public WorldObjects(TextureBase textureBase, SpriteBatch spriteBatch) {
		this.textureBase = textureBase;
		this.spriteBatch = spriteBatch;
		objects = new Array<>();
		
	}
	
	public void addObject(NetworkObject object) {
		objects.add(object);
	}
	
	
	public NetworkObject getPlayer() {
		return player;
	}

	public NetworkObject getObject(int id) {
		for (NetworkObject object : objects) {
			if (object.getId() == id)
				return object;
		}
		return null;
	}
	
	public void update(float deltaTime) {
		objects.forEach(object -> object.update(deltaTime));
	}
	
	public void draw(float xOffset, float yOffset) {
		for (NetworkObject object : objects) {
			if (isPointOnScreen(object.getX() * World.TILE_WIDTH,
								object.getY() * World.TILE_HEIGHT,
								xOffset,
								yOffset)) {
				object.draw(spriteBatch, textureBase, xOffset, yOffset);
			}
		}
	}
	
	private boolean isPointOnScreen(float x, float y, float xOffset, float yOffset) {
		int dx = 32;
		int dy = 32;
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();
		if (x >= xOffset - dx && x < xOffset + w + dx && y >= yOffset - dy && y < yOffset + h + dy)
			return true;
		return false;
	}

	
	public void createObjects(Connection connection) {
		int amount = connection.receiveInt();
		for (int i = 0; i < amount; i++) {
			int textureId = connection.receiveInt();
			int id = connection.receiveInt();
			NetworkObject object = new NetworkObject(0, 0, id, textureId);
			objects.add(object);
			updateObject(connection, id);
			object.jumpToTarget();
			
		}
	}

	public void updateObjects(Connection connection) {
		int amount = connection.receiveInt();
		for (int i = 0; i < amount; i++) {
			int id = connection.receiveInt();
			updateObject(connection, id);
		}
	}

	private void updateObject(Connection connection, int id) {
		NetworkObject object = getObject(id);
		int x = connection.receiveInt();
		int y = connection.receiveInt();
		object.setPosition(x, y);
		
		AnimationState animation = AnimationState.values()[connection.receiveInt()];
		object.setAnimation(animation);
		switch (animation) {
		case Attacking:
		case Targeting:
			x = connection.receiveInt();
			y = connection.receiveInt();
			object.setAttackTarget(x, y);
			break;
		default:
			break;
		}
	}

	public void destroyObjects(Connection connection) {
		int amount = connection.receiveInt();
		for (int i = 0; i < amount; i++) {
			int id = connection.receiveInt();
			objects.removeValue(new NetworkObject(0, 0, id, 0), false);
			
			if(id == player.getId())
				player = null;
		}
	}

	public void setPlayer(int id) {
		for (NetworkObject object : objects) {
			if (object.getId() == id) {
				player = object;
				break;
			}
		}
	}
}
