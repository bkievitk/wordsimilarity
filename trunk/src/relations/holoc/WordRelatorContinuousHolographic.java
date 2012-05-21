package relations.holoc;

import gui.WordMap;

import java.awt.Color;
import java.util.Hashtable;
import java.util.Set;

import relations.WordRelator;

public class WordRelatorContinuousHolographic extends WordRelator {

	private static final long serialVersionUID = 1014750367429484800L;

	@SuppressWarnings("rawtypes")
	public final static Class wizardPanel = WizardContinuousHolographic.class;
	public final static String description = "This is an experimental relator that is based on BEAGLE. This comparator uses continuous holographic representations instead of discrete ones.";
	public final static String typeName = "Continuous Holographic";
	
	private Hashtable<String,WordData> words = new Hashtable<String,WordData>();
	public static final int WORD_SPAN_LIMIT = 3;
		
	public WordRelatorContinuousHolographic(Color color, String name, WordMap wordMap) {
		super(color, name, wordMap);
	}

	public Set<String> getWords() {
		return words.keySet();
	}
	
	public double getDistance(String word1, String word2) {
		
		WordData w1 = words.get(word1);
		WordData w2 = words.get(word2);
		if(w1 == null || w2 == null) {
			return 0;
		}
		
		try {

			double intStart = 0;
			double intStop = Math.PI * 10;
			
			// Distance = integral(a*b) / (integral(a*a) * integral(b*b))
			
			double integralAB = 0;
			for(WordData d1 : w1.cooccurences.keySet()) {
				int m1 = w1.cooccurences.get(d1);
				for(WordData d2 : w2.cooccurences.keySet()) {
					int m2 = w2.cooccurences.get(d2);
					Function f = d1.representation.multiply(d2.representation);
					integralAB += f.integrateRange(m1 * m2, intStart, intStop);
				}	
			}
			
			double integralA = 0;
			for(WordData d1 : w1.cooccurences.keySet()) {
				int m1 = w1.cooccurences.get(d1);
				for(WordData d2 : w1.cooccurences.keySet()) {
					int m2 = w1.cooccurences.get(d2);
					Function f = d1.representation.multiply(d2.representation);
					integralA += f.integrateRange(m1 * m2, intStart, intStop);
				}	
			}
			
			double integralB = 0;
			for(WordData d1 : w2.cooccurences.keySet()) {
				int m1 = w2.cooccurences.get(d1);
				for(WordData d2 : w2.cooccurences.keySet()) {
					int m2 = w2.cooccurences.get(d2);
					Function f = d1.representation.multiply(d2.representation);
					integralB += f.integrateRange(m1 * m2, intStart, intStop);
				}	
			}
			
			double dot = integralAB / (Math.sqrt(integralA) * Math.sqrt(integralB));
			
			//System.out.println(integralAB + " " + integralA + " " + integralB + " " + dot + " " + Math.acos(dot));
			
			return Math.min(1, Math.max(0, 1 - dot));
			
		} catch (UnhandledFunctionException e) {
			e.printStackTrace();
		}
		
		return 0;
	}

	public String toString() {
		return "Holographic Continuous {" + name + "}";
	}	

	public WordData getWordData(String word) {
		WordData wordData = words.get(word);
		if(wordData == null) {
			wordData = new WordData(words.size());
			words.put(word,wordData);
		}
		return wordData;
	}
	
	public void learn(String[] sentence) {		
		super.learn(sentence);			
		WordData[] wordDatas = new WordData[sentence.length];
		for(int i=0;i<sentence.length;i++) {
			wordDatas[i] = getWordData(sentence[i]);
		}
		learn(wordDatas);
	}
	
	public void learn(WordData[] words) {
		for(int i=0;i<words.length;i++) {
			words[i].count ++;
			for(int j=Math.max(0, i-WORD_SPAN_LIMIT);j<=Math.min(words.length-1, i+WORD_SPAN_LIMIT);j++) {
				if(i != j) {
					words[i].learnCoOccurence(words[j]);
				}
			}
		}
	}
	
}
