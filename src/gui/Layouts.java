package gui;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import mdsj.MDSJ;
import relations.WordRelator;
import tools.TransformMy3D;
import tools.Tsne;
import tools.VectorTools;

/**
 * The layout manager provides a set of layouts for the word nodes.
 * 
 * @author bkievitk
 */

public class Layouts {

	public static final Random rand = new Random();

	public static void translationLayout(WordMap wordMap, Hashtable<String,Integer> language1, Hashtable<String,Integer> language2) {
				
		int[] match = 	{0,1,2,2,2,3,3,4,4,5,5,5,5,6,6,7,8,9,10,10,10,11,11,12,13,13,13,14,15,15,16,13,4,10,6,6,4};
		int[] counts = new int[17];
		
		for(String s : language1.keySet()) {
			wordMap.setWordStatus(s, true);
			
			int x = match[language1.get(s)];
			counts[x]++;
			
			wordMap.activeWords.get(s).location[0] = x;
			wordMap.activeWords.get(s).location[1] = counts[x];
		}
		
		for(String s : language2.keySet()) {
			wordMap.setWordStatus(s, true);
			wordMap.activeWords.get(s).location[0] = language2.get(s);
			wordMap.activeWords.get(s).location[1] = 0;
		}
	}
	
	public static void layoutProcrustes(WordMap wordMap, WordRelator wr1, WordRelator wr2, WordRelator trans) {
		String[] words1 = wr1.getWords().toArray(new String[0]);
		String[] words2 = wr2.getWords().toArray(new String[0]);
		Arrays.sort(words1);

		Vector<String> words1Order = new Vector<String>();
		Vector<String> words2Order = new Vector<String>();
		
		for(String word1 : words1) {
			if(wordMap.activeWords.get(word1) != null) {
				for(String word2 : words2) {
					if(wordMap.activeWords.get(word2) != null) {
						if((trans == null && word1.equals(word2)) || (trans != null && trans.getDistance(word1, word2) > .99)) {
							words1Order.add(word1);
							words2Order.add(word2);
							break;
						}
					}
				}
			}
		}
		
		//for(int i=0;i<words1Order.size();i++) {
		//	System.out.println(words1Order.get(i) + " " + words2Order.get(i));
		//}

		double[][] sim1 = new double[words1Order.size()][words1Order.size()];
		for(int i=0;i<sim1.length;i++) {
			for(int j=0;j<sim1.length;j++) {
				sim1[i][j] = wr1.getDistance(words1Order.get(i), words1Order.get(j));
			}
		}
		
		double[][] sim2 = new double[words2Order.size()][words2Order.size()];
		for(int i=0;i<sim2.length;i++) {
			for(int j=0;j<sim2.length;j++) {
				sim2[i][j] = wr2.getDistance(words2Order.get(i), words2Order.get(j));
			}
		}
						
		double[][][] ret = Correlation.transformProcrustes(sim1, sim2);
		double[][] pos1 = ret[0];
		double[][] pos2 = ret[1];
		
		for(int i=0;i<words1Order.size();i++) {
			WordNode wn = wordMap.words.get(words1Order.get(i));
			double[] loc = {pos1[i][0], pos1[i][1], 0};
			wn.location = loc;
		}
		
		for(int i=0;i<words2Order.size();i++) {
			WordNode wn = wordMap.words.get(words2Order.get(i));
			double[] loc = {pos2[i][0], pos2[i][1], 0};
			wn.location = loc;
		}
	}
	
	/**
	 * Place randomly around the available space.
	 * @param wordMap
	 * @param dim
	 */
	public static void layoutRandom(WordMap wordMap, Dimension dim) {
		for(WordNode node : wordMap.getActiveWordNodeList()) {
			node.location[0] = rand.nextInt(dim.width-20) + 10;
			node.location[1] = rand.nextInt(dim.height-20) + 10;
			node.location[2] = 0;
		}
	}		

	/**
	 * Layout in a grid minimizing the difference between the x and the y step.
	 * @param wordMap
	 * @param dim
	 */
	public static void layoutGrid(WordMap wordMap, Dimension dim) {
				
		int screenWidth = dim.width;
		int screenHeight = dim.height;
		int count = wordMap.getActiveWordNodeList().size();
		double cellWidth = Math.sqrt((screenWidth * screenHeight) / (double)count);
		
		int numRows = (int)(screenWidth / cellWidth);
		
		int x = 0;
		int y = 0;
		
		for(WordNode word : wordMap.getActiveWordNodeList()) {
			word.location[0] = (x * cellWidth);
			word.location[1] = (y * cellWidth);
			word.location[2] = 0;
			x++;
			if(x > numRows) {
				x = 0;
				y++;
			}
		}
	}
		
