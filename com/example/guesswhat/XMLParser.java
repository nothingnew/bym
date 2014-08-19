package com.example.guesswhat;

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLParser extends DefaultHandler {

	public XMLParser() {
		super();
		
		questionsList = new ArrayList<QuestionBar>();
	}

	private List<QuestionBar> questionsList;
	private QuestionBar temp;
	private String text;
	private String answers[];
	private boolean results[];
	private int index;

	private boolean bText = false;
	private boolean bAnswer = false;

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		if (qName.equalsIgnoreCase("QUESTION")) {
			answers = new String[4];
			results = new boolean[4];
			index = 0;
		}

		if (qName.equalsIgnoreCase("TEXT")) {
			bText = true;
		}

		if (qName.equalsIgnoreCase("ANSWER")) {
			bAnswer = true;

			if (attributes.getValue("result").equalsIgnoreCase("true"))
				results[index] = true;
			else
				results[index] = false;
		}
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		if (qName.equalsIgnoreCase("QUESTION")) {
			temp = new QuestionBar(text, answers, results);
			questionsList.add(temp);
		}
	}

	public void characters(char ch[], int start, int length)
			throws SAXException {

		if (bText) {
			bText = false;
			text = new String(ch, start, length);
		}

		if (bAnswer) {
			bAnswer = false;
			answers[index++] = new String(ch, start, length);
		}
	}

	public List<QuestionBar> getResults() {
		return questionsList;
	}
}