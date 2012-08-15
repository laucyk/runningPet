package com.project.runningpet;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.HorizontalAlign;

import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;

import com.project.runningpet.BaseActivity.SceneType;

public class RunCompleteScene extends Scene{

	String TAG = "RunCompleteScene";	
	BaseActivity activity;
	Camera mCamera;
	
	BitmapTextureAtlas mGoldTexture;
	ITextureRegion mGoldTextureRegion;
	
	//HUD entities
	String petNameString; 
	Text petNameText;
	String goldString;
	Text goldText;
	Sprite goldIcon;
	Rectangle healthBar;
	Rectangle healthBarTop;
	
	//Settings
	BitmapTextureAtlas mSettingsTexture;
	ITextureRegion mSettingsTextureRegion;
	Sprite settingsSprite;
	
	float minAtPace = 0;
	int goldEarned = 0;
	int healthEarned = 0;
	
	int stepsTaken = 0;
	int caloriesBurned = 0;
	
	Font mFont;
	Text earnText;
	Text healthText;
	Text stepText;
	
	public RunCompleteScene() {
		activity = BaseActivity.getSharedInstance();
		BaseActivity.currentSceneType = SceneType.RUN_COMPLETE;
		mCamera = BaseActivity.mCamera;
		setBackground(new Background(0, 1, 0.8784f));
		
		mFont = FontFactory.create(activity.getFontManager(), activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
		mFont.load();
		
		updateHealth();
		updateGold();
		setEarnText();
		setHUD();
		setHealthText();
	}
	
	private void updateHealth() {
		
	}
	
	private void updateGold() {
		float gold_xCoeff = BaseActivity.dbHelper.getRunParamGoldXCoeff(BaseActivity.runParam_id);		
		goldEarned = Math.round(minAtPace * gold_xCoeff);
	}
	
	private void setEarnText() {
		earnText = new Text(BaseActivity.CAMERA_WIDTH * 0.1f, BaseActivity.CAMERA_HEIGHT * 0.02f, mFont, 
				BaseActivity.getNormalizedText(mFont, "Great! You have earned " + String.format("%d", goldEarned), BaseActivity.CAMERA_WIDTH - BaseActivity.CAMERA_WIDTH * 2/10), 
				new TextOptions(HorizontalAlign.LEFT), activity.getVertexBufferObjectManager());
		attachChild(earnText);
		
	}
	
	private void setHUD() {			
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
					
		healthBar = new Rectangle(0, 0, BaseActivity.CAMERA_WIDTH * 7/10, BaseActivity.CAMERA_HEIGHT * 5/100, activity.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				switch(pSceneTouchEvent.getAction()) {
				case MotionEvent.ACTION_DOWN:
					Log.d(TAG, "healthBar action down");
					activity.setCurrentScene(new RunOptionsScene());
					return true;
				case MotionEvent.ACTION_UP:
					Log.d(TAG, "healthBar action up");					
					return true;				
				}
				return false;				
			}
			
		};
		healthBar.setColor(1, 0, 0);
		healthBar.setAlpha(0.25f);
		healthBar.setPosition(BaseActivity.CAMERA_WIDTH / 2 - healthBar.getWidth() / 2, earnText.getY() + earnText.getHeight() * 2);
		
		float percentHealth = (float) BaseActivity.dbHelper.getPetHealth(BaseActivity.pet_id) / 100;
		float xHealthBarTop = -healthBar.getWidth() * (1-percentHealth);
				
		Log.d(TAG, String.format("health = %d |  percent = %f | xHealthBarTop = %f", BaseActivity.dbHelper.getPetHealth(BaseActivity.pet_id), percentHealth, xHealthBarTop));
		healthBarTop = new Rectangle(0, 0, 
				healthBar.getWidth() * percentHealth, healthBar.getHeight(), activity.getVertexBufferObjectManager());		
		healthBarTop.setColor(1, 0, 0);				
		
		attachChild(healthBar);
		healthBar.attachChild(healthBarTop);
	}
	
	private void setHealthText() {
		healthText = new Text(healthBar.getX(), healthBar.getY() + healthBar.getHeight() * 1.25f, mFont, 
				BaseActivity.getNormalizedText(mFont, String.format("You have improved %s's health!", BaseActivity.petName), BaseActivity.CAMERA_WIDTH - BaseActivity.CAMERA_WIDTH * 2/10), 
				new TextOptions(HorizontalAlign.LEFT), activity.getVertexBufferObjectManager());
		attachChild(healthText);
		
		stepText = new Text(healthText.getX(), healthText.getY() + healthText.getHeight() * 1.25f, mFont, 
				BaseActivity.getNormalizedText(mFont, String.format("You have taken about %d steps and burned %d calories.", stepsTaken, caloriesBurned), BaseActivity.CAMERA_WIDTH - BaseActivity.CAMERA_WIDTH * 2/10), 
				new TextOptions(HorizontalAlign.LEFT), activity.getVertexBufferObjectManager());
		attachChild(stepText);
	}
}
