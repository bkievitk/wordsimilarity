package relations;

import gui.SentenceCleaner;
import gui.WordMap;
import gui.WordNode;

import java.awt.*;
import java.io.*;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;

import javax.swing.*;

import tools.ReadFileFormat;
import tools.TASABook;

public abstract class WordRelator implements Serializable {
	
	private static final long serialVersionUID = 8794580866053688456L;
	
	// Line color.
	public Color color;
	
	// Specific name of relator.
	public String name;

	// These clean incoming sentences.
	transient public LinkedList<SentenceCleaner> cleaners = new LinkedList<SentenceCleaner>();
	
	// What range to show.
	public double min = 0;
	public double max = 1;
	
	// Line scaling for visualization.
	public double scaller = 1;
	
	// Track if the realator is finalized.
	private boolean finalized = false;
	
	public static final String ERROR_INITIALIZING = "ERROR_INITIALIZING";
	protected WordMap wordMap;
	
	/**
	 * The core of the relator is a distance metric between two words.
	 * @param word1	First word.
	 * @param word2	Second word.
	 * @return Value in the range of 0 to 1 where 1 is highly similar and 0 is non-similar.
	 */
	public abstract double getDistance(String word1, String word2);	
			
	public WordRelator(Color color, String name, WordMap wordMap) {
		this.name = name;
		this.color = color;	
		this.wordMap = wordMap;
	}
	
	/**
	 * Require a relator description.
	 */
	public abstract String toString();
	
	/**
	 * This is the list of words that the relator knows about.
	 * @return
	 */
	public Set<String> getWords() {
		return null;
	}

	/**
	 * Learn all of the data in a file.
	 * @param f
	 * @return
	 */
	public boolean learn(final File f, final JProgressBar progress, final JLabel label) {

		(new Thread() {				
			public void run() {
			
				// Try to interpret different file formats.
				
				label.setText("Pre-loading file.");
				
				String result = ReadFileFormat.readFile(f);
				if(result != null) {
					
					String[] sentences = result.split("[\r\n]+");
					label.setText("Learning data.");
					progress.setMaximum(sentences.length);
					
					// Split by paragraphs.
					for(int i=0;i<sentences.length;i++) {
						progress.setValue(i);
						learn(sentences[i]);
					}
				}
				progress.setValue(progress.getMaximum());
				
			}
		}).start();
		
		return true;
	}

	/**
	 * Learn from any input stream.
	 * @param input
	 * @return
	 */
	public boolean learn(final InputStream input, final JProgressBar progress, final JLabel label) {
		
			(new Thread() {				
				public void run() {
					
					BufferedReader r = new BufferedReader(new InputStreamReader(input));
					
					final Vector<String> lines = new Vector<String>();
					String line;
								
					label.setText("Downloading web page.");
					progress.setValue(0);
					try {
						while((line = r.readLine()) != null) {
							lines.add(line);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					label.setText("Learning data.");
					progress.setMaximum(lines.size());
					for(int i=0;i<lines.size();i++) {
						progress.setValue(i);
						learn(lines.get(i));
					}
					progress.setValue(progress.getMaximum());
				}
			}).start();
			
			
			return true;
		
	}

	public void learnDocument(String document) {
		for(String paragraph : document.split("[\r\n]+")) {
			learn(paragraph);
		}
	}
	
	/**
	 * Learn a single paragraph.
	 * @param paragraph
	 */
	public synchronized void learn(String paragraph) {
		for(SentenceCleaner cleaner : cleaners) {
			paragraph = cleaner.clean(paragraph);
		}
		
		for(String sentence : paragraph.split("[.?!]")) {
			learn(sentence.split(" +"));
		}
	}
	
	/**
	 * Learn a TASA book.
	 * @param book
	 */
	public void learn(TASABook book) {
		for(String sentence : book.sentences) {
			learn(sentence);
		}
	}
	
	/**
	 * Learn this sentence.
	 * @param sentence
	 */
	public void learn(String[] sentence) {
		for(String word : sentence) {
			WordNode node = wordMap.words.get(word);
			if(node == null) {
				node = wordMap.setWordStatus(word,false);
			}
			node.incrementCount();
		}
	}
	
	/**
	 * Apply the learning that you have done.
	 * Relevant for static algorithms.
	 */
	public void finalizeSpace() {
		finalized = true;
	}
	
	/**
	 * See if the space is finalized.
	 * @return
	 */
	public boolean isFinalized() {
		return finalized;
	}
		
}
