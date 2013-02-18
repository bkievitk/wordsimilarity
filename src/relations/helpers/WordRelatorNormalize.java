package relations.helpers;

import gui.WordMap;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Vector;

import relations.WordRelator;
import transformations.Transformation;

public class WordRelatorNormalize extends WordRelator  {

	private static final long serialVersionUID = -4190443865957432301L;
	public WordRelator relator;
	
	public LinkedList<Transformation> transforms = new LinkedList<Transformation>();
	
	@SuppressWarnings("rawtypes")
	public final static Class wizardPanel = WizardNormalize.class;
	public final static String description = "This will create a set of connections that are a normalized version of the original.";
	public final static String typeName = "Normalize";
	
	public WordRelatorNormalize(Color color, String name, WordMap wordMap) {
		super(color, name, wordMap);
	}

	public double getDistance(String word1, String word2) {
		double val = relator.getDistance(word1, word2);
		for(Transformation t : transforms) {
			val = t.transform(val);
		}
		return val;
	}

	public String toString() {
		return "Normalized {" + relator.toString() + "}";
	}
}

