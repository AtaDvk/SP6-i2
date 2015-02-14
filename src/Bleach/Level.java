package Bleach;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import Bleach.Loader.Discette;

public class Level implements LevelInteractable {

    private List<BufferedImage> backgrounds; // A list of textures that are to
    private boolean isScrolling; // Does the level auto-scroll.
    // Used for the parallaxing of backgrounds.
    private String key; // Identifier for this level.
    private List<EntityTranslatable> loots;
    private List<EntityTranslatable> mobiles;
    private int parallaxDistance; // How far away the background layers are.
    private List<EntityTranslatable> players;
    private List<EntityTranslatable> projectiles;
    private int screenWidth, screenHeight;
    private double scrollAngle; // Auto-scroll: scroll towards this angle.
    private double scrollVelocity; // Auto-scroll speed, pixels per second.

    private List<TerrainBlock> terrains;
    private long timePreviousScroll; // Time since last scroll happened. Used to
				     // calculate delta-time.
    // be parallaxed in the
    // background.
    private Point2D.Double viewport; // Offset for scrolling. This points at the
    // middle of the viewport.
    private int width, height;

    public Level() {
	this(800, 600, "Level" + System.currentTimeMillis());
    }

    public Level(Discette.JsonObjectLevel levelData) {
	this();
	levelBuilder(levelData);
    }

    public Level(int width, int height, String key) {
	this.width = this.screenWidth = width;
	this.height = this.screenHeight = height;
	this.key = key;
	this.parallaxDistance = 10;
	this.isScrolling = false;
	this.scrollVelocity = 0;
	this.scrollAngle = 0;
	this.timePreviousScroll = System.currentTimeMillis();

	this.mobiles = new ArrayList<>();
	this.loots = new ArrayList<>();
	this.players = new ArrayList<>();
	this.projectiles = new ArrayList<>();
	this.backgrounds = new ArrayList<>();
	this.terrains = new ArrayList<>();

	this.viewport = new Point2D.Double(0, 0);
	this.screenWidth = this.screenHeight = 1000;
    }

    public Level(String key) {
	this(800, 600, key);
    }

    public void addBackground(BufferedImage img) {
	/*
	 * Add a background image to scroll (parallax), add it behind others if
	 * some exists already.
	 */
	if (img != null)
	    this.backgrounds.add(img);
    }

    public void addLoot(EntityTranslatable loot) {
	if (loot != null)
	    this.loots.add(loot);
    }

    public void addMobile(EntityTranslatable mob) {
	if (mob != null)
	    this.mobiles.add(mob);
    }

    public void addPlayer(EntityTranslatable player) {
	if (player != null)
	    this.players.add(player);
    }

    public void addProjectile(EntityTranslatable proj) {
	if (proj != null)
	    this.projectiles.add(proj);
    }

    public void addTerrainBlock(TerrainBlock terrain) {
	this.terrains.add(terrain);
    }

    public void clearBackgrounds() {
	/* Removes all backgrounds */
	this.backgrounds.clear();
    }

    public void doAutoScroll(boolean doScroll) {
	this.isScrolling = doScroll;
	this.timePreviousScroll = System.nanoTime();
    }

    public void focusEntity(Entity entity, boolean center) {
	int padding = 150;

	if (center) {
	    this.viewport.x = entity.x;
	    this.viewport.y = entity.y;
	} else {
	    if (entity.x > this.viewport.x - this.screenWidth / 2.0 + this.screenWidth - padding) {
		this.viewport.x = (int) entity.x - this.screenWidth / 2.0 + padding;
	    }
	    if (entity.x < this.viewport.x - this.screenWidth / 2.0 + padding) {
		this.viewport.x = (int) entity.x + this.screenWidth / 2.0 - padding;
	    }
	    if (entity.y > this.viewport.y - this.screenHeight / 2.0 + this.screenHeight - padding) {
		this.viewport.y = (int) entity.y - this.screenHeight / 2.0 + padding;
	    }
	    if (entity.y < this.viewport.y - this.screenHeight / 2.0 + padding) {
		this.viewport.y = (int) entity.y + this.screenHeight / 2.0 - padding;
	    }
	}

	// Limit viewport to screen
	this.viewport.x = this.viewport.x - this.screenWidth / 2.0 < 0 ? 0 + this.screenWidth / 2.0 : this.viewport.x;
	this.viewport.y = this.viewport.y - this.screenHeight / 2.0 < 0 ? 0 + this.screenHeight / 2.0 : this.viewport.y;
	this.viewport.x = this.viewport.x + this.screenWidth / 2.0 > this.width ? this.width - this.screenWidth / 2.0 : this.viewport.x;
	this.viewport.y = this.viewport.y + this.screenHeight / 2.0 > this.height ? this.height - this.screenHeight / 2.0 : this.viewport.y;
    }

