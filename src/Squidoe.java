import Bleach.Bleach;
import Bleach.Entity.EntityLiving;
import Bleach.Level.LevelInteractable;
import Bleach.PhysicsEngine.Force.ExternalForce;

public class Squidoe extends EntityLiving {

    protected Squidoe(double x, double y) {
	super(Bleach.getSprite("Squidoe"), x, y, 50, 100, 10, 25);
	this.setMass(-0.001);
    }

    @Override
    public void AI(LevelInteractable activeLevel) {
	Player player = (Player) activeLevel.getPlayers().get(0);

	double playerX = player.getPosition().getX();
	double playerY = player.getPosition().getY();

	double myX = getPosition().getX();
	double myY = getPosition().getY();

	if (playerX > myX)
	    addExternalForce(null, new ExternalForce(0.0, 10));
	else if (playerX < myX)
	    addExternalForce(null, new ExternalForce(Math.PI, 10));

	if (playerY > myY)
	    addExternalForce(null, new ExternalForce(Math.PI * 0.5, 10));
	else if (playerY < myY)
	    addExternalForce(null, new ExternalForce(Math.PI * 1.5, 10));
    }

    @Override
    public double dealDamage() {
	// TODO
	return 0;
    }

    @Override
    public double takeDamage(double amount) {
	// TODO
	return 0;
    }

}
