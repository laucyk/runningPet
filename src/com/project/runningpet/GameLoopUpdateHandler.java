package com.project.runningpet;

import org.andengine.engine.handler.IUpdateHandler;

public class GameLoopUpdateHandler implements IUpdateHandler {

	@Override
	public void onUpdate(float pSecondsElapsed) {
		((RunScene) BaseActivity.mCurrentScene).updateSteps();
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

}
