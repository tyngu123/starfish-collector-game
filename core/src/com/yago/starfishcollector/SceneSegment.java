package com.yago.starfishcollector;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class SceneSegment {

	private Actor actor;
	private Action action;
	
	public SceneSegment(Actor a1,Action a2) {
		actor = a1;
		action = a2;
	}
	
	// attaches the action to the actor
	public void start() {
		actor.clearActions();
		actor.addAction(action);
	}
	
	// checks whether an actor has finished its actions by checking if there are zero actions attached to the actor
	public boolean isFinished() {
		return (actor.getActions().size == 0);
	}
	
	// clears all actions from the actor in case any infinitely repeating actions were attached to the actor
	public void finish() {
		//simulate 100000 seconds elapsed time to complete in-progress action
		if(actor.hasActions())
			actor.getActions().first().act(100000);
		// remove any remaining actions
		actor.clearActions();
	}
}
