package transformations;

import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class Transformation implements Cloneable {
	public JPanel panel;
	public LinkedList<ChangeListener> change = new LinkedList<ChangeListener>();
	
	public static final Transformation[] types = {
		new TransformArcCosine(), 
		new TransformCosine(), 
		new TransformInvert(), 
		new TransformMultiply(), 
		new TransformPower(), 
		new TransformShift(), 
		new TransformSigmoid(), 
	};
	
	public Transformation() {
		panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(name()));
	}
	
	public abstract String name();
	public abstract double transform(double pt);
	
	public String toString() {
		return name();
	}
	
	public abstract Transformation clone();
	
	public void changed() {
		for(ChangeListener l : change) {
			l.stateChanged(new ChangeEvent(l));
		}
	}
}
