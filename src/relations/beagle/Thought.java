package relations.beagle;


import java.io.*;
import java.util.*;

import tools.CSV;

/**
 * This represents a thought in BEAGLE.
 * @author bkievitk
 */

public class Thought implements Serializable {
	
	private static final long serialVersionUID = -8837166574013829572L;
	
	// Track word name and data.
	public String representation;
	public int count = 0;
	public double[] lexical;
	public double[] environmental;
	
	/**
	 * A thought is composed of a word and a track for its frequency.
	 * @param word		Word in natural language.
	 * @param frequency	Frequency of word occuring scalled.
	 */	
	public Thought(String word, int dimension) {
		this.representation = word;
		this.count = 0;
		environmental = VectorTools.newGaussian(dimension);
		//lexical  = VectorTools.newGaussian(dimension);
		lexical  = VectorTools.zero(dimension);
	}
	
	public String toString() {
		return representation + " [" + count + "]";
	}
	
	public static String toString(Thought[] thoughts) {
		String ret = "";
		for(int i=0;i<thoughts.length;i++) {
			if(i != 0) {
				ret += " ";
			}
			ret += thoughts[i];
		}
		return ret;
	}
	
	/**
	 * Build a stoplist from a given file.
	 * Each line is a new word.
	 * @param f
	 * @return
	 */
	public static HashSet<String> loadStoplist(File f) {
		HashSet<String> stoplist = new HashSet<String>();
		
		// Assume it's comming from a csv file.
		CSV csv = new CSV(f);
		String[] line;

		// Read all lines in.
		while((line = csv.getLine()) != null) {
			stoplist.add(line[0].trim());
		}

		return stoplist;
	}

	/**
	 * Builds hash representation of words based on their representation strings.
	 * @param words
	 * @return
	 */
	public static Hashtable<String,Thought> buildWordSet(Vector<Thought> words) {
		Hashtable<String,Thought> ret = new Hashtable<String,Thought>();
		for(Thought word : words) {
			ret.put(word.representation, word);
		}
		return ret;
	}
	
	public static Hashtable<String,Thought> buildWordSet(String[] words, int dimensions) {
		Hashtable<String,Thought> ret = new Hashtable<String,Thought>();
		for(String word : words) {
			ret.put(word, new Thought(word,dimensions));
		}
		return ret;
	}
	
	public static Thought addWord(String word, int dimensions, Hashtable<String, Thought> hash) {
		Thought t = new Thought(word,dimensions);
		hash.put(word, t);
		return t;
	}
	
	public static Vector<Thought> loadWordsSimple(File f, int dimensions) {
		Vector<Thought> ret = new Vector<Thought>();
		try {
			BufferedReader r = new BufferedReader(new FileReader(f));
			String word;
			while((word = r.readLine()) != null) {
				String[] line = word.split(",");
				Thought t = new Thought(line[1], dimensions);
				t.count = Integer.parseInt(line[0]);
				ret.add(t);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * Load words from a file in csv form using X format.
	 * @param f
	 * @param stopList
	 * @return
	 */
	public static Vector<Thought> loadWords(File f, int dimensions) {
		
		Vector<Thought> ret = new Vector<Thought>();
		
		CSV csv = new CSV(f);
		String[] line;
		
		HashSet<String> words = new HashSet<String>();
		
		while((line = csv.getLine()) != null) {
			String word = line[0].trim();
			word = word.replaceAll(" ?\\(.*\\)", "");
			
			if(word.indexOf(' ') != -1) {
				// Ignore sentences.
			}
			else {
				word = word.toLowerCase();
				word = word.replaceAll("[^a-z]", "");
				
				// Only add words without spaces.
				if(!words.contains(word)) {
					words.add(word);
					//float frequency = Float.parseFloat(line[line.length-1]);
					ret.add(new Thought(word, dimensions));
				}
			}
		}

		return ret;
	}
}
