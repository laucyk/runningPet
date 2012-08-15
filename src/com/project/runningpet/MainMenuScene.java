package com.project.runningpet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.andengine.engine.camera.Camera;
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

import com.project.runningpet.BaseActivity.SceneType;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

//placeHolder scene class for the main menu, currently only includes a start menu item 
public class MainMenuScene extends Scene{
	
	String TAG = "MainMenuScene";	
	BaseActivity activity;
	Camera mCamera;	
	
	private BitmapTextureAtlas mMenuTexture;
	protected ITextureRegion mMenuNewTextureRegion;
	protected ITextureRegion mMenuLoadTextureRegion;
	
	//Menu buttons
	Sprite newButton;
	Sprite loadButton;
	
	//Text
	Font mFont;
	
	View mLayout;

	public MainMenuScene() {		
		activity = BaseActivity.getSharedInstance();
		BaseActivity.currentSceneType = SceneType.MENU;
		mCamera = BaseActivity.mCamera;
		
		setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
		
		setButtons();
	}
	
	private void setButtons() {
		//Load assets
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		this.mMenuTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 64, TextureOptions.BILINEAR);
		this.mMenuNewTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mMenuTexture, activity, "menu_background.png", 0, 0);
		this.mMenuLoadTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mMenuTexture, activity, "menu_background.png", 0, 0);
		this.mMenuTexture.load();
		
		//Load text
		mFont = FontFactory.create(activity.getFontManager(), activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
		mFont.load();		
		
		//Setup new button
		newButton = new Sprite(0, 0, mMenuNewTextureRegion, activity.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				switch(pSceneTouchEvent.getAction()) {
				case MotionEvent.ACTION_DOWN:
					Log.d(TAG, "newButton action down");
					setAlpha(0.5f);
					return true;
				case MotionEvent.ACTION_UP:
					Log.d(TAG, "newButton action up");
					setAlpha(1.0f);
					if (contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY())) { //Need to use pSceneTouchEvent (x,y), those are the global coord I believe						
						
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								AlertDialog.Builder builder;
								AlertDialog alertDialog;

								Context mContext = activity.mContext;
								LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
								mLayout = inflater.inflate(R.layout.dialog_new_pet,
								                               (ViewGroup) activity.findViewById(R.id.dialog_new_pet_root));											

								builder = new AlertDialog.Builder(mContext);
								builder.setView(mLayout);
								
								builder//.setMessage("Are you sure you want to exit?")
							       .setCancelable(true)
							       .setPositiveButton("Done", new DialogInterface.OnClickListener() {
							           public void onClick(DialogInterface dialog, int id) {					                
							        	   EditText etNewPet = (EditText) mLayout.findViewById(R.id.dialog_new_pet_editText);
							        	   String petName = etNewPet.getText().toString();							        	   
							        	   
							        	   if (BaseActivity.dbHelper.addPetInfo(petName) > -1) {	
							        		   int pet_idNew = (int) BaseActivity.dbHelper.getNumPets();
							        		   setBaseActivityParam(pet_idNew);							        		   
							        		   
							        		   Log.d(TAG, String.format("%s, %d gold added, %d health", petName, BaseActivity.gold, BaseActivity.health));
							        		   
							        		   GameScene scene = new GameScene();							        		   
							        		   BaseActivity.mGameScene = scene;
							        		   activity.setCurrentScene(scene);
							        	   }
							           }
							       });								
								alertDialog = builder.create();
								builder.show();
							}							
						});
									
					}
					return true;
				case MotionEvent.ACTION_MOVE:
					if (contains(pTouchAreaLocalX, pTouchAreaLocalY)) {
						
					}
					return true;
				}
				return false;				
			}
		};
		newButton.setScale(2.0f);
		newButton.setPosition(mCamera.getWidth() / 2 - newButton.getWidth()/ 2, 
				mCamera.getHeight() / 2 - newButton.getHeight() / 2 - 25);
		
		//Attach and register touch
		attachChild(newButton);		
		registerTouchArea(newButton);
		setTouchAreaBindingOnActionDownEnabled(true);
		
		//Setup new button text
		Text newText = new Text(0, 0, mFont, 
				BaseActivity.getNormalizedText(mFont, "New Pet", newButton.getWidth()), 
				new TextOptions(HorizontalAlign.LEFT), activity.getVertexBufferObjectManager());		
		newButton.attachChild(newText);
		float buttonCenterX = newButton.getWidth() / 2 - newText.getWidth() / 2;
		float buttonCenterY = newButton.getHeight() / 2 - newText.getHeight() / 2;
		newText.setPosition(buttonCenterX, buttonCenterY);
		
		//Set up load button
		loadButton = new Sprite(0, 0, mMenuLoadTextureRegion, activity.getVertexBufferObjectManager()) {;
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				switch(pSceneTouchEvent.getAction()) {
				case MotionEvent.ACTION_DOWN:
					Log.d(TAG, "loadButton action down");
					setAlpha(0.5f);
					return true;
				case MotionEvent.ACTION_UP:
					Log.d(TAG, "loadButton action up");
					setAlpha(1.0f);
					if (contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY())) { 
						/*LoadScene scene = new LoadScene();
						BaseActivity.mLoadScene = scene;
						activity.setCurrentScene(scene);*/
						
						BaseActivity.pet_id = 1;						        	   
						   
						Log.d(TAG, String.format("petName = %s, gold = %d", BaseActivity.petName, BaseActivity.gold));
					   	try {
							BaseActivity.dbHelper.updateDb(BaseActivity.pet_id);
							setBaseActivityParam(BaseActivity.pet_id);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}							        	   
					   	GameScene scene = new GameScene();							        		   
					   	BaseActivity.mGameScene = scene;
					   	activity.setCurrentScene(scene);
					}
					return true;				
				}
				return false;
			}
		};
		loadButton.setScale(2.0f);
		loadButton.setPosition(newButton.getX(), newButton.getY() + newButton.getHeight() * 2 * 1.5f);
		
		//Attach and register touch
		attachChild(loadButton);
		registerTouchArea(loadButton);
		setTouchAreaBindingOnActionDownEnabled(true);
		
		//Setup load button text
		Text loadText = new Text(0, 0, mFont, 
				BaseActivity.getNormalizedText(mFont, "Load Pet", loadButton.getWidth()), 
				new TextOptions(HorizontalAlign.LEFT), activity.getVertexBufferObjectManager());
		loadButton.attachChild(loadText);
		loadText.setPosition(buttonCenterX, buttonCenterY);				
	}
		
	private void setBaseActivityParam(int pet_id) {
		BaseActivity.pet_id = pet_id;
		BaseActivity.petName = BaseActivity.dbHelper.getPetName(pet_id);
		BaseActivity.gold = BaseActivity.dbHelper.getPetGold(pet_id);
		BaseActivity.health = BaseActivity.dbHelper.getPetHealth(pet_id);
		Log.d(TAG, String.format("%d, %s, %d, %d", pet_id, BaseActivity.petName, BaseActivity.gold, BaseActivity.health));
	}

}