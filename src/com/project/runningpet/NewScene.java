package com.project.runningpet;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;

import com.project.runningpet.BaseActivity.SceneType;

public class NewScene extends Scene{
	
	String TAG = "NewScene";	
	BaseActivity activity;
	Camera mCamera;

	public NewScene() {
		activity = BaseActivity.getSharedInstance();
		activity.currentSceneType = SceneType.NEW;		
		mCamera = activity.mCamera;		
		
		setBackground(new Background(1, 0.8f, 0.2784f));
		
		setTextEntry();
	}
	
	private void setTextEntry() {
		
	}
	
}
