package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import tools.VerticalLayout;

/**
 * The legend gui shows the relevant legend information.
 * @author bkievitk
 */

public class LegendGUI extends JPanel {

	private static final long serialVersionUID = 1002510373446815637L;

	@SuppressWarnings("serial")
	public LegendGUI(final Options options) {
		setLayout(new VerticalLayout(5,5));
		setPreferredSize(new Dimension(0,200));
		this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		
		// Listen to gui changes.
		options.changeListeners.add(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				repaint();
			}			
		});
				
		add(new JLabel("Part Of Speech Label",JLabel.CENTER));
		
		JPanel posColors = new JPanel() {
			public void paintComponent(Graphics g2) {
				super.paintComponent(g2);
				
				Graphics2D g = (Graphics2D)g2;
				
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
								
				g.setColor(Color.WHITE);
				g.fillRect(0,0,getWidth(),getHeight());
				
				Font f = g.getFont();
				g.setFont(new Font(f.getName(),Font.PLAIN,10));
				
				for(int i=0;i<WordNode.posNames.length;i++) {
					
					int widthAvailable = getWidth() - 40;
					int widthEach = widthAvailable / WordNode.posNames.length;
					
					int radius = Math.min(widthEach / 3, 30);
					int x = 20 + widthEach * i + widthEach / 2;
					int y = getHeight() / 2 - 12;
					g.setColor(WordNode.posColors[i]);
					g.fillOval(x-radius,y-radius,2*radius,2*radius);
					
					g.setColor(Color.BLACK);
					g.drawOval(x-radius,y-radius,2*radius,2*radius);
					
					
					String name = WordNode.posNames[i];
					FontMetrics fm = g.getFontMetrics();
					int width = fm.stringWidth(name);
					g.drawString(name, x-width/2, getHeight() - 5);
				}
			}
		};
		posColors.setPreferredSize(new Dimension(0,50));
		posColors.setBackground(Color.WHITE);
		posColors.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		add(posColors);
		
		add(new JLabel("Word Frequency",JLabel.CENTER));
		
		JPanel frequency = new JPanel() {
			public void paintComponent(Graphics g2) {
				super.paintComponent(g2);
				
				Graphics2D g = (Graphics2D)g2;
				g.setColor(Color.WHITE);
				g.fillRect(0,0,getWidth(),getHeight());
				
				Font f = g.getFont();
				g.setFont(new Font(f.getName(),Font.PLAIN,10));
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				int steps = 7;
				int size = 1;
				
				for(int i=0;i<steps;i++) {
					
					int widthAvailable = getWidth() - 40;
					int widthEach = widthAvailable / steps;
					
					int radius = WordNode.getRadius(options, size);
					int x = 20 + widthEach * i + widthEach / 2;
					int y = getHeight() / 2 - 12;
					
					g.setColor(Color.BLACK);
					g.fillOval(x-radius,y-radius,2*radius,2*radius);
					
					size *= 10;
					
					String name = "10^" + i;
					FontMetrics fm = g.getFontMetrics();
					int width = fm.stringWidth(name);
					g.drawString(name, x-width/2, getHeight() - 5);
				}
			}
		};
		frequency.setPreferredSize(new Dimension(0,80));
		frequency.setBackground(Color.WHITE);
		frequency.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		add(frequency);
		
	}
}
