package gui;

import java.awt.*;
import java.awt.geom.*;
import java.io.Serializable;
import java.util.*;

import javax.swing.JComboBox;
import javax.swing.JProgressBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import relations.WordRelator;


public class WordMap implements Serializable {
	
	private static final long serialVersionUID = -2220684327700953351L;
	
	// Words.
	public Hashtable<String,WordNode> words = new Hashtable<String,WordNode>();
	public Hashtable<String,WordNode> activeWords = new Hashtable<String,WordNode>();
	public Vector<WordNode> wordsSorted = new Vector<WordNode>();
	public Vector<WordNode> activeWordsSorted = new Vector<WordNode>();

	// Relations.
	public LinkedList<WordRelator> activeRelations = new LinkedList<WordRelator>();
	public LinkedList<WordRelator> inactiveRelations = new LinkedList<WordRelator>();
		
	// Changes.
	public transient Vector<ChangeListener> wordChange = new Vector<ChangeListener>();
	public transient Vector<ChangeListener> relationChange = new Vector<ChangeListener>();
	
	/**
	 * Clear
	 */
	public void clear() {
		words.clear();
		activeWords.clear();
		wordsSorted.clear();
		activeWordsSorted.clear();
		activeRelations.clear();
		inactiveRelations.clear();
	}
	
	public void addRelator(WordRelator relator) {
		activeRelations.add(relator);
		setRelatorStatus(relator,true);
		addRelatorWords(relator);
	}
	
	public void activateRelatorWords(WordRelator relator) {
		Set<String> words = relator.getWords();
		if(words != null) {
			for(String word : words) {
				setWordStatus(word,true);
			}
			wordChanged();
		}
	}
	
	/**
	 * Set the word lists directly.
	 * @param words
	 * @param activeWords
	 */
	public void setWords(Hashtable<String,WordNode> words, Hashtable<String,WordNode> activeWords) {
		this.words = words;
		this.activeWords = activeWords;
		
		wordsSorted.clear();
		for(WordNode word : words.values()) {
			addAlphabetical(wordsSorted,word);
		}
		
		activeWordsSorted.clear();
		for(WordNode word : activeWords.values()) {
			addAlphabetical(activeWordsSorted,word);
		}
	}
	
	/**
	 * Call when word set is changed.
	 */
	public void wordChanged() {
		ChangeEvent ce = new ChangeEvent(this);
		for(ChangeListener cl : wordChange) {
			cl.stateChanged(ce);
		}
	}

	/**
	 * Call when relations are changed.
	 */
	public void relationChanged() {
		ChangeEvent ce = new ChangeEvent(this);
		for(ChangeListener cl : relationChange) {
			cl.stateChanged(ce);
		}
	}
	
	/**
	 * All words the system knows about.
	 * @return
	 */
	public Collection<WordNode> getFullWordNodeList() {
		return words.values();
	}

	/**
	 * Get active relators.
	 * @return
	 */
	public Collection<WordRelator> getActiveWordRelators() {
		return activeRelations;
	}

	/**
	 * Get active words.
	 * @return
	 */
	public Collection<WordNode> getActiveWordNodeList() {
		return activeWords.values();
	}
		
	/**
	 * Set a relator to be active or inactive.
	 * @param relator
	 * @param active
	 */
	public void setRelatorStatus(WordRelator relator, boolean active) {
		
		// Remove from inactive and active.
		inactiveRelations.remove(relator);
		activeRelations.remove(relator);

		// Add back to one list.
		if(active) {
			activeRelations.add(relator);
		} else {
			inactiveRelations.add(relator);
		}
		
		// The relations have changed.
		relationChanged();
	}
	
	/**
	 * Add all of the words that a particular relator knows about.
	 * @param relator
	 */
	public void addRelatorWords(WordRelator relator) {
		Set<String> newWords = relator.getWords();
				
		if(newWords != null) {
			for(String word : newWords) {
				addNewWord(word);
			}	
		}
	}
	
