package relations.wordnet;

import java.awt.Color;
import java.io.File;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import relations.WordRelator;
import edu.mit.jwi.item.POS;
import edu.sussex.nlp.jws.*;
import gui.WordMap;
import gui.WordNode;

public class WordRelatorWordNet extends WordRelator {

	private static final long serialVersionUID = 7867399013391059881L;

	private static JWS jws = null;

	public static final int TYPE_ADAPTED_LESK = 0;
	public static final int TYPE_ADAPTED_LESK_TANIMOTO = 1;
	public static final int TYPE_ADAPTED_LESK_TANIMOTO_NO_HYPONYMS = 2;
	public static final int TYPE_HIRST_AND_ST_ONGE = 3;
	public static final int TYPE_JIANG_AND_CONRATH = 4;
	public static final int TYPE_LEACOCK_AND_CHODOROW = 5;
	public static final int TYPE_LIN = 6;
	public static final int TYPE_PATH = 7;
	public static final int TYPE_RESNIK = 8;
	public static final int TYPE_WU_AND_PALMER = 9;
	public static final String[] types = {"Adapted Lesk (SLOW!)","Adapted Lesk Tanimoto (SLOW!)","Adapted Lesk Tanimoto No Hyponyms (SLOW!)","Hirst And Stonge (SLOW!)","Jiang And Conrath","Leacock And Chodorow","Lin","Path","Resnik","Wu And Palmer"};
	
	@SuppressWarnings("rawtypes")
	public final static Class wizardPanel = WizardWordNetSetup.class;
	public final static String description = "This will compute the similarity between word using the WordNet online linguistics tool.";
	public final static String typeName = "WordNet";
	
	public boolean selectBest = true;
	public int type = TYPE_ADAPTED_LESK;
	
	public static AdaptedLesk adaptedLesk = null;
	public static AdaptedLeskTanimoto adaptedLeskTanimoto = null;
	public static AdaptedLeskTanimotoNoHyponyms adaptedLeskTanimotoNoHyponyms = null;
	public static HirstAndStOnge hirstAndStOnge = null;
	public static JiangAndConrath jiangAndConrath = null;
	public static LeacockAndChodorow leacockAndChodorow = null;	
	public static Lin lin = null;
	public static Path path = null;
	public static Resnik resnik = null;
	public static WuAndPalmer wuAndPalmer = null;
		
	public static void setJWS(JWS inJWS, WordMap wordMap) {
		jws = inJWS;
		adaptedLesk = jws.getAdaptedLesk();
		adaptedLeskTanimoto = jws.getAdaptedLeskTanimoto();
		adaptedLeskTanimotoNoHyponyms = jws.getAdaptedLeskTanimotoNoHyponyms();
		hirstAndStOnge = jws.getHirstAndStOnge();
		jiangAndConrath = jws.getJiangAndConrath();
		leacockAndChodorow = jws.getLeacockAndChodorow();		
		lin = jws.getLin();
		path = jws.getPath();
		resnik = jws.getResnik();
		wuAndPalmer = jws.getWuAndPalmer();
		
		for(WordNode w : wordMap.words.values()) {
			w.updatePOS();
		}
		wordMap.wordChanged();
	}
	
