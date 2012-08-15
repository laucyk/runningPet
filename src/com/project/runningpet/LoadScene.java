package com.project.runningpet;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;

import com.project.runningpet.BaseActivity.SceneType;

public class LoadScene extends Scene{
	
	String TAG = "LoadScene";	
	BaseActivity activity;
	Camera mCamera;

	public LoadScene() {
		activity = BaseActivity.getSharedInstance();
		activity.currentSceneType = SceneType.LOAD;		
		mCamera = activity.mCamera;		
		
		setBackground(new Background(1, 0.8f, 0.5784f));		
	}
	
}
