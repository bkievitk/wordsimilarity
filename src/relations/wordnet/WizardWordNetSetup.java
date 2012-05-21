package relations.wordnet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import tools.PanelTools;
import tools.VerticalLayout;
import wizard.PanelIntro;
import wizard.Wizard;
import wizard.WizardPanel;

public class WizardWordNetSetup extends WizardPanel {

	private static final long serialVersionUID = 4028531776365799169L;
		
	public WizardWordNetSetup(final Wizard wizard) {
		super(wizard, WordRelatorWordNet.typeName);
		
		setLayout(new VerticalLayout(5,5));
		
		//http://wordnet.princeton.edu/wordnet/download/
		JPanel loadWordNet = new JPanel(new BorderLayout());
		TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Load WordNet");
		title.setTitleJustification(TitledBorder.LEFT);
		loadWordNet.setBorder(title);
		
			loadWordNet.add(PanelTools.wrappingText("To use a WordNet relator, you must download the WordNet data set. Please download WordNet version 2.1 from http://wordnet.princeton.edu/wordnet/download/. When you have downloaded the data set. Click on the button below to select the root file of your WordNet directory."),BorderLayout.CENTER);
			final JButton button = new JButton("Select WordNet Directory");
			final JLabel errorMessage = new JLabel("Invalid Location");
			errorMessage.setForeground(Color.RED);
			errorMessage.setVisible(false);
			loadWordNet.add(errorMessage,BorderLayout.NORTH);
			
			if(WordRelatorWordNet.isSetJWS()) {
				button.setText("WordNet Loaded");
				button.setEnabled(false);
			}
			
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {						
					if(WordRelatorWordNet.loadWordNet(wizard.wordMap)) {
						errorMessage.setVisible(false);
						button.setText("WordNet Loaded");
						button.setEnabled(false);
						wizard.next.setEnabled(true);						
						invalidate();
						validate();
						repaint();
					} else {
						errorMessage.setVisible(true);
						invalidate();
						validate();
						repaint();						
					}
				}					
			});
			loadWordNet.add(button,BorderLayout.SOUTH);
			
			WizardWordNetOptions panel = new WizardWordNetOptions(wizard);
			wizard.panels.put(WordRelatorWordNet.typeName + "_OPTIONS", panel);
			
		add(loadWordNet);
	}

	public boolean canFinish() {
		return false;
	}

	public String getSubTitle() {
		return "WordNet Loading";
	}

	public String getTitle() {
		return "Relation Manager";
	}

	public String nextPanel() {
		if(!WordRelatorWordNet.isSetJWS()) {
			return null;
		} else {
			return WordRelatorWordNet.typeName;
		}
	}

	public String previousPanel() {
		return PanelIntro.name;
	}

}
