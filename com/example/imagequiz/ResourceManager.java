package com.example.imagequiz;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;
import org.xml.sax.InputSource;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

public class ResourceManager {

	// ResourceManager Singleton instance
	private static ResourceManager INSTANCE;

	/*
	 * The variables listed should be kept public, allowing us easy access to
	 * them when creating new Sprites, Text objects and to play sound files
	 */
	public ITextureRegion mGameTextureRegionImage[];
	public ITextureRegion mGameTextureRegionQuestion;
	public ITextureRegion mGameTextureRegionComplete;
	public ITiledTextureRegion mGameTextureRegionBackground;

	public Sound mSound[];

	public Font mFont;
	public Font mFont2;

	public String mCorrectCompleteString;
	public String mWrongCompleteString;

	public List<QuestionData> mQuestions;

	ResourceManager() {
		// The constructor is of no use to us
	}

	public synchronized static ResourceManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ResourceManager();
		}
		return INSTANCE;
	}

	/*
	 * Each scene within a game should have a loadTextures method as well as an
	 * accompanying unloadTextures method. This way, we can display a loading
	 * image during scene swapping, unload the first scene's textures then load
	 * the next scenes textures.
	 */
	public synchronized void loadGameTextures(Engine pEngine, Context pContext) {

		BitmapTextureAtlasTextureRegionFactory
				.setAssetBasePath("gfx/imagequiz/background/");

		BuildableBitmapTextureAtlas mBitmapTextureAtlas1 = new BuildableBitmapTextureAtlas(
				pEngine.getTextureManager(), 1030, 400);
		BuildableBitmapTextureAtlas mBitmapTextureAtlas2 = new BuildableBitmapTextureAtlas(
				pEngine.getTextureManager(), 1024, 320);

		mGameTextureRegionQuestion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mBitmapTextureAtlas1, pContext, "question.png");
		mGameTextureRegionComplete = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mBitmapTextureAtlas1, pContext, "complete.png");
		mGameTextureRegionBackground = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(mBitmapTextureAtlas2, pContext,
						"background.png", 3, 1);

		// to load images for answers
		mGameTextureRegionImage = new TextureRegion[6];

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
	}

	/*
	 * All textures should have a method call for unloading once they're no
	 * longer needed; ie. a level transition.
	 */
	public synchronized void unloadGameTextures() {
		// call unload to remove the corresponding texture atlas from memory
		BuildableBitmapTextureAtlas mBitmapTextureAtlas1 = (BuildableBitmapTextureAtlas) mGameTextureRegionQuestion
				.getTexture();
		mBitmapTextureAtlas1.unload();
		BuildableBitmapTextureAtlas mBitmapTextureAtlas2 = (BuildableBitmapTextureAtlas) mGameTextureRegionBackground
				.getTexture();
		mBitmapTextureAtlas2.unload();

		System.gc();
	}

	/*
	 * As with textures, we can create methods to load sound/music objects for
	 * different scene's within our games.
	 */
	public synchronized void loadSounds(Engine pEngine, Context pContext) {
		// Set the SoundFactory's base path
		SoundFactory.setAssetBasePath("sfx/imagequiz/");
		mSound = new Sound[3];
		try {
			// Create mSound object via SoundFactory class
			mSound[0] = SoundFactory.createSoundFromAsset(
					pEngine.getSoundManager(), pContext, "cell_phone_nr0.mp3");
			mSound[1] = SoundFactory.createSoundFromAsset(
					pEngine.getSoundManager(), pContext, "cell_phone_nr1.mp3");
			mSound[2] = SoundFactory.createSoundFromAsset(
					pEngine.getSoundManager(), pContext, "cell_phone_nr2.mp3");
		} catch (final IOException e) {
			Log.v("Sounds Load", "Exception:" + e.getMessage());
		}
	}

	/*
	 * In some cases, we may only load one set of sounds throughout our entire
	 * game's life-cycle. If that's the case, we may not need to include an
	 * unloadSounds() method. Of course, this all depends on how much variance
	 * we have in terms of sound
	 */
	public synchronized void unloadSounds() {
		// we call the release() method on sounds to remove them from memory
		for (int i = 0; i < 3; ++i) {
			if (!mSound[i].isReleased())
				mSound[i].release();
		}
	}

	/*
	 * Lastly, we've got the loadFonts method which, once again, tends to only
	 * need to be loaded once as Font's are generally used across an entire
	 * game, from menu to shop to game-play.
	 */
	public synchronized void loadFonts(Engine pEngine) {
		FontFactory.setAssetBasePath("fonts/");

		// Create mFont object via FontFactory class
		mFont = FontFactory.create(pEngine.getFontManager(),
				pEngine.getTextureManager(), 256, 256,
				Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 48f, true,
				Color.BLACK_ARGB_PACKED_INT);

		mFont.load();

		mFont2 = FontFactory.create(pEngine.getFontManager(),
				pEngine.getTextureManager(), 256, 256,
				Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 40f, true,
				Color.BLACK_ARGB_PACKED_INT);

		mFont2.load();
	}

	/*
	 * If an unloadFonts() method is necessary, we can provide one
	 */
	public synchronized void unloadFonts() {
		// Similar to textures, we can call unload() to destroy font resources
		mFont.unload();
		mFont2.unload();
	}

	public synchronized void loadText() {

		mCorrectCompleteString = "DOBRZE.\nDotknij aby przejœæ dalej.";
		mWrongCompleteString = "Niestety nie uda³o siê.\nDotknij aby przejœæ dalej.";
	}

	public synchronized void loadQuestions(Context pContext) {

		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			XMLParser handler = new XMLParser();

			InputStream inputStream = pContext.getAssets().open(
					"xml/imagequiz/questions.xml");
			Reader reader = new InputStreamReader(inputStream, "UTF-8");

			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");

			saxParser.parse(is, handler);

			// result
			mQuestions = handler.getResults();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized QuestionData getNextQuestion(QuestionData except) {

		Random rgen = new Random();
		QuestionData data;

		data = mQuestions.get(rgen.nextInt(mQuestions.size()));
		
		if (except == null)
			return data;
			
		while (data.getQuestionText().equalsIgnoreCase(except.getQuestionText()))
		{
			data = mQuestions.get(rgen.nextInt(mQuestions.size()));
		}
		
		return data;
	}

	public synchronized void loadQuestionTextures(String[] files,
			Engine pEngine, Context pContext) {

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/imagequiz/game/");

		BuildableBitmapTextureAtlas mBitmapTextureAtlas = new BuildableBitmapTextureAtlas(
				pEngine.getTextureManager(), 1024, 768);

		for (int i = 0; i < 6; ++i) {
			mGameTextureRegionImage[i] = BitmapTextureAtlasTextureRegionFactory
					.createFromAsset(mBitmapTextureAtlas, pContext, files[i]);
		}

		try {
			mBitmapTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 1, 1));
			mBitmapTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}

	public synchronized void unloadQuestionTextures() {

		BuildableBitmapTextureAtlas mBitmapTextureAtlas = (BuildableBitmapTextureAtlas) mGameTextureRegionImage[0]
				.getTexture();
		mBitmapTextureAtlas.unload();
		System.gc();
	}
}