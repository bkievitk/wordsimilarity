package relations.sspace;

import java.awt.Color;

import relations.WordRelator;
import wizard.PanelData;
import wizard.PanelIntro;
import wizard.PanelRelation;
import wizard.Wizard;

@SuppressWarnings("serial")
public class WizardRandomIndexing extends PanelRelation {

	public WordRelatorRandomIndexing relator;
	
	public WizardRandomIndexing(Wizard wizard) {
		super(wizard, WordRelatorRandomIndexing.typeName);
		
		relator = new WordRelatorRandomIndexing(Color.BLACK, null, wizard.wordMap);
		
		addDefaults();
		this.addCleaners(relator.cleaners);
		
		final PanelData panelData = new PanelData(wizard, relator, true, WordRelatorRandomIndexing.typeName + "_DATA") {
			
			public String getSubTitle() {
				return "Teach " + WordRelatorRandomIndexing.typeName;
			}

			public String previousPanel() {
				return WordRelatorRandomIndexing.typeName;
			}	
			
			public void finish() {
				relator.finalizeSpace();
				super.finish();
			}
			
			public boolean canFinish() {
				return true;
			}
		};
		wizard.panels.put(WordRelatorRandomIndexing.typeName + "_DATA", panelData);

	}

	public WordRelator getRelator() {
		return relator;
	}

	public boolean canFinish() {
		return false;
	}

	public String getSubTitle() {
		return WordRelatorRandomIndexing.typeName;
	}

	public String getTitle() {
		return "Relation Manager";
	}

	public String nextPanel() {
		return WordRelatorRandomIndexing.typeName + "_DATA";
	}

	public String previousPanel() {
		return PanelIntro.name;
	}
}
