package com.yago.starfishcollector;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

public class BaseActor extends Group {

	// Animation
	private Animation<TextureRegion> animation;
	private float elapsedTime;
	private boolean animationPaused;

	// Velocity represents how the position is changing
	private Vector2 velocityVec;

	// Acceleration
	private Vector2 accelerationVec;
	private float acceleration;

	// Deceleration
	private float maxSpeed;
	private float deceleration;

	// Collision
	private Polygon boundaryPolygon;

	// World Boundaries
	private static Rectangle worldBounds;

	public BaseActor(float x, float y, Stage s) {
		super();
		setPosition(x, y);
		s.addActor(this);

		animation = null;
		elapsedTime = 0;
		animationPaused = false;

		velocityVec = new Vector2(0, 0);

		accelerationVec = new Vector2(0, 0);
		acceleration = 0;

		maxSpeed = 1000;
		deceleration = 0;
	}

	public void setAnimation(Animation<TextureRegion> anim) {
		animation = anim;
		TextureRegion tr = animation.getKeyFrame(0);
		float w = tr.getRegionWidth();
		float h = tr.getRegionHeight();
		setSize(w, h);
		setOrigin(w / 2, h / 2);

	}

	public void setAnimationPaused(boolean pause) {
		animationPaused = pause;
	}

	// Loading animation with multiple files
	public Animation<TextureRegion> loadAnimationFromFiles(String[] fileNames, float frameDuration, boolean loop) {
		int fileCount = fileNames.length;
		Array<TextureRegion> textureArray = new Array<TextureRegion>();

		for (int n = 0; n < fileCount; n++) {
			String fileName = fileNames[n];
			Texture texture = new Texture(Gdx.files.internal(fileName));
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			textureArray.add(new TextureRegion(texture));
		}
		Animation<TextureRegion> anim = new Animation<TextureRegion>(frameDuration, textureArray);

		if (loop)
			anim.setPlayMode(Animation.PlayMode.LOOP);
		else
			anim.setPlayMode(Animation.PlayMode.NORMAL);
		if (animation == null)
			setAnimation(anim);

		return anim;
	}

	// Loading animation with a spritesheet
	public Animation<TextureRegion> loadAnimationFromSheet(String fileName, int rows, int cols, float frameDuration,
			boolean loop) {
		Texture texture = new Texture(Gdx.files.internal(fileName), true);
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		int frameWidth = texture.getWidth() / cols;
		int frameHeight = texture.getHeight() / rows;

		TextureRegion[][] temp = TextureRegion.split(texture, frameWidth, frameHeight);

		Array<TextureRegion> textureArray = new Array<TextureRegion>();

		for (int r = 0; r < rows; r++)
			for (int c = 0; c < cols; c++)
				textureArray.add(temp[r][c]);

		Animation<TextureRegion> anim = new Animation<TextureRegion>(frameDuration, textureArray);

		if (loop)
			anim.setPlayMode(Animation.PlayMode.LOOP);
		else
			anim.setPlayMode(Animation.PlayMode.NORMAL);

		if (animation == null)
			setAnimation(anim);

		return anim;
	}

	// Loading one single frame image
	public Animation<TextureRegion> loadTexture(String fileName) {
		String[] fileNames = new String[1];
		fileNames[0] = fileName;
		return loadAnimationFromFiles(fileNames, 1, true);
	}

	// Returns if animation is finished
	public boolean isAnimationFinished() {
		return animation.isAnimationFinished(elapsedTime);
	}

	public void setSpeed(float speed) {
		// if length is zero, then assume motion angle is 0 degrees
		if (velocityVec.len() == 0)
			velocityVec.set(speed, 0);
		else
			velocityVec.setLength(speed);
	}

	public float getSpeed() {
		return velocityVec.len();
	}

	public void setMotionAngle(float angle) {
		velocityVec.setAngleDeg(angle);
	}

	// vai ser conveniente para que possamos saber qual direção o personagem esta
	// indo
	public float getMotionAngle() {
		return velocityVec.angleDeg();
	}

