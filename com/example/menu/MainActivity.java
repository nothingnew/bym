package com.example.menu;

import org.andengine.engine.FixedStepEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.IModifier;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;

public class MainActivity extends BaseGameActivity {

	private static final int WIDTH = 1024; // 800
	private static final int HEIGHT = 768; // 480

	private Scene mScene;
	private Camera mCamera;
	//private Sound mSound;
	private Font mFont;
	private ITextureRegion mTextureRegionBackground;
	private ITextureRegion mTextureRegionTitle;
	private ITextureRegion mTextureRegionBrain[];
	private String mMenuString;
	private Sprite brainSprite[];
	private Sprite titleSprite;
	private boolean mBrainSplitted;
	
	@Override
	public EngineOptions onCreateEngineOptions() {

		mCamera = new Camera(0, 0, WIDTH, HEIGHT);

		EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE_SENSOR, new FillResolutionPolicy(),
				mCamera);

		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		//engineOptions.getAudioOptions().setNeedsSound(true);

		mEngine = new FixedStepEngine(engineOptions, 30);

		return engineOptions;
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback) {

		// load textures
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");

		BuildableBitmapTextureAtlas mBitmapTextureAtlas1 = new BuildableBitmapTextureAtlas(
				mEngine.getTextureManager(), 1024, 768);
		BuildableBitmapTextureAtlas mBitmapTextureAtlas2 = new BuildableBitmapTextureAtlas(
				mEngine.getTextureManager(), 1024, 200);
		
		mTextureRegionBackground = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mBitmapTextureAtlas1, this, "background.png");
		mTextureRegionBrain = new ITextureRegion[4];
		mTextureRegionBrain[0] = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mBitmapTextureAtlas1, this, "left.png");
		mTextureRegionBrain[1] = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mBitmapTextureAtlas1, this, "top.png");
		mTextureRegionBrain[2] = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mBitmapTextureAtlas1, this, "right.png");
		mTextureRegionBrain[3] = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mBitmapTextureAtlas1, this, "bottom.png");
		mTextureRegionTitle = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mBitmapTextureAtlas2, this, "title.png");

		try {
			mBitmapTextureAtlas1
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 1, 1));
			mBitmapTextureAtlas2
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 0, 0));

			mBitmapTextureAtlas1.load();
			mBitmapTextureAtlas2.load();
		} catch (TextureAtlasBuilderException e) {
			Debug.e(e);
		}

		// Load fonts.
		mFont = FontFactory.create(mEngine.getFontManager(),
				mEngine.getTextureManager(), 256, 256,
				Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 50f, true,
				Color.BLACK_ARGB_PACKED_INT);

		mFont.load();

		// Load texts
		mMenuString = "Break Your Mind\n\nGuess What          Image Quiz";

		// Load sounds
//		SoundFactory.setAssetBasePath("sfx/menu/");
//		try {
//			// Create mSound object via SoundFactory class
//			mSound = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(), this, "linkin_park.mp3");
//
//		} catch (final IOException e) {
//			Log.v("Sounds Load", "Exception:" + e.getMessage());
//		}

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

		// nie dziala, leci error
		// mSound.play();
		
		mBrainSplitted = false;
		
		titleSprite = new Sprite(WIDTH / 2, HEIGHT - 100, 
				mTextureRegionTitle, getVertexBufferObjectManager());
		pScene.attachChild(titleSprite);
		
		brainSprite = new Sprite[4];
		
		brainSprite[0] = new Sprite(WIDTH / 2 - 250 + 90, HEIGHT / 2 - 110 - 35,
				mTextureRegionBrain[0], getVertexBufferObjectManager()) {

			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

				int eventAction = pSceneTouchEvent.getAction();

				switch (eventAction) {
				case TouchEvent.ACTION_DOWN: {
					Log.v("Brain", "left brain clicked");

					if (mBrainSplitted)
						brainMerge(0);
					else
						brainSplit();
					break;
				}
				default:
					break;
				}

				return true;
			}
		};
		brainSprite[1] = new Sprite(WIDTH / 2 - 5, HEIGHT / 2 + 150 - 110 - 100,
				mTextureRegionBrain[1], getVertexBufferObjectManager()) {

			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

				int eventAction = pSceneTouchEvent.getAction();

				switch (eventAction) {
				case TouchEvent.ACTION_DOWN: {
					Log.v("Brain", "top brain clicked");

					if (mBrainSplitted)
						brainMerge(1);
					else
						brainSplit();
					break;
				}
				default:
					break;
				}

				return true;
			}
		};
		brainSprite[2] = new Sprite(WIDTH / 2 + 250 - 90, HEIGHT / 2 - 110,
				mTextureRegionBrain[2], getVertexBufferObjectManager()) {

			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

				int eventAction = pSceneTouchEvent.getAction();

				switch (eventAction) {
				case TouchEvent.ACTION_DOWN: {
					Log.v("Brain", "right brain clicked");

					if (mBrainSplitted)
						brainMerge(2);
					else
						brainSplit();
					break;
				}
				default:
					break;
				}

				return true;
			}
		};
		brainSprite[3] = new Sprite(WIDTH / 2 + 30, HEIGHT / 2 - 150 - 110 + 50,
				mTextureRegionBrain[3], getVertexBufferObjectManager()) {

			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

				int eventAction = pSceneTouchEvent.getAction();

				switch (eventAction) {
				case TouchEvent.ACTION_DOWN: {
					Log.v("Brain", "bottom brain clicked");

					if (mBrainSplitted)
						brainMerge(3);
					else
						brainSplit();
					break;
				}
				default:
					break;
				}

				return true;
			}
		};
		

