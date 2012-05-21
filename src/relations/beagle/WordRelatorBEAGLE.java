package relations.beagle;

import gui.WordMap;

import java.awt.Color;
import java.util.Set;
import relations.WordRelator;

public class WordRelatorBEAGLE extends WordRelator {

	private static final long serialVersionUID = -247318239939677662L;
	
	public BEAGLE beagle;
	
	@SuppressWarnings("rawtypes")
	public final static Class wizardPanel = WizardBEAGLESetup.class;
	public final static String description = "This will compute either context free word relations or order relative word relations as computed by the BEAGLE holographic language model.";
	public final static String typeName = "BEAGLE";
	
	public WordRelatorBEAGLE(Color color, String name, int dimensions, WordMap wordMap) {
		super(color, name, wordMap);
		
		OptionsModule options = new OptionsModule(dimensions);		
		beagle = new BEAGLE(options);
	}

	public double getDistance(String word1, String word2) {
		Thought t1 = beagle.thoughts.get(word1);
		Thought t2 = beagle.thoughts.get(word2);
		
		if(t1 == null || t2 == null) {
			return 0;
		}
		
		return VectorTools.getCosine(t1.lexical,t2.lexical);
	}
	
	public Set<String> getWords() {
		return beagle.thoughts.keySet();
	}
	
	public void learn(String[] sentence) {
		super.learn(sentence);
		Thought[] thoughts = beagle.toThought(sentence);
		if(beagle.options.learnContext) { beagle.learnContext(thoughts, false); }
		if(beagle.options.learnOrder) { beagle.learnOrder(thoughts, false); }
	}

	public String toString() {
		return "BEAGLE + {" + name + "}";
	}
	
	public Object buildWord(String word) {
		return new Thought(word,beagle.options.dimensions);
	}

}
