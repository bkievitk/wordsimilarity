package tools;

import java.util.Vector;

public class TASABook {
	public String author;
	public String p;
	public String drp;
	public String subject;
	public Vector<String> sentences = new Vector<String>();
	
	public int getGradeLevel() {
		double drp = Double.parseDouble(this.drp);
		if(drp < 51) {
			return 0;
		} else if(drp < 59) {
			return 1;
		} else if(drp < 62) {
			return 2;
		} else if(drp < 67) {
			return 3;
		} else if(drp < 73) {
			return 4;
		} else {
			return 5;
		}
	}
	
	public void show() {
		System.out.println("Author:  " + author);
		System.out.println("P:       " + p);
		System.out.println("DRP:     " + drp);
		System.out.println("Subject: " + subject);
		for(String s : sentences) {
			System.out.println(s);
		}
	}
	
	public void quickClean() {
		Vector<String> newSentences = new Vector<String>();
		for(String s : sentences) {
			s = s.toLowerCase();
			s = s.replaceAll("  +", " ");
			s = s.replaceAll("[^a-z ]", "");
			s = s.trim();
			
			if(s.length() > 0) {
				newSentences.add(s);
			}
		}
		sentences = newSentences;
	}	
}
