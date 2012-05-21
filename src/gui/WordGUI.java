package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import relations.WordRelator;
import tools.KBox;
import tools.PanelTools;
import tools.RangeSlider;
import tools.SemanticNeighborHoodDensity;
import tools.Stats;
import tools.VerticalLayout;
import tools.WeightedObject;

public class WordGUI extends JPanel {
	
	private static final long serialVersionUID = 8457793643938843092L;

	public JList allWords;
	public JList activeWords;
	public JScrollPane allWordsScroll;
	public JScrollPane activeWordsScroll;
	public static Random rand = new Random();
	
	public static void readAll(InputStream is, byte[] bytes) throws IOException {
		for(int start = 0; start<bytes.length; start += is.read(bytes,start,bytes.length-start));
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {

		//http://www.indiana.edu/~semantic/word2word/listFiles.php
		
		/*
		try {
			int BUFFER = 2048;
			byte data[] = new byte[BUFFER];
			
			BufferedInputStream origin = new BufferedInputStream(new FileInputStream("IEP2.cmp"), BUFFER);;
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream("IEP4.cmp")));
			ZipEntry entry = new ZipEntry("entry");
			out.putNextEntry(entry);
			
			int count;
			while((count = origin.read(data, 0, BUFFER)) != -1) {
				out.write(data, 0, count);
			}
			origin.close();
			out.close();
			
			
			
			BufferedOutputStream dest = null;
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream("IEP4.cmp")));
			entry = zis.getNextEntry();
			
			// write the files to the disk
			dest = new BufferedOutputStream(new FileOutputStream("IEPUncompressed.cmp"), BUFFER);
			while ((count = zis.read(data, 0, BUFFER)) != -1) {
				dest.write(data, 0, count);
			}
			dest.flush();
			dest.close();			
			zis.close();
			
			
			FileInputStream f1 = new FileInputStream("IEP2.cmp");
			FileInputStream f2 = new FileInputStream("IEPUncompressed.cmp");
			byte[] buffer1 = new byte[1000];
			byte[] buffer2 = new byte[1000];

			f1.read(buffer1);
			f2.read(buffer2);
			
			for(int i=0;i<buffer1.length;i++) {
				if(buffer1[i] != buffer2[i]) {
					System.out.println("ERROR");
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		*/
		
		MainGUIExternal gui = new MainGUIExternal(false);	
		
		try {
			
			
			
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("test.w2w")));
			gui.wordMap.activeRelations = (LinkedList<WordRelator>)in.readObject();
			gui.wordMap.inactiveRelations = (LinkedList<WordRelator>)in.readObject();
			Hashtable<String,WordNode> words = (Hashtable<String,WordNode>)in.readObject();
			Hashtable<String,WordNode> activeWords = (Hashtable<String,WordNode>)in.readObject();	
			gui.wordMap.setWords(words,activeWords);
			gui.wordMap.wordChanged();
			gui.wordMap.relationChanged();
			/*
			WordRelationCrystalized crystalized = new WordRelationCrystalized(Color.BLACK, "new", gui.wordMap);
			
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream("IEP.cmp")));
			zis.getNextEntry();

			byte[] number = new byte[4];
			readAll(zis, number);
			
			int wordCount = number[0] | (number[1] << 8) | (number[2] << 16) | (number[3] << 24);
			
			crystalized.weights = new double[wordCount][wordCount];

			for(int i=0;i<wordCount;i++) {
				for(int j=i+1;j<wordCount;j++) {
					number = new byte[2];
					readAll(zis, number);
					
					short distS = (short)(number[0] | (number[1] << 8));
					double dist = distS * (double)Short.MAX_VALUE;
					
					crystalized.weights[i][j] = crystalized.weights[j][i] = dist;
				}	
			}
			
			crystalized.wordToInt = new Hashtable<String,Integer>();
			for(int i=0;i<wordCount;i++) {
				number = new byte[1];
				readAll(zis, number);
				
				byte[] chars = new byte[number[0] & 0xFF];
				readAll(zis, chars);
				
				String str = new String(chars);
				crystalized.wordToInt.put(str, i);
				
			}
			
			
			zis.close();
			
			gui.wordMap.addRelator(crystalized);
			
			gui.showWordFrame();
			
			
			

			String[] targets = {"chase","book","cat","cookie","dragon","man"};
			//String[] targets = {"chase"};
			for(String target : targets) {
				int numBins = 100;
				final int[] counts = new int[numBins];
				
				WordRelator relator = gui.wordMap.activeRelations.get(0);
				
				for(WordNode word : gui.wordMap.wordsSorted) {
					if(!word.word.equals(target)) {
						double dist = relator.getDistance(word.word, target);
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
								
				double[] vals = new double[counts.length];
				for(int i=0;i<vals.length;i++) {
					vals[i] = i;
				}
				
				double[] expVals = new double[counts.length];
				for(int i=0;i<expVals.length;i++) {
					if(i > param[0]) {
						expVals[i] = Math.pow((i-param[0]) * param[2], param[1]);
					} else {
						expVals[i] = 0;
					}
				}

				VectorTools.show(param);
				VectorTools.show(expVals);
				
				BufferedImage image = new BufferedImage(400,300,BufferedImage.TYPE_INT_RGB);
				Graphics g = image.getGraphics();
				g.setColor(Color.WHITE);
				
				g.fillRect(0, 0, image.getWidth(), image.getHeight());
				//Stats.showHistogram(g, new Rectangle(0,0,image.getWidth(),image.getHeight()), counts, 0, numBins, null);
				
				g.setColor(Color.BLACK);
				double[] ranges = Stats.showGraph(g, new Rectangle(0,0,image.getWidth(),image.getHeight()), vals, VectorTools.toDouble(counts));
				
				g.setColor(Color.RED);
				Stats.showGraph(g, new Rectangle(0,0,image.getWidth(),image.getHeight()), vals, expVals, ranges);
				
				PictureFrame.makeFrame(image);
				
			}
		*/	
		} catch(IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		
 	}
	