	public static boolean validWordNetDirectory(File file, WordMap wordMap) {
		if(file != null && file.exists()) {
			String[] versions = {"2.1","3.0"};
			for(String version : versions) {
				File dict = new File(file + File.separator + version + File.separator + "dict");
				File semcor = new File(file + File.separator + version + File.separator + "WordNet-InfoContent-" + version + File.separator + "ic-semcor.dat");
				if(dict.exists() && semcor.exists()) {
					try {
						WordRelatorWordNet.setJWS(new JWS(file.getAbsolutePath(), version),wordMap);
						return true;
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return false;
	}
	
	public static boolean loadWordNet(WordMap wordMap) {
		
		// Try windows default locations first.

		if(validWordNetDirectory(new File("C:/Program Files (x86)/WordNet"),wordMap)) {
			return true;
		}
		
		if(validWordNetDirectory(new File("C:/Program Files/WordNet"),wordMap)) {
			return true;
		}
		
		JOptionPane.showMessageDialog(null, "We were unable to find WordNet and the info content files in the default directory. Please select the location of your WordNet install.");
		JFileChooser fileChooser = new JFileChooser(new File("."));
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.showOpenDialog(null);
		File file = fileChooser.getSelectedFile();
		
		if(validWordNetDirectory(file,wordMap)) {
			return true;
		}
		
		JOptionPane.showMessageDialog(null, "WordNet still not found.\r\nDownload from http://wordnet.princeton.edu/wordnet/download/current-version/\r\nand paste http://www.d.umn.edu/~tpederse/Data/WordNet-InfoContent-2.1.tar.gz into the WordNet directory.\r\nProceding anyway.");
		return false;		
	}

	public static Vector<POS> getPOS(String word) {
		Vector<POS> poss = new Vector<POS>();
		if(WordRelatorWordNet.isSetJWS()) {
			for(POS pos : POS.values()) {
				if(jws.getDictionary().getIndexWord(word, pos) != null) {
					poss.add(pos);
				}
			}
		}
		return poss;
	}
	
	public static boolean isSetJWS() {
		return jws != null;
	}
	
	public WordRelatorWordNet(Color color, String name, WordMap wordMap) {
		super(color, name, wordMap);
	}

	public static String removeMcRaeClarification(String str) {
		str = str.toLowerCase();
		int underscore = str.indexOf('_');
		if(underscore >= 0) {
			str = str.substring(0,underscore);
		}
		str = str.trim();
		return str;
	}
	
	public double getDistance(String word1, String word2) {
		word1 = removeMcRaeClarification(word1);
		word2 = removeMcRaeClarification(word2);
				
		if(jws != null) {
			switch(type) {
				case TYPE_ADAPTED_LESK: if(selectBest) { return adaptedLesk.max(word1, word2, "n"); } else { return adaptedLesk.lesk(word1, 1, word2, 1, "n"); }
				case TYPE_ADAPTED_LESK_TANIMOTO: if(selectBest) { return adaptedLeskTanimoto.max(word1, word2, "n"); } else { return adaptedLeskTanimoto.lesk(word1, 1, word2, 1, "n"); }
				case TYPE_ADAPTED_LESK_TANIMOTO_NO_HYPONYMS: if(selectBest) { return adaptedLeskTanimotoNoHyponyms.max(word1, word2, "n"); } else { return adaptedLeskTanimotoNoHyponyms.lesk(word1, 1, word2, 1, "n"); }
				case TYPE_HIRST_AND_ST_ONGE: if(selectBest) { return hirstAndStOnge.max(word1, word2, "n"); } else { return hirstAndStOnge.hso(word1, 1, word2, 1, "n"); }
				case TYPE_JIANG_AND_CONRATH: if(selectBest) { return jiangAndConrath.max(word1, word2, "n"); } else { return jiangAndConrath.jcn(word1, 1, word2, 1, "n"); }
				case TYPE_LEACOCK_AND_CHODOROW: if(selectBest) { return leacockAndChodorow.max(word1, word2, "n"); } else { return leacockAndChodorow.lch(word1, 1, word2, 1, "n"); }
				case TYPE_LIN: if(selectBest) { return lin.max(word1, word2, "n"); } else { return lin.lin(word1, 1, word2, 1, "n"); }
				case TYPE_PATH: if(selectBest) { return path.max(word1, word2, "n"); } else { return path.path(word1, 1, word2, 1, "n"); }
				case TYPE_RESNIK: if(selectBest) { return resnik.max(word1, word2, "n"); } else { return resnik.res(word1, 1, word2, 1, "n"); }
				case TYPE_WU_AND_PALMER: if(selectBest) { return wuAndPalmer.max(word1, word2, "n"); } else { return wuAndPalmer.wup(word1, 1, word2, 1, "n"); }
			}
		}
		return 0;
	}

	public String toString() {
		return "WordNet [" + types[type] + "] {" + name + "}";
	}

	public void learn(String[] sentence) {
		super.learn(sentence);
	}
	
}
