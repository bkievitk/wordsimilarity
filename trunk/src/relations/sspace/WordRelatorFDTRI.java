package relations.sspace;

import java.awt.Color;
import edu.ucla.sspace.common.SemanticSpace;
import edu.ucla.sspace.tri.FixedDurationTemporalRandomIndexing;
import gui.WordMap;

public class WordRelatorFDTRI extends WordRelatorSemanticSpace {

	private static final long serialVersionUID = -8609014775753162370L;
	
	@SuppressWarnings("rawtypes")
	public final static Class wizardPanel = WizardFDTRI.class;
	public final static String description = "Analyzes how words change over time using Tensor matricies.";
	public final static String typeName = "Fixed Duration Temporal Random Indexing";
	
	public WordRelatorFDTRI(Color color, String name, WordMap wordMap) {
		super(color, name, getSemanticSpace(), wordMap);
	}
	
	public static SemanticSpace getSemanticSpace() {
		return new FixedDurationTemporalRandomIndexing();		
	}

}