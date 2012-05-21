package relations.holoc;

import gui.WordMap;
import relations.WordRelator;
import wizard.PanelData;
import wizard.PanelIntro;
import wizard.PanelRelation;
import wizard.Wizard;

public class WizardContinuousHolographic extends PanelRelation {

	private static final long serialVersionUID = -8810024312310213842L;
	
	public WordRelatorContinuousHolographic relator;
	public WordMap wordMap;
	
	@SuppressWarnings("serial")
	public WizardContinuousHolographic(Wizard wizard) {
		super(wizard, WordRelatorContinuousHolographic.typeName);
		
		relator = new WordRelatorContinuousHolographic(defaultColor,"", wizard.wordMap);
		
		this.wordMap = wizard.wordMap;

		addDefaults();
		addCleaners(relator.cleaners);
		
		final PanelData panelData = new PanelData(wizard, relator, true, WordRelatorContinuousHolographic.typeName + "_DATA") {
			
			public String getSubTitle() {
				return "Teach " + WordRelatorContinuousHolographic.typeName;
			}

			public String previousPanel() {
				return WordRelatorContinuousHolographic.typeName;
			}	
			
			public void finish() {
				super.finish();
			}
			
			public boolean canFinish() {
				return true;
			}
		};
		wizard.panels.put(WordRelatorContinuousHolographic.typeName + "_DATA", panelData);
	}

	public boolean canFinish() {
		return false;
	}

	public String getSubTitle() {
		return WordRelatorContinuousHolographic.typeName;
	}

	public String getTitle() {
		return "Relation Manager";
	}	

	public String nextPanel() {
		return WordRelatorContinuousHolographic.typeName + "_DATA";
	}

	public String previousPanel() {
		return PanelIntro.name;
	}
	
	public WordRelator getRelator() {
		return relator;
	}

}