	/**
	 * Add a word to the sorted list.
	 * @param words
	 * @param word
	 */
	private void addAlphabetical(Vector<WordNode> words, WordNode word) {
		int index = Collections.binarySearch(words, word);
		if (index < 0) {
			words.add(-index-1, word);
		}
	}
	
	/**
	 * Remove a word from the sorted list.
	 * @param words
	 * @param word
	 */
	private void removeAlphabetical(Vector<WordNode> words, WordNode word) {
		if(word != null) {
			int index = Collections.binarySearch(words, word);
			if (index >= 0) {
				words.remove(index);
			}
		}
	}

	/**
	 * Put a word into the active or inactive list.
	 * @param word
	 * @param active
	 * @return
	 */
	public WordNode setWordStatus(WordNode word, boolean active) {
		return setWordStatus(word.word,active);
	}
	
	/**
	 * Add a new word but not active.
	 * @param word
	 * @return
	 */
	public WordNode addNewWord(String word) {
		WordNode wordNode = words.get(word);
		
		// Add if there is no word.
		if(wordNode == null) {
			wordNode = new WordNode(word);
			words.put(word, wordNode);
			addAlphabetical(wordsSorted,wordNode);
			wordNode.updatePOS();
		}
		
		return wordNode;
	}
	
	/**
	 * Put a word into the active or inactive list.
	 * @param word
	 * @param active
	 * @return
	 */
	public WordNode setWordStatus(String word, boolean active) {
		WordNode wordNode = words.get(word);
		
		// Add if there is no word.
		if(wordNode == null) {
			wordNode = new WordNode(word);
			words.put(word, wordNode);
			addAlphabetical(wordsSorted,wordNode);
			wordNode.updatePOS();
		}
		
		// Add to active list.
		if(active) {
			// If not already there.
			if(!activeWords.containsKey(word)) {
				activeWords.put(word, wordNode);
				addAlphabetical(activeWordsSorted,wordNode);
			}
		} else {
			// Only remove if it's there.
			if(activeWords.containsKey(word)) {
				activeWords.remove(word);
				removeAlphabetical(activeWordsSorted,wordNode);
			}
		}
		
		return wordNode;
	}
	
	/**
	 * Get the wordNode for a word.
	 * @param word
	 * @param count
	 * @return
	 */
	public WordNode getWord(String word, int count) {
		return words.get(word);
	}

	/**
	 * Draw a curved line.
	 * @param g
	 * @param p1
	 * @param p2
	 * @param bow
	 * @param width
	 * @param color
	 */
	public void curvedLine(Graphics2D g, Point p1, Point p2, int bow, int width, Color color) {
		QuadCurve2D q = new QuadCurve2D.Float();
		
		double diffX = (p1.x - p2.x);
		double diffY = (p1.y - p2.y);
		double len = Math.sqrt(diffX * diffX + diffY * diffY);
		double scaleX = diffX / len;
		double scaleY = diffY / len;
		double normX = -scaleY;
		double normY = scaleX;
		
		double ctrlx = (p1.x + p2.x) / 2 + normX * bow;
		double ctrly = (p1.y + p2.y) / 2 + normY * bow;
		
		g.setStroke(new BasicStroke(width));
		g.setColor(color);
		q.setCurve(p1.x, p1.y, ctrlx, ctrly, p2.x, p2.y);
		g.draw(q);
	}
	
