package relations.holoc;

public class FunctionSinSqr extends Function {

	private static final long serialVersionUID = -5898701940464301738L;
	
	public double a;
	public double b;
	public double c;
	public double d;

	// y = m * sin((x + a) * b) * sin((x + c) * d)
	
	public FunctionSinSqr(double a, double b, double c, double d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}

	public Function add(Function f) throws UnhandledFunctionException {
		throw(new UnhandledFunctionException());
	}

	public double integrateAt(double mult, double x) {
		return b * d * mult * sqr(Math.sin(a * c * x + .5 * (a + c) * x * x + x * x * x / 3));
	}
	
	public double sqr(double a) {
		return a * a;
	}

	public Function multiply(Function f) throws UnhandledFunctionException {
		throw(new UnhandledFunctionException());
	}
	

}