	public static void layoutBinary(WordMap wordMap, WordRelator wordRelator1, WordRelator wordRelator2) {
		for(WordNode word1 : wordMap.getActiveWordNodeList()) {

			double relator1Sum = 0;
			double relator2Sum = 0;
			for(WordNode word2 : wordMap.getActiveWordNodeList()) {
				if(!word1.equals(word2)) {
					double relator1Add = Math.pow(wordRelator1.getDistance(word1.word, word2.word), 4);
					double relator2Add = Math.pow(wordRelator2.getDistance(word1.word, word2.word), 4);
					
					if(relator1Add != relator2Add) {
						System.out.println(relator1Add + " " + relator2Add);
					}
					
					relator1Sum += relator1Add;
					relator2Sum += relator2Add;
				}
			}
						
			if(relator1Sum + relator2Sum == 0) {
				word1.location[0] = .5;
			} else {
				word1.location[0] = relator1Sum / (relator1Sum + relator2Sum);
			}
			
			word1.location[1] = relator1Sum + relator2Sum;
			word1.location[2] = 0;
			
		}
	}
	
	/**
	 * Layout with a given word in the center.
	 * All other words then surround it with their distance proportional to the similarity to the center word.
	 * @param wordMap
	 * @param dim
	 * @param wordRelator
	 * @param word
	 */
	public static void layoutWordCentered(WordMap wordMap, Dimension dim, WordRelator wordRelator, String word) {
				
		if(word == null || wordRelator == null || !wordMap.activeWords.containsKey(word)) {
			return;
		}

		double max = 0;
		for(WordNode wordNode : wordMap.getActiveWordNodeList()) {
			max = Math.max(max, wordRelator.getDistance(word, wordNode.word));
		}
		
		int cx = dim.width / 2;
		int cy = dim.height / 2;
		double scaller = Math.min(cx,cy) / max - 20;
		
		for(WordNode wordNode : wordMap.getActiveWordNodeList()) {
			if(wordNode.word.equals(word)) {
				wordNode.location[0] = cx;
				wordNode.location[1] = cy;								
			} else {
				double dist = (max - wordRelator.getDistance(word, wordNode.word)) * scaller;
				double angle = rand.nextDouble() * Math.PI * 2;
				wordNode.location[0] = cx + Math.cos(angle) * dist;
				wordNode.location[1] = cy + Math.sin(angle) * dist;
				wordNode.location[2] = 0;
			}
		}
	}

	
	/**
	 * Layout with a given word in the center.
	 * All other words then surround it with their distance proportional to the similarity to the center word, in 3D space.
	 * @param wordMap
	 * @param dim
	 * @param wordRelator
	 * @param word
	 */
	public static void layoutWordCentered3D(WordMap wordMap, Dimension dim, WordRelator wordRelator, String word) {
		
		if(word == null || wordRelator == null || !wordMap.activeWords.containsKey(word)) {
			return;
		}

		double max = 0;
		for(WordNode wordNode : wordMap.getActiveWordNodeList()) {
			max = Math.max(max, wordRelator.getDistance(word, wordNode.word));
		}
		
		int cx = dim.width / 2;
		int cy = dim.height / 2;
		double scaller = Math.min(cx,cy) / max - 20;
		
		for(WordNode wordNode : wordMap.getActiveWordNodeList()) {
			if(wordNode.word.equals(word)) {
				wordNode.location[0] = cx;
				wordNode.location[1] = cy;								
			} else {
				TransformMy3D t = new TransformMy3D();
				t.combine(TransformMy3D.rotateX(rand.nextDouble() * Math.PI * 2));
				t.combine(TransformMy3D.rotateY(rand.nextDouble() * Math.PI * 2));
				t.combine(TransformMy3D.rotateZ(rand.nextDouble() * Math.PI * 2));
				t.combine(TransformMy3D.stretch(scaller, scaller, scaller));
				double[] point = {1,0,0};
				point = t.apply(point);
				point[0] += cx;
				point[1] += cy;
				
				wordNode.location = point;
			}
		}
	}
	
	/**
	 * Keep the relative position of all of the nodes but fit them into the screen as well as possible.
	 * @param wordMap
	 * @param dim
	 */
	public static void layoutFitScreen(WordMap wordMap, Dimension dim) {
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double minZ = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double maxY = Double.MIN_VALUE;
		double maxZ = Double.MIN_VALUE;
		
		for(WordNode w : wordMap.activeWords.values()) {
			minX = Math.min(minX, w.location[0]);
			minY = Math.min(minY, w.location[1]);
			minZ = Math.min(minZ, w.location[2]);
			maxX = Math.max(maxX, w.location[0]);
			maxY = Math.max(maxY, w.location[1]);
			maxZ = Math.max(maxY, w.location[2]);
		}

		int padding = 20;
		
		
		double scaleX = ((dim.getWidth() - padding * 2) / (maxX - minX));
		double scaleY = ((dim.getHeight() - padding * 2) / (maxY - minY));
		double scaleZ = ((((dim.getHeight() + dim.getWidth()) / 2) - padding * 2) / (maxZ - minZ));
		
		if(maxX - minX < .000001) {
			scaleX = 1;
		} if(maxY - minY < .000001) {
			scaleY = 1;
		} if(maxZ - minZ < .000001) {
			scaleZ = 1;
		}
		
		double shiftX = -(minX * scaleX) + padding;
		double shiftY = -(minY * scaleY) + padding;
		double shiftZ = -(minZ * scaleZ) + padding - ((dim.getHeight() + dim.getWidth()) / 4);
		
		for(WordNode w : wordMap.activeWords.values()) {
			double x = w.location[0] * scaleX + shiftX;
			double y = w.location[1] * scaleY + shiftY;
			double z = w.location[2] * scaleZ + shiftZ;
			w.location[0] = x;
			w.location[1] = y;
			w.location[2] = z;
		}
	}
	
