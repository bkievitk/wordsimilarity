package tools;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;

import javax.imageio.ImageIO;


import com.aetrion.flickr.*;
import com.aetrion.flickr.photos.*;

public class FlickrImage {

	@SuppressWarnings("unchecked")
	public static Vector<BufferedImage> retrieveImages(String word, int count) {
		try {

			Vector<BufferedImage> images = new Vector<BufferedImage>();
			
			Transport t = new REST();
			Flickr f = new Flickr("c1942994694d39d889488fed4ae149b4", "99871fed57f624ee", t);
			
			PhotosInterface psi = f.getPhotosInterface();
			
			SearchParameters search = new SearchParameters();
			search.setText(word);
			PhotoList list = psi.search(search, count, 1);

			for (Iterator<Photo> iterator = list.iterator(); iterator.hasNext();) {
			    try {
				    Photo photo = iterator.next();
				    String imageURL = photo.getMediumUrl();
				    URL url = new URL(imageURL);
				    images.add(ImageIO.read(url.openStream()));
			    } catch(Exception e) {
			    	e.printStackTrace();
			    }
			}
			
			return images;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
