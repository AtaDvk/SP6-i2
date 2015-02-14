package Bleach;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import Bleach.InputManager.Receptionist;
import Bleach.InputManager.Receptionist.KeyBinding;
import Bleach.Loader.Discette;
import Bleach.PhysicsEngine.Physique;
import Bleach.Renderer.Picasso;

public class Bleach extends JPanel {

    // Singleton pattern
    private static Bleach instance = null;

    // Pointer to the active level.
    private Level activeLevel;

    // FPS limiter, limits how often the game is rendered.
    private double FPS = 60;
    // A handle to the window.
    private JFrame jWindow;
    // All the levels.
    private Map<String, Level> levels = new HashMap<>();
    // A (set of) bool to see if the game is paused by any subsystem.
    private Map<PauseType, Boolean> pause = new HashMap<>();

    private Receptionist receptionist = null;

    private Picasso renderer;

    private long timeDebug;

    // Used for delta-time in the game loop (e.g. FPS limiting)
    private double timePreviousLoop;

    // Used for delta-time in the rendering (e.g. calculating actual rendering
    // FPS)
    private double timePreviousRender;

    private int winHeight = 600; // Default height;
    private String winTitle = "Game window"; // Default title;;
    private int winWidth = 800; // Default width;

    private Bleach() {
	// Let's try to HW-accelerate stuff.
	System.setProperty("sun.java2d.opengl", "True");
	this.timePreviousLoop = this.timePreviousRender = System.currentTimeMillis();
    }

    public static Bleach getInstance() {
	if (instance == null)
	    instance = new Bleach();
	return instance;
    }

    public static Sprite getSprite(String key) {
	return Discette.getImage(key);
    }

    public static BufferedImage getTexture(String key) {
	Sprite sprite = Discette.getImage(key);

	return sprite == null ? null : sprite.getFrame();
    }

    public static void loadImages(String assetJsonPath) {
	Discette.loadImages(assetJsonPath);
    }

    public static Discette.JsonObjectLevel loadLevel(String assetJsonPath) {
	return Discette.loadLevel(assetJsonPath);
    }

    public static void loadSounds(String assetJsonPath) throws IOException, UnsupportedAudioFileException {
	Discette.loadSound(assetJsonPath);
    }

    private void gameLoop() {

	boolean quit = false;
	boolean paused = false;
	double deltaTime;

	while (!quit) {
	    deltaTime = System.currentTimeMillis() - this.timePreviousLoop;

	    // Simulate work
	    while (System.currentTimeMillis() - this.timePreviousLoop < 34) {
		Thread.yield();
	    }

	    if (!isPaused()) {
		/* Physics engine */
		Physique.step(this.activeLevel);

		/* Let's iterate entities and tick() and/or delete them */
		Iterator<EntityTranslatable> iter;

		/* Projectiles heartbeat */
		iter = this.activeLevel.getProjectiles().iterator();
		EntityTranslatable projectile;
		while (iter.hasNext()) {
		    projectile = iter.next();
		    if (projectile.isDead()) {
			iter.remove();
		    } else {
			((Entity) projectile).tick(this.activeLevel);
		    }
		}

		/* Mobiles heartbeat */
		iter = this.activeLevel.getMobiles().iterator();
		EntityTranslatable mobile;
		while (iter.hasNext()) {
		    mobile = iter.next();
		    if (mobile.isDead()) {
			iter.remove();
		    } else {
			((Entity) mobile).tick(this.activeLevel);
		    }
		}

		/* Players heartbeat */
		iter = this.activeLevel.getPlayers().iterator();
		EntityTranslatable player;
		while (iter.hasNext()) {
		    player = iter.next();
		    if (player.isDead()) {
			iter.remove();
		    } else {
			((Entity) player).tick(this.activeLevel);
			this.activeLevel.focusEntity(((Entity) player), false);
		    }
		}
	    }
	    paintComponent(this.getGraphics());
	    this.timePreviousLoop = System.currentTimeMillis();
	}
    }

    public boolean isPaused() {
	/* Check if any subsystem is pausing the game */
	for (Entry<PauseType, Boolean> entry : this.pause.entrySet()) {
	    if (entry.getValue()) {
		return true;
	    }
	}

	return false;
    }

    private boolean setActiveLevel(String key) {
	Level newLevel = null;
	newLevel = this.levels.get(key);
	if (newLevel != null)
	    this.activeLevel = newLevel;

	return newLevel != null;
    }

