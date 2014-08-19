package com.example.guesswhat;

import java.util.Random;

public class QuestionBar {

	private String questionText;
	private String[] questionAnswers;
	private boolean[] questionResults;

	public QuestionBar(String questionText, String[] questionAnswers,
			boolean[] questionResults) {

		this.questionText = questionText;
		this.questionAnswers = questionAnswers;
		this.questionResults = questionResults;
	}
	
	public String getQuestionText() {
		return questionText;
	}
	
	public String getQuestionAnswer(int index) {
		return questionAnswers[index];
	}
	
	public String[] getQuestionAnswersArray() {
		return questionAnswers;
	}
	
	public boolean getQuestionResult(int index) {
		return questionResults[index];
	}

	public boolean[] getQuestionResultsArray() {
		return questionResults;
	}
	
	public void randomLocation() {

		Random rand = new Random();
		int position;
		String stringTemp;
		boolean boolTemp;
		
		for (int i=0; i<4; ++i) {
		    position = rand.nextInt(4);
		    
		    stringTemp = questionAnswers[i];
		    questionAnswers[i] = questionAnswers[position];
		    questionAnswers[position] = stringTemp;
		    
		    boolTemp = questionResults[i];
		    questionResults[i] = questionResults[position];
		    questionResults[position] = boolTemp;
		}
	}
}
