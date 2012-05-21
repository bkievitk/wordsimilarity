package relations.sspace;

import java.awt.Color;
import edu.ucla.sspace.common.SemanticSpace;
import edu.ucla.sspace.isa.IncrementalSemanticAnalysis;
import gui.WordMap;

public class WordRelatorISA extends WordRelatorSemanticSpace {

	private static final long serialVersionUID = -450553637080569953L;
	
	@SuppressWarnings("rawtypes")
	public final static Class wizardPanel = WizardISA.class;
	public final static String description = "ISA";
	public final static String typeName = "Incremental Semantic Analysis";
	
	public WordRelatorISA(Color color, String name, WordMap wordMap) {
		super(color, name, getSemanticSpace(), wordMap);
	}
	
	public static SemanticSpace getSemanticSpace() {
		return new IncrementalSemanticAnalysis();		
	}

}
