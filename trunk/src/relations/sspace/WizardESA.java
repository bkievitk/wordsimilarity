package relations.sspace;

import java.awt.Color;

import relations.WordRelator;
import wizard.PanelData;
import wizard.PanelIntro;
import wizard.PanelRelation;
import wizard.Wizard;

@SuppressWarnings("serial")
public class WizardESA extends PanelRelation {

	// Hold onto relator.
	public WordRelatorESA relator;
		
	public WizardESA(Wizard wizard) {
		super(wizard, WordRelatorESA.typeName);

		// Create relator.
		relator = new WordRelatorESA(Color.BLACK, null, wizard.wordMap);
		
		// Add default options.
		addDefaults();
		this.addCleaners(relator.cleaners);
				
		// Create data panel.
		final PanelData panelData = new PanelData(wizard, relator, true, WordRelatorESA.typeName + "_DATA") {
			
			public String getSubTitle() {
				return "Teach " + WordRelatorESA.typeName;
			}

			public String previousPanel() {
				return WordRelatorESA.typeName;
			}	
			
			public void finish() {
				relator.finalizeSpace();
				super.finish();
			}
			
			public boolean canFinish() {
				return true;
			}
		};
		wizard.panels.put(WordRelatorESA.typeName + "_DATA", panelData);

	}

	public WordRelator getRelator() {
		return relator;
	}

	public boolean canFinish() {
		return false;
	}

	public String getSubTitle() {
		return WordRelatorESA.typeName;
	}

	public String getTitle() {
		return "Relation Manager";
	}

	public String nextPanel() {
		return WordRelatorESA.typeName + "_DATA";
	}

	public String previousPanel() {
		return PanelIntro.name;
	}
}
