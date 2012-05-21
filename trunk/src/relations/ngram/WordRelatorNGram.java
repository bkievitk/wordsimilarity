package relations.ngram;

import gui.WordMap;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.awt.Color;
import java.io.*;
import relations.WordRelator;

public class WordRelatorNGram extends WordRelator {
	
	private static final long serialVersionUID = 6989386384336426841L;
	
	// The relative position of the word to compare to.
	public int position;
	public Hashtable<String,NGramWord> nGramData = new Hashtable<String,NGramWord>();	
	
	@SuppressWarnings("rawtypes")
	public final static Class wizardPanel = WizardNGramOptions.class;
	public final static String description = "This will compute the probability of any given word following the target word by looking at text nGrams.";
	public final static String typeName = "NGram";
	
	public WordRelatorNGram(Color color, String name, int position, WordMap wordMap) {
		super(color, name, wordMap);
		this.position = position;
	}

	public void learn(String[] sentence) {	
		super.learn(sentence);	
		
		for(int i=0;i<sentence.length-position;i++) {
			NGramWord word = nGramData.get(sentence[i]);
			if(word == null) {
				word = new NGramWord(sentence[i]);
				nGramData.put(sentence[i], word);
			}
			word.count++;
			Long val = word.data.remove(sentence[i + position]);
			if(val == null) {
				val = (long)0;
			}
			val += 1;
			word.data.put(sentence[i + position], val);
		}
		
		/*
		if(sentence.length > position) {
			NGramWord word = nGramData.get(sentence[0]);
			if(word == null) {
				word = new NGramWord(sentence[0]);
				nGramData.put(sentence[0], word);
			}
			word.count++;
			Long val = word.data.remove(sentence[position]);
			if(val == null) {
				val = (long)0;
			}
			val += 1;
			word.data.put(sentence[position], val);
		}
		*/
	}

	public double getDistance(String word1, String word2) {	
		NGramWord word = nGramData.get(word1);
		if(word == null){
			return 0;
		}
		
		Long part = word.data.get(word2);	
		if(part == null) {
			return 0;
		} else {
			return part / (double)word.count;
		}
	}
	
	public Set<String> getWords() {
		HashSet<String> words = new HashSet<String>();
		for(String word1 : nGramData.keySet()) {
			words.add(word1);
			for(String word2 : nGramData.get(word1).data.keySet()) {
				words.add(word2);
			}
		}
		return words;
	}
	
	public String toString() {
		return "NGram {" + name + "}";
	}
	
}

class NGramWord implements Serializable {
	
	private static final long serialVersionUID = 2340471232020561731L;
	
	public String word;
	public long count = 0;
	public Hashtable<String,Long> data = new Hashtable<String,Long>();
	
	public NGramWord(String word) {
		this.word = word;
	}
	
	public boolean equals(NGramWord o) {
		return word.equals(o.word);
	}
	
	public int hashCode() {
		return word.hashCode();
	}	
}