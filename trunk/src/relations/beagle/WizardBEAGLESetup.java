package relations.beagle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import tools.PanelTools;
import wizard.PanelIntro;
import wizard.Wizard;
import wizard.WizardPanel;

public class WizardBEAGLESetup extends WizardPanel {

	private static final long serialVersionUID = 1648715599721063533L;
	public int finalDim = -1;
	
	public WizardBEAGLESetup(final Wizard wizard) {
		super(wizard, WordRelatorBEAGLE.typeName);
		
		setLayout(new BorderLayout());
		
		JPanel dimensionPanel = new JPanel(new BorderLayout());
		TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Select Dimensions");
		title.setTitleJustification(TitledBorder.LEFT);
		dimensionPanel.setBorder(title);

			JTextArea description = PanelTools.wrappingText("Select dimensionality of the BEAGLE vectors. A higher dimensionality will take more space but provide better results. The dimension must be set first and kept for the lifespan of this relator. Suggested selection between 100 and 300 (hard limit 2 to 2000).");
			final JTextField dimensionField = new JTextField();
			final JButton setDimension = new JButton("set");
			
			JPanel dimInnerPanel = new JPanel(new BorderLayout());
			dimInnerPanel.add(dimensionField,BorderLayout.CENTER);
			dimInnerPanel.add(setDimension,BorderLayout.WEST);
			dimensionPanel.add(description,BorderLayout.CENTER);
			dimensionPanel.add(dimInnerPanel,BorderLayout.SOUTH);
			
			setDimension.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					try {
						int dim = Integer.parseInt(dimensionField.getText());
												
						if(dim > 2 && dim <= 2000) {
							
							if(finalDim >= 0) {
								
								//Custom button text
								Object[] options = {"No","Yes, Reset Settings"};
								int n = JOptionPane.showOptionDialog(null,
									"By changing the dimension, you will\n" +
								    "erase all settings you have made for\n" +
								    "this relation tool. Continue?",
								    "Confirm Change Dimension",
								    JOptionPane.YES_NO_OPTION,
								    JOptionPane.WARNING_MESSAGE,
								    null,
								    options,
								    options[0]);
								if(n != 1) {
									dimensionField.setText(finalDim + "");
									return;
								}
							}
							
							finalDim = dim;
							wizard.next.setEnabled(true);
							setDimension.setText("reset");

							WordRelatorBEAGLE beagle = new WordRelatorBEAGLE(Color.BLACK, null, dim, wizard.wordMap);
							
							WizardBEAGLEOptions panelBEAGLE = new WizardBEAGLEOptions(wizard, beagle);
							wizard.panels.put(WordRelatorBEAGLE.typeName + "_OPTIONS", panelBEAGLE);

							WizardBEAGLEData panelBEAGLEData = new WizardBEAGLEData(wizard, beagle);
							wizard.panels.put(WordRelatorBEAGLE.typeName + "_DATA", panelBEAGLEData);
							
							invalidate();
							validate();
							repaint();
						}
					} catch(NumberFormatException e) {
					}
				}
			});
			
		add(dimensionPanel,BorderLayout.NORTH);
	}

	public boolean canFinish() {
		return false;
	}

	public String getSubTitle() {
		return "BEAGLE Relation Dimensions";
	}

	public String getTitle() {
		return "Relation Manager";
	}

	public String nextPanel() {
		if(finalDim >= 0) {
			return WordRelatorBEAGLE.typeName + "_OPTIONS";
		} else {
			return null;
		}
	}

	public String previousPanel() {
		return PanelIntro.name;
	}

}
