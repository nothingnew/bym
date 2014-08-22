package com.example.findwords;

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
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;
import org.xml.sax.InputSource;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

public class ResourceManager {

	private static ResourceManager INSTANCE;

	public ITiledTextureRegion mGameTextureRegionBackground;
	public ITextureRegion mGameTextureRegionComplete;
	public Sound mSound;
	public Font mFont;
	public List<List<String>> mGrids;

	ResourceManager() {
		// The constructor is of no use to us
	}

	public synchronized static ResourceManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ResourceManager();
		}
		return INSTANCE;
	}

	public synchronized void loadSounds(Engine pEngine, Context pContext) {
		
		SoundFactory.setAssetBasePath("sfx/findwords/");
		try {
			mSound = SoundFactory.createSoundFromAsset(
					pEngine.getSoundManager(), pContext, "cell_phone_nr0.mp3");
		} catch (final IOException e) {
			Log.v("Sounds Load", "Exception:" + e.getMessage());
		}
	}

	public synchronized void unloadSounds() {

		if (!mSound.isReleased())
			mSound.release();
	}

	public synchronized void loadFonts(Engine pEngine) {
		FontFactory.setAssetBasePath("fonts/");

		mFont = FontFactory.create(pEngine.getFontManager(),
				pEngine.getTextureManager(), 300, 300,
				Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 48f, true,
				Color.BLACK_ARGB_PACKED_INT);

		mFont.load();
	}

	public synchronized void unloadFonts() {

		mFont.unload();
	}

	public synchronized void loadWords(Context pContext) {
		
		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			XMLParser handler = new XMLParser();

			InputStream inputStream = pContext.getAssets().open(
					"xml/findwords/words.xml");
			Reader reader = new InputStreamReader(inputStream, "UTF-8");

			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");

			saxParser.parse(is, handler);

			// result
			mGrids = handler.getResults();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// DEBUG
		Log.v("mGrids", "mGrids size = " + String.valueOf(mGrids.size()));
		for (List<String> mCurrentWordsList : mGrids)
		{
			Log.v("mGrids mCurrentWordsList", "mCurrentWordsList size = " + String.valueOf(mCurrentWordsList.size()));
			for (int i = 0; i < mCurrentWordsList.size(); ++i)
				Log.v("mGrids mCurrentWordsList", mCurrentWordsList.get(i));
		}
		//////////////////
	}

	public synchronized void loadGameTextures(Engine pEngine, Context pContext) {

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/findwords/");

		
		BuildableBitmapTextureAtlas mBitmapTextureAtlas1 = new BuildableBitmapTextureAtlas(
				pEngine.getTextureManager(), 300, 100);
		BuildableBitmapTextureAtlas mBitmapTextureAtlas2 = new BuildableBitmapTextureAtlas(
				pEngine.getTextureManager(), 800, 300);
		
		mGameTextureRegionBackground = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(mBitmapTextureAtlas1, pContext,
						"background.png", 3, 1);
		mGameTextureRegionComplete = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mBitmapTextureAtlas2, pContext, "complete.png");

		try {
			mBitmapTextureAtlas1
			.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
					0, 0, 0));
			mBitmapTextureAtlas2
			.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
					0, 1, 1));
			mBitmapTextureAtlas1.load();
			mBitmapTextureAtlas2.load();
		} catch (TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}
	
	public synchronized void unloadGameTextures() {

		BuildableBitmapTextureAtlas mBitmapTextureAtlas1 = (BuildableBitmapTextureAtlas) mGameTextureRegionBackground
				.getTexture();
		mBitmapTextureAtlas1.unload();
		BuildableBitmapTextureAtlas mBitmapTextureAtlas2 = (BuildableBitmapTextureAtlas) mGameTextureRegionComplete
				.getTexture();
		mBitmapTextureAtlas2.unload();

		System.gc();
	}
	
	public synchronized List<String> getNewWords() {
		
		Random rgen = new Random();
		return mGrids.get(rgen.nextInt(mGrids.size()));
	}
}