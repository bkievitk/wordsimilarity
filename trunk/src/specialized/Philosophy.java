package specialized;

import java.io.*;
import java.net.URL;

public class Philosophy {

	public static void main(String[] args) {
			
		/*
		try {
			//internetEncyclopediaOfPhilosophy(new File("internetEncyclopediaOfPhilosophy"));
			internetEncyclopediaOfPhilosophy(new File("internetEncyclopediaOfPhilosophy"),new File("internetEncyclopediaOfPhilosophy_byURL"));
			//stanfordEncyclopediaOfPhilosophy(new File("stanfordEncyclopediaOfPhilosophy"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}
	
	public static void stanfordEncyclopediaOfPhilosophy(File directory) throws IOException {
		String root = "http://plato.stanford.edu/";
		String contents = root + "contents.html";

		URL url = new URL(contents);
		BufferedReader r = new BufferedReader(new InputStreamReader(url.openStream()));
		String line;
		
		while((line = r.readLine()) != null) {
			String startKey = "<a href=\"entries/";
			int start = line.indexOf(startKey);
			if(start >= 0) {
				start += 9;
				int end = line.indexOf('"',start+1);
				String urlName = line.substring(start,end);
				String name = line.substring(start+8,end-1);
				
				File outFile = new File(directory + "/" + name + ".txt");
				
				if(outFile.exists()) {
					System.out.println("Skipping " + outFile);
				} else {
					System.out.println("Writing to " + outFile);

					try {
						URL page = new URL(root + urlName);
						BufferedReader r2 = new BufferedReader(new InputStreamReader(page.openStream()));
						BufferedWriter w = new BufferedWriter(new FileWriter(outFile));
											
						while(!(line = r2.readLine()).contains("<!--DO NOT MODIFY THIS LINE AND ABOVE-->"));
						while(!(line = r2.readLine()).contains("<div id=\"pubinfo\">"));
						while(	(line = r2.readLine()) != null &&
								!line.contains("<a name=\"Bib\"") &&
								!line.contains("<a name=\"BibGenWor\">") &&
								!line.contains("<h2>Bibliography</h2>") && 
								!line.contains("<h2><a name=\"Bibliography\">Bibliography</a></h2>")) {
							line = line.replaceAll("<[^>]*>", "");
							line = line.trim();
							line = replaceWebMarkers(line);
							w.write(line + "\n");
						}
						
						if(line == null) {
							System.out.println("PAUSED");
							System.in.read();
						} else if(!line.contains("<a name=\"Bib\">")) {
							System.out.println("End line: " + line);
						}
						
						w.close();
					} catch(IOException e) {
						outFile.delete();
						throw(e);
					}
				}
			}
		}
	}
	
	
	public static void internetEncyclopediaOfPhilosophy(BufferedReader in, BufferedWriter out) throws IOException {
		String line;
		String startKey = "<div class=\"entry\">";
		
		while((line = in.readLine()) != null && !line.contains(startKey));
		if(line == null) {
			System.out.println("Start exception.");
			throw(new IOException());
		}
		
		while(	 (line = in.readLine()) != null &&
				!(line.contains("References and Further Reading") && !line.contains("#")) &&
				!(line.contains("Author Information") && !line.contains("#"))
				
				
				
				
				) {
			line = line.replaceAll("<[^>]*>", "");
			line = line.trim();
			line = replaceWebMarkers(line);
			out.write(line + "\n");
		}
		
		if(line == null) {
			throw(new IOException());
		}
		out.close();
	}
	
	public static void internetEncyclopediaOfPhilosophy(File directoryIn, File directoryOut) throws IOException {
		for(File f : directoryIn.listFiles()) {
			String newFileName = directoryOut.getPath() + File.separatorChar + replaceWebMarkers(f.getName());
			BufferedReader in = new BufferedReader(new FileReader(f));
			BufferedWriter out = new BufferedWriter(new FileWriter(new File(newFileName)));
			try {
				internetEncyclopediaOfPhilosophy(in,out);
			} catch(IOException e) {
				e.printStackTrace();
				System.out.println(f);
				throw(e);
			}
		}
	}
	
	public static void internetEncyclopediaOfPhilosophy(File directory) throws IOException {
		String root = "http://www.iep.utm.edu/";
		for(char c ='a'; c<= 'z'; c++) {
			URL url = new URL(root + c);
			BufferedReader r = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			while(!(line = r.readLine()).contains("index-list"));
			
			while(!(line = r.readLine()).contains("</div><!-- /entry -->")) {
				if(line.startsWith("<li><a href=")) {
					int start = line.indexOf("href=\"") + 6;
					int stop = line.indexOf("\"",start+1);

					int start2 = line.indexOf("title=") + 7;
					int stop2 = line.indexOf("\"", start2);

					if(start2 < 0 || stop2 < 0) {
						System.out.println(line);
					}
					
					String name = line.substring(start2, stop2);
					name = name.replace('\\', '-');
					name = name.replace('/', '-');
					
					File outFile = new File(directory + "/" + line.substring(start+23, stop-1) + ".txt");
					
					if(outFile.exists()) {
						System.out.println("Skipping " + outFile);
					} else {
						System.out.println("Writing " + outFile);
						
						
						URL page = new URL(line.substring(start, stop));
						BufferedReader r2 = new BufferedReader(new InputStreamReader(page.openStream()));
						BufferedWriter w = new BufferedWriter(new FileWriter(outFile));
						
						while((line = r2.readLine()) != null) {
							w.write(line + "\n");
						}
						
						//internetEncyclopediaOfPhilosophy(r2, w);
						
						
						w.close();
						
						
					}
				}
			}	
		}
	}
	
	public static String replaceWebMarkers(String str) {
		while(true) {
			int start = str.indexOf("&#");
			if(start >= 0) {
				int stop = str.indexOf(";",start);
				
				char c;
				if(str.charAt(start+2) == 'x') {
					c = (char)Integer.parseInt(str.substring(start+3,stop),16);
				} else {
					c = (char)Integer.parseInt(str.substring(start+2,stop));
				}
				
				str = str.substring(0,start) + c + str.substring(stop + 1);
			} else {

				str = str.replaceAll("&ldquo;", "'");
				str = str.replaceAll("&rdquo;", "'");
				str = str.replaceAll("â€.", "\"");
				return str;
			}
		}
	}
}
