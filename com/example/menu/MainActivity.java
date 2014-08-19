package com.example.menu;

import java.io.IOException;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.FixedStepEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
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

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;

public class MainActivity extends BaseGameActivity {

	private static final int WIDTH = 1024; // 800
	private static final int HEIGHT = 768; // 480

	private Scene mScene;
	private Camera mCamera;
	private Sound mSound;
	private Font mFont;
	private ITextureRegion mTextureRegionBackground;
	private String mMenuString;

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
				mEngine.getTextureManager(), 800, 300);

		mTextureRegionBackground = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mBitmapTextureAtlas1, this, "background.png");

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
				Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 32f, true,
				Color.BLACK_ARGB_PACKED_INT);

		mFont.load();

		// Load texts
		mMenuString = "Break Your Mind\n\nImage Quiz\nGuess What\n";

		// Load sounds
		SoundFactory.setAssetBasePath("sfx/menu/");

		try {
			// Create mSound object via SoundFactory class
			mSound = SoundFactory.createSoundFromAsset(
					mEngine.getSoundManager(), this,
					"Stauros - Jaki tu spokoj.mp3");

		} catch (final IOException e) {
			Log.v("Sounds Load", "Exception:" + e.getMessage());
		}

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

		Sprite menuSprite = new Sprite(WIDTH / 2, HEIGHT / 2,
				mTextureRegionBackground, getVertexBufferObjectManager()) {

			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

				int eventAction = pSceneTouchEvent.getAction();

				switch (eventAction) {
				case TouchEvent.ACTION_UP: {
					// mSound.stop();
					if (pSceneTouchEvent.getX() > WIDTH / 2) // right
						startActivity(new Intent(pContext,
								com.example.imagequiz.MainActivity.class));
					else // left
						startActivity(new Intent(pContext,
								com.example.guesswhat.MainActivity.class));
					break;
				}
				default:
					break;
				}

				return true;
			}
		};

		Text menuText = new Text(menuSprite.getWidth() / 2,
				menuSprite.getHeight() / 2, mFont, mMenuString, 100,
				getVertexBufferObjectManager());

		menuText.setHorizontalAlign(HorizontalAlign.CENTER);

		menuSprite.attachChild(menuText);

		pScene.attachChild(menuSprite);

		pScene.registerTouchArea(menuSprite);

		pScene.setTouchAreaBindingOnActionDownEnabled(true);

		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
}