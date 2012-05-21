package gui.simple;

import gui.SentenceCleaner;
import gui.WordMap;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import relations.WordRelator;
import relations.beagle.WordRelatorBEAGLE;
import relations.ngram.WordRelatorNGram;
import relations.wordnet.WordRelatorWordNet;
import tools.VerticalLayout;

public class GUISimpleChooseSimilarity extends JPanel {

	private static final long serialVersionUID = 3692916089246006825L;

	public WordRelator newRelator = null;
	
	public JPanel selectTypePanel = new JPanel(new VerticalLayout(20,20));
	public JPanel finishPanel = new JPanel(new VerticalLayout(20,20));
	
	public JPanel wordNetPanel;
	public JPanel beaglePanel;
	public JPanel nGramPanel;
	
	public static final int TYPE_BEAGLE = 0;
	public static final int TYPE_NGRAM = 1;
	public static final int TYPE_WORDNET = 2;
	public static final int TYPE_NONE = 3;
	
	public int layoutType = TYPE_NONE;
	
	public WordMap wordMap;

	public Vector<JTextArea> messages = new Vector<JTextArea>();
	
	public JToggleButton beagle;
	public JToggleButton nGram;
	public JToggleButton wordNet;
	public JToggleButton none;
	
	public static LinkedList<SentenceCleaner> standardCleaners = getStandardCleaners();
		
	public static LinkedList<SentenceCleaner> getStandardCleaners() {
		LinkedList<SentenceCleaner> cleaners = new LinkedList<SentenceCleaner>();
		cleaners.add(SentenceCleaner.getCleaner("To Lower Case."));
		cleaners.add(SentenceCleaner.getCleaner("Remove Web Tags."));
		cleaners.add(SentenceCleaner.getCleaner("Alpha Numeric Only."));
		return cleaners;
	}
	
	public void writeMessage(String message) {
		for(JTextArea area : messages) {
			area.append(message + "\n");
		}
	}
	
	public void clearMessages() {
		for(JTextArea area : messages) {
			area.setText("");
		}
	}
	
	public void arrangePanels(int type) {
		if(type != layoutType) {
			
			removeAll();
			
			add(selectTypePanel,BorderLayout.WEST);
			add(finishPanel,BorderLayout.EAST);
		
			clearMessages();
			
			switch(type) {
				case TYPE_BEAGLE:
					add(beaglePanel,BorderLayout.CENTER);
					newRelator = new WordRelatorBEAGLE(Color.BLACK, "BEAGLE", 200, wordMap);
					newRelator.cleaners = standardCleaners;
					writeMessage("BEAGLE Similarity Created.");
					break;
				case TYPE_NGRAM:
					add(nGramPanel,BorderLayout.CENTER);
					newRelator = new WordRelatorNGram(Color.BLACK, "NGram", 1, wordMap);
					newRelator.cleaners = standardCleaners;
					writeMessage("NGram Similarity Created.");
					break;
				case TYPE_WORDNET:
					add(wordNetPanel,BorderLayout.CENTER);
					newRelator = new WordRelatorWordNet(Color.BLACK, "Word Net", wordMap);
					newRelator.cleaners = standardCleaners;
					writeMessage("WordNet Similarity Created.");
					break;
				case TYPE_NONE:
					newRelator = null;
					break;
			}
			
			invalidate();
			validate();
			repaint();
			
			layoutType = type;
		}
	}
	
