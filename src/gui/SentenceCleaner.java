package gui;

import gui.simple.GUISimple;

import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.*;

import javax.swing.*;

import tools.StemmerPorter;

/**
 * Tools to clean up a line.
 * @author bkievitk
 */

public abstract class SentenceCleaner {
	
	public abstract String clean(String sentence);
	
	public static final Vector<SentenceCleaner> cleaners = getCleaners();
	public static final HashSet<String> stopList = loadStopList();

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
				sentence = sentence.replaceAll("[^a-zA-Z.?! ]", "");
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
				for(String word : words) {
					newSentence += StemmerPorter.stem(word) + " ";
				}
				return newSentence;
			}
			
			public String toString() {
				return "Apply Porter Stemmer";
			}
		});
		
		
		return cleaners;
	}
	
	public static HashSet<String> loadStopList() {
		HashSet<String> stopList = new HashSet<String>();
		
		InputStream input = null;

		// Deal with load from multiple sources later.
		try {
			URL url = GUISimple.class.getResource("/stop.stp");
			if(url != null) {
				try {
					input = url.openStream();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {			
				input = new FileInputStream(new File("resources/stop.stp"));
			}
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
		}
		
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(input));
			String line;
			while((line = r.readLine()) != null) {
				stopList.add(line.trim());
			}
		} catch (IOException e) {
			//e.printStackTrace();
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
