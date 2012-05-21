package tools;

import java.awt.*;
import javax.swing.*;

public class Stats {
	
	public static void main(String[] args) {
		
		final int[] counts = {2,0,216,266,0,0,0,0,0,0};
		JPanel panel = new JPanel() {
			private static final long serialVersionUID = -8018961435586535780L;
			public void paintComponent(Graphics g) {
				showHistogram(g, new Rectangle(20,20,400,100), counts, 0, 100, null);
			}
		};
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);
		frame.add(panel);
		frame.setVisible(true);
	}
	
	public static double[] showGraph(Graphics g, Rectangle r, double[] xs, double[] ys) {
		double xMax = Double.MIN_VALUE;
		double xMin = Double.MAX_VALUE;
		for(double x : xs) {
			xMax = Math.max(x, xMax);
			xMin = Math.min(x, xMin);
		}
		
		double yMax = Double.MIN_VALUE;
		double yMin = Double.MAX_VALUE;
		for(double y : ys) {
			yMax = Math.max(y, yMax);
			yMin = Math.min(y, yMin);
		}

		double[] ranges = {xMin,xMax,yMin,yMax};
		return showGraph(g, r, xs, ys, ranges);
	}
	
	public static double[] showGraph(Graphics g, Rectangle r, double[] xs, double[] ys, double[] ranges) {
		double xMin = ranges[0];
		double xMax = ranges[1];
		double yMin = ranges[2];
		double yMax = ranges[3];
		
		for(int i=0;i<xs.length;i++) {
			int x = (int)((xs[i] - xMin) * r.width / (xMax - xMin) + r.x);
			int y = (int)(r.height - (ys[i] - yMin) * r.height / (yMax - yMin) + r.y);
			g.drawOval(x - 2, y - 2, 4, 4);
			
			if(i > 0) {
				int xOld = (int)((xs[i-1] - xMin) * r.width / (xMax - xMin) + r.x);
				int yOld = (int)(r.height - (ys[i-1] - yMin) * r.height / (yMax - yMin) + r.y);
				g.drawLine(x, y, xOld, yOld);
			}
		}
		
		double[] ret = {xMin,xMax,yMin,yMax};
		return ret;
	}
	
	public static boolean showHistogram(Graphics g, Rectangle r, int[] counts, int min, int max, String[] labels) {
		
		int maxCount = 0;
		for(int count : counts) {
			maxCount = Math.max(count, maxCount);
		}
		
		if(maxCount == 0) {
			return false;
		}
		
		String show = (4 * maxCount / 5) + "";
		int width = g.getFontMetrics().stringWidth(show);
		Rectangle inner = new Rectangle(r.x + width + 10, r.y, r.width - 20, r.height - 20);
		
		for(int i=0;i<counts.length;i++) {
			
			int x = inner.width * i / counts.length + inner.x;
			width = inner.width / counts.length;
			int height = (counts[i] * inner.height / maxCount);
			int y = inner.y + inner.height - height;
			
			g.setColor(Color.GRAY);
			g.fillRect(x, y, width, height);
			
			g.setColor(Color.BLACK);
			g.drawRect(x, y, width, height);
			
			if(labels == null) {
				int binStart = i * (max - min) / counts.length + min;
				int binStop = binStart + (max - min) / counts.length - 1;
				g.drawString(binStart + "-" + binStop, x, inner.y + inner.height + 17);
			} else {
				g.drawString(labels[i], x, inner.y + inner.height + 17);
			}
		}

		g.setColor(Color.BLACK);
		for(int i=0;i<5;i++) {
			show = (i * maxCount / 5) + "";
			width = g.getFontMetrics().stringWidth(show);
			g.drawString(show, inner.x - width - 5, inner.y + inner.height - inner.height * i / 5);
		}
		
		g.drawRect(r.x, r.y, r.width-1, r.height-1);
		
		return true;
	}
}
