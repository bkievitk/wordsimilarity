package cluster;

import gui.Options;
import gui.WordMap;
import gui.WordNode;

import javax.swing.*;
import javax.swing.event.*;

import relations.WordRelator;
import relations.beagle.VectorTools;
import tools.MyColorPanel;
import tools.VerticalLayout;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Clusterer;
import weka.clusterers.Cobweb;
import weka.clusterers.EM;
import weka.clusterers.FarthestFirst;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.Vector;

public class Clustering extends JPanel {

	private static final long serialVersionUID = -5879229821946385928L;
	private WordMap map;
	private MyColorPanel[] colors = new MyColorPanel[0];
	private JPanel colorPanel = new JPanel();
	private Options options;
	
	public Color randomColor() {
		Random rand = new Random();
		return new Color(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255));
	}
	
	public Clustering(final WordMap map, final Options options) {
		this.map = map;
		this.options = options;
		
		this.setLayout(new VerticalLayout(3,3));

		final JSlider groupNum = new JSlider();
		groupNum.setMinimum(2);
		groupNum.setMaximum(20);
		groupNum.setValue(2);
		groupNum.setMinorTickSpacing(1);
		groupNum.setMajorTickSpacing(5);
		groupNum.setPaintTicks(true);
		groupNum.setPaintLabels(true);
		this.add(groupNum);
		
		groupNum.setBorder(BorderFactory.createTitledBorder(groupNum.getBorder(), "number of groups"));

		colorPanel.setLayout(new GridLayout(0, 3, 10, 10));
		colorPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "colors"));
		JScrollPane scroll = new JScrollPane(colorPanel);
		scroll.setPreferredSize(new Dimension(0,200));
		this.add(scroll);
		
		colors = new MyColorPanel[groupNum.getValue()];
		for(int i=0;i<colors.length;i++) {
			colors[i] = new MyColorPanel(true, randomColor(), 6);
			colors[i].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), i + ""));
		}
		colorPanel.removeAll();
		for(JPanel color : colors) {
			colorPanel.add(color);
		}
		invalidate();

		groupNum.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				int numGroups = groupNum.getValue();
				if(numGroups > colors.length) {
					MyColorPanel[] newColors = new MyColorPanel[numGroups];
					for(int i=0;i<colors.length;i++) {
						newColors[i] = colors[i];
					}
					for(int i=colors.length;i<numGroups;i++) {
						newColors[i] = new MyColorPanel(true, randomColor(), 6);
						newColors[i].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), i + ""));
					}
					colors = newColors;
				} else if(numGroups < colors.length) {
					MyColorPanel[] newColors = new MyColorPanel[numGroups];
					for(int i=0;i<newColors.length;i++) {
						newColors[i] = colors[i];
					}
				}
				
				colorPanel.removeAll();
				for(JPanel color : colors) {
					colorPanel.add(color);
				}
				invalidate();
				validate();
				repaint();
				
			}
		});
		
		final JComboBox comparators = map.getComboComparators();
		comparators.setBorder(BorderFactory.createTitledBorder(comparators.getBorder(), "comparator"));
		add(comparators);
		
		String[] clusteringAlgorithms = {"EM","FurthestFirst","Cobweb","SimpleKMeans"};
		final JComboBox algorithms = new JComboBox(clusteringAlgorithms);
		algorithms.setBorder(BorderFactory.createTitledBorder(algorithms.getBorder(), "clusterType"));
		add(algorithms);
		
		JButton run = new JButton("run");
		add(run);
		
		run.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				options.coloringType = Options.COLORING_OPEN;
				Vector<WordNode> words = map.activeWordsSorted;
				WordRelator relator = (WordRelator)comparators.getSelectedItem();
				
				Instances data = new Instances("name", new FastVector(), 0);
				
				for(int i=0;i<words.size();i++) {
					data.insertAttributeAt(new Attribute("Dim " + i), data.numAttributes());
				}
				
				for(int i=0;i<words.size();i++) {
					double[] vec = new double[words.size()];
					for(int j = 0; j < words.size(); j++) {
						vec[j] = relator.getDistance(words.get(i).word, words.get(j).word);
					}
					data.add(new Instance(1.0 / words.size(), vec));
				}
				
				Clusterer clusterer = null;
				
				try {
					switch(algorithms.getSelectedIndex()) {
						case 0:
							// EM
							String[] emOptions = new String[4];
							emOptions[0] = "-I";                 	// max. iterations
							emOptions[1] = "100";
							emOptions[2] = "-N";                 	// clusters
							emOptions[3] = "" + colors.length;
							EM em = new EM();
							em.setOptions(emOptions);
							clusterer = em;
						break;
						case 1:
							// FurthestFirst
							String[] ffOptions = new String[2];
							ffOptions[0] = "-N";                 	// clusters
							ffOptions[1] = "" + colors.length;
							FarthestFirst ff = new FarthestFirst(); 
							ff.setOptions(ffOptions);
							clusterer = ff;
						break;
						case 2:
							// Cobweb
							String[] coOptions = new String[0];
							Cobweb co = new Cobweb(); 
							co.setOptions(coOptions);
							clusterer = co;
						break;
						case 3:
							// SimpleKMeans
							String[] skOptions = new String[2];
							skOptions[0] = "-N";                 	// clusters
							skOptions[1] = "" + colors.length;
							SimpleKMeans sk = new SimpleKMeans(); 
							sk.setOptions(skOptions);
							clusterer = sk;
						break;
							
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				try {
					clusterer.buildClusterer(data);		// build the clusterer
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				try {
					Vector<Color> colorSet = new Vector<Color>();
					for(int i=0;i<colors.length;i++) {
						colorSet.add(colors[i].getColor());
					}

					Random rand = new Random();
					for(int i=0;i<data.numInstances();i++) {
						int clusterID = clusterer.clusterInstance(data.instance(i));
						while(colorSet.size() <= clusterID) {
							colorSet.add(new Color(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255)));
						}
						words.get(i).color = colorSet.get(clusterID);
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				options.change();
			}
			
		});
		
	}
	
}
