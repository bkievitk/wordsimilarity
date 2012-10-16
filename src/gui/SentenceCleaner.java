package gui;

import gui.simple.GUISimple;

import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.*;

import javax.swing.*;

import org.tartarus.snowball.SnowballProgram;
import org.tartarus.snowball.ext.*;


/**
 * Tools to clean up a line.
 * @author bkievitk
 */

public abstract class SentenceCleaner {
	
	public abstract String clean(String sentence);
	
	public static final Vector<SentenceCleaner> cleaners = getCleaners();
	
	private static HashSet<String> stopList;
	private static int stopListLanguage = -1;
	
	private static String symbols;
	private static int symbolsLanguage = -1;
	
	public static SentenceCleaner getCleaner(String name) {
		for(SentenceCleaner c : cleaners) {
			if(c.toString().equals(name)) {
				return c;
			}
		}
		return null;
	}
	
	public static Vector<SentenceCleaner> getCleaners() {
		Vector<SentenceCleaner> cleaners = new Vector<SentenceCleaner>();

		cleaners.add(new SentenceCleaner() {
			public String clean(String sentence) {
				return sentence.toLowerCase();
			}
			
			public String toString() {
				return "To Lower Case.";
			}
		});
		
		cleaners.add(new SentenceCleaner() {
			public String clean(String sentence) {
				if(symbolsLanguage != Options.language) {
					try {
						BufferedReader r = new BufferedReader(new FileReader(new File(Options.getLanguageDir() + "/symbols.txt")));
						symbols = r.readLine();
						r.close();
					} catch(IOException e) {
						e.printStackTrace();
					}
				}
				sentence = sentence.replaceAll("[^ " + symbols + "]", "");
				return sentence;
			}
			
			public String toString() {
				return "Alpha Numeric Only.";
			}
		});
		
		cleaners.add(new SentenceCleaner() {
			public String clean(String sentence) {
				return sentence.replaceAll("<[^>]*>", "");
			}
			
			public String toString() {
				return "Remove Web Tags.";
			}
		});
		
		cleaners.add(new SentenceCleaner() {
			public String clean(String sentence) {
				return sentence.replaceAll("[\t\r\n ]+", " ");
			}
			
			public String toString() {
				return "Remove Excess Whitespace.";
			}
		});
		
		cleaners.add(new SentenceCleaner() {
			public String clean(String sentence) {
				if(stopListLanguage != Options.language) {
					stopList = loadStopList();
					stopListLanguage = Options.language;
				}
				
				String[] words = sentence.split(" +");
				String newSentence = "";
				for(String word : words) {
					if(!stopList.contains(word)) {
						if(newSentence.length() > 0) {
							newSentence += " " + word;
						} else {
							newSentence += word;
						}
					}
				}
				return newSentence;
			}
			
			public String toString() {
				return "Remove stoplisted words.";
			}
		});
		
		cleaners.add(new SentenceCleaner() {
			public String clean(String sentence) {
				
				String[] words = sentence.split(" +");
				String newSentence = "";
				
				SnowballProgram stemmer = null;
				
				switch(Options.language) {
					case Options.Armenian: stemmer = new ArmenianStemmer(); break;
					case Options.Basque: stemmer = new BasqueStemmer(); break;
					case Options.Catalan: stemmer = new CatalanStemmer(); break;
					case Options.Danish: stemmer = new DanishStemmer(); break;
					case Options.Dutch: stemmer = new DutchStemmer(); break;
					case Options.English: stemmer = new EnglishStemmer(); break;
					case Options.Finnish: stemmer = new FinnishStemmer(); break;
					case Options.French: stemmer = new FrenchStemmer(); break;
					case Options.German: stemmer = new GermanStemmer(); break;
					case Options.Hungarian: stemmer = new HungarianStemmer(); break;
					case Options.Irish: stemmer = new IrishStemmer(); break;
					case Options.Italian: stemmer = new ItalianStemmer(); break;
					case Options.Norwegian: stemmer = new NorwegianStemmer(); break;
					case Options.Portuguese: stemmer = new PortugueseStemmer(); break;
					case Options.Romanian: stemmer = new RomanianStemmer(); break;
					case Options.Spanish: stemmer = new SpanishStemmer(); break;
					case Options.Swedish: stemmer = new SwedishStemmer(); break;
					case Options.Turkish: stemmer = new TurkishStemmer(); break;		
				}
				
				for(String word : words) {
					stemmer.setCurrent(word);
					stemmer.stem();
					word = stemmer.getCurrent();
					newSentence += word + " ";
				}
				return newSentence;
			}
			
			public String toString() {
				return "Apply Stemmer";
			}
		});
		
		
		return cleaners;
	}
	
