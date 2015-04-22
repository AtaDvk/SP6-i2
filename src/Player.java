import Bleach.Bleach;
import Bleach.Entity.EntityLiving;
import Bleach.Level.LevelInteractable;

public class Player extends EntityLiving {

    public Player() {
	super(Bleach.getSprite("player"), 400 - 30, 8000 - 30, 15, 100, 0, 120);

	this.setMass(-0.001);

	getForce().setVectorAngle(Math.PI * 1.5);
	getForce().setVelocity(10);
    }

    @Override
    public void AI(LevelInteractable activeLevel) {
    }

    @Override
    public double dealDamage() {
	return attackPower;
    }

    @Override
    public double takeDamage(double amount) {
	health = Math.max(0, health - amount);
	return health;
    }
}
