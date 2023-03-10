package com.yago.starfishcollector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class LevelScreen extends BaseScreen {

	private Turtle turtle;
	private boolean win;

	private Label starfishLabel;
	
	private DialogBox dialogBox;

	@Override
	public void initialize() {
		BaseActor ocean = new BaseActor(0, 0, mainStage);
		ocean.loadTexture("water-border.jpg");
		ocean.setSize(1200, 900);

		BaseActor.setWorldBounds(ocean);

		new Starfish(400, 400, mainStage);
		new Starfish(500, 100, mainStage);
		new Starfish(100, 450, mainStage);
		new Starfish(200, 250, mainStage);

		new Rock(200, 150, mainStage);
		new Rock(100, 300, mainStage);
		new Rock(300, 350, mainStage);
		new Rock(450, 200, mainStage);

		turtle = new Turtle(20, 20, mainStage);

		starfishLabel = new Label("Starfish Left:", BaseGame.labelStyle);
		starfishLabel.setColor(Color.CYAN);
		//starfishLabel.setPosition(20, 520);
		uiStage.addActor(starfishLabel);

		ButtonStyle buttonStyle = new ButtonStyle();

		Texture buttonTex = new Texture(Gdx.files.internal("undo.png"));
		TextureRegion buttonRegion = new TextureRegion(buttonTex);
		buttonStyle.up = new TextureRegionDrawable(buttonRegion);

		Button restartButton = new Button(buttonStyle);
		restartButton.setColor(Color.CYAN);
		//restartButton.setPosition(720, 520);
		uiStage.addActor(restartButton);

		// when clicking the restart button, the game resets
		restartButton.addListener((Event e) -> {
			if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(Type.touchDown))
				return false;
			StarfishGame.setActiveScreen(new LevelScreen());
			return false;
		});
		
		uiTable.pad(10);
		uiTable.add(starfishLabel).top();
		uiTable.add().expandX().expandY();
		uiTable.add(restartButton).top();
		
		Sign sign1 = new Sign(20,400, mainStage);
		sign1.setText("West Starfish Bay");
		
		Sign sign2 = new Sign(600,300, mainStage);
		sign2.setText("East Starfish Bay");
		
		dialogBox = new DialogBox(0,0, uiStage);
		dialogBox.setBackgroundColor(Color.TAN);
		dialogBox.setFontColor(Color.BROWN);
		dialogBox.setFontScale(0.80f);
		dialogBox.alignCenter();
		dialogBox.setVisible(false);
		
		uiTable.row();
		uiTable.add(dialogBox).colspan(3);

		win = false;

	}

	@Override
	public void update(float dt) {

		for (BaseActor rockActor : BaseActor.getList(mainStage, "com.yago.starfishcollector.Rock"))
			turtle.preventOverlap(rockActor);

		for (BaseActor starfishActor : BaseActor.getList(mainStage, "com.yago.starfishcollector.Starfish")) {
			Starfish starfish = (Starfish) starfishActor;
			if (turtle.overlaps(starfish) && !starfish.collected) {
				starfish.collected = true;
				starfish.clearActions();
				starfish.addAction(Actions.fadeOut(1));
				starfish.addAction(Actions.after(Actions.removeActor()));
				Whirlpool whirl = new Whirlpool(0, 0, mainStage);
				whirl.centerAtActor(starfish);
				whirl.setOpacity(0.25f);
			}
		}
		if (BaseActor.count(mainStage, "com.yago.starfishcollector.Starfish") == 0 && !win) {
			win = true;
			BaseActor youWinMessage = new BaseActor(0, 0, uiStage);
			youWinMessage.loadTexture("you-win.png");
			youWinMessage.centerAtPosition(400, 300);
			youWinMessage.setOpacity(0);
			youWinMessage.addAction(Actions.delay(1));
			youWinMessage.addAction(Actions.after(Actions.fadeIn(1)));
		}

		starfishLabel.setText("Starfish Left: " + BaseActor.count(mainStage, "com.yago.starfishcollector.Starfish"));
		
		for (BaseActor signActor : BaseActor.getList(mainStage, "com.yago.starfishcollector.Sign")) {
			Sign sign = (Sign)signActor;
			
			turtle.preventOverlap(sign);
			boolean nearby = turtle.isWithingDistance(4, sign);
			
			if (nearby && !sign.isViewing()) {
				dialogBox.setText(sign.getText());
				dialogBox.setVisible(true);
				sign.setViewing(true);
			}
			if (sign.isViewing() && !nearby) {
				dialogBox.setText(" ");
				dialogBox.setVisible(false);
				sign.setViewing(false);
			}
		}
	}

}
