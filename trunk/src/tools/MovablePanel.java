package tools;

import gui.WordMap;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.concurrent.Semaphore;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import relations.WordRelator;

public class MovablePanel extends JPanel {
	
	private static final long serialVersionUID = -3415124470292091962L;

	private JButton addRemove = new JButton();
	private JButton delete = new JButton();
	
	private boolean isSelected = true;	
	private WordRelator relator;
	private PanelSelector panel;
	private JLabel typeLabel;
	private MyColorPanel colorPanel;
	private RangeSlider range;
	private JSlider multiple;
	private TitledBorder title;

	private static final ImageIcon minimize = new ImageIcon(getImage("minimize.png"));
	private static final ImageIcon maximize = new ImageIcon(getImage("maximize.png"));
	private static final ImageIcon close = new ImageIcon(getImage("close.png"));
	
	private Semaphore rangeSemaphore = new Semaphore(1);
	private boolean doNotChange = false;
	
	public static BufferedImage getImage(String str) {
		try {
			
			URL url = MovablePanel.class.getResource("/" + str);
			if(url != null) {
				try {
					return ImageIO.read(url.openStream());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
			return ImageIO.read(new FileInputStream(new File("resources/" + str)));
			
		} catch (IOException e) {
			return new BufferedImage(10,10,BufferedImage.TYPE_INT_RGB);
		}
	}
	
	public void set(boolean isSelected, WordRelator relator) {
		setSelected(isSelected,false);
		this.relator = relator;
		typeLabel.setText(relator.toString());
		colorPanel.setColor(relator.color, false);
		
		doNotChange = true;
		range.setValue((int)(relator.min * 1000));
		range.setUpperValue((int)(relator.max * 1000));
		
		multiple.setValue((int)(relator.scaller * 100));
		title.setTitle(relator.name);
	}
	
	private JPanel buildInnerPanel(final WordMap wordMap) {
		JPanel visualization = new JPanel();
		
		SpringLayout layout = new SpringLayout();
		visualization.setLayout(layout);
		
		colorPanel = new MyColorPanel(true,Color.BLACK,7);
		visualization.setLayout(layout);
		visualization.add(colorPanel);
		
		colorPanel.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				relator.color = colorPanel.getColor();
				wordMap.relationChanged();
			}
		});
		
