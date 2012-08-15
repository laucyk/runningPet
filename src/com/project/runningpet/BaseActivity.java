package com.project.runningpet;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga
 *
 * @author Nicolas Gramlich
 * @since 15:13:46 - 15.06.2010
 */
public class BaseActivity extends SimpleBaseGameActivity {
	String TAG = "BaseActivity";
	
	static int CAMERA_WIDTH;
	static int CAMERA_HEIGHT;

	public static Font mFont;
	public static Camera mCamera;	

	// A reference to the current scene
	public static Scene mCurrentScene;
	public static SceneType currentSceneType;
	public static BaseActivity instance;
	public Context mContext = this;
	public static DatabaseHelper dbHelper;
	
	//To hold scene instance, speed things up on back pressed
	public static NewScene mNewScene;
	public static LoadScene mLoadScene;
	public static GameScene mGameScene;
	public static RunOptionsScene mRunOptionsScene;
	public static RunScene mRunScene;
	public static RunCompleteScene mRunCompleteScene;
	public static MainMenuScene mMainMenuScene;
	public static FoodScene mFoodScene;
	public static SettingsScene mSettingsScene;
	
	//To hold common elements about game
	public static int pet_id = -1;
	public static String petName = "blank";
	public static int gold;
	public static int health;
	public static int runParam_id = -1;	
	
	//Scene enum
	public enum SceneType {
		SPLASH, MENU, NEW, LOAD, GAME, RUN_OPTIONS, RUN, RUN_COMPLETE, FOOD, SETTINGS
	}

	public static BaseActivity getSharedInstance() {
		return instance;
	}

	@Override
	public EngineOptions onCreateEngineOptions() {
		Display display = getWindowManager().getDefaultDisplay(); 
		CAMERA_WIDTH = display.getWidth();  // deprecated
		CAMERA_HEIGHT = display.getHeight();  // deprecated
		
		instance = this;
		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);		

		return new EngineOptions(true, ScreenOrientation.PORTRAIT_SENSOR,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
	}

	@Override
	protected void onCreateResources() {
		mFont = FontFactory.create(this.getFontManager(),
				this.getTextureManager(), 256, 256,
				Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
		mFont.load();
	}

	@Override
	protected Scene onCreateScene() {
		dbHelper = new DatabaseHelper(mContext);
		
		mEngine.registerUpdateHandler(new FPSLogger());
		mCurrentScene = new MainMenuScene();
		mMainMenuScene = (MainMenuScene) mCurrentScene;
		currentSceneType = SceneType.MENU;
		return mCurrentScene;
	}

	// to change the current main scene
	public void setCurrentScene(Scene scene) {
		mCurrentScene = null;
		mCurrentScene = scene;
		getEngine().setScene(mCurrentScene);

	}

	@Override
	public void onBackPressed() {
		switch (currentSceneType) {
             case SPLASH:
            	 break;
             case MENU:
                 //finish();
                 break;
             case NEW:
            	 setCurrentScene(mMainMenuScene);
            	 currentSceneType = SceneType.MENU;
            	 break;
             case LOAD:
            	 setCurrentScene(mMainMenuScene);
            	 currentSceneType = SceneType.MENU;
            	 break;
             case GAME:
            	 //Should have a confirm popup, or pause
                 setCurrentScene(mMainMenuScene);
                 currentSceneType = SceneType.MENU;
                 break; 
             case RUN_OPTIONS:
            	 mGameScene.setGold(BaseActivity.gold);
                 setCurrentScene(mGameScene);
                 currentSceneType = SceneType.GAME;
                 break;
             case RUN:
            	 setCurrentScene(mRunOptionsScene);
            	 currentSceneType = SceneType.RUN_OPTIONS;
            	 break;
             case RUN_COMPLETE:
            	 mGameScene.setGold(BaseActivity.gold);
            	 setCurrentScene(mGameScene);
            	 currentSceneType = SceneType.GAME;
            	 break;
             case FOOD:
            	 mGameScene.setGold(BaseActivity.gold);
            	 setCurrentScene(mGameScene);
            	 currentSceneType = SceneType.GAME;
            	 break;
             case SETTINGS:
            	 break;
        }
	}
	
	public static String getNormalizedText(Font font, String ptext, float textWidth) {
		//Returns a string that will word wrap in AndEngine text
        // no need to normalize, its just one word, so return
        if (!ptext.contains(" "))
                return ptext;
        String[] words = ptext.split(" ");
        StringBuilder normalizedText = new StringBuilder();
        StringBuilder line = new StringBuilder();
       
        for (int i = 0; i < words.length; i++) {
        		Text mText = new Text(BaseActivity.CAMERA_WIDTH * 1/10, BaseActivity.CAMERA_HEIGHT * 1/10, font, 
        				line + words[i], new TextOptions(HorizontalAlign.LEFT), instance.getVertexBufferObjectManager());
        	
                if (mText.getWidth() > (textWidth)) {
                        normalizedText.append(line).append('\n');
                        line = new StringBuilder();
                }
               
                if(line.length() == 0)                         
                        line.append(words[i]);
                else
                        line.append(' ').append(words[i]);
       
                if (i == words.length - 1)
                        normalizedText.append(line);
        }
        return normalizedText.toString();
	}
}
