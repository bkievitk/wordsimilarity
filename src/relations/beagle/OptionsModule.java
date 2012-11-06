package relations.beagle;

import java.awt.Component;
import java.io.File;
import java.io.Serializable;

import javax.swing.JOptionPane;

public class OptionsModule implements Serializable {
	
	private static final long serialVersionUID = -8188323173449592328L;
	
	public static final int CONVOLUTION = 0;
	public static final int RPM = 1;
		
	public int combineOperator = RPM;
	public boolean autoAddWords = false;
	public int windowSize = 7;
	public int dimensions = 500;
	public double wordWeightPenalty = 0.0;
	public boolean stopList = false;
	public double envWeight = 0.0;
	public int numResults = 10;
	public boolean negationRepulsion = false;
	public boolean updateWordCount = true;

	public boolean learnContext = true;
	public boolean learnOrder = false;

	public transient File stopListPath;
	public transient File wordSetPath;
	
	public OptionsModule(int dimensions) {
		this.dimensions = dimensions;
	}
	
	public OptionsModule(OptionsModule clone) {
		combineOperator = clone.combineOperator;
		autoAddWords = clone.autoAddWords;
		windowSize = clone.windowSize;
		dimensions = clone.dimensions;
		wordWeightPenalty = clone.wordWeightPenalty;
		stopList = clone.stopList;
		envWeight = clone.envWeight;
		numResults = clone.numResults;
	}
	
	/**
	 * If true, new words are automatically added to the dictionary.
	 * @param autoAddWords
	 * @return
	 */
	public boolean setAutoAddWords(boolean autoAddWords) {
		this.autoAddWords = autoAddWords;
		return true;
	}

	/**
	 * If true, a stoplist is used to learn word unordered relations.
	 * @param stopList
	 * @return
	 */
	public boolean setStopList(boolean stopList) {
		this.stopList = stopList;
		return true;
	}
	
	/**
	 * If true, a stoplist is used to learn word unordered relations.
	 * @param stopList
	 * @return
	 */
	public boolean setNegationRepulsion(boolean repulsionOn) {
		this.negationRepulsion = repulsionOn;
		return true;	
	}
	
	/**
	 * If true, a stoplist is used to learn word unordered relations.
	 * @param stopList
	 * @return
	 */
	public boolean setUpdateWordcount(boolean updateWordCount) {
		this.updateWordCount = updateWordCount;
		return true;
	}
	
	
	/**
	 * This value is used to penalize the significance of commonly used words.
	 * @param wordWeightPenalty
	 * @param parent
	 * @return
	 */
	public boolean setNumResults(int numResults, Component parent) {
		if(numResults < 1) {
			
			JOptionPane.showMessageDialog(parent,
				    "You must return at least one result.",
				    "Size Error",
				    JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		this.numResults = numResults;
		return true;
	}
	
	/**
	 * This value is used to penalize the significance of commonly used words.
	 * @param wordWeightPenalty
	 * @param parent
	 * @return
	 */
	public boolean setWordWeightPenalty(double wordWeightPenalty, Component parent) {
		if(wordWeightPenalty < 0 || wordWeightPenalty > 1.0) {
			
			JOptionPane.showMessageDialog(parent,
				    "Word weight penalty must be between 0 and 1.",
				    "Size Error",
				    JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		this.wordWeightPenalty = wordWeightPenalty;
		return true;
	}
	
	/**
	 * This value allows a fraction of the learning to come from the environmental vector.
	 * @param envWeight
	 * @param parent
	 * @return
	 */
	public boolean setEnvironmentalWeight(double envWeight, Component parent) {
		if(envWeight < 0 || envWeight > 1.0) {
			
			JOptionPane.showMessageDialog(parent,
				    "Environmental weight must be between 0 and 1.",
				    "Size Error",
				    JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		this.envWeight = envWeight;
		return true;
	}
	
	/**
	 * This is the window size for convolusion.
	 * @param size
	 * @param parent
	 * @return
	 */
	public boolean setWindowSize(int size, Component parent) {
		if(size < 1) {
			
			JOptionPane.showMessageDialog(parent,
				    "The window size must be at least 1.",
				    "Size Error",
				    JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		if(size > 7) {
			
			JOptionPane.showMessageDialog(parent,
				    "A large window size may take excessivly long to learn.",
				    "Size Warning",
				    JOptionPane.WARNING_MESSAGE);
		}
		
		windowSize = size;
		return true;
	}
	
	/**
	 * Dimensions of BEAGLE vectors.
	 * @param dimensions
	 * @param parent
	 * @return
	 */
	public boolean setDimensions(int dimensions, Component parent) {
		if(dimensions < 1) {
			
			JOptionPane.showMessageDialog(parent,
				    "The number of dimensions must be at least 1.",
				    "Size Error",
				    JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		if(dimensions < 100 || dimensions > 5000) {
			
			JOptionPane.showMessageDialog(parent,
				    "We suggest that you choose dimensions between 100 and 5000.",
				    "Size Warning",
				    JOptionPane.WARNING_MESSAGE);
		}

		int val = JOptionPane.showConfirmDialog(parent, "This will reset the BEAGLE vectors to random initial starting values.\nAre you sure you wish to continue?", "Change Dimensions", JOptionPane.WARNING_MESSAGE);
		
		if(val == JOptionPane.OK_OPTION) {
			this.dimensions = dimensions;
			return true;
		}
		return false;
		
	}
	
	/**
	 * Choose the operator to combine with.
	 * @param newOperator
	 * @param parent
	 * @return
	 */
	public boolean setCombineOperator(int newOperator, Component parent) {
		if(newOperator != CONVOLUTION && newOperator != RPM) {
			
			JOptionPane.showMessageDialog(parent,
				    "This is an invalid combine operator.",
				    "Type Error",
				    JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		combineOperator = newOperator;
		return true;
	}
	
}
