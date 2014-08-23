package com.example.imagequiz;

import org.andengine.engine.FixedStepEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.AnimatedSprite.IAnimationListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.modifier.IModifier;

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

		pOnCreateSceneCallback.onCreateSceneFinished(mScene);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) {
		
		IAnimationListener pCorrectAnimationListener = new IAnimationListener() {

			@Override
			public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
					int pInitialLoopCount) {

				++correctAnswersCount;
				ResourceManager.getInstance().mSound[0].play();
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
				for (int i = 0; i < 6; ++i)
					mScene.unregisterTouchArea(buttonSprite[i]);
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

				for (int i = 0; i < 6; ++i)
					mScene.registerTouchArea(buttonSprite[i]);
			}
		};
		
		buttonSprite = new AnswerSprite[6];
		for (int i = 0; i < 6; ++i) {
			buttonSprite[i] = new AnswerSprite(BUTTON_WIDTH / 2 + (i % 3) * BUTTON_WIDTH,
					BUTTON_HEIGHT / 2 + (i / 3) * BUTTON_HEIGHT,
					ResourceManager.getInstance().mGameTextureRegionImage[i],
					pCorrectAnimationListener, pWrongAnimationListener,
					getVertexBufferObjectManager());

			buttonSprite[i].setCorrect(mQuestionData.getQuestionAnswer(i));

			pScene.attachChild(buttonSprite[i]);
			pScene.registerTouchArea(buttonSprite[i]);
		}

		questionSprite = new Sprite(QUESTION_WIDTH / 2,
				QUESTION_HEIGHT	/ 2 + BUTTON_HEIGHT * 2,
				ResourceManager.getInstance().mGameTextureRegionQuestion,
				getVertexBufferObjectManager());

		questionText = new Text(QUESTION_WIDTH / 2, QUESTION_HEIGHT / 2,
				ResourceManager.getInstance().mFont, mQuestionData.getQuestionText(),
				100, getVertexBufferObjectManager());
		questionText.setHorizontalAlign(HorizontalAlign.CENTER);

		questionSprite.attachChild(questionText);

		pScene.attachChild(questionSprite);
		
		pScene.setTouchAreaBindingOnActionDownEnabled(true);

		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	public void onAnswersCheck() {

		if (locker) // do not check when question is already finished
			return;

		int correctAnswersChecked = 0;

		for (int i = 0; i < 6; ++i) {
			if (buttonSprite[i].isChecked() && buttonSprite[i].isCorrect())
				++correctAnswersChecked;
		}

		if (correctAnswersChecked == mQuestionData.numberOfCorrectAnswers) {
			locker = true;
			onQuestionComplete();
		}
	}

	public void onQuestionComplete() {

		Sprite completeSprite = new Sprite(WIDTH / 2, HEIGHT / 2 - 200,
				ResourceManager.getInstance().mGameTextureRegionComplete,
				getVertexBufferObjectManager());

		completeSprite.setScale(0.0f);

		Text completeText = new Text(completeSprite.getWidth() / 2, completeSprite.getHeight() / 2,
				ResourceManager.getInstance().mFont, ResourceManager.getInstance().mCompleteString,
				100, getVertexBufferObjectManager());

		completeText.setHorizontalAlign(HorizontalAlign.CENTER);

		completeSprite.attachChild(completeText);

		// Animation listeners
		IEntityModifierListener enterModifierListener = new IEntityModifierListener() {

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {

				for (int i = 0; i < 6; ++i)
					mScene.unregisterTouchArea(buttonSprite[i]);
			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {

				for (int i = 0; i < mScene.getChildCount(); ++i) {
					if (mScene.getChildByIndex(i) == pItem)
						continue;

					mScene.getChildByIndex(i).setVisible(false);
				}

				loadNewQuestion();
			}
		};

		IEntityModifierListener exitModifierListener = new IEntityModifierListener() {

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {

				ResourceManager.getInstance().mSound[2].play();
				showNewQuestion();
				for (int i = 0; i < mScene.getChildCount(); ++i)
					mScene.getChildByIndex(i).setVisible(true);
			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {	

				for (int i = 0; i < 6; ++i)
					mScene.registerTouchArea(buttonSprite[i]);

				locker = false;
				pItem.detachChildren();
				pItem.detachSelf();
			}
		};

		// Moving
		MoveByModifier moveModifier[] = new MoveByModifier[2];
		moveModifier[0] = new MoveByModifier(1.5f, 0f, 200f);
		moveModifier[1] = new MoveByModifier(1.0f, 0f, 100f);

		// Scaling
		ScaleModifier scaleModifier[] = new ScaleModifier[2];
		scaleModifier[0] = new ScaleModifier(1.5f, 0.0f, 1.0f);
		scaleModifier[1] = new ScaleModifier(1.0f, 1.0f, 0.0f);

		// Delay
		DelayModifier delayModifier = new DelayModifier(1.5f);

		// Connecting moving and scaling together
		ParallelEntityModifier parallelEntityModifier[] = new ParallelEntityModifier[2];
		parallelEntityModifier[0] = new ParallelEntityModifier(moveModifier[0], scaleModifier[0]);
		parallelEntityModifier[1] = new ParallelEntityModifier(moveModifier[1], scaleModifier[1]);
		parallelEntityModifier[0].addModifierListener(enterModifierListener);
		parallelEntityModifier[1].addModifierListener(exitModifierListener);

		// Final animation modifier
		SequenceEntityModifier sequenceEntityModifier =
				new SequenceEntityModifier(parallelEntityModifier[0], delayModifier, parallelEntityModifier[1]);

		// Show screen on scene
		mScene.attachChild(completeSprite);

		// Apply animation
		completeSprite.registerEntityModifier(sequenceEntityModifier);
	}

	public boolean loadNewQuestion() {

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
}