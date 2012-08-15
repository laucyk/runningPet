package com.project.runningpet;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import com.project.runningpet.BaseActivity.SceneType;

import android.util.Log;
import android.view.MotionEvent;

public class GameScene extends Scene implements IOnSceneTouchListener {
	
	String TAG = "GameScene";	
	BaseActivity activity;
	Camera mCamera;
			
	//Background
	BitmapTextureAtlas mBackgroundTexture;
	ITextureRegion mBackgroundTextureRegion;
	Sprite backgroundSprite;
	
	//HUD START
	//Name
	String petNameString; 
	Text petNameText;
	
	//Health
	Rectangle healthBar;
	
	//Gold
	BitmapTextureAtlas mGoldTexture;
	ITextureRegion mGoldTextureRegion;
	String goldString;
	Text goldText;
	Sprite goldIcon;
	
	//Settings
	BitmapTextureAtlas mSettingsTexture;
	ITextureRegion mSettingsTextureRegion;
	Sprite settingsSprite;
	//HUD END
	
	//Menu
	BitmapTextureAtlas mMenuTexture;
	ITiledTextureRegion mMenuFeedTextureRegion;
			
	//Food sprite
	BitmapTextureAtlas mBowlTexture;
	ITextureRegion mBowlTextureRegion;
	Sprite bowlSprite;
		
	final int MENU_FEED = 0;
	
	public GameScene() {
		activity = BaseActivity.getSharedInstance();
		BaseActivity.currentSceneType = SceneType.GAME;		
		mCamera = BaseActivity.mCamera;		
		
		setBackground(new Background(0.09804f, 0.6274f, 0.8784f));		
		setOnSceneTouchListener(this);
		
		mSetBackground();
		setHUD();
		setFood();
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// TODO Auto-generated method stub
		return false;
	}	
	
