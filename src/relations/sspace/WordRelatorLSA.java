package relations.sspace;

import java.awt.Color;
import java.io.IOException;
import edu.ucla.sspace.common.SemanticSpace;
import edu.ucla.sspace.lsa.LatentSemanticAnalysis;
import gui.WordMap;

public class WordRelatorLSA extends WordRelatorSemanticSpace {

	private static final long serialVersionUID = -450553637080569953L;
	
	@SuppressWarnings("rawtypes")
	public final static Class wizardPanel = WizardLSA.class;
	public final static String description = "Document by word matricies are created. Singular Value Decomposition is then used to reduce matrix size.";
	public final static String typeName = "Latent Semantic Analysis";
	
	public WordRelatorLSA(Color color, String name, WordMap wordMap) {
		super(color, name, getSemanticSpace(), wordMap);
	}
	
	public static SemanticSpace getSemanticSpace() {
		try {
			return new LatentSemanticAnalysis();
		} catch (IOException e) {
			return null;
		}
	}

}
