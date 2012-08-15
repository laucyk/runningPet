package com.project.runningpet;

import java.util.ArrayList;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.HorizontalAlign;

import com.project.runningpet.BaseActivity.SceneType;
import com.project.runningpet.FoodScene.FoodButton;

import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;

public class RunOptionsScene extends Scene{
	
	String TAG = "RunOptionsScene";	
	BaseActivity activity;
	Camera mCamera;
			
	BitmapTextureAtlas mRunOptionTexture;
	ITextureRegion mRunOptionTextureRegion;	
	
	BitmapTextureAtlas mGoldTexture;
	ITextureRegion mGoldTextureRegion;
	
	Font mFont;
	
	//HUD entities
	String petNameString; 
	Text petNameText;
	String goldString;
	Text goldText;
	Sprite goldIcon;
	Rectangle healthBar;
	
	//Settings
	BitmapTextureAtlas mSettingsTexture;
	ITextureRegion mSettingsTextureRegion;
	Sprite settingsSprite;
	
	//Buttons
	RunButton mRunButton;
	ArrayList <RunButton> runButtonList = new ArrayList <RunButton>();
	float buttonXScale;
	
	//Text entities
	Text conditionText;
	Text adviceText;
	
	public RunOptionsScene() {
		activity = BaseActivity.getSharedInstance();
		BaseActivity.currentSceneType = SceneType.RUN_OPTIONS;		
		mCamera = BaseActivity.mCamera;		
		
		setBackground(new Background(0.4f, 0.5f, 0.5f));
		mFont = FontFactory.create(activity.getFontManager(), activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
		mFont.load();
		
		setHUD();
		setText();
		setAllButtons();		
	}
	
	private void setHUD() {			
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		//Pet name
		petNameString = "Health"; 
		petNameText = new Text(10, 10, BaseActivity.mFont, petNameString, activity.getVertexBufferObjectManager());
		attachChild(petNameText);		
				
		//Gold		
		goldString = "0123456789"; //Allocate memory for every digit 
		goldText = new Text(0, 0, BaseActivity.mFont, goldString, activity.getVertexBufferObjectManager());
		
		int goldInt = 100;
		goldText.setText(String.valueOf(goldInt));
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
		healthBar.setPosition(BaseActivity.CAMERA_WIDTH / 2 - healthBar.getWidth() / 2, petNameText.getY() + petNameText.getHeight() * 2);
		
		float percentHealth = (float) BaseActivity.dbHelper.getPetHealth(BaseActivity.pet_id) / 100;
		float xHealthBarTop = -healthBar.getWidth() * (1-percentHealth);
				
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
		
	private void setAllButtons() {		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		mRunOptionTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 64, TextureOptions.BILINEAR);
		mRunOptionTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mRunOptionTexture, activity, "menu_background.png", 0, 0);	
		this.mRunOptionTexture.load();
				
		float centerX = BaseActivity.CAMERA_WIDTH / 2 - mRunOptionTextureRegion.getWidth() / 2;
		
		int spaceFactor = 5;
		
		setButton(mRunOptionTextureRegion, centerX, adviceText.getY() + adviceText.getHeight() + BaseActivity.CAMERA_HEIGHT * spaceFactor/100, 1);
		long numRunParam = BaseActivity.dbHelper.getNumRunParam();
		for (int i = 0; i < numRunParam - 1; i++)
			setButton(mRunOptionTextureRegion, centerX, runButtonList.get(i).getSprite().getY() + runButtonList.get(i).getSprite().getHeight() + BaseActivity.CAMERA_HEIGHT * spaceFactor/100, i+2);		
	}
	
	private void setButton(ITextureRegion mITextureRegion, float pX, float pY, int runParam_id) {				
		RunButton button = new RunButton(mITextureRegion, pX, pY, runParam_id);
		Sprite sprite = button.getSprite();		
		
		attachChild(sprite);
		registerTouchArea(sprite);
		setTouchAreaBindingOnActionDownEnabled(true);		
		
		runButtonList.add(button);
	}
	
