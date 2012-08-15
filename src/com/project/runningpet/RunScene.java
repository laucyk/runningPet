package com.project.runningpet;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.debug.Debug;

import com.project.runningpet.BaseActivity.SceneType;

import android.util.Log;

public class RunScene extends Scene{
	
	String TAG = "RunScene";	
	BaseActivity activity;
	Camera mCamera;
	
	//Pet sprite
	AnimatedSprite petSprite;
	BuildableBitmapTextureAtlas mBitmapTextureAtlas;
	TiledTextureRegion mPetTextureRegion;
	
	//Run stats
	int stepsTaken = 0;
	float distance = 0;

	public RunScene() {
		activity = BaseActivity.getSharedInstance();
		BaseActivity.currentSceneType = SceneType.RUN;
		mCamera = BaseActivity.mCamera;
		setBackground(new Background(0, 1, 0.8784f));
		
		setPet();
		registerUpdateHandler(new GameLoopUpdateHandler());
	}
	
	private void setPet() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		mBitmapTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 512, 256, TextureOptions.NEAREST);		
		mPetTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas, activity, "snapdragon_tiled.png", 4, 3);
		
		try {
			this.mBitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 1));
			this.mBitmapTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		
		float scale = 10;
		float centerX = BaseActivity.CAMERA_WIDTH / 2; //- mPetTextureRegion.getWidth() * scale / 2;
		float centerY = BaseActivity.CAMERA_HEIGHT / 2;// - mPetTextureRegion.getHeight() * scale / 2; 
		petSprite =  new AnimatedSprite(centerX, centerY, mPetTextureRegion, activity.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				Log.d(TAG, "pet touched");
				RunCompleteScene scene = new RunCompleteScene();
				BaseActivity.mRunCompleteScene = scene;
				activity.setCurrentScene(scene);
				return false;
			}			
		};
		petSprite.animate(100);
		petSprite.setScale(scale);
		attachChild(petSprite);
		registerTouchArea(petSprite);
		setTouchAreaBindingOnActionDownEnabled(true);
	}
	
	public void updateSteps() {
		
	}
	
	private void finishRun() {
			
	}
	
}