//		Sprite menuSprite = new Sprite(WIDTH / 2, HEIGHT - 150,
//				mTextureRegionBackground, getVertexBufferObjectManager()) {
//
//			@Override
//			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
//					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
//
//				int eventAction = pSceneTouchEvent.getAction();
//
//				switch (eventAction) {
//				case TouchEvent.ACTION_UP: {
//					// mSound.stop();
//					if (pSceneTouchEvent.getX() > WIDTH / 2) // right
//						startActivity(new Intent(pContext,
//								com.example.imagequiz.MainActivity.class));
//					else // left
//						startActivity(new Intent(pContext,
//								com.example.guesswhat.MainActivity.class));
//					break;
//				}
//				default:
//					break;
//				}
//
//				return true;
//			}
//		};
//
//		Text menuText = new Text(menuSprite.getWidth() / 2,
//				menuSprite.getHeight() / 2, mFont, mMenuString, 100,
//				getVertexBufferObjectManager());
//
//		menuText.setHorizontalAlign(HorizontalAlign.CENTER);
//
//		menuSprite.attachChild(menuText);
//
//		pScene.attachChild(menuSprite);
//		pScene.registerTouchArea(menuSprite);
		
		for (int i = 0; i < 4; ++i)
		{
			pScene.attachChild(brainSprite[i]);
			pScene.registerTouchArea(brainSprite[i]);
		}

		pScene.setBackgroundEnabled(true);
		pScene.setBackground(new Background(1.0f, 1.0f, 1.0f));
		pScene.setTouchAreaBindingOnActionDownEnabled(true);

		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	public void brainSplit() {

		mBrainSplitted = true;
		
		MoveByModifier moveModifier[] = new MoveByModifier[4];
		moveModifier[0] = new MoveByModifier(0.8f, -90f, 35f);
		moveModifier[1] = new MoveByModifier(0.8f, 5f, 100f);
		moveModifier[2] = new MoveByModifier(0.8f, 90f, 0f);
		moveModifier[3] = new MoveByModifier(0.8f, -30f, -50f);

		for (int i = 0; i < 4; ++i) {
			brainSprite[i].registerEntityModifier(moveModifier[i]);
		}
	}
	
	public void brainMerge(final int index) {

		mBrainSplitted = false;
		final Context pContext = this;

		IEntityModifierListener entityModifierListener = new IEntityModifierListener() {

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {

				switch(index) {
				case 0:
					startActivity(new Intent(pContext,
								com.example.imagequiz.MainActivity.class));
					break;
				case 1:
					startActivity(new Intent(pContext,
								com.example.truefalse.MainActivity.class));
					break;
				case 2:
					startActivity(new Intent(pContext,
								com.example.guesswhat.MainActivity.class));
					break;
				case 3:
					startActivity(new Intent(pContext,
								com.example.findwords.MainActivity.class));
					break;
				default:
					Log.w("Brain", "wrong index in brainMerge function!");					
				}
			}
		};

		// Moving
		MoveByModifier moveModifier[] = new MoveByModifier[4];
		moveModifier[0] = new MoveByModifier(0.2f, 90f, -35f);
		moveModifier[1] = new MoveByModifier(0.2f, -5f, -100f);
		moveModifier[2] = new MoveByModifier(0.2f, -90f, 0f);
		moveModifier[3] = new MoveByModifier(0.2f, 30f, 50f);
		moveModifier[3].addModifierListener(entityModifierListener);

		for (int i = 0; i < 4; ++i)
			brainSprite[i].registerEntityModifier(moveModifier[i]);
	}
}