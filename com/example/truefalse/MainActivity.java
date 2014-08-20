package com.example.truefalse;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.primitive.DrawMode;
import org.andengine.entity.primitive.Mesh;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

import android.graphics.Typeface;


public class MainActivity extends BaseGameActivity {

	private static final int WIDTH = 768;
	private static final int HEIGHT = 1024;
	
	private static final String QUESTION = "Oceñ, czy zdania s¹ prawdziwe, czy fa³szywe:";
	private static final String[] TEXT = new String[10];
	private static final String PRAWDA = "PRAWDA";
	private static final String FA£SZ = "FA£SZ";
	
	private Scene mScene;
	private Camera mCamera;
	
	private Rectangle[] line = new Rectangle[10];
	private Rectangle[] trueButton = new Rectangle[10];
	private Rectangle[] falseButton = new Rectangle[10];
	private Mesh[] frameLine = new Mesh[10];
	private Mesh[] frameTrue = new Mesh[10];
	private Mesh[] frameFalse = new Mesh[10];
	private Font qFont, lFont, aFont;
	private Text qText;
	private Text[] lText = new Text[10];
	private Text[] tText = new Text[10];
	private Text[] fText = new Text[10];
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		// Define our mCamera object
		mCamera = new Camera(0, 0, WIDTH, HEIGHT);
		
		// Declare & Define our engine options to be applies to our Engine object
		EngineOptions engineOptions = new EngineOptions(true, 
			ScreenOrientation.PORTRAIT_SENSOR, new FillResolutionPolicy(), mCamera);
		
