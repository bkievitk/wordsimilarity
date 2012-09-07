package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import relations.WordRelator;
import tools.KBox;
import tools.PanelTools;
import tools.Stats;
import tools.VerticalLayout;
import tools.WeightedObject;

public class NetworkTools extends JTabbedPane {

	private static final long serialVersionUID = 4773645627647897222L;

	private WordMap wordMap;
	
	public NetworkTools(WordMap wordMap) {
		this.wordMap = wordMap;
		addTab("Distance",distancePanel());
		addTab("Discrepency",discrepencyPanel());
		addTab("Statistics",statisticsPanel());
		addTab("Word",wordPanel());
		addTab("Scatter Plot",scatterPanel());
		addTab("Size vs Word Similarity",wordSimilarityOverSetSizeContainer());
	}
	
	public static void main(String[] args) {
		MainGUIExternal gui = new MainGUIExternal(false);	
		IO.loadWordSimilarity(gui.wordMap, new File("C:/Users/bkiev_000/Desktop/Brent/metaphysicsIEP.csv"));
		IO.loadWordSimilarity(gui.wordMap, new File("C:/Users/bkiev_000/Desktop/Brent/metaphysicsSEP.csv"));
		IO.loadWordSimilarity(gui.wordMap, new File("C:/Users/bkiev_000/Desktop/Brent/kantIEP.csv"));
		IO.loadWordSimilarity(gui.wordMap, new File("C:/Users/bkiev_000/Desktop/Brent/kantSEP.csv"));		
	}

	private static final int SELECT_UNION = 0;
	private static final int SELECT_FIRST = 1;
	private static final int SELECT_AVERAGE = 2;
	private static final int SELECT_INTERSECTION = 3;
	private static final String[] SELECT_NAMES = {"Union","First","Average","Intersection"};
		
