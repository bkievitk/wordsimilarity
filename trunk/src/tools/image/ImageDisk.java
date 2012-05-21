package tools.image;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

public class ImageDisk {
	
	/**
	 * Load an image from a file.
	 * @param f
	 * @return
	 */
	public static BufferedImage loadImageFile(File f) {
		try {
		    return ImageIO.read(f);
		} catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Load an image from the web.
	 * @param urlString	Web url.
	 * @return Retrieved image.
	 */
	public static Image loadImageWeb(String urlString) {
		URL url;
		try {
			
			// Indicate waiting.
			System.out.println("Waiting for image. " + urlString);
			
			// Retrieve image.
			url = new URL(urlString);
			Image image = Toolkit.getDefaultToolkit().createImage(url);

			// Track attempt to retrieve image.
			int count = 0;
			
			// While you do not have height and width information.
			while(image.getHeight(null) < 0 || image.getWidth(null) < 0) {
				
				// Update counter.
				count++;
				
				// Pause for data to arrive.
				try { Thread.sleep(100); } catch (InterruptedException e) {	e.printStackTrace(); }

				// If you have waited for 2 seconds, then time out.
				if(count > 20) {
					System.out.println("Image timed out.");
					return null;
				}
			}
			System.out.println("Image recieved.");
			
			return image;
			
		} catch (MalformedURLException e) {
			
			// You fed it a bad url for the image.
			e.printStackTrace();
			return null;
		}
	}	
	
	/**
	 * Save a BufferedImage as a .pgm file.
	 * @param img
	 * @param file
	 */
	public static void savePGM(BufferedImage img, File file) {
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(file));
			
			w.write("P5\n");
			w.write(img.getWidth() + " " + img.getHeight() +"\n");
			w.write(255 + "\n");
			
			for(int y=0;y<img.getWidth();y++) {
				for(int x=0;x<img.getWidth();x++) {
					Color c = new Color(img.getRGB(x, y));
					int avg = (c.getRed() + c.getGreen() + c.getBlue())/3;
					w.write(avg);
				}
			}
			
			w.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
