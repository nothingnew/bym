package com.example.findwords;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.andengine.engine.FixedStepEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.modifier.IModifier;

import android.util.Log;

public class MainActivity extends BaseGameActivity implements
		IOnSceneTouchListener {

	private static final int WIDTH = 1024; // 800
	private static final int HEIGHT = 768; // 480
	private static final int BUTTON_WIDTH = 93;
	private static final int BUTTON_HEIGHT = 96;
	private static final int ROWS = 8;
	private static final int COLUMNS = 11;
	private static final String alphabet = "A•BC∆DE FGHIJKL£MN—O”PQRSåTUVWXYZèØ";

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

		for (int i = 0; i < COLUMNS; ++i)
			for (int j = 0; j < ROWS; ++j) {

				mSpriteLetters[i][j] = new LetterButtonSprite(
						BUTTON_WIDTH / 2 + i * BUTTON_WIDTH,
						BUTTON_HEIGHT / 2 + j * BUTTON_HEIGHT,
						ResourceManager.getInstance().mGameTextureRegionBackground,
						ResourceManager.getInstance().mFont, "X",
						getVertexBufferObjectManager());

				pScene.attachChild(mSpriteLetters[i][j]);
			}

		loadNewGrid();

		pScene.setOnSceneTouchListener(this);
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene,
			final TouchEvent pSceneTouchEvent) {

		if (mCurrentWordsList.isEmpty())
			return true;

		int eventAction = pSceneTouchEvent.getAction();

		switch (eventAction) {
		case TouchEvent.ACTION_DOWN:
			if (mIgnoreMove)
				mIgnoreMove = false;
			// no break here!
		case TouchEvent.ACTION_MOVE: {
			if (mIgnoreMove)
				break;

			int i = (int) pSceneTouchEvent.getX() / BUTTON_WIDTH;
			int j = (int) pSceneTouchEvent.getY() / BUTTON_HEIGHT;

			if (mSpriteLetters[i][j].getCurrentTileIndex() != 1)
				onLetterButtonClick(i, j);
			
			break;
			}
		default:
			return true;
		}

		return true;
	}

	public void onLetterButtonClick(int i, int j) {

		if (mCurrentSpriteList.isEmpty()) {
			mSpriteLetters[i][j].setCurrentTileIndex(1);
			mCurrentSpriteList.add(mSpriteLetters[i][j]);
			updateAvailableSprites();
			mCurrentText += mSpriteLetters[i][j].getLetter();

			return;
		}

		if (!mAvailableSpriteList.contains(mSpriteLetters[i][j])) {

			for (LetterButtonSprite sprite : mCurrentSpriteList)
				sprite.resetBackground();

			mCurrentSpriteList.clear();
			mCurrentText = "";
			mIgnoreMove = true;

			return;
		}

		mCurrentSpriteList.add(mSpriteLetters[i][j]);
		Collections.sort(mCurrentSpriteList, new LetterButtonComparator());
		updateAvailableSprites();
		mCurrentText = "";
		for (LetterButtonSprite sprite : mCurrentSpriteList)
			mCurrentText += sprite.getLetter();

		if (!checkCorrectness())
			mSpriteLetters[i][j].setCurrentTileIndex(1);
	}

	public boolean checkCorrectness() {

		for (String word : mCurrentWordsList)
			if (word.equalsIgnoreCase(mCurrentText)) {
				mIgnoreMove = true;
				mCurrentText = "";
				for (LetterButtonSprite sprite : mCurrentSpriteList) {
					if (sprite.isLocked())
						sprite.resetBackground();
					else
						sprite.lockIn();
				}
				mCurrentSpriteList.clear();
				mCurrentWordsList.remove(word);

				if (mCurrentWordsList.isEmpty())
					onTaskComplete();
				
				return true;
			}

		return false;
	}

	public void updateAvailableSprites() {

		// clear current elements
		mAvailableSpriteList.clear();

		// in list is empty do nothing
		if (mCurrentSpriteList.isEmpty())
			return;

		LetterButtonSprite first = mCurrentSpriteList.get(0);
		int firstColumn = (int) first.getX() / BUTTON_WIDTH;
		int firstRow = (int) first.getY() / BUTTON_HEIGHT;

		// if there is only one sprite we can go in both directions
		if (mCurrentSpriteList.size() == 1) {

			if (firstColumn != 0)
				mAvailableSpriteList
						.add(mSpriteLetters[firstColumn - 1][firstRow]);
			if (firstColumn != COLUMNS - 1)
				mAvailableSpriteList
						.add(mSpriteLetters[firstColumn + 1][firstRow]);
			if (firstRow != 0)
				mAvailableSpriteList
						.add(mSpriteLetters[firstColumn][firstRow - 1]);
			if (firstRow != ROWS - 1)
				mAvailableSpriteList
						.add(mSpriteLetters[firstColumn][firstRow + 1]);

			return;
		}

		LetterButtonSprite last = mCurrentSpriteList.get(mCurrentSpriteList.size() - 1);
		int lastColumn = (int) last.getX() / BUTTON_WIDTH;
		int lastRow = (int) last.getY() / BUTTON_HEIGHT;

		// if there are more sprites we can go only in one direction
		if (firstColumn == lastColumn) {
			if (lastRow != 0)
				mAvailableSpriteList
						.add(mSpriteLetters[firstColumn][lastRow - 1]);
			if (firstRow != ROWS - 1)
				mAvailableSpriteList
						.add(mSpriteLetters[firstColumn][firstRow + 1]);
		} else if (firstRow == lastRow) {
			if (firstColumn != 0)
				mAvailableSpriteList
						.add(mSpriteLetters[firstColumn - 1][firstRow]);
			if (lastColumn != COLUMNS - 1)
				mAvailableSpriteList
						.add(mSpriteLetters[lastColumn + 1][firstRow]);
		} else
			Log.w("Grid", "updateAvailableSprites: two letters not in the same line!");

		return;
	}

	public void loadNewGrid() {

		// must be local copy due to remove operations
		mCurrentWordsList = new ArrayList<String>();
		mCurrentWordsList.addAll(ResourceManager.getInstance().getNewWords());

		//int counter;
		int column, row;
		Random rgen = new Random();

		// clear old grid
		for (int i = 0; i < COLUMNS; ++i)
			for (int j = 0; j < ROWS; ++j) {
				mSpriteLetters[i][j].setLetter(" ");
				mSpriteLetters[i][j].Reset();
			}

		row = 0;
		Collections.shuffle(mCurrentWordsList);

		for (String word : mCurrentWordsList) {
			if (row == ROWS) // too much words :)
			{
				Log.v("Grid", "loadNewGrid: too much words to put into grid (8+), truncate.");
				mCurrentWordsList = mCurrentWordsList.subList(0, ROWS);
				break;
			}
			
			if (word.length() == COLUMNS)
				column = 0;
			else
				column = rgen.nextInt(COLUMNS - word.length() + 1);

			// put word into grid
			for (int i = 0; i < word.length(); ++i)
				mSpriteLetters[column + i][row].setLetter(word.toUpperCase(Locale.getDefault()).subSequence(i, i + 1));
			
			++row;
		}

		// fill empty spaces with random letters from alphabet
		for (int i = 0; i < COLUMNS; ++i)
			for (int j = 0; j < ROWS; ++j)
				if (mSpriteLetters[i][j].getLetter() == " ")
					mSpriteLetters[i][j].setLetter(String.valueOf(alphabet.charAt(rgen.nextInt(alphabet.length()))));
	}

	public void onTaskComplete() {

		Sprite completeSprite = new Sprite(WIDTH / 2, HEIGHT / 2 - 200,
				ResourceManager.getInstance().mGameTextureRegionComplete,
				getVertexBufferObjectManager());

		String text= "DOBRZE\n£adujÍ nowe zadanie.";
		Text completeText = new Text(completeSprite.getWidth() / 2,
				completeSprite.getHeight() / 2,
				ResourceManager.getInstance().mFont, text,
				100, getVertexBufferObjectManager());
		completeText.setHorizontalAlign(HorizontalAlign.CENTER);

		completeSprite.attachChild(completeText);

		// Animation control
		IEntityModifierListener enterModifierListener = new IEntityModifierListener() {

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {

				ResourceManager.getInstance().mSound.play();
			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {

//				pModifier.reset();
//				pItem.clearEntityModifiers();
//				pItem.unregisterEntityModifier((IEntityModifier) pModifier);
			}
		};

		IEntityModifierListener exitModifierListener = new IEntityModifierListener() {

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {

				loadNewGrid();
			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {

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
}
