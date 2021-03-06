package world;

import java.util.HashMap;

import util.Point;
import world.WallTile.WallType;

public class Turret extends BuildableObject {
	
	private static final long serialVersionUID = 1L;
	private final double RELOAD_TIME = 0.3f;
	private final int ATTACK_RANGE = 3; 
	private final int ATTACK_DAMAGE = 35;
	private double reloadTimer = 0;
	
	public Turret() {
		type = ObjectType.Turret;
		resourceCost = new HashMap<ItemType, Integer>();
		resourceCost.put(ItemType.Stone, 5);
		resourceCost.put(ItemType.Wood, 5);
	}
	
	@Override
	public GameObject instanciate(Point position, GameInterface game) {
		Turret turret = new Turret();
		turret.position = position;
		game.getWorld().addWallTileAtPosition(position, WallType.TurretBase);
		
		return turret;
	}
	
	@Override
	public void update(GameInterface game, double deltaTime) {
		super.update(game, deltaTime);
		
		WallTile tile = game.getWorld().getWallTileAtPosition(position);
		if (tile == null || tile.getType() != WallType.TurretBase) {
			shouldBeRemoved = true;
		}
		
		if (reloadTimer > 0) {
			reloadTimer -= deltaTime;
		} else {
			GameObject enemy = getClosestObject(game.getObjects().getObjectsOfType(Enemy.class));
			if (enemy != null && squareDistanceTo(enemy) < ATTACK_RANGE*ATTACK_RANGE) {
				reloadTimer = RELOAD_TIME;
				enemy.damage(game, ATTACK_DAMAGE);
				game.doForEachClient(c->c.sendCreateEffect(EffectType.Projectile, 0, id, enemy.id));
				animationState = AnimationState.Targeting;
				animationTarget.set(enemy.position);
				game.doForEachClient(c -> c.sendUpdateObject(this));
			}
		}
	}
	

	private float squareDistanceTo(GameObject target) {
		float dx = position.x - target.getPosition().x;
		float dy = position.y - target.getPosition().y;
		return dx*dx + dy*dy;
	}

	@Override
	protected int getMaxHp() {
		return 0;
	}
}
