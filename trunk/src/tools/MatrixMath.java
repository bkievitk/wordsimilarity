package tools;

import java.util.Vector;

public class MatrixMath {
	
	public static double distSqr(double[] a, double[] b) {
		double sum = 0;
		for(int i=0;i<a.length;i++) {
			double diff = a[i] - b[i];
			sum += diff * diff;
		}
		return sum;
	}
	
	public static double[][] copy(double[][] a) {
		double[][] b = new double[a.length][];
		for(int i=0;i<a.length;i++) {
			b[i] = new double[a[i].length];
			for(int j=0;j<a[i].length;j++) {
				b[i][j] = a[i][j];
			}
		}
		return b;
	}

	public static double[] copy(double[] a) {
		double[] ret = new double[a.length];
		for(int i=0;i<a.length;i++) {
			ret[i] = a[i];
		}
		return ret;
	}
	
	public static int[] copy(int[] a) {
		int[] ret = new int[a.length];
		for(int i=0;i<a.length;i++) {
			ret[i] = a[i];
		}
		return ret;
	}
	
	public static double dist(double[] a, double[] b) {
		return Math.sqrt(distSqr(a, b));
	}
	
	public static double[] average(Vector<double[]> pts) {
		if(pts == null || pts.size() == 0) {
			return null;
		}
		
		double[] sum = new double[pts.get(0).length];
		for(double[] pt : pts) {
			for(int i=0;i<sum.length;i++) {
				sum[i] += pt[i];
			}
		}
		
		for(int i=0;i<sum.length;i++) {
			sum[i] /= pts.size();
		}
		
		return sum;
	}
	
	public static double[] average(double[] a, double[] b) {
		double[] ret = new double[a.length];
		for(int i=0;i<ret.length;i++) {
			ret[i] = (a[i] + b[i]) / 2;
		}
		return ret;
	}
	
	public static double[][] multiply(double[][] a, double[][] b) {
				
		double[][] c = new double[b.length][a[0].length];
		for(int x=0;x<c.length;x++) {
			for(int y=0;y<c[0].length;y++) {
				double sum = 0;
				for(int i=0;i<a.length;i++) {
					sum += a[i][y] * b[x][i];
				}
				c[x][y] = sum;
			}
		}
		return c;
	}
	
	public static double[][] transpose(double[][] a) {
		double[][] ret = new double[a[0].length][a.length];
		for(int x=0;x<a.length;x++) {
			for(int y=0;y<a[0].length;y++) {
				ret[x][y] = a[y][x];
			}
		}
		return ret;
	}
	
	public static double[][] sub(double[][] a, double[][] b) {
		double[][] ret = new double[a.length][a[0].length];
		for(int x=0;x<a.length;x++) {
			for(int y=0;y<a[0].length;y++) {
				ret[x][y] = a[x][y] - b[x][y];
			}
		}
		return ret;
	}
	
	public static double[][] add(double[][] a, double[][] b) {
		double[][] ret = new double[a.length][a[0].length];
		for(int x=0;x<a.length;x++) {
			for(int y=0;y<a[0].length;y++) {
				ret[x][y] = a[x][y] + b[x][y];
			}
		}
		return ret;
	}
	
	public static int[] add(int[] a, int b) {
		int[] ret = new int[a.length];
		for(int x=0;x<a.length;x++) {
			ret[x] = a[x] + b;
		}
		return ret;
	}
	
	public static double[] add(double[] a, double[] b) {
		double[] ret = new double[a.length];
		for(int x=0;x<a.length;x++) {
			ret[x] = a[x] + b[x];
		}
		return ret;
	}
	
	public static double[][] scale(double[][] a, double b) {
		double[][] ret = new double[a.length][a[0].length];
		for(int x=0;x<a.length;x++) {
			for(int y=0;y<a[0].length;y++) {
				ret[x][y] = a[x][y] * b;
			}
		}
		return ret;
	}
	
	public static double[] scale(double[] a, double b) {
		double[] ret = new double[a.length];
		for(int x=0;x<a.length;x++) {
			ret[x] = a[x] + b;
		}
		return ret;
	}
	
	public static void show(double[][] t) {
		System.out.println("Matrix.");
		for(int y=0;y<t[0].length;y++) {
			for(int x=0;x<t.length;x++) {
				System.out.print("[" + t[x][y] + "]");
			}
			System.out.println();
		}
	}
	
	// Matrix Inversion code taken from here:
	///////////////////////////////////////////////////////////////////////////
	//  																	 //
	//Program file name: Inverse.java                                        //
	// 																		 //
	//� Tao Pang 2006                                                        //
	//  																	 //
	//Last modified: January 18, 2006                                        //
	//  																	 //
	//(1) This Java program is part of the book, "An Introduction to         //
	//Computational Physics, 2nd Edition," written by Tao Pang and      	 //
	//published by Cambridge University Press on January 19, 2006.      	 //
	//  																	 //
	//(2) No warranties, express or implied, are made for this program.      //
	//  																	 //
	///////////////////////////////////////////////////////////////////////////

	public static double[][] invert(double a[][]) {
		int n = a.length;
		double x[][] = new double[n][n];
		double b[][] = new double[n][n];
		int index[] = new int[n];
		
		for (int i=0; i<n; ++i) b[i][i] = 1;

		// Transform the matrix into an upper triangle
		gaussian(a, index);
		
		// Update the matrix b[i][j] with the ratios stored
		for (int i=0; i<n-1; ++i)
		for (int j=i+1; j<n; ++j)
		for (int k=0; k<n; ++k)
		b[index[j]][k] -= a[index[j]][i]*b[index[i]][k];

		// Perform backward substitutions
		for (int i=0; i<n; ++i) {
			x[n-1][i] = b[index[n-1]][i]/a[index[n-1]][n-1];
			for (int j=n-2; j>=0; --j) {
				x[j][i] = b[index[j]][i];
				for (int k=j+1; k<n; ++k) {
					x[j][i] -= a[index[j]][k]*x[k][i];
				}
				x[j][i] /= a[index[j]][j];
			}
		}
		return x;
	}

	public static void gaussian(double a[][], int index[]) {
		
		int n = index.length;
		double c[] = new double[n];

		// Initialize the index
		for (int i=0; i<n; ++i) {
			index[i] = i;
		}
		
		// Find the rescaling factors, one from each row
		for (int i=0; i<n; ++i) {
			double c1 = 0;
			for (int j=0; j<n; ++j) {
				double c0 = Math.abs(a[i][j]);
				if (c0 > c1) {
					c1 = c0;
				}
			}
			c[i] = c1;
		}
		
		// Search the pivoting element from each column
		int k = 0;
		for (int j=0; j<n-1; ++j) {
			double pi1 = 0;
			for (int i=j; i<n; ++i) {
				double pi0 = Math.abs(a[index[i]][j]);
				pi0 /= c[index[i]];
				if (pi0 > pi1) {
					pi1 = pi0;
					k = i;
				}
			}

			// Interchange rows according to the pivoting order
			int itmp = index[j];
			index[j] = index[k];
			index[k] = itmp;
			for (int i=j+1; i<n; ++i) {
				double pj = a[index[i]][j]/a[index[j]][j];
				
				// Record pivoting ratios below the diagonal
				a[index[i]][j] = pj;
				
				// Modify other elements accordingly
				for (int l=j+1; l<n; ++l) {
					a[index[i]][l] -= pj*a[index[j]][l];
				}
			}
		}
	}
}
