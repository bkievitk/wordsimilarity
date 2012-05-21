package tools;

import java.awt.GridLayout;
import java.util.*;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import relations.WordRelator;

import flanagan.math.Minimization;
import flanagan.math.MinimizationFunction;
import gui.WordMap;
import gui.WordNode;

public abstract class SemanticNeighborHoodDensity {
	public abstract double calculateDensity(WordMap wordMap, String word, WordRelator relator, double[] parameters);
	public abstract double calculateDensity(WordMap wordMap, String word, WordRelator relator);
	public abstract String name();
	public abstract String description();
	public abstract JPanel options();

	public String toString() {
		return name();
	}
	
	public static Vector<SemanticNeighborHoodDensity> getDensityMeasures() {
		Vector<SemanticNeighborHoodDensity> ret = new Vector<SemanticNeighborHoodDensity>();
		
		SemanticNeighborHoodDensity boundingCircle = new SemanticNeighborHoodDensity() {
			
			private JTextField radius = new JTextField(".9");
			private JPanel boundingCircleOptions;
			
			public double calculateDensity(WordMap wordMap, String word, WordRelator relator) {
				double[] parameters = {Double.parseDouble(radius.getText())};
				return calculateDensity(wordMap, word, relator, parameters);
			}
			
			public double calculateDensity(WordMap wordMap, String word, WordRelator relator, double[] parameters) {
				int count = 0;
				for(WordNode word2 : wordMap.wordsSorted) {
					if(!word.equals(word2.word)) {
						double distance = relator.getDistance(word, word2.word);
						if(distance < parameters[0]) {
							count++;
						}
					}
				}
				return count;
			}

			public String name() {
				return "Bounding Circle";
			}
			
			public String description() {
				return "Counts the number of words within a given bounding circle from target word. Words with higher values are assumed to have a larger semantic neighborhood.";
			}

			public JPanel options() {
				if(boundingCircleOptions == null) {
					boundingCircleOptions = new JPanel(new VerticalLayout(3,3));
					boundingCircleOptions.add(PanelTools.wrappingText("Circle radius"));
					boundingCircleOptions.add(radius);
				}
				return boundingCircleOptions;
			}
		};
		ret.add(boundingCircle);
		
		SemanticNeighborHoodDensity distanceOfTop = new SemanticNeighborHoodDensity() {

			private JTextField topN = new JTextField("10");
			private JPanel distanceOfTopOptions;
			
			public double calculateDensity(WordMap wordMap, String word, WordRelator relator) {
				double[] parameters = {Double.parseDouble(topN.getText())};
				return calculateDensity(wordMap, word, relator, parameters);
			}
			
			public double calculateDensity(WordMap wordMap, String word, WordRelator relator, double[] parameters) {
				KBox<WordNode> top = new KBox<WordNode>((int)parameters[0],true);
				for(WordNode word2 : wordMap.wordsSorted) {
					if(!word.equals(word2.word)) {
						double distance = relator.getDistance(word, word2.word);
						top.add(new WeightedObject<WordNode>(word2, distance));
					}
				}
				
				double sum = 0;
				for(WeightedObject<WordNode> word2 : top.getObjects()) {
					sum += word2.weight;
				}
				return sum / top.size();				
			}

			public String name() {
				return "Distance Of Top";
			}
			
			public String description() {
				return "Finds the average distance of the top N most similar words to the target word. Words with lower values are assumed to have a larer semantic neighborhood.";
			}
			
			public JPanel options() {
				if(distanceOfTopOptions == null) {
					distanceOfTopOptions = new JPanel(new VerticalLayout(3,3));
					distanceOfTopOptions.add(PanelTools.wrappingText("Top N"));
					distanceOfTopOptions.add(topN);
				}
				return distanceOfTopOptions;
			}
		};
		ret.add(distanceOfTop);
		
		SemanticNeighborHoodDensity averageDistance = new SemanticNeighborHoodDensity() {

			private JPanel averageDistanceOptions;
			
			public double calculateDensity(WordMap wordMap, String word, WordRelator relator) {
				double[] parameters = {};
				return calculateDensity(wordMap, word, relator, parameters);
			}
			
			public double calculateDensity(WordMap wordMap, String word, WordRelator relator, double[] parameters) {
				int count = 0;
				double sum = 0;
				for(WordNode word2 : wordMap.wordsSorted) {
					if(!word.equals(word2.word)) {
						sum += relator.getDistance(word, word2.word);
						count ++;
					}
				}

				return sum / count;
			}

			public String name() {
				return "Average Distance";
			}
			
			public String description() {
				return "Finds the average distance of all words to the target word. Words with lower values are assumed to have a larer semantic neighborhood.";
			}
			
			public JPanel options() {
				if(averageDistanceOptions == null) {
					averageDistanceOptions = new JPanel(new VerticalLayout(3,3));
				}
				return averageDistanceOptions;
			}
		};
		ret.add(averageDistance);
			
		SemanticNeighborHoodDensity exponentialFit = new SemanticNeighborHoodDensity() {
			
			private JTextField steps = new JTextField("100");
			private JRadioButton exponent = new JRadioButton("exponent", true);
			private JRadioButton multiplier = new JRadioButton("multiplier");
			private JPanel exponentialFitOptions;
			
			public double calculateDensity(WordMap wordMap, String word, WordRelator relator) {
				
				double binary;
				if(exponent.isSelected()) {
					binary = 1;
				} else {
					binary = -1;
				}
				double[] parameters = {Double.parseDouble(steps.getText()),binary};
				return calculateDensity(wordMap, word, relator, parameters);
			}
			
			public double calculateDensity(WordMap wordMap, String word, WordRelator relator, double[] parameters) {
					
				int numBins = (int)parameters[0];
				
				final int[] counts = new int[numBins];
								
				for(WordNode word2 : wordMap.wordsSorted) {
					if(!word.equals(word2.word)) {
						double dist = relator.getDistance(word, word2.word);
						for(int i=0;i<numBins;i++) {
							if(dist < i / (double)numBins) {
								counts[i]++;
							}
						}
					}
				}

				double[] param = {5, .5, 2};
				double[] step = {1,.1, .1};
				MinimizationFunction minFunc = new MinimizationFunction() {
					public double function(double[] param) {
						double sumSqr = 0;
						for(int i=0;i<counts.length;i++) {
							double func = 0;
							if(i > param[0]) {
								func = Math.pow((i-param[0]) * param[2], param[1]);
							} else {
								func = 0;
							}
							double diff = counts[i] - func;
							sumSqr += diff * diff;
						}
						return sumSqr;
					}
				};
				
				Minimization m = new Minimization();
				m.nelderMead(minFunc, param, step);
				param = m.getParamValues();
				
				double val;
				
				if(parameters[1] > 0) {
					val = param[1];
				} else {
					val = param[2];
				}
					
				return val;
			}

			public String name() {
				return "Exponential Fit";
			}
			
			public String description() {
				return "Counts how many words are neighbors within growing similarity bounds. Then tries to map a funciton of the form ((i-a) * b) ^ c to fit the counts. The multiplier b and the exponent c, are assumed to be indicative of the semantic neighborhood density.";
			}
			
			public JPanel options() {
				if(exponentialFitOptions == null) {
					exponentialFitOptions = new JPanel(new VerticalLayout(3,3));
					exponentialFitOptions.add(PanelTools.wrappingText("Steps"));
					exponentialFitOptions.add(steps);
					exponentialFitOptions.add(PanelTools.wrappingText("Measure"));
					JPanel expOption = new JPanel(new GridLayout(0,2));
					expOption.add(exponent);
					expOption.add(multiplier);
					exponentialFitOptions.add(expOption);
					ButtonGroup group = new ButtonGroup();
					group.add(exponent);
					group.add(multiplier);
				}
				return exponentialFitOptions;
			}
		};
		ret.add(exponentialFit);
		
		return ret;
	}
}
