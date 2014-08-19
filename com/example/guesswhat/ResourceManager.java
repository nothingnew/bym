package com.example.guesswhat;

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
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;
import org.xml.sax.InputSource;

import com.example.guesswhat.QuestionBar;
import com.example.guesswhat.XMLParser;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

public class ResourceManager {

	// ResourceManager Singleton instance
	private static ResourceManager INSTANCE;
	
	// Declaration of resources
	public ITextureRegion questionRegion, finishRegion;
	public ITiledTextureRegion answerRegion;
	
	public Sound buttonClick, success;

	public Font qFont, aFont;
	
	public Text[] answerText;
	
	public String successMessage, failMessage;
	
	public List<QuestionBar> questionsList;

	// Constructor
	ResourceManager(){
		
	}

	public synchronized static ResourceManager getInstance(){
		if(INSTANCE == null){
			INSTANCE = new ResourceManager();
		}
		return INSTANCE;
	}

	// ---------------------
	// 		 TEXTURES
	// ---------------------
	
	public synchronized void loadGameTextures(Engine pEngine, Context pContext){
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		// Bitmap texture atlases
		BuildableBitmapTextureAtlas mBitmapTextureAtlas1 = new BuildableBitmapTextureAtlas(
				pEngine.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		BuildableBitmapTextureAtlas mBitmapTextureAtlas2 = new BuildableBitmapTextureAtlas(
				pEngine.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);

		// Create the question bar and answer button texture regions
		questionRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mBitmapTextureAtlas1, pContext, "question_bar.png");
		answerRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(mBitmapTextureAtlas2, pContext, "answer_button.png", 1, 4);
		finishRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mBitmapTextureAtlas1, pContext, "finish.png");
		
		// Build the bitmap texture atlases
		try {
			mBitmapTextureAtlas1.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, 
							BitmapTextureAtlas>(0, 0, 0));
			mBitmapTextureAtlas2.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, 
							BitmapTextureAtlas>(0, 0, 0));
			mBitmapTextureAtlas1.load();
			mBitmapTextureAtlas2.load();
		} catch (TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}
	
	// Unloading textures
	public synchronized void unloadGameTextures(){
		BuildableBitmapTextureAtlas mBitmapTextureAtlas1 = (BuildableBitmapTextureAtlas) questionRegion.getTexture();
		mBitmapTextureAtlas1.unload();
		
		BuildableBitmapTextureAtlas mBitmapTextureAtlas2 = (BuildableBitmapTextureAtlas) answerRegion.getTexture();
		mBitmapTextureAtlas2.unload();

		// Once all textures have been unloaded, attempt to invoke the Garbage Collector
		System.gc();
	}
	
	// ---------------------
	// 		  SOUNDS
	// ---------------------
	
	public synchronized void loadSounds(Engine pEngine, Context pContext){
		SoundFactory.setAssetBasePath("sfx/");
		 try {
			 // Create sound object via SoundFactory class
			 buttonClick = SoundFactory.createSoundFromAsset(pEngine.getSoundManager(), pContext, "button_click.mp3");
			 success = SoundFactory.createSoundFromAsset(pEngine.getSoundManager(), pContext, "success_sound.mp3");
		 } catch (final IOException e) {
             Log.v("Sounds Load","Exception:" + e.getMessage());
		 }
	}	
	
	// Unloading sounds
	public synchronized void unloadSounds(){
		// we call the release() method on sounds to remove them from memory
		if(!buttonClick.isReleased()) 
				buttonClick.release();
		
		if(!success.isReleased()) 
			success.release();
	}
	
	// ---------------------
	// 		  FONTS
	// ---------------------
	
	public synchronized void loadFonts(Engine pEngine){
		// Creating our fonts
		qFont = FontFactory.create(pEngine.getFontManager(), 
				pEngine.getTextureManager(), 256, 256, 
				Typeface.create(Typeface.DEFAULT, Typeface.BOLD),  60f, true, Color.WHITE_ARGB_PACKED_INT);
		qFont.load();
		
		aFont = FontFactory.create(pEngine.getFontManager(), 
				pEngine.getTextureManager(), 256, 256, 
				Typeface.create(Typeface.DEFAULT, Typeface.NORMAL),  36f, true, Color.WHITE_ARGB_PACKED_INT);
		aFont.load();
	}
	
	// Unloading fonts
	public synchronized void unloadFonts(){
		qFont.unload();
		aFont.unload();
	}
	
	// ---------------------
	// 		   TEXT
	// ---------------------
	
	public synchronized void loadText() {

		answerText = new Text[4];
		
		successMessage = "BRAWO!\n>>> Dotknij, aby graæ dalej. <<<";
	}
	
	// ---------------------
	// 		 QUESTIONS
	// ---------------------

	public synchronized void loadQuestions(Context pContext) {
		
		try {
			SAXParserFactory saxFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxFactory.newSAXParser();

			XMLParser handler = new XMLParser();

			InputStream inputStream = pContext.getAssets().open("xml/questions.xml");
			Reader reader = new InputStreamReader(inputStream, "UTF-8");

			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");

			saxParser.parse(is, handler);

			questionsList = handler.getResults();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized QuestionBar nextQuestion() {

		Random rand = new Random();
		QuestionBar data = questionsList.get(rand.nextInt(questionsList.size()));

		return data;
	}

	public synchronized void loadQuestionTexts(float pX, float pY, String[] files,
			Engine pEngine, Context pContext) {

		for (int i=0; i<4; ++i) {			
			answerText[i] = new Text(pX, pY, aFont, getNormalizedText(files[i]), 300, pEngine.getVertexBufferObjectManager());
			answerText[i].setHorizontalAlign(HorizontalAlign.CENTER);
			answerText[i].setColor(Color.BLACK);
		}
	}
	
	public synchronized String getNormalizedText(String text) {
		
		char s[] = new char[text.length()];
		
		if (text.length() > 15) {
			for (int i = 15; i < text.length(); ++i) {
				if (text.charAt(i) == ' ') {
					s = text.toCharArray();
					s[i] = '\n';
					text = new String(s);
					break;
				}
				else if (i == text.length()) {
					break;
				}
				else
					continue;
			}
		}
		else
			return text;
		
		return text;
	}
}