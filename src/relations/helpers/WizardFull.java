package relations.helpers;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import relations.WordRelator;
import tools.PanelTools;
import tools.SimpleDictionaryGUI;
import wizard.PanelIntro;
import wizard.PanelRelation;
import wizard.Wizard;

@SuppressWarnings("serial")
public class WizardFull extends PanelRelation {
	
	public WordRelatorFull relator;
	public HashSet<String> words;
	public JCheckBox allWords;
	public SimpleDictionaryGUI gui;
	
	public WizardFull(Wizard wizard) {
		super(wizard, WordRelatorFull.typeName);
		
		relator = new WordRelatorFull(defaultColor,"", wizard.wordMap);
		
		addDefaults();
		
		TitledBorder title;
		
		JPanel weightPanel = new JPanel(new BorderLayout());
		title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Select Connection Weight");
		title.setTitleJustification(TitledBorder.LEFT);
		weightPanel.setBorder(title);
		
			Hashtable<Integer,Component> labels = new Hashtable<Integer,Component>();
			labels.put(0,new JLabel("%0"));
			labels.put(50,new JLabel("%50"));
			labels.put(100,new JLabel("%100"));
			final JSlider slider = new JSlider();
			slider.setMinimum(0);
			slider.setMaximum(100);
			slider.setLabelTable(labels);
			slider.setPaintLabels(true);
			weightPanel.add(slider,BorderLayout.CENTER);
			slider.setValue((int)(relator.value* 100));
			slider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					relator.value = slider.getValue() / 100.0;
				}
			});
			
		scrollWindow.add(weightPanel);				
		
		JPanel wordPanel = new JPanel(new BorderLayout());
		title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Select Words In Set");
		title.setTitleJustification(TitledBorder.LEFT);
		wordPanel.setBorder(title);
		
			words = new HashSet<String>();
			gui = new SimpleDictionaryGUI(words);
			wordPanel.add(gui,BorderLayout.CENTER);
			
			allWords = new JCheckBox("Select this to set this connection weight for all words.");
			wordPanel.add(PanelTools.addLabel("       Otherwise select words to connect.", allWords, BorderLayout.SOUTH),BorderLayout.NORTH);
			allWords.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if(allWords.isSelected()) {
						gui.setEnabled(false);
						relator.words = null;
					} else {
						gui.setEnabled(true);
						relator.words = words;
					}
				}
			});
			allWords.setSelected(true);
			gui.setEnabled(false);
			relator.words = null;
		
		scrollWindow.add(wordPanel);
		
	}

	public WordRelator getRelator() {
		return relator;
	}
	
	public String nextPanel() {
		return null;
	}

	public String previousPanel() {
		return PanelIntro.name;
	}

	public boolean canFinish() {
		return true;
	}

	public String getTitle() {
		return "Relation Manager";
	}

	public String getSubTitle() {
		return "Full Connection Relation";
	}	
}
