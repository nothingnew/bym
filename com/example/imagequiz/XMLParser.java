package com.example.imagequiz;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLParser extends DefaultHandler {

	public XMLParser() {
		super();
		
		questions = new ArrayList<QuestionData>();
	}

	private List<QuestionData> questions;
	private QuestionData tmp;
	private String text;
	private String images[];
	private boolean answers[];
	private int index;

	private boolean bText = false;
	private boolean bImage = false;

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		//System.out.println("Start Element :" + qName);

		if (qName.equalsIgnoreCase("QUESTION")) {
			images = new String[6];
			answers = new boolean[6];
			index = 0;
		}

		if (qName.equalsIgnoreCase("TEXT")) {
			bText = true;
		}

		if (qName.equalsIgnoreCase("IMAGE")) {
			bImage = true;

			if (attributes.getValue("answer").equalsIgnoreCase("true"))
				answers[index] = true;
			else
				answers[index] = false;

			//System.out.println("Attribute : " + attributes.getValue("answer"));
		}
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		//System.out.println("End Element :" + qName);

		if (qName.equalsIgnoreCase("QUESTION")) {
			tmp = new QuestionData(text, images, answers);
			questions.add(tmp);
		}
	}

	public void characters(char ch[], int start, int length)
			throws SAXException {

		//System.out.println(new String(ch, start, length));

		if (bText) {
//			System.out.println("Question Text : "
//					+ new String(ch, start, length));
			bText = false;

			text = new String(ch, start, length);
		}

		if (bImage) {
//			System.out.println("Image File : " + new String(ch, start, length));
			bImage = false;

			images[index++] = new String(ch, start, length);
		}
	}

	public List<QuestionData> getResults() {
		return questions;
	}

}