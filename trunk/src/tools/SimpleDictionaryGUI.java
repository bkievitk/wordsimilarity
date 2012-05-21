package tools;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.text.Caret;

import relations.WordRelator;
import relations.beagle.Thought;

@SuppressWarnings("serial")
public class SimpleDictionaryGUI extends JPanel {

	public Set<String> thoughtSet;
	public Map<String,Thought> thoughtMap;
	public WordRelator relator;
	
	public JTextArea showDictionary;
	public JTextField search = new JTextField();
	private JButton add;
	private JButton remove;
	
	public SimpleDictionaryGUI(Set<String> thoughts) {
		thoughtSet = thoughts;
		setup();
	}
	
	/*
	public SimpleDictionaryGUI(Map<String,Object> thoughts, WordRelator relator) {
		thoughtMap = thoughts;
		this.relator = relator;
		setup();
	}
	*/
	public SimpleDictionaryGUI(Map<String,Thought> thoughts, WordRelator relator) {
		thoughtMap = thoughts;
		this.relator = relator;
		setup();
	}
	
	public void setup() {
		
		setLayout(new BorderLayout());
		
		JPanel searchPanel = new JPanel(new FlowLayout());
		searchPanel.add(search);
		search.setColumns(10);
		
		add = new JButton("add");
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String word = search.getText();	
				if(thoughtSet != null) {
					thoughtSet.add(word);
				} if(thoughtMap != null) {
					//thoughtMap.put(word,(Thought)relator.buildWord(word));
				}
				showRemainingTerms();
			}			
		});
		searchPanel.add(add);
				
		remove = new JButton("remove");
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String word = search.getText();
				if(thoughtSet != null) {
					thoughtSet.remove(word);
				} if(thoughtMap != null) {
					thoughtMap.remove(word);
				}
				showRemainingTerms();
			}			
		});
		searchPanel.add(remove);
		
		showDictionary = new JTextArea() {
			public void setEnabled(boolean value) {
				super.setEnabled(value);
				if(!value) {
					setBackground(new Color(240,240,240));
				} else {
					setBackground(new Color(255,255,255));
				}
			}
		};
		 
		JScrollPane scroll = new JScrollPane(showDictionary);
		scroll.setPreferredSize(new Dimension(200,200));
		
		add(scroll, BorderLayout.CENTER);
		add(searchPanel,BorderLayout.NORTH);

		showDictionary.setEditable(false);
		showDictionary.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent arg0) {
				String showText = showDictionary.getText();
				int position = showDictionary.getCaretPosition();
				int stop = showText.indexOf('\n', position);
				int start = showText.lastIndexOf('\n', position)+1;
				showDictionary.setCaretPosition(start);
				Caret c = showDictionary.getCaret();
				c.moveDot(stop);
				try {
					String selected = showDictionary.getSelectedText();
					if(selected.length() > 1) {
						selected = selected.substring(selected.indexOf(')') + 1);
						
						
						search.setText(selected);
						showRemainingTerms();
					}
				} catch(IllegalArgumentException e) {}
			}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {}
			
		});
		
		search.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {}
			public void keyTyped(KeyEvent arg0) {}
			public void keyReleased(KeyEvent arg0) {
				showRemainingTerms();
			}
		});
		
		showRemainingTerms();
		
	}
	
	public void setEnabled(boolean value) {
		super.setEnabled(value);		
		showDictionary.setEnabled(value);
		search.setEnabled(value);
		add.setEnabled(value);
		remove.setEnabled(value);		
	}
	
	
	public void showRemainingTerms() {
		showDictionary.setText("");
		
		Vector<String> terms = new Vector<String>();
		
		Set<String> thoughts;
		if(thoughtSet != null) {
			thoughts = thoughtSet;
		} else {
			thoughts = thoughtMap.keySet();
		}
		
		for(String thought : thoughts) {
			if(thought.startsWith(search.getText())) {
				terms.add(thought);
			}
		}
		
		Object[] termsA = terms.toArray();
		Arrays.sort(termsA);
		
		for(Object o : termsA) {
			showDictionary.append(o + "\n");
		}
	}
}
