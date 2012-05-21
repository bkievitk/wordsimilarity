package tools.image;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

public class ImageNormalizer {
	
	/**
	 * All images are to be put through the normalizer before being saved or used.
	 * Not sure exactly what this will do yet. It will probably do some simple filtering.
	 * @param image		Image to filter.
	 * @param width		Width of output image.
	 * @param height	Height of output image.
	 * @return
	 */
	public static BufferedImage normalizeSimple(Image image, int width, int height) {
		BufferedImage buff = toBufferedImage(image, width, height);
		// Apply filters here.
		return buff;
	}
	
	/**
	 * This method returns a buffered image with the contents of an image. 
	 * @param image
	 * @return
	 */
	public static BufferedImage toBufferedImage(Image image, int width, int height) {
		if (image instanceof BufferedImage) { 
			return (BufferedImage)image; 
		} 
		
		// This code ensures that all the pixels in the image are loaded 
		image = new ImageIcon(image).getImage(); 
		
		// Create a buffered image with a format that's compatible with the screen 
		BufferedImage bimage = null; 
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment(); 
		try { 
			
			// Determine the type of transparency of the new buffered image 
			int transparency = Transparency.OPAQUE; 
			
			// Create the buffered image 
			GraphicsDevice gs = ge.getDefaultScreenDevice(); 
			GraphicsConfiguration gc = gs.getDefaultConfiguration(); 
			bimage = gc.createCompatibleImage( width, height, transparency); 
			
		} catch (HeadlessException e) { 
			e.printStackTrace();
			return null;
		} 
		
		if (bimage == null) { 
			// Create a buffered image using the default color model 
			int type = BufferedImage.TYPE_INT_RGB; 
			bimage = new BufferedImage(width, height, type); 
		} 
		// Copy image to buffered image 
		Graphics g = bimage.createGraphics(); 
		
		// Paint the image onto the buffered image 
		g.drawImage(image, 0, 0, width, height, null); 
		g.dispose(); 
		
		return bimage; 
	}	
}
