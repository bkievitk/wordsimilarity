package relations.beagle;

import java.util.Random;
import java.util.Vector;

/**
 * Vector manipulation tools.
 * @author Brent Kievit-Kylar
 */

public class VectorTools {

	public static Random rand = new Random();

	public static double[] copy(double[] v) {
		double[] ret = new double[v.length];
		for(int i=0;i<v.length;i++) {
			ret[i] = v[i];
		}
		return ret;
	}
	
	public static double[] zero(int len) {
		double[] ret = new double[len];
		for(int i=0;i<len;i++) {
			ret[i] = 0;
		}
		return ret;
	}
	
	public static double[] getPointwiseMultiply(double[] v1, double[] v2) {
		double[] ret = new double[v1.length];
		for(int i=0;i<v1.length;i++) {
			ret[i] = v1[i] * v2[i];
		}
		return ret;
	}
	
	/**
	 * Sum the entire set of vectors.
	 * @param vectors	Set of vectors.
	 * @return
	 */
	public static double[] sumVectors(Vector<double[]> vectors) {
		if(vectors.size() <= 0) {
			return null;
		}
		
		double[] ret = new double[vectors.get(0).length];
		for(double[] vec : vectors) {
			for(int i=0;i<ret.length;i++) {
				ret[i] += vec[i];
			}
		}
		return ret;
	}
		
	public static double[] rotate(double[] v, int times) {
		double[] ret = new double[v.length];
		for(int i=0;i<v.length;i++) {
			
			// We need to find the appropriate modulus index.
			// This fixes the inapropriate negative modulus problem.
			int index = (i + times) % v.length;
			if(index < 0) {
				index += v.length;
			}
			
			ret[i] = v[index];
		}
		return ret;
	}
	
	public static void show(double[] a) {
		for(double d : a) {
			System.out.print("[" + d + "]");
		}
		System.out.println();
	}
	
