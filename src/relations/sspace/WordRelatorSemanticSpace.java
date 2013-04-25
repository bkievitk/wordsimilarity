package relations.sspace;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Set;

import relations.WordRelator;
import relations.beagle.VectorTools;
import tools.BufferedReaderFormatted;

import edu.ucla.sspace.common.SemanticSpace;
import edu.ucla.sspace.esa.ExplicitSemanticAnalysis;
import edu.ucla.sspace.lsa.LatentSemanticAnalysis;
import edu.ucla.sspace.vector.Vector;
import gui.SentenceCleaner;
import gui.WordMap;

@SuppressWarnings("serial")
public abstract class WordRelatorSemanticSpace extends WordRelator {

	public SemanticSpace semanticSpace;
	
	// Tier 1 Algorithms.
	
	public static void main(String[] args) {
		try {
			//SemanticSpace semanticSpace = new ExplicitSemanticAnalysis();					// *Works
			//SemanticSpace semanticSpace = new FixedDurationTemporalRandomIndexing(); 		// *Works
			//SemanticSpace semanticSpace = new HyperspaceAnalogueToLanguage(); 			// *Works
			SemanticSpace semanticSpace = new LatentSemanticAnalysis();					// *Works
			//SemanticSpace semanticSpace = new RandomIndexing(); 							// Works
			//SemanticSpace semanticSpace = new Coals(); 									// Works (slow?)
			//SemanticSpace semanticSpace = new PurandareFirstOrder(); 						// Error: Cannot run program "vcluster": CreateProcess error=2, The system cannot find the file specified
			//SemanticSpace semanticSpace = new IncrementalSemanticAnalysis(); 				// *Works
			//SemanticSpace semanticSpace = new ReflectiveRandomIndexing(); 				// Works (Slow)
			//SemanticSpace semanticSpace = new StructuredVectorSpace();					// Error: No extractors available
			//SemanticSpace semanticSpace = new DependencyVectorSpace();					// Error: No extractors available
			//SemanticSpace semanticSpace = new Grefenstette();								// Error: Null pointer exception
										
			LinkedList<SentenceCleaner> cleaners = new LinkedList<SentenceCleaner>();
			cleaners.add(SentenceCleaner.getCleaner("Remove Web Tags."));
			cleaners.add(SentenceCleaner.getCleaner("To Lower Case."));
			cleaners.add(SentenceCleaner.getCleaner("Alpha Numeric Only."));
			
			//URL url = new URL("http://en.wikipedia.org/wiki/Dog");
			//BufferedReaderFormatted reader = new BufferedReaderFormatted(new InputStreamReader(url.openStream()),cleaners);
			//BufferedReader reader = new BufferedReaderFormatted(new StringReader("dog cat\ndog wolf\nfish dog"),cleaners);
			//semanticSpace.processDocument(reader);

			semanticSpace.processDocument(new BufferedReaderFormatted(new StringReader("dog dog wolf cat"),cleaners));
			semanticSpace.processDocument(new BufferedReaderFormatted(new StringReader("dog dog cat wolf"),cleaners));
			semanticSpace.processDocument(new BufferedReaderFormatted(new StringReader("fish dog"),cleaners));
			semanticSpace.processDocument(new BufferedReaderFormatted(new StringReader("frog car"),cleaners));
			semanticSpace.processDocument(new BufferedReaderFormatted(new StringReader("car dog"),cleaners));
			semanticSpace.processDocument(new BufferedReaderFormatted(new StringReader("frog wolf"),cleaners));

			
			/*
dog cat
dog wolf
fish dog
frog car
dog car
frog wolf
			 */
			Properties properties = new Properties();
			properties.setProperty(LatentSemanticAnalysis.LSA_DIMENSIONS_PROPERTY, "200");
			properties.setProperty(LatentSemanticAnalysis.RETAIN_DOCUMENT_SPACE_PROPERTY, "true");
			
			semanticSpace.processSpace(properties);
			
			//System.out.println("Document Vector Length: " + ((LatentSemanticAnalysis)semanticSpace).getDocumentVector(0).length());
			//System.out.println("Vec len: " + semanticSpace.getVectorLength());
			//System.out.println("Name: " + semanticSpace.getSpaceName());
			/*
			for(String word : semanticSpace.getWords()) {
				System.out.println("Word: " + word + ":" + semanticSpace.getVector(word).length());
			}
			*/
			//System.out.println("Dist 1 : " + Similarity.cosineSimilarity(semanticSpace.getVector("wolf"), semanticSpace.getVector("cat")));
			//System.out.println("Dist 1 : " + Similarity.cosineSimilarity(semanticSpace.getVector("wolf"), semanticSpace.getVector("dog")));

			double[] v1 = convertVector(semanticSpace.getVector("dog"));
			double[] v2 = convertVector(semanticSpace.getVector("car"));
			try {
				semanticSpace.getVector("fish");
			} catch(Exception e) {
				
			}
			double[] v4 = convertVector(semanticSpace.getVector("wolf"));
			double[] v5 = convertVector(semanticSpace.getVector("cat"));
			//double[] v6 = convertVector(semanticSpace.getVector("frog"));
			
			VectorTools.show(v1);
			VectorTools.show(v2);
			//VectorTools.show(v3);
			VectorTools.show(v4);
			VectorTools.show(v5);
			//VectorTools.show(v6);
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	//testAlgorithm(new LatentSemanticAnalysis()); // Catch IO
	//testAlgorithm(new RandomIndexing());
	//testAlgorithm(new HyperspaceAnalogueToLanguage());
	//testAlgorithm(new FixedDurationTemporalRandomIndexing());
	//testAlgorithm(new Coals());  // Took too long.
	//testAlgorithm(new PurandareFirstOrder());  // Took too long.
	
	// Tier 2 Algorithms
	
	//testAlgorithm(new BEAGLE()); // Required startup values.
	//testAlgorithm(new IncrementalSemanticAnalysis()); // Memory error.
	//testAlgorithm(new ExplicitSemanticAnalysis()); // Catch IO
	//testAlgorithm(new ReflectiveRandomIndexing());  // Took too long.
	//testAlgorithm(new StructuredVectorSpace()); // No extractors available error.
	//testAlgorithm(new DependencyVectorSpace()); // No extractors available error.
	
	// Tier 3 Algorithms
	
	//testAlgorithm(new LatentRelationalAnalysis(String corpus_directory, String index_directory, boolean do_index));
	//testAlgorithm(new Grefenstette()); // Null pointer exception.
		
	public WordRelatorSemanticSpace(Color color, String name, SemanticSpace semanticSpace, WordMap wordMap) {
		super(color, name, wordMap);
		this.semanticSpace = semanticSpace;
	}
	
	@SuppressWarnings("rawtypes")
	public static double[] convertVector(Vector v) {
		double[] ret = new double[v.length()];
		for(int i=0;i<ret.length;i++) {
			ret[i] = v.getValue(i).doubleValue();
		}
		return ret;
	}
	
	public double getDistance(String word1, String word2) {
		try {
			Vector<?> vec1 = semanticSpace.getVector(word1);
			Vector<?> vec2 = semanticSpace.getVector(word2);
			if(vec1 == null || vec2 == null) {
				return 0;
			}
			double[] v1 = convertVector(vec1);
			double[] v2 = convertVector(vec2);
			return VectorTools.getCosine(v1, v2);
		} catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
    public String toString() {
    	return "Semantic Space [" + semanticSpace.getSpaceName() + "] {" + name + "}";
    }

    public Set<String> getWords() {
		return semanticSpace.getWords();
	}
	    
    public boolean learn(File input) {
    	try {
			return learn(new FileInputStream(input));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
    }
    
    public boolean learn(InputStream input) {
    	try {
    		
			// Read each line as a document.
			BufferedReaderFormatted reader = new BufferedReaderFormatted(new InputStreamReader(input),cleaners);
			String line;
			while((line = reader.readLine()) != null) {
				semanticSpace.processDocument(new BufferedReader(new StringReader(line)));
			}
    		
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
    }

    public String join(String[] parts) {
    	if(parts.length > 0) {
	    	String ret = parts[0];
	    	for(int i=1;i<parts.length;i++) {
	    		ret += " " + parts[i];
	    	}
	    	return ret;
    	} else {
    		return "";
    	}
    }
    
	public void learn(String[] sentence) {
		super.learn(sentence);
		
		try {
			String joined = join(sentence);
			joined = joined.trim();
			
			if(joined.length() > 0) {
				semanticSpace.processDocument(new BufferedReaderFormatted(new StringReader(join(sentence)),cleaners));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void finalizeSpace() {
		Properties properties = new Properties();
		semanticSpace.processSpace(properties);
	}

}
