package tools;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Vector;

import javax.imageio.ImageIO;

import tools.json.JSONArray;
import tools.json.JSONException;
import tools.json.JSONObject;

public class GoogleImage {
		
	/**
	 * Retrieve images from a Google search and place them in the given directory.
	 * Will create sub-directory to root named "name" and will fill with images named "name"x.jpg where x is the image number.
	 * @param name		Search item. Must be properly formatted for url.
	 * @param root		Directory to use as root to save to.
	 * @param number	Number of items to look up.
	 * @param overwrite	Overwrite the directory if it already exists.
	 */
	public static Vector<BufferedImage> retrieveImages(String word, int count) {
		
		Vector<BufferedImage> images = new Vector<BufferedImage>();
				
		int itemCount = 0;
		
		// Item on Google paging system.
		int theirItem = 0;
		
		HashSet<String> urlsFound = new HashSet<String>();
		
		// Loop until you have enough items and are told to return.
		while(true) {
			
			try {
				
				// Extended search parameters.
				// http://code.google.com/apis/ajaxsearch/documentation/reference.html#_class_GSearch
				
				// Connect to URL.
				// Search for "name".
				// Start on item "item"
				URL url = new URL("http://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + word + "&start=" + theirItem);
				URLConnection connection = url.openConnection();

				// Set your referer name.
				connection.addRequestProperty("Referer", "http://www.my-ajax-site.com");

				// Read results.
				String line;
				StringBuilder builder = new StringBuilder();
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				while((line = reader.readLine()) != null) {
					builder.append(line);
				}

				// Use JSON to connect.
				JSONObject json = new JSONObject(builder.toString());
				JSONObject responseData = json.getJSONObject("responseData");
				JSONArray responses = responseData.getJSONArray("results");
				
				for(int i=0;i<responses.length();i++) {
					JSONObject result = responses.getJSONObject(i);
					try {
						String unescapedUrl = result.getString("unescapedUrl");
						
						if(!urlsFound.contains(unescapedUrl)) {
							urlsFound.add(unescapedUrl);
							images.add(ImageIO.read(new URL(unescapedUrl)));
							itemCount++;

							if(itemCount >= count) {
								return images;
							}
						}
					} catch(Exception e) {
					}
				}
				
				theirItem += 5;
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch(IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}		
}


