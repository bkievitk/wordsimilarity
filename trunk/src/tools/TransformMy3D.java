package tools;

import java.util.Vector;

public class TransformMy3D {
	
	private double[][] t;

	// Length, Width
	// x,y
	
	public TransformMy3D(double[][] t) {
		this.t = t;
	}
	
	public TransformMy3D() {
		t = new double[4][4];
		t[0][0] = 1;
		t[1][1] = 1;
		t[2][2] = 1;
		t[3][3] = 1;
	}
	
	public TransformMy3D combine(TransformMy3D transform) {
		t = MatrixMath.multiply(t,transform.t);
		return transform;
	}
	
	public TransformMy3D combineNew(TransformMy3D transform) {
		return new TransformMy3D(MatrixMath.multiply(t,transform.t));
	}
		
	public void show() {
		System.out.println("Matrix.");
		for(int y=0;y<t[0].length;y++) {
			for(int x=0;x<t.length;x++) {
				System.out.print("[" + t[x][y] + "]");
			}
			System.out.println();
		}
	}
	
	public static TransformMy3D translate(double x, double y, double z) {
		TransformMy3D transform = new TransformMy3D();
		transform.t[3][0] = x;
		transform.t[3][1] = y;
		transform.t[3][2] = z;
		return transform;
	}
	
	public static TransformMy3D stretch(double x, double y, double z) {
		TransformMy3D transform = new TransformMy3D();
		transform.t[0][0] = x;
		transform.t[1][1] = y;
		transform.t[2][2] = z;
		return transform;
	}

	public static TransformMy3D rotateX(double theta) {
		TransformMy3D transform = new TransformMy3D();
		double cosT = Math.cos(theta);
		double sinT = Math.sin(theta);
		transform.t[0][0] = 1;
		transform.t[1][1] = cosT;
		transform.t[2][2] = cosT;
		transform.t[2][1] = -sinT;
		transform.t[1][2] = sinT;
		return transform;
	}
	
	public static TransformMy3D rotateY(double theta) {
		TransformMy3D transform = new TransformMy3D();
		double cosT = Math.cos(theta);
		double sinT = Math.sin(theta);
		transform.t[1][1] = 1;
		transform.t[0][0] = cosT;
		transform.t[2][2] = cosT;
		transform.t[2][0] = sinT;
		transform.t[0][2] = -sinT;
		return transform;
	}
	
	public static TransformMy3D rotateZ(double theta) {
		TransformMy3D transform = new TransformMy3D();
		double cosT = Math.cos(theta);
		double sinT = Math.sin(theta);
		transform.t[2][2] = 1;
		transform.t[0][0] = cosT;
		transform.t[1][1] = cosT;
		transform.t[1][0] = -sinT;
		transform.t[0][1] = sinT;
		return transform;
	}

	public Vector<double[]> apply(Vector<double[]> points) {
		Vector<double[]> ret = new Vector<double[]>();
		for(double[] point : points) {
			ret.add(apply(point));
		}
		return ret;
	}

	public double[] apply(double[] point) {
		double x = t[0][0] * point[0] + t[1][0] * point[1] + t[2][0] * point[2] + t[3][0];
		double y = t[0][1] * point[0] + t[1][1] * point[1] + t[2][1] * point[2] + t[3][1];
		double z = t[0][2] * point[0] + t[1][2] * point[1] + t[2][2] * point[2] + t[3][2];
		double[] ret = new double[3];
		ret[0] = x;
		ret[1] = y;
		ret[2] = z;
		return ret;
	}
	
	public Vector<double[]> applyNoShift(Vector<double[]> points) {
		Vector<double[]> ret = new Vector<double[]>();
		for(double[] point : points) {
			if(point == null) {
				ret.add(null);
			} else {
				ret.add(applyNoShift(point));
			}
		}
		return ret;
	}

	public double[] applyNoShift(double[] point) {
		double x = t[0][0] * point[0] + t[1][0] * point[1] + t[2][0] * point[2];
		double y = t[0][1] * point[0] + t[1][1] * point[1] + t[2][1] * point[2];
		double z = t[0][2] * point[0] + t[1][2] * point[1] + t[2][2] * point[2];
		double[] ret = new double[3];
		ret[0] = x;
		ret[1] = y;
		ret[2] = z;
		return ret;
	}
	
}
