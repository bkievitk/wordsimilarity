package gui;

import java.awt.Color;
import java.util.Vector;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Options {
	private Color backgroundColor = Color.WHITE;
	private Color highlightedConnectionColor = Color.RED;
	public Color nodeColor = Color.BLACK;
	
	// Node size scalling.
	public static final int SCALLING_CONSTANT = 0;
	public static final int SCALLING_LINEAR = 1;
	public static final int SCALLING_ROOT = 2;
	public static final int SCALLING_LOG = 3;
	private int scallingType = SCALLING_CONSTANT;
	private int scallingFactor = 100;
	
	// Node coloring.
	public static final int COLORING_CONSTANT = 0;
	public static final int COLORING_POS = 1;
	public int coloringType = COLORING_POS;
	
	// Word labeling.
	public static final int LABEL_NONE = 0;
	public static final int LABEL_TEXT = 1;
	public static final int LABEL_FULL = 2;
	public int labelType = LABEL_TEXT;

	// Label type.
	public int biDirectionalType = BI_DIRECTIONAL_ARROW;
	public static final int BI_DIRECTIONAL_NONE = 0;
	public static final int BI_DIRECTIONAL_ARROW = 1;
	public static final int BI_DIRECTIONAL_TOP_BOTTOM = 2;
	public static final int BI_DIRECTIONAL_ARROW_MID = 3;
	public static final int BI_DIRECTIONAL_DOT_END = 4;
	public static final int BI_DIRECTIONAL_SPIKE = 5;
	
	// When options change.
	public Vector<ChangeListener> changeListeners = new Vector<ChangeListener>();

	public Color getNodeColor() {
		return nodeColor;
	}

	public void setNodeColor(Color color) {
		nodeColor = color;
		change();
	}
	
	public Color getHighlightedConnectionColor() {
		return highlightedConnectionColor;
	}

	public void setHighlightedConnectionColor(Color color) {
		highlightedConnectionColor = color;
		change();
	}
	
	public Color getBackgroundColor() {
		return backgroundColor;
	}
	
	public void setBackgroundColor(Color color) {
		backgroundColor = color;
		change();
	}
	
	public void change() {
		ChangeEvent e = new ChangeEvent(this);
		for(ChangeListener c : changeListeners) {
			c.stateChanged(e);
		}
	}

	public void setScallingType(int type) {
		scallingType = type;
		change();
	}
	
	public void setScallingFactor(int factor) {
		scallingFactor = factor;
		change();
	}
	
	public int getScallingType() {
		return scallingType;
	}
	
	public int getScallingFactor() {
		return scallingFactor;
	}
}