	private void mSetBackground() {
		mBackgroundTexture = new BitmapTextureAtlas(activity.getTextureManager(), 2048, 2048, TextureOptions.BILINEAR);
		mBackgroundTexture.load();
		mBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBackgroundTexture, activity, "background-01.png", 0, 0);
		backgroundSprite = new Sprite(0, 0, mBackgroundTextureRegion, activity.getVertexBufferObjectManager());
		attachChild(backgroundSprite);
	}
	
	private void setHUD() {			
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
				
		//Health bar
		//TiledSprite used to show different sprite on push down and lift up
		/*mMenuTexture = new BitmapTextureAtlas(activity.getTextureManager(), 64, 32, TextureOptions.BILINEAR);
		mMenuFeedTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mMenuTexture, activity, "face_box_tiled.png", 0, 0, 2, 1);
		this.mMenuTexture.load();
		
		TiledSprite feedButton = new TiledSprite(100, BaseActivity.CAMERA_HEIGHT - 30, mMenuFeedTextureRegion, activity.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				switch(pSceneTouchEvent.getAction()) {
				case MotionEvent.ACTION_DOWN:
					Log.d(TAG, "action down");
					setCurrentTileIndex(1);
					return true;
				case MotionEvent.ACTION_UP:
					Log.d(TAG, "action up");
					setCurrentTileIndex(0);
					return true;
				}
				return false;
			};
		};
		feedButton.setScale(3.0f);
				
		hud.attachChild(feedButton);
		registerTouchArea(feedButton);
		setTouchAreaBindingOnActionDownEnabled(true);*/
		
		//Pet name
		petNameString = BaseActivity.petName; //To be pulled from database
		petNameText = new Text(10, 10, BaseActivity.mFont, petNameString, activity.getVertexBufferObjectManager());
		attachChild(petNameText);		
				
		//Gold		
		goldString = "0123456789"; //Allocate memory for every digit 
		goldText = new Text(0, 0, BaseActivity.mFont, goldString, activity.getVertexBufferObjectManager());
				
		goldText.setText(String.valueOf(BaseActivity.gold));
		goldText.setPosition(BaseActivity.CAMERA_WIDTH - 125, 10);	
		attachChild(goldText);
		
		//Gold icon
		mGoldTexture = new BitmapTextureAtlas(activity.getTextureManager(), 32, 32, TextureOptions.BILINEAR);
		mGoldTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mGoldTexture, activity, "box.png", 0, 0);
		this.mGoldTexture.load();
		goldIcon = new Sprite(goldText.getX() - (mGoldTextureRegion.getWidth() + 5), goldText.getY(), mGoldTextureRegion, activity.getVertexBufferObjectManager());
		attachChild(goldIcon);
		
		healthBar = new Rectangle(0, 0, BaseActivity.CAMERA_WIDTH * 7/10, BaseActivity.CAMERA_HEIGHT * 5/100, activity.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				switch(pSceneTouchEvent.getAction()) {
				case MotionEvent.ACTION_DOWN:
					Log.d(TAG, "healthBar action down");					
					return true;
				case MotionEvent.ACTION_UP:
					Log.d(TAG, "healthBar action up");
					RunOptionsScene scene = new RunOptionsScene();
					BaseActivity.mRunOptionsScene = scene;
					activity.setCurrentScene(scene);
					return true;				
				}
				return false;				
			}
			
		};
		healthBar.setColor(1, 0, 0);
		healthBar.setAlpha(0.25f);
		healthBar.setPosition(BaseActivity.CAMERA_WIDTH / 2 - healthBar.getWidth() / 2, BaseActivity.CAMERA_HEIGHT - healthBar.getHeight() * 150/100);
		
		float percentHealth = (float) BaseActivity.dbHelper.getPetHealth(BaseActivity.pet_id) / 100;		
		float xHealthBarTop = -healthBar.getWidth() * (1 - percentHealth);
				
		Log.d(TAG, String.format("health = %d |  percent = %f | xHealthBarTop = %f", BaseActivity.dbHelper.getPetHealth(BaseActivity.pet_id), percentHealth, xHealthBarTop));
		Rectangle healthBarTop = new Rectangle(0, 0, 
				healthBar.getWidth() * percentHealth, healthBar.getHeight(), activity.getVertexBufferObjectManager());
		healthBarTop.setColor(1, 0, 0);		
		
		attachChild(healthBar);
		registerTouchArea(healthBar);
		setTouchAreaBindingOnActionDownEnabled(true);
				
		healthBar.attachChild(healthBarTop);
		registerTouchArea(healthBarTop);
		setTouchAreaBindingOnActionDownEnabled(true);
		
		//Settings
		mSettingsTexture = new BitmapTextureAtlas(activity.getTextureManager(), 32, 32, TextureOptions.BILINEAR);
		mSettingsTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mGoldTexture, activity, "box.png", 0, 0);
		this.mSettingsTexture.load();
		settingsSprite = new Sprite(BaseActivity.CAMERA_WIDTH - mSettingsTextureRegion.getWidth() * 2, goldText.getY(), mSettingsTextureRegion, activity.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				switch(pSceneTouchEvent.getAction()) {
				case MotionEvent.ACTION_DOWN:
					Log.d(TAG, "healthBar action down");					
					return true;
				case MotionEvent.ACTION_UP:
					Log.d(TAG, "healthBar action up");
					SettingsScene scene = new SettingsScene();
					BaseActivity.mSettingsScene = scene;
					activity.setCurrentScene(scene);
					return true;				
				}
				return false;				
			}
		};
		attachChild(settingsSprite);	
		registerTouchArea(settingsSprite);
		setTouchAreaBindingOnActionDownEnabled(true);
		
	}
	
	private void setFood() {
		//Gold icon
		mBowlTexture = new BitmapTextureAtlas(activity.getTextureManager(), 32, 32, TextureOptions.BILINEAR);
		mBowlTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mGoldTexture, activity, "box.png", 0, 0);
		this.mBowlTexture.load();
		bowlSprite = new Sprite(0, 0, 
				mBowlTextureRegion, activity.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				switch(pSceneTouchEvent.getAction()) {
				case MotionEvent.ACTION_DOWN:
					Log.d(TAG, "bowl action down");					
					return true;
				case MotionEvent.ACTION_UP:
					Log.d(TAG, "bowl action up");
					FoodScene scene = new FoodScene();
					BaseActivity.mFoodScene = scene;
					activity.setCurrentScene(scene);
					return true;				
				}
				return false;				
			}			
		};
		bowlSprite.setScale(4.0f);
		bowlSprite.setPosition(healthBar.getX() + healthBar.getWidth() / 2 - bowlSprite.getWidth() / 2, healthBar.getY() - healthBar.getHeight());
		attachChild(bowlSprite);
		registerTouchArea(bowlSprite);
		setTouchAreaBindingOnActionDownEnabled(true);
	}
	
	public void setGold(int gold) {
		goldText.setText(String.valueOf(gold));
	}
}