    @Override
    public int getBackgroundParallaxDistance() {
	return this.parallaxDistance;
    }

    @Override
    public List<BufferedImage> getBackgrounds() {
	return this.backgrounds;
    }

    public int getHeight() {
	return this.height;
    }

    public String getKey() {
	/* Returns the identifier of this level. */
	return this.key;
    }

    @Override
    public List<EntityTranslatable> getLoots() {
	return this.loots;
    }

    @Override
    public List<EntityTranslatable> getMobiles() {
	return this.mobiles;
    }

    @Override
    public List<EntityTranslatable> getPlayers() {
	return this.players;
    }

    @Override
    public List<EntityTranslatable> getProjectiles() {
	return this.projectiles;
    }

    @Override
    public List<TerrainBlock> getTerrains() {
	return this.terrains;
    }

    @Override
    public Point2D.Double getViewport() {
	if (this.isScrolling) {
	    /*
	     * Viewport is set to auto-scroll. Let's calculate the new position
	     * based on the delta-time.
	     */
	    this.viewport.x += Math.cos(this.scrollAngle) * (this.scrollVelocity * (System.currentTimeMillis() - this.timePreviousScroll));
	    this.viewport.y += Math.sin(this.scrollAngle) * (this.scrollVelocity * (System.currentTimeMillis() - this.timePreviousScroll));
	    this.timePreviousScroll = System.currentTimeMillis();
	}

	return this.viewport;
    }

    public int getWidth() {
	return this.width;
    }

    public void levelBuilder(Discette.JsonObjectLevel levelObject) {
	this.width = levelObject.width == null ? this.width : levelObject.width;
	this.height = levelObject.height == null ? this.height : levelObject.height;
	this.key = levelObject.key == null ? this.key : levelObject.key;

	for (Discette.JsonObjectLevel.JsonObjectBacks background : levelObject.backgrounds) {
	    Sprite sprite = null;
	    sprite = Discette.getImage(background.texturekey);
	    if (sprite != null)
		addBackground(sprite.getFrame());
	}

	for (Discette.JsonObjectLevel.JsonObjectTiles tile : levelObject.tiles) {
	    if (tile == null)
		break;

	    Sprite sprite = null;
	    sprite = Discette.getImage(tile.texturekey);

	    if (sprite != null) {
		addTerrainBlock(new TerrainBlock(sprite, tile.gridx, tile.gridy, tile.gridwidth, tile.gridheight, tile.absolutex, tile.absolutey));
	    }
	}
    }

    @Override
    public void removeLoot(EntityTranslatable loot) {
	this.loots.remove(loot);
    }

    @Override
    public void removeMobile(EntityTranslatable mobile) {
	this.mobiles.remove(mobile);
    }

    @Override
    public void removePlayer(EntityTranslatable player) {
	this.players.remove(player);
    }

    @Override
    public void removeProjectile(EntityTranslatable projectile) {
	this.projectiles.remove(projectile);
    }

    @Override
    public void removeTerrain(EntityTranslatable terrain) {
	this.terrains.remove(terrain);
    }

    public int setBackgroundParallaxDistance(int dist) {
	/* Sets the parallax distance for backgrounds. Returns the old distance. */
	int retval = this.parallaxDistance;
	this.parallaxDistance = dist;

	return retval;
    }

    public void setScreenSize(int width, int height) {
	/*
	 * Tells the level the dimensions of the screen.
	 */
	this.screenWidth = width;
	this.screenHeight = height;
    }

    public void setScrollAngle(double angleRad) {
	this.scrollAngle = angleRad;
    }

    public void setScrollSpeed(double speedPPS) {
	this.scrollVelocity = speedPPS;
    }

    public void setViewport(Point2D.Double offset) {
	this.viewport = offset;
    }
}