	private double[] wordSimilarityOverSetSize(JTextArea text, int min, int max, WordRelator wr1, WordRelator wr2, String targetWord, int wordSelect, int similarityMeasure) {
		
		// Get the top n results for both comparators.		
		KBox<String> box1 = new KBox<String>(max, true);		
		KBox<String> box2 = new KBox<String>(max, true);
		
		if(wordSelect == SELECT_AVERAGE) {
			for(String word : wr1.getWords()) {
				box1.add(new WeightedObject<String>(word, 
						wr1.getDistance(targetWord, word) +
						wr2.getDistance(targetWord, word)));
			}
		} else {
			for(String word : wr1.getWords()) {
				box1.add(new WeightedObject<String>(word, wr1.getDistance(targetWord, word)));
			}			
			for(String word : wr2.getWords()) {
				box2.add(new WeightedObject<String>(word, wr2.getDistance(targetWord, word)));
			}
		}
		
		// Clear text.
		text.setText("");
		
		// Entry for each result.
		double[] results = new double[max-min+1];
		
		
		// For each n.
		for(int n=min;n<=max;n++) {
			
			String[] topWords = null;
			
			
			switch(wordSelect) {
				case SELECT_UNION:
					
					// Add words that are in the top n of either comparator.
					HashSet<String> words = new HashSet<String>();
					for(int i=0;i<n;i++) {
						
						// Comparator 1
						String word = box1.getObject(i).object;
						if(word != null) {
							words.add(word);
						}

						// Comparator 2
						word = box2.getObject(i).object;
						if(word != null) {
							words.add(word);
						}
					}	
					
					// To list.
					topWords = words.toArray(new String[0]);
				break;
				
				case SELECT_FIRST:

					// Add all words that are in the top n of the first comparator.
					words = new HashSet<String>();
					for(int i=0;i<n;i++) {
						String word = box1.getObject(i).object;
						if(word != null) {
							words.add(word);
						}
					}
					
					// To list
					topWords = words.toArray(new String[0]);					
				break;
				
				case SELECT_AVERAGE:
					
					// Actually average
					topWords = new String[Math.min(n, box1.size())];
					for(int i=0;i<topWords.length;i++) {
						topWords[i] = box1.getObject(i).object;
					}
					
				break;
				case SELECT_INTERSECTION:

					HashSet<String> w1 = new HashSet<String>();
					HashSet<String> w2 = new HashSet<String>();

					for(int i=0;i<n;i++) {
						String word = box1.getObject(i).object;
						if(word != null) {
							w1.add(word);
						}
						word = box2.getObject(i).object;
						if(word != null) {
							w2.add(word);
						}
					}
					
					w1.retainAll(w2);

					// To list
					topWords = w1.toArray(new String[0]);
					
				break;
			}
			
			int count = topWords.length;
			
			if(count > 0) {								
				
				// Get new ranks for list 1.
				WeightedObject<String>[] rank1 = new WeightedObject[count];
				for(int j=0;j<topWords.length;j++) {
					rank1[j] = new WeightedObject<String>(topWords[j], wr1.getDistance(targetWord, topWords[j]));
				}				
				Arrays.sort(rank1);		
				
				// Get new ranks for list 2.
				WeightedObject<String>[] rank2 = new WeightedObject[count];
				for(int j=0;j<topWords.length;j++) {
					rank2[j] = new WeightedObject<String>(topWords[j], wr2.getDistance(targetWord, topWords[j]));
				}				
				Arrays.sort(rank2);	
				
				
				Hashtable<Object,Integer> rank2_a = new Hashtable<Object,Integer>();
				for(int j=0;j<rank2.length;j++) {
					rank2_a.put(rank2[j].object, j + 1);
				}
				
				double val = 0;
				
				if(similarityMeasure >= Correlation.DISTANCE_SPEARMAN && similarityMeasure <= Correlation.DISTANCE_CONTENTIOUS) {
					
					int[] p1 = new int[rank1.length];
					for(int i=0;i<p1.length;i++) {
						p1[i] = i;
					}
					
					int[] p2 = new int[rank1.length];
					for(int i=0;i<p1.length;i++) {
						p2[i] = rank2_a.get(rank1[i].object) - 1;
					}
					
					val = Correlation.distance(p1, p2, similarityMeasure);
					
				} else if(similarityMeasure == Correlation.CORRELATION_PEARSON) {
					double[] x = new double[rank1.length];
					for(int i=0;i<x.length;i++) {
						x[i] = rank1[i].weight;
					}
					
					double[] y = new double[rank1.length];
					for(int i=0;i<y.length;i++) {
						y[i] = rank2[rank2_a.get(rank1[i].object) - 1].weight;
					}
					
					val = Correlation.correlationPearson(x, y);
				}
				
				
				
				
				
				// Save results.
				text.append(val + ",");
				results[n-min] = val;
			} else {
				results[n-min] = -100;
			}
		}
		
		return results;
	}
	
	public Container wordSimilarityOverSetSizeContainer() {
		//JPanel ret = new JPanel(new GridLayout(0,1));
		Container ret = Box.createVerticalBox();
		
		JButton run = new JButton("run");
		ret.add(run);
		
		final JTextArea text = new JTextArea();
		text.setPreferredSize(new Dimension(0,20));
		ret.add(PanelTools.addLabel("output values:", text, BorderLayout.WEST));
		
		final BufferedImage image = new BufferedImage(500,500,BufferedImage.TYPE_INT_RGB);
		
		final JPanel panel = new JPanel() {
			private static final long serialVersionUID = -1143203678271329780L;
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(image, 0, 0, this);
			}
		};
		panel.setPreferredSize(new Dimension(0,500));
		ret.add(panel);
		
		final JSlider minSlider = new JSlider(2,5000);
		final JSlider maxSlider = new JSlider(2,5000);
		ret.add(PanelTools.addLabel("min", minSlider, BorderLayout.WEST));
		ret.add(PanelTools.addLabel("max", maxSlider, BorderLayout.WEST));
		
		final JTextField word = new JTextField();
		word.setPreferredSize(new Dimension(0,20));
		final JComboBox<?> relator1 = wordMap.getComboComparators();
		final JComboBox<?> relator2 = wordMap.getComboComparators();
		final JCheckBox log = new JCheckBox("Logarithmic X");
		
