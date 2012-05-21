package tools;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A Panel to allow a user to select a color.
 * A custom tool was used instead of JColorSelector for reduced size, as well
 * as an issue with it not firing an event for reselection of the same color.
 * READY
 * @author brentkk
 */

public class MyColorPanel extends JPanel {

	private static final long serialVersionUID = 5760560272794217761L;
	
	// This color set was taken from the MSPaint standard colors.		
	public static int[] colorValueSet = {
			0x000000,
			0x808080,
			0x800000,
			0x808000,
			0x008000,
			0x008080,
			0x000080,
			0x800080,
			0x808040,
			0x004040,
			0x0080FF,
			0x004080,
			0x8000FF,
			0x804000,
			0xFFFFFF,
			0xC0C0C0,
			0xFF0000,
			0xFFFF00,
			0x00FF00,
			0x00FFFF,
			0x0000FF,
			0xFF00FF,
			0xFFFF80,
			0x00FF80,
			0x80FFFF,
			0x8080FF,
			0xFF0080,
			0xFF8040};
	
	public static Color[] colorSet = toColor(colorValueSet);

	// Color set.
	private Color[][] colors;
	
	// Sizes.
	private int width = 15;
	private int height = 15;
	private int space = 2;
	
	// Current color.
	private Color color = null;

	// Listeners.
	private Vector<ChangeListener> listeners = new Vector<ChangeListener>();
	
	private JPanel palletPanel;
	private JPanel splotchPanel;
	private JPanel opacityPanel;
	
	/**
	 * Convert hex values into colors.
	 * @param color
	 * @return
	 */
	private static Color[] toColor(int[] color) {
		Color[] ret = new Color[color.length];
		for(int i=0;i<ret.length;i++) {
			ret[i] = new Color(color[i]);
		}
		return ret;
	}
	
	/**
	 * Get the currently selected color.
	 * @return
	 */
	public Color getColor() {
		return color;
	}
	
	private void colorChanged() {
		for(ChangeListener l : listeners) {
			l.stateChanged(new ChangeEvent(this));
		}
	}
	
	@SuppressWarnings("serial")
	public MyColorPanel(boolean showSplotch, Color colorStart, int colorsAcross) {
		colors = buildPallet(colorsAcross);
		color = colorStart;

		final MyColorPanel myThis = this;
		
		setLayout(new BorderLayout());
		
		final JSlider opacity = new JSlider(0,255);
		if(colorStart != null) {
			opacity.setValue(colorStart.getAlpha());
		}
		
		opacity.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(color != null) {
					color = new Color(color.getRed(),color.getGreen(),color.getBlue(),opacity.getValue());
					myThis.repaint();
					colorChanged();
				}
			}			
		});
		
		palletPanel = new JPanel() {

			/**
			 * Set dimensions based on colors and sizes.
			 */
			public Dimension getPreferredSize() {
				return new Dimension((width+space) * (colors.length) + space,(height+space) * (colors[0].length) + space);
			}
			
			/**
			 * Render color boxes.
			 */
			public void paintComponent(Graphics g) {
				
				// Help center.
				Dimension size = this.getSize(null);
				Dimension preff = this.getPreferredSize();		
				int offX = (size.width - preff.width) / 2;
				int offY = (size.height - preff.height) / 2;
				
				g.setColor(this.getBackground());
				g.fillRect(0,0,size.width,size.height);
				
				// Draw all buttons.
				for(int x=0;x<colors.length;x++) {
					for(int y=0;y<colors[x].length;y++) {
						
						// Only if a color has been set.
						if(colors[x][y] != null) {
							int px = x*(width + space) + offX;
							int py = y*(height + space) + offY;
												
							g.setColor(colors[x][y]);
							g.fillRect(px, py, width, height);
							
							g.setColor(Color.LIGHT_GRAY);
							g.drawRect(px, py, width, height);
						}
					}
				}
				
				// Draw black over.
				if(!this.isEnabled()) {
					Color cover = new Color(0,0,0,150);
					g.setColor(cover);
					g.fillRect(0,0,size.width,size.height);
				}
			}
		};
				
		palletPanel.addMouseListener( new MouseListener() {
			
			/**
			 * When they click, find the color clicked on.
			 */
			public void mouseClicked(MouseEvent e) {
				if(myThis.isEnabled()) {
					
					// Offset centering.
					Dimension size = palletPanel.getSize(null);
					Dimension preff = palletPanel.getPreferredSize();		
					int offX = (size.width - preff.width) / 2;
					int offY = (size.height - preff.height) / 2;
					
					// Get color.
					int x = (e.getX() - offX) / (width + space);
					int y = (e.getY() - offY) / (height + space);
					
					// Valid location.
					if(x < 0 || x >= colors.length || y < 0 || y > colors[0].length) {
						color = null;
					} else {
						color = colors[x][y];
					}
					
					// Update listeners.
					colorChanged();
					repaint();
				}
			}
			
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
		});
		
		if(showSplotch) {
			splotchPanel = new JPanel() {
	
				public Dimension getPreferredSize() {
					return new Dimension((width+space) * 2, (height+space) * 2);
				}
				
				public void paintComponent(Graphics g) {
					if(color != null) {
						int px = (getWidth() - width * 2) / 2;
						int py = (getHeight() - height * 2) / 2;
						
						g.setColor(Color.WHITE);
						g.fillRect(px, py, width * 2 + space, height * 2 + space);
						
						g.setColor(color);
						g.fillRect(px, py, width * 2 + space, height * 2 + space);
						
						g.setColor(Color.LIGHT_GRAY);
						g.drawRect(px, py, width * 2 + space, height * 2 + space);			
					}
				}
			};
			add(splotchPanel,BorderLayout.EAST);
		}
				
		opacityPanel = new JPanel(new BorderLayout());
		opacityPanel.add(opacity,BorderLayout.CENTER);
		opacityPanel.add(new JLabel("opacity"),BorderLayout.WEST);
		
		add(palletPanel,BorderLayout.CENTER);
		add(opacityPanel,BorderLayout.SOUTH);
		
	}

	public void setColor(Color color, boolean update) {
		this.color = color;
		if(update) {
			colorChanged();
			repaint();
		}
	}
	
	/**
	 * This will fire when a color is selected.
	 * @param l
	 */
	public void addChangeListener(ChangeListener l) {
		listeners.add(l);
	}

	/**
	 * Remove listener.
	 * @param l
	 */
	public void removeChangeListener(ChangeListener l) {
		listeners.remove(l);
	}

	/**
	 * Use colorValueSet to convert into a rectangle of color values.
	 * @param width
	 * @return
	 */
	public static Color[][] buildPallet(int width) {
				
		int height = (int)Math.ceil(colorValueSet.length / (float)width);
		Color[][] ret = new Color[width][height];
		
		// Add colors to 2D box.
		int index = 0;
		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++) {
				if(index >= colorValueSet.length) {
					ret[x][y] = null;
				} else {
					ret[x][y] = new Color(colorValueSet[index]);
				}
				index++;
			}
		}
		
		return ret;
	}
	
}
