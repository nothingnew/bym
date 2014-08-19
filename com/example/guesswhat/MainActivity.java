package com.example.guesswhat;

import org.andengine.engine.FixedStepEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.AnimatedSprite.IAnimationListener;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

public class MainActivity extends BaseGameActivity {

	private static final int WIDTH = 1024;
	private static final int HEIGHT = 768;
	private static final int QUESTION_WIDTH = WIDTH;
	private static final int QUESTION_HEIGHT = HEIGHT / 3;
	private static final int ANSWER_WIDTH = WIDTH / 2;
	private static final int ANSWER_HEIGHT = HEIGHT / 3;
	
	private Scene mScene;
	private Camera mCamera;
	
	private Sprite questionBarSprite, finishSprite;
	private QuestionBar questionBar;
	private AnswerButton[] answerButton;
	
	private Text qText, fText;
	
	private boolean lock;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		// Define our mCamera object
		mCamera = new Camera(0, 0, WIDTH, HEIGHT);
		
		// Declare & Define our engine options to be applies to our Engine object
		EngineOptions engineOptions = new EngineOptions(true, 
			ScreenOrientation.LANDSCAPE_SENSOR, new FillResolutionPolicy(), mCamera);
		
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		engineOptions.getAudioOptions().setNeedsSound(true);
		
		mEngine = new FixedStepEngine(engineOptions, 60);
		
		// Return the engineOptions object, passing it to the engine
		return engineOptions;
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) {
		
		// Load the game texture resources
		ResourceManager.getInstance().loadGameTextures(mEngine, this);
		
		// Load the sound resources
		ResourceManager.getInstance().loadSounds(mEngine, this);
		
		// Load the font resources
		ResourceManager.getInstance().loadFonts(mEngine);
		
		// Load the text resources
		ResourceManager.getInstance().loadText();
		
		// Load all questions
		ResourceManager.getInstance().loadQuestions(this);

		// Load first question
		questionBar = ResourceManager.getInstance().nextQuestion();
		questionBar.randomLocation();
		ResourceManager.getInstance().loadQuestionTexts(ANSWER_WIDTH / 2, ANSWER_HEIGHT / 2,
				questionBar.getQuestionAnswersArray(), mEngine, this);
			
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		// Create the Scene object
		mScene = new Scene();

		pOnCreateSceneCallback.onCreateSceneFinished(mScene);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) {
				
		lock = false;
		
		// Answer buttons
		answerButton = new AnswerButton[4];
		
		IAnimationListener pAnimationListener = new IAnimationListener() {

			@Override
			public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
					int pInitialLoopCount) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,
					int pOldFrameIndex, int pNewFrameIndex) {
				
				if(pOldFrameIndex == 0 && pNewFrameIndex == 1)
					ResourceManager.getInstance().success.play();
			}

			@Override
			public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
					int pRemainingLoopCount, int pInitialLoopCount) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
				
				answerChecking();				
			}
			
		};
		
		for (int i=0; i<4; ++i) {
			answerButton[i] = new AnswerButton(ANSWER_WIDTH / 2 + (i % 2) * ANSWER_WIDTH,
					ANSWER_HEIGHT / 2 + (i / 2) * ANSWER_HEIGHT, ResourceManager.getInstance().answerRegion,
					getVertexBufferObjectManager(), pAnimationListener,
					questionBar.getQuestionAnswer(i));
			
			answerButton[i].setCorrect(questionBar.getQuestionResult(i));
			pScene.attachChild(answerButton[i]);
			pScene.registerTouchArea(answerButton[i]);
		}
		
		// Question bar
		questionBarSprite = new Sprite(QUESTION_WIDTH / 2, HEIGHT - (QUESTION_HEIGHT / 2), 
				ResourceManager.getInstance().questionRegion, mEngine.getVertexBufferObjectManager());

		qText = new Text(QUESTION_WIDTH / 2, QUESTION_HEIGHT / 2, ResourceManager.getInstance().qFont, 
				"Pytanie", 300, getVertexBufferObjectManager());
		qText.setHorizontalAlign(HorizontalAlign.CENTER);
		qText.setColor(15f/255f, 30f/255f, 45f/255f);
		qText.setText(questionBar.getQuestionText());
		
		questionBarSprite.attachChild(qText);
		pScene.attachChild(questionBarSprite);
		
		pScene.setTouchAreaBindingOnActionDownEnabled(true);

		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	public void answerChecking() {

		if(lock == true) {
			return;
		}
		
		for (int i=0; i<4; ++i) {
			if (answerButton[i].isTouched() == false)
				continue;

			if (answerButton[i].isCorrect()) {
				lock = true;
				finishedQuestion(true);
			}
		}
	}

	public void finishedQuestion(boolean correct) {

			finishSprite = new Sprite(WIDTH / 2, HEIGHT / 2,
				ResourceManager.getInstance().finishRegion, mEngine.getVertexBufferObjectManager()) {
			
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

				int eventAction = pSceneTouchEvent.getAction();

				switch (eventAction) {
				case TouchEvent.ACTION_UP: {
					
					lock = false;
					
					mScene.unregisterTouchArea(this);
					ResourceManager.getInstance().buttonClick.play();
					mScene.detachChild(this);
					
					showNewQuestion();
					
					for (int i=0; i<mScene.getChildCount(); ++i) {
						mScene.getChildByIndex(i).setVisible(true);
					}
					for (int i=0; i<4; ++i) {
						mScene.registerTouchArea(answerButton[i]);
					}
					
					break;
				}
				default:
					break;
				}

				return true;
			}
		};

		String msg = new String();
		if (correct) {
			msg = ResourceManager.getInstance().successMessage;
		}

		fText = new Text(WIDTH / 2, HEIGHT / 2, ResourceManager.getInstance().aFont, msg,
				mEngine.getVertexBufferObjectManager());
		fText.setHorizontalAlign(HorizontalAlign.CENTER);
		fText.setColor(Color.BLACK);

		finishSprite.attachChild(fText);
		mScene.attachChild(finishSprite);
		mScene.registerTouchArea(finishSprite);
		
		for (int i=0; i<4; ++i) {
			mScene.unregisterTouchArea(answerButton[i]);
		}

		loadNewQuestion();
	}

	public boolean loadNewQuestion() {

		questionBar = ResourceManager.getInstance().nextQuestion();
		questionBar.randomLocation();
		ResourceManager.getInstance().loadQuestionTexts(ANSWER_WIDTH / 2, ANSWER_HEIGHT / 2,
				questionBar.getQuestionAnswersArray(), mEngine, this);

		return true;
	}

	public boolean showNewQuestion() {

		for (int i=0; i<4; ++i) {
			answerButton[i].setText(getVertexBufferObjectManager(), questionBar.getQuestionAnswer(i));
			answerButton[i].setCorrect(questionBar.getQuestionResult(i));
			answerButton[i].Reset();
		}
		
		qText.setText(questionBar.getQuestionText());

		return true;
	}
}