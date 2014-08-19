package com.example.imagequiz;

import java.util.Random;

public class QuestionData {

	private String mQuestionText;
	private String mQuestionImages[];
	private boolean mQuestionAnswers[];

	public int numberOfCorrectAnswers;

	public QuestionData(String mQuestionText, String[] mQuestionImages,
			boolean[] mQuestionAnswers) {

		this.mQuestionText = mQuestionText;
		this.mQuestionImages = mQuestionImages;
		this.mQuestionAnswers = mQuestionAnswers;
		numberOfCorrectAnswers = 0;

		for (int i = 0; i < 6; ++i) {
			if (mQuestionAnswers[i])
				++numberOfCorrectAnswers;
		}
	}
	
	public String getQuestionText() {
		return mQuestionText;
	}
	
	public String getQuestionImage(int index) {
		return mQuestionImages[index];
	}
	public String[] getQuestionImagesArray() {
		return mQuestionImages;
	}
	
	public boolean getQuestionAnswer(int index) {
		return mQuestionAnswers[index];
	}

	public boolean[] getQuestionAnswersArray() {
		return mQuestionAnswers;
	}
	
	public void randomShuffle() {

		Random rgen = new Random();
		int rPos;
		String tmpString;
		boolean tmpBoolean;
		for (int i=0; i<6; ++i) {
		    rPos = rgen.nextInt(6);
		    
		    tmpString = mQuestionImages[i];
		    mQuestionImages[i] = mQuestionImages[rPos];
		    mQuestionImages[rPos] = tmpString;
		    
		    tmpBoolean = mQuestionAnswers[i];
		    mQuestionAnswers[i] = mQuestionAnswers[rPos];
		    mQuestionAnswers[rPos] = tmpBoolean;
		}
	}
}
