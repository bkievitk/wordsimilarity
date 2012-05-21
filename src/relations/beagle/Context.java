package relations.beagle;

public class Context {
	public double[] indicator;
	
	public Context(int dimensions) {
		indicator = VectorTools.newIndicator(dimensions);
	}
}
