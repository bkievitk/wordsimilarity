package relations.pmi;

import gui.WordMap;

import java.awt.Color;
import java.util.Hashtable;
import java.util.Set;

import relations.WordRelator;

public class WordRelatorPMI extends WordRelator {

	private static final long serialVersionUID = -3037716904772150452L;

	Hashtable<String,Long> wordCounts = new Hashtable<String,Long>();
	Hashtable<String,Long> pairCounts = new Hashtable<String,Long>();
	long totalWord = 0;
	long totalPair = 0;
	
	public static void main(String[] args) {
		WordRelatorPMI pmi = new WordRelatorPMI(Color.BLACK,"",new WordMap());
		pmi.learn("hello world");
		pmi.learn("hello world fred");
		pmi.learn("hello frank fred");
		System.out.println(pmi.getDistance("hello", "world"));
		System.out.println(pmi.getDistance("frank", "world"));
		System.out.println(pmi.getDistance("bob", "world"));
		System.out.println(pmi.getDistance("frank", "hello"));
	}
	
	public WordRelatorPMI(Color color, String name, WordMap wordMap) {
		super(color, name, wordMap);
	}

	public Set<String> getWords() {
		return wordCounts.keySet();
	}
	
	public void learn(String[] sentence) {
		
		// Add word counts.
		for(String word : sentence) {
			Long count = wordCounts.remove(word);
			if(count == null) {
				count = 0l;
			}
			count++;
			totalWord++;
			wordCounts.put(word, count);
		}
		
		// Add word pair counts.
		for(int i=0;i<sentence.length;i++) {
			for(int j=0;j<sentence.length;j++) {
				String join = joinWords(sentence[i],sentence[j]);
				Long count = pairCounts.remove(join);
				if(count == null) {
					count = 0l;
				}
				count++;
				totalPair++;
				pairCounts.put(join, count);
			}
		}
		
		super.learn(sentence);
	}
	
	private String joinWords(String w1, String w2) {
		if(w1.compareTo(w2) > 0) {
			String tmp = w1;
			w1 = w2;
			w2 = tmp;
		}
		return w1 + "~" + w2;
	}
	
	@Override
	public double getDistance(String word1, String word2) {
				
		Long v1 = wordCounts.get(word1);
		Long v2 = wordCounts.get(word2);
		Long v12 = pairCounts.get(joinWords(word1,word2));
		
		if(v1 == null || v2 == null || v12 == null) {
			return 0;
		} else {
			double val = (v12 * totalWord * totalWord) / ((double)(v1* v2 * totalPair));
			double ret = Math.log(val) / Math.log(2);
			return ret;
		}
	}

	@Override
	public String toString() {
		return "PMI + {" + name + "}";
	}

}
