package tools;

import java.io.*;

public class TASA {
	
	BufferedReader r;
	
	public TASA(File f) {
		try {
			r = new BufferedReader(new FileReader(f));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public TASABook readBook() {
		TASABook ret = new TASABook();
		String line;
		try {

			// Read until you get a header.
			while((line = r.readLine())!=null) {
				if (line.matches("\\[[a-zA-Z0-9.]*\\] *\\[[a-zA-Z0-9.=#]*\\] *\\[[a-zA-Z0-9.=]*\\]( *\\[[a-zA-Z0-9.=]*\\])?.*")) {
					String[] items = line.split("(\\] *\\[)|(\\[)|(\\])");
					ret.author = items[1];
					ret.p = items[2];
					ret.drp = items[3].substring(4);
					ret.subject = items[4].replaceAll("=Yes", "");
					break;
				} else {
					System.out.println(">>>>> First line was not a header {" + line + "}");
				}
			}
				
			String sentence = "";
			while((line = r.readLine())!=null) {
				if(line.length() > 2 && line.substring(0, 3).equals("[S]")) {
					// New sentence.
					
					// Trim first.
					sentence = sentence.trim();
										
					// If sentence is large enough, then add it.
					if(sentence.length() > 0) {
						ret.sentences.add(sentence);
					}
					
					// Reset sentence to this line.
					sentence = line.substring(3);
					
				} else if(line.length() == 0) {
					// Empty line.
				} else if (line.length() > 0 && line.charAt(0) != '[') {
					// Does not start with [
					sentence = sentence + line;
				} else if (line.matches("\\[[a-zA-Z0-9.]*\\] *\\[[a-zA-Z0-9.=#]*\\] *\\[[a-zA-Z0-9.=]*\\]( *\\[[a-zA-Z0-9.=]*\\])?.*")) {
					r.reset();
					return ret;
				} else {
					System.out.println("Bad line.");
				}				
				r.mark(300);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
