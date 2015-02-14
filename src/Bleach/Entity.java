package Bleach;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import Bleach.PhysicsEngine.CollisionEngine.CollisionListener;
import Bleach.PhysicsEngine.Force.ExternalForce;
import Bleach.PhysicsEngine.Force.Force;

public class Entity implements EntityTranslatable {
	protected Sprite sprite;
	protected double x, y, xOld, yOld;
	protected final double r;
	protected boolean hasRectangularCollisionModel = false;
	//protected boolean isLanded = false;
	protected Force internalForce = new Force(Math.toRadians(0), 0);
	protected Map<ExternalForce.ForceIdentifier, ExternalForce> externalForces = new HashMap<>();
	protected CollisionListener collisionListener = null;

	protected double mass = 0.0;
	protected double weight = 0;

	protected long timePreviousTick;
	protected long timeStartFalling;
	protected boolean bMoving; // Is the entity currently moving?
	protected boolean bDead;

	protected Entity(Sprite sprite, double x, double y, double r) {
		this.sprite = sprite;
		this.x = xOld = x;
		this.y = yOld = y;
		this.r = r;
		timePreviousTick = System.currentTimeMillis();
		timeStartFalling = System.currentTimeMillis();
		bMoving = false;
		bDead = false;
	}

	@Override
	public void addExternalForce(ExternalForce.ForceIdentifier identifier, ExternalForce externalForce) {
		this.externalForces.put(identifier, externalForce);
	}
	
	@Override
	public void die(){
		bDead = true;
	}

	@Override
	public Rectangle2D.Double getBoundary() {
		return new Rectangle2D.Double(x - r, y - r, r * 2, r * 2);
	}

	@Override
	public CollisionListener getCollisionListener() {
		return new CollisionListener() {

			@Override
			public void onCollision(Entity collidedWith) {
				collisionListener.onCollision(collidedWith);
				for (ExternalForce externalForce : externalForces.values())
					if (externalForce.getCollisionListener() != null)
						externalForce.getCollisionListener().onCollision(collidedWith);
			}
		};
	}

	@Override
	public Map<ExternalForce.ForceIdentifier, ExternalForce> getExternalForces() {
		return this.externalForces;
	}

	@Override
	public double getFallingTime() {
		/*
		 * Returns the delta time in seconds since this started falling.
		 */

		return (System.currentTimeMillis() - timeStartFalling) / 1000.0;
	}

	@Override
	public Force getForce() {
		return internalForce;
	}

	@Override
	public double getMass() {
		return mass;
	}

	@Override
	public Point2D.Double getPosition() {
		return new Point2D.Double(x, y);
	}
	
	@Override
	public Point2D.Double getPrevPosition(){
		return new Point2D.Double(xOld, yOld);
	}

	@Override
	public double getRadius() {
		return r;
	}

	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public double getWeight() {
		return weight;
	}

	@Override
	public boolean hasCollisionListener() {
		return this.collisionListener != null ? true : false;
	}

	public boolean hasRectangularCollisionModel() {
		return hasRectangularCollisionModel;
	}
	
	@Override
	public boolean isDead(){
		return bDead;
	}

	@Override
	public boolean isMoving() {
		return bMoving;
	}

	@Override
	public void isMoving(boolean setMoving) {
		bMoving = setMoving;
	}

	public boolean isOutsideoflevel(LevelInteractable activeLevel) {
		if (getBoundary().x >= ((Level) activeLevel).getWidth() || getBoundary().x + getBoundary().width <= 0 || getBoundary().y >= ((Level) activeLevel).getHeight() || getBoundary().y + getBoundary().height <= 0) {

			return true;
		}
		return false;
	}
	
	@Override
	public void startFalling(){
		/*
		 * We start falling after landing so this is basically "landed()"
		 */
		
		timeStartFalling = System.currentTimeMillis();
		this.getExternalForces().remove(ExternalForce.ForceIdentifier.JUMP);
		this.getExternalForces().remove(ExternalForce.ForceIdentifier.GRAVITY);
	}

	public void setHasRectangularCollisionModel(boolean hasRectangularCollisionModel) {
		this.hasRectangularCollisionModel = hasRectangularCollisionModel;
	}

	@Override
	public void setMass(double mass) {
		this.mass = mass;
	}

	@Override
	public void setOnCollision(CollisionListener onCollision) {
		this.collisionListener = onCollision;
	}

	@Override
	public void setPosition(Point2D.Double position) {
		xOld = x;
		yOld = y;
		this.x = position.x;
		this.y = position.y;
	}

	@Override
	public void setWeight(double weight) {
		this.weight = weight;

	}

	public void tick(LevelInteractable activeLevel) {
		
	}
}
