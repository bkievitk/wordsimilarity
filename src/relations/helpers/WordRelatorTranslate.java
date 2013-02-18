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
import relations.beagle.VectorTools;


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

	public WordRelatorTranslate(Color color, String name, WordMap wordMap, String[] xAxis, String[] yAxis, double[][] values) {
		super(color, name, wordMap);

		language1 = new Hashtable<String,Integer>();
		language2 = new Hashtable<String,Integer>();
		
		for(int i=0;i<yAxis.length;i++) {
			if(language1.containsKey(yAxis[i])) {
				int j = 0;
				while(language1.containsKey(yAxis[i] + "_" + j)) {
					j++;
				}
				language1.put(yAxis[i] + "_" + j,language1.size());
			} else {
				language1.put(yAxis[i],language1.size());
			}
		}
		
		for(int i=0;i<xAxis.length;i++) {
			if(language2.containsKey(xAxis[i])) {
				int j = 0;
				while(language2.containsKey(xAxis[i] + "_" + j)) {
					j++;
				}
				language2.put(xAxis[i] + "_" + j,language2.size());
			} else {
				language2.put(xAxis[i],language2.size());
			}
		}
		
		weights = VectorTools.copy(values);
	}
	
	public WordRelatorTranslate(Color color, WordMap wordMap, BufferedReader input, String title) {
		super(color,title,wordMap);

		try {
			language1 = new Hashtable<String,Integer>();
			language2 = new Hashtable<String,Integer>();
	
			String[] words = cleanCSVLine(input.readLine());
			
			for(int i=1;i<words.length;i++) {
				if(language2.containsKey(words[i])) {
					int j = 0;
					while(language2.containsKey(words[i] + "_" + j)) {
						j++;
					}
					language2.put(words[i] + "_" + j,language2.size());
				} else {
					language2.put(words[i],language2.size());
				}
			}
			
			Vector<double[]> weights = new Vector<double[]>();
			
			String line;
			while((line = input.readLine()) != null) {
				if(line.length() == 0) {
					break;
				}
				
				String[] elements = cleanCSVLine(line);

				if(language1.containsKey(elements[0])) {
					int j = 0;
					while(language1.containsKey(elements[0] + "_" + j)) {
						j++;
					}
					language1.put(elements[0] + "_" + j,language1.size());
				} else {
					language1.put(elements[0],language1.size());
				}
				
				
				double[] lineVals = new double[elements.length-1];
				weights.add(lineVals);
				
				for(int j=1;j<elements.length;j++) {
					lineVals[j-1] = Double.parseDouble(elements[j]);
				};
			}
			
			this.weights = new double[weights.size()][];
			for(int i=0;i<weights.size();i++) {
				this.weights[i] = weights.get(i);
			}

			/*
			double max = this.weights[0][0];
			double min = this.weights[0][0];
			for(int i=0;i<this.weights.length;i++) {
				for(int j=0;j<this.weights[0].length;j++) {
					max = Math.max(max, this.weights[i][j]);
					min = Math.min(min, this.weights[i][j]);
				}	
			}
			for(int i=0;i<this.weights.length;i++) {
				for(int j=0;j<this.weights[0].length;j++) {
					this.weights[i][j] = 1 - (this.weights[i][j] - min) / (max - min);
				}	
			}
			*/
			
			for(int i=0;i<this.weights.length;i++) {
				double max = this.weights[i][0];
				double min = this.weights[i][0];
				for(int j=0;j<this.weights[0].length;j++) {
					max = Math.max(max, this.weights[i][j]);
					min = Math.min(min, this.weights[i][j]);
				}
				for(int j=0;j<this.weights[0].length;j++) {
					this.weights[i][j] = 1 - (this.weights[i][j] - min) / (max - min);
				}	
			}
				
			//for(String w : language1.keySet()) {
			//	System.out.println(w + " " + language1.get(w));
			//}
			//for(String w : language2.keySet()) {
			//	System.out.println(w + " " + language2.get(w));
			//}
			//for(int i=0;i<this.weights.length;i++) {
			//	for(int j=0;j<this.weights[0].length;j++) {
			//		System.out.print(String.format("%4.4f ", this.weights[i][j]));
			//	}	
			//	System.out.println();
			//}
			
		} catch(IOException e) {
			e.printStackTrace();
		}
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
				
				//System.out.println(w1 + " " + w2);
				
				if(w1 == null || w2 == null) {
					return 0;
				}
				return weights[w1][w2];
			} else {
				return 0;
			}
		} else if(word1.charAt(0) == '<') {
			Integer w1 = language1.get(word2);
			Integer w2 = language2.get(word1);
			
			System.out.println(w1 + " " + w2);
			
			if(w1 == null || w2 == null) {
				return 0;
			}
			return weights[w1][w2];
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
