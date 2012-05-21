package tools.image;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class PictureFrame extends JPanel {

	private static final long serialVersionUID = 1L;
	public BufferedImage img;

	public static void makeFrame(BufferedImage img) {
		JFrame frame = new JFrame();
		frame.setSize(img.getWidth(),img.getHeight());
		frame.add(new PictureFrame(img));
		frame.setVisible(true);
	}
	
	public PictureFrame(BufferedImage img) {
		this.img = img;
	}
	
	public void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, this);
	}
}
