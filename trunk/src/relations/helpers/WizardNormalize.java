package relations.helpers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import gui.WordMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import relations.WordRelator;
import tools.PanelTools;
import transformations.Transformation;
import weka.core.Debug.Random;
import wizard.PanelIntro;
import wizard.PanelRelation;
import wizard.Wizard;

public class WizardNormalize extends PanelRelation {

	private static final long serialVersionUID = 8116740988211880419L;

	public WordRelatorNormalize relator;
	public WordMap wordMap;
	private JComboBox comparator;
	
	public WizardNormalize(Wizard wizard) {
		super(wizard, WordRelatorNormalize.typeName);
		
		relator = new WordRelatorNormalize(defaultColor,"",wizard.wordMap);
		
		this.wordMap = wizard.wordMap;

		addDefaults();
		
		scrollWindow.add(PanelTools.wrappingText("Select the comparator to normalize."));
		comparator = wordMap.getComboComparators();
		scrollWindow.add(comparator);

		final JPanel transformation = new JPanel(new BorderLayout());
		transformation.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Set Transformation"));
		
		final JPanel transformPanel = new JPanel(new GridLayout(0,1));
		transformPanel.setPreferredSize(new Dimension(0,200));
		
		JPanel distributionPanel = new JPanel() {
			private static final long serialVersionUID = -489647703806385730L;

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				WordRelator relator1 = (WordRelator)comparator.getSelectedItem();
				
				if(relator1 == null) {
					g.setColor(Color.BLACK);
					g.drawString("No relator selected", 10, 10);
					return;
				}

				g.drawString("Original: Blue", 10, 10);
				g.drawString("Modified: Red", 10, 22);
				
				int[] buckets1 = new int[100];
				int[] buckets2 = new int[100];
				String[] words = relator1.getWords().toArray(new String[0]);
				Random rand = new Random();
				
				for(int i=0;i<10000;i++) {
					String w1 = words[rand.nextInt(words.length)];
					String w2 = words[rand.nextInt(words.length)];
					double val = relator1.getDistance(w1, w2);
					double val2 = val;
					
					for(Transformation t : relator.transforms) {
						val2 = t.transform(val2);
					}

					buckets1[Math.max(0, Math.min(99, (int)(val * 100)))]++;
					buckets2[Math.max(0, Math.min(99, (int)(val2 * 100)))]++;
				}

				int max = 0;
				for(int i=0;i<buckets1.length;i++) {
					max = Math.max(max, buckets1[i]);
					max = Math.max(max, buckets2[i]);
				}
				
				for(int i=0;i<buckets1.length;i++) {
					int x = getWidth() * i / buckets1.length;
					int width = getWidth() / buckets1.length;

					int height1 = getHeight() * buckets1[i] / max;
					int y1 = getHeight() - height1;
					
					int height2 = getHeight() * buckets2[i] / max;
					int y2 = getHeight() - height2;
					
					g.setColor(Color.BLUE);
					g.fillRect(x, y1, width / 2, height1);
					
					g.setColor(Color.RED);
					g.fillRect(x + width / 2, y2, width / 2, height2);
				}
			}
		};
		distributionPanel.setPreferredSize(new Dimension(0,200));

		JPanel controlPanel = new JPanel(new FlowLayout());

		JButton remove = new JButton("Remove");
		JButton add = new JButton("Add");
		final JComboBox type = new JComboBox(Transformation.types);
		controlPanel.add(remove);
		controlPanel.add(add);
		controlPanel.add(type);

		transformation.add(controlPanel, BorderLayout.NORTH);
		transformation.add(new JScrollPane(transformPanel), BorderLayout.CENTER);
		transformation.add(distributionPanel, BorderLayout.SOUTH);
		
		final ChangeListener cl = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				transformation.invalidate();
				transformation.validate();
				transformation.repaint();
			}
		};
		
		comparator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				transformation.invalidate();
				transformation.validate();
				transformation.repaint();
			}
		});
		
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Transformation t1 = (Transformation)type.getSelectedItem();
				Transformation t2 = t1.clone();
				t2.change.add(cl);
				relator.transforms.push(t2);
				transformPanel.removeAll();
				for(Transformation t : relator.transforms) {
					transformPanel.add(t.panel);
				}
				transformation.invalidate();
				transformation.validate();
				transformation.repaint();
			}
		});
		
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				relator.transforms.pop();
				transformPanel.removeAll();
				for(Transformation t : relator.transforms) {
					transformPanel.add(t.panel);
				}
				transformation.invalidate();
				transformation.validate();
				transformation.repaint();

			}
		});

		scrollWindow.add(transformation);
	}
	
	public boolean canFinish() {
		return comparator.getSelectedItem() != null;
	}
	
	public String getTitle() {
		return "Relation Manager";
	}

	public String getSubTitle() {
		return "Normalization relation";
	}

	public String nextPanel() {
		return null;
	}
	
	public WordRelator getRelator() {
		return relator;
	}

	public String previousPanel() {
		return PanelIntro.name;
	}

	public void finish() {
		WordRelator relator1 = (WordRelator)comparator.getSelectedItem();
		
		if(relator != null) {
			relator.relator = relator1;
			wizard.wordMap.setRelatorStatus(getRelator(),true);
		}
	}

}
