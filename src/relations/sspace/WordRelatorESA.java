package relations.sspace;

import java.awt.Color;
import java.io.IOException;
import edu.ucla.sspace.common.SemanticSpace;
import edu.ucla.sspace.esa.ExplicitSemanticAnalysis;
import gui.WordMap;

public class WordRelatorESA extends WordRelatorSemanticSpace {

	private static final long serialVersionUID = -450553637080569953L;
	
	@SuppressWarnings("rawtypes")
	public final static Class wizardPanel = WizardESA.class;
	public final static String description = "A method that represents the meaning of texts in a high-dimensional space of concepts derived from Wikipedia (Computing Semantic Relatedness using Wikipedia-based Explicit Semantic Analysis, Evgeniy Gabrilovich and Shaul Markovitch).";
	public final static String typeName = "Explicit Semantic Analysis";
	
	public WordRelatorESA(Color color, String name, WordMap wordMap) {
		super(color, name, getSemanticSpace(), wordMap);
	}
	
	public static SemanticSpace getSemanticSpace() {
		try {
			return new ExplicitSemanticAnalysis();
		} catch (IOException e) {
			return null;
		}
	}

}