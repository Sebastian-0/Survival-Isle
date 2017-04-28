package world;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import util.Point;

public class PathFinder {
	private static final int STEP_COST = 100;
	
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
			Node tile = getTile(state, neighbour);
			if (!tile.isClosed) {
				int stepCost = getStepCost(neighbour);
				int oldCost = tile.fCost;
				tile.fCost = Math.min(oldCost, node.gCost + stepCost + getHeuristic(state, neighbour));
				if (tile.fCost < oldCost) {
					if (tile.isInList)
						state.openNodes.remove(tile);
					state.openNodes.add(tile);
					tile.gCost = node.gCost + stepCost;
					tile.parent = node;
					tile.isInList = true;
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
	
	private int getStepCost(Point position) {
		return (int) (STEP_COST * world.getPathMultiplierAt(position));
	}
	
	private int getHeuristic(State state, Point position) {
		return (int) (Math.abs(position.x - state.end.x) +
					Math.abs(position.y - state.end.y)); 
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

		int gCost;
		int fCost;
		boolean isClosed;
		boolean isInList;
		
		public Node(Point position) {
			this.position = position;
			gCost = 0;
			fCost = Integer.MAX_VALUE;
		}

		@Override
		public int compareTo(Node other) {
			return fCost - other.fCost;
		}
	}
}
