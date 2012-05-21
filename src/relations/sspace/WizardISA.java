package relations.sspace;

import java.awt.Color;

import relations.WordRelator;
import wizard.PanelData;
import wizard.PanelIntro;
import wizard.PanelRelation;
import wizard.Wizard;

@SuppressWarnings("serial")
public class WizardISA extends PanelRelation {

	public WordRelatorISA relator;
	
	public WizardISA(Wizard wizard) {
		super(wizard, WordRelatorISA.typeName);
		
		relator = new WordRelatorISA(Color.BLACK, null, wizard.wordMap);
		
		addDefaults();
		this.addCleaners(relator.cleaners);
		
		final PanelData panelData = new PanelData(wizard, relator, true, WordRelatorISA.typeName + "_DATA") {
			
			public String getSubTitle() {
				return "Teach " + WordRelatorISA.typeName;
			}

			public String previousPanel() {
				return WordRelatorISA.typeName;
			}	
			
			public void finish() {
				relator.finalizeSpace();
				super.finish();
			}
			
			public boolean canFinish() {
				return true;
			}
		};
		wizard.panels.put(WordRelatorISA.typeName + "_DATA", panelData);

	}

	public WordRelator getRelator() {
		return relator;
	}

	public boolean canFinish() {
		return false;
	}

	public String getSubTitle() {
		return WordRelatorISA.typeName;
	}

	public String getTitle() {
		return "Relation Manager";
	}

	public String nextPanel() {
		return WordRelatorISA.typeName + "_DATA";
	}

	public String previousPanel() {
		return PanelIntro.name;
	}
}