	public static HashSet<String> loadStopList() {
		HashSet<String> stopList = new HashSet<String>();
		
		try {
			
			BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(new File(Options.getLanguageDir() + "/stoplist.stp"))));
			String line;
			while((line = r.readLine()) != null) {
				stopList.add(line.trim());
			}
			r.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		return stopList;
	}
	
	@SuppressWarnings("serial")
	public static JPanel getSentenceCleanerPanel(final LinkedList<SentenceCleaner> selectedCleaners) {
		
		final JTextArea cleanerList = new JTextArea();
		final JComboBox options = new JComboBox();
		final JButton remove = new JButton("Remove");
		final JButton add = new JButton("Add");
		final JButton defaultButton = new JButton("Default");
		
		SpringLayout layout = new SpringLayout();
		JPanel panel = new JPanel(layout) {
			public void setEnabled(boolean b) {
				super.setEnabled(b);
				cleanerList.setEditable(b);
				options.setEditable(b);
				remove.setEnabled(b);
				add.setEnabled(b);
				defaultButton.setEnabled(b);
			}
		};
				
		cleanerList.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(cleanerList);
		panel.add(scrollPane);
		
		for(SentenceCleaner cleaner : cleaners) {
			options.addItem(cleaner);
		}
		panel.add(options);
		
		panel.add(remove);
		
		panel.add(add);
		
		panel.add(defaultButton);
		
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedCleaners.addLast((SentenceCleaner)options.getSelectedItem());
				cleanerList.setText(getList(selectedCleaners));
			}
		});
		
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedCleaners.removeLast();			
				cleanerList.setText(getList(selectedCleaners));
			}
		});
		
		defaultButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				while(selectedCleaners.size() > 0) {
					selectedCleaners.remove();
				}
				selectedCleaners.addLast(getCleaner("Remove Web Tags."));
				selectedCleaners.addLast(getCleaner("To Lower Case."));
				selectedCleaners.addLast(getCleaner("Alpha Numeric Only."));				
				
				cleanerList.setText(getList(selectedCleaners));
			}
		});

		layout.putConstraint(SpringLayout.WEST, remove, 2, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, remove, 2, SpringLayout.NORTH, panel);

		layout.putConstraint(SpringLayout.WEST, add, 2, SpringLayout.EAST, remove);
		layout.putConstraint(SpringLayout.NORTH, add, 2, SpringLayout.NORTH, panel);
		
		layout.putConstraint(SpringLayout.WEST, defaultButton, 2, SpringLayout.EAST, add);
		layout.putConstraint(SpringLayout.NORTH, defaultButton, 2, SpringLayout.NORTH, panel);
		
		layout.putConstraint(SpringLayout.WEST, options, 2, SpringLayout.EAST, defaultButton);
		layout.putConstraint(SpringLayout.NORTH, options, 2, SpringLayout.NORTH, panel);

		layout.putConstraint(SpringLayout.NORTH, scrollPane, 5, SpringLayout.SOUTH, remove);
		layout.putConstraint(SpringLayout.WEST, scrollPane, 2, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, scrollPane, -2, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.SOUTH, scrollPane, -2, SpringLayout.SOUTH, panel);
		
		return panel;
	}
	
	public static String getList(LinkedList<SentenceCleaner> cleaners) {
		String ret = "";
		for(SentenceCleaner cleaner : cleaners) {
			ret += cleaner + "\n";
		}
		return ret;
	}
	
}
