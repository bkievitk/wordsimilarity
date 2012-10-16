package relations.beagle;

import java.io.*;
import java.net.URLConnection;
import java.util.*;

import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import tools.KBox;
import tools.WeightedObject;

/**
 * This is the core BEAGLE model. It holds all of the words and their representations.
 * @author bkievitk
 */

public class BEAGLE implements Serializable {

	private static final long serialVersionUID = 26076518604400990L;
	
	public final static String description = "This will compute either context free word relations or order relative word relations as computed by the BEAGLE holographic language model.";
	
	// Define parts of BEAGLE.
	private int[] E1;
	private int[] E2;
	private Thought PSI;
	private HashSet<String> stopList;

	// All changeable options.
	public OptionsModule options;
	
	// Context information.
	private Hashtable<String,Context> contexts = new Hashtable<String,Context>();
	private String contextString = null;
	private Context context = null;
	
	// Set of thoughts referenced by their representations.
	public Hashtable<String,Thought> thoughts = new Hashtable<String,Thought>();

	// When something changes in the context of this BEAGLE model, inform these listeners.
	private LinkedList<ChangeListener> changeListeners = new LinkedList<ChangeListener>();
	
	// This represents the thought at the end of a sentence.
	private Thought sentenceTerminate;
		
	public static String typeName = "BEAGLE";
	
	public BEAGLE(OptionsModule module) {
		this(null,module);
	}
	
	public BEAGLE(String[] dictionary, OptionsModule module) {
		
		// Build a default options module.
		options = module;
		
		// Create the static vector sets.
		E1 = VectorTools.getRandomOrder(options.dimensions);
		E2 = VectorTools.getRandomOrder(options.dimensions);
		PSI = new Thought("PSI",options.dimensions);
		
		// Load all words.
		if(dictionary != null) {
			// Build from the given word list.
			thoughts = Thought.buildWordSet(dictionary, options.dimensions);
		} else {
			// Build using the setup standard function.
			//setup();			
		}
		
		sentenceTerminate = new Thought("sentenceterminate",options.dimensions);
		thoughts.put(sentenceTerminate.representation, sentenceTerminate);
	}

	public void clearStopListFromThoughts() {
		for(String word : stopList) {
			thoughts.remove(word);
		}
	}
	
