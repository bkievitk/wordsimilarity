package tools;

import gui.WordMap;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import relations.WordRelator;


public class PanelSelector extends JPanel implements ComponentListener, MouseListener {

	private static final long serialVersionUID = -8450264860821994633L;

	public WordMap wordMap;
	
	public int width;
	public JPanel divider;
	private int selectedIndex = -1;
	
	public Vector<MovablePanel> panels = new Vector<MovablePanel>();
	
	public PanelSelector(final WordMap wordMap, int width) {
		this.wordMap = wordMap;
		this.width = width;
		
		wordMap.relationChange.add(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {					
				build();
			}
		});
		
		setLayout(null);
		divider = new JPanel();
		divider.setBackground(Color.BLACK);
		
		this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,1));

		addComponentListener(this);
		addMouseListener(this);
	}

	public void componentHidden(ComponentEvent arg0) {
	}

	public void componentMoved(ComponentEvent arg0) {
	}

	public void componentResized(ComponentEvent arg0) {
		build();
	}

	public void componentShown(ComponentEvent arg0) {
	}
	
	public Dimension getPreferredSize() {
		int y = 17;
		
		for(int i=0;i<Math.min(panels.size(), wordMap.activeRelations.size()+wordMap.inactiveRelations.size());i++) {
			int height = Math.max(30,panels.get(i).getPreferredSize().height);
			y += height + 5;
		}
		
		return new Dimension(width,y);
	}
	
	public void build() {
		this.removeAll();

		int total = wordMap.activeRelations.size() + wordMap.inactiveRelations.size();
		while(panels.size() < total) {
			panels.add(new MovablePanel(this));
		}
		
		if(wordMap.activeRelations.size() == 0 && wordMap.inactiveRelations.size() == 0) {
			JTextArea label = PanelTools.wrappingText("There are no comparators available. Build some using the Similarity Wizard in Tools -> Similarity Wizard.");
			label.setLocation(5,5);
			label.setSize(width-10,100);
			add(label);
			invalidate();
			validate();
			repaint();
			return;
		}
		
		add(divider);

		int i = 0;
		int y = 5;
		for(WordRelator relator : wordMap.activeRelations) {
			MovablePanel panel = panels.get(i);
			panel.set(true, relator);
			i++;
			add(panel);
			panel.setLocation(5, y);
			int height = Math.max(30,panel.getPreferredSize().height);
			panel.setSize(width-10, height);
			y += height + 5;
		}
		y += 5;
		divider.setLocation(5,y);
		divider.setSize(width-10, 2);
		y += 7;
		
		for(WordRelator relator : wordMap.inactiveRelations) {
			MovablePanel panel = panels.get(i);
			panel.set(false, relator);
			i++;
			add(panel);
			panel.setLocation(5, y);
			int height = Math.max(30,panel.getPreferredSize().height);
			panel.setSize(width-10, height);
			y += height + 5;
		}
		invalidate();
		validate();
		repaint();
	}

	public void mouseClicked(MouseEvent arg0) {
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public int getPanelID(int py) {
		int y = 5;
		for(int i=0;i<wordMap.activeRelations.size()+wordMap.inactiveRelations.size();i++) {
			int height = Math.max(30,panels.get(i).getPreferredSize().height);
			y += height + 5;
			if(py < y) {
				return i;
			}
		}
		return -1;
	}
	
	public void mousePressed(MouseEvent arg0) {
		selectedIndex = getPanelID(arg0.getY());
	}
	
	public void mouseReleased(MouseEvent arg0) {
		if(selectedIndex >= 0) {
			int secondIndex = getPanelID(arg0.getY());
			if(secondIndex >= 0) {
				WordRelator temp = wordMap.activeRelations.get(selectedIndex);
				wordMap.activeRelations.set(selectedIndex, wordMap.activeRelations.get(secondIndex));
				wordMap.activeRelations.set(secondIndex, temp);
				wordMap.relationChanged();
			}
		}
	}
	
}