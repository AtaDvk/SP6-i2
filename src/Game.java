import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import Bleach.Bleach;
import Bleach.Entity.Entity;
import Bleach.InputManager.Receptionist;
import Bleach.InputManager.Receptionist.KeyBinding;
import Bleach.Level.Level;
import Bleach.Loader.Discette;
import Bleach.PhysicsEngine.CollisionEngine.CollisionListener;
import Bleach.PhysicsEngine.Force.ExternalForce;
import Bleach.SoundEngine.Boom;

/*
 * This is for testing the game engine.
 * This is where the game developer resides.
 * 
 * */

public class Game {

	private EnemySpawner enemySpawner;

	private Bleach gameEngine;
	private Player player;
	private Level level;
	private Receptionist inputHandler;

	public Game() {

		try {
			gameEngine = Bleach.getInstance();

			gameEngine.setFPS(60);
			gameEngine.setSize(800, 600);
			gameEngine.setTitle("Squidoes!");

			loadGameData();
			initGameObjects();
			initGameLogics();

			gameEngine.run();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	private void loadGameData() {

		try {

			Bleach.loadImages("assets/images/assets.json");
			Bleach.loadSounds("assets/sounds/assets.json");

		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	}

	private void initGameObjects() throws LineUnavailableException {

		player = new Player();

		level = new Level(800, 8000, "Town");
		level.addPlayer(player);

		level.levelBuilder(Bleach.loadLevel("assets/levels/level1.json"));

		// level.setMusicTrack("melody7");
		Boom.setAmbientSoundtrack(Discette.getSound("bubbles"));
		gameEngine.addLevel(level);

		this.enemySpawner = new EnemySpawner(player, level);

		gameEngine.init();
	}

	private void initGameLogics() {
		inputHandler = new Receptionist() {

			@Override
			public void handleEvent(ActionEvent event) {
			}

			@Override
			public void handleEvent(MouseEvent event) {
			}

		};

		inputHandler.addKeyBinding(new KeyBinding(KeyStroke.getKeyStroke("pressed UP"), "pressed UP", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				player.addExternalForce(null, new ExternalForce(Math.PI * 1.5, 50));
			}

		}));

		inputHandler.addKeyBinding(new KeyBinding(KeyStroke.getKeyStroke("released UP"), "released UP", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}

		}));

		inputHandler.addKeyBinding(new KeyBinding(KeyStroke.getKeyStroke("pressed LEFT"), "pressed LEFT", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				player.addExternalForce(null, new ExternalForce(Math.PI, 50));
			}

		}));

		inputHandler.addKeyBinding(new KeyBinding(KeyStroke.getKeyStroke("released LEFT"), "released LEFT", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}

		}));

		inputHandler.addKeyBinding(new KeyBinding(KeyStroke.getKeyStroke("pressed DOWN"), "pressed DOWN", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				player.addExternalForce(null, new ExternalForce(Math.PI * 0.5, 50));
			}

		}));

		inputHandler.addKeyBinding(new KeyBinding(KeyStroke.getKeyStroke("released DOWN"), "released DOWN", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}

		}));

		inputHandler.addKeyBinding(new KeyBinding(KeyStroke.getKeyStroke("pressed RIGHT"), "pressed RIGHT", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				player.addExternalForce(null, new ExternalForce(0, 50));
			}

		}));

		inputHandler.addKeyBinding(new KeyBinding(KeyStroke.getKeyStroke("released RIGHT"), "released RIGHT", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}

		}));

		inputHandler.addKeyBinding(new KeyBinding(KeyStroke.getKeyStroke("shift pressed SHIFT"), "shift pressed SHIFT", new AbstractAction() {

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

				Boom.playSound("drop");

				JOptionPane.showMessageDialog(gameEngine.getWindowFrame().getContentPane(), "You got eaten by a squid!\nWah-Wah-Waaah", "GAME OVER!", JOptionPane.OK_OPTION);
				System.exit(0);

			}
		});

		gameEngine.addReceptionist(inputHandler);
	}

	public static void main(String[] args) {

		new Game();

	}
}
