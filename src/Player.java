
import Bleach.EntityLiving;
import Bleach.LevelInteractable;
import Bleach.Entity.Sprite;

public class Player extends EntityLiving {

    public Player(Sprite sprite, double x, double y) {
	super(sprite, x, y, 8, // radius
	80, // health
	1, // attack power
	120 // speed
	);
    }

    @Override
    public void AI(LevelInteractable activeLevel) {
	// this could be used for some debuffs like 'confusion', 'fear' etc.
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