	public static void show(double[][] a) {
		for(double d[] : a) {
			for(double c : d) {
				System.out.print("[" + c + "]");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public static double[] limitSize(double[] v, double len) {
		double dist = dist(v);
		if(dist > len) {
			return mult(v,len/dist);
		}
		return mult(v,1.0);
	}
	
	/**
	 * Move a point a fraction of the way from one point to another.
	 * @param from
	 * @param to
	 * @param amount
	 */
	public static void setBetween(double[] from, double[] to, double amount) {
		for(int i=0;i<from.length;i++) {
			from[i] = from[i] * (1-amount) + to[i] * amount;
		}
	}
	
	public static double[] getBetween(double[] from, double[] to, double amount) {
		double[] ret = new double[from.length];
		for(int i=0;i<from.length;i++) {
			ret[i] = from[i] * (1-amount) + to[i] * amount;
		}
		return ret;
	}

	/**
	 * Add the first vector to the second one.
	 * @param from
	 * @param to
	 */
	public static void setAdd(double[] from, double[] to) {
		for(int i=0;i<from.length;i++) {
			to[i] += from[i];
		}
	}
	
	public static double[] getAdd(double[] from, double[] to) {
		double[] ret = new double[from.length];
		for(int i=0;i<from.length;i++) {
			ret[i] = from[i] + to[i];
		}
		return ret;
	}
	
	public static void setSub(double[] from, double[] to) {
		for(int i=0;i<from.length;i++) {
			to[i] -= from[i];
		}
	}
	
	public static double[][] transpose(double[][] vecs) {
		double[][] t = new double[vecs[0].length][vecs.length];
		for(int x=0;x<vecs.length;x++) {
			for(int y=0;y<vecs[0].length;y++) {
				t[y][x] = vecs[x][y];
			}
		}
		return t;
	}
	
	public static double[][] cov(double[][] a) {
		return mult(a,transpose(a));
	}
	
	public static double[] domEig(double[][] vec) {
		double[][] bt = {newGaussian(vec[0].length)};
		double[][] b = transpose(bt);

		double[][] a = vec;
		for(int i=0;i<100;i++) {
			a = mult(a,vec);
			double[][] eig = mult(a,b);
			for(int j=0;j<eig.length;j++) {
				System.out.print(eig[j][0]);
			}
			System.out.println();
		}
		return null;
	}
	
	// Matricies are x, by y
	// Matrix.length = height, Matrix[0].length = width
	public static double[][] mult(double[][] a, double[][] b) {
		double[][] c = new double[a.length][b[0].length];
		for(int x=0;x<c.length;x++) {
			for(int y=0;y<c[0].length;y++) {
				double sum = 0;
				for(int i=0;i<a[0].length;i++) {
					sum += a[x][i] * b[i][y];
				}
				c[x][y] = sum;
			}			
		}
		return c;
	}
	
	public static double[][] getSubMean(double[][] vecs) {
		return getSubMean(vecs,mean(vecs));
	}
	
	public static double[][] getSubMean(double[][] vecs, double[] mean) {
		double[][] subMean = new double[vecs.length][];
		
		// Subtract from each entry.
		int i = 0;
		for(double[] vec : vecs) {
			subMean[i] = VectorTools.getSub(mean, vec);
			i++;
		}
		
		return subMean;
	}
	
	public static double[] mean(double[][] vecs) {
		double[] mean = new double[vecs[0].length];
		for(double[] entry : vecs) {
			VectorTools.setAdd(entry, mean);
		}
		mean = VectorTools.mult(mean, 1.0/vecs.length);
		return mean;
	}
	
	public static double[] getSub(double[] a, double[] b) {
		double[] ret = new double[a.length];
		for(int i=0;i<a.length;i++) {
			ret[i] = a[i] - b[i];
		}
		return ret;
	}
	
	public static double dist(double[] a, double[] b) {
		double sum = 0;
		for(int i=0;i<a.length;i++) {
			sum += (a[i] - b[i]) * (a[i] - b[i]);
		}
		return Math.sqrt(sum);
	}
	
	public static double dist(double[] a) {
		double sum = 0;
		for(int i=0;i<a.length;i++) {
			sum += (a[i] * a[i]);
		}
		return Math.sqrt(sum);
	}
	
	public static double dot(double[] a, double[] b) {
		double sum = 0;
		for(int i=0;i<a.length;i++) {
			sum += a[i] * b[i];
		}
		return sum;
	}
	
	public static double[] mult(double[] a, double b) {
		double[] ret = new double[a.length];
		for(int i=0;i<a.length;i++) {
			ret[i] = a[i] * b;
		}
		return ret;
	}
	
	public static double[] normalize(double[] a) {
		double dist = dist(a);
		if(dist == 0) {
			return mult(a,0);
		}
		return mult(a,1/dist);
	}
	
	public static double[] setLen(double[] a, double len) {
		double dist = dist(a);
		return mult(a,len/dist);
	}
	
	public static double[] normalizeLen(double[] a) {
		double dist = dist(a);
		return mult(a,a.length/dist);
	}
		
	public static double getCosine(double[] a, double[] b) {
		double sum = dist(a) * dist(b);
		if(sum == 0) {
			return -1;
		}
		return dot(a,b) / sum;
	}
	
	public static double getAngle(double[] a, double[] b) {
		return Math.acos(getCosine(a,b));
	}
		
	public static double[] convolve(double[] a, double[] b) {
		double[] ret = new double[a.length];
		
		for(int i=0;i<a.length;i++) {
			ret[i] = 0;
			for(int j=0;j<a.length;j++) {
				ret[i] += a[j] * b[(i-j+2*a.length) % a.length];
			}
		}
		
		return ret;
	}
	
	public static double[] corelate(double[] a, double[] b) {
		double[] ret = new double[a.length];
		
		for(int i=0;i<a.length;i++) {
			ret[i] = 0;
			for(int j=0;j<a.length;j++) {
				ret[i] += a[j] * b[(i+j+2*a.length) % a.length];
			}
		}
		
		return ret;
	}
	
	public static double[] newGaussian(int len) {
		double[] ret = new double[len];
		for(int i=0;i<len;i++) {
			ret[i] = rand.nextGaussian();
		}
		return ret;
	}
	
	public static double[] newIndicator(int len) {
		double[] ret = new double[len];
		for(int i=0;i<len;i++) {
			ret[i] = rand.nextInt(2) - 1;
		}
		return ret;
	}
	
	public static double[] rearangeForward(double[] a, int[] b) {
		double[] ret = new double[a.length];
		for(int i=0;i<a.length;i++) {
			ret[i] = a[b[i]];
		}
		return ret;
	}
	
	public static double[] rearangeBackward(double[] a, int[] b) {
		double[] ret = new double[a.length];
		for(int i=0;i<a.length;i++) {
			ret[b[i]] = a[i];
		}
		return ret;
	}
	
	public static int[] getRandomOrder(int size) {
		int[] ret = new int[size];
		int[] b = new int[size];
		
		for(int i=0;i<size;i++) {
			b[i] = i;
		}
		
		for(int i=0;i<size;i++) {
			int j = rand.nextInt(size - i);
			ret[i] = b[j];
			b[j] = b[size-i-1];
		}
		
		return ret;
	}
}
