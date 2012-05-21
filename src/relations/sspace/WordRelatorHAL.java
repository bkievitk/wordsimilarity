package relations.sspace;

import java.awt.Color;
import java.util.Properties;

import edu.ucla.sspace.common.SemanticSpace;
import edu.ucla.sspace.hal.HyperspaceAnalogueToLanguage;
import gui.WordMap;

public class WordRelatorHAL extends WordRelatorSemanticSpace {

	private static final long serialVersionUID = -8969398037176447962L;
	
	@SuppressWarnings("rawtypes")
	public final static Class wizardPanel = WizardHAL.class;
	public final static String description = "A word by word matrix of co-occurences is created. Low entropy elements are then removed from the matrix.";
	public final static String typeName = "Hyperspace Analogue to Language";
	
	public static int WINDOW_SIZE_START = 3;
	public static int RETAIN_SIZE_START = 3;
	public static double THRESHOLD_SIZE_START = .5;
	
	public int windowSize = WINDOW_SIZE_START;
	public int retain = RETAIN_SIZE_START;
	public double threshold = THRESHOLD_SIZE_START;
	
	public WordRelatorHAL(Color color, String name, WordMap wordMap) {
		super(color, name, getSemanticSpace(), wordMap);
	}
	
	public static SemanticSpace getSemanticSpace() {
		return new HyperspaceAnalogueToLanguage();
	}
	
	public void finalizeSpace() {
		Properties properties = new Properties();
		properties.setProperty("windowSize", windowSize + "");
		properties.setProperty("retain", retain + "");
		properties.setProperty("threshold", threshold + "");
		semanticSpace.processSpace(properties);
	}

}