package com.example.findwords;

import java.util.Comparator;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.IFont;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class LetterButtonSprite extends AnimatedSprite {

	private Text letterText;
	private CharSequence letterString;
	private boolean locked;

	public LetterButtonSprite(float pX, float pY,
			ITiledTextureRegion pTiledTextureRegion, IFont pFont,
			CharSequence pLetter,
			VertexBufferObjectManager pVertexBufferObjectManager) {

		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);

		letterString = pLetter;

		letterText = new Text(getWidth() / 2, getHeight() / 2, pFont, pLetter,
				pVertexBufferObjectManager);
		attachChild(letterText);

		locked = false;

		// letterText.setIgnoreUpdate(true);
		// setIgnoreUpdate(true);
	}

	public boolean Reset() {

		setCurrentTileIndex(0);
		locked = false;

		return true;
	}

	public void resetBackground() {

		if (locked)
			setCurrentTileIndex(2);
		else
			setCurrentTileIndex(0);
	}

	public CharSequence getLetter() {
		return letterString;
	}

	public void setLetter(CharSequence newLetter) {
		letterString = newLetter;
		letterText.setText(newLetter);
	}

	public void lockIn() {

		if (locked)
			return;

		locked = true;
		setCurrentTileIndex(2);
	}

	public boolean isLocked() {
		return locked;
	}
}

// comparator class used to sort list
class LetterButtonComparator implements Comparator<LetterButtonSprite> {

	@Override
	public int compare(LetterButtonSprite arg0, LetterButtonSprite arg1) {
		if (arg0.getX() == arg1.getX())
			return arg0.getY() < arg1.getY() ? 1 : -1;
		else
			return arg0.getX() < arg1.getX() ? -1 : 1;
	}
}