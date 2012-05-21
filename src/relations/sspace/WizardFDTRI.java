package relations.sspace;

import java.awt.Color;

import relations.WordRelator;
import wizard.PanelData;
import wizard.PanelIntro;
import wizard.PanelRelation;
import wizard.Wizard;

@SuppressWarnings("serial")
public class WizardFDTRI extends PanelRelation {

	// Hold onto relator.
	public WordRelatorFDTRI relator;
	
	public WizardFDTRI(Wizard wizard) {
		super(wizard, WordRelatorFDTRI.typeName);
		
		relator = new WordRelatorFDTRI(Color.BLACK, null, wizard.wordMap);
		
		addDefaults();
		this.addCleaners(relator.cleaners);
		
		final PanelData panelData = new PanelData(wizard, relator, true, WordRelatorFDTRI.typeName + "_DATA") {
			
			public String getSubTitle() {
				return "Teach " + WordRelatorFDTRI.typeName;
			}

			public String previousPanel() {
				return WordRelatorFDTRI.typeName;
			}	
			
			public void finish() {
				relator.finalizeSpace();
				super.finish();
			}
			
			public boolean canFinish() {
				return true;
			}
		};
		wizard.panels.put(WordRelatorFDTRI.typeName + "_DATA", panelData);

	}

	public WordRelator getRelator() {
		return relator;
	}

	public boolean canFinish() {
		return false;
	}

	public String getSubTitle() {
		return WordRelatorFDTRI.typeName;
	}

	public String getTitle() {
		return "Relation Manager";
	}

	public String nextPanel() {
		return WordRelatorFDTRI.typeName + "_DATA";
	}

	public String previousPanel() {
		return PanelIntro.name;
	}
}
