package com.example.imagequiz;

//import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class AnswerSprite extends AnimatedSprite {

	private Sprite imageSprite;
	private IAnimationListener correctAnimationListener,
			wrongAnimationListener;
	private boolean checked;
	private boolean correct;
	// animation
	private final long wrongFrameDuration[] = { 200, 200 };
	private final int wrongFrameNumers[] = { 2, 0 };
	private final int wrongLoopCount = 3;
	private final long correctFrameDuration[] = { 200, 200, 1100 };
	private final int correctFrameNumers[] = { 1, 0, 1 };

	public AnswerSprite(float pX, float pY, ITextureRegion pTextureRegion,
			IAnimationListener pCorrectAnimationListener,
			IAnimationListener pWrongAnimationListener,
			VertexBufferObjectManager pVertexBufferObject) {

		super(pX, pY,
				ResourceManager.getInstance().mGameTextureRegionBackground,
				pVertexBufferObject);

		checked = false;
		correct = false;
		correctAnimationListener = pCorrectAnimationListener;
		wrongAnimationListener = pWrongAnimationListener;

		imageSprite = new Sprite(getWidth() / 2, getHeight() / 2,
				pTextureRegion, pVertexBufferObject);

		attachChild(imageSprite);
	}

	public boolean Reset() {

		if (isAnimationRunning())
			stopAnimation();

		setCurrentTileIndex(0);
		checked = false;

		return true;
	}

	public boolean isChecked() {
		return checked;
	}

	public boolean isCorrect() {
		return correct;
	}

	public void setCorrect(boolean c) {
		correct = c;
	}

	public void setImage(ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObject) {

		detachChild(imageSprite);
		imageSprite = new Sprite(getWidth() / 2, getHeight() / 2,
				pTextureRegion, pVertexBufferObject);

		attachChild(imageSprite);
	}

	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
			final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

		if (checked)
			return true;

		int eventAction = pSceneTouchEvent.getAction();

		switch (eventAction) {
		case TouchEvent.ACTION_DOWN: {
			if (correct) {
				checked = true;
				// private final long correctFrameDuration[] = {300, 300, 1400};
				// private final int correctFrameNumers[] = {1, 0, 1};
				animate(correctFrameDuration, correctFrameNumers, false,
						correctAnimationListener);
			} else {
				// private final long frameDuration[] = {300, 300};
				// private final int frameNumers[] = {2, 0};
				// private final int loopCount = 3;
				animate(wrongFrameDuration, wrongFrameNumers, wrongLoopCount,
						wrongAnimationListener);
			}
			break;
		}
		default:
			return true;
		}

		return true;
	}
}
