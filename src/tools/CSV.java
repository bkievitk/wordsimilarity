package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class CSV {
	public BufferedReader r;
	public Vector<String[]> lines = new Vector<String[]>();
	public String splitExpression = ",";
		
	public static void applyToAll(File f) {
		if(f.isDirectory()) {
			for(File f2 : f.listFiles()) {
				applyToAll(f2);
			}
		}
		else if(FileTools.isType(f, ".dat")) {
			System.out.println(f);
			CSV csv = new CSV(f);
			csv.splitExpression = "[, ]+";
			csv.readAll();
			csv.removeLineSize(2, 2);
			csv.splitExpression = ",";
			csv.write(f);
		}
	}
	
	public void dump() {
		for(String[] s : lines) {
			System.out.println(join(s, splitExpression));
		}
	}
	
	/**
	 * Initialize with the file that you are linking to.
	 * @param f
	 */
	public CSV(File f) {
		try {
			r = new BufferedReader(new FileReader(f));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void removeLine(int index) {
		lines.remove(index);
	}
	
	public void removeLine(int start, int step) {
		for(int i=start;i<lines.size();i+= (step - 1)) {
			lines.remove(i);
		}
	}
	
	public void removeLineSize(int start, int size) {
		for(int i=start;i<lines.size();i++) {
			if(lines.get(i).length < size) {
				System.out.println("Removing line " + i + " " + lines.get(i).length);
				lines.remove(i);
				i--;
			}
		}
	}
	
	public static String join(Object[] words, String spacer) {
		String s = "";
		for(int i=0;i<words.length;i++) {
			s = s + words[i];
			if(i < words.length-1) {
				s = s + spacer;
			}
		}
		return s;
	}
	
	public void write(File f) {
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(f));
			for(String[] str : lines) {
				w.write(join(str,splitExpression) + "\n");
			}
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieve a line from the file.
	 * Do not save to memory.
	 * @return
	 */
	public String[] getLine() {
		try {
			String line = r.readLine();
			if(line == null) {
				close();
				return null;
			}
			return line.split(splitExpression);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Retrieve a line from the file into buffer.
	 * @return False if done.
	 */
	public boolean readLine() {
		String[] line = getLine();
		if(line == null) {
			return false;
		}
		lines.add(line);
		return true;
	}
	
	/**
	 * Real all line in the file.
	 */
	public void readAll() {
		while(readLine());
		close();
	}
	
	/**
	 * Clean up.
	 */
	public void close() {
		try {
			r.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
