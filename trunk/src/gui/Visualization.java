package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.Semaphore;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import tools.TransformMy3D;

public class Visualization extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

	private static final long serialVersionUID = -1092676685897254172L;
	
	public WordMap wordMap;
	public Options options;
	public WordNode selected = null;
	public WordNode hoverNode = null;
	
	private BufferedImage rendered = null;
	public JProgressBar progress;
	
	public static final int STATE_RENDER_ONCE = 0;
	public static final int STATE_RENDER_ALWAYS = 1;
	public static final int STATE_RENDER_SMART = 2;
	public int renderState = STATE_RENDER_SMART;
	public boolean imageChanged = true;
		
	public Semaphore render = new Semaphore(1);
	
	public Point mouseDown;
	public int mouseButton;
	
	public void paintComponent(Graphics g) {		
		super.paintComponent(g);
		
		if(imageChanged || imageSizeChanged()) {			
			render.drainPermits();
			render.release();		
			imageChanged = false;
		}

		g.drawImage(rendered, 0, 0, null);
		
		if(wordMap.activeRelations.size() > 0 && wordMap.activeWords.size() == 0) {
			g.setColor(new Color(0,0,0,100));
			String text1 = "No words selected. Go to Tools -> Word Manager to select words.";
			String text2 = "Remember to choose a layout from the Layout menu after.";

			FontMetrics fm = g.getFontMetrics();
			g.drawString(text1, this.getWidth() / 2 - fm.stringWidth(text1) / 2, this.getHeight() / 2 - 8);
			g.drawString(text2, this.getWidth() / 2 - fm.stringWidth(text2) / 2, this.getHeight() / 2 + 8);
		}

		g.setColor(new Color(0,0,0,100));
		FontMetrics fm = g.getFontMetrics();
		String message = "Zoom with scroll wheel, click and drag a node to move it or the screen to pan.";
		g.drawString(message, this.getWidth() - fm.stringWidth(message) - 5, this.getHeight() - 5);
		
		
		if(hoverNode != null) {
			
			fm = g.getFontMetrics();
			int width = 0;
			int height = fm.getHeight() * 3;
			
			String name = "Word: " + hoverNode.word;
			String count = "Count: " + hoverNode.getCount() + "";
			String pos = "PoS: " + WordNode.posNames[hoverNode.getPoS()];
			
			width = Math.max(width, fm.stringWidth(name));
			width = Math.max(width, fm.stringWidth(count));
			width = Math.max(width, fm.stringWidth(pos));
			
			wordMap.renderWordConnections((Graphics2D)g, options, hoverNode,options.getHighlightedConnectionColor());
			
			g.setColor(Color.WHITE);
			g.fillRect(hoverNode.getX(), hoverNode.getY(), width + 10, height + 10);
			g.setColor(Color.BLACK);
			g.drawRect(hoverNode.getX(), hoverNode.getY(), width + 10, height + 10);
			g.drawString(name,  hoverNode.getX() + 5, hoverNode.getY() + fm.getHeight() + 5);
			g.drawString(count, hoverNode.getX() + 5, hoverNode.getY() + fm.getHeight() * 2 + 5);
			g.drawString(pos,   hoverNode.getX() + 5, hoverNode.getY() + fm.getHeight() * 3 + 5);
			
		}		
	}
	
	public BufferedImage getImage() {
		return rendered;
	}
	
	public Visualization(final WordMap wordMap, final Options options, final JProgressBar progress) {
		this.wordMap = wordMap;
		this.options = options;
		this.progress = progress;
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);			

		Thread renderThread = new Thread() {				
			public void run() {
				while(true) {
					
					try {
						render.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					if(getWidth() > 0 && getHeight() > 0) {
						BufferedImage image = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);												
						Graphics2D g2 = (Graphics2D)image.getGraphics();
												
						g2.setColor(options.getBackgroundColor());
						g2.fillRect(0, 0, getWidth(), getHeight());
	
						if(renderState == STATE_RENDER_ONCE || renderState == STATE_RENDER_ALWAYS) {
							if(renderState == STATE_RENDER_ONCE) {
								renderState = STATE_RENDER_SMART;
							}
							wordMap.render(g2, new Dimension(getWidth(),getHeight()),true,progress,options);
						} else if(renderState == STATE_RENDER_SMART) {
							wordMap.render(g2, new Dimension(getWidth(),getHeight()),false,progress,options);
						}
						
						rendered = image;
						repaint();
					}
				}
			}
		};
		renderThread.start();
				
		wordMap.relationChange.add(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				imageChanged = true;
				repaint();
			}			
		});

		wordMap.wordChange.add(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				imageChanged = true;
				repaint();
			}			
		});
		
		options.changeListeners.add(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				imageChanged = true;
				repaint();
			}			
		});		
	}
	
	public boolean imageSizeChanged() {
		return (rendered == null || rendered.getWidth() != getWidth() || rendered.getHeight() != getHeight());
	}
	
	public void setImage() {
		if(imageSizeChanged()) {
			rendered = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);
		}
		Graphics g = rendered.getGraphics();
		g.setColor(options.getBackgroundColor());
		g.fillRect(0, 0, getWidth(), getHeight());
	}		
	
	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		mouseDown = e.getPoint();
		mouseButton = e.getButton();
		
		if(e.getButton() == 1) {
			selected = null;
			hoverNode = null;
			for(WordNode node : wordMap.getActiveWordNodeList()) {
				int dx = node.getX() - e.getX();
				int dy = node.getY() - e.getY();
				int radius = node.getRadius(options);
				if(dx * dx + dy * dy < radius * radius) {
					selected = node;
					break;
				}
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		selected = null;
	}

	public void mouseDragged(MouseEvent e) {
		if(mouseButton == 1) {
			if(selected != null) {
				selected.location[0] = e.getX();
				selected.location[1] = e.getY();
				imageChanged = true;
				repaint();
			} else {
				int dx = mouseDown.x - e.getX();
				int dy = mouseDown.y - e.getY();
				mouseDown = e.getPoint();
				for(WordNode word : wordMap.getActiveWordNodeList()) {
					word.location[0] -= dx;
					word.location[1] -= dy;
				}
				imageChanged = true;
				repaint();
			}
		} else if(mouseButton == 3) {
			
			double dx = (mouseDown.x - e.getX()) * .01;
			double dy = (mouseDown.y - e.getY()) * .01;

			TransformMy3D t = new TransformMy3D(); 
			t = t.combineNew(TransformMy3D.translate(getWidth() / 2, getHeight() / 2, 0));
			t = t.combineNew(TransformMy3D.rotateY(dx));
			t = t.combineNew(TransformMy3D.rotateX(dy));
			t = t.combineNew(TransformMy3D.translate(-getWidth() / 2, -getHeight() / 2, 0));
			
			for(WordNode word : wordMap.activeWordsSorted) {
				word.location = t.apply(word.location);
			}
			mouseDown = e.getPoint();
			imageChanged = true;
			repaint();
			
		}
	}

	public void mouseMoved(MouseEvent e) {
		hoverNode = null;
		for(WordNode node : wordMap.getActiveWordNodeList()) {
			int dx = node.getX() - e.getX();
			int dy = node.getY() - e.getY();
			int radius = node.getRadius(options);
			if(dx * dx + dy * dy < radius * radius) {
				hoverNode = node;
				break;
			}
		}
		repaint();		
	}


	public void mouseWheelMoved(MouseWheelEvent e) {
		int rotations = e.getWheelRotation();

		int mouseX = e.getX();
		int mouseY = e.getY();
		double multiplier = 1.0;
		
		if(rotations > 0) {
			multiplier = .8;
		} else if(rotations < 0) {
			multiplier = 1.2;
		}
		
		for(WordNode word : wordMap.getActiveWordNodeList()) {
			word.location[0] = (word.location[0] - mouseX) * multiplier + mouseX;
			word.location[1] = (word.location[1] - mouseY) * multiplier + mouseY;
		}
		imageChanged = true;
		repaint();
	}
}
