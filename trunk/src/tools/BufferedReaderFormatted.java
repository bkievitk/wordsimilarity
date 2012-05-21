package tools;

import gui.SentenceCleaner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;

public class BufferedReaderFormatted extends BufferedReader {

	private LinkedList<SentenceCleaner> cleaners;
	
	public BufferedReaderFormatted(Reader in, LinkedList<SentenceCleaner> cleaners) {
		super(in);
		this.cleaners = cleaners;
	}
	
	public String readLine() throws IOException {
		String line = super.readLine();
		if(line != null) {
			for(SentenceCleaner cleaner : cleaners) {
				line = cleaner.clean(line);
			}
		}		
		
		//System.out.println("Read: " + line);
		return line;
	}

}
