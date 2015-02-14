package Bleach.PhysicsEngine.CollisionEngine;

import Bleach.Entity.Entity;

public interface CollisionListener {
    public void onCollision(Entity collidedWith);
}