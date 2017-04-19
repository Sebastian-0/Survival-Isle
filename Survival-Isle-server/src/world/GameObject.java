package world;

import java.io.Serializable;

import server.Connection;
import util.Point;

@SuppressWarnings("serial")
public class GameObject implements Serializable {

	public static int idCounter;

	public enum AnimationState {
		Idle(0),
		Attacking(1);

		public final int id;

		AnimationState(int id) {
			this.id = id;
		}
	}

	protected int id;
	protected int textureId;
	protected Point position;
	protected Point attackTarget;
	protected AnimationState animationState;

	public GameObject() {
		id = idCounter++;
		
		position = new Point(0, 0);
		attackTarget = new Point(0, 0);
		animationState = AnimationState.Idle;
	}
	
	public void update() { }

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
		sendUpdate(connection);
		connection.sendInt(textureId);
	}

	public void sendUpdate(Connection connection) {
		connection.sendInt(id);
		connection.sendInt((int)position.x);
		connection.sendInt((int)position.y);
		connection.sendInt(animationState.id);

		if (animationState == AnimationState.Attacking) {
			connection.sendInt((int)attackTarget.x);
			connection.sendInt((int)attackTarget.y);
		}
	}

	public void sendDestroy(Connection connection) {
		connection.sendInt(id);
	}

}
