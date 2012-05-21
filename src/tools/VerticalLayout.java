package tools;

import java.awt.*;

public class VerticalLayout implements LayoutManager {

	public int width = 0;
	public int height = 0;

	public int vSpace;
	public int hSpace;
	
	public VerticalLayout(int hSpace, int vSpace) {
		this.vSpace = vSpace;
		this.hSpace = hSpace;
	}
	
	public void addLayoutComponent(String arg0, Component arg1) {
	}

	public void layoutContainer(Container arg0) {		
		width = arg0.getWidth();
		height = vSpace;			
		boolean inScroll = (arg0.getParent() instanceof javax.swing.JViewport);
					
		for(Component component : arg0.getComponents()) {
			int preferredHeight = component.getPreferredSize().height;
			component.setLocation(hSpace, height);
			
			if(inScroll) {
				component.setSize(width - hSpace * 2 - 15, preferredHeight);
			} else {
				component.setSize(width - hSpace * 2, preferredHeight);
			}
			
			height += preferredHeight + vSpace;
		}
	}
	
	public Dimension minimumLayoutSize(Container arg0) {
		return new Dimension(width,height);
	}

	public Dimension preferredLayoutSize(Container arg0) {
		return new Dimension(width,height);
	}

	public void removeLayoutComponent(Component arg0) {
	}

}
