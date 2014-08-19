package com.example.guesswhat;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

import com.example.guesswhat.ResourceManager;

public class AnswerButton extends AnimatedSprite {

	private Text answerSpriteText;
	private boolean touched;
	private boolean correct;
	private IAnimationListener animationListener;
	
	private final long frameDuration[] = {1000, 300, 300, 300, 300, 1000};
	private final int frameIncorrectNumber[] = {3, 2, 3, 2, 0, 0};
	private final int frameCorrectNumber[] = {3, 1, 3, 1, 3, 1};

	public AnswerButton(float pX, float pY, ITiledTextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObject, IAnimationListener pAnimationListener,
			String texts) {

		super(pX, pY, pTextureRegion, pVertexBufferObject);

		touched = false;
		correct = false;
		animationListener = pAnimationListener;

		answerSpriteText = new Text(getWidth() / 2, getHeight() / 2, ResourceManager.getInstance().aFont, 
				ResourceManager.getInstance().getNormalizedText(texts), 300, getVertexBufferObjectManager());
		answerSpriteText.setHorizontalAlign(HorizontalAlign.CENTER);
		answerSpriteText.setColor(Color.BLACK);

		attachChild(answerSpriteText);
	}

	public boolean Reset() {
		
		if(isAnimationRunning())
			stopAnimation();
		
		setCurrentTileIndex(0);
		touched = false;

		return true;
	}

	public boolean isTouched() {
		return touched;
	}

	public boolean isCorrect() {
		return correct;
	}

	public void setCorrect(boolean x) {
		correct = x;
	}

	public void setText(VertexBufferObjectManager pVertexBufferObject, String texts) {
		
		detachChild(answerSpriteText);
		
		answerSpriteText = new Text(getWidth() / 2, getHeight() / 2, ResourceManager.getInstance().aFont, 
				ResourceManager.getInstance().getNormalizedText(texts), 300, getVertexBufferObjectManager());
		answerSpriteText.setHorizontalAlign(HorizontalAlign.CENTER);
		answerSpriteText.setColor(Color.BLACK);

		attachChild(answerSpriteText);
	}

	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
			final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		
		if (touched)
			return true;
		
		// If buttonSprite is touched with the finger
		int eventAction = pSceneTouchEvent.getAction();

		switch (eventAction) {
		case TouchEvent.ACTION_DOWN: {
			if (correct) {
				animate(frameDuration, frameCorrectNumber, false, animationListener);
				touched = true;
			} else {
				animate(frameDuration, frameIncorrectNumber, false);
			}
			break;
		}
		default:
			return true;
		}
		
		return true;
	}
}
