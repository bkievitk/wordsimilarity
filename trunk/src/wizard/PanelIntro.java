package wizard;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import tools.PanelTools;
import tools.VerticalLayout;

@SuppressWarnings("serial")
public class PanelIntro extends WizardPanel {
	
	public static String name = "INTRO";
	
	private String nextPanel = null;
	
	public PanelIntro(final Wizard wizard) {
		super(wizard, name);

		TitledBorder title;
		
		JPanel scrolling = new JPanel(new VerticalLayout(5,5));
		setLayout(new BorderLayout());
		JScrollPane scroll = new JScrollPane(scrolling,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scroll);
		
		ButtonGroup buttons = new ButtonGroup();
		
		
		for(int i=0;i<Wizard.relationTypes.getRelationCount();i++) {
			
			// Create panel.
			JPanel panel = new JPanel(new BorderLayout());
			
			// Add description.
			JTextArea panelText = PanelTools.wrappingText(Wizard.relationTypes.getDescription(i));
			panel.add(panelText,BorderLayout.CENTER);				
			
			// Add selection button.
			final JRadioButton panelButton = new JRadioButton("Select " + Wizard.relationTypes.getName(i) + " Relation");
			panel.add(panelButton,BorderLayout.SOUTH);
			buttons.add(panelButton);
			
			// Add title.
			title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), Wizard.relationTypes.getName(i) + " Connection");
			title.setTitleJustification(TitledBorder.LEFT);
			panel.setBorder(title);
			
			scrolling.add(panel);
			
			// Link button.
			final int id = i;
			panelButton.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					if(panelButton.isSelected()) {
						nextPanel = Wizard.relationTypes.getName(id);
						wizard.next.setEnabled(nextPanel != null);
						
					}
				}
			});
			
		}
		
	}
	
	public String nextPanel() {
		return nextPanel;
	}

	public String previousPanel() {
		return null;
	}

	public boolean canFinish() {
		return false;
	}
	
	public String getTitle() {
		return "Relation Manager";		
	}
	
	public String getSubTitle() {
		return "Select the Relation Type";
	}
	
}
