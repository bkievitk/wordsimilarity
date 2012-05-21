package relations.holoc;

import java.util.Random;

public class FunctionSin extends Function {

	private static final long serialVersionUID = -6157532233030816509L;

	public static final Random random = new Random();

	public double a;
	public double b;
	
	// y = sin((x + a) * b)
	
	public FunctionSin() {
		a = random.nextDouble() * Math.PI * 2;
		b = random.nextDouble() * Math.PI * 2;
	}

	public Function add(Function f) throws UnhandledFunctionException {
		throw(new UnhandledFunctionException());
	}

	public double integrateAt(double mult, double x) {
		return a * b * mult * Math.sin(x) + .5 * b * mult * Math.sin(x * x);
	}

	public Function multiply(Function f) throws UnhandledFunctionException {
		if(f instanceof FunctionSin) {
			FunctionSin f1 = (FunctionSin)f;
			return new FunctionSinSqr(a,b,f1.a,f1.b);
		} else {
			throw(new UnhandledFunctionException());
		}
	}

}
