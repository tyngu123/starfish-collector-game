package com.yago.starfishcollector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public abstract class BaseScreen implements Screen,InputProcessor {
	protected Stage mainStage;
	protected Stage uiStage;
	protected Table uiTable;

	public BaseScreen() {
		mainStage = new Stage();
		uiStage = new Stage();
		
		uiTable = new Table();
		uiTable.setFillParent(true);
		uiStage.addActor(uiTable);
		
		initialize();
	}

	public abstract void initialize();

	public abstract void update(float dt);

	public void render(float dt) {
		uiStage.act(dt);
		mainStage.act(dt);
		update(dt);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		mainStage.draw();
		uiStage.draw();
	}

	public boolean keyDown(int keycode) {
		return false;
	}

	public boolean keyUp(int keycode) {
		return false;
	}

	public boolean keyTyped(char c) {
		return false;
	}

	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	public boolean scrolled(int amount) {
		return false;
	}

	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void show() {
		InputMultiplexer im = (InputMultiplexer)Gdx.input.getInputProcessor();
		im.addProcessor(this);
		im.addProcessor(uiStage);
		im.addProcessor(mainStage);
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		InputMultiplexer im = (InputMultiplexer)Gdx.input.getInputProcessor();
		im.removeProcessor(this);
		im.removeProcessor(uiStage);
		im.removeProcessor(mainStage);
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
}
