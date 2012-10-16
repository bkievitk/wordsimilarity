package gui;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import relations.wordnet.WordRelatorWordNet;
import tools.*;
import wizard.Wizard;

public class MainGUIExternal extends MainGUI {

	private static final long serialVersionUID = -8985532147878099642L;
	
	// Frames.
	public JFrame wizardFrame = new JFrame("Wizard");
	public JFrame wordFrame = new JFrame("Word");
	public JProgressBar progress = new JProgressBar();
	public JFrame renderingProgress = new JFrame("Rendering Progress");
	public JFrame networkTools = new JFrame("Network Tools");
	public JFrame showOptions = new JFrame("Options");
	public JFrame mainFrame = new JFrame();
	public JFrame tutorial = new JFrame();
		
	// Send messages when the visualization changes.
	private Vector<ChangeListener> visualizationChanged = new Vector<ChangeListener>();
		
	/**
	 * Simply start up the main GUI.
	 * @param args
	 */
	public static void main(String[] args) {
		new MainGUIExternal(false);		
	}
	
	/**
	 * Listen for visualization change.
	 * @param listener
	 */
	public void addVisualizationChangeListener(ChangeListener listener) {
		visualizationChanged.add(listener);
	}
	
	/**
	 * Fire this when the visualization has changed.
	 */
	public void visualizationChanged() {
		ChangeEvent e = new ChangeEvent(this);
		for(ChangeListener change : visualizationChanged) {
			change.stateChanged(e);
		}
	}
	
	/**
	 * Build the external JFrames that you can open.
	 */
	public void buildFrames() {
		
		// Relation wizard.
		wizardFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		wizardFrame.setSize(500,800);
		wizardFrame.add(new Wizard(wordMap,wizardFrame));
		wizardFrame.setVisible(false);
		
		// Word manager.
		wordFrame.setSize(500,500);
		wordFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		wordFrame.add(new WordGUI(wordMap));
		wordFrame.setVisible(false);
		
		// Progress bar.
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(progress,BorderLayout.SOUTH);
		panel.add(PanelTools.wrappingText("Rendering progress."),BorderLayout.CENTER);
		renderingProgress.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		renderingProgress.setSize(200,80);
		renderingProgress.add(panel);
			
		// Network tools.
		networkTools.setSize(300,500);
		networkTools.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		networkTools.add(new NetworkTools(wordMap, visualizationPanel));
		networkTools.setVisible(false);

		// General options.
		showOptions.setSize(300,500);
		showOptions.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		showOptions.add(new ShowOptions(options, this));
		showOptions.setVisible(false);
				
		// Main panel.
		mainFrame.setSize(800,700);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.add(this);
		
		tutorial = new JFrame("Tutorial");
		tutorial.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		tutorial.setSize(800,600);
		tutorial.add(new JScrollPane(new Tutorial()));
	}
	
	public MainGUIExternal(boolean startSetup) {	

		// Build startups.
		wordMap = new WordMap();
		visualizationPanel = new Visualization(wordMap,options,progress);	
		
		if(startSetup) {
			// Load word net.
			WordRelatorWordNet.loadWordNet(wordMap);
		}
		
		// When the visualization changes, update it.
		visualizationChanged.add(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				visualizationPanel.imageChanged = true;
				visualizationPanel.repaint();
			}			
		});

		// These are the external frames.
		buildFrames();

		// When the words or relations changed, the visualization changes.
		wordMap.wordChange.add(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				visualizationChanged();
			}
		});
		
		wordMap.relationChange.add(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				visualizationChanged();
			}
		});
			
		// Actually place main panels.
		setLayout(new BorderLayout());
		
		// Add metrics and legend.
		JPanel rightSide = new JPanel(new BorderLayout());
		add(rightSide,BorderLayout.EAST);
		JScrollPane scroll = new JScrollPane(new PanelSelector(wordMap,240),JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		rightSide.add(scroll,BorderLayout.CENTER);
		rightSide.add(new LegendGUI(options),BorderLayout.SOUTH);
		add(visualizationPanel,BorderLayout.CENTER);
				
		if(startSetup) {
			Object[] options = {"No words",
	                "TASA PoS",
	                "TASA Counts and PoS",
	                "Select word file",
	                "Download word data"};
			int n = JOptionPane.showOptionDialog(null,
				"Select initial word set. These will go in the 'full' word set, but not the 'active' word set.",
				"Word Set",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[0]);
	
			if(n != 0) {
				// Load default word set.
				try {
					BufferedReader r;
					
					if(n == 3) {
						JFileChooser chooser = new JFileChooser(new File("."));
											
						// Binary.
						javax.swing.filechooser.FileFilter wrd = new javax.swing.filechooser.FileFilter() {
							public boolean accept(File arg0) {	return arg0.getName().endsWith(".wrd") || arg0.isDirectory();	}
							public String getDescription() {	return "Word File (.wrd)";	}							
						};
											
						chooser.setAcceptAllFileFilterUsed(false);		
						chooser.addChoosableFileFilter(wrd);						
						chooser.showOpenDialog(null);
						
						r = new BufferedReader(new FileReader(chooser.getSelectedFile()));
					}if(n == 4) {
						r = new BufferedReader(new InputStreamReader((new URL("http://www.indiana.edu/~semantic/word2word/tasaWords.wrd")).openStream()));
						String line;
						BufferedWriter w = new BufferedWriter(new FileWriter(new File("tasaWords.wrd")));
						while((line = r.readLine()) != null) {
							w.write(line + "\n");
						}
						w.close();
						r.close();
						r = new BufferedReader(new FileReader(new File("tasaWords.wrd")));
					} else {
						r = new BufferedReader(new FileReader(new File("tasaWords.wrd")));
					}
					
					// First line is just the header.
					String line = r.readLine();
					
					while((line = r.readLine()) != null) {
						String[] parts = line.split(",");
						
						// Must have word, count and pos
						if(parts.length == 3) {
							
							// Extract parts.
							String word = parts[0];
							Integer count = Integer.parseInt(parts[1]);
							Integer pos = Integer.parseInt(parts[2]);
							
							// Only use frequent words.
							// This word set should already only contain these words.
							if(count > 1000) {
								WordNode node = wordMap.setWordStatus(word,false);
								if(n == 1) {
									node.setCount(0);
								} else {
									node.setCount(count);
								}
								node.setPoS(pos);
							}
						}
					}
					r.close();
					
					// We have added words, now show update.
					wordMap.wordChanged();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		// Build Menu.
		JMenuBar menu = AdvancedMenu.buildMenu(this);
		mainFrame.setJMenuBar(menu);
		mainFrame.setVisible(true);	
		
		if(startSetup) {
			tutorial.setVisible(true);
		}
	}
	
	public void showTutorial() {
		tutorial.setVisible(true);
	}
	
	public void showWordFrame() {
		wordFrame.setVisible(true);
	}
	
	public void showNetworkTools() {
		networkTools.setVisible(true);
	}
	
	public void showOptions() {
		showOptions.setVisible(true);
	}
	
	public void showWizardFrame() {
		wizardFrame.setVisible(true);
	}
	
	public void showRenderingProgress() {
		renderingProgress.setVisible(true);
	}
}
