package Bleach;

import Bleach.PhysicsEngine.Force.ExternalForce;

public abstract class EntityLiving extends Entity {

	protected double health; // Current HP
	protected double healthMax; // Maximum HP
	protected double attackPower; // How much damage the entity deals when it attacks.
	protected boolean mayJump;
	protected Inventory inventory;

	protected EntityLiving(Sprite sprite, double x, double y, double r, double health, double attackPower, double velocity) {
		super(sprite, x, y, r);
		this.health = this.healthMax = health;
		this.attackPower = attackPower;
		this.getForce().setVelocity(velocity);
		inventory = new Inventory();
		mass = 5;
		mayJump = true;
	}

	public double getDamage() {
		return attackPower;
	}

	public double getHealth() {
		return health;
	}

	public double getHealthMax() {
		return healthMax;
	}

	public Inventory getInventory() {
		return inventory;
	}
	
	public boolean jump(double jumpVelocity){
		if(mayJump){
			addExternalForce(ExternalForce.ForceIdentifier.JUMP, new ExternalForce(Math.toRadians(270), jumpVelocity));
			mayJump = false;
			return true;
		}
		return false;
	}
	
	@Override
	public void startFalling(){
		super.startFalling();
		mayJump = true;
	}

	@Override
	public void tick(LevelInteractable activeLevel) {
		super.tick(activeLevel);

		AI(activeLevel);
		timePreviousTick = System.currentTimeMillis();
	}

	abstract void AI(LevelInteractable activeLevel);

	abstract double dealDamage();;

	abstract double takeDamage(double amount); // Returns health after damage.
}
