package wizard;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class WizardPanel extends JPanel {
	
	public String name;
	
	public Wizard wizard;
	
	public WizardPanel(Wizard wizard, String name) {
		this.wizard = wizard;
		this.name = name;
	}
	
	public void finish() {
		
	}
		
	public abstract String nextPanel();
	public abstract String previousPanel();
	public abstract boolean canFinish();
	
	public abstract String getTitle();
	public abstract String getSubTitle();
}
