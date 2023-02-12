package com.yago.starfishcollector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class Turtle extends BaseActor{

	public Turtle(float x, float y, Stage s) {
		super(x, y, s);
		String[] filenames = 
			{"turtle-1.png","turtle-2.png","turtle-3.png",
			"turtle-4.png","turtle-5.png","turtle-6.png"};
		
		loadAnimationFromFiles(filenames, 0.1f, true);
		
		setAcceleration(400);
		setMaxSpeed(100);
		setDeceleration(400);
		
		setBoundaryPolygon(8);
		
	}
	
	public void act(float dt) {
		super.act(dt);
		
		if( Gdx.input.isKeyPressed(Keys.A))
			accelerateAtAngle(180);
		if( Gdx.input.isKeyPressed(Keys.D))
			accelerateAtAngle(0);
		if (Gdx.input.isKeyPressed(Keys.W))
			accelerateAtAngle(90);
		if(Gdx.input.isKeyPressed(Keys.S))
			accelerateAtAngle(270);
		
		// Apply acceleration physics
		applyPhysics(dt);
		
		// It pauses the animation if he's not moving
		setAnimationPaused( !isMoving() );
		
		// Rotates itself to the position it faces
		if ( getSpeed() > 0)
			setRotation(getMotionAngle());
		
		// Keep the turtle within the world boundaries
		boundToWorld();
		
		alignCamera();
		
	}

}
