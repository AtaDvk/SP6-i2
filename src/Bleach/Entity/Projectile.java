package Bleach.Entity;

import Bleach.EntityLiving;

public abstract class Projectile extends Entity {

    /**
     * Used for checking if the projectile came from the player or enemies.
     * Makes it so that enemies don't shoot each other and enables
     * "friendly fire" options for players.
     */
    private EntityLiving owner = null;

    public Projectile(Sprite sprite, double x, double y, double r, double angle, EntityLiving owner) {
	super(sprite, x, y, r);
	this.owner = owner;
	this.getForce().setVectorAngle(angle);
	this.getForce().setVelocity(100);
	bMoving = true;
    }

    public EntityLiving getOwner() {
	return owner;
    }

    public abstract double dealDamage();
}
