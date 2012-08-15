package com.project.runningpet;

import java.util.ArrayList;

import org.andengine.engine.camera.Camera;
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

import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;

import com.project.runningpet.BaseActivity.SceneType;

public class FoodScene extends Scene{
	
	String TAG = "FoodScene";	
	BaseActivity activity;
	Camera mCamera;
			
	BitmapTextureAtlas mFoodOptionTexture;
	ITextureRegion mFoodOptionTextureRegion;	
	
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
	FoodButton mFoodButton;
	ArrayList <FoodButton> foodButtonList = new ArrayList <FoodButton>();
	float buttonXScale;
	
	//Text entities
	Text conditionText;
	Text adviceText;	
	
	final int MENU_FEED = 0;
	
	public FoodScene() {
		activity = BaseActivity.getSharedInstance();
		BaseActivity.currentSceneType = SceneType.FOOD;		
		mCamera = BaseActivity.mCamera;		
		
		setBackground(new Background(1, 0.6274f, 0.8784f));
		mFont = FontFactory.create(activity.getFontManager(), activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
		mFont.load();
		
		setHUD();
		setInfoText();
		setAllButtons();		
	}
	
	private void setHUD() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");		
		//Pet name
		petNameString = "Energy"; 
		petNameText = new Text(10, 10, BaseActivity.mFont, petNameString, activity.getVertexBufferObjectManager());
		attachChild(petNameText);		
				
		//Gold		
		goldString = "0123456789"; //Allocate memory for every digit 
		goldText = new Text(0, 0, BaseActivity.mFont, goldString, activity.getVertexBufferObjectManager());
		
		BaseActivity.gold = BaseActivity.dbHelper.getPetGold(BaseActivity.pet_id); //Get from database
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
		
		mFoodOptionTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 64, TextureOptions.BILINEAR);
		mFoodOptionTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mFoodOptionTexture, activity, "menu_background.png", 0, 0);
		this.mFoodOptionTexture.load();
				
		//float centerX = BaseActivity.CAMERA_WIDTH / 2 - mAppleOptionTextureRegion.getWidth() / 2;
		float centerX = BaseActivity.CAMERA_WIDTH / 3.5f - mFoodOptionTextureRegion.getWidth() / 2;
		
		int spaceFactor = 5;
		
		setButton(mFoodOptionTextureRegion, centerX, adviceText.getY() + adviceText.getHeight() + BaseActivity.CAMERA_HEIGHT * spaceFactor/100, 1);
		long numFood = BaseActivity.dbHelper.getNumFood();
		for (int i = 0; i < numFood-1; i++)
			setButton(mFoodOptionTextureRegion, centerX, foodButtonList.get(i).getSprite().getY() + foodButtonList.get(i).getSprite().getHeight() + BaseActivity.CAMERA_HEIGHT * spaceFactor/100, i+2);		
	}
	
	private void setButton(ITextureRegion mITextureRegion, float pX, float pY, int food_id) {
		FoodButton button = new FoodButton(mITextureRegion, pX, pY, food_id);
		Sprite sprite = button.getSprite();		
		
		attachChild(sprite);
		registerTouchArea(sprite);
		setTouchAreaBindingOnActionDownEnabled(true);		
		
		foodButtonList.add(button);
	}
	
	private void setInfoText() {	
		conditionText = new Text(BaseActivity.CAMERA_WIDTH * 0.1f, healthBar.getY() + healthBar.getHeight() + BaseActivity.CAMERA_HEIGHT * 0.02f, mFont, 
				BaseActivity.getNormalizedText(mFont, "Draco is very hungry! It is critical to feed Draco to maintain his HP.", BaseActivity.CAMERA_WIDTH - BaseActivity.CAMERA_WIDTH * 2/10), 
				new TextOptions(HorizontalAlign.LEFT), activity.getVertexBufferObjectManager());
		
		mFont = FontFactory.create(activity.getFontManager(), activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 32);
		mFont.load();
		adviceText = new Text(conditionText.getX(), conditionText.getY() + BaseActivity.CAMERA_HEIGHT * 0.1f, mFont, 
				BaseActivity.getNormalizedText(mFont, "Tips: It is important to feed Draco a balanced diet that includes vegetables, grain, fruit, and protein in order to avoid any diseases.", 
				BaseActivity.CAMERA_WIDTH - BaseActivity.CAMERA_WIDTH * 0.2f), 
				new TextOptions(HorizontalAlign.LEFT), activity.getVertexBufferObjectManager());
		
		attachChild(conditionText);
		attachChild(adviceText);
		 		
	}
	
	class FoodButton {		
		String TAG = "FoodButton";
		
		private Sprite mSprite;
		private int food_id;
		private float scale = 1.25f;
		
		public FoodButton (ITextureRegion mITextureRegion, float pX, float pY, int f_id) {
			food_id = f_id;
			
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
							int cost = BaseActivity.dbHelper.getFoodCost(food_id);
							
							if (BaseActivity.gold >= cost) {
								if (!BaseActivity.dbHelper.isMaxPetFoodHours(BaseActivity.pet_id)) {
									BaseActivity.gold -= cost;
									BaseActivity.dbHelper.updatePetGold(BaseActivity.pet_id, BaseActivity.gold);
									BaseActivity.dbHelper.addPetFood(BaseActivity.pet_id, food_id);								
									goldText.setText(String.valueOf(BaseActivity.gold));
									Log.d(TAG, String.format("pet_id = %d, food_id = %d BOUGHT", BaseActivity.pet_id, food_id));
								}
								else {
									Log.d(TAG, "");
								}
							}
							else {
								Log.d(TAG, "");
							}
								
							Log.d(TAG, String.format("pet_id = %d, food_id = %d", BaseActivity.pet_id, food_id));
						}
						return true;

					}
					return false;
				};
			};		
			mSprite.setScale(scale);		
			mSprite.setPosition(pX, pY);
			
			String foodName = BaseActivity.dbHelper.getFoodName(food_id);
			int foodCost = BaseActivity.dbHelper.getFoodCost(food_id);
			
			Text foodText = new Text(0, 0, mFont, 
					BaseActivity.getNormalizedText(mFont, foodName,  
					BaseActivity.CAMERA_WIDTH - BaseActivity.CAMERA_WIDTH * 0.2f),
					new TextOptions(HorizontalAlign.LEFT), activity.getVertexBufferObjectManager());
			
			float buttonCenterX = mSprite.getWidth() / 2 - foodText.getWidth() / 2;
			float buttonCenterY = mSprite.getHeight() / 2 - foodText.getHeight() / 2;
			mSprite.attachChild(foodText);
			foodText.setPosition(buttonCenterX, buttonCenterY);			
			
			Text foodCostText = new Text(0, 0, mFont, 
					BaseActivity.getNormalizedText(mFont, String.valueOf(foodCost),  
					BaseActivity.CAMERA_WIDTH - BaseActivity.CAMERA_WIDTH * 0.2f),
					new TextOptions(HorizontalAlign.LEFT), activity.getVertexBufferObjectManager()); 
						
			attachChild(foodCostText);
			foodCostText.setPosition(mSprite.getX() + mSprite.getWidth() * getScale() + BaseActivity.CAMERA_WIDTH * 0.1f, mSprite.getY());
		}
		
		public Sprite getSprite() {
			return mSprite;
		}
		
		public int getFoodId() {
			return food_id;
		}
		
		public float getScale() {
			return scale;
		}
	}
}