		// It is necessary in a lot of applications to define the following
		// wake lock options in order to disable the device's display
		// from turning off during gameplay due to inactivity
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		
		// Return the engineOptions object, passing it to the engine
		return engineOptions;
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) {
		
		// Load our fonts
		qFont = FontFactory.create(mEngine.getFontManager(), mEngine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD),  32f, true, Color.WHITE_ARGB_PACKED_INT);
		qFont.load();
		
		lFont = FontFactory.create(mEngine.getFontManager(), mEngine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL),  24f, true, Color.WHITE_ARGB_PACKED_INT);
		lFont.load();
		
		aFont = FontFactory.create(mEngine.getFontManager(), mEngine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD),  20f, true, Color.WHITE_ARGB_PACKED_INT);
		aFont.load();
				
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		// Create the Scene object
		mScene = new Scene();
		
		// Notify the callback that we're finished creating the scene, returning
		// mScene to the mEngine object (handled automatically)
		pOnCreateSceneCallback.onCreateSceneFinished(mScene);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) {
		
		// ---------------------
		// 		RECTANGLES
		// ---------------------
		
		// Question block, line and true/false button rectangles
		Rectangle question = new Rectangle(384, 937, 768, 174, mEngine.getVertexBufferObjectManager());
		question.setColor(180f/255f, 240f/255f, 240f/255f);
		mScene.attachChild(question);
		
		for (int i=0; i<10; i++)
		{
			line[i] = new Rectangle(256, 807.5f-i*85f, 512, 85, mEngine.getVertexBufferObjectManager());
			line[i].setColor(Color.WHITE);
			mScene.attachChild(line[i]);
		}
		
		for (int i=0; i<10; i++)
		{
			trueButton[i] = new Rectangle(576, 807.5f-i*85f, 128, 85, mEngine.getVertexBufferObjectManager());
			trueButton[i].setColor(255f/255f, 240f/255f, 150f/255f);
			mScene.attachChild(trueButton[i]);
		}
		
		for (int i=0; i<10; i++)
		{
			falseButton[i] = new Rectangle(704, 807.5f-i*85f, 128, 85, mEngine.getVertexBufferObjectManager());
			falseButton[i].setColor(255f/255f, 240f/255f, 150f/255f);
			mScene.attachChild(falseButton[i]);
		}
		
		// ----------------------------------------------------
		// 		EXAMPLES OF CORRECT AND INCORRECT ANSWERS
		// ----------------------------------------------------
		
		Rectangle correctAnswer1 = new Rectangle(384, 42.5f, 768, 85, mEngine.getVertexBufferObjectManager());
		correctAnswer1.setColor(130f/255f, 235f/255f, 95f/255f);
		falseButton[1].attachChild(correctAnswer1);
		
		Rectangle correctAnswer11 = new Rectangle(384, 42.5f, 768, 85, mEngine.getVertexBufferObjectManager());
		correctAnswer11.setColor(130f/255f, 235f/255f, 95f/255f);
		trueButton[1].attachChild(correctAnswer11);
		
		Rectangle correctAnswer111 = new Rectangle(384, 42.5f, 768, 85, mEngine.getVertexBufferObjectManager());
		correctAnswer111.setColor(130f/255f, 235f/255f, 95f/255f);
		line[1].attachChild(correctAnswer111);
		
		Rectangle correctAnswer2 = new Rectangle(384, 42.5f, 768, 85, mEngine.getVertexBufferObjectManager());
		correctAnswer2.setColor(130f/255f, 235f/255f, 95f/255f);
		falseButton[2].attachChild(correctAnswer2);
		
		Rectangle correctAnswer22 = new Rectangle(384, 42.5f, 768, 85, mEngine.getVertexBufferObjectManager());
		correctAnswer22.setColor(130f/255f, 235f/255f, 95f/255f);
		trueButton[2].attachChild(correctAnswer22);
		
		Rectangle correctAnswer222 = new Rectangle(384, 42.5f, 768, 85, mEngine.getVertexBufferObjectManager());
		correctAnswer222.setColor(130f/255f, 235f/255f, 95f/255f);
		line[2].attachChild(correctAnswer222);
		
		Rectangle correctAnswer3 = new Rectangle(384, 42.5f, 768, 85, mEngine.getVertexBufferObjectManager());
		correctAnswer3.setColor(130f/255f, 235f/255f, 95f/255f);
		falseButton[4].attachChild(correctAnswer3);
		
		Rectangle correctAnswer33 = new Rectangle(384, 42.5f, 768, 85, mEngine.getVertexBufferObjectManager());
		correctAnswer33.setColor(130f/255f, 235f/255f, 95f/255f);
		trueButton[4].attachChild(correctAnswer33);
		
		Rectangle correctAnswer333 = new Rectangle(384, 42.5f, 768, 85, mEngine.getVertexBufferObjectManager());
		correctAnswer333.setColor(130f/255f, 235f/255f, 95f/255f);
		line[4].attachChild(correctAnswer333);
		
		Rectangle incorrectAnswer1 = new Rectangle(384, 42.5f, 768, 85, mEngine.getVertexBufferObjectManager());
		incorrectAnswer1.setColor(235f/255f, 100f/255f, 90f/255f);
		falseButton[0].attachChild(incorrectAnswer1);
		
		Rectangle incorrectAnswer11 = new Rectangle(384, 42.5f, 768, 85, mEngine.getVertexBufferObjectManager());
		incorrectAnswer11.setColor(235f/255f, 100f/255f, 90f/255f);
		trueButton[0].attachChild(incorrectAnswer11);
		
		Rectangle incorrectAnswer111 = new Rectangle(384, 42.5f, 768, 85, mEngine.getVertexBufferObjectManager());
		incorrectAnswer111.setColor(235f/255f, 100f/255f, 90f/255f);
		line[0].attachChild(incorrectAnswer111);
		
		Rectangle incorrectAnswer3 = new Rectangle(384, 42.5f, 768, 85, mEngine.getVertexBufferObjectManager());
		incorrectAnswer3.setColor(235f/255f, 100f/255f, 90f/255f);
		falseButton[3].attachChild(incorrectAnswer3);
		
		Rectangle incorrectAnswer33 = new Rectangle(384, 42.5f, 768, 85, mEngine.getVertexBufferObjectManager());
		incorrectAnswer33.setColor(235f/255f, 100f/255f, 90f/255f);
		trueButton[3].attachChild(incorrectAnswer33);
		
		Rectangle incorrectAnswer333 = new Rectangle(384, 42.5f, 768, 85, mEngine.getVertexBufferObjectManager());
		incorrectAnswer333.setColor(235f/255f, 100f/255f, 90f/255f);
		line[3].attachChild(incorrectAnswer333);
		
		// -----------------
		// 		FRAMES
		// -----------------
		
		// Frame buffer for lines
		float frameLineBuffer[] = {
				0, 85, 0, // point one
				512, 85, 0, // point two
				512, 0, 0, // point three
				0, 0, 0, // point four
		};
		
		// Frame buffer for true/false buttons
		float frameBuffer[] = {
				0, 85, 0, // point one
				128, 85, 0, // point two
				128, 0, 0, // point three
				0, 0, 0, // point four
		};

		// Line frames
		for (int i=0; i<10; i++)
		{
			frameLine[i] = new Mesh(0, 0, frameLineBuffer, 4, DrawMode.LINE_LOOP, mEngine.getVertexBufferObjectManager());
			frameLine[i].setColor(Color.BLACK);
			line[i].attachChild(frameLine[i]);
		}

		// True-buttons frames
		for (int i=0; i<10; i++)
		{
			frameTrue[i] = new Mesh(0, 0, frameBuffer, 4, DrawMode.LINE_LOOP, mEngine.getVertexBufferObjectManager());
			frameTrue[i].setColor(Color.BLACK);
			trueButton[i].attachChild(frameTrue[i]);
		}
		
		// False-buttons frames
		for (int i=0; i<10; i++)
		{
			frameFalse[i] = new Mesh(0, 0, frameBuffer, 4, DrawMode.LINE_LOOP, mEngine.getVertexBufferObjectManager());
			frameFalse[i].setColor(Color.BLACK);
			falseButton[i].attachChild(frameFalse[i]);
		}
		
		// -----------------
		// 		TEXT
		// -----------------
		
		// Create TextOptions for our text
		final TextOptions textOptions = new TextOptions();
		textOptions.setHorizontalAlign(HorizontalAlign.CENTER);
		
		final TextOptions textOptions2 = new TextOptions();
		textOptions2.setHorizontalAlign(HorizontalAlign.LEFT);
		
		// Create our question text
		qText = new Text(384, 87, qFont, QUESTION, QUESTION.length(), textOptions, mEngine.getVertexBufferObjectManager());
		qText.setColor(15f/255f, 30f/255f, 45f/255f);
		question.attachChild(qText);
		
		// Create our line texts
		TEXT[0] = "Cytryna jest niebieska.";
		TEXT[1] = "Stolic¹ Polski jest Warszawa.";
		TEXT[2] = "Gerard to imiê kobiece.";
		TEXT[3] = "Rok ma 14 miesiêcy.";
		TEXT[4] = "Godzina ma 60 minut.";
		TEXT[5] = "Gitara to nazwa warzywa.";
		TEXT[6] = "Pstr¹g to ryba.";
		TEXT[7] = "Wiêkszoœæ ludzi mieszka w stodo³ach.";
		TEXT[8] = "Ananas to owoc.";
		TEXT[9] = "Fryzjer robi buty.";
		
		for (int i=0; i<10; i++)
		{
			lText[i] = new Text(256, 42.5f, lFont, TEXT[i], TEXT[i].length(), textOptions2, mEngine.getVertexBufferObjectManager());
			lText[i].setColor(Color.BLACK);
			line[i].attachChild(lText[i]);
		}
		
		// True-button text
		for (int i=0; i<10; i++)
		{
			tText[i] = new Text(64, 42.5f, aFont, PRAWDA, PRAWDA.length(), textOptions, mEngine.getVertexBufferObjectManager());
			tText[i].setColor(Color.BLACK);
			trueButton[i].attachChild(tText[i]);
		}
		
		// False-button text
		for (int i=0; i<10; i++)
		{
			fText[i] = new Text(64, 42.5f, aFont, FA£SZ, FA£SZ.length(), textOptions, mEngine.getVertexBufferObjectManager());
			fText[i].setColor(Color.BLACK);
			falseButton[i].attachChild(fText[i]);
		}
		
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
}