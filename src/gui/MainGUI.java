package gui;

import java.util.Random;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public abstract class MainGUI extends JPanel implements VisualizationInterface {

	private static final long serialVersionUID = -8985532147878099642L;
	public static final Random rand = new Random();
	
	// Words.
	public WordMap wordMap;

	// GUI References.
	public Visualization visualizationPanel;	
	public Options options = new Options();
	public JProgressBar progress = new JProgressBar();

	public abstract void showTutorial();
	public abstract void showWordFrame();
	public abstract void showNetworkTools();
	public abstract void showOptions();
	public abstract void showWizardFrame();
	public abstract void showRenderingProgress();
	public abstract void showClustering();
}