		final JPanel wordSelectPanel = new JPanel(new GridLayout(0,1));
		final JRadioButton[] selectButtons = new JRadioButton[SELECT_NAMES.length];
		ButtonGroup selectGroup = new ButtonGroup();
		for(int i=0;i<selectButtons.length;i++) {
			JRadioButton selectButton = new JRadioButton(SELECT_NAMES[i]);
			selectGroup.add(selectButton);
			wordSelectPanel.add(selectButton);
			if(i == 0) {
				selectButton.setSelected(true);
			}
			selectButtons[i] = selectButton;
		}
		wordSelectPanel.setBorder(BorderFactory.createTitledBorder("Word Selection"));
		
		
		
		final JPanel similaritySelectPanelRanked = new JPanel(new GridLayout(0,1));
		final JRadioButton[] similarityButtons = new JRadioButton[Correlation.DISTANCE_NAMES.length];	
		ButtonGroup similarityGroup = new ButtonGroup();
		for(int i=0;i<similarityButtons.length;i++) {
			JRadioButton similarityButton = new JRadioButton(Correlation.DISTANCE_NAMES[i]);
			similarityGroup.add(similarityButton);
			similaritySelectPanelRanked.add(similarityButton);
			if(i == 0) {
				similarityButton.setSelected(true);
			}
			similarityButtons[i] = similarityButton;
		}
		similaritySelectPanelRanked.setBorder(BorderFactory.createTitledBorder("Similarity Measure Ranked"));
		
		final JPanel similaritySelectPanel = new JPanel(new GridLayout(0,1));
		final JRadioButton similarityPearson = new JRadioButton("pearson");
		similarityGroup.add(similarityPearson);
		similaritySelectPanel.add(similarityPearson);
		similaritySelectPanel.setBorder(BorderFactory.createTitledBorder("Similarity Measure Correlation"));
		
		ret.add(PanelTools.addLabel("target word", word, BorderLayout.WEST));
		ret.add(relator1);
		ret.add(relator2);
		ret.add(log);
		ret.add(wordSelectPanel);		
		ret.add(similaritySelectPanelRanked);	
		ret.add(similaritySelectPanel);		
		
		run.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				int min = minSlider.getValue();
				int max = maxSlider.getValue();
				
				int selectType = -1;
				for(int i=0;i<selectButtons.length;i++) {
					if(selectButtons[i].isSelected()) {
						selectType = i;
					}
				}
				
				int similarityType = -1;
				for(int i=0;i<similarityButtons.length;i++) {
					if(similarityButtons[i].isSelected()) {
						similarityType = i;
					}
				}
				if(similarityPearson.isSelected()) {
					similarityType = Correlation.CORRELATION_PEARSON;
				}
				
