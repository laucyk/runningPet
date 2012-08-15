package com.project.runningpet;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;

import com.project.runningpet.BaseActivity.SceneType;

public class SettingsScene extends Scene{

	String TAG = "Settings";	
	BaseActivity activity;
	Camera mCamera;
	
	public SettingsScene() {
		activity = BaseActivity.getSharedInstance();
		activity.currentSceneType = SceneType.SETTINGS;
		mCamera = activity.mCamera;
		setBackground(new Background(0.5f, 1, 0.8784f));
		
		setHUD();
		setButtons();
	}
	
	private void setHUD() {
		
	}
	
	private void setButtons() {
		
	}
	
}