	private void setText() {
		Font mFont;
		mFont = FontFactory.create(activity.getFontManager(), activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
		mFont.load();
		
		String healthStatus = "";
		if (BaseActivity.dbHelper.getPetCondition(BaseActivity.pet_id) == 1)
			healthStatus = "Draco is ";
		else
			healthStatus = "Draco has ";
				
		String conditionName = BaseActivity.dbHelper.getPetConditionName(BaseActivity.pet_id);
		
		conditionText = new Text(BaseActivity.CAMERA_WIDTH * 1/10, healthBar.getY() + healthBar.getHeight() + BaseActivity.CAMERA_HEIGHT * 2/100, mFont, 
				BaseActivity.getNormalizedText(mFont, healthStatus + conditionName, BaseActivity.CAMERA_WIDTH - BaseActivity.CAMERA_WIDTH * 2/10), 
				new TextOptions(HorizontalAlign.LEFT), activity.getVertexBufferObjectManager());
		
		mFont = FontFactory.create(activity.getFontManager(), activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 32);
		mFont.load();
		adviceText = new Text(conditionText.getX(), conditionText.getY() + BaseActivity.CAMERA_HEIGHT * 5/100, mFont, 
				BaseActivity.getNormalizedText(mFont, "It is better to take your pet out for a walk more often!", BaseActivity.CAMERA_WIDTH - BaseActivity.CAMERA_WIDTH * 2/10), 
				new TextOptions(HorizontalAlign.LEFT), activity.getVertexBufferObjectManager());
		
		attachChild(conditionText);
		attachChild(adviceText);
	}

	class RunButton {		
		String TAG = "RunButton";
		
		private Sprite mSprite;
		private int runParam_id;
		private float scale = 1.25f;
		
		public RunButton (ITextureRegion mITextureRegion, float pX, float pY, int r_id) {			
			runParam_id = r_id;
			
			mSprite = new Sprite(0, 0, mITextureRegion, activity.getVertexBufferObjectManager()) {
				@Override
				public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
					switch(pSceneTouchEvent.getAction()) {
					case MotionEvent.ACTION_DOWN:
						//Log.d(TAG, "action down");
						setAlpha(0.5f);
						return true;
					case MotionEvent.ACTION_UP:
						//Log.d(TAG, "action up");
						setAlpha(1.0f);
						if (contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY())) {
							Log.d(TAG, String.format("runParam_id = %d", runParam_id));
							BaseActivity.runParam_id = runParam_id; 
									
							RunScene scene = new RunScene();
							BaseActivity.mRunScene = scene;
							activity.setCurrentScene(scene);
						}
						return true;
					}
					return false;
				};
			};		
			mSprite.setScale(scale);		
			mSprite.setPosition(pX, pY);
			
			String runParamDifficulty = BaseActivity.dbHelper.getRunParamDifficulty(runParam_id);			
			Log.d(TAG, runParamDifficulty);
			Text runParamText = new Text(0, 0, mFont, 
					BaseActivity.getNormalizedText(mFont, runParamDifficulty,  
					BaseActivity.CAMERA_WIDTH - BaseActivity.CAMERA_WIDTH * 0.2f),
					new TextOptions(HorizontalAlign.LEFT), activity.getVertexBufferObjectManager());
			
			float buttonCenterX = mSprite.getWidth() / 2 - runParamText.getWidth() / 2;
			float buttonCenterY = mSprite.getHeight() / 2 - runParamText.getHeight() / 2;
			mSprite.attachChild(runParamText);
			runParamText.setPosition(buttonCenterX, buttonCenterY);
		}
		
		public Sprite getSprite() {
			return mSprite;
		}
		
		public int getRunParamId() {
			return runParam_id;
		}
		
		public float getScale() {
			return scale;
		}
	}	
}