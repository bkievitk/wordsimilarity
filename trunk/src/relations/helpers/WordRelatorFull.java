package relations.helpers;
import gui.WordMap;

import java.awt.Color;
import java.util.HashSet;
import relations.WordRelator;


public class WordRelatorFull extends WordRelator {

	private static final long serialVersionUID = -7282161178181704194L;
	
	public double value = 1.0;
	public HashSet<String> words = null;

	@SuppressWarnings("rawtypes")
	public final static Class wizardPanel = WizardFull.class;
	public final static String description = "This will create a connection of equal weight between every word specified. This is mostly for debugging purposes but can be used to emphasize relations between words manually.";
	public final static String typeName = "Full";

	public WordRelatorFull(Color color, String name, WordMap wordMap) {
		super(color, name, wordMap);
	}
	
	public double getDistance(String word1, String word2) {	
		if(words == null || (words.contains(word1) && words.contains(word2))) {
			return value;
		} else {
			return 0;
		}
	}
	
	public String toString() {
		return "Complete Connection {" + name + "]";
	}

	public void learn(String[] sentence) {		
		super.learn(sentence);
	}


}