	public WordGUI(final WordMap wordMap) {
		
		setLayout(new BorderLayout());
		
		final JButton add = new JButton(">");
		final JButton remove = new JButton("<");
				
		TitledBorder boarder = BorderFactory.createTitledBorder("Word Manager");
		boarder.setTitleJustification(TitledBorder.RIGHT);
		setBorder(boarder);
		
		allWords = new JList(wordMap.wordsSorted); 
		allWords.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		allWords.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		allWords.setVisibleRowCount(-1);

		activeWords = new JList(wordMap.activeWordsSorted); 
		activeWords.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		activeWords.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		activeWords.setVisibleRowCount(-1);
					
		final JPanel panel = new JPanel();

		allWordsScroll = new JScrollPane(allWords);
		allWordsScroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1), "All Words"));
		activeWordsScroll = new JScrollPane(activeWords);
		activeWordsScroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1), "Selected Words"));
		activeWordsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		panel.add(allWordsScroll);
		panel.add(activeWordsScroll);
		panel.add(add);
		panel.add(remove);
		
		wordMap.wordChange.add(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				allWords.updateUI();
				activeWords.updateUI();
			}			
		});
		
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(Object o : allWords.getSelectedValues()) {
					wordMap.setWordStatus((WordNode)o,true);
				}
				allWords.clearSelection();
				panel.repaint();
			}			
		});
		
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(Object o : activeWords.getSelectedValues()) {
					wordMap.setWordStatus((WordNode)o,false);
				}
				activeWords.clearSelection();
				panel.repaint();
			}			
		});
		
		panel.setLayout(new LayoutManager() {

			public void addLayoutComponent(String arg0, Component arg1) {
			}

			public void layoutContainer(Container arg0) {
				int buttonWidth = Math.max(add.getPreferredSize().width,add.getPreferredSize().width);
				int buttonHeight = Math.max(add.getPreferredSize().height,add.getPreferredSize().height);
				
				int textWidth = (arg0.getWidth() - buttonWidth) / 2;
				int textHeight = arg0.getHeight();
				
				allWordsScroll.setLocation(0, 0);
				allWordsScroll.setSize(textWidth, textHeight);

				add.setLocation(textWidth, arg0.getHeight()/2-buttonHeight);
				add.setSize(buttonWidth, buttonHeight);
				
				remove.setLocation(textWidth, arg0.getHeight()/2);
				remove.setSize(buttonWidth, buttonHeight);
				
				activeWordsScroll.setLocation(textWidth+buttonWidth, 0);
				activeWordsScroll.setSize(textWidth, textHeight);
			}

			public Dimension minimumLayoutSize(Container arg0) {
				int width = Math.max(allWords.getPreferredSize().width,activeWords.getPreferredSize().width) * 2 +
							Math.max(add.getPreferredSize().width,add.getPreferredSize().width);
				int height = Math.max(allWords.getPreferredSize().height,activeWords.getPreferredSize().height);
				return new Dimension(width,height);
			}

			public Dimension preferredLayoutSize(Container arg0) {
				int width = Math.max(allWords.getPreferredSize().width,activeWords.getPreferredSize().width) * 2 +
							Math.max(add.getPreferredSize().width,add.getPreferredSize().width);
				int height = Math.max(allWords.getPreferredSize().height,activeWords.getPreferredSize().height);
				return new Dimension(width,height);
			}

			public void removeLayoutComponent(Component arg0) {
			}
			
		});
		
		add(panel,BorderLayout.CENTER);
		
		JTabbedPane tabbed = new JTabbedPane();
		tabbed.setPreferredSize(new Dimension(230,10));
		
			JPanel allPanel = new JPanel(new VerticalLayout(3,3));
			allPanel.add(PanelTools.wrappingText("Add all words from the word list into the active word list or remove all words from the active word list."));
			JPanel buttons = new JPanel(new GridLayout(1,2));
			JButton addAll = new JButton("Add");
			JButton removeAll = new JButton("Remove");
			
			addAll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for(WordNode word : wordMap.words.values()) {
						wordMap.setWordStatus(word,true);
					}
					wordMap.wordChanged();					
				}
			});
			
			removeAll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					wordMap.activeWords.clear();
					wordMap.activeWordsSorted.clear();
					wordMap.wordChanged();

					panel.validate();
					panel.repaint();
				}
			});
			
			buttons.add(addAll);
			buttons.add(removeAll);
			allPanel.add(buttons);
			
		tabbed.addTab("all", allPanel);
		
			JPanel relatorPanel = new JPanel(new VerticalLayout(3,3));
			relatorPanel.add(PanelTools.wrappingText("Add or remove all words that the word relator has learned explicityly about."));
			final JComboBox relators = wordMap.getComboComparators();
			relatorPanel.add(relators);
			
			buttons = new JPanel(new GridLayout(1,2));
			JButton addRelator = new JButton("Add");
			JButton removeRelator = new JButton("Remove");
			
			addRelator.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					WordRelator relator = (WordRelator)relators.getSelectedItem();
					if(relator != null) {
						Set<String> words = relator.getWords();
						if(words != null) {
							for(String word : words) {
								wordMap.setWordStatus(word,true);
							}
							wordMap.wordChanged();
						}
						panel.repaint();
					}
				}
			});
			
			removeRelator.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					WordRelator relator = (WordRelator)relators.getSelectedItem();
					if(relator != null) {
						Set<String> words = relator.getWords();
						if(words != null) {
							for(String word : words) {
								wordMap.setWordStatus(word,false);
							}
							wordMap.wordChanged();
						}
						panel.repaint();
					}
				}
			});

			buttons.add(addRelator);
			buttons.add(removeRelator);
			relatorPanel.add(buttons);
			
		tabbed.addTab("relator", relatorPanel);

			JPanel randomPanel = new JPanel(new VerticalLayout(3,3));
			randomPanel.add(PanelTools.wrappingText("Add or remove a random selection of words from the all list to the selected list."));
			
			final JSlider count1 = new JSlider();
			count1.setMinimum(0);
			count1.setMaximum(1000);
			count1.setMinorTickSpacing(100);
			count1.setMajorTickSpacing(250);
			count1.setPaintTicks(true);
			count1.setPaintLabels(true);
			count1.setValue(25);
			randomPanel.add(PanelTools.addLabel("Words  ", count1, BorderLayout.WEST));
			
			buttons = new JPanel(new GridLayout(1,2));
			addAll = new JButton("Add");
			removeAll = new JButton("Remove");
			
			addAll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for(int i=0;i<count1.getValue();i++) {
						WordNode newNode = wordMap.wordsSorted.get(rand.nextInt(wordMap.wordsSorted.size()));
						wordMap.setWordStatus(newNode.word,true);
					}
					wordMap.wordChanged();
					panel.repaint();
				}
			});
			
			removeAll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for(int i=0;i<count1.getValue();i++) {
						if(wordMap.activeWordsSorted.size() > 0) {
							wordMap.setWordStatus(wordMap.activeWordsSorted.get(rand.nextInt(wordMap.activeWordsSorted.size())).word,false);
						}
					}
					wordMap.wordChanged();
					panel.repaint();
				}
			});
			
			buttons.add(addAll);
			buttons.add(removeAll);
			randomPanel.add(buttons);
			
		tabbed.addTab("rand", randomPanel);

			JPanel wordPanel = new JPanel(new VerticalLayout(3,3));
			wordPanel.add(PanelTools.wrappingText("Add a single word to the full word list."));
			final JTextField newWord = new JTextField("");
			wordPanel.add(newWord);
			
			buttons = new JPanel(new GridLayout(1,2));
			JButton addWord = new JButton("Add");
			JButton removeWord = new JButton("Remove");
			buttons.add(addWord);
			buttons.add(removeWord);
			wordPanel.add(buttons);

			final JCheckBox addToActive = new JCheckBox("Add to active list OR");
			wordPanel.add(addToActive);
			wordPanel.add(new JLabel("       Remove from primary"));
			
			addWord.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					wordMap.setWordStatus(newWord.getText(),addToActive.isSelected());
					wordMap.wordChanged();
					panel.repaint();
				}
			});
			
			removeWord.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					wordMap.setWordStatus(newWord.getText(),addToActive.isSelected());
					wordMap.wordChanged();
					panel.repaint();
				}
			});
			
		tabbed.addTab("word", wordPanel);
		
			JPanel nearPanel = new JPanel(new VerticalLayout(3,3));

			nearPanel.add(PanelTools.wrappingText("Select a word and keep all of the words that are closest to that word."));
			
			final JComboBox similarities = new JComboBox();
			wordMap.relationChange.add(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					similarities.removeAllItems();
					for(WordRelator relation : wordMap.activeRelations) {
						similarities.addItem(relation);
					}
					panel.repaint();
				}
			});
			nearPanel.add(similarities);
			
			final JSlider count = new JSlider();
			count.setMinimum(0);
			count.setMaximum(200);
			count.setMinorTickSpacing(50);
			count.setMajorTickSpacing(100);
			count.setPaintTicks(true);
			count.setPaintLabels(true);
			count.setValue(25);
			
			nearPanel.add(PanelTools.addLabel("Top N  ", count, BorderLayout.WEST));
			
			final JTextField word = new JTextField();
			nearPanel.add(PanelTools.addLabel("Word     ", word, BorderLayout.WEST));
						
			JButton addRelated = new JButton("Add");
			nearPanel.add(addRelated);
			
			addRelated.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Object o = similarities.getSelectedItem();
					if(o != null && o instanceof WordRelator) {
						WordNode center = wordMap.words.get(word.getText());
						if(center != null) {
							wordMap.setWordStatus(center,true);
							
							WordRelator relator = (WordRelator)o;
							int num = count.getValue();
							
							KBox<WordNode> box = new KBox<WordNode>(num,true);
							for(WordNode node : wordMap.words.values()) {
								box.add(new WeightedObject<WordNode>(node, relator.getDistance(center.word, node.word)));
							}
							
							for(int i=0;i<box.size();i++) {
								wordMap.setWordStatus(box.getObject(i).object,true);
							}
							wordMap.wordChanged();
							panel.repaint();
						}
					}
				}
			});

			tabbed.addTab("near", nearPanel);
			
		
		JPanel countPanel = new JPanel(new VerticalLayout(3,3));
		countPanel.add(PanelTools.wrappingText("Select all words with a given occurance count."));
		final RangeSlider range = new RangeSlider(0,1000000);
		
		range.setValue(0);
		range.setUpperValue(1000000);   
		range.setMajorTickSpacing(100000);
		range.setPaintTicks(true);
		range.setPaintLabels(true);
		
		Hashtable<Integer,JLabel> labelTable = new Hashtable<Integer,JLabel>();
		labelTable.put( new Integer( 0 ), new JLabel("0") );
		labelTable.put( new Integer( 1000000 ), new JLabel("1000000") );
		range.setLabelTable( labelTable );
		countPanel.add(range);
		
		JPanel sliderValues = new JPanel(new GridLayout(1,2));
		final JTextField minValue = new JTextField("0");
		final JTextField maxValue = new JTextField("1000000");
		sliderValues.add(minValue);
		sliderValues.add(maxValue);
		
		range.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				minValue.setText(range.getValue() + "");
				maxValue.setText(range.getUpperValue() + "");
			}			
		});
		countPanel.add(sliderValues);
		
		minValue.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {}
			public void keyReleased(KeyEvent arg0) {
				try {
					int min = Integer.parseInt(minValue.getText());
					if(min != range.getValue()) {
						range.setValue(min);
					}
				} catch(NumberFormatException e) {}
			}
			public void keyTyped(KeyEvent arg0) {}			
		});
		
		maxValue.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {}
			public void keyReleased(KeyEvent arg0) {
				try {
					int max = Integer.parseInt(maxValue.getText());
					if(max != range.getUpperValue()) {
						range.setUpperValue(max);
					}
				} catch(NumberFormatException e) {}
			}
			public void keyTyped(KeyEvent arg0) {}			
		});
		
		buttons = new JPanel(new GridLayout(1,2));
		addWord = new JButton("Add");
		removeWord = new JButton("Remove");
		buttons.add(addWord);
		buttons.add(removeWord);
		countPanel.add(buttons);
		
		addWord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for(WordNode wordNode : wordMap.words.values()) {
					if(wordNode.getCount() >= range.getValue() && wordNode.getCount() <= range.getUpperValue()) {
						wordMap.setWordStatus(wordNode,true);
					}
				}
				wordMap.wordChanged();
			}			
		});
		
		removeWord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for(WordNode wordNode : wordMap.words.values()) {
					if(wordNode.getCount() >= range.getValue() && wordNode.getCount() <= range.getUpperValue()) {
						wordMap.setWordStatus(wordNode,false);
					}
				}
				wordMap.wordChanged();
			}			
		});
		
		tabbed.addTab("count", countPanel);
				
		JPanel listPanel = new JPanel(new VerticalLayout(3,3));
		tabbed.addTab("list", listPanel);
		listPanel.add(PanelTools.wrappingText("Add or remove words in a list, one word per line."));
		
		final JTextArea listTextArea = new JTextArea();
		listTextArea.setPreferredSize(new Dimension(10,100));
		listPanel.add(new JScrollPane(listTextArea));
		
		JPanel listButtons = new JPanel(new GridLayout(1,2));
		JButton listAdd = new JButton("add");
		JButton listRemove = new JButton("remove");
		listButtons.add(listAdd);
		listButtons.add(listRemove);
		listPanel.add(listButtons);
		
		listAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for(String word : listTextArea.getText().split("\n")) {
					wordMap.setWordStatus(word, true);
				}
				wordMap.wordChanged();
			}
		});
		
		listRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for(String word : listTextArea.getText().split("\n")) {
					wordMap.setWordStatus(word, false);
				}
				wordMap.wordChanged();
			}
		});
		
		tabbed.addTab("neighborhood", semanticNeighborhoodDensity(wordMap));
		
		add(tabbed,BorderLayout.EAST);
		
	}
	
	public JPanel semanticNeighborhoodDensity(final WordMap wordMap) {
		final JPanel semanticNeighborhoodDensity = new JPanel(new VerticalLayout(3,3));

		Vector<SemanticNeighborHoodDensity> measures = SemanticNeighborHoodDensity.getDensityMeasures();

		final JPanel optionsHolder = new JPanel(new BorderLayout());
		final JTextArea description = PanelTools.wrappingText(measures.get(0).description());

		optionsHolder.removeAll();
		optionsHolder.add(measures.get(0).options(), BorderLayout.CENTER);

		final double[] foundRange = new double[2];
		
		final String[] histogramLabels = {"e-2","e-1","e0","e1","e2","e3","e4","e5","e6","e7"};
		final int[] histogram = new int[10];
		JPanel histogramPanel = new JPanel() {
			private static final long serialVersionUID = 3116252484821343567L;
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, getWidth(), getHeight());
				
				if(!Stats.showHistogram(g, new Rectangle(0,18,getWidth(), getHeight()-18), histogram, 0, histogram.length, histogramLabels)) {
					g.setColor(Color.BLACK);
					g.drawString("No data", 0, 12);
				} else {
					g.setColor(Color.BLACK);
					
					g.drawString(String.format("Range: %.3f - %.3f", foundRange[0], foundRange[1]), 2, 14);
					//g.drawString("Range: " + foundRange[0] + "-" + foundRange[1], 2, 14);
				}
				
			}
		};
		histogramPanel.setPreferredSize(new Dimension(0,120));
		
		final JComboBox combo = new JComboBox(measures);
		combo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				description.setText(((SemanticNeighborHoodDensity)combo.getSelectedItem()).description());
				optionsHolder.removeAll();
				optionsHolder.add(((SemanticNeighborHoodDensity)combo.getSelectedItem()).options(), BorderLayout.CENTER);
				semanticNeighborhoodDensity.validate();
				semanticNeighborhoodDensity.invalidate();
				semanticNeighborhoodDensity.repaint();
			}
		});
		
		final JComboBox relators = wordMap.getComboComparators();
		
		JPanel range = new JPanel(new GridLayout(0,2));
		final JTextField min = new JTextField("0");
		final JTextField max = new JTextField("1");
		range.add(new JLabel("min"));
		range.add(new JLabel("max"));
		range.add(min);
		range.add(max);

		JPanel addRemove = new JPanel(new GridLayout(1,0));
		JButton add = new JButton("add");
		JButton remove = new JButton("remove");
		JButton stats = new JButton("stats");
		addRemove.add(add);
		addRemove.add(remove);
		addRemove.add(stats);
				
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				WordRelator relator = (WordRelator)relators.getSelectedItem();
				SemanticNeighborHoodDensity densityMeasure = ((SemanticNeighborHoodDensity)combo.getSelectedItem());
								
				try {

					double minD = Double.parseDouble(min.getText());
					double maxD = Double.parseDouble(max.getText());
										
					// For each word.
					for(WordNode word : wordMap.wordsSorted) {
						double value = densityMeasure.calculateDensity(wordMap, word.word, relator);						
						if(value >= minD && value <= maxD) {
							wordMap.setWordStatus(word, true);
						}
					}
					wordMap.wordChanged();
					
				} catch(NumberFormatException e2) {
					e2.printStackTrace();
				}			

				semanticNeighborhoodDensity.repaint();
			}
		});
		
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				WordRelator relator = (WordRelator)relators.getSelectedItem();
				SemanticNeighborHoodDensity densityMeasure = ((SemanticNeighborHoodDensity)combo.getSelectedItem());
								
				try {
					double minD = Double.parseDouble(min.getText());
					double maxD = Double.parseDouble(max.getText());
					
					// For each word.
					for(WordNode word : wordMap.wordsSorted) {
						double value = densityMeasure.calculateDensity(wordMap, word.word, relator);						
						if(value >= minD && value <= maxD) {
							wordMap.setWordStatus(word, false);
						}
					}
					wordMap.wordChanged();
					
				} catch(NumberFormatException e2) {
					e2.printStackTrace();
				}			

				semanticNeighborhoodDensity.repaint();
			}
		});
		
		stats.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				WordRelator relator = (WordRelator)relators.getSelectedItem();
				SemanticNeighborHoodDensity densityMeasure = ((SemanticNeighborHoodDensity)combo.getSelectedItem());
				
				int offset = 2;
				
				try {
					
					// Clear histogram.
					for(int i=0;i<histogram.length;i++) {
						histogram[i] = 0;
					}
					foundRange[0] = Double.MAX_VALUE;
					foundRange[1] = Double.MIN_VALUE;
					
					// For each word.
					for(WordNode word : wordMap.wordsSorted) {

						double value = densityMeasure.calculateDensity(wordMap, word.word, relator);
						
						foundRange[0] = Math.min(foundRange[0], value);
						foundRange[1] = Math.max(foundRange[1], value);

						int histIndex = Math.max(0,Math.min(histogram.length-1, (int)Math.log10(value) + offset));
						histogram[histIndex]++;
					}
					wordMap.wordChanged();
					
				} catch(NumberFormatException e2) {
					e2.printStackTrace();
				}			

				semanticNeighborhoodDensity.repaint();
			}
		});

		
		
		
		
		
		semanticNeighborhoodDensity.add(relators);
		semanticNeighborhoodDensity.add(combo);
		semanticNeighborhoodDensity.add(description);
		semanticNeighborhoodDensity.add(range);
		semanticNeighborhoodDensity.add(addRemove);
		semanticNeighborhoodDensity.add(optionsHolder);
		semanticNeighborhoodDensity.add(histogramPanel);
		
		return semanticNeighborhoodDensity;
	}
}
