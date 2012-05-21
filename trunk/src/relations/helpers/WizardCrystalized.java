package relations.helpers;

import javax.swing.JComboBox;

import gui.WordMap;
import relations.WordRelator;
import tools.PanelTools;
import wizard.PanelIntro;
import wizard.PanelRelation;
import wizard.Wizard;

public class WizardCrystalized extends PanelRelation {

	private static final long serialVersionUID = -6940526022839074961L;
	
	public WordRelationCrystalized relator;
	public JComboBox comparators;
	public WordMap wordMap;
	
	public WizardCrystalized(Wizard wizard) {
		super(wizard, WordRelationCrystalized.typeName);
		
		relator = new WordRelationCrystalized(defaultColor,"",wizard.wordMap);
		
		this.wordMap = wizard.wordMap;

		addDefaults();
		
		scrollWindow.add(PanelTools.wrappingText("Select the comparator to make a clone of."));
		
		comparators = wordMap.getComboComparators();
		scrollWindow.add(comparators);
		
		
		//comparators.addActionListener(new ActionListener() {
		//	public void actionPerformed(ActionEvent arg0) {
		//	}
		//});
	}

	public boolean canFinish() {
		return comparators.getSelectedItem() != null;
	}

	public String getTitle() {
		return "Relation Manager";
	}

	public String getSubTitle() {
		return "Crystalized Connection Relation";
	}	

	public String nextPanel() {
		return null;
	}

	public String previousPanel() {
		return PanelIntro.name;
	}
	
	public WordRelator getRelator() {
		return relator;
	}
	
	public void finish() {
		WordRelator toCrystalize = (WordRelator)comparators.getSelectedItem();
		if(toCrystalize != null) {
			relator.compute(toCrystalize, wordMap.activeWords.keySet());
			wizard.wordMap.setRelatorStatus(getRelator(),true);
			wizard.wordMap.addRelatorWords(getRelator());
		}
	}

}
