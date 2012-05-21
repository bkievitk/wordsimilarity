package tools;

import java.awt.*;

import javax.swing.*;

public class PanelTools {

	public static JPanel addLabel(String label, Component c, String location) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		panel.add(c,BorderLayout.CENTER);
		JLabel labelPanel = new JLabel(label);	
		labelPanel.setOpaque(false);
		panel.add(labelPanel,location);
		return panel;
	}
	
	public static JTextArea wrappingText(String text) {
		JTextArea wrappingText = new JTextArea();
		wrappingText.setEditable(false);
		wrappingText.setLineWrap(true);
		wrappingText.setWrapStyleWord(true);	
		wrappingText.setFont(new Font("Tahoma",Font.PLAIN,10));			
		wrappingText.setText(text);
		wrappingText.setOpaque(false);
		return wrappingText;
	}
}
