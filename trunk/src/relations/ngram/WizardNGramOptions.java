package relations.ngram;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;

import relations.*;
import tools.PanelTools;
import wizard.PanelData;
import wizard.PanelIntro;
import wizard.PanelRelation;
import wizard.Wizard;

@SuppressWarnings("serial")
public class WizardNGramOptions extends PanelRelation {
	
	public WordRelatorNGram relator;
	
	public WizardNGramOptions(Wizard wizard) {
		super(wizard, WordRelatorNGram.typeName);
		
		relator = new WordRelatorNGram(defaultColor,"",1,wizard.wordMap);
		
		addDefaults();
		
		TitledBorder title;
				
		JPanel orderPanel = new JPanel(new BorderLayout());
		title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Select Relative Word");
		title.setTitleJustification(TitledBorder.LEFT);
		orderPanel.setBorder(title);
		
			JTextArea description = PanelTools.wrappingText("Select the relation of the 'to' word relative to the 'from' word. For example, in the sentence 'the dog ate the bone', the 'from' word is always the first word, or 'the', the 'to' word at index 1 would be 'dog'. Therefore if you set the index to 1, the word 'dog' would have a strong connection with the word 'the'.");
			orderPanel.add(description,BorderLayout.CENTER);
			final JSlider slider = new JSlider(1,5);
			slider.setPaintLabels(true);
			slider.setPaintTicks(true);
			slider.setMinorTickSpacing(1);
			slider.setMajorTickSpacing(1);
			slider.setValue(1);
			orderPanel.add(slider,BorderLayout.SOUTH);
			
			slider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					relator.position = slider.getValue();
				}				
			});
			
		scrollWindow.add(orderPanel);
				
		final PanelData panelData = new PanelData(wizard, relator, true, WordRelatorNGram.typeName + "_DATA") {
			
			public String getSubTitle() {
				return "Teach " + WordRelatorNGram.typeName;
			}

			public String previousPanel() {
				return WordRelatorNGram.typeName;
			}	
			
			public void finish() {
				relator.finalizeSpace();
				super.finish();
			}
			
			public boolean canFinish() {
				return true;
			}
		};
		wizard.panels.put(WordRelatorNGram.typeName + "_DATA", panelData);
		
		
		addCleaners(relator.cleaners);
	}

	public WordRelator getRelator() {
		return relator;
	}
	
	public String nextPanel() {
		return WordRelatorNGram.typeName + "_DATA";
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
		return "NGram Relation";
	}
	
}
