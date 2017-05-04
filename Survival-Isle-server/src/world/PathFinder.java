package world;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import util.Point;

public class PathFinder {
	private static final int STEP_COST = 1;
	
	private ServerWorld world;

	public PathFinder(ServerWorld world) {
		this.world = world;
	}
	
	public List<Point> search(Point start, Point end) {
		if (!start.equals(end)) {
			State state = new State(world.getWidth(), world.getHeight(), end);
			Node startTile = getTile(state, start);
			addNeighbours(state, startTile);
			startTile.isClosed = true;
			
			while (!state.openNodes.isEmpty()) {
				Node node = state.openNodes.poll();
				node.isClosed = true;
				if (node.position.equals(end)) {
					return constructPath(node);
				}
				addNeighbours(state, node);
			}
		}
		
		return new LinkedList<Point>();
	}
	
	private void addNeighbours(State state, Node node) {
		addNeighbour(state, node, 1, 0);
		addNeighbour(state, node, -1, 0);
		addNeighbour(state, node, 0, 1);
		addNeighbour(state, node, 0, -1);
	}
	
	private void addNeighbour(State state, Node node, int dx, int dy) {
		Point neighbour = new Point(node.position.x + dx, node.position.y + dy);
		if (isValidPosition(state, neighbour)) {
			Node neighbourTile = getTile(state, neighbour);
			if (!neighbourTile.isClosed) {
				float stepCost = getStepCost(neighbour);
				float oldCost = neighbourTile.fCost;
				neighbourTile.fCost = Math.min(oldCost, node.gCost + stepCost + getHeuristic(state, neighbour));
				if (neighbourTile.fCost < oldCost) {
					if (neighbourTile.isInList)
						state.openNodes.remove(neighbourTile);
					state.openNodes.add(neighbourTile);
					neighbourTile.gCost = node.gCost + stepCost;
					neighbourTile.parent = node;
					neighbourTile.isInList = true;
				}
			}
		}
	}
	
	private boolean isValidPosition(State state, Point position) {
		boolean isInBounds = position.x >= 0 && position.y >= 0 && 
				position.x < state.tiles.length && position.y < state.tiles[0].length;
		return isInBounds;
	}
	
	private Node getTile(State state, Point position) {
		Node node = state.tiles[(int) position.x][(int) position.y];
		if (node == null) {
			node = new Node(position);
			state.tiles[(int) position.x][(int) position.y] = node;
		}
		return node;
	}
	
	private float getStepCost(Point position) {
//		if (world.getAdditionalPathCostAt(position) > 1) {
//			System.out.println("Additional: " + world.getAdditionalPathCostAt(position) + " vs. " + STEP_COST * world.getPathMultiplierAt(position));
//		}
		return STEP_COST * world.getPathMultiplierAt(position) + world.getAdditionalPathCostAt(position);
	}
	
	private float getHeuristic(State state, Point position) {
		return Math.abs(position.x - state.end.x) + Math.abs(position.y - state.end.y); 
	}
	
	private List<Point> constructPath(Node node) {
		List<Point> path = new LinkedList<>();
		while (node != null) {
			path.add(0, node.position);
			node = node.parent;
		}
		return path;
	}


	private static class State {
		Node[][] tiles;
		PriorityQueue<Node> openNodes;
		Point end;
		
		public State(int width, int height, Point end) {
			tiles = new Node[width][height];
			this.end = end;
			
			openNodes = new PriorityQueue<>();
		}
	}


	private static class Node implements Comparable<Node> {
		Point position;
		Node parent;

		float gCost;
		float fCost;
		boolean isClosed;
		boolean isInList;
		
		public Node(Point position) {
			this.position = position;
			gCost = 0;
			fCost = Integer.MAX_VALUE;
		}

		@Override
		public int compareTo(Node other) {
			return (int) Math.signum(fCost - other.fCost);
		}
	}
}