	public void buildSelectPanel() {
		selectTypePanel.setBackground(Color.WHITE);
		
		JLabel label = new JLabel("1a) Select");
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setFont(new Font(label.getFont().getName(),Font.PLAIN,30));
		selectTypePanel.add(label);
		
		beagle = new JToggleButton(GUISimple.loadIcon("similarity_BEAGLE.png"));
		beagle.setBackground(Color.WHITE);
		selectTypePanel.add(beagle);
		
		nGram = new JToggleButton(GUISimple.loadIcon("similarity_NGram.png"));
		nGram.setBackground(Color.WHITE);
		selectTypePanel.add(nGram);		

		wordNet = new JToggleButton(GUISimple.loadIcon("similarity_WordNet.png"));
		wordNet.setBackground(Color.WHITE);
		selectTypePanel.add(wordNet);
				
		ButtonGroup g = new ButtonGroup();
		g.add(beagle);
		g.add(nGram);
		g.add(wordNet);
		none = new JToggleButton();
		g.add(none);
		
		selectTypePanel.setPreferredSize(new Dimension(200,10));
		add(selectTypePanel,BorderLayout.WEST);
		
		beagle.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if(beagle.isSelected()) {
					arrangePanels(TYPE_BEAGLE);
				}
			}
		});
		
		nGram.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if(nGram.isSelected()) {
					arrangePanels(TYPE_NGRAM);
				}
			}
		});
		
		wordNet.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if(wordNet.isSelected()) {
					arrangePanels(TYPE_WORDNET);
				}
			}
		});
	}
	
	public void buildFinishPanel() {
		
		finishPanel.setBackground(Color.WHITE);
		finishPanel.setPreferredSize(new Dimension(250,10));
		
		JLabel finishLabel = new JLabel("   1c) Create   ");
		finishLabel.setHorizontalAlignment(JLabel.CENTER);
		finishLabel.setFont(new Font(finishLabel.getFont().getName(),Font.PLAIN,30));
		finishPanel.add(finishLabel);
		
		JButton finishButton = new JButton("Create");
		finishButton.setBackground(Color.WHITE);
		finishPanel.add(finishButton);

		final JPanel relations = new JPanel(new VerticalLayout(3,3));
		finishPanel.add(relations);
		relations.setPreferredSize(new Dimension(10,400));
		relations.setBackground(Color.WHITE);
		
		wordMap.relationChange.add(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {						
				relations.removeAll();
				
				if(wordMap.activeRelations.size() > 0) {
					relations.add(GUISimple.getLabel("Created Relations",20));
				}
				
				for(WordRelator relator : wordMap.activeRelations) {
					JButton button = new JButton("Remove " + relator.toString());
					button.setBackground(Color.WHITE);
					relations.add(button);
					
					final WordRelator relatorFinal = relator;
					button.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							wordMap.activeRelations.remove(relatorFinal);
							wordMap.relationChanged();
						}
					});
				}

				none.setSelected(true);
				
				finishPanel.invalidate();
				finishPanel.validate();
				finishPanel.repaint();
			}
		});
		
		finishButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {						
				if(newRelator != null) {
					wordMap.setRelatorStatus(newRelator, true);
					arrangePanels(TYPE_NONE);
				}
			}
		});
	}
	
	public void buildWordNetPanel() {
		wordNetPanel = new JPanel(new VerticalLayout(20,20));
		wordNetPanel.setBackground(Color.WHITE);
		JLabel label;
		
		JLabel teachLabel1 = new JLabel("1b) Select");
		teachLabel1.setHorizontalAlignment(JLabel.CENTER);
		teachLabel1.setFont(new Font(teachLabel1.getFont().getName(),Font.PLAIN,30));
		wordNetPanel.add(teachLabel1);
		
		JLabel wordNetLabel = new JLabel("Word Net");
		wordNetLabel.setHorizontalAlignment(JLabel.CENTER);
		wordNetLabel.setFont(new Font(teachLabel1.getFont().getName(),Font.PLAIN,20));
		wordNetPanel.add(wordNetLabel);
		
		label = new JLabel("Choose Word Net type.");
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setFont(new Font(label.getFont().getName(),Font.PLAIN,20));
		wordNetPanel.add(label);
		
		final JComboBox metricOptions = new JComboBox(WordRelatorWordNet.types);
		wordNetPanel.add(metricOptions);
		metricOptions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(newRelator instanceof WordRelatorWordNet) {
					WordRelatorWordNet wordNet = (WordRelatorWordNet)newRelator;
					wordNet.type = metricOptions.getSelectedIndex();
					writeMessage("    WordNet type " + WordRelatorWordNet.types[metricOptions.getSelectedIndex()] + " selected.");
				}
			}
		});
		
		wordNetPanel.add(GUISimple.getLabel("Messages.", 20));		
		JTextArea messages = new JTextArea();
		messages.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		messages.setRows(5);
		messages.setEditable(false);
		this.messages.add(messages);
		wordNetPanel.add(new JScrollPane(messages));
					
	}
		
	public JPanel buildTeachPanel(String name) {
		JPanel learn = new JPanel(new VerticalLayout(20,20));
		

		final JLabel progressLabel = new JLabel("waiting ...");
		final JProgressBar progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		
		learn.setBackground(Color.WHITE);
		
		learn.add(GUISimple.getLabel("1b) Teach", 30));
		learn.add(GUISimple.getLabel(name, 20));
		learn.add(GUISimple.getLabel("Choose website to learn or enter your own.", 20));
	
		String[] urls = {"http://en.wikipedia.org/wiki/Dog","http://en.wikipedia.org/wiki/Cat"};
		final JComboBox selectWeb = new JComboBox(urls);
		selectWeb.setEditable(true);
		learn.add(selectWeb);
		
		JButton teachWeb = new JButton("Click to Teach Website",GUISimple.loadIcon("teach.png"));
		teachWeb.setBackground(Color.WHITE);				
		learn.add(teachWeb);
		
		teachWeb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					newRelator.learn((new URL((String)selectWeb.getSelectedItem()).openStream()), progressBar, progressLabel);
					writeMessage("    Learned website " + selectWeb.getSelectedItem() + ".");
				} catch (MalformedURLException e) {
					writeMessage("    The website " + selectWeb.getSelectedItem() + " is invalid.");
				} catch (IOException e) {
					writeMessage("    Problem reading from " + selectWeb.getSelectedItem() + ".");
				}
			}
		});
		
		learn.add(new JLabel());
		
		learn.add(GUISimple.getLabel("Or enter text to learn.", 20));
		
		final JTextArea text = new JTextArea();
		text.setRows(10);
		text.setEditable(true);
		text.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		learn.add(text);
		
		JButton teachText = new JButton("Click to Teach Text",GUISimple.loadIcon("teach.png"));
		teachText.setBackground(Color.WHITE);				
		learn.add(teachText);
		
		teachText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				newRelator.learn(text.getText());
				writeMessage("    Learned text entered.");
			}
		});
				
		learn.add(GUISimple.getLabel("Messages.", 20));		
		JTextArea messages = new JTextArea();
		messages.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		messages.setRows(5);
		messages.setEditable(false);
		this.messages.add(messages);
		learn.add(new JScrollPane(messages));		

		learn.add(progressLabel);
		learn.add(progressBar);
		
		return learn;
	}
	
	public GUISimpleChooseSimilarity(final WordMap wordMap) {
		this.wordMap = wordMap;
		
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);

		buildSelectPanel();
		buildFinishPanel();
		buildWordNetPanel();
		beaglePanel = buildTeachPanel("BEAGLE");
		nGramPanel = buildTeachPanel("NGram");
		
	}
}