	/**
	 * Get a combo box with a list of active comparators.
	 * Self updating.
	 * @return
	 */
	public JComboBox getComboComparators() {
		final JComboBox combo = new JComboBox();
		for(WordRelator relator : activeRelations) {
			combo.addItem(relator);
		}
		
		relationChange.add(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				combo.removeAllItems();
				for(WordRelator relator : activeRelations) {
					combo.addItem(relator);
				}
			}
		});
		return combo;
	}
	
	/**
	 * Draw a curved arrow of a given type.
	 * @param g
	 * @param p1
	 * @param p2
	 * @param bow
	 * @param width
	 * @param color
	 * @param backUp1
	 * @param backUp2
	 * @param end
	 */
	public void curvedArrow(Graphics2D g, Point p1, Point p2, int bow, int width, Color color, int backUp1, int backUp2, int end) {
		QuadCurve2D q = new QuadCurve2D.Float();
			
		double diffX = (p1.x - p2.x);
		double diffY = (p1.y - p2.y);
		double len = Math.sqrt(diffX * diffX + diffY * diffY);
		double scaleX = diffX / len;
		double scaleY = diffY / len;
		double normX = -scaleY;
		double normY = scaleX;
		
		double startX = p1.x - backUp1 * scaleX;
		double startY = p1.y - backUp1 * scaleY;
		
		double stopX = p2.x + backUp2 * scaleX;
		double stopY = p2.y + backUp2 * scaleY;
		
		double ctrlx = (p1.x + p2.x) / 2 + normX * bow;
		double ctrly = (p1.y + p2.y) / 2 + normY * bow;
		
		g.setStroke(new BasicStroke(width));
		g.setColor(color);
		q.setCurve(startX, startY, ctrlx, ctrly, stopX, stopY);
		g.draw(q);
		
		if(end == Options.BI_DIRECTIONAL_ARROW) {
			g.drawLine((int)stopX, (int)stopY, (int)(stopX + normX * 5 + scaleX * 10), (int)(stopY + normY * 5 + scaleY * 10));
			g.drawLine((int)stopX, (int)stopY, (int)(stopX - normX * 5 + scaleX * 10), (int)(stopY - normY * 5 + scaleY * 10));
		} else if(end == Options.BI_DIRECTIONAL_DOT_END) {
			g.fillOval((int)(stopX-3), (int)(stopY-3), 6, 6);
		} else {
			double ax = (p1.x + p2.x) / 2 + normX * bow / 2;
			double ay = (p1.y + p2.y) / 2 + normY * bow / 2;
			
			g.drawLine((int)ax, (int)ay, (int)(ax + normX * 5 + scaleX * 10), (int)(ay + normY * 5 + scaleY * 10));
			g.drawLine((int)ax, (int)ay, (int)(ax - normX * 5 + scaleX * 10), (int)(ay - normY * 5 + scaleY * 10));
		}
	}
	
	/**
	 * Draw all connections between two word nodes.
	 * @param g
	 * @param word1
	 * @param word2
	 * @param options
	 * @param overrideColor
	 */
	public void renderConnection(Graphics2D g, WordNode word1, WordNode word2, Options options, Color overrideColor) {

		int relatorID = -1;

		
		for(int i=0;i<activeRelations.size();i++) {
			WordRelator relator = activeRelations.get(i);
			
			relatorID ++;
			
			Color color;
			if(overrideColor == null) {
				color = relator.color;
			} else {
				color = overrideColor;
			}
			
			double dist = relator.getDistance(word1.word, word2.word);
			if(dist > relator.min) {
				dist = Math.min(dist, relator.max); 
				
				switch(options.biDirectionalType) {
					case Options.BI_DIRECTIONAL_NONE:
						curvedLine(g,	word1.getLocation(),						// Start
										word2.getLocation(),						// Stop
										(10 + relatorID * 10),				// Curve
										(int)(dist * 3 * relator.scaller),	// Width
										color);								// Color
					break;
					case Options.BI_DIRECTIONAL_ARROW:
						curvedArrow(g,	word1.getLocation(),						// Start
										word2.getLocation(),						// Stop
										(10 + relatorID * 10),				// Curve
										(int)(dist * 3 * relator.scaller),	// Width
										color, 								// Color
										word1.getRadius(options) + 5, 		// Start radius
										word2.getRadius(options) + 5, 		// Stop radius
										Options.BI_DIRECTIONAL_ARROW);		// Type
					break;
					case Options.BI_DIRECTIONAL_TOP_BOTTOM:
						curvedLine(g,
								new Point(word1.getX(), word1.getY() + word1.getRadius(options)),	// Start
								new Point(word2.getX(), word2.getY() - word2.getRadius(options)),	// Stop
								(10 + relatorID * 10),														// Curve
								(int)(dist * 3 * relator.scaller),											// Width
								relator.color);																// Color
					break;
					case Options.BI_DIRECTIONAL_ARROW_MID:
						curvedArrow(g,	word1.getLocation(),						// Start
										word2.getLocation(),						// Stop
										(10 + relatorID * 10),				// Curve
										(int)(dist * 3 * relator.scaller),	// Width
										color, 								// Color
										word1.getRadius(options) + 5, 		// Start radius
										word2.getRadius(options) + 5, 		// Stop radius
										Options.BI_DIRECTIONAL_ARROW_MID);	// Type
					break;
					case Options.BI_DIRECTIONAL_DOT_END:
						curvedArrow(g,	word1.getLocation(),						// Start
										word2.getLocation(),						// Stop
										(10 + relatorID * 10),				// Curve
										(int)(dist * 3 * relator.scaller),	// Width
										color, 								// Color
										word1.getRadius(options) + 5,		// Start radius
										word2.getRadius(options) + 5,		// Stop radius
										Options.BI_DIRECTIONAL_DOT_END);	// Type
					break;
					case Options.BI_DIRECTIONAL_SPIKE:
						
						double width = dist * 3 * relator.scaller;
						
						Point p1 = word1.getLocation();
						Point p2 = word2.getLocation();
						int bow = (10 + relatorID * 10);
						double backUp = word2.getRadius(options) + 5;
																
						double dx = (p1.x - p2.x);
						double dy = (p1.y - p2.y);
						double len = Math.sqrt(dx * dx + dy * dy);
						double scaleX = dx / len;
						double scaleY = dy / len;
						double normX = -scaleY;
						double normY = scaleX;
		
						double start1X = p1.x + normX * width;
						double start1Y = p1.y + normY * width;
						double start2X = p1.x - normX * width;
						double start2Y = p1.y - normY * width;
						
						double stopX = p2.x + backUp * scaleX;
						double stopY = p2.y + backUp * scaleY;
						
						double ctrl1x = (start1X + p2.x) / 2 + normX * bow;
						double ctrl1y = (start1Y + p2.y) / 2 + normY * bow;
						
						double ctrl2x = (start2X + p2.x) / 2 + normX * bow;
						double ctrl2y = (start2Y + p2.y) / 2 + normY * bow;
						
						GeneralPath path = new GeneralPath();
						QuadCurve2D.Double line1 = new QuadCurve2D.Double(start1X, start1Y, ctrl1x, ctrl1y, stopX, stopY);
						QuadCurve2D.Double line2 = new QuadCurve2D.Double(stopX, stopY, ctrl2x, ctrl2y, start2X, start2Y);
						path.append(line1, false);
						path.append(line2, true);
						
						g.setStroke(new BasicStroke(1));
						g.setColor(color);
						g.fill(path);
						
					break;
				}
			}
		}		
	}
	
	/**
	 * Render all nodes out from a given word.
	 * @param g
	 * @param options
	 * @param word1
	 * @param overrideColor
	 */
	public void renderWordConnections(Graphics2D g, Options options, WordNode word1, Color overrideColor) {
		for(WordNode word2 : activeWords.values()) {
			if(word1 != word2) {
				renderConnection(g, word1, word2, options, overrideColor);
			}
		}
	}
		
	/**
	 * Render all connections and nodes.	
	 * @param g
	 * @param d
	 * @param forceRender
	 * @param progressBar
	 * @param options
	 */
	public void render(Graphics2D g, Dimension d, boolean forceRender, JProgressBar progressBar, Options options) {
				
		int numToRender = activeWords.size() * activeWords.size() * activeRelations.size();
		
		if(progressBar != null) {
			progressBar.setMinimum(0);
			progressBar.setMaximum(activeWords.size() * activeWords.size() + 1);
		}
		
		if(numToRender < 10000 || forceRender) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		
		int rendered = 0;
				
		if(numToRender < 100000 || forceRender) {
			for(WordNode word1 : activeWords.values()) {
				
				renderWordConnections(g, options, word1, null);
				rendered += activeWords.size();
				
				if(progressBar != null && numToRender > 100000) {
					progressBar.setValue(rendered);
				}	
			}
			progressBar.setValue(rendered + 1);
		}

		FontMetrics fm = g.getFontMetrics();
		g.setStroke(new BasicStroke(1));

		for(WordNode word1 : activeWords.values()) {
			int radius = word1.getRadius(options);
			
			
			if(word1.img != null) {
				g.drawImage(word1.img, word1.getX() - radius, word1.getY() - radius, radius * 2, radius * 2, null);
				g.setColor(Color.BLACK);
				g.drawRect(word1.getX()-radius, word1.getY()-radius, radius*2, radius*2);
			} else {
				g.setColor(word1.getColor(options));			
				g.fillOval(word1.getX()-radius, word1.getY()-radius, radius*2, radius*2);
				
				g.setColor(Color.BLACK);			
				g.drawOval(word1.getX()-radius, word1.getY()-radius, radius*2, radius*2);
			}
		}
		
		if(options.labelType != Options.LABEL_NONE) {
			for(WordNode word1 : activeWords.values()) {
				int width = fm.stringWidth(word1.word) + 6;
				int height = fm.getHeight();
	
	
				int radius = word1.getRadius(options);
				int textX = word1.getX() + radius;
				int textY = word1.getY() + radius;
				
				if(options.labelType == Options.LABEL_FULL) {
					g.setColor(Color.WHITE);
					g.fillRect(textX, textY, width, height);
				
					g.setColor(Color.BLACK);
					g.drawRect(textX, textY, width, height);
				}
				
				g.setColor(Color.BLACK);
				g.drawString(word1.word, textX + 3, textY + fm.getAscent());
			}
		}
		
		if(numToRender >= 100000 && !forceRender) {
			
			g.setFont(new Font(g.getFont().getName(),Font.PLAIN,20));
			fm = g.getFontMetrics();
			
			String toRender = "There are " + formatStringComma(numToRender) + " connections.";
			String toRender2 = "This will take too long to render in real time.";
			
			int width = Math.max(fm.stringWidth(toRender), fm.stringWidth(toRender2)) + 10;
			int height = fm.getHeight() * 2 + 5;

			int x = (d.width - width) / 2;
			int y = (d.height - height) / 2;
			
			g.setColor(new Color(255,255,50,100));
			g.fillRoundRect(x, y, width, height,16,16);
			g.setColor(new Color(0,0,0,100));
			g.drawRoundRect(x, y, width, height,16,16);
			g.setColor(new Color(0,0,0,100));
			g.drawString(toRender, x + (width - fm.stringWidth(toRender)) / 2, y + fm.getHeight());
			g.drawString(toRender2, x + (width - fm.stringWidth(toRender2)) / 2, y + fm.getHeight() * 2);
		}
	}
	
	/**
	 * Add commas for every three places.
	 * @param val
	 * @return
	 */
	public static String formatStringComma(int val) {
		String ret = "";
		while(val >= 1000) {
			ret = ("," + fomatString1000(val % 1000)) + ret;
			val /= 1000;
		}
		ret = val + ret;
		return ret;
	}
	
	/**
	 * Pad a string with zeros so it has 3 places.
	 * @param val
	 * @return
	 */
	public static String fomatString1000(int val) {
		if(val < 10) {
			return "00" + val;
		} else if(val < 100) {
			return "0" + val;
		} else {
			return "" + val;
		}
	}
	
	
	
}
