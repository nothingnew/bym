package com.example.findwords;

import org.andengine.engine.FixedStepEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.ui.activity.BaseGameActivity;

import android.view.MotionEvent;

public class MainActivity extends BaseGameActivity implements IOnSceneTouchListener {

	private static final int WIDTH = 1024; // 800
	private static final int HEIGHT = 768; // 480
	private static final int ROWS = 8;
	private static final int COLUMNS = 11;

	private Scene mScene;
	private Camera mCamera;

	private AnimatedSprite mSpriteLetters[][];
	private Text mTextLetters[][];

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

		// Load text
		ResourceManager.getInstance().loadText();

		// Load sounds
		ResourceManager.getInstance().loadSounds(mEngine, this);

		// Load images
		ResourceManager.getInstance().loadGameTextures(mEngine, this);

		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {

		mScene = new Scene();

		pOnCreateSceneCallback.onCreateSceneFinished(mScene);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) {

		mSpriteLetters = new AnimatedSprite[ROWS][COLUMNS];
		mTextLetters = new Text[ROWS][COLUMNS];

		for (int i = 0; i < ROWS; ++i) {
			for (int j = 0; j < COLUMNS; ++j) {

				mSpriteLetters[i][j] = new AnimatedSprite(
						93 / 2 + j * 93, 96 / 2 + (8 - i - 1) * 96,
						ResourceManager.getInstance().mGameTextureRegionBackground2,
						getVertexBufferObjectManager()) {

					@Override
					public boolean onAreaTouched(
							final TouchEvent pSceneTouchEvent,
							final float pTouchAreaLocalX,
							final float pTouchAreaLocalY) {

						int eventAction = pSceneTouchEvent.getMotionEvent().getAction();
						switch (eventAction) {
						case MotionEvent.ACTION_DOWN: {
							ResourceManager.getInstance().mSound.play();
							setCurrentTileIndex((getCurrentTileIndex() + 1)
									% getTileCount());
							break;
						}
						case MotionEvent.ACTION_UP: {
							ResourceManager.getInstance().mSound.play();
							setCurrentTileIndex((getCurrentTileIndex() + 1)
									% getTileCount());
							break;
						}
						default:
							return true;
						}

						// if (pSceneTouchEvent.isActionDown())
						// {
						// ResourceManager.getInstance().mSound.play();
						// setCurrentTileIndex((getCurrentTileIndex() + 1) %
						// getTileCount());
						// }

						return true;
					}

				};

				mTextLetters[i][j] = new Text(93 / 2, 96 / 2,
						ResourceManager.getInstance().mFont,
						ResourceManager.getInstance().mLettersString
								.subSequence(i * COLUMNS + j, i * COLUMNS + j
										+ 1),
						mEngine.getVertexBufferObjectManager());

				mSpriteLetters[i][j].attachChild(mTextLetters[i][j]);
				//pScene.registerTouchArea(mSpriteLetters[i][j]);
				pScene.attachChild(mSpriteLetters[i][j]);
			}
		}

		// Mark some letters (words)
		// word in 4th row (GRUDZIEÑ)
		for (int j = 3; j < COLUMNS; ++j) {
			mSpriteLetters[3][j].setCurrentTileIndex(2);
		}
		// word in 6th row (LUTY)
		for (int j = 3; j < 7; ++j) {
			mSpriteLetters[5][j].setCurrentTileIndex(2);
		}
		// word in 2nd column (MARZEC)
		for (int i = 1; i < 7; ++i) {
			mSpriteLetters[i][1].setCurrentTileIndex(2);
		}
		// word in 10th column (not all letters of this word)
		for (int i = 0; i < 6; ++i) {
			mSpriteLetters[i][9].setCurrentTileIndex(1);
		}

		pScene.setTouchAreaBindingOnActionDownEnabled(true);
		pScene.setOnSceneTouchListener(this);

		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, final TouchEvent pSceneTouchEvent)
	{
	    if (pSceneTouchEvent.isActionMove())
	    {
			int i, j;
			i = (int) pSceneTouchEvent.getY() / 96;
			j = (int) pSceneTouchEvent.getX() / 93;

			if (mSpriteLetters[ROWS - 1 - i][j].getCurrentTileIndex() != 1) // do action like on touch
				mSpriteLetters[ROWS - 1 - i][j].setCurrentTileIndex(1);

	    }
	    return false;
	}
}