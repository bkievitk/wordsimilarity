package tools;

import java.awt.Graphics;
import java.awt.image.*;
import javax.swing.*;

public class PictureFrame {

	public static void makeFrame(final BufferedImage image) {
		JFrame frame = new JFrame();
		frame.setSize(image.getWidth() + 10, image.getHeight() + 60);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.add(new JPanel() {
			private static final long serialVersionUID = -5186695181116080555L;
			public void paintComponent(Graphics g) {
				g.drawImage(image, 0, 0, this);
			}
		});
		frame.setVisible(true);
	}
}
