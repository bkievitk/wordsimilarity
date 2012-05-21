package relations.sspace;

import java.awt.Color;

import relations.WordRelator;
import wizard.PanelData;
import wizard.PanelIntro;
import wizard.PanelRelation;
import wizard.Wizard;

@SuppressWarnings("serial")
public class WizardLSA extends PanelRelation {

	public WordRelatorLSA relator;
	
	public WizardLSA(Wizard wizard) {
		super(wizard, WordRelatorLSA.typeName);
		
		relator = new WordRelatorLSA(Color.BLACK, null, wizard.wordMap);
		
		addDefaults();
		this.addCleaners(relator.cleaners);
		
		final PanelData panelData = new PanelData(wizard, relator, true, WordRelatorLSA.typeName + "_DATA") {
			
			public String getSubTitle() {
				return "Teach " + WordRelatorLSA.typeName;
			}

			public String previousPanel() {
				return WordRelatorLSA.typeName;
			}	
			
			public void finish() {
				relator.finalizeSpace();
				super.finish();
			}
			
			public boolean canFinish() {
				return true;
			}
		};
		wizard.panels.put(WordRelatorLSA.typeName + "_DATA", panelData);

	}

	public WordRelator getRelator() {
		return relator;
	}

	public boolean canFinish() {
		return false;
	}

	public String getSubTitle() {
		return WordRelatorLSA.typeName;
	}

	public String getTitle() {
		return "Relation Manager";
	}

	public String nextPanel() {
		return WordRelatorLSA.typeName + "_DATA";
	}

	public String previousPanel() {
		return PanelIntro.name;
	}
}
