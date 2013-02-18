package relations.helpers;

import gui.WordMap;

import java.awt.Color;
import java.io.BufferedReader;
import java.util.Hashtable;
import java.util.Set;

import relations.WordRelator;

public class WordRelationSingleWord extends WordRelator {
	
	private static final long serialVersionUID = 5050227339930267836L;
	public Hashtable<String,Double> values;
	public String target;

	public Set<String> getWords() {
		return values.keySet();
	}
	
	public WordRelationSingleWord(Color color, String name, WordMap wordMap, String target, BufferedReader r) {
		super(color, name, wordMap);
		this.target = target;
	}
	
	public WordRelationSingleWord(Color color, String name, WordMap wordMap, String target) {
		super(color, name, wordMap);
		this.target = target;
	}

	public double getDistance(String word1, String word2) {
		if(target.equals(word1)) {
			Double value = values.get(word2);
			if(value != null) {
				return value;
			} else {
				return 0;
			}
		} else if(target.equals(word2)) {
			Double value = values.get(word1);
			if(value != null) {
				return value;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

	public String toString() {
		return "Single Word {" + name + "}";
	}

}
