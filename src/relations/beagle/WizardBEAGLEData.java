package relations.beagle;

import relations.WordRelator;
import wizard.PanelData;
import wizard.Wizard;

public class WizardBEAGLEData extends PanelData {

	private static final long serialVersionUID = -7393625141736720169L;
	public WordRelatorBEAGLE relator;
	
	public WizardBEAGLEData(Wizard wizard, WordRelator relator) {
		super(wizard,relator,true, WordRelatorBEAGLE.typeName + "_DATA");
		this.relator = (WordRelatorBEAGLE)relator;
	}
	
	public String getSubTitle() {
		return "BEAGLE Relation Data";
	}

	public String previousPanel() {
		return WordRelatorBEAGLE.typeName + "_OPTIONS";
	}
}
