package com.example.menu;

//import java.io.IOException;
//
//import org.andengine.audio.sound.Sound;
//import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.FixedStepEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.Scene;
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
//import android.util.Log;

public class MainActivity extends BaseGameActivity {

	private static final int WIDTH = 1024; // 800
	private static final int HEIGHT = 768; // 480

	private Scene mScene;
	private Camera mCamera;
	//private Sound mSound;
	private Font mFont;
	private ITextureRegion mTextureRegionBackground, mTextureRegionFullBrain;
	private ITextureRegion mTextureRegionBrain[];
	private String mMenuString;
	private Sprite brainSprite[];
	
	private boolean splitted;
	private boolean merged;
	private boolean notStarted;
	
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

		// load textures
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");

		BuildableBitmapTextureAtlas mBitmapTextureAtlas1 = new BuildableBitmapTextureAtlas(
				mEngine.getTextureManager(), 1024, 768);

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

		try {
			mBitmapTextureAtlas1
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 1, 1));

			mBitmapTextureAtlas1.load();
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

		final Context pContext = this;

		// nie dziala, leci error
		// mSound.play();
		
		splitted = false;
		merged = true;
		notStarted = true;
		
		brainSprite = new Sprite[4];
		
		brainSprite[0] = new Sprite(WIDTH / 2 - 250 + 90, HEIGHT / 2 - 110 - 35,
				mTextureRegionBrain[0], getVertexBufferObjectManager()) {

			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

				int eventAction = pSceneTouchEvent.getAction();

				switch (eventAction) {
				case TouchEvent.ACTION_DOWN: {
					System.out.println("left brain clicked");
					
					if (splitted == false && merged == true) {
						brainSplit();
						splitted = true;
						merged = false;
					} else {
						brainMerge(0);
						merged = true;
						splitted = false;
						notStarted = true;
					}
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
					System.out.println("top brain clicked");
					
					if (splitted == false && merged == true) {
						brainSplit();
						splitted = true;
						merged = false;
					} else {
						brainMerge(1);
						merged = true;
						splitted = false;
						notStarted = true;
					}
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
					System.out.println("right brain clicked");
					
					if (splitted == false && merged == true) {
						brainSplit();
						splitted = true;
						merged = false;
					} else {
						brainMerge(2);
						merged = true;
						splitted = false;
						notStarted = true;
					}
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
					System.out.println("bottom brain clicked");

					if (splitted == false && merged == true) {
						brainSplit();
						splitted = true;
						merged = false;
					} else {
						brainMerge(3);
						merged = true;
						splitted = false;
						notStarted = true;
					}
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

		pScene.setTouchAreaBindingOnActionDownEnabled(true);

		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	public void brainSplit() {
		
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
					if(notStarted == true) {
						startActivity(new Intent(pContext,
								com.example.imagequiz.MainActivity.class));
						notStarted = false;
					}
					break;
				case 1:
					if(notStarted == true) {
						startActivity(new Intent(pContext,
								com.example.truefalse.MainActivity.class));
						notStarted = false;
					}
					break;
				case 2:
					if(notStarted == true) {
						startActivity(new Intent(pContext,
								com.example.guesswhat.MainActivity.class));
						notStarted = false;
					}
					break;
				case 3:
					if(notStarted == true) {
						startActivity(new Intent(pContext,
								com.example.findwords.MainActivity.class));
						notStarted = false;
					}
					break;
				default:
					System.out.println("Function 'brainMerge' error!");						
				}
			}
		};
		
		// Moving
		MoveByModifier moveModifier[] = new MoveByModifier[4];
		
		moveModifier[0] = new MoveByModifier(0.2f, 90f, -35f);
		moveModifier[1] = new MoveByModifier(0.2f, -5f, -100f);
		moveModifier[2] = new MoveByModifier(0.2f, -90f, 0f);
		moveModifier[3] = new MoveByModifier(0.2f, 30f, 50f);
		
//		// Scaling
//		ScaleModifier scaleModifier[] = new ScaleModifier[4];
//		
//		scaleModifier[0] = new ScaleModifier(0.2f, 1.0f, 0.0f);
//		scaleModifier[1] = new ScaleModifier(0.2f, 1.0f, 0.0f);
//		scaleModifier[2] = new ScaleModifier(0.2f, 1.0f, 0.0f);
//		scaleModifier[3] = new ScaleModifier(0.2f, 1.0f, 0.0f);
//		
//		// Connecting moving and scaling together
//		ParallelEntityModifier parallelEntityModifier[] = new ParallelEntityModifier[4];
		
		for (int i = 0; i < 4; ++i) {
			moveModifier[i].addModifierListener(entityModifierListener);
			brainSprite[i].registerEntityModifier(moveModifier[i]);
//			parallelEntityModifier[i] = new ParallelEntityModifier(moveModifier[i], scaleModifier[i]);
//			brainSprite[i].registerEntityModifier(parallelEntityModifier[i]);
		}
	}
}