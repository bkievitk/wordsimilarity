package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import tools.MyColorPanel;
import tools.PanelTools;
import tools.VerticalLayout;

public class ShowOptions extends JTabbedPane {

	private static final long serialVersionUID = 3183431390936790213L;
	
	private Options options;
	private VisualizationInterface visualization;
	
	public ShowOptions(Options options, VisualizationInterface visualization) {
		this.options = options;
		this.visualization = visualization;
		
		addTab("Color",colorPanel());
		addTab("Node",nodePanel());
	}
	
	public JPanel colorPanel() {
		JPanel colorPanel = new JPanel(new VerticalLayout(3,3));

		final MyColorPanel backgroundColor = new MyColorPanel(true, options.getBackgroundColor(),14);
		colorPanel.add(PanelTools.addLabel("Select Background Color", backgroundColor, BorderLayout.NORTH));
		backgroundColor.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				options.setBackgroundColor(backgroundColor.getColor());
				visualization.visualizationChanged();
			}			
		});
		
		final MyColorPanel highlightColor = new MyColorPanel(true, options.getHighlightedConnectionColor(),14);
		colorPanel.add(PanelTools.addLabel("Select Highlighted Connection Color", highlightColor, BorderLayout.NORTH));
		highlightColor.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				options.setHighlightedConnectionColor(highlightColor.getColor());
				visualization.visualizationChanged();
			}			
		});
		
		final MyColorPanel nodeColor = new MyColorPanel(true, options.getNodeColor(),14);
		colorPanel.add(PanelTools.addLabel("Select Node Color", nodeColor, BorderLayout.NORTH));
		nodeColor.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				options.setNodeColor(nodeColor.getColor());
				visualization.visualizationChanged();
			}			
		});

		final JRadioButton constant = new JRadioButton("Constant Color");
		final JRadioButton pos = new JRadioButton("Part of Speach Color");
		ButtonGroup group = new ButtonGroup();
		group.add(constant);
		group.add(pos);
		pos.setSelected(true);

		colorPanel.add(constant);
		colorPanel.add(pos);
		
		ChangeListener cl = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(constant.isSelected()) {
					options.coloringType = Options.COLORING_CONSTANT;
				} else if(pos.isSelected()) {
					options.coloringType = Options.COLORING_POS;
				}
				visualization.visualizationChanged();
			}			
		};

		constant.addChangeListener(cl);
		pos.addChangeListener(cl);
		
		return colorPanel;
	}
	
	public JPanel nodePanel() {
		JPanel nodePanel = new JPanel(new VerticalLayout(3,3));

		nodePanel.add(new JLabel("Select node scalling type."));
		
		final JRadioButton sizeConstant = new JRadioButton("constant");
		final JRadioButton sizeLinear = new JRadioButton("linear");
		final JRadioButton sizeRoot = new JRadioButton("root");
		final JRadioButton sizeLog = new JRadioButton("log");
		ButtonGroup group = new ButtonGroup();
		group.add(sizeConstant);
		group.add(sizeLinear);
		group.add(sizeRoot);
		group.add(sizeLog);
		sizeConstant.setSelected(true);
		
		nodePanel.add(sizeConstant);
		nodePanel.add(sizeLinear);
		nodePanel.add(sizeRoot);
		nodePanel.add(sizeLog);
		
		//scallingType
		ActionListener scalling = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(sizeConstant.isSelected()) {
					options.setScallingType(Options.SCALLING_CONSTANT);
				} else if(sizeLinear.isSelected()) {
					options.setScallingType(Options.SCALLING_LINEAR);
				} else if(sizeRoot.isSelected()) {
					options.setScallingType(Options.SCALLING_ROOT);
				} else if(sizeLog.isSelected()) {
					options.setScallingType(Options.SCALLING_LOG);
				}
				visualization.repaint();
			}			
		};

		sizeConstant.addActionListener(scalling);
		sizeLinear.addActionListener(scalling);
		sizeRoot.addActionListener(scalling);
		sizeLog.addActionListener(scalling);
		
		final JSlider multiple = new JSlider();
		Hashtable<Integer,JLabel> labels = new Hashtable<Integer,JLabel>();
		labels.put(50, new JLabel("%50"));
		labels.put(100, new JLabel("%100"));
		labels.put(200, new JLabel("%200"));
		labels.put(400, new JLabel("%400"));
		labels.put(400, new JLabel("%800"));
		multiple.setMinimum(50);
		multiple.setMaximum(800);
		multiple.setLabelTable(labels);
		multiple.setPaintLabels(true);
		multiple.setValue(100);
		multiple.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				options.setScallingFactor(multiple.getValue());
				visualization.repaint();
			}
		});
		nodePanel.add(PanelTools.addLabel("Select Node Scalling.", multiple, BorderLayout.NORTH));
		
		final JRadioButton noLabel = new JRadioButton("No Label");
		final JRadioButton wordLabel = new JRadioButton("Word Label");
		final JRadioButton fullLabel = new JRadioButton("Full Label");
		wordLabel.setSelected(true);
		group = new ButtonGroup();
		group.add(noLabel);
		group.add(wordLabel);
		group.add(fullLabel);
		nodePanel.add(noLabel);
		nodePanel.add(wordLabel);
		nodePanel.add(fullLabel);
		
		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(noLabel.isSelected()) {
					options.labelType = Options.LABEL_NONE;
				} else if(wordLabel.isSelected()) {
					options.labelType = Options.LABEL_TEXT;
				} else if(fullLabel.isSelected()) {
					options.labelType = Options.LABEL_FULL;
				}
				visualization.visualizationChanged();
			}
		};

		noLabel.addActionListener(al);
		wordLabel.addActionListener(al);
		fullLabel.addActionListener(al);
				
		return nodePanel;
	}
}
