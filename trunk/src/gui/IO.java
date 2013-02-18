package gui;

import java.awt.Color;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.swing.*;

import relations.WordRelator;
import relations.helpers.WordRelationSingleWord;
import relations.helpers.WordRelationCrystalized;
import tools.PanelTools;
import tools.VerticalLayout;

public class IO {

	public static final int CMP_SHORT = 0;
	public static final int CMP_DOUBLE = 1;
	
	public static void loadColorCodes(WordMap wordMap, BufferedReader r, Color[] allColors) throws IOException {
		String line;
		Hashtable<String,Color> colors = new Hashtable<String,Color>();
		while((line = r.readLine()) != null) {
			String[] parts = line.split(",");
			String word = parts[0];
			String type = parts[1];
			
			WordNode node = wordMap.words.get(word);
			if(node != null) {
				Color color = colors.get(type);
				if(color == null) {
					color = allColors[colors.size()];
					colors.put(type, color);
				}
				node.color = color;
			}
		}
	}
	
	public static void loadColorCodes(WordMap wordMap, BufferedReader r, Hashtable<String,Color> colors) throws IOException {
		String line;
		while((line = r.readLine()) != null) {
			String[] parts = line.split(",");
			String word = parts[0];
			String type = parts[1];
			
			WordNode node = wordMap.words.get(word);
			if(node != null) {
				node.color = colors.get(type);
			}
		}
	}
	
	public static void readAll(InputStream is, byte[] bytes) throws IOException {
		for(int start = 0; start<bytes.length; start += is.read(bytes,start,bytes.length-start));
	}
	
