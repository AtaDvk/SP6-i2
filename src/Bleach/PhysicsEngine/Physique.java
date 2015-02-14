package Bleach.PhysicsEngine;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import Bleach.Entity;
import Bleach.EntityTranslatable;
import Bleach.LevelInteractable;
import Bleach.TerrainBlock;
import Bleach.PhysicsEngine.CollisionEngine.Impact;
import Bleach.PhysicsEngine.TranslationEngine.Translatraton;

public class Physique {

	private static long timestamp = System.currentTimeMillis();

	public static boolean step(LevelInteractable currentLevelSetting) {

		// Flag that represents whether if a collision has occured during the
		// physics calculation step
		boolean collisionPresent = false;

		// List that will contain all the entities present on the level
		List<EntityTranslatable> entities = accumulateLevelEntityTranslatables(currentLevelSetting);

		// Current time in nanoseconds
		double deltaTimeSec = (System.currentTimeMillis() - timestamp) / 1000.0;

		// Iterate over entities and calculate physics
		for (EntityTranslatable currentEntity : entities) {

			// predict terrain collision
			
			// Translate entity
			Point2D.Double newPosition = Translatraton.translate((Entity) currentEntity, deltaTimeSec);
			
			// Checks whether if the new position collides with any object in its way
			if (currentEntity.getMass() != 0) {
				for (EntityTranslatable otherEntity : entities) {

					// As long as it doesn't check for a collision with itself...
					if (currentEntity != otherEntity){
						if (Impact.collides(currentEntity, otherEntity)) {

							// Trigger entities' onCollision-actions, if present
							if (((Entity) currentEntity).hasCollisionListener())
								((Entity) currentEntity).getCollisionListener().onCollision((Entity) otherEntity);
							if (((Entity) otherEntity).hasCollisionListener())
								((Entity) otherEntity).getCollisionListener().onCollision((Entity) currentEntity);

							// Flag sets true
							collisionPresent = true;
							Impact.collide(currentEntity, otherEntity);
						}
					}
				}
			}
		}

		// Update timestamp
		timestamp = System.currentTimeMillis();

		return collisionPresent;
	}

	private static List<EntityTranslatable> accumulateLevelEntityTranslatables(LevelInteractable level) {
		List<EntityTranslatable> entities = new ArrayList<>();

		// Accumulate objects on scene
		entities.addAll(level.getLoots());
		entities.addAll(level.getMobiles());
		entities.addAll(level.getPlayers());
		entities.addAll(level.getProjectiles());
		entities.addAll(level.getTerrains());

		return entities;
	}
}
