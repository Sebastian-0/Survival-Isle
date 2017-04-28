package world;

import java.io.Serializable;
import java.util.List;

import server.Connection;
import util.Point;

public abstract class GameObject implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static int idCounter;

	public enum AnimationState {
		Idle,
		Attacking,
		Targeting;
	}

	protected int id;
	protected ObjectType type;
	protected Point position;
	protected Point animationTarget;
	protected AnimationState animationState;
	protected boolean shouldBeRemoved;
	protected boolean isDead;
	protected float hp;

	public GameObject() {
		id = idCounter++;
		
		position = new Point(0, 0);
		animationTarget = new Point(0, 0);
		animationState = AnimationState.Idle;
		
		hp = getMaxHp();
	}
	
	public void update(GameInterface game, double deltaTime) { }

	public int getId() {
		return id;
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public void sendCreate(Connection connection) {
		connection.sendInt(type.ordinal());
		sendUpdate(connection);
	}

	public void sendUpdate(Connection connection) {
		connection.sendInt(id);
		connection.sendInt((int)position.x);
		connection.sendInt((int)position.y);
		connection.sendInt(animationState.ordinal());

		switch (animationState) {
		case Attacking:
		case Targeting:
			connection.sendInt((int)animationTarget.x);
			connection.sendInt((int)animationTarget.y);
			break;
		default:
			break;
		}
	}

	public void sendDestroy(Connection connection) {
		connection.sendInt(id);
	}

	public boolean shouldBeRemoved() {
		return shouldBeRemoved;
	}

	protected abstract int getMaxHp();
	
	public void damage(GameInterface game, float amount) {
		hp -= amount;
		if (hp <= 0 && !isDead) {
			die(game);
		}
	}

	protected void die(GameInterface game) {
		isDead = true;
	}
	
	protected GameObject getClosestObject(List<? extends GameObject> objects) {
		if (!objects.isEmpty()) {
			double minDistance = Float.MAX_VALUE;
			GameObject closestObject = null;
			for (GameObject object : objects) {
				double distance = Point.distanceSq(position.x, position.y, object.position.x, object.position.y);
				if (distance < minDistance) {
					minDistance = distance;
					closestObject = object;
				}
			}
			return closestObject;
		}
		return null;
	}
}
