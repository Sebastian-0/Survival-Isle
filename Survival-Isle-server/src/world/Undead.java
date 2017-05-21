package world;

import java.io.Serializable;

public class Undead extends Enemy implements Serializable {
	
	private static final long serialVersionUID = 1L; 

	private static final double MOVEMENT_TIME = .2;
	private static final int PLAYER_DAMAGE = 20;
	private static final int TILE_DAMAGE = 10;
	
	public Undead(GameObject target, int difficulty) {
		super(difficulty);
		type = ObjectType.Undead;
		this.target = target;
		maxHp = 150 + difficulty * 5;
		hp = getMaxHp();
	}
	
	@Override
	public void update(GameInterface game, double deltaTime) {
		if (target == null || target.shouldBeRemoved) {
			die(game);
			return;
		}
		
		super.update(game, deltaTime);
	}
	
	@Override
	protected void updateTarget(GameInterface game) {
		; //Do nothing
	}

	@Override
	protected int getMaxHp() {
		return maxHp;
	}
	
	@Override
	protected int getPlayerDamage() {
		return PLAYER_DAMAGE;
	}
	
	@Override
	protected int getTileDamage() {
		return TILE_DAMAGE;
	}
	
	@Override
	protected double getMovementTime() {
		return MOVEMENT_TIME;
	}
	
	
	@Override
	protected void spawnDeathEffect(GameInterface game) {
		game.doForEachClient(s->s.sendCreateEffect(EffectType.EnemyDied, getId()));
	}
}
