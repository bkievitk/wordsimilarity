package gui;

import javax.swing.JApplet;

public class MainGUIApplet extends JApplet {
	private static final long serialVersionUID = 8193184307058676964L;
	
	public void init() {
		MainGUIInternal gui = new MainGUIInternal(false);
		add(gui.desktop);
	}

}
