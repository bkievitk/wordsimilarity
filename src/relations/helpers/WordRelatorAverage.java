package relations.helpers;

import gui.WordMap;

import java.awt.Color;

import relations.WordRelator;

public class WordRelatorAverage extends WordRelator {

	private static final long serialVersionUID = -4190443865957432301L;
	public WordRelator relator1;
	public WordRelator relator2;

	@SuppressWarnings("rawtypes")
	public final static Class wizardPanel = WizardAverage.class;
	public final static String description = "This will create a connection that is the average of two word relators already existing.";
	public final static String typeName = "Average";
	
	public WordRelatorAverage(Color color, String name, WordMap wordMap) {
		super(color, name, wordMap);
	}

	public double getDistance(String word1, String word2) {
		return (relator1.getDistance(word1, word2) + relator2.getDistance(word1, word2)) / 2;
	}

	public String toString() {
		return "Average of {" + relator1.toString() + " and " + relator2.toString() + "}";
	}

}
