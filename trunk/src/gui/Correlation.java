package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Hashtable;

import mdsj.MDSJ;

import relations.beagle.VectorTools;
import relations.helpers.WordRelationCrystalized;
import tools.WeightedObject;

public class Correlation {

	public static final int DISTANCE_SPEARMAN = 0;
	public static final int DISTANCE_KENDALL = 1;
	public static final int DISTANCE_DISTANCES = 2;
	public static final int DISTANCE_DISTANCES2 = 3;
	public static final int DISTANCE_CONTENTIOUS = 4;

	public static final int CORRELATION_PEARSON = 5;
	public static final int CORRELATION_PROCRUSTES = 6;
		
	public static final String[] RANK_NAMES = {
		"spearman",
		"kendall",
		"distances",
		"distances2",
	};
	
	public static final String[] CORRELATION_NAMES = {
		"pearson",
		"procrustes",
	};
	
	public static void main(String[] args) {
		
		
		WordMap wordMap = new WordMap();
		try {
			//WordRelationCrystalized en = new WordRelationCrystalized(Color.BLACK, wordMap, new BufferedReader(new FileReader(new File("results/english.csv"))));
			//WordRelationCrystalized fr = new WordRelationCrystalized(Color.BLACK, wordMap, new BufferedReader(new FileReader(new File("results/french.csv"))));

			String root = "../semantic_probabilisticBeagle/philpapers/";
			WordRelationCrystalized en = new WordRelationCrystalized(Color.BLACK, wordMap, new BufferedReader(new FileReader(new File(root + "iep_allSim.csv"))));
			WordRelationCrystalized fr = new WordRelationCrystalized(Color.BLACK, wordMap, new BufferedReader(new FileReader(new File(root + "sep_allSim.csv"))));
			System.out.println(correlationProcrustes(en.weights, fr.weights));
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		/*
		double[] x = {10+1,10+.5,10+0};
		double[] y = {10+1,10+.4,10+.1};
		System.out.println(correlationPearson(x,y));
		
		// Get new ranks for list 1.
		WeightedObject[] rank1 = {
				new WeightedObject("a",2.1),
				new WeightedObject("b",1.1),
				new WeightedObject("c",4.1),
				new WeightedObject("d",3.1),
				};
		//Arrays.sort(rank1);		
		
		// Get new ranks for list 2.
		WeightedObject[] rank2 = {
				new WeightedObject("a",3.1),
				new WeightedObject("b",4.1),
				new WeightedObject("c",1.1),
				new WeightedObject("d",2.1),
				};			
		//Arrays.sort(rank2);	
		
		
		Hashtable<Object,Integer> rank2_a = new Hashtable<Object,Integer>();
		for(int j=0;j<rank2.length;j++) {
			rank2_a.put(rank2[j].object, j + 1);
		}
		
		double[] x = new double[rank1.length];
		for(int i=0;i<x.length;i++) {
			x[i] = rank1[i].weight;
		}
		
		double[] y = new double[rank1.length];
		for(int i=0;i<y.length;i++) {
			y[i] = rank2[rank2_a.get(rank1[i].object) - 1].weight;
		}
		
		for(int i=0;i<x.length;i++) {
			System.out.println(x[i] + " " + y[i]);
		}
		
		int[] p1 = new int[rank1.length];
		for(int i=0;i<p1.length;i++) {
			p1[i] = i;
		}
		
		int[] p2 = new int[rank1.length];
		for(int i=0;i<p1.length;i++) {
			p2[i] = rank2_a.get(rank1[i].object) - 1;
		}

		for(int i=0;i<x.length;i++) {
			System.out.println(p1[i] + " " + p2[i]);
		}
		*/
		//double[] x = {0.625,0.64,0.68,0.733,0.741,0.749,0.752,0.756,0.798,0.835,1.0};
		//double[] y = {0.79,0.799,0.803,0.802,0.776,0.76,0.741,0.784,0.833,0.854,1.0};
		//System.out.println(correlationPearson(x,y));
		
	}

	public static double distance(double[][] p1, double[][] p2, int type) {
		switch(type) {
			case CORRELATION_PROCRUSTES: return correlationProcrustes(p1,p2);
		}
		return -1;
	}
	
	public static double distance(double[] p1, double[] p2, int type) {
		switch(type) {
			case CORRELATION_PEARSON: return correlationPearson(p1,p2);
		}
		return -1;
	}
	
	public static void normalize(double[][] a) {
		double max = a[1][0];
		double min = a[1][0];
		
		for(int x=0;x<a.length;x++) {
			for(int y=0;y<a[x].length;y++) {
				if(x != y) {
					a[x][y] = Math.max(0, a[x][y]);
					max = Math.max(max, a[x][y]);
					min = Math.min(min, a[x][y]);
				}
			}	
		}
		
		for(int x=0;x<a.length;x++) {
			for(int y=0;y<a[x].length;y++) {
				if(x == y) {
					a[x][y] = 0;
				} else {
					a[x][y] = (a[x][y] - min) / (max - min);
					a[x][y] = 1 - a[x][y];
				}
				//System.out.print(a[x][y] + ",");
			}	
			//System.out.println();
		}
	}
	
	public static double[][][] transformProcrustes(double[][] a, double[][] b) {
		
		for(int x=0;x<a.length;x++) {
			for(int y=0;y<a[x].length;y++) {
				if(x == y) {
					a[x][y] = 0;
					b[x][y] = 0;
				} else {
					a[x][y] = Math.max(0.1, Math.min(.99, 1 - a[x][y]));
					b[x][y] = Math.max(0.1, Math.min(.99, 1 - b[x][y]));
				}
			}	
		}		
		
		double[][] mdsA = VectorTools.transpose(MDSJ.classicalScaling(a, 2));
		double[][] mdsB = VectorTools.transpose(MDSJ.classicalScaling(b, 2));

		normalizeTranslation(mdsA);
		normalizeTranslation(mdsB);
				
		normalizeScaling(mdsA);
		normalizeScaling(mdsB);

		double[][] mdsB1 = normalizeRotation(mdsA, mdsB);
		
		for(int i=0;i<mdsB.length;i++) {
			mdsB[i][0] = -mdsB[i][0];
		}
		double[][] mdsB2 = normalizeRotation(mdsA, mdsB);
	
		double sumDiff1 = 0;
		for(int i=0;i<mdsA.length;i++) {
			sumDiff1 += VectorTools.dist(mdsA[i], mdsB1[i]);
		}
		
		double sumDiff2 = 0;
		for(int i=0;i<mdsA.length;i++) {
			sumDiff2 += VectorTools.dist(mdsA[i], mdsB2[i]);
		}

		sumDiff1 /= mdsA.length;
		sumDiff2 /= mdsA.length;
		System.out.println("d1: " + sumDiff1);
		System.out.println("d2: " + sumDiff2);
		System.out.println("Avg dist: " + Math.min(sumDiff1,sumDiff2));
		
		if(sumDiff1 > sumDiff2) {
			double[][][] ret = {mdsA,mdsB2};
			return ret;
		} else {
			double[][][] ret = {mdsA,mdsB1};
			return ret;
		}
	}

	public static double weightProcrustes(double[][] a, double[][] b) {

		for(int x=0;x<a.length;x++) {
			for(int y=0;y<a[x].length;y++) {
				if(x == y) {
					a[x][y] = 0;
					b[x][y] = 0;
				} else {
					a[x][y] = Math.max(0.1, Math.min(.99, 1 - a[x][y]));
					b[x][y] = Math.max(0.1, Math.min(.99, 1 - b[x][y]));
				}
			}	
		}
		
		//normalize(a);
		//normalize(b);
		
		double[][] mdsA = VectorTools.transpose(MDSJ.classicalScaling(a, 2));
		double[][] mdsB = VectorTools.transpose(MDSJ.classicalScaling(b, 2));

		normalizeTranslation(mdsA);
		normalizeTranslation(mdsB);
				
		normalizeScaling(mdsA);
		normalizeScaling(mdsB);

		double[][] mdsB1 = normalizeRotation(mdsA, mdsB);
		
		for(int i=0;i<mdsB.length;i++) {
			mdsB[i][0] = -mdsB[i][0];
		}
		double[][] mdsB2 = normalizeRotation(mdsA, mdsB);
	
		double sumDiff1 = 0;
		for(int i=0;i<mdsA.length;i++) {
			sumDiff1 += VectorTools.dist(mdsA[i], mdsB1[i]);
		}
		
		double sumDiff2 = 0;
		for(int i=0;i<mdsA.length;i++) {
			sumDiff2 += VectorTools.dist(mdsA[i], mdsB2[i]);
		}

		sumDiff1 /= mdsA.length;
		sumDiff2 /= mdsA.length;
		
		return Math.min(sumDiff1, sumDiff2);
	}
	
	private static double[][] normalizeRotation(double[][] mdsA, double[][] mdsB) {
		
		double num = 0;
		double denom = 0;
		
		for(int i=0;i<mdsA.length;i++) {
			num   += mdsA[i][0] * mdsB[i][1] - mdsA[i][1] * mdsB[i][0];
			denom += mdsA[i][0] * mdsB[i][0] + mdsA[i][1] * mdsB[i][1];
		}
		double angle = -Math.atan2(num, denom);
		double cosAngle = Math.cos(angle);
		double sinAngle = Math.sin(angle);
				
		double[][] mdsBNew1 = new double[mdsB.length][mdsB[0].length];
		for(int i=0;i<mdsA.length;i++) {			
			double newX = cosAngle * mdsB[i][0] - sinAngle * mdsB[i][1];
			double newY = sinAngle * mdsB[i][0] + cosAngle * mdsB[i][1];
			mdsBNew1[i][0] = newX;
			mdsBNew1[i][1] = newY;			
		}
		
		return mdsBNew1;
	}
	
	public static double correlationProcrustes(double[][] a, double[][] b) {
					
		for(int x=0;x<a.length;x++) {
			for(int y=0;y<a[x].length;y++) {
				if(x == y) {
					a[x][y] = 0;
					b[x][y] = 0;
				} else {
					a[x][y] = Math.max(0.01, Math.min(.99, 1 - a[x][y]));
					b[x][y] = Math.max(0.01, Math.min(.99, 1 - b[x][y]));
				}
			}	
		}		
		
		double[][] mdsA = VectorTools.transpose(MDSJ.classicalScaling(a, 2));
		double[][] mdsB = VectorTools.transpose(MDSJ.classicalScaling(b, 2));

		//VectorTools.show(mdsA);
		//VectorTools.show(mdsB);
		
		normalizeTranslation(mdsA);
		normalizeTranslation(mdsB);
				
		normalizeScaling(mdsA);
		normalizeScaling(mdsB);
				
		
		double num = 0;
		double denom = 0;
		
		for(int i=0;i<mdsA.length;i++) {
			num   += mdsA[i][0] * mdsB[i][1] - mdsA[i][1] * mdsB[i][0];
			denom += mdsA[i][0] * mdsB[i][0] + mdsA[i][1] * mdsB[i][1];
		}
		double angle = -Math.atan2(num, denom);
		double cosAngle = Math.cos(angle);
		double sinAngle = Math.sin(angle);
				
		for(int i=0;i<mdsA.length;i++) {			
			double newX = cosAngle * mdsB[i][0] - sinAngle * mdsB[i][1];
			double newY = sinAngle * mdsB[i][0] + cosAngle * mdsB[i][1];
			mdsB[i][0] = newX;
			mdsB[i][1] = newY;			
		}
			
		/*
		
		BufferedImage image = new BufferedImage(800,600,BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		
		double[] ranges = {-3,3,-3,3};
				
		g.setColor(Color.RED);
		double[][] mdsAT = VectorTools.transpose(mdsA);
		Stats.showGraph(g, new Rectangle(0,0,image.getWidth(),image.getHeight()), mdsAT[0], mdsAT[1], ranges, philosophers, false);
		
		g.setColor(Color.GREEN);
		double[][] mdsBT = VectorTools.transpose(mdsB);
		Stats.showGraph(g, new Rectangle(0,0,image.getWidth(),image.getHeight()), mdsBT[0], mdsBT[1], ranges, philosophers, false);
				
		PictureFrame.makeFrame(image);
		*/
		
		double sumDiff = 0;
		for(int i=0;i<mdsA.length;i++) {
			sumDiff += VectorTools.dist(mdsA[i], mdsB[i]);
		}
		
		return sumDiff / mdsA.length;
	}
	
	public static void normalizeTranslation(double[][] values) {
		double[] sum = new double[values[0].length];
		for(double[] vec : values) {
			for(int i=0;i<vec.length;i++) {
				sum[i] += vec[i];
			}
		}
		for(int i=0;i<sum.length;i++) {
			sum[i] /= values.length;
		}
		for(double[] vec : values) {
			for(int i=0;i<vec.length;i++) {
				vec[i] -= sum[i];
			}
		}
	}

	/*
	public static void normalizeScaling2(double[][] values) {
		double sum = 0;
		for(int i=0;i<values.length;i++) {
			sum += VectorTools.dist(values[i]);
		}
		sum /= values.length;
		
		for(int i=0;i<values.length;i++) {
			
		}
	}
	*/
	
	public static void normalizeScaling(double[][] values) {
		
		double sumOfSums = 0;
		for(int i=0;i<values.length;i++) {
			
			double sum = 0;
			for(int j=0;j<values[i].length;j++) {
				sum += values[i][j] * values[i][j];
			}
			sumOfSums += Math.sqrt(sum);
		}
		double scaller = values.length / sumOfSums;
		

		for(int i=0;i<values.length;i++) {
			values[i] = VectorTools.mult(values[i], scaller);
		}		
	}
		
	public static double correlationPearson(double[] x, double[] y) {
		
		long n = x.length;		
		double xSum = 0;
		double ySum = 0;
		double xySum = 0;
		double xxSum = 0;
		double yySum = 0;
		
		for(int i=0;i<x.length;i++) {
			xSum += x[i];
			ySum += y[i];
			xySum += x[i] * y[i];
			xxSum += x[i] * x[i];
			yySum += y[i] * y[i];
		}
		
		return (n * xySum - xSum * ySum) / (Math.sqrt(n * xxSum - xSum * xSum) * Math.sqrt(n * yySum - ySum * ySum));
	}
	
	public static double distance(int[] p1, int[] p2, int type) {
		switch(type) {
			case DISTANCE_SPEARMAN: return distanceSpearman(p1,p2);
			case DISTANCE_KENDALL: return distanceKendall(p1,p2);
			case DISTANCE_DISTANCES: return distanceDistances(p1,p2);
			case DISTANCE_DISTANCES2: return distanceDistances2(p1,p2);
			case DISTANCE_CONTENTIOUS: return distanceContentious(p1,p2);
		}
		return -1;
	}
	
	public static double distanceSpearman(int[] p1, int[] p2) {
		
		// computes Spearman's rho
		long sum = 0;
		long size = p1.length;
		for(int i=0; i<size; i++) {
			long diff = p1[i] - p2[i];
		    sum += diff * diff;
		}
		return 1 - 6 * sum / (double)(size * (size * size - 1)); 
	}

	public static double distanceKendall(int[] p1, int[] p2) {
		
		// computes Kendall's tau
		long sum = 0;
		long size = p1.length;
		for(int i=0; i<size; i++) {
			for(int j=0; j<size; j++) {
				if ((p1[i] < p1[j]) && (p2[i] < p2[j])) {
					sum ++;
				}
			}
		}
		return 4 * sum / (double)(size * (size - 1)) - 1; 
	}
	
	public static double distanceDistances(int[] p1, int[] p2) {
		
		// computes the distances coefficient, which may be a new measure of correlation.
		// it is based on computing the relative positions of all items in the two perms
		// and summing the absolute values of the differences.
		
		long sum = 0;
		long size = p1.length;
		for(int i=0; i<size; i++) {
			for(int j=i; j<size; j++) {
				long temp = Math.abs((p1[i] - p1[j]) - (p2[i] - p2[j]));
				sum += temp * temp;
			}
		}
		
		long s2 = size * size;
		long s3 = s2 * size;
		long sm1 = size - 1;
		long sm12 = sm1 * sm1;
		long sm13 = sm12 * sm1;
		long maxD = 2 * (size * (2 * sm13 + 3 * sm12 + sm1) / 6 + s2 * sm12 / 4);
		return 1 - 4 * sum / (double)maxD;
	}
	
	public static double distanceDistances2(int[] p1, int[] p2) {
		
		// computes the distances coefficient, which may be a new measure of correlation.
		// it is based on computing the relative positions of all items in the two perms
		// and summing the absolute values of the differences.
		// if the difference represents a reversal, the difference is squared.
		
		long sum = 0;
		long size = p1.length;

		for(int i=0; i<size; i++) {
			for(int j=i; j<size; j++) {
				long diff1 = p1[i] - p1[j];
				long diff2 = p2[i] - p2[j];
				long temp = 0;
				
				if (diff1 * diff2 > 0) {
					// both have same sign
				    temp = Math.abs(diff1 - diff2);
				}
				else {
					temp = Math.abs(diff1 - diff2) * Math.abs(diff1 - diff2);
				}
					
				sum += temp;
		    }
		}
		
		sum /= 2 * (size - 1);
		
		long x = size - 1;
		long x2 = x * x;
		long x3 = x2 * x;
		long maxDistance = 4 * (size * (2 * x3 + 3 * x2 + x) / 6 - x * x * size * size / 4) / (2 * x);
		
		return 1 - 2 * sum / (double)maxDistance;
	}
	
	public static double distanceContentious(int[] p1, int[] p2) {
		
		/* 
		 * I think there needs to be some consideration of how far the LCS is
		 * displaced between the two permutations ... but this only makes sense
		 * if the elements are consecutive.  The issue comes from the two permutations
		 * 5 3 4 2 1 and 5 4 3 1 2.  Currently the second is judged as more similar to
		 * 1 2 3 4 5 than the first, mostly because they have the same length of LCS and 
		 * in the second, the element 3 is in its proper place.
		 * However a case could be made for the first one, since the "3 4" LCS is only 
		 * one spot out of its home. 
		 
		 * 20080731 - maybe the elements of the LCS should not be excluded from the rho computation.
		 * This might be quite good - elements of the LCS that are in the same location
		 * in both permutations will now count "double" towards the similarity.  There are several ways
		 * to combine the two measures though.  I can compute the two similarity measures and average them (giving
		 * a value in the range [0..1]) or multiply them (value in the range [0..1] but biased towards 0, or 
		 * multiply them and take the square root (range [0..1], who knows what bias) or subtract each from 1,
		 * take the product and subtract that from 1 (range [0..1], biased towards 1). 
		 *
		 * 20080801 - the rho factor needs to be scaled down so that it can't reverse the ranking of two
		 * permutations that have the same length LCS.  The LCS rankings are all multiples of ... 1/(size-1)
		 * so if I scale the rho from [0..1] to [0..1/(size-1}] that should work ok.
		 *
		 * This is giving quite good results!
		 *
		 */

		int size = p1.length;
		
		// see comment 20080731 above
		int[] lcs = findLCS(p1,p2);
		int temp = 0;
		int maxDiff = 0;
		int LCSLength = 0;
		int x = size - 1;
		
		for (int i = 0; i < size; i++) {
		    if (lcs[i] == 1) {
				LCSLength++;
			}
		    int val = p1[i] - 1;
		    int diff = p1[val] - p2[val];
		    temp += diff * diff;    
		    if (x > 0) {
				maxDiff += x * x;
				x -= 2;            
		    }
		} // for
		maxDiff *= 2;
		//p2->myL = LCSLength;           
	      
		/*
		 * Now consider what should be returned ... if the LCS is the whole thing, obviously return 1 -
		 * but I wonder if this needs to be a special case.
		 * Now (I think) that the only perm 
		 * with an LCS of 1 is the reversal ... so if the LCSLength is 1, the return value should be
		 * 0.  For all other LCS lengths, LCSLength/size is relevant.
		 * Actually I don't think either extreme needs to be made into a special case.
		 */
		 
		double LCSScaled = (LCSLength - 1) * size / (double)(size - 1);
		double LCSSimilarity = LCSScaled / size;
		double rhoSimilarity = (1 - temp / (double)maxDiff) / (size - 1); // see 20080801 for explanation of denominator
		return LCSSimilarity + rhoSimilarity; // see 20080801 for exp.
	}
	
	private static double flips(int[] p1, int[] p2) {
		/*
		 * I want to count the smallest number of flips of adjacent elements needed to convert one
		 * permutation into the other.  The idea is: find an element in p2 that is the furthest
		 * from its position in p1 ... no, that won't necessarily find the smallest number of flips: there
		 * may be some element that gets pushed further from its home when the first one moves, because its 
		 * home lies outside the movement range of the one that is moving.
		 *
		 * How about this: suppose i is the first value that is currently to the right of its home.  i has to
		 * move to its home, so we can start with the flips needed to get it there.  Is it possible that 
		 * by doing some preliminary flips we can save time later?
		 * Nothing to the right of i's current position can be affected by i's move.  The elements between i and
		 * its home are all > i, and all will be shifted one spot to the right by i's move.  Suppose i+1 is in position 
		 * i+1.  After i moves, i+1 will be in position i+2 and will need a move to get to its home.  Can there be
		 * any advantage to making this move before moving i?  I don't think so.
		 *
		 * By experimentation and upon reflection, it is apparent that flips and Kendall tau are equivalent
		 */

		int size = p1.length;
		
		long f = 0;
		int[] p2Copy = new int[size];
		for (int i = 0; i < size; i++) {
			p2Copy[i] = p2[i];
		}

		for (int i = 0; i < size; i++) {
			int cur = p1[i];
			int j = i;
			
			while (p2Copy[j] != cur) {
				j++;
			}
				
			while (j > i) {
				p2Copy[j] = p2Copy[j - 1];
				j--;
				f++;
			}
			
			p2Copy[i] = cur;
		}
		
		return 1 - 2 * f / (double)((size - 1) * size);
	}

	private static int[] findLCS(int[] p1, int[] p2) {
		// returns the Longest Common Subsequence of p1 and p2.
		// the int array returned contains 0's and 1's; it references the
		// elements of p1 for absence/presence in the LCS
		
		int size = p1.length;
		int[] lcs = new int[size];		
		long[][] table = new long[size + 1][size + 1];
				
		// fill in the rest of the table
		for (int row = 0; row < size; row++) {
		    for (int col = 0; col < size; col++) {
				if (p1[row] == p2[col]) {
					table[row + 1][col + 1] = 1 + table[row][col];
				}
				else {
					table[row + 1][col + 1] = Math.max(table[row][col + 1], table[row + 1][col]);
				}
		    }
		}
			
		// now find the LCS
		long LCSLength = table[size][size];
		int curRow = size;
		int curCol = size;
		while (LCSLength > 0) {
		    if (p1[curRow - 1] == p2[curCol - 1]) {
		    	lcs[curRow - 1] = 1;
				curRow--;
				curCol--;
				LCSLength--;
		    }
		    else if (table[curRow][curCol] == table[curRow][curCol - 1]) {
				curCol--;
			}
		    else {
				curRow--;
			}
		}
		return lcs;
	}
}
