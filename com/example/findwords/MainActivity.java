package com.example.findwords;

import java.util.ArrayList;
import java.util.List;

import org.andengine.engine.FixedStepEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.ui.activity.BaseGameActivity;

public class MainActivity extends BaseGameActivity implements IOnSceneTouchListener {

	private static final int WIDTH = 1024; // 800
	private static final int HEIGHT = 768; // 480
	private static final int BUTTON_WIDTH = 93;
	private static final int BUTTON_HEIGHT = 96;
	private static final int ROWS = 8;
	private static final int COLUMNS = 11;

	private Scene mScene;
	private Camera mCamera;

	private LetterButtonSprite mSpriteLetters[][];
	private List<LetterButtonSprite> mCurrentSpriteList;
	private List<LetterButtonSprite> mAvailableSpriteList;
	private List<String> mCurrentWordsList;
	private String mCurrentText;
	private boolean mIgnoreMove;

	@Override
	public EngineOptions onCreateEngineOptions() {

		mCamera = new Camera(0, 0, WIDTH, HEIGHT);

		EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE_SENSOR, new FillResolutionPolicy(),
				mCamera);

		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		engineOptions.getAudioOptions().setNeedsSound(true);

		mEngine = new FixedStepEngine(engineOptions, 30);

		return engineOptions;
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback) {

		// Load fonts.
		ResourceManager.getInstance().loadFonts(mEngine);

		// Load words
		ResourceManager.getInstance().loadWords(this);
		mCurrentWordsList = ResourceManager.getInstance().getNewWords();

		// Load sounds
		ResourceManager.getInstance().loadSounds(mEngine, this);

		// Load images
		ResourceManager.getInstance().loadGameTextures(mEngine, this);
		
		// Init variables
		mCurrentSpriteList = new ArrayList<LetterButtonSprite>();
		mAvailableSpriteList = new ArrayList<LetterButtonSprite>();
		mCurrentText = new String();
		mIgnoreMove = false;

		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {

		mEngine.registerUpdateHandler(new FPSLogger());
		
		mScene = new Scene();

		pOnCreateSceneCallback.onCreateSceneFinished(mScene);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) {

		mSpriteLetters = new LetterButtonSprite[COLUMNS][ROWS];

		for (int i = 0; i < COLUMNS; ++i) {
			for (int j = 0; j < ROWS; ++j) {

				mSpriteLetters[i][j] = new LetterButtonSprite(
						BUTTON_WIDTH / 2 + i * BUTTON_WIDTH,
						BUTTON_HEIGHT / 2 + j * BUTTON_HEIGHT,
						ResourceManager.getInstance().mGameTextureRegionBackground2,
						ResourceManager.getInstance().mFont,
						ResourceManager.getInstance().mLettersString
						.subSequence(j * COLUMNS + i, j * COLUMNS + i + 1),
						getVertexBufferObjectManager());

				pScene.attachChild(mSpriteLetters[i][j]);
			}
		}
		
		pScene.setOnSceneTouchListener(this);

		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, final TouchEvent pSceneTouchEvent)
	{
		// zmienic wszystko na switch!
		
		if (pSceneTouchEvent.isActionMove() && mIgnoreMove)
		{
			System.out.println("move locked");
			return true;
		}
		
		if (mIgnoreMove && pSceneTouchEvent.isActionDown())
		{
			System.out.println("move unlocked");
			mIgnoreMove = false;
		}
		
	    if (pSceneTouchEvent.isActionMove() || pSceneTouchEvent.isActionDown())
	    {
			int i = (int) pSceneTouchEvent.getX() / BUTTON_WIDTH;
			int j = (int) pSceneTouchEvent.getY() / BUTTON_HEIGHT;

			if (mSpriteLetters[i][j].getCurrentTileIndex() != 1)
			{
				onLetterButtonClick(i, j);
			}
	    }
	    
	    return true;
	}
	
	public void onLetterButtonClick(int i, int j) {
		
		if(mCurrentSpriteList.isEmpty())
		{
			mSpriteLetters[i][j].setCurrentTileIndex(1);
			mCurrentSpriteList.add(mSpriteLetters[i][j]);
			mCurrentText += mSpriteLetters[i][j].getLetter();
			return;
		}
			
		if (mSpriteLetters[i][j].getX() != mCurrentSpriteList.get(0).getX() &&
				mSpriteLetters[i][j].getY() != mCurrentSpriteList.get(0).getY())
		{
			for(LetterButtonSprite sprite : mCurrentSpriteList)
				sprite.resetBackground();
			
			mCurrentSpriteList.clear();
			mCurrentText = "";
			
			mIgnoreMove = true;
			
			return;
		}
		
		mCurrentSpriteList.add(mSpriteLetters[i][j]);
		mCurrentText += mSpriteLetters[i][j].getLetter();
		
		// sprawdzic czy currentText to poprawna odpowiedz
		if (!checkCorrectness())
			mSpriteLetters[i][j].setCurrentTileIndex(1);
	}
	
	public boolean checkCorrectness() {
		
		//if(mCurrentText.length() > 5)
		if (mCurrentWordsList.contains(mCurrentText)) // zadziala? nie porownuje tekstow tylko obiekty?
		{
			mIgnoreMove = true;
			mCurrentText = "";
			for(LetterButtonSprite sprite : mCurrentSpriteList)
			{
				if(sprite.isLocked())
					sprite.resetBackground();
				else
					sprite.lockIn();
			}
			mCurrentSpriteList.clear();
			return true;
		}
		else
			return false;
	}
}