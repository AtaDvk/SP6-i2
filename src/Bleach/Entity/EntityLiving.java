package Bleach.Entity;

import Bleach.Level.LevelInteractable;
import Bleach.PhysicsEngine.Force.ExternalForce;

public abstract class EntityLiving extends Entity {

    protected double attackPower; // How much damage the entity deals when it
    protected double health; // Current HP
    protected double healthMax; // Maximum HP
    protected Inventory inventory;
    // attacks.
    protected boolean mayJump;

    protected EntityLiving(Sprite sprite, double x, double y, double r, double health, double attackPower, double velocity) {
	super(sprite, x, y, r);
	this.health = this.healthMax = health;
	this.attackPower = attackPower;
	this.getForce().setVelocity(velocity);
	this.inventory = new Inventory();
	this.mass = 5;
	this.mayJump = true;
    }

    public abstract void AI(LevelInteractable activeLevel);

    public abstract double dealDamage();

    public double getDamage() {
	return this.attackPower;
    }

    public double getHealth() {
	return this.health;
    }

    public double getHealthMax() {
	return this.healthMax;
    }

    public Inventory getInventory() {
	return this.inventory;
    }

    public boolean jump(double jumpVelocity) {
	if (this.mayJump) {
	    addExternalForce(ExternalForce.ForceIdentifier.JUMP, new ExternalForce(Math.toRadians(270), jumpVelocity));
	    this.mayJump = false;
	    return true;
	}
	return false;
    }

    @Override
    public void startFalling() {
	super.startFalling();
	this.mayJump = true;
    }

    public abstract double takeDamage(double amount); // Returns health after
						      // damage.;

    @Override
    public void tick(LevelInteractable activeLevel) {
	super.tick(activeLevel);

	AI(activeLevel);
	this.timePreviousTick = System.currentTimeMillis();
    }
}
