package relations.holoc;

import java.io.Serializable;

public abstract class Function implements Serializable {

	private static final long serialVersionUID = -8320191864815943598L;
	
	public double integrateRange(double mult, double min, double max) {
		return integrateAt(mult,max) - integrateAt(mult,min);
	}
	
	public abstract double integrateAt(double mult, double x) ;
	
	public abstract Function add(Function f) throws UnhandledFunctionException ;
	public abstract Function multiply(Function f) throws UnhandledFunctionException ;
}
