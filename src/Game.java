import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import Bleach.Bleach;
import Bleach.Entity.Entity;
import Bleach.InputManager.Receptionist;
import Bleach.InputManager.Receptionist.KeyBinding;
import Bleach.Level.Level;
import Bleach.PhysicsEngine.CollisionEngine.CollisionListener;
import Bleach.PhysicsEngine.Force.ExternalForce;
import Bleach.SoundEngine.Boom;

/*
 * This is for testing the game engine.
 * This is where the game developer resides.
 * 
 * */

public class Game {

    public static void main(String[] args) {

	Bleach myGame = Bleach.getInstance();

	Bleach.loadImages("assets/images/assets.json");

	try {
	    Bleach.loadSounds("assets/sounds/assets.json");
	} catch (IOException e2) {
	    e2.printStackTrace();
	} catch (UnsupportedAudioFileException e2) {
	    e2.printStackTrace();
	}

	myGame.setFPS(60);

	myGame.setSize(800, 600);
	myGame.setTitle("My super game!");

	Level firstLevel = new Level(2800, 1200, "Town");

	Player player = new Player(Bleach.getSprite("mushi"), 100, 100);
	firstLevel.addPlayer(player);

	firstLevel.levelBuilder(Bleach.loadLevel("assets/levels/level1.json"));

	// firstLevel.setMusicTrack("melody7");

	myGame.addLevel(firstLevel);

	myGame.init();

	// Adding a hot receptionist Receptionist receptionist = new
	Receptionist receptionist = new Receptionist() {

	    @Override
	    public void handleEvent(ActionEvent event) {
		// TODO

	    }

	    @Override
	    public void handleEvent(MouseEvent event) {
		// TODO

	    }
	};

	receptionist.addKeyBinding(new KeyBinding(KeyStroke.getKeyStroke("pressed A"), "pressed A", new AbstractAction() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		player.getForce().setVectorAngle(Math.PI);
		player.isMoving(true);
		System.out.println("A");
	    }
	}));

	receptionist.addKeyBinding(new KeyBinding(KeyStroke.getKeyStroke("released A"), "released A", new AbstractAction() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		player.isMoving(false);
	    }
	}));

	receptionist.addKeyBinding(new KeyBinding(KeyStroke.getKeyStroke("pressed D"), "pressed D", new AbstractAction() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		player.getForce().setVectorAngle(0);
		player.isMoving(true);
	    }
	}));

	receptionist.addKeyBinding(new KeyBinding(KeyStroke.getKeyStroke("released D"), "released D", new AbstractAction() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		player.isMoving(false);
	    }
	}));

	receptionist.addKeyBinding(new KeyBinding(KeyStroke.getKeyStroke("shift pressed SHIFT"), "shift pressed SHIFT", new AbstractAction() {

	    @Override
	    public void actionPerformed(ActionEvent e) {

		ExternalForce thrust = new ExternalForce(Math.toRadians(270), 120);
		thrust.setOnCollision(new CollisionListener() {

		    @Override
		    public void onCollision(Entity collidedWith) {
			thrust.kill();
		    }
		});

		player.startFalling();
		player.addExternalForce(ExternalForce.ForceIdentifier.JUMP, thrust);

		Boom.playSound("explosion");
	    }
	}));

	((Entity) player).setOnCollision(new CollisionListener() {

	    @Override
	    public void onCollision(Entity collidedWith) {
	    }

	});

	myGame.addReceptionist(receptionist);

	myGame.run();

    }
}
