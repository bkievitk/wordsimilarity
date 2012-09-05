package relations.helpers;

import gui.WordMap;

import java.awt.Color;
import java.util.Hashtable;
import java.util.Set;

import relations.WordRelator;

public class WordRelaitonSingleWord extends WordRelator {
	
	private static final long serialVersionUID = 5050227339930267836L;
	public Hashtable<String,Integer> wordToInt;
	public double[] weights;
	public String target;

	public Set<String> getWords() {
		return wordToInt.keySet();
	}
	
	public WordRelaitonSingleWord(Color color, String name, WordMap wordMap, String target) {
		super(color, name, wordMap);
		this.target = target;
	}

	public double getDistance(String word1, String word2) {
		if(target.equals(word1)) {
			Integer id = wordToInt.get(word2);
			if(id != null) {
				return weights[id];
			} else {
				return 0;
			}
		} else if(target.equals(word2)) {
			Integer id = wordToInt.get(word1);
			if(id != null) {
				return weights[id];
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
