package transformations;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

public class TransformPower extends Transformation {
	
	private double power;
	
	public TransformPower() {
		init(1);
	}
	
	public TransformPower(double val) {
		init(val);
	}
	
	private void init(double valIn) {
		power = valIn;
		
		final JTextField text = new JTextField(power + "");
		panel.add(text);
		text.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {}
			public void keyTyped(KeyEvent arg0) {}
			public void keyReleased(KeyEvent arg0) {
				try {
					power = Double.parseDouble(text.getText());
					changed();
				} catch(NumberFormatException e) {}
			}
		});
	}
	
	public String name() {return "power"; };
	
	public double transform(double pt) {
		return Math.pow(pt,power);
	}

	public Transformation clone() {
		return new TransformPower(power);
	}
}