		range = new RangeSlider();		
		Hashtable<Integer,JLabel> labels = new Hashtable<Integer,JLabel>();
		labels.put(0, new JLabel("0"));
		labels.put(500, new JLabel(".5"));
		labels.put(1000, new JLabel("1"));
		range.setLabelTable(labels);
		range.setMinimum(0);
		range.setMaximum(1000);
		range.setValue(0);
		range.setUpperValue(1000);   
		range.setMajorTickSpacing(100);
		range.setPaintTicks(true);
		range.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				
				try {
					rangeSemaphore.acquire();
					if(doNotChange) {
						doNotChange = false;
					} else {
						relator.min = (range.getValue() / 1000.0);
						relator.max = (range.getUpperValue() / 1000.0);
						wordMap.relationChanged();
					}
					rangeSemaphore.release();
					
				} catch(InterruptedException e) {}
			}			
		});
		JPanel rangeLabeled = PanelTools.addLabel("Show Connections of Strength", range, BorderLayout.NORTH);
		visualization.add(rangeLabeled);     
				
		final JSlider connectionNumber = new JSlider();
		connectionNumber.setMinimum(1);
		connectionNumber.setMaximum(20);
		
		labels = new Hashtable<Integer,JLabel>();
		labels.put(0, new JLabel("0"));
		labels.put(5, new JLabel(".5"));
		labels.put(10, new JLabel("1"));
		labels.put(20, new JLabel("2"));
		connectionNumber.setLabelTable(labels);
		
		connectionNumber.setPaintLabels(true);
		connectionNumber.setMinorTickSpacing(1);
		connectionNumber.setMajorTickSpacing(1);
		connectionNumber.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				WordMap wordMap = panel.wordMap;
				
				double[] toSort = new double[wordMap.activeWords.size() * Math.max(100, wordMap.activeWords.size())];
				int j = 0;
				int i = 0;
								
				for(gui.WordNode node1 : wordMap.activeWordsSorted) {
					if(j > 100) {
						break;
					}
					for(gui.WordNode node2 : wordMap.activeWordsSorted) {
						toSort[i] = relator.getDistance(node1.word, node2.word);
						i++;
					}
					j++;
				}
				
				Arrays.sort(toSort);

				double cutoffWeight = toSort[toSort.length - wordMap.activeWords.size() * connectionNumber.getValue() / 10];
				relator.min = cutoffWeight;
				relator.max = 1;
				
				wordMap.relationChanged();
			}
		});
		JPanel connectionLabeled = PanelTools.addLabel("Show Connections by Number", connectionNumber, BorderLayout.NORTH);
		visualization.add(connectionLabeled);   
		
		multiple = new JSlider();
		labels = new Hashtable<Integer,JLabel>();
		labels.put(50, new JLabel("%50"));
		labels.put(400, new JLabel("%400"));
		multiple.setMinimum(50);
		multiple.setMaximum(400);
		multiple.setLabelTable(labels);
		multiple.setPaintLabels(true);
		multiple.setValue(100);
		multiple.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				relator.scaller = (multiple.getValue() / 100.0);
				wordMap.relationChanged();
			}			
		});
		JPanel multipleLabeled = PanelTools.addLabel("Connection Multiplier", multiple, BorderLayout.NORTH);
		visualization.add(multipleLabeled);
		
		
		layout.putConstraint(SpringLayout.NORTH, colorPanel, 3, SpringLayout.NORTH, visualization);
		layout.putConstraint(SpringLayout.WEST, colorPanel, 2, SpringLayout.WEST, visualization);
		layout.putConstraint(SpringLayout.EAST, colorPanel, -2, SpringLayout.EAST, visualization);

		layout.putConstraint(SpringLayout.NORTH, rangeLabeled, 3, SpringLayout.SOUTH, colorPanel);
		layout.putConstraint(SpringLayout.WEST, rangeLabeled, 2, SpringLayout.WEST, visualization);
		layout.putConstraint(SpringLayout.EAST, rangeLabeled, -2, SpringLayout.EAST, visualization);
		
		layout.putConstraint(SpringLayout.NORTH, connectionLabeled, 3, SpringLayout.SOUTH, rangeLabeled);
		layout.putConstraint(SpringLayout.WEST, connectionLabeled, 2, SpringLayout.WEST, visualization);
		layout.putConstraint(SpringLayout.EAST, connectionLabeled, -2, SpringLayout.EAST, visualization);
		
		
		layout.putConstraint(SpringLayout.NORTH, multipleLabeled, 3, SpringLayout.SOUTH, connectionLabeled);
		layout.putConstraint(SpringLayout.WEST, multipleLabeled, 2, SpringLayout.WEST, visualization);
		layout.putConstraint(SpringLayout.EAST, multipleLabeled, -2, SpringLayout.EAST, visualization);
		
		visualization.setPreferredSize(new Dimension(180,260));
		
		title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "");
		
		title.setTitleJustification(TitledBorder.LEFT);
		visualization.setBorder(title);
		
		return visualization;
	}
	
	public MovablePanel(final PanelSelector panel) {
		this.panel = panel;
		
		addRemove.setPreferredSize(new Dimension(21,21));
		delete.setPreferredSize(new Dimension(21,21));

		delete.setBorder(null);
		addRemove.setBorder(null);
		
		delete.setIcon(close);
		delete.setVisible(false);
		
		setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		SpringLayout layout = new SpringLayout();
		JPanel topPanel = new JPanel(layout);
			topPanel.setPreferredSize(new Dimension(10,26));
		
			typeLabel = new JLabel("");
			
			topPanel.add(addRemove);
			topPanel.add(typeLabel);
			topPanel.add(delete);
			
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, typeLabel, 3, SpringLayout.VERTICAL_CENTER, topPanel);
			layout.putConstraint(SpringLayout.WEST, typeLabel, 7, SpringLayout.WEST, topPanel);

			layout.putConstraint(SpringLayout.NORTH, addRemove, 3, SpringLayout.NORTH, topPanel);
			layout.putConstraint(SpringLayout.SOUTH, addRemove, -3, SpringLayout.SOUTH, topPanel);
			layout.putConstraint(SpringLayout.EAST, addRemove, -3, SpringLayout.EAST, topPanel);
			
			layout.putConstraint(SpringLayout.NORTH, delete, 3, SpringLayout.NORTH, topPanel);
			layout.putConstraint(SpringLayout.SOUTH, delete, -3, SpringLayout.SOUTH, topPanel);
			layout.putConstraint(SpringLayout.EAST, delete, -3, SpringLayout.WEST, addRemove);
			
			topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
		add(topPanel,BorderLayout.NORTH);
		add(buildInnerPanel(panel.wordMap),BorderLayout.CENTER);
		
		
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(panel.wordMap.inactiveRelations.size());
				panel.wordMap.inactiveRelations.remove(relator);
				System.out.println(panel.wordMap.inactiveRelations.size());
				panel.build();
			}
		});
		
		addRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				isSelected = !isSelected;
				setSelected(isSelected,true);
			}			
		});
	}
	
	private void setSelected(boolean isSelected, boolean update) {
		if(isSelected) {
			addRemove.setIcon(minimize);
			delete.setVisible(false);
			if(update) {
				panel.wordMap.activeRelations.remove(relator);
				panel.wordMap.activeRelations.add(relator);
				panel.wordMap.inactiveRelations.remove(relator);
				panel.wordMap.relationChanged();
			}
		} else {
			delete.setVisible(true);
			addRemove.setIcon(maximize);
			if(update) {
				panel.wordMap.inactiveRelations.remove(relator);
				panel.wordMap.inactiveRelations.add(relator);
				panel.wordMap.activeRelations.remove(relator);
				panel.wordMap.relationChanged();
			}
		}
	}
	
}