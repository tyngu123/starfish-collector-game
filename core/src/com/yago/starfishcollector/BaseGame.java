package com.yago.starfishcollector;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public abstract class BaseGame extends Game {

	private static BaseGame game;
	
	public static LabelStyle labelStyle;
	
	// nine-patch 
	public static TextButtonStyle textButtonStyle;
	
	public BaseGame() {
		game = this;
	}
	
	public static void setActiveScreen(BaseScreen s) {
		game.setScreen(s);
	}
	
	public void create() {
		// prepare for multiple classes/stages to receive discrete input
		InputMultiplexer im = new InputMultiplexer();
		Gdx.input.setInputProcessor(im);
		
		BitmapFont customFont = new BitmapFont(Gdx.files.internal("cooper.fnt"));
		
		
		labelStyle = new LabelStyle();
		labelStyle.font = customFont;
		
		textButtonStyle = new TextButtonStyle();
		Texture buttonTex = new Texture(Gdx.files.internal("button.png"));
		NinePatch buttonPatch = new NinePatch(buttonTex,24,24,24,24);
		textButtonStyle.up = new NinePatchDrawable(buttonPatch);
		textButtonStyle.font = customFont;
		textButtonStyle.fontColor = Color.GRAY;
		
		
	}
	
}