	public static void saveWorkspace(final WordMap wordMap, final OutputStream writer) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(writer);
		out.writeObject(wordMap.activeRelations);
		out.writeObject(wordMap.inactiveRelations);
		out.writeObject(wordMap.words);
		out.writeObject(wordMap.activeWords);			
		out.close();
	}
	
	public static void saveCSV(final WordMap wordMap, final OutputStream writer) throws IOException {
		BufferedWriter w = new BufferedWriter(new OutputStreamWriter(writer));
		for(WordRelator relation : wordMap.activeRelations) {
			w.write(relation.name + "\r\n");
			
			w.write(" ");
			for(String w1 : wordMap.activeWords.keySet()) {
				w.write("," + w1);
			}
			w.write("\r\n");
			
			for(String w1 : wordMap.activeWords.keySet()) {
				w.write(w1);
				for(String w2 : wordMap.activeWords.keySet()) {
					w.write("," + relation.getDistance(w1, w2));
				}	
				w.write("\r\n");
			}
			w.write("\r\n");
		}
		w.close();	
	}
	
	public static void savePairs(final WordMap wordMap, final OutputStream writer) throws IOException {
		BufferedWriter w = new BufferedWriter(new OutputStreamWriter(writer));	
		w.write("word1,word2");
		for(WordRelator relation : wordMap.activeRelations) {
			w.write("," + relation.name);
		}
		w.write("\n");									
		for(int w1 = 0; w1<wordMap.activeWordsSorted.size(); w1++) {
			WordNode w1Node = wordMap.activeWordsSorted.get(w1);
			for(int w2 = w1+1; w2<wordMap.activeWordsSorted.size(); w2++) {
				WordNode w2Node = wordMap.activeWordsSorted.get(w2);
				w.write(w1Node.word + "," + w2Node.word);
				for(WordRelator relation : wordMap.activeRelations) {
					w.write("," + relation.getDistance(w1Node.word, w2Node.word));
				}
				w.write("\n");
			}
		}									
		w.close();
	}
	
	public static void saveWordList(final WordMap wordMap, final OutputStream writer) throws IOException {
		BufferedWriter w = new BufferedWriter(new OutputStreamWriter(writer));
		for(WordNode node : wordMap.activeWordsSorted) {
			w.write(node.word + "\n");
		}
		w.close();
	}
	
	public static long getExpectedSize(int cuttoff, Vector<WordNode> wordsAll, int type) {
		int count = 0;
		for(WordNode word : wordsAll) {
			if(word.getCount() > cuttoff) {
				count++;
			}
		}
		switch(type) {
			case CMP_SHORT:
				return count * count + count * 10; 
			case CMP_DOUBLE:
				return count * count * 4 + count * 10; 
		};
		return 0;
	}
	
	public static void saveCMP(final WordMap wordMap, final OutputStream writer, final int cuttoff, final JProgressBar progressBar, final int type, final JFrame frame) {
		
		(new Thread() {
			public void run() {

				Vector<WordNode> words = new Vector<WordNode>();
				final Vector<WordNode> wordsAll = wordMap.wordsSorted;
				
				for(WordNode word : wordsAll) {
					if(word.getCount() > cuttoff) {
						words.add(word);
					}
				}
				
				try {
					 
					ZipOutputStream w = new ZipOutputStream(writer);
					ZipEntry entry = new ZipEntry("dataEntry");
					w.putNextEntry(entry);
					WordRelator metric = wordMap.activeRelations.get(0);
					
					byte[] typeKey = {(byte)type};
					w.write(typeKey);
					
					int size = words.size();
					byte[] number = {(byte)size,(byte)(size >> 8),(byte)(size >> 16),(byte)(size >> 24)};
					w.write(number);

					progressBar.setMaximum(words.size());
					progressBar.setMinimum(0);
					
					for(int i=0;i<words.size();i++) {
						progressBar.setValue(i);
						
						byte[] buffer = null;
						int index = 0;
						
						switch(type) {
							case CMP_SHORT:
								buffer = new byte[(words.size() - i - 1) * 2];
								for(int j=i+1;j<words.size();j++) {
									double dist = metric.getDistance(words.get(i).word, words.get(j).word);
									dist = Math.max(0, Math.min(1, dist));
									
									short distS = (short)(dist * Short.MAX_VALUE);
									buffer[index] = (byte)distS;
									buffer[index + 1] = (byte)(distS >> 8);

									index += 2;
								}
								break;
							case CMP_DOUBLE:
								buffer = new byte[(words.size() - i - 1) * 8];
								for(int j=i+1;j<words.size();j++) {
									double dist = metric.getDistance(words.get(i).word, words.get(j).word);
									long distL = Double.doubleToLongBits(dist);
									buffer[index] = (byte)distL; index++;
									buffer[index] = (byte)(distL >> 8); index++;
									buffer[index] = (byte)(distL >> 16); index++;
									buffer[index] = (byte)(distL >> 24); index++;
									buffer[index] = (byte)(distL >> 32); index++;
									buffer[index] = (byte)(distL >> 40); index++;
									buffer[index] = (byte)(distL >> 48); index++;
									buffer[index] = (byte)(distL >> 56); index++;
								}
								break;
						};
						
						w.write(buffer);
					}
					for(int i=0;i<words.size();i++) {
						char[] wordArray = words.get(i).word.toCharArray();
						byte[] byteArray = new byte[wordArray.length+1];
						for(int k=0;k<wordArray.length;k++) {
							byteArray[k+1] = (byte)wordArray[k];
						}
						byteArray[0] = (byte)wordArray.length;
						
						if(wordArray.length > 125) {
							System.out.println("!!! Length ERROR");
						}
						w.write(byteArray);
					}
					w.close();
					frame.dispose();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		
	}
	
	public static void saveCMPFrame(final WordMap wordMap, final OutputStream writer) {

		if(wordMap.activeRelations.size() > 0) {
			
			final Vector<WordNode> wordsAll = wordMap.wordsSorted;
			
			JPanel panel = new JPanel(new VerticalLayout(3,3));
			
			panel.add(PanelTools.wrappingText("Select save type."));
			String[] types = {"short","double"};
			final JComboBox type = new JComboBox(types);
			panel.add(type);

			final JLabel message = new JLabel();
			final JTextField cuttoff = new JTextField("0");
			final JProgressBar progressBar = new JProgressBar();
			
			type.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					try {
						int cuttoffI = Integer.parseInt(cuttoff.getText());
						long expectedSize = getExpectedSize(cuttoffI, wordsAll, type.getSelectedIndex()); 
						message.setText("Expected size: " + expectedSize + " bytes.");
					} catch(NumberFormatException e) {
					}
				}
			});

			panel.add(PanelTools.wrappingText("Select word count cuttoff."));
			panel.add(cuttoff);			
			panel.add(message);
			
			int cuttoffI = Integer.parseInt(cuttoff.getText());
			long expectedSize = getExpectedSize(cuttoffI, wordsAll, type.getSelectedIndex()); 
			
			message.setText("Expected size: " + expectedSize + " bytes.");
			
			cuttoff.addKeyListener(new KeyListener() {
				public void keyReleased(KeyEvent arg0) {
					try {
						int cuttoffI = Integer.parseInt(cuttoff.getText());
						long expectedSize = getExpectedSize(cuttoffI, wordsAll, type.getSelectedIndex()); 
						message.setText("Expected size: " + expectedSize + " bytes.");
					} catch(NumberFormatException e) {
					}
				}
				public void keyPressed(KeyEvent arg0) {}
				public void keyTyped(KeyEvent arg0) {}
			});
			
			
			JButton save = new JButton("save");
			panel.add(save);
			
			panel.add(progressBar);
			

			final JFrame frame = new JFrame();
			
			save.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					try {
						saveCMP(wordMap, writer, Integer.parseInt(cuttoff.getText()), progressBar, type.getSelectedIndex(), frame);
					} catch(NumberFormatException e) {
					}
				}
			});
			
			frame.setSize(400, 300);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.add(panel);
			frame.setVisible(true);
			
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void loadWorkSpace(WordMap wordMap, BufferedInputStream input) throws IOException {
		try {
			ObjectInputStream in = new ObjectInputStream(input);
			wordMap.activeRelations = (LinkedList<WordRelator>)in.readObject();
			wordMap.inactiveRelations = (LinkedList<WordRelator>)in.readObject();
			Hashtable<String,WordNode> words = (Hashtable<String,WordNode>)in.readObject();
			Hashtable<String,WordNode> activeWords = (Hashtable<String,WordNode>)in.readObject();	
			wordMap.setWords(words,activeWords);
			
			wordMap.wordChanged();
			wordMap.relationChanged();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static WordRelationCrystalized loadCSV(WordMap wordMap, BufferedReader input) throws IOException {
		return loadCSV(wordMap,input,Color.BLACK);
	}
	
	public static WordRelationCrystalized loadCSV(WordMap wordMap, File input, Color c) throws IOException {
		return loadCSV(wordMap, new BufferedReader(new FileReader(input)), c);
	}
	
	public static WordRelationCrystalized loadCSV(WordMap wordMap, BufferedReader input, Color c) throws IOException {
		while(true) {
			WordRelationCrystalized crystalized = new WordRelationCrystalized(c, wordMap,input);
			if(crystalized.name == WordRelator.ERROR_INITIALIZING) {
				break;
			}
			wordMap.setRelatorStatus(crystalized, true);
			wordMap.addRelatorWords(crystalized);
			return crystalized;
		}
		return null;
	}

	public static WordRelationSingleWord loadWordSimilarity(WordMap wordMap, File file) {
		return loadWordSimilarity(wordMap, file, null, Color.BLACK);
	}
	
	public static WordRelationSingleWord loadWordSimilarity(WordMap wordMap, File file, String cmpWrd, Color color) {
		return loadWordSimilarity(wordMap, file, cmpWrd, color, file.getName());
	}
	
	public static WordRelationSingleWord loadWordSimilarity(WordMap wordMap, File file, String cmpWrd, Color color, String name) {
		try {
			BufferedReader r = new BufferedReader(new FileReader(file));
			
			if(cmpWrd == null) {
				cmpWrd = r.readLine().split(",")[0];
			}
			
			String line;
			
			WordRelationSingleWord crystalized = new WordRelationSingleWord(color,name,wordMap,cmpWrd);
			crystalized.values = new Hashtable<String,Double>();
			
			while((line = r.readLine()) != null) {
				String[] strs = line.split(",");
				String word = strs[0];
				double value = Double.parseDouble(strs[1]);
				crystalized.values.put(word, value);
			}
			
			wordMap.setRelatorStatus(crystalized, true);
			wordMap.addRelatorWords(crystalized);	
			
			r.close();
			
			return crystalized;
			
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void loadImages(WordMap wordMap, File file) {
		// Load all images in directory.
		for(File f : file.listFiles()) {										
			if(f.getName().endsWith(".png")) {
				String name = f.getName().substring(0,f.getName().length()-4).trim();
				try {
					BufferedImage img = ImageIO.read(f);
					WordNode node = wordMap.getWord(name, 0);
					if(node != null) {
						System.out.println("image match");
						node.setImage(img);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void loadCMP(WordMap wordMap, BufferedInputStream input) throws IOException {
		WordRelationCrystalized crystalized = new WordRelationCrystalized(Color.BLACK, "new", wordMap);
		
		ZipInputStream zis = new ZipInputStream(input);
		zis.getNextEntry();

		byte[] number = new byte[1];
		readAll(zis, number);
		
		int type = number[0];
		
		number = new byte[4];
		readAll(zis, number);
		
		int wordCount = (number[0] & 0xFF) | ((number[1] & 0xFF) << 8) | ((number[2] & 0xFF) << 16) | ((number[3] & 0xFF) << 24);
		
		crystalized.weights = new double[wordCount][wordCount];

		for(int i=0;i<wordCount;i++) {
			for(int j=i+1;j<wordCount;j++) {
				
				switch(type) {
					case CMP_SHORT:
						
						number = new byte[2];
						readAll(zis, number);
						short distS = (short)((number[0] & 0xFF) | ((number[1] & 0xFF) << 8));
						
						double dist = distS / (double)Short.MAX_VALUE;
						crystalized.weights[i][j] = crystalized.weights[j][i] = dist;

					break;
					case CMP_DOUBLE:
						
						number = new byte[8];
						readAll(zis, number);
						
						long distL = (long)((number[0] & 0xFFl) | ((number[1] & 0xFFl) << 8) | ((number[2] & 0xFFl) << 16) | ((number[3] & 0xFFl) << 24) | ((number[4] & 0xFFl) << 32) | ((number[5] & 0xFFl) << 40) | ((number[6] & 0xFFl) << 48) | ((number[7] & 0xFFl) << 56));
						crystalized.weights[i][j] = crystalized.weights[j][i] = Double.longBitsToDouble(distL);
						
					break;
				}
				
				
			}	
		}
		
		crystalized.wordToInt = new Hashtable<String,Integer>();
		for(int i=0;i<wordCount;i++) {
			number = new byte[1];
			readAll(zis, number);
			
			byte[] chars = new byte[number[0] & 0xFF];
			readAll(zis, chars);
			
			String str = new String(chars);
			crystalized.wordToInt.put(str, i);
			
		}
		
		
		zis.close();
		
		wordMap.addRelator(crystalized);
	}
}
