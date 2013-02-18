package relations.helpers;

import gui.WordMap;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

import relations.WordRelator;

public class WordRelationCrystalized extends WordRelator {

	private static final long serialVersionUID = -5880669782756296255L;
	
	@SuppressWarnings("rawtypes")
	public final static Class wizardPanel = WizardCrystalized.class;
	public final static String description = "This will pre-compute all of the values for an existing relation over a set of words.";
	public final static String typeName = "Crystalized";
		
	public Hashtable<String,Integer> wordToInt;
	public double[][] weights;
		
	public Set<String> getWords() {
		return wordToInt.keySet();
	}
	
	public WordRelationCrystalized(Color color, String name, WordMap wordMap, Hashtable<String,Integer> wordToInt, double[][] weights) {
		super(color, name, wordMap);
		this.wordToInt = wordToInt;
		this.weights = weights;
	}
	
	public WordRelationCrystalized(Color color, String name, WordMap wordMap) {
		super(color, name, wordMap);
	}


	public WordRelationCrystalized(Color color, WordMap wordMap, BufferedReader input) {
		super(color,readName(input),wordMap);
		init(color,wordMap,input,"", "");
	}
	
	public WordRelationCrystalized(Color color, WordMap wordMap, BufferedReader input, String name) {
		super(color,name,wordMap);
		init(color,wordMap,input,"", "");
	}
	
	public WordRelationCrystalized(Color color, WordMap wordMap, BufferedReader input, String tagStart, String tagStop) {
		super(color,readName(input),wordMap);
		init(color,wordMap,input,tagStart, tagStop);
	}
	
	private void init(Color color, WordMap wordMap, BufferedReader input, String tagStart, String tagStop) {
		
		if(name == WordRelator.ERROR_INITIALIZING) {
			return;
		}

		try {
			
			String[] words = cleanCSVLine(input.readLine());
			
			wordToInt = new Hashtable<String,Integer>();
			
			for(int i=1;i<words.length;i++) {
				wordToInt.put(tagStart + words[i] + tagStop,wordToInt.size());
			}
			weights = new double[words.length-1][words.length-1];
			
			String line;
			int i = 0;
			while((line = input.readLine()) != null) {
				if(line.length() == 0) {
					break;
				}
				
				String[] elements = cleanCSVLine(line);
				for(int j=1;j<elements.length;j++) {
					weights[i][j-1] = Double.parseDouble(elements[j]);
				}
				i++;
			}
			
		} catch(IOException e) {
			super.name = WordRelator.ERROR_INITIALIZING;
			e.printStackTrace();
		}
	}
	
	public static String readName(BufferedReader input) {
		try {
			String line = input.readLine();
			if(line == null) {
				return WordRelator.ERROR_INITIALIZING;
			}
			return cleanCSV(line);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return "";
	}
	
	public static String cleanCSV(String element) {
		return element.replaceAll("[\"']+", "").trim();
	}
	
	public static String[] cleanCSVLine(String line) {
		String[] elements = line.split(",");
		for(int i=0;i<elements.length;i++) {
			elements[i] = cleanCSV(elements[i]);
		}
		return elements;
	}
	
	public void compute(WordRelator relator, Set<String> words) {
		
		wordToInt = new Hashtable<String,Integer>();
		
		int i=0;
		for(String word : words) {
			wordToInt.put(word,i);
			i++;
		}
		
		weights = new double[words.size()][words.size()];
		
		int x,y;
		x = 0;
		for(String word1 : words) {
			y = 0;
			for(String word2 : words) {
				weights[x][y] = relator.getDistance(word1, word2);
				y++;
			}
			x++;
		}
		
	}

	public double getDistance(String word1, String word2) {
		Integer w1 = wordToInt.get(word1);
		Integer w2 = wordToInt.get(word2);
		if(w1 == null || w2 == null) {
			return 0;
		}
		return weights[w1][w2];
	}
	
	public String toString() {
		return "Crystalized {" + name + "}";
	}

	public void learn(String[] sentence) {	
		super.learn(sentence);
	}

}
