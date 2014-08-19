package com.example.imagequiz;

import org.andengine.engine.FixedStepEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
//import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.AnimatedSprite.IAnimationListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
//import org.andengine.input.touch.detector.SurfaceGestureDetector;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.align.HorizontalAlign;

//import android.content.Intent;

//import android.os.Looper;
//import android.content.Context;

//public class MainActivity extends BaseGameActivity implements IOnSceneTouchListener {

public class MainActivity extends BaseGameActivity {

	private static final int WIDTH = 1024; // 800
	private static final int HEIGHT = 768; // 480
	private static final int QUESTION_WIDTH = WIDTH;
	private static final int QUESTION_HEIGHT = 128;
	private static final int BUTTON_WIDTH = WIDTH / 3;
	private static final int BUTTON_HEIGHT = (HEIGHT - QUESTION_HEIGHT) / 2;

	private Scene mScene;
	private Camera mCamera;
	private AnswerSprite buttonSprite[];
	private Sprite questionSprite;
	private Text questionText;
	private QuestionData mQuestionData;
	private boolean locker;
	
	public int correctAnswersCount;
	public int wrongAnswersCount;
	
	//private SurfaceGestureDetector gest;

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

		// Load images
		ResourceManager.getInstance().loadGameTextures(mEngine, this);

		// Load texts
		ResourceManager.getInstance().loadText();

		// Load sounds
		ResourceManager.getInstance().loadSounds(mEngine, this);

		// Load all questions
		ResourceManager.getInstance().loadQuestions(this);

		// Load first question
		mQuestionData = ResourceManager.getInstance().getNextQuestion(null);
		mQuestionData.randomShuffle();
		ResourceManager.getInstance().loadQuestionTextures(
				mQuestionData.getQuestionImagesArray(), mEngine, this);

		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {

		mEngine.registerUpdateHandler(new FPSLogger());
		
		mScene = new Scene();
		
		//mScene.setOnSceneTouchListener(this);

		pOnCreateSceneCallback.onCreateSceneFinished(mScene);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) {

		// MENU
		Sprite splashScreenSprite = new Sprite(WIDTH / 2, HEIGHT / 2,
				ResourceManager.getInstance().mGameTextureRegionComplete,
				getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

				int eventAction = pSceneTouchEvent.getAction();

				switch (eventAction) {
				case TouchEvent.ACTION_UP: {
					mScene.unregisterTouchArea(this);
					ResourceManager.getInstance().mSound[1].play();
					initGame(mScene);
					mScene.detachChild(this);
					break;
				}
				default:
					break;
				}

				return true;
			}
		};

		String text = "Image Quiz :)\n\nSTART";

		Text splashScreenText = new Text(splashScreenSprite.getWidth() / 2,
				splashScreenSprite.getHeight() / 2,
				ResourceManager.getInstance().mFont, text,
				100, getVertexBufferObjectManager());
		splashScreenText.setHorizontalAlign(HorizontalAlign.CENTER);
		
		splashScreenSprite.attachChild(splashScreenText);
		pScene.attachChild(splashScreenSprite);
		pScene.registerTouchArea(splashScreenSprite);

		//////////////////////////////////////
		
		IAnimationListener pCorrectAnimationListener = new IAnimationListener() {

			@Override
			public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
					int pInitialLoopCount) {
				++correctAnswersCount;
			}

			@Override
			public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,
					int pOldFrameIndex, int pNewFrameIndex) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
					int pRemainingLoopCount, int pInitialLoopCount) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
				onAnswersCheck();

			}
		};
		
		IAnimationListener pWrongAnimationListener = new IAnimationListener() {

			@Override
			public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
					int pInitialLoopCount) {
				++wrongAnswersCount;
			}

			@Override
			public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,
					int pOldFrameIndex, int pNewFrameIndex) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
					int pRemainingLoopCount, int pInitialLoopCount) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
				//onAnswersCheck();

			}
		};
		
		buttonSprite = new AnswerSprite[6];

		for (int i = 0; i < 6; ++i) {
			buttonSprite[i] = new AnswerSprite(BUTTON_WIDTH / 2 + (i % 3)
					* BUTTON_WIDTH,
					BUTTON_HEIGHT / 2 + (i / 3) * BUTTON_HEIGHT,
					ResourceManager.getInstance().mGameTextureRegionImage[i],
					pCorrectAnimationListener, pWrongAnimationListener,
					getVertexBufferObjectManager());

			buttonSprite[i].setCorrect(mQuestionData.getQuestionAnswer(i));
			//MENU
			//pScene.attachChild(buttonSprite[i]);
			//pScene.registerTouchArea(buttonSprite[i]);
		}

		questionSprite = new Sprite(QUESTION_WIDTH / 2, QUESTION_HEIGHT
				/ 2 + BUTTON_HEIGHT * 2,
				ResourceManager.getInstance().mGameTextureRegionQuestion,
				getVertexBufferObjectManager());

		questionText = new Text(QUESTION_WIDTH / 2, QUESTION_HEIGHT / 2,
				ResourceManager.getInstance().mFont, "Przyk³adowe pytanie?",
				100, getVertexBufferObjectManager());
		questionText.setHorizontalAlign(HorizontalAlign.CENTER);
		questionText.setText(mQuestionData.getQuestionText());

		questionSprite.attachChild(questionText);

		//MENU
		//pScene.attachChild(questionSprite);
		
		/////
