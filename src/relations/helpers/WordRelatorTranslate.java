package relations.helpers;
import gui.WordMap;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import relations.WordRelator;


public class WordRelatorTranslate extends WordRelator {

	private static final long serialVersionUID = -5880669782756296255L;
	
	@SuppressWarnings("rawtypes")
	public final static Class wizardPanel = WizardCrystalized.class;
	public final static String description = "This represents a probabilistic translation.";
	public final static String typeName = "Translate";
		
	public Hashtable<String,Integer> language1;
	public Hashtable<String,Integer> language2;
	public double[][] weights;
		
	public Set<String> getWords() {
		Set<String> ret = new HashSet<String>();
		ret.addAll(language1.keySet());
		ret.addAll(language2.keySet());
		return ret;
	}
	
	public WordRelatorTranslate(Color color, String name, WordMap wordMap) {
		super(color, name, wordMap);
	}
	
	public WordRelatorTranslate(Color color, WordMap wordMap, BufferedReader input) {
		super(color,readName(input),wordMap);
		
		if(name == WordRelator.ERROR_INITIALIZING) {
			return;
		}

		try {
			
			String line = input.readLine();
			
			String[] sizes = line.split("x");
			int l1 = Integer.parseInt(sizes[0]);
			int l2 = Integer.parseInt(sizes[1]);
			
			String[] words = cleanCSVLine(input.readLine());

			language1 = new Hashtable<String,Integer>();
			language2 = new Hashtable<String,Integer>();
			
			for(int i=1;i<words.length;i++) {
				language1.put(words[i],language1.size());
			}
			weights = new double[l2][l1];
			
			int i = 0;
			while((line = input.readLine()) != null) {
				if(line.length() == 0) {
					break;
				}
				
				String[] elements = cleanCSVLine(line);
				language2.put("<" + elements[0] + ">",language2.size());
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

	public double getDistance(String word1, String word2) {
		
		if(word1.length() == 0 || word2.length() == 0) {
			return 0;
		}
		
		if(word2.charAt(0) == '<') {
			if(word1.charAt(0) != '<') {
				Integer w1 = language1.get(word1);
				Integer w2 = language2.get(word2);
				if(w1 == null || w2 == null) {
					return 0;
				}
				return weights[w2][w1];
			} else {
				return 0;
			}
		} else if(word1.charAt(0) == '<') {
			Integer w1 = language1.get(word2);
			Integer w2 = language2.get(word1);
			if(w1 == null || w2 == null) {
				return 0;
			}
			return weights[w2][w1];
		} else {
			return 0;
		}
	}
	
	public String toString() {
		return "Translate {" + name + "}";
	}

	public void learn(String[] sentence) {	
		super.learn(sentence);
	}

}
