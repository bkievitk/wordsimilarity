package relations.wordnet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import relations.WordRelator;
import tools.PanelTools;
import wizard.PanelRelation;
import wizard.Wizard;

public class WizardWordNetOptions extends PanelRelation {

	private static final long serialVersionUID = -7615124447878617913L;
	
	public WordRelatorWordNet relator;
	
	public WizardWordNetOptions(Wizard wizard) {
		super(wizard, WordRelatorWordNet.typeName + "_OPTIONS");
		relator = new WordRelatorWordNet(Color.BLACK,"",wizard.wordMap);
		
		addDefaults();
		
		TitledBorder title;

		JPanel metrics = new JPanel(new BorderLayout());
		title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Select Distance Metric");
		title.setTitleJustification(TitledBorder.LEFT);
		metrics.setBorder(title);
		
			final JComboBox metricOptions = new JComboBox(WordRelatorWordNet.types);
			metrics.add(PanelTools.wrappingText("Select the distance metric heuristic that you wish to use."),BorderLayout.CENTER);
			metrics.add(metricOptions,BorderLayout.SOUTH);
			
			metricOptions.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					relator.type = metricOptions.getSelectedIndex();
				}				
			});
			
		scrollWindow.add(metrics);
		
		JPanel metricsFirstBest = new JPanel(new BorderLayout());
		title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Select Metric Choice");
		title.setTitleJustification(TitledBorder.LEFT);
		metricsFirstBest.setBorder(title);
		
			JPanel options = new JPanel(new BorderLayout());
			JRadioButton selectFirst = new JRadioButton("Select Primary");
			final JRadioButton selectBest = new JRadioButton("Select Best");
			ButtonGroup group = new ButtonGroup();
			group.add(selectFirst);
			group.add(selectBest);
			options.add(selectFirst,BorderLayout.WEST);
			options.add(selectBest,BorderLayout.EAST);
			
			selectBest.setSelected(true);
			relator.selectBest = true;
			
			ActionListener al = new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					relator.selectBest = selectBest.isSelected();
				}
			};

			selectFirst.addActionListener(al);
			selectBest.addActionListener(al);
			
			metricsFirstBest.add(PanelTools.wrappingText("The distance metric will return either the best match between all synonyms in the given synsets or it will just return the distance between the primary two meanings."),BorderLayout.CENTER);
			metricsFirstBest.add(options,BorderLayout.SOUTH);
			
		scrollWindow.add(metricsFirstBest);
		
		//JWS jws = WordRelatorWordNet.jws;
		
		/*
		AdaptedLesk t1 = jws.getAdaptedLesk();
		AdaptedLeskTanimoto t2 = jws.getAdaptedLeskTanimoto();
		AdaptedLeskTanimotoNoHyponyms t3 = jws.getAdaptedLeskTanimotoNoHyponyms();
		HirstAndStOnge t4 = jws.getHirstAndStOnge();
		JiangAndConrath t6 = jws.getJiangAndConrath();
		LeacockAndChodorow t7 = jws.getLeacockAndChodorow();		
		Lin t8 = jws.getLin();
		Path t9 = jws.getPath();
		Resnik t10 = jws.getResnik();
		WuAndPalmer t11 = jws.getWuAndPalmer();

		System.out.println(AdaptedLesk.class.getClass().getSuperclass());
		
		//t1.lesk(str1,1,str2, 1, "n");
		//t1.max(str1,str2,"n");
		 */
		
		
	}

	public WordRelator getRelator() {
		return relator;
	}

	public boolean canFinish() {
		return true;
	}

	public String getSubTitle() {
		return "WordNet Relation";
	}

	public String getTitle() {
		return "Relation Manager";
	}

	public String nextPanel() {
		return null;
	}

	public String previousPanel() {
		return WordRelatorWordNet.typeName;
	}

}
