package wizard;

import gui.WordMap;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import javax.swing.*;
import relations.RelationLoader;
import tools.MovablePanel;

@SuppressWarnings("serial")
public class Wizard extends JPanel {

	public JLabel title;
	public JLabel subTitle;
	
	public JButton back;
	public JButton next;
	public JButton finish;
	public JButton cancel;
	
	public Hashtable<String,WizardPanel> panels = new Hashtable<String,WizardPanel>();
	public WizardPanel currentPanel;
	
	public WordMap wordMap;
	
	public Component parent;
	
	public static RelationLoader relationTypes = new RelationLoader();
		
	public Wizard(WordMap wordMap, Component parent) {
		this.parent = parent;
		this.wordMap = wordMap;
		
		setLayout(new BorderLayout());
		
		SpringLayout titleLayout = new SpringLayout();
		
		JPanel titlePanel = new JPanel(titleLayout);
		titlePanel.setPreferredSize(new Dimension(0,60));
		titlePanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		titlePanel.setBackground(Color.WHITE);
		
		title = new JLabel("Relation Manager");		
		title.setFont(new Font(title.getFont().getFontName(),Font.PLAIN,14));		
		titlePanel.add(title);
		
		subTitle = new JLabel("Select the Relation Type");
		titlePanel.add(subTitle);

		titleLayout.putConstraint(SpringLayout.WEST, title, 5, SpringLayout.WEST, titlePanel);
		titleLayout.putConstraint(SpringLayout.NORTH, title, 7, SpringLayout.NORTH, titlePanel);
		
		titleLayout.putConstraint(SpringLayout.WEST, subTitle, 10, SpringLayout.WEST, title);
		titleLayout.putConstraint(SpringLayout.NORTH, subTitle, 5, SpringLayout.SOUTH, title);

		final BufferedImage networkImage = MovablePanel.getImage("network.png");
		JPanel imagePanel = new JPanel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(networkImage, 0, 0, null);
			}
		};
		imagePanel.setPreferredSize(new Dimension(networkImage.getWidth(),networkImage.getHeight()));
		titleLayout.putConstraint(SpringLayout.EAST, imagePanel, 0, SpringLayout.EAST, titlePanel);
		titleLayout.putConstraint(SpringLayout.NORTH, imagePanel, 0, SpringLayout.NORTH, titlePanel);
		titlePanel.add(imagePanel);
		
		add(titlePanel,BorderLayout.NORTH);
		
		SpringLayout controlLayout = new SpringLayout();
		
		JPanel controls = new JPanel(controlLayout);
		controls.setPreferredSize(new Dimension(0,35));
		
		controls.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		
		add(controls,BorderLayout.SOUTH);
		
		back = new JButton("< Back");
		controls.add(back);
		next = new JButton("Next >");
		controls.add(next);
		finish = new JButton("Finish");
		controls.add(finish);
		cancel = new JButton("Cancel");
		controls.add(cancel);

		back.setEnabled(false);
		next.setEnabled(false);
		finish.setEnabled(false);
				
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				exit();
			}			
		});
		
		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String nextPanelString = currentPanel.nextPanel();
				WizardPanel nextPanel = panels.get(nextPanelString);
				if(nextPanel != null) {
					setPanel(nextPanel);	
				} else {
					System.out.println("No panel named: [" + nextPanelString + "]");
					
					System.out.println("Panels existing: ");
					for(String name : panels.keySet()) {
						System.out.println("  " + name);
					}
				}
			}			
		});
		
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String previousPanel = currentPanel.previousPanel();
				WizardPanel previous = panels.get(previousPanel);
				if(next != null) {
					setPanel(previous);					
				}
			}			
		});
		
		finish.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				currentPanel.finish();
				exit();
			}			
		});

		controlLayout.putConstraint(SpringLayout.EAST, cancel, -7, SpringLayout.EAST, controls);
		controlLayout.putConstraint(SpringLayout.NORTH, cancel, 7, SpringLayout.NORTH, controls);
		controlLayout.putConstraint(SpringLayout.SOUTH, cancel, -7, SpringLayout.SOUTH, controls);

		controlLayout.putConstraint(SpringLayout.EAST, finish, -7, SpringLayout.WEST, cancel);
		controlLayout.putConstraint(SpringLayout.NORTH, finish, 7, SpringLayout.NORTH, controls);
		controlLayout.putConstraint(SpringLayout.SOUTH, finish, -7, SpringLayout.SOUTH, controls);

		controlLayout.putConstraint(SpringLayout.EAST, next, -7, SpringLayout.WEST, finish);
		controlLayout.putConstraint(SpringLayout.NORTH, next, 7, SpringLayout.NORTH, controls);
		controlLayout.putConstraint(SpringLayout.SOUTH, next, -7, SpringLayout.SOUTH, controls);
		
		controlLayout.putConstraint(SpringLayout.EAST, back, -3, SpringLayout.WEST, next);
		controlLayout.putConstraint(SpringLayout.NORTH, back, 7, SpringLayout.NORTH, controls);
		controlLayout.putConstraint(SpringLayout.SOUTH, back, -7, SpringLayout.SOUTH, controls);
		
		buildPanels();
	}
	
	public void buildPanels() {	
		
		
		panels.put(PanelIntro.name, new PanelIntro(this));
		
		for(int type=0;type<relationTypes.getRelationCount();type++) {
			WizardPanel newPanel = relationTypes.getWizardPanel(type, this);
			try {
				panels.put(newPanel.name, newPanel);
			} catch (Exception e) {}
		}
		
		setPanel(panels.get(PanelIntro.name));		
	}
	
	public void setPanel(WizardPanel panel) {
		if(currentPanel != null) {
			remove(currentPanel);
		}
		
		currentPanel = panel;
		add(currentPanel,BorderLayout.CENTER);
		next.setEnabled(currentPanel.nextPanel() != null);
		back.setEnabled(currentPanel.previousPanel() != null);
		finish.setEnabled(currentPanel.canFinish());
		title.setText(currentPanel.getTitle());					
		subTitle.setText(currentPanel.getSubTitle());
		
		invalidate();
		validate();
		repaint();	
	}
	
	public void exit() {
		buildPanels();
		parent.setVisible(false);
	}
	
}
