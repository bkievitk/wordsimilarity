package relations.sspace;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import relations.WordRelator;
import tools.PanelTools;
import tools.VerticalLayout;
import wizard.PanelData;
import wizard.PanelIntro;
import wizard.PanelRelation;
import wizard.Wizard;
import wizard.WizardPanel;

@SuppressWarnings("serial")
public class WizardHAL extends PanelRelation {

	public WordRelatorHAL relator;
	
	// Extra options.
	private WizardPanel panelHALOptions;
	private PanelData panelData;
	
	public WizardHAL(Wizard wizard) {
		super(wizard, WordRelatorHAL.typeName);
		
		relator = new WordRelatorHAL(Color.BLACK, null, wizard.wordMap);
		
		addDefaults();
		this.addCleaners(relator.cleaners);
		
		panelData = new PanelData(wizard, relator, true, WordRelatorHAL.typeName + "_DATA") {
			
			public String getSubTitle() {
				return "Teach " + WordRelatorHAL.typeName;
			}

			public String previousPanel() {
				return panelHALOptions.name;
			}	
			
			public void finish() {
				relator.finalizeSpace();
				super.finish();
			}
			
			public boolean canFinish() {
				return true;
			}
		};
		wizard.panels.put(WordRelatorHAL.typeName + "_DATA", panelData);
		
		panelHALOptions = new WizardPanel(wizard, WordRelatorHAL.typeName + "_OPTIONS") {
			
			public String nextPanel() {
				return WordRelatorHAL.typeName + "_DATA";
			}

			public String previousPanel() {
				return WordRelatorHAL.typeName;
			}

			public boolean canFinish() {
				return false;
			}

			public String getTitle() {
				return "HAL Options";
			}

			public String getSubTitle() {
				return "Set HAL Options";
			}
		};
		
		JPanel windows = new JPanel(new VerticalLayout(5,5));
		
		/*
		 -s, --windowSize=INT how many words to consider in each direction
-r, --retain=INT how many column dimensions to retain in the final word co-occurrence matrix. The retained columns will be those that provide the most information for distinguishing the semantics of the words. Unlike the --threshold option, this specifies a hard limit for how many to retain. This option may not specified at the same time as --threshold
-h, --threshold=DOUBLE the minimum information theoretic entropy a word must have to be retained in the final word co-occurrence matrix. This option may not be used at the same time as --retain. 
		 */
		
		JPanel windowSize = new JPanel(new BorderLayout());
		TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Window Size");
		title.setTitleJustification(TitledBorder.LEFT);
		windowSize.setBorder(title);
		windowSize.add(PanelTools.wrappingText("How many words to consider in each direction."),BorderLayout.CENTER);
			JPanel internalPanel = new JPanel(new BorderLayout());
			final JTextField windowSizeText = new JTextField(WordRelatorHAL.WINDOW_SIZE_START + "");
			windowSizeText.addKeyListener(new KeyListener() {
				public void keyReleased(KeyEvent arg0) {
					try {
						relator.windowSize = Integer.parseInt(windowSizeText.getText());
						windowSizeText.setForeground(Color.BLACK);
					} catch(NumberFormatException e) {
						windowSizeText.setForeground(Color.RED);
					}
				}
				public void keyPressed(KeyEvent arg0) {}
				public void keyTyped(KeyEvent arg0) {}
			});
			internalPanel.add(windowSizeText,BorderLayout.CENTER);
			windowSize.add(internalPanel,BorderLayout.SOUTH);
		windows.add(windowSize);
		
		
		JPanel retainSize = new JPanel(new BorderLayout());
		title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Retain Size");
		title.setTitleJustification(TitledBorder.LEFT);
		retainSize.setBorder(title);
		retainSize.add(PanelTools.wrappingText("How many column dimensions to retain in the final word co-occurrence matrix. The retained columns will be those that provide the most information for distinguishing the semantics of the words. Unlike the --threshold option, this specifies a hard limit for how many to retain."),BorderLayout.CENTER);
			internalPanel = new JPanel(new BorderLayout());
			final JTextField retainSizeText = new JTextField(WordRelatorHAL.RETAIN_SIZE_START + "");
			retainSizeText.addKeyListener(new KeyListener() {
				public void keyReleased(KeyEvent arg0) {
					try {
						relator.retain = Integer.parseInt(retainSizeText.getText());
						retainSizeText.setForeground(Color.BLACK);
					} catch(NumberFormatException e) {
						retainSizeText.setForeground(Color.RED);
					}
				}
				public void keyPressed(KeyEvent arg0) {}
				public void keyTyped(KeyEvent arg0) {}
			});
			internalPanel.add(retainSizeText,BorderLayout.CENTER);
			retainSize.add(internalPanel,BorderLayout.SOUTH);
		windows.add(retainSize);
		
		
		JPanel thresholdSize = new JPanel(new BorderLayout());
		title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Threshold Size");
		title.setTitleJustification(TitledBorder.LEFT);
		thresholdSize.setBorder(title);
		thresholdSize.add(PanelTools.wrappingText("The minimum information theoretic entropy a word must have to be retained in the final word co-occurrence matrix. This option may not be used at the same time as --retain."),BorderLayout.CENTER);
			internalPanel = new JPanel(new BorderLayout());
			final JTextField thresholdSizeText = new JTextField(WordRelatorHAL.THRESHOLD_SIZE_START + "");
			thresholdSizeText.addKeyListener(new KeyListener() {
				public void keyReleased(KeyEvent arg0) {
					try {
						relator.threshold = Double.parseDouble(thresholdSizeText.getText());
						thresholdSizeText.setForeground(Color.BLACK);
					} catch(NumberFormatException e) {
						thresholdSizeText.setForeground(Color.RED);
					}
				}
				public void keyPressed(KeyEvent arg0) {}
				public void keyTyped(KeyEvent arg0) {}
			});
			internalPanel.add(thresholdSizeText,BorderLayout.CENTER);
			thresholdSize.add(internalPanel,BorderLayout.SOUTH);
		windows.add(thresholdSize);
		
		
		panelHALOptions.setLayout(new BorderLayout());
		panelHALOptions.add(windows,BorderLayout.CENTER);
		
		
		panelHALOptions.name = "HAL_OPTIONS";
		wizard.panels.put(panelHALOptions.name, panelHALOptions);

	}

	public WordRelator getRelator() {
		return relator;
	}

	public boolean canFinish() {
		return false;
	}

	public String getSubTitle() {
		return WordRelatorHAL.typeName;
	}

	public String getTitle() {
		return "Relation Manager";
	}

	public String nextPanel() {
		return panelHALOptions.name;
	}

	public String previousPanel() {
		return PanelIntro.name;
	}
}