	/**
	 * Layout according to MDS.
	 * @param wordMap
	 * @param dim
	 * @param wordRelator
	 */
	public static void layoutMDS(WordMap wordMap, Dimension dim, WordRelator wordRelator, boolean threeD) {
		if (wordRelator == null) {
			return;
		}
		
		double[][] dists = new double[wordMap.getActiveWordNodeList().size()][wordMap.getActiveWordNodeList().size()];
		int x,y;
		
		/*
		double max = 0;
		double min = 0;
		x = 0;
		for(WordNode t1 : wordMap.getActiveWordNodeList()) {
			y = 0;
			for(WordNode t2 : wordMap.getActiveWordNodeList()) {
				dists[x][y] = wordRelator.getDistance(t1.word, t2.word);
				max = Math.max(max, dists[x][y]);
				min = Math.min(min, dists[x][y]);
				y++;
			}
			x++;
		}
		
		for(int i=0;i<dists.length;i++) {
			for(int j=0;j<dists.length;j++) {
				double scalled = Math.max(0, Math.min(1, dists[i][j]));
				scalled = Math.pow(scalled, .1);
				dists[i][j] = scalled;
			}	
		}
		*/
		
		x = 0;
		for(WordNode t1 : wordMap.getActiveWordNodeList()) {
			y = 0;
			for(WordNode t2 : wordMap.getActiveWordNodeList()) {
				if(t1 == t2) {
					dists[x][y] = 0;
				} else {
					dists[x][y] = Math.max(.01, Math.min(.99, 1 - wordRelator.getDistance(t1.word, t2.word)));
					//dists[x][y] = Math.acos(wordRelator.getDistance(t1.word, t2.word));
				}
				y++;
			}
			x++;
		}
		
		
		double[][] newSpace;
		
		if(threeD) {
			newSpace = MDSJ.classicalScaling(dists, 3);
		} else {
			newSpace = MDSJ.classicalScaling(dists, 2);
		}
		
		VectorTools.show(newSpace);
		
		int i=0;
		for(WordNode t1 : wordMap.getActiveWordNodeList()) {
			double x1 = (newSpace[0][i] + .5) * dim.width;
			double y1 = (newSpace[1][i] + .5) * dim.height;
			t1.location = new double[3];
			t1.location[0] = x1;
			t1.location[1] = y1;			
			
			if(threeD) {
				double z = (newSpace[2][i] + .5) * ((dim.height + dim.width) / 2);
				t1.location[2] = z;
			} else {
				t1.location[2] = 0;
			}
			i++;
		}
	}
	
	/**
	 * Layout according to T-SNE
	 * @param wordMap
	 * @param dim
	 * @param wordRelator
	 * @param threeD
	 */
	public static void layoutTSNE(WordMap wordMap, Dimension dim, WordRelator wordRelator, boolean threeD) {
		if (wordRelator == null) {
			return;
		}
		
		double[][] dists = new double[wordMap.getActiveWordNodeList().size()][wordMap.getActiveWordNodeList().size()];
		int x,y;
		
		x = 0;
		for(WordNode t1 : wordMap.getActiveWordNodeList()) {
			y = 0;
			for(WordNode t2 : wordMap.getActiveWordNodeList()) {
				if(t1 == t2) {
					dists[x][y] = 0;
				} else {
					dists[x][y] = 1 - wordRelator.getDistance(t1.word, t2.word);
				}
				y++;
			}
			x++;
		}

		double[][] newSpace;
		

		if(threeD) {
			newSpace = Tsne.tsne_p(dists, null, 3);
		} else {
			newSpace = Tsne.tsne_p(dists, null, 2);
		}
		
		int i=0;
		for(WordNode t1 : wordMap.getActiveWordNodeList()) {
			x = (int)((newSpace[i][0] + .5) * dim.width);
			y = (int)((newSpace[i][1] + .5) * dim.height);
			t1.location[0] = x;
			t1.location[1] = y;			
			
			if(threeD) {
				t1.location[2] = (newSpace[i][2] + .5) * ((dim.height + dim.width) / 2);
			} else {
				t1.location[2] = 0;
			}
			i++;
		}
	}
}