	public boolean isMoving() {
		return (getSpeed() > 0);
	}

	public void setAcceleration(float acc) {
		acceleration = acc;
	}

	public void accelerateAtAngle(float angle) {
		accelerationVec.add(new Vector2(acceleration, 0).setAngleDeg(angle));
	}

	public void accelerateForward() {
		accelerateAtAngle(getRotation());
	}

	public void setMaxSpeed(float ms) {
		maxSpeed = ms;
	}

	public void setDeceleration(float dec) {
		deceleration = dec;
	}

	public void applyPhysics(float dt) {
		// Apply acceleration
		velocityVec.add(accelerationVec.x * dt, accelerationVec.y * dt);

		float speed = getSpeed();

		// decrease speed (decelerate) when not accelerating
		if (accelerationVec.len() == 0)
			speed -= deceleration * dt;

		// keep speed within set bounds
		speed = MathUtils.clamp(speed, 0, maxSpeed);

		// update velocity
		setSpeed(speed);

		// apply velocity
		moveBy(velocityVec.x * dt, velocityVec.y * dt);

		// reset acceleration
		accelerationVec.set(0, 0);

	}

	// Creates a rectangular polygon
	public void setBoundaryRectangle() {
		float w = getWidth();
		float h = getHeight();
		float[] vertices = { 0, 0, w, 0, w, h, 0, h };
		boundaryPolygon = new Polygon(vertices);
	}

	// Avoid the issue of overlapping corners that only contain transparent pixels
	// Ellipse with collision
	public void setBoundaryPolygon(int numSides) {
		float w = getWidth();
		float h = getHeight();

		float[] vertices = new float[2 * numSides];
		for (int i = 0; i < numSides; i++) {
			float angle = i * 6.28f / numSides;
			// x-coordinate
			vertices[2 * i] = w / 2 * MathUtils.cos(angle) + w / 2;
			// y-coordinate
			vertices[2 * i + 1] = h / 2 * MathUtils.sin(angle) + h / 2;
		}
		boundaryPolygon = new Polygon(vertices);
	}

	/*
	 * Next its a method that returns the collision polygon for this actor,
	 * adjusting it according to the Actor object's current parameters(such as
	 * position and rotation)
	 */
	public Polygon getBoundaryPolygon() {
		boundaryPolygon.setPosition(getX(), getY());
		boundaryPolygon.setOrigin(getOriginX(), getOriginY());
		boundaryPolygon.setRotation(getRotation());
		boundaryPolygon.setScale(getScaleX(), getScaleY());
		return boundaryPolygon;
	}

