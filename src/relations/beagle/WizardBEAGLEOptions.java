package relations.beagle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;

import relations.WordRelator;
import tools.PanelTools;
import tools.SimpleDictionaryGUI;
import wizard.PanelRelation;
import wizard.Wizard;

public class WizardBEAGLEOptions extends PanelRelation {

	private static final long serialVersionUID = 1799782741858074153L;

	public static final Color defaultColor = Color.BLACK;
	public WordRelatorBEAGLE relator;
	public int finalDim = -1;

	public WizardBEAGLEOptions(Wizard wizard, final WordRelatorBEAGLE relator) {
		super(wizard, WordRelatorBEAGLE.typeName + "_OPTIONS");
		
		this.relator = relator;
		
		addDefaults();
		addCleaners(relator.cleaners);
		
		TitledBorder title;

		JPanel learningContext = new JPanel(new BorderLayout());
		title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Context Learning");
		title.setTitleJustification(TitledBorder.LEFT);
		learningContext.setBorder(title);
		
			final JCheckBox contextLearning = new JCheckBox("Learn Context Information.");
			contextLearning.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					relator.beagle.options.learnContext = contextLearning.isSelected();
				}
			});
			
			contextLearning.setSelected(true);
			relator.beagle.options.learnContext = true;
			
			learningContext.add(contextLearning,BorderLayout.NORTH);
			learningContext.add(PanelTools.wrappingText("Select this to learn word context. This will not take into account the word ordering but only the relative co-occurance of words."),BorderLayout.CENTER);
			
		scrollWindow.add(learningContext);
		
		JPanel orderContext = new JPanel(new BorderLayout());
		title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Order Learning");
		title.setTitleJustification(TitledBorder.LEFT);
		orderContext.setBorder(title);
		
			final JCheckBox orderLearning = new JCheckBox("Learn Order Information.");
			orderLearning.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					relator.beagle.options.learnOrder = orderLearning.isSelected();
				}
			});
			orderLearning.setSelected(false);
			relator.beagle.options.learnOrder = false;
			
			orderContext.add(orderLearning,BorderLayout.NORTH);
			orderContext.add(PanelTools.wrappingText("Select this to learn word order. This will look at what orer words co-occur in."),BorderLayout.CENTER);
			
			Box orderOptions = Box.createVerticalBox();
			
				Box window = Box.createVerticalBox();
					
					title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Window Size");
					title.setTitleJustification(TitledBorder.LEFT);
					window.setBorder(title);
					
					window.add(PanelTools.wrappingText("Select the window of word relations to look at."));
								
					final JSlider slider = new JSlider(2,10);
						slider.setPaintLabels(true);
						slider.setPaintTicks(true);
						slider.setMinorTickSpacing(1);
						slider.setMajorTickSpacing(1);
						slider.setValue(7);
					
					window.add(slider);
					
					slider.addChangeListener(new ChangeListener() {
						public void stateChanged(ChangeEvent e) {
							relator.beagle.options.windowSize = slider.getValue();
						}				
					});

				orderOptions.add(window);
				
				Box combineOptions = Box.createVerticalBox();
				
					title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Combination Options");
					title.setTitleJustification(TitledBorder.LEFT);
					combineOptions.setBorder(title);

					final JRadioButton convolution = new JRadioButton("Select Convolution.");
					final JRadioButton rmp = new JRadioButton("Select Random Permutation.");
					ButtonGroup group = new ButtonGroup();
					group.add(convolution);
					group.add(rmp);
					
					combineOptions.add(PanelTools.wrappingText("Convolution is a slower operator that is based on the operation of light in a hologram. It merges two vectors into one that can be partially doceded with either original stream."));
					combineOptions.add(convolution);
					combineOptions.add(PanelTools.wrappingText("The Random Permutation Model uses a randomly generated ordering to permute the second of a pair of object before joining them. This permutation can then be used as a key to extract a facsimile of the original."));
					combineOptions.add(rmp);
					
					convolution.setEnabled(true);
					relator.beagle.options.combineOperator = OptionsModule.CONVOLUTION;
					
					ActionListener al = new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							if(convolution.isSelected()) {
								relator.beagle.options.combineOperator = OptionsModule.CONVOLUTION;
							} else if(rmp.isSelected()) {
								relator.beagle.options.combineOperator = OptionsModule.RPM;
							}
						}
					};

					convolution.addActionListener(al);
					rmp.addActionListener(al);
					
								
					
				orderOptions.add(combineOptions);

			orderContext.add(orderOptions,BorderLayout.SOUTH);
			
			
		scrollWindow.add(orderContext);
		
		//public int combineOperator = CONVOLUTION;
		//public int windowSize = 7;

		
		JPanel wordPanel = new JPanel(new BorderLayout());
		title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Select Words To Learn");
		title.setTitleJustification(TitledBorder.LEFT);
		wordPanel.setBorder(title);
		
			final SimpleDictionaryGUI gui = new SimpleDictionaryGUI(relator.beagle.thoughts,relator);
			wordPanel.add(gui,BorderLayout.CENTER);
			
			final JCheckBox allWords = new JCheckBox("Learn all words.");
			wordPanel.add(PanelTools.addLabel("       Otherwise select words to pay attention to.", allWords, BorderLayout.SOUTH),BorderLayout.NORTH);
			allWords.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					relator.beagle.options.autoAddWords = allWords.isSelected();
				}
			});
			allWords.setSelected(true);
			relator.beagle.options.autoAddWords = true;
			
		scrollWindow.add(wordPanel);
		
	}

	public WordRelator getRelator() {
		return relator;
	}
	
	public boolean canFinish() {
		return true;
	}

	public String getSubTitle() {
		return "BEAGLE Relation";
	}

	public String getTitle() {
		return "Relation Manager";
	}

	public String nextPanel() {
		return WordRelatorBEAGLE.typeName + "_DATA";
	}

	public String previousPanel() {
		return WordRelatorBEAGLE.typeName;
	}

}
