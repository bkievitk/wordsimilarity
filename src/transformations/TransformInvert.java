package transformations;

public class TransformInvert extends Transformation {
	public String name() {return "invert"; };
	
	public double transform(double pt) {
		return 1 - pt;
	}

	public Transformation clone() {
		return new TransformInvert();
	}
}