    public void addLevel(Level level) {
	if (level != null) {
	    level.setScreenSize(this.winWidth, this.winHeight);
	    this.levels.put(level.getKey(), level);

	    // No active level has been set, let's set it to this one.
	    if (this.activeLevel == null)
		this.activeLevel = level;
	}
    }

    public void addReceptionist(Receptionist receptionist) {
	this.receptionist = receptionist;

	for (KeyBinding keyBinding : receptionist.getKeyBindings()) {
	    Bleach.this.getInputMap().put(keyBinding.getKey(), keyBinding.getActionMapKey());
	    Bleach.this.getActionMap().put(keyBinding.getActionMapKey(), keyBinding.getAction());
	}

	this.addMouseMotionListener(new MouseMotionListener() {

	    @Override
	    public void mouseDragged(MouseEvent e) {
		Bleach.this.receptionist.handleEvent(e);
	    }

	    @Override
	    public void mouseMoved(MouseEvent event) {
		Bleach.this.receptionist.handleEvent(event);
	    }
	});

    }

    /**
     * This sets up the window and starts the game. *
     **/
    public void init() {

	// Set the size of this JPanel before inserting it into the window.
	setSize(this.winWidth, this.winHeight);

	// Sometimes setSize() just fails. Go figure.
	setPreferredSize(new Dimension(this.winWidth, this.winHeight));

	// This is a pointer to this JPanel used in the Event Dispatch Thread
	// (EDT).
	final Bleach EDTpointerToPanel = this;

	// This is the window title variable used in the Event Dispatch Thread
	// (EDT).
	final String EDTwindowTitle = this.winTitle;

	try {
	    SwingUtilities.invokeAndWait(new Runnable() {
		/*
		 * Event Dispatch Thread - prevents potential race conditions
		 * that could lead to deadlock.
		 */
		@Override
		public void run() {
		    Bleach.this.jWindow = new JFrame(EDTwindowTitle);
		    Bleach.this.jWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    Bleach.this.jWindow.setResizable(false);
		    Bleach.this.jWindow.add(EDTpointerToPanel);

		    // Fixes a bug that sometimes adds 10 pixels to width and
		    // height. Weird stuff.
		    Bleach.this.jWindow.pack();
		    Bleach.this.jWindow.pack();

		    // Center the window on the primary monitor.
		    Bleach.this.jWindow.setLocationRelativeTo(null);

		    Bleach.this.jWindow.setVisible(true);
		}
	    });
	} catch (InvocationTargetException | InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	setDoubleBuffered(true);
	setFocusable(true);
	setBackground(Color.cyan);

	this.renderer = new Picasso(this.winWidth, this.winHeight);
    }

    public void init(int windowWidth, int windowHeight, String windowTitle) {
	this.winWidth = windowWidth;
	this.winHeight = windowHeight;
	this.winTitle = windowTitle;
	init();
    }

    @Override
    public void paintComponent(Graphics g) {
	double deltaTime = System.currentTimeMillis() - this.timePreviousRender;

	if (this.FPS > 0 && deltaTime < 1000.0 / this.FPS)
	    return;

	double actualFPS = (1000.0 / Math.max(1, (deltaTime)));

	this.timeDebug += deltaTime;
	if (this.timeDebug >= 1000) {
	    this.timeDebug = 0;
	    this.renderer.clearDebugLines();
	    this.renderer.addDebugLine("FPS: " + (int) actualFPS);
	}

	this.renderer.render(g, this.activeLevel);

	this.timePreviousRender = System.currentTimeMillis();
    }

    public void run() {
	gameLoop();
    }

    public double setFPS(double newFPS) {
	/* Sets the FPS, returns the old FPS. */
	double retval = this.FPS;
	this.FPS = newFPS;
	return retval;
    }

    public void setTitle(String title) {
	this.winTitle = title;
    }

    /**
     * The game can be paused by many reasons, this is an enumeration of those.
     **/
    private enum PauseType {
	// In-game information is displayed (e.g. a splash-screen is displayed,
	// a book, notepad, messageboard etc is displayed, inventory is
	// displayed)
	GAMEMESSAGE,

	// The loader is working (e.g. save game)
	LOADER,

	// The user used the pause functionality.
	USER
    }
}
