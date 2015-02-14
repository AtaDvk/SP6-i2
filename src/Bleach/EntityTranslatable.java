package Bleach;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import Bleach.PhysicsEngine.CollisionEngine.CollisionListener;
import Bleach.PhysicsEngine.Force.ExternalForce;
import Bleach.PhysicsEngine.Force.Force;

public interface EntityTranslatable {

	public void addExternalForce(ExternalForce.ForceIdentifier identifier, ExternalForce externalForce);
	
	public void die();

	public Rectangle2D.Double getBoundary();

	public CollisionListener getCollisionListener();

	public Map<ExternalForce.ForceIdentifier, ExternalForce> getExternalForces();

	public double getFallingTime();

	public Force getForce();

	public double getMass();

	public Point2D.Double getPosition();
	
	public Point2D.Double getPrevPosition();

	public double getRadius();

	public double getWeight();

	public boolean hasCollisionListener();

	public boolean isDead();
	
	public boolean isMoving();

	public void isMoving(boolean setMoving);
	
	public void startFalling();

	public void setMass(double mass);

	public void setOnCollision(CollisionListener onCollision);

	public void setPosition(Point2D.Double position);

	public void setWeight(double weight);

}
