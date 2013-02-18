package transformations;

public class TransformArcCosine extends Transformation {
	
	public String name() {return "arc cosine"; };
	
	public double transform(double pt) {
		return Math.acos(pt);
	}

	public Transformation clone() {
		return new TransformArcCosine();
	}
}