	/**
	 * Load the vector data from a file.
	 * @param vectorFile
	 */
	@SuppressWarnings("unchecked")
	public void loadVectors(File vectorFile) {
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream(vectorFile);
			in = new ObjectInputStream(fis);
			thoughts = (Hashtable<String,Thought>)in.readObject();
			E1 = (int[])in.readObject();
			E2 = (int[])in.readObject();
			PSI = (Thought)in.readObject();
			stopList = (HashSet<String>)in.readObject();	
			sentenceTerminate = (Thought)in.readObject();			
		} catch(IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load the options data from a file.
	 * @param optionFile
	 */
	public void loadOptions(File optionFile) {
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream(optionFile);
			in = new ObjectInputStream(fis);
			options = (OptionsModule)in.readObject();			
		} catch(IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save the vector data to a file.
	 * @param vectorFile
	 */
	public void saveVectors(File vectorFile) {
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try	{
			fos = new FileOutputStream(vectorFile);
			out = new ObjectOutputStream(fos);
			out.writeObject(thoughts);
			out.writeObject(E1);
			out.writeObject(E2);
			out.writeObject(PSI);
			out.writeObject(stopList);	
			out.writeObject(sentenceTerminate);
			out.close();
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Save the options data to a file.
	 * @param optionFile
	 */
	public void saveOptions(File optionFile) {
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try	{
			fos = new FileOutputStream(optionFile);
			out = new ObjectOutputStream(fos);
			out.writeObject(options);
			out.close();
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Resets the word counts to zero.
	 */
	public void resetCounts() {
		for(Thought t : thoughts.values()) {
			t.count = 0;
		}
	}

	public void addChangeListener(ChangeListener c) { changeListeners.add(c); }
	public void removeChangeListener(ChangeListener c) { changeListeners.remove(c); }	
	public void informChangeListeners() {
		ChangeEvent ce = new ChangeEvent("Changed");
		for(ChangeListener c : changeListeners) {
			c.stateChanged(ce);
		}
	}	

	/**
	 * Set the context you are working in.
	 * @param context
	 */
	public void setContext(String context) {
		if(context.equals("Global")) {
			this.context = null;
		} else {
			this.context = contexts.get(context);
			contextString = context;
		}
		informChangeListeners();
	}

	/**
	 * Build a new context.
	 * @param context
	 * @return
	 */
	public boolean addContext(String context) {
		if(contexts.contains(context)) {
			return false;
		}
		contexts.put(context,new Context(options.dimensions));
		informChangeListeners();
		return true;
	}	

	/**
	 * Count available contexts.
	 * @return
	 */
	public int getContextCount() {
		return contexts.size();
	}

	/**
	 * Show current context.
	 * @return
	 */
	public String getContext() {
		if(context == null) {
			return "--Global--";
		}
		return "--" + contextString + "--";
		
	}

	/**
	 * Show all contexts.
	 */
	public String getContexts() {
		String str = "Global";
		for(String s : contexts.keySet()) {
			str += "\n" + s;
		}
		return str;
	}

	/**
	 * Normalize all lexical vectors to length 5.
	 */
	public void normalize() {
		for(Thought t : thoughts.values()) {
			t.lexical = VectorTools.setLen(t.lexical,5);
		}
		informChangeListeners();
	}
		
	/**
	 * This will learn a corpus from a file.
	 * Makes a call to the BufferedReader version.
	 * @param corpus
	 * @param learnContext
	 * @param learnOrder
	 * @throws IOException 
	 */
	public void learnCorpus(File corpus, ChangeListener listener) throws IOException {
		BufferedReader r;
		
		try {
			// Simply open the reader and pass it on.
			r = new BufferedReader(new FileReader(corpus));
			learnCorpus(r,corpus.length(),listener);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void learnCorpus(URLConnection corpus, ChangeListener listener) throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(corpus.getInputStream()));
		learnCorpus(r,corpus.getContentLength(),listener);
	}

	/**
	 * This will look through every line in the BufferedReader and learn it.
	 * @param r
	 * @param learnContext
	 * @param learnOrder
	 * @throws IOException 
	 */
	private void learnCorpus(BufferedReader r, long length, ChangeListener listener) throws IOException {
		String paragraph;
		long read = 0;
		
		// Read each paragraph.
		while((paragraph = r.readLine()) != null) {
			read += paragraph.length() + 1;
			
			// Read each line.
			for(String s : paragraph.split("[.?!]")) {
				
				// Convert to thought modules.
				Thought[] thoughts = toThought(s);
				
				// Run appropriate learning techniques.
				if(options.learnContext) { learnContext(thoughts, false); }
				if(options.learnOrder) { learnOrder(thoughts, false); }
				
				if(listener != null) {
					listener.stateChanged(new ChangeEvent((double)read/length));
				}
			}
		}
		
		// Do this quietly, then inform at the end.
		informChangeListeners();
	}
		
	/**
	 * Find the sum of the environmental vectors that define a sentence.
	 * @param sentence
	 * @return
	 */
	public double[] sentenceLexicalMiddle(Thought[] sentence) { 
		if(sentence.length == 0) {
			return null;
		}
		
		double[] middle = new double[options.dimensions];
		for(Thought thought : sentence) {
			VectorTools.setAdd(thought.lexical, middle);
		}
		return middle;
	}
	
	/**
	 * Find the sum of the environmental vectors that define a sentence.
	 * @param sentence
	 * @return
	 */
	public double[] sentenceEnvironmentalMiddle(Thought[] sentence) { 
		if(sentence.length == 0) {
			return null;
		}
		
		double[] middle = new double[options.dimensions];
		for(Thought thought : sentence) {
			VectorTools.setAdd(thought.environmental, middle);
		}
		return middle;
	}
	
	/**
	 * Setup loads all words and initializes their lexical and environmental vectors.
	 */
	public void setup() {

		// Load the stop list.
		if(options.stopListPath != null && options.stopListPath.isFile()) {
			stopList = Thought.loadStoplist(options.stopListPath);
		} else {
			final JFileChooser chooser = new JFileChooser("data");
		    chooser.setFileFilter(new FileNameExtensionFilter("Stoplist files", "stp"));
			int returnVal = chooser.showDialog(null,"Select Stoplist file.");
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				options.stopListPath = chooser.getSelectedFile();
				stopList = Thought.loadStoplist(options.stopListPath);
			} else {
				stopList = new HashSet<String>();
			}
		}
		
		// Load words.
		if(options.wordSetPath != null && options.wordSetPath.isFile()) {
			thoughts = Thought.buildWordSet(Thought.loadWordsSimple(options.wordSetPath, options.dimensions));
		} else {
			final JFileChooser chooser = new JFileChooser("data");
		    chooser.setFileFilter(new FileNameExtensionFilter("Word set file.", "wrd"));
			int returnVal = chooser.showDialog(null,"Select word set file.");
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				options.wordSetPath = chooser.getSelectedFile();
				thoughts = Thought.buildWordSet(Thought.loadWordsSimple(options.wordSetPath, options.dimensions));
			} else {
				thoughts = new Hashtable<String, Thought>();
			}
		}
	}
	
	/**
	 * Find the top n nearest words to this one.
	 * @param word	Word to compare to.
	 */
	public KBox<Thought> topNContext(String word) {
		
		// If we have not been given a context.
		if(context == null) {
			
			// Get thought centroid.
			Thought[] thoughts = toThought(word);
			double[] lexicalThought = sentenceLexicalMiddle(thoughts);
			
			// Exit if no word found.
			if(lexicalThought == null) {
				return null;
			}
			
			return topNLexicalMatchesNonStoplisted(lexicalThought);
			
		} else {

			/*
			 * todo:
			 */
			Thought[] thoughts = toThought(word);
			double[] environmentalThought = sentenceEnvironmentalMiddle(thoughts);
			
			double[] decode = VectorTools.getPointwiseMultiply(environmentalThought, context.indicator);
			return topNLexicalMatchesNonStoplisted(decode);
		}
	}
	
	/**
	 * Get the top n thoughts that should directly follow this one.
	 */
	public KBox<Thought> topNNext(String word) {
		switch(options.combineOperator) {
			case OptionsModule.CONVOLUTION:
				return topNNextConvolusion(word);
			case OptionsModule.RPM:
				return topNNextRPM(word);
		}
		return null;
	}
	
	/**
	 * Get the top n thoughts that should directly follow this one based on RPM encoding.
	 * @param word
	 * @return
	 */
	private KBox<Thought> topNNextRPM(String word) {
		
		Thought tWord = thoughts.get(word);
		
		// Exit if no word found.
		if(tWord == null) {
			return null;
		}
		
		double[] lex = tWord.lexical;		
		double[] rotated = VectorTools.rotate(lex, 1);		
		return topNEnvironmentalMatches(rotated);
	}
	
	/**
	 * Get the top n thoughts that should directly follow this one based on Convolution encoding.
	 * @param word
	 * @return
	 */
	private KBox<Thought> topNNextConvolusion(String word) {

		Thought tWord = thoughts.get(word);
		
		// Exit if no word found.
		if(tWord == null) {
			return null;
		}
				
		double[] a = VectorTools.rearangeForward(PSI.environmental, E1);
		double[] b = VectorTools.corelate(a, tWord.lexical);
		double[] c = VectorTools.rearangeBackward(b, E2);			
		return topNEnvironmentalMatches(c);
	}
	
	/**
	 * Find the top N matches comparing lexical vectors.
	 * @param representation
	 * @param word
	 */
	public KBox<Thought> topNLexicalMatches(double[] representation) {
		KBox<Thought> kBox = new KBox<Thought>(options.numResults,true);
		for(Thought comp : thoughts.values()) {
			double angle = VectorTools.getCosine(representation, comp.lexical);
			kBox.add(new WeightedObject<Thought>(comp, angle));
		}
		return kBox;
	}
	
	/**
	 * Find the top N matches comparing lexical vectors.
	 * @param representation
	 * @param word
	 */
	private KBox<Thought> topNLexicalMatchesNonStoplisted(double[] representation) {
		KBox<Thought> kBox = new KBox<Thought>(options.numResults,true);
		for(Thought comp : thoughts.values()) {
			if(!stopList.contains(comp.representation)) {
				double angle = VectorTools.getCosine(representation, comp.lexical);
				kBox.add(new WeightedObject<Thought>(comp, angle));
			}
		}
		return kBox;
	}
	
	/**
	 * Find the top N matches comparing environmental matches.
	 * @param representation
	 * @param word
	 */
	private KBox<Thought> topNEnvironmentalMatches(double[] representation) {
		KBox<Thought> kBox = new KBox<Thought>(options.numResults,true);
		for(Thought comp : thoughts.values()) {
			double angle = VectorTools.getCosine(representation, comp.environmental);
			kBox.add(new WeightedObject<Thought>(comp, angle));
		}
		return kBox;
	}
	
	/**
	 * Find the top N matches comparing environmental matches.
	 * @param representation
	 * @param word
	 */
	public KBox<Thought> topNEnvironmentalMatchesNonStoplisted(double[] representation) {
		KBox<Thought> kBox = new KBox<Thought>(options.numResults,true);
		for(Thought comp : thoughts.values()) {
			if(!stopList.contains(comp.representation)) {
				double angle = VectorTools.getCosine(representation, comp.environmental);
				kBox.add(new WeightedObject<Thought>(comp, angle));
			}
		}
		return kBox;
	}
	
	/**
	 * Cleans up input line.
	 * @param s
	 * @return
	 */
	public static String cleanLine(String s) {
		s = s.toLowerCase();
		s = s.replaceAll("  +", " ");
		s = s.replaceAll("[^a-z ]", "");
		s = s.trim();
		return s;
	}
	
	/**
	 * Convert a string into a list of thoughts.
	 * @param sentence
	 * @return
	 */
	public Thought[] toThought(String sentence) {		
		
		// First we need to clean the line up a little bit.
		sentence = cleanLine(sentence);
		
		// Break into words.
		String[] words = sentence.split(" ");
		
		return toThought(words);
	}
	
	public Thought[] toThought(String[] words) {
		Vector<Thought> matches = new Vector<Thought>();
		
		// For each word.
		for(String word : words) {
			
			// If you have a thought for that word, then use it.
			Thought t = thoughts.get(word);
			
			if(t != null) {
				matches.add(t);
			} else {
				// We have not found a word match.
				if(options.autoAddWords) {
					// We are going to add this word to the dictionary.
					Thought newWord = Thought.addWord(word, options.dimensions, thoughts);
					matches.add(newWord);
				}
			}
		}
		
		// Convert to array.
		Thought[] ret = new Thought[matches.size()];
		matches.copyInto(ret);
				
		return ret;
	}
	
	/**
	 * Remove words that appear on the stop list.
	 * @param thoughts
	 * @return
	 */
	public Thought[] removeStoplisted(Thought[] thoughts) {
		
		Vector<Thought> newList = new Vector<Thought>();
		for(Thought thought : thoughts) {
			if(!stopList.contains(thought.representation)) {
				newList.add(thought);
			}
		}
		
		// Convert to array.
		Thought[] ret = new Thought[newList.size()];
		newList.copyInto(ret);
		return ret;
	}
		
	/**
	 * Learn contextual information about each word in this sentence.
	 * @param thoughts
	 */
	public void learnContext(Thought[] thoughts, boolean update) {		

		// We will update how many times we have seen a thought.
		if(options.updateWordCount) {
			for(Thought t : thoughts) {
				t.count++;
			}
		}

		// Only non-stop listed words if applicable.
		if(options.stopList) {
			thoughts = removeStoplisted(thoughts);
		}
		
		// We are in the global context.
		if(context == null) {
			learnContextGlobal(thoughts);
		} else {
			learnContextLocal(thoughts);
		}
		
		// Update if asked to.
		if(update) {
			informChangeListeners();
		}
	}
	
	public void learnContextGlobal(Thought[] thoughts) {		
	
		// For each word
		for(int i=0;i<thoughts.length;i++) {
			double[] sumEnvironmental = VectorTools.zero(options.dimensions);
			double[] sumLexical = VectorTools.zero(options.dimensions);
			
			// For each word except this word.
			for(int j=0;j<thoughts.length;j++) {
				if(i != j) {
					double scale = - sigmoid(thoughts[j].count);
					double[] scalled = VectorTools.mult(thoughts[j].environmental, scale);
					VectorTools.setAdd(scalled, sumEnvironmental);					
					VectorTools.setAdd(thoughts[j].environmental, sumLexical);
				}
			}

			// Here we will normalize the learned environmental.
						
			VectorTools.setAdd(sumEnvironmental, thoughts[i].lexical);
			sumLexical = VectorTools.mult(sumLexical, options.envWeight);
			VectorTools.setAdd(sumLexical, thoughts[i].lexical);
				
		}
	}
	
	public double sigmoid(double v) {
		return 1.0 / (1.0 + Math.pow(Math.E, -(v / 1000 - 2)));
	}
	
	public void learnContextLocal(Thought[] thoughts) {	
		
		for(int i=0;i<thoughts.length;i++) {
			double[] sum = VectorTools.zero(options.dimensions);
			for(int j=0;j<thoughts.length;j++) {
				if(i != j) {
					double[] contexted = VectorTools.getPointwiseMultiply(thoughts[j].environmental, context.indicator);
					double[] scalled = VectorTools.mult(contexted, 10.0);
					VectorTools.setAdd(scalled, sum);
				}
			}
			VectorTools.setAdd(sum, thoughts[i].lexical);
		}
	}
	
	/**
	 * Learn order information about each word in this sentence.
	 * @param thoughts
	 */
	public void learnOrder(Thought[] thoughts, boolean update) {
		
		// We will update how many times we have seen a thought.
		if(options.updateWordCount) {
			for(Thought t : thoughts) {
				t.count++;
			}
		}

		// Add the termination thought.
		thoughts = addTermination(thoughts);
				
		if(options.combineOperator == OptionsModule.CONVOLUTION) {
			learnOrderConvolution(thoughts);
		} else if(options.combineOperator == OptionsModule.RPM) {
			learnOrderRPM(thoughts);			
		}

		if(update) {
			informChangeListeners();
		}
	}
	
	public Thought[] addTermination(Thought[] thoughts) {
		Thought[] thoughtsTerminated = new Thought[thoughts.length+1];
		for(int i=0;i<thoughts.length;i++) {
			thoughtsTerminated[i] = thoughts[i];
		}
		thoughtsTerminated[thoughts.length] = sentenceTerminate;
		return thoughtsTerminated;
	}

	/**
	 * Encode order information using RPM.
	 * @param thoughts
	 */
	public void learnOrderRPM(Thought[] thoughts) {
		for(int i=0;i<thoughts.length;i++) {
			double[] sum = VectorTools.zero(options.dimensions);
			for(int j=0;j<thoughts.length;j++) {
				int offset = i-j;
				if(offset != 0) {
					double[] rotated = VectorTools.rotate(thoughts[j].environmental, offset);
					VectorTools.setAdd(rotated, sum);
				}
			}
			// We can now appropriatly scale the sum vector.
			
			// Then set back 
			VectorTools.setAdd(sum, thoughts[i].lexical);
		}
	}

	/**
	 * Encode order information using convolution.
	 * @param thoughts
	 */
	public void learnOrderConvolution(Thought[] thoughts) {
		if(thoughts.length > 1) {			
			// For every possible starting location.
			for(int start=0;start<thoughts.length;start++) {
				// For every possible stopping location.
				for(int stop=start+1;stop<start+options.windowSize&&stop<thoughts.length;stop++) {
					// For each word to learn within range.
					for(int at=start;at<=stop;at++) {						
						Thought tmp = thoughts[at];
						thoughts[at] = PSI;
						double[] bound = bind(thoughts,start,stop);
						thoughts[at] = tmp;
						
						bound = VectorTools.normalizeLen(bound);
						VectorTools.setAdd(bound, thoughts[at].lexical);
						
					}
				}
			}
		}
	}
	
	/**
	 * Get the bound array.
	 * @param thoughts
	 * @param start
	 * @param stop
	 * @return
	 */
	private double[] bind(Thought[] thoughts, int start, int stop) {
		if(start + 1 == stop) {
			double[] a = VectorTools.rearangeForward(thoughts[start].environmental, E1);
			double[] b = VectorTools.rearangeForward(thoughts[stop].environmental, E2);
			double[] c = VectorTools.convolve(a, b);
			return c;
		}
		
		double[] upTo = bind(thoughts, start, stop - 1);		
		double[] a = VectorTools.rearangeForward(upTo, E1);
		double[] b = VectorTools.rearangeForward(thoughts[stop].environmental, E2);
		return VectorTools.convolve(a, b);
	}
}