	// Since polygons can't overlap, this method is needed
	public boolean overlaps(BaseActor other) {
		Polygon poly1 = this.getBoundaryPolygon();
		Polygon poly2 = other.getBoundaryPolygon();

		// Initial test to improve performance
		if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle()))
			return false;

		return Intersector.overlapConvexPolygons(poly1, poly2);
	}

	/*
	 * Recall that the actor method setPosition actually sets the bottom left corner
	 * of an Actor object to a given location. So we add this method
	 */
	public void centerAtPosition(float x, float y) {
		setPosition(x - getWidth() / 2, y - getHeight() / 2);
	}

	// It center the actor on the other actor's position
	public void centerAtActor(BaseActor other) {
		centerAtPosition(other.getX() + other.getWidth() / 2, other.getY() + other.getHeight() / 2);
	}

	// It sets the opacity
	public void setOpacity(float opacity) {
		this.getColor().a = opacity;
	}

	// It prevents an actor to overlap the other
	public Vector2 preventOverlap(BaseActor other) {
		Polygon poly1 = this.getBoundaryPolygon();
		Polygon poly2 = other.getBoundaryPolygon();

		if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle()))
			return null;

		MinimumTranslationVector mtv = new MinimumTranslationVector();
		boolean polygonOverlap = Intersector.overlapConvexPolygons(poly1, poly2, mtv);

		if (!polygonOverlap)
			return null;
		this.moveBy(mtv.normal.x * mtv.depth, mtv.normal.y * mtv.depth);
		return mtv.normal;
	}

	// This method will helps us when it comes to managing the actors
	public static ArrayList<BaseActor> getList(Stage stage, String className) {
		ArrayList<BaseActor> list = new ArrayList<BaseActor>();
		Class<?> theClass = null;
		try {
			theClass = Class.forName(className);
		} catch (Exception error) {
			error.printStackTrace();
		}

		for (Actor a : stage.getActors()) {
			if (theClass.isInstance(a))
				list.add((BaseActor) a);
		}
		return list;

	}

	// This method let us know how many actors are left on the stage.
	public static int count(Stage stage, String className) {
		return getList(stage, className).size();
	}

	// Enables us to store the size of the game world
	public static void setWorldBounds(float width, float height) {
		worldBounds = new Rectangle(0, 0, width, height);
	}

	public static void setWorldBounds(BaseActor ba) {
		setWorldBounds(ba.getWidth(), ba.getHeight());
	}

	// To keep an actor withing the rectangular area defined by world bounds
	public void boundToWorld() {
		// check left edge
		if (getX() < 0)
			setX(0);
		// check right edge
		if (getX() + getWidth() > worldBounds.width)
			setX(worldBounds.width - getWidth());
		// check bottom edge
		if (getY() < 0)
			setY(0);
		// check top edge
		if (getY() + getHeight() > worldBounds.height)
			setY(worldBounds.height - getHeight());
	}

	// Moving the camera
	public void alignCamera() {
		Camera cam = this.getStage().getCamera();
		Viewport v = this.getStage().getViewport();

		// Center camera on actor
		cam.position.set(this.getX() + this.getOriginX(), this.getY() + this.getOriginY(), 0);

		// Bound camera to layout
		cam.position.x = MathUtils.clamp(cam.position.x, cam.viewportWidth / 2,
				worldBounds.width - cam.viewportWidth / 2);
		cam.position.y = MathUtils.clamp(cam.position.y, cam.viewportHeight / 2,
				worldBounds.height - cam.viewportHeight / 2);
		cam.update();
	}
	
	// feature for wrapping around the screen
	public void wrapAroundWorld() {
		if(getX() + getWidth() < 0)
			setX(worldBounds.width);
		
		if(getX() > worldBounds.width)
			setX(-getWidth());
		
		if(getY() + getHeight() < 0)
			setY(worldBounds.height);
		
		if(getY() > worldBounds.height)
			setY(-getHeight());
	}
	
	// This method is a way to determine if two actors are "close"
	public boolean isWithingDistance(float distance,BaseActor other) {
		Polygon poly1 = this.getBoundaryPolygon();
		float scaleX = (this.getWidth() + 2 * distance) / this.getWidth();
		float scaleY = (this.getHeight() + 2 * distance) / this.getHeight();
		poly1.setScale(scaleX, scaleY);
		
		Polygon poly2 = other.getBoundaryPolygon();
		
		//initial test to improve performace
		if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle()))
			return false;
		return Intersector.overlapConvexPolygons(poly1,poly2);
	}
	
	// to give access to SceneActions class
	public static Rectangle getWorldBounds() {
		return worldBounds;
	}

	public void draw(Batch batch, float parentAlpha) {

		// apply color tint effect
		Color c = getColor();
		batch.setColor(c.r, c.g, c.b, c.a);

		if (animation != null && isVisible()) {
			batch.draw(animation.getKeyFrame(elapsedTime), getX(), getY(), getOriginX(), getOriginY(), getWidth(),
					getHeight(), getScaleX(), getScaleY(), getRotation());
		}
		// we had to move it to down here so the actors attached to the group render after(and therefore,appear on top of) the image corresponding 
		//to the group object itself
		super.draw(batch, parentAlpha);

	}

	public void act(float dt) {
		super.act(dt);

		if (!animationPaused) {
			elapsedTime += dt;
		}
	}
}
