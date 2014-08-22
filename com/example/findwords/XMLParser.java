package com.example.findwords;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLParser extends DefaultHandler {

	public XMLParser() {
		super();
		
		grids = new ArrayList<List<String>>();
	}

	private List<String> words;
	private List<List<String>> grids;

	private boolean bText = false;

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		//System.out.println("Start Element :" + qName);

		if (qName.equalsIgnoreCase("GRID")) {
			words = new ArrayList<String>();
		}

		if (qName.equalsIgnoreCase("WORD")) {
			bText = true;
		}
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		//System.out.println("End Element :" + qName);

		if (qName.equalsIgnoreCase("GRID")) {
			if (!words.isEmpty())
				grids.add(words);
		}
	}

	public void characters(char ch[], int start, int length)
			throws SAXException {

		//System.out.println(new String(ch, start, length));

		if (bText) {
//			System.out.println("Word : " + new String(ch, start, length));
			bText = false;
			if (length <= 11)
				words.add(new String(ch, start, length));
			else
				System.out.println("[XML] za dlugi wyraz : " + new String(ch, start, length)); 
		}
	}

	public List<List<String>> getResults() {
		return grids;
	}

}