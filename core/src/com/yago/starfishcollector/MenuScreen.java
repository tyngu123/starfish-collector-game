package com.yago.starfishcollector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class MenuScreen extends BaseScreen {
	public void initialize() {

		BaseActor ocean = new BaseActor(0, 0, mainStage);
		ocean.loadTexture("water.jpg");
		ocean.setSize(800, 600);

		BaseActor title = new BaseActor(0, 0, mainStage);
		title.loadTexture("starfish-collector.png");
		
		// Start Button
		TextButton startButton = new TextButton("Start", BaseGame.textButtonStyle);
	

		startButton.addListener((Event e) -> {
			if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(Type.touchDown))
				return false;
			StarfishGame.setActiveScreen(new StoryScreen());
			return false;
		});

		// Quit Button
		TextButton quitButton = new TextButton("Quit", BaseGame.textButtonStyle);
		

		quitButton.addListener((Event e) -> {
			if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(Type.touchDown))
				return false;
			Gdx.app.exit();
			return false;
		});
		
		uiTable.add(title).colspan(2);
		uiTable.row();
		uiTable.add(startButton);
		uiTable.add(quitButton);

	}
	
	// Player can use keyboard instead of using the mouse
	public boolean keyDown(int keyCode) {
		if (Gdx.input.isKeyPressed(Keys.ENTER))
			StarfishGame.setActiveScreen(new LevelScreen());
		if (Gdx.input.isKeyPressed(Keys.ESCAPE))
			Gdx.app.exit();
		return false;
	}

	public void update(float dt) {
		if (Gdx.input.isKeyPressed(Keys.S))
			StarfishGame.setActiveScreen(new LevelScreen());
	}
}
