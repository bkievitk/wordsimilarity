package transformations;

public class TransformSigmoid extends Transformation {
	public String name() {return "sigmoid"; };
	
	public double transform(double pt) {
		return 1 / (1 + Math.pow(Math.E,-pt));
	}

	public Transformation clone() {
		return new TransformSigmoid();
	}
}
