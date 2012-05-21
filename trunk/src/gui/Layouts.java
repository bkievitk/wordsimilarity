package gui;

import java.awt.Dimension;
import java.util.Random;
import mdsj.MDSJ;
import relations.WordRelator;
import tools.TransformMy3D;
import tools.Tsne;

/**
 * The layout manager provides a set of layouts for the word nodes.
 * 
 * @author bkievitk
 */

public class Layouts {

	public static final Random rand = new Random();

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
				word1.location[0] = .7;
			} else {
				word1.location[0] = relator1Sum / (relator1Sum + relator2Sum);
			}
			
			//System.out.println(relator1Sum + " " + relator2Sum + " " + word1.location[0]);
			
			
			word1.location[1] = rand.nextDouble();
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
			newSpace = MDSJ.classicalScaling(dists, 3);
		} else {
			newSpace = MDSJ.classicalScaling(dists, 2);
		}
		
		int i=0;
		for(WordNode t1 : wordMap.getActiveWordNodeList()) {
			double x1 = (newSpace[0][i] + .5) * dim.width;
			double y1 = (newSpace[1][i] + .5) * dim.height;
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
