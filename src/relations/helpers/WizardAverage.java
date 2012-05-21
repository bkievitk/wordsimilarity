package relations.helpers;

import gui.WordMap;

import javax.swing.JComboBox;

import relations.WordRelator;
import tools.PanelTools;
import wizard.PanelIntro;
import wizard.PanelRelation;
import wizard.Wizard;

public class WizardAverage extends PanelRelation {

	private static final long serialVersionUID = 8116740988211880419L;

	public WordRelatorAverage relator;
	public WordMap wordMap;
	private JComboBox comparator1;
	private JComboBox comparator2;
	
	public WizardAverage(Wizard wizard) {
		super(wizard, WordRelatorAverage.typeName);
		
		relator = new WordRelatorAverage(defaultColor,"",wizard.wordMap);
		
		this.wordMap = wizard.wordMap;

		addDefaults();
		
		scrollWindow.add(PanelTools.wrappingText("Select the first comparator to make a clone of."));
		comparator1 = wordMap.getComboComparators();
		scrollWindow.add(comparator1);
		
		scrollWindow.add(PanelTools.wrappingText("Select the second comparator to make a clone of."));
		comparator2 = wordMap.getComboComparators();
		scrollWindow.add(comparator2);
		
	}

	public boolean canFinish() {
		return (comparator1.getSelectedItem() != null) && (comparator2.getSelectedItem() != null);
	}
	
	public String getTitle() {
		return "Relation Manager";
	}

	public String getSubTitle() {
		return "Average relation";
	}

	public String nextPanel() {
		return null;
	}
	
	public WordRelator getRelator() {
		return relator;
	}

	public String previousPanel() {
		return PanelIntro.name;
	}

	public void finish() {
		WordRelator relator1 = (WordRelator)comparator1.getSelectedItem();
		WordRelator relator2 = (WordRelator)comparator2.getSelectedItem();
		
		if((relator1 != null) && (relator2 != null)) {
			relator.relator1 = relator1;
			relator.relator2 = relator2;
			wizard.wordMap.setRelatorStatus(getRelator(),true);
		}
	}


}