//		Looper.prepare();
//		gest = new SurfaceGestureDetector(null){
//
//			@Override
//			protected boolean onSingleTap() {
//				// TODO Auto-generated method stub
//				return false;
//			}
//
//			@Override
//			protected boolean onDoubleTap() {
//				// TODO Auto-generated method stub
//				return false;
//			}
//
//			@Override
//			protected boolean onSwipeUp() {
//				// TODO Auto-generated method stub
//				return false;
//			}
//
//			@Override
//			protected boolean onSwipeDown() {
//				// TODO Auto-generated method stub
//				return false;
//			}
//
//			@Override
//			protected boolean onSwipeLeft() {
//				ResourceManager.getInstance().mSound[1].play();
//				++correctAnswersCount;
//				onQuestionComplete(true);
//				return true;
//			}
//
//			@Override
//			protected boolean onSwipeRight() {
//				ResourceManager.getInstance().mSound[0].play();
//				++wrongAnswersCount;
//				onQuestionComplete(false);
//				return true;
//			}
//			
//		};
		//////

		pScene.setTouchAreaBindingOnActionDownEnabled(true);

		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	private void initGame(Scene pScene) {
		
		for (int i = 0; i < 6; ++i) {
			pScene.attachChild(buttonSprite[i]);
			pScene.registerTouchArea(buttonSprite[i]);
		}
		
		pScene.attachChild(questionSprite);
		
		correctAnswersCount = 0;
		wrongAnswersCount = 0;
		locker = false;
		
		//start innej apki
		//startActivity(new Intent(this, MainActivity.class));
	}
	
	public void onAnswersCheck() {

		if (locker) // do not check when question is already finished
			return;

		int correctAnswersChecked = 0;
		// int wrongAnswersChecked = 0;

		for (int i = 0; i < 6; ++i) {
			if (buttonSprite[i].isChecked() == false)
				continue;

			if (buttonSprite[i].isCorrect())
				++correctAnswersChecked;
			// else
			// ++wrongAnswersChecked;
		}

		if (correctAnswersChecked == mQuestionData.numberOfCorrectAnswers) {
			locker = true;
			onQuestionComplete(true);
		}
		// else if (wrongAnswersChecked == 6 -
		// mQuestionData.numberOfCorrectAnswers)
		// onQuestionComplete(false);
	}

	public void onQuestionComplete(boolean correct) {

		Sprite completeSprite = new Sprite(WIDTH / 2, HEIGHT / 2,
				ResourceManager.getInstance().mGameTextureRegionComplete,
				getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

				int eventAction = pSceneTouchEvent.getAction();

				switch (eventAction) {
				case TouchEvent.ACTION_DOWN: {
					mScene.unregisterTouchArea(this);
					ResourceManager.getInstance().mSound[1].play();
					mScene.detachChild(this);
					showNewQuestion();
					for (int i = 0; i < mScene.getChildCount(); ++i) {
						mScene.getChildByIndex(i).setVisible(true);
					}
					for (int i = 0; i < 6; ++i) {
						mScene.registerTouchArea(buttonSprite[i]);
					}
					break;
				}
				default:
					break;
				}

				locker = false;
				return true;
			}
		};

		String text;
		if (correct)
			text = ResourceManager.getInstance().mCorrectCompleteString;
		else // unused
			text = ResourceManager.getInstance().mWrongCompleteString;
		
		// stats
		text += "\ndobrze: ";
		text += String.valueOf(correctAnswersCount);
		text += "    Ÿle: ";
		text += String.valueOf(wrongAnswersCount);

		Text completeText = new Text(completeSprite.getWidth() / 2,
				completeSprite.getHeight() / 2,
				ResourceManager.getInstance().mFont2, text,
				100, getVertexBufferObjectManager());
		completeText.setHorizontalAlign(HorizontalAlign.CENTER);

		for (int i = 0; i < mScene.getChildCount(); ++i) {
			mScene.getChildByIndex(i).setVisible(false);
		}

		completeSprite.attachChild(completeText);
		mScene.attachChild(completeSprite);
		mScene.registerTouchArea(completeSprite);
		for (int i = 0; i < 6; ++i) {
			mScene.unregisterTouchArea(buttonSprite[i]);
		}

		loadNewQuestion();
	}

	public boolean loadNewQuestion() {

		// osobny watek zeby nie freezowac apki? zbaezpieczyc!
//		final Context pContext = this;
//		Thread th = new Thread() {
//			@Override
//			public void run() {
//				mQuestionData = ResourceManager.getInstance().getNextQuestion();
//				mQuestionData.randomShuffle();
//				ResourceManager.getInstance().unloadQuestionTextures();
//				ResourceManager.getInstance().loadQuestionTextures(
//						mQuestionData.getQuestionImagesArray(), mEngine,
//						pContext);
//			}
//		};
//		th.run();

		 mQuestionData = ResourceManager.getInstance().getNextQuestion(mQuestionData);
		 mQuestionData.randomShuffle();
		 ResourceManager.getInstance().unloadQuestionTextures();
		 ResourceManager.getInstance().loadQuestionTextures(
		 mQuestionData.getQuestionImagesArray(), mEngine, this);

		return true;
	}

	public boolean showNewQuestion() {

		for (int i = 0; i < 6; ++i) {
			buttonSprite[i].setImage(
					ResourceManager.getInstance().mGameTextureRegionImage[i],
					getVertexBufferObjectManager());
			buttonSprite[i].setCorrect(mQuestionData.getQuestionAnswer(i));
			buttonSprite[i].Reset();
		}
		questionText.setText(mQuestionData.getQuestionText());

		return true;
	}

//	@Override
//	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
//		return gest.onManagedTouchEvent(pSceneTouchEvent);
//	}
}