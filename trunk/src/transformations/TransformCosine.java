package transformations;

public class TransformCosine extends Transformation {
	public String name() {return "cosine"; };
	
	public double transform(double pt) {
		return Math.cos(pt);
	}

	public Transformation clone() {
		return new TransformCosine();
	}
}
