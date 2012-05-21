package relations.sspace;

import java.awt.Color;
import edu.ucla.sspace.common.SemanticSpace;
import edu.ucla.sspace.ri.RandomIndexing;
import gui.WordMap;

public class WordRelatorRandomIndexing extends WordRelatorSemanticSpace {

	private static final long serialVersionUID = 2323036747981819134L;
	
	@SuppressWarnings("rawtypes")
	public final static Class wizardPanel = WizardRandomIndexing.class;
	public final static String description = "Random Indexing (RI) is a word co-occurrence based approach to statistical semantics. RI uses statistical approximations of the full word co-occurrence data to achieve dimensionality reduction. This results in a much quicker running time and fewer required dimensions.";
	public final static String typeName = "Random Indexing";
	
	public WordRelatorRandomIndexing(Color color, String name, WordMap wordMap) {
		super(color, name, getSemanticSpace(), wordMap);
	}
	
	public static SemanticSpace getSemanticSpace() {
		return new RandomIndexing();
	}

}