				if(max > min) {
					double[] results = wordSimilarityOverSetSize(text, min, max, 
							(WordRelator)relator1.getSelectedItem(), 
							(WordRelator)relator2.getSelectedItem(), 
							word.getText(), selectType, similarityType);
					
					
					double[] xs = new double[max-min+1];
					
					if(log.isSelected()) {
						for(int i=0;i<=max-min;i++) {
							xs[i] = Math.log((i+1));
						}				
					} else {
						for(int i=0;i<=max-min;i++) {
							xs[i] = i;
						}				
					}
					
					Graphics g = image.getGraphics();
					g.setColor(Color.WHITE);
					g.fillRect(0, 0, panel.getWidth(), panel.getHeight());
					g.setColor(Color.BLACK);
					Stats.showGraph(g, new Rectangle(0,0,panel.getWidth(),panel.getHeight()), xs, results);
					panel.repaint();
				}
			}
		});
		
		
		return ret;
	}

	public JPanel scatterPanel() {
		JPanel scatterPanel = new JPanel(new VerticalLayout(3,3));

			scatterPanel.add(PanelTools.wrappingText("Select word comparators."));
			
			final JComboBox relator1 = wordMap.getComboComparators();
			final JComboBox relator2 = wordMap.getComboComparators();
			
			scatterPanel.add(relator1);
			scatterPanel.add(relator2);

			JButton calculate = new JButton("calculate");
			scatterPanel.add(calculate);
			
			final BufferedImage[] image = new BufferedImage[1];
			final JPanel render = new JPanel() {
				private static final long serialVersionUID = 9085227113769930888L;
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					
					if(image[0] != null) {
						g.drawImage(image[0], 0, 0, this);
					} else {
						g.setColor(Color.BLACK);
						g.drawString("Press render to render.", 2, 14);
					}
				}
			};
			render.setPreferredSize(new Dimension(0,400));
			scatterPanel.add(render);
						
			calculate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					image[0] = new BufferedImage(render.getWidth(),render.getHeight(),BufferedImage.TYPE_INT_RGB);
					Graphics g = image[0].getGraphics();
					g.setColor(Color.WHITE);
					g.fillRect(0, 0, render.getWidth(), render.getHeight());
					
					WordRelator relate1 = (WordRelator)relator1.getSelectedItem();
					WordRelator relate2 = (WordRelator)relator2.getSelectedItem();
										
					g.setColor(Color.BLACK);
					Vector<WordNode> words = wordMap.activeWordsSorted;
					for(int i=0;i<words.size();i++) {
						for(int j=i+1;j<words.size();j++) {
							double d1 = relate1.getDistance(words.get(i).word, words.get(j).word);
							double d2 = relate2.getDistance(words.get(i).word, words.get(j).word);
							
							if(d1 > .0001 && d2 > .0001) {
	
								int x = (int)(d1 * render.getWidth());
								int y = render.getHeight() - (int)(d2 * render.getHeight());
								g.drawLine(x, y, x, y);
							}
						}	
					}
					
					
					render.repaint();
				}
			});
			
		return scatterPanel;
	}
	
	public JPanel wordPanel() {
		JPanel wordPanel = new JPanel(new VerticalLayout(3,3));
		
			wordPanel.add(PanelTools.wrappingText("Select word to find matches."));
			
			final JTextField word = new JTextField();
			wordPanel.add(word);
			
			final JTextArea results = new JTextArea();
			wordPanel.add(results);
			
			JButton enter = new JButton("find");
				enter.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						WordRelator wordRelator = (WordRelator)JOptionPane.showInputDialog(
		                    null,
		                    "Select comparator.",
		                    "Comparator",
		                    JOptionPane.PLAIN_MESSAGE,
		                    null,
		                    wordMap.activeRelations.toArray(),
		                    null);
						KBox<String> closest = new KBox<String>(20,true);
						String word1 = word.getText();
						
						for(WordNode word2 : wordMap.wordsSorted) {
							closest.add(new WeightedObject<String>(word2.word, wordRelator.getDistance(word1, word2.word)));
						}
						
						results.setText("");
						for(WeightedObject<String> word : closest.getObjects()) {
							results.append(word.weight + ") " + word.object + "\n");
						}
					}
				});
			wordPanel.add(enter);
		
		
		return wordPanel;
	}
	
	public JPanel distancePanel() {
		JPanel distancePanel = new JPanel(new VerticalLayout(3,3));
		
			distancePanel.add(PanelTools.wrappingText("Use this tool to calculate the minimum distance between different words in the network. This will give word chain similarities."));
						
			final JCheckBox allBox = new JCheckBox("Find among all active comparators. ");
			distancePanel.add(allBox);
			
			final JComboBox relators = wordMap.getComboComparators();
			distancePanel.add(PanelTools.addLabel("Find among this relator ", relators,BorderLayout.NORTH));
			
			JButton calculate = new JButton("Calculate");
			distancePanel.add(calculate);

			distancePanel.add(PanelTools.wrappingText("\nResults shown below.\n"));

			final JTextField fromWord = new JTextField();
			final JTextField toWord = new JTextField();
			distancePanel.add(PanelTools.addLabel("From word ", fromWord,BorderLayout.WEST));
			distancePanel.add(PanelTools.addLabel("To word      ", toWord,BorderLayout.WEST));
			
			final JTextArea results = new JTextArea();
			results.setPreferredSize(new Dimension(10,300));
			results.setEditable(false);
			results.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
			distancePanel.add(results);
			
			calculate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					WordNode w1 = wordMap.words.get(fromWord.getText());
					WordNode w2 = wordMap.words.get(toWord.getText());
					if(w1 != null && w2 != null) {
						
						Vector<WordNode> path;
						
						if(allBox.isSelected()) {
							path = getShortestPath(w1, w2, null);
						} else {
							path = getShortestPath(w1, w2, (WordRelator)relators.getSelectedItem());
						}
						
						
						results.setText("");

						if(path == null) {
							results.setText("No path found.");
							return;
						}
						
						for(int i=0;i<path.size();i++) {
							results.append(path.get(i).word + "\n");
							
							if(i < path.size()-1) {
								double minDist = 100000;
								WordRelator minRelator = null;
								for(WordRelator r : wordMap.activeRelations) {
									double dist = r.getDistance(path.get(i).word, path.get(i+1).word);
									dist = 1 - dist;
									dist = Math.max(.000001, dist);
									if(dist < minDist) {
										minDist = dist;
										minRelator = r;
									}
								}
								results.append("    " + minDist + ") " + minRelator + "\n");
							}
						}
					} else {
						results.setText("Selected words are not both active.");
					}
				}				
			});
		return distancePanel;
	}
	
	public double getDistance(WordNode w1, WordNode w2, WordRelator r) {	
		
		if(r == null) {
			double minDist = 100000;			
			for(WordRelator r2 : wordMap.activeRelations) {
				double dist = r2.getDistance(w1.word, w2.word);
				if(dist > .01) {
					dist = 1 - dist;
					dist = Math.max(.000001, dist);
					minDist = Math.min(minDist, dist);
				}
			}
			
			return minDist;	
		} else {
			double dist = r.getDistance(w1.word, w2.word);
			if(dist > .01) {
				dist = 1 - dist;
				dist = Math.max(.000001, dist);
			} else {
				dist = 100000;
			}
			return dist;		
		}
	}
	
	public Vector<WordNode> getShortestPath(WordNode w1, WordNode w2, WordRelator r) {
		
		Hashtable<WordNode,Double> g_score = new Hashtable<WordNode,Double>();
		Hashtable<WordNode,Double> h_score = new Hashtable<WordNode,Double>();
		Hashtable<WordNode,Double> f_score = new Hashtable<WordNode,Double>();
		Hashtable<WordNode,WordNode> came_from = new Hashtable<WordNode,WordNode>();

		g_score.put(w1, 0.0);
		h_score.put(w1, getDistance(w1, w2, r));
		f_score.put(w1, h_score.get(w1));
		
		HashSet<WordNode> openSet = new HashSet<WordNode>();
		HashSet<WordNode> closedSet = new HashSet<WordNode>();
		openSet.add(w1);
		
		while(openSet.size() > 0) {
			double min = Double.MAX_VALUE;
			WordNode x = null;
			for(WordNode w : openSet) {
				Double fScore = f_score.get(w);
				if(fScore != null && fScore < min) {
					min = fScore;
					x = w;
				}
			}
			
			if(x == null) {
				return null;
			}
			
			if(x.equals(w2)) {
				return reconstruct_path(came_from, x);
			}
			
			openSet.remove(x);
			closedSet.add(x);
			
			for(WordNode y : wordMap.words.values()) {
				if(closedSet.contains(y)) {
					continue;
				}
				double tentative_g_score = g_score.get(x) + getDistance(x, y, r);
				boolean tentative_is_better = false;
				
				if(!openSet.contains(y)) {
					openSet.add(y);
					tentative_is_better = true;
				} else if(tentative_g_score < g_score.get(y)) {
					tentative_is_better = true;
				} else {
                    tentative_is_better = false;
				}

				if(tentative_is_better) {
	                came_from.put(y,x);
	                g_score.put(y, tentative_g_score);
	                h_score.put(y, getDistance(y, w2, r));
	                f_score.put(y, g_score.get(y) + h_score.get(y));
				}
			}
		}
		
		return null;
	}
	
	public Vector<WordNode> reconstruct_path(Hashtable<WordNode,WordNode> came_from, WordNode current_node) {
		if(came_from.containsKey(current_node)) {
			Vector<WordNode> p = reconstruct_path(came_from, came_from.get(current_node));
			p.add(current_node);
			return p;
		} else {
			Vector<WordNode> p = new Vector<WordNode>();
			p.add(current_node);
			return p;
		}
	}
	
	public JPanel discrepencyPanel() {
		JPanel discrepencyPanel = new JPanel(new VerticalLayout(3,3));
		
			discrepencyPanel.add(PanelTools.wrappingText("Show the connections between nodes with maximal or minimal discrepency among the different connectors."));
		
			JPanel splitPanel = new JPanel(new GridLayout(1,2));
			final JRadioButton max = new JRadioButton("max");
			JRadioButton min = new JRadioButton("min");
			ButtonGroup group = new ButtonGroup();
			group.add(max);
			group.add(min);
			splitPanel.add(min);
			splitPanel.add(max);
			discrepencyPanel.add(splitPanel);
			max.setSelected(true);
			
			final JSlider numN = new JSlider();
			numN.setMinimum(10);
			numN.setMaximum(100);
			numN.setMajorTickSpacing(-1);
			numN.setMinorTickSpacing(10);
			numN.setPaintLabels(false);
			numN.setPaintTicks(true);
			discrepencyPanel.add(PanelTools.addLabel("Select number of matches to show.", numN, BorderLayout.NORTH));

			discrepencyPanel.add(PanelTools.wrappingText("Select the type of discrepency measurement to use."));
			
			final JRadioButton variance = new JRadioButton("variance");
			variance.setSelected(true);
			final JRadioButton range = new JRadioButton("range");
			group = new ButtonGroup();
			group.add(variance);
			group.add(range);
			discrepencyPanel.add(variance);
			discrepencyPanel.add(range);
			
			final JTextArea results = new JTextArea();
			results.setEditable(false);
			results.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
			JScrollPane scroll = new JScrollPane(results);
			scroll.setPreferredSize(new Dimension(10,200));
			discrepencyPanel.add(scroll);
			
			JButton calculate = new JButton("Calculate");
			discrepencyPanel.add(calculate);
			
			calculate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(wordMap.activeRelations.size() > 1) {
						KBox<Connection> box = new KBox<Connection>(numN.getValue(),max.isSelected());
						for(WordNode w1 : wordMap.getActiveWordNodeList()) {
							for(WordNode w2 : wordMap.getActiveWordNodeList()) {
								if(w1 != w2) {
									
									double value;
									if(variance.isSelected()) {
										double sum = 0;
										double sumSqr = 0;
										for(WordRelator relation : wordMap.activeRelations) {
											double thisVal = relation.getDistance(w1.word, w2.word);
											sum += thisVal;
											sumSqr += thisVal * thisVal;
										}
										int n = wordMap.activeRelations.size();
										value = sumSqr / n - (sum / n) * (sum / n);
									} else {										
										double min = 1;
										double max = 0;
										for(WordRelator relation : wordMap.activeRelations) {
											double thisVal = relation.getDistance(w1.word, w2.word);
											min = Math.min(min, thisVal);
											max = Math.max(max, thisVal);
										}
										value = max - min;
									}
									
									box.add(new WeightedObject<Connection>(new Connection(w1,w2), value));
								}
							}
						}
						results.setText("");
						for(int i=0;i<box.size();i++) {
							results.append(box.getObject(i).weight + ") " + box.getObject(i).object.toString() + "\n");
						}
					} else {
						results.setText("There must be at least two relators\nto perform this operation.");
					}
				}				
			});
		return discrepencyPanel;
	}
	
	class Connection {
		public WordNode w1;
		public WordNode w2;
		public WordRelator r;
		public Double distance;
		
		public Connection(WordNode w1, WordNode w2, WordRelator r, Double distance) {
			this.w1 = w1;
			this.w2 = w2;
			this.r = r;
			this.distance = distance;
		}
		
		public Connection(WordNode w1, WordNode w2) {
			this.w1 = w1;
			this.w2 = w2;
		}
		
		public String toString() {
			return w1.word + ", " + w2.word;
		}	
	}
	
	public double[] getStats(WordRelator relator) {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		double sum = 0;
		double sumSqrd = 0;
		int count = wordMap.activeWords.size() * wordMap.activeWords.size();
		int numConnections = 0;
		
		for(WordNode w1 : wordMap.activeWords.values()) {
			for(WordNode w2 : wordMap.activeWords.values()) {
				double val = relator.getDistance(w1.word, w2.word);
				min = Math.min(min, val);
				max = Math.max(max, val);
				sum += val;
				sumSqrd += val * val;
				if(val > .001) {
					numConnections ++;
				}
			}
		}

		double avg = sum / count;
		double variance = (sumSqrd / count) - (sum / count) * (sum / count);
		double percentConnections = numConnections / (double)count;
		
		double[] vals = {min,max,avg,variance,percentConnections};
		return vals;
	}
	
	public JPanel statisticsPanel() {
		JPanel statisitcsPanel = new JPanel(new BorderLayout());
		
		JButton calculate = new JButton("Calculate");
		statisitcsPanel.add(calculate,BorderLayout.NORTH);
		
		final JPanel results = new JPanel(new VerticalLayout(3,3));
		statisitcsPanel.add(new JScrollPane(results),BorderLayout.CENTER);
		
		calculate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				results.removeAll();

				for(WordRelator relator : wordMap.activeRelations) {
					JPanel statistics = new JPanel(new BorderLayout());
					statistics.setPreferredSize(new Dimension(10,150));
					statistics.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1), relator.toString()));

					JPanel statisticsInner = new JPanel(new VerticalLayout(3,3));
					statistics.add(statisticsInner,BorderLayout.CENTER);
					
					double[] vals = getStats(relator);
					statisticsInner.add(new JLabel("min: " + vals[0]));
					statisticsInner.add(new JLabel("max: " + vals[1]));
					statisticsInner.add(new JLabel("average: " + vals[2]));
					statisticsInner.add(new JLabel("variance: " + vals[3]));
					statisticsInner.add(new JLabel("percent connections: " + vals[4]));
					
					results.add(statistics);
				}
				
				int max = 0;
				for(WordNode word : wordMap.activeWords.values()) {
					max = Math.max(max, word.getCount());
				}
				
				final int[] histogram = new int[100];
				for(WordNode word : wordMap.activeWords.values()) {
					int bin = word.getCount() * histogram.length / max;
					bin = Math.max(0, Math.min(histogram.length-1, bin));
					histogram[bin]++;
				}
				
				final int maxF = max;
				JPanel histogramPanel = new JPanel() {
					private static final long serialVersionUID = 3116252484821343567L;
					public void paintComponent(Graphics g) {
						Stats.showHistogram(g, new Rectangle(0,0,getWidth(), getHeight()), histogram, 0, maxF, null);
					}
				};	
				histogramPanel.setPreferredSize(new Dimension(0,400));
				results.add(new JLabel("Word Frequency Histogram"));	
				results.add(histogramPanel);			
				
				if(wordMap.activeRelations.size() > 1) {
					JPanel statistics = new JPanel();
					statistics.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1), "All"));
					results.add(statistics);
				} else if(wordMap.activeRelations.size() == 0) {
					results.add(new JLabel("No comparators selected."));
				}
				
				invalidate();
				validate();
				repaint();
			}
		});
		
		return statisitcsPanel;
	}

}
