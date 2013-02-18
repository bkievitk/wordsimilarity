package transformations;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

public class TransformShift extends Transformation {

	private double val;
	
	public TransformShift() {
		init(0);
	}
	
	public TransformShift(double val) {
		init(val);
	}
	
	private void init(double valIn) {
		val = valIn;
		final JTextField text = new JTextField(val + "");
		panel.add(text);
		text.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {}
			public void keyTyped(KeyEvent arg0) {}
			public void keyReleased(KeyEvent arg0) {
				try {
					val = Double.parseDouble(text.getText());
					changed();
				} catch(NumberFormatException e) {}
			}
		});
	}
	
	public String name() {return "shift"; };
	
	public double transform(double pt) {
		return pt + val;
	}

	public Transformation clone() {
		return new TransformShift(val);
	}
}
