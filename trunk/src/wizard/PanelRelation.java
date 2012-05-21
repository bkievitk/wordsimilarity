package wizard;

import gui.SentenceCleaner;

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;

import relations.WordRelator;
import tools.MyColorPanel;
import tools.PanelTools;
import tools.VerticalLayout;

public abstract class PanelRelation extends WizardPanel {

	private static final long serialVersionUID = -6942560984519321644L;

	public static final Color defaultColor = Color.BLACK;
	
	public MyColorPanel color;
	public JTextField nameField;
	public JPanel scrollWindow;
	
	public PanelRelation(Wizard wizard, String name) {
		super(wizard, name);
		
		scrollWindow = new JPanel(new VerticalLayout(5,5));
		final JScrollPane scroll = new JScrollPane(scrollWindow,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent e) {}
			public void componentMoved(ComponentEvent e) {}
			public void componentShown(ComponentEvent e) {}
			
			public void componentResized(ComponentEvent e) {
				scrollWindow.setPreferredSize(new Dimension(getWidth(),scrollWindow.getPreferredSize().height));
				invalidate();
				validate();
				repaint();
			}

		});
		
		setLayout(new BorderLayout());
		add(scroll,BorderLayout.CENTER);
		
	}
	
	public abstract WordRelator getRelator();
		
	public JPanel addCleaners(LinkedList<SentenceCleaner> cleanerList) {
		
		JPanel cleanerPanel = new JPanel(new BorderLayout());
		TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Select Sentence Cleaning Tools");
		title.setTitleJustification(TitledBorder.LEFT);
		cleanerPanel.setBorder(title);
		
			JPanel cleanerShow = SentenceCleaner.getSentenceCleanerPanel(cleanerList);
			cleanerShow.setPreferredSize(new Dimension(150,85));
			cleanerPanel.add(cleanerShow,BorderLayout.CENTER);
			
			JTextArea description = PanelTools.wrappingText("When you give this similarity module sentences to learn, it will use these tools to clean up the text before learning it.");
			cleanerPanel.add(description,BorderLayout.NORTH);
					
			scrollWindow.add(cleanerPanel);
		
		return cleanerShow;
	}
	
	public void addDefaults() {
		
		TitledBorder title;
		
		JPanel colorPanel = new JPanel(new BorderLayout());
		title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Select Color");
		title.setTitleJustification(TitledBorder.LEFT);
		colorPanel.setBorder(title);
		
			color = new MyColorPanel(true,defaultColor,14);
			colorPanel.add(color,BorderLayout.CENTER);
			color.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					if(getRelator() != null) {
						getRelator().color = color.getColor();
					}
				}
			});
			
		scrollWindow.add(colorPanel);

		JPanel namePanel = new JPanel(new BorderLayout());
		title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Select Name");
		title.setTitleJustification(TitledBorder.LEFT);
		namePanel.setBorder(title);
		
			JTextArea description = PanelTools.wrappingText("Select a name for this word relationship.");
			nameField = new JTextField();/* {
				public void setEnabled(boolean b) {
					super.setEditable(b);
					if(b) {
						setBackground(Color.WHITE);
					} else {
						setBackground(Color.LIGHT_GRAY);
					}
				}
			};*/
			
			namePanel.add(description,BorderLayout.CENTER);
			namePanel.add(nameField,BorderLayout.SOUTH);
			nameField.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent arg0) {}
				public void keyReleased(KeyEvent arg0) {
					if(getRelator() != null) {
						getRelator().name = nameField.getText();
					}
				}
				public void keyTyped(KeyEvent arg0) {}
			});
			
		scrollWindow.add(namePanel);
	}
	
	public void finish() {
		wizard.wordMap.setRelatorStatus(getRelator(),true);
		wizard.wordMap.addRelatorWords(getRelator());
	}
}
