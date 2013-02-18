package relations.helpers;

import gui.WordMap;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Set;

import relations.WordRelator;

public class WordRelatorTranslateKey extends WordRelator {

	private static final long serialVersionUID = 9223244615347273869L;
	public Hashtable<String,String> pairs = new Hashtable<String,String>();
	
	public WordRelatorTranslateKey(Color color, String name, WordMap wordMap, BufferedReader r) throws IOException {
		super(color, name, wordMap);
		
		String line;
		while((line = r.readLine()) != null) {
			String[] parts = line.split(",");
			pairs.put(parts[0], parts[1]);
			pairs.put(parts[1], parts[0]);
		}
	}

	@Override
	public double getDistance(String word1, String word2) {
		if(word2.equals(pairs.get(word1)) || word1.equals(pairs.get(word2))) {
			return 1;
		}
		return 0;
	}

	@Override
	public String toString() {
		return "Translate Key {" + name + "}";
	}
	
	public Set<String> getWords() {
		return pairs.keySet();
	}

}
