package tools.image;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class ImageTransform {
	public static BufferedImage resize(BufferedImage img, int width, int height) {
		
		// If you are already the right size then do nothing.
		if(img.getWidth() == width && img.getHeight() == height) {
			return img;
		}

		// Resize the image.
		BufferedImage ret = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics g = ret.getGraphics();
		g.drawImage(img, 0, 0, width, height, null);
		return ret;
	}
}
