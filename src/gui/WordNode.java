package gui;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Vector;

import relations.wordnet.WordRelatorWordNet;

import edu.mit.jwi.item.POS;


public class WordNode implements Comparable<WordNode>, Serializable {
	
	private static final long serialVersionUID = 2665658187504594735L;
	
	public String word;
	public double[] location;
	private int count;
	public BufferedImage img = null;

	public static final int X = 0;
	public static final int Y = 1;
	public static final int Z = 2;
	
	public void setImage(BufferedImage img) {
		this.img = img;
	}
	
	public int getCount() {
		return count;
	}
	  
	public int getX() {
		return (int)location[X];
	}
	
	public int getY() {
		return (int)location[Y];
	}
	
	public Point getLocation() {
		return new Point((int)location[X],(int)location[Y]);
	}
	
	
	public static Color[] posColors = {
		new Color(0,0,0,100),		
		new Color(255,0,0,100),
		new Color(0,255,0,100),
		new Color(0,0,255,100),
		new Color(255,255,0,100),
		new Color(255,255,255,100),};
	
	public static String[] posNames = {"Unk","Adj","Adv","Noun","Verb","Mult"};
	//public Color posColor = posColors[0]; 
	private int pos;
	
	public void updatePOS() {
		Vector<POS> poss = WordRelatorWordNet.getPOS(word);
		if(poss.size() == 0) {
			pos = 0;
		} else if(poss.size() == 1) {
			if(poss.get(0).equals(POS.ADJECTIVE)) {
				pos = 1;
			} else if(poss.get(0).equals(POS.ADVERB)) {
				pos = 2;
			} else if(poss.get(0).equals(POS.NOUN)) {
				pos = 3;
			} else if(poss.get(0).equals(POS.VERB)) {
				pos = 4;
			}
		} else {
			pos = 5;
		}
	}
	
	public Color getColor(Options options) {
		if(options.coloringType == Options.COLORING_CONSTANT) {
			return options.nodeColor;
		} else if(options.coloringType == Options.COLORING_POS) {
			return posColors[pos];
		} else {
			return Color.BLACK;
		}
	}
	
	public void setCount(int count) {
		this.count = count;
	}

	public void setPoS(int pos) {
		this.pos = pos;
	}
	
	public int getPoS() {
		return pos;
	}
	
	public void incrementCount() {
		count ++;
	}
	
	public int getRadius(Options options) {
		return getRadius(options,count);
	}
	
	public static int getRadius(Options options, int count) {
		int radius = 10;
		switch(options.getScallingType()) {
			case Options.SCALLING_CONSTANT:
				radius = 10 * options.getScallingFactor() / 100;
				break;
			case Options.SCALLING_LINEAR:
				radius = count / 1000 * options.getScallingFactor() / 100;
				break;
			case Options.SCALLING_ROOT:
				radius = (int)(Math.sqrt(count) / 10 * options.getScallingFactor() / 100);
				break;
			case Options.SCALLING_LOG:
				radius = (int)(Math.log(count) * 2 * options.getScallingFactor() / 100);
				break;
		}
		return Math.min(50,Math.max(2, radius));
	}
	
	public WordNode(String word) {
		this(word,10);
	}
	
	public WordNode(String word, int count) {
		this.word = word;
		double[] tenVec = {10,10,10};
		this.location = tenVec;
		this.count = count;
		updatePOS();
	}
	
	public String toString() {
		return word;
	}

	public int compareTo(WordNode arg0) {
		return word.compareTo(arg0.word);
	}
	
	public boolean equals(WordNode node) {
		return word.equals(node.word);
	}
	
	public boolean equals(Object node) {
		if(node instanceof WordNode) {
			return equals((WordNode)node);
		}
		return false;
	}
	
	public int hashCode() {
		return word.hashCode();
	}
}
