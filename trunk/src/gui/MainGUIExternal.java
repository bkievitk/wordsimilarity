package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.URL;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import cluster.Clustering;

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
	public JFrame clustering = new JFrame("Clustering");
	public JFrame mainFrame = new JFrame();
	public JFrame tutorial = new JFrame();

	private static int slide = 0;
	
	// Send messages when the visualization changes.
	private Vector<ChangeListener> visualizationChanged = new Vector<ChangeListener>();
		

	private static Robot r;
	
	static {
		try {
			r = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Simply start up the main GUI.
	 * @param args
	 */
	public static void main(String[] args) {
		
		JFrame console = new JFrame();
		console.setSize(400, 200);
		console.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JTextArea text = new JTextArea();
		System.setOut(new PrintStream(new TextAreaOutputStream(text,"message")));
		System.setErr(new PrintStream(new TextAreaOutputStream(text,"error")));
		console.add(new JScrollPane(text));
		console.setVisible(true);
		
		final MainGUIExternal gui = new MainGUIExternal(false);	
		
		/*
		JPanel remotePanel = new JPanel();
		
		final JLabel label = new JLabel("   Learn Obama state of the union.");
		
		
		JButton next = new JButton("next");
		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				switch(slide) {
						
					case 0:
						new Thread() {
							public void run() {
								gui.wizardFrame.requestFocus();	
								learnBEAGLE(gui, "obama", "presentation/obama.txt", true); 
							}
						}.start();							
						label.setText("   Learn Bush state of the union.");
					break;
					
					case 1:
						new Thread() {
							public void run() {
								gui.wizardFrame.requestFocus();	
								learnBEAGLE(gui, "bush", "presentation/bush.txt", false); 
							}
						}.start();	
						label.setText("   Select word.");						
					break;
					
					case 2:
						
						new Thread() {
							public void run() {
								gui.requestFocus();	

								doTypeTimes(1, KeyEvent.VK_ALT, KeyEvent.VK_T);
								r.delay(500);
								
								doTypeTimes(1, KeyEvent.VK_ALT, KeyEvent.VK_W);
								r.delay(500);
		
								doTypeTimes(4, KeyEvent.VK_TAB);
														
								doTypeTimesP(4, 5000, KeyEvent.VK_RIGHT);
								doTypeTimesP(1, 5000, KeyEvent.VK_UP);
								doTypeTimesP(3, 5000, KeyEvent.VK_RIGHT);
								doTypeTimesP(1, 5000, KeyEvent.VK_UP);
								
								doTypeTimes(1, KeyEvent.VK_DOWN);
								doTypeTimes(1, KeyEvent.VK_RIGHT);
		
								r.delay(5000);
								
								doTypeTimes(3, KeyEvent.VK_TAB);
								doTypeTimes(3, KeyEvent.VK_DELETE);
		
								doTypeTimes(1, KeyEvent.VK_5);
								doTypeTimes(1, KeyEvent.VK_0);
								
								r.delay(5000);
								
								doTypeTimes(2, KeyEvent.VK_TAB);
								doTypeTimes(1, KeyEvent.VK_SPACE);
							}
						}.start();	
						label.setText("   Layout.");	
					break;
					
					case 3:
						
						new Thread() {
							public void run() {
								gui.requestFocus();	
								
								doTypeTimes(1, KeyEvent.VK_ALT, KeyEvent.VK_L);
								r.delay(500);
								
								doTypeTimes(1, KeyEvent.VK_ALT, KeyEvent.VK_S);
								r.delay(500);

								doTypeTimes(1, KeyEvent.VK_TAB);
								r.delay(500);
								doTypeTimes(1, KeyEvent.VK_SPACE);
								
								doTypeTimes(2, KeyEvent.VK_DOWN);
								r.delay(500);
								doTypeTimes(1, KeyEvent.VK_ENTER);

								doTypeTimes(1, KeyEvent.VK_TAB);
								doTypeTimes(1, KeyEvent.VK_SPACE);
							}
						}.start();	
						label.setText("   Layout.");
						
					break;
				}
				slide++;
			}
		});
		
		remotePanel.setLayout(new BorderLayout());
		remotePanel.add(next, BorderLayout.WEST);
		remotePanel.add(label, BorderLayout.CENTER);
		JFrame remote = new JFrame();
		remote.setSize(300, 80);
		remote.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		remote.add(remotePanel);
		remote.setVisible(true);
		*/
	}
	
	public static void learnBEAGLE(MainGUIExternal gui, String name, String file, boolean wait) {
		
		gui.requestFocus();	

		doTypeTimes(1, KeyEvent.VK_ALT, KeyEvent.VK_T);
		r.delay(500);
		
		doTypeTimes(1, KeyEvent.VK_ALT, KeyEvent.VK_S);
		r.delay(500);
		
		gui.wizardFrame.requestFocus();	
		
		// Beagle.
		doTypeTimes(2, KeyEvent.VK_TAB);
		doTypeTimes(1, KeyEvent.VK_SPACE);
		
		// Select wizard.
		doTypeTimes(25, KeyEvent.VK_TAB);
		if(wait) { r.delay(5000); }
		doTypeTimes(1, KeyEvent.VK_SPACE);
		
		// Write dimensions.
		doTypeTimes(3, KeyEvent.VK_TAB);
		doTypeTimes(1, KeyEvent.VK_2);
		doTypeTimes(2, KeyEvent.VK_0);
		 
		// Commit.
		doTypeTimes(1, KeyEvent.VK_SHIFT, KeyEvent.VK_TAB);	
		doTypeTimes(1, KeyEvent.VK_SPACE);	
		
		// Confirm.
		doTypeTimes(3, KeyEvent.VK_TAB);
		if(wait) { r.delay(10000); }
		doTypeTimes(1, KeyEvent.VK_SPACE);

		// Type name
		doTypeTimes(5, KeyEvent.VK_TAB);
		type(name);

		// Set default filters.
		doTypeTimes(4, KeyEvent.VK_TAB);
		doTypeTimes(1, KeyEvent.VK_SPACE);

		// Next screen.
		doTypeTimes(19, KeyEvent.VK_TAB);		
		if(wait) { r.delay(10000); }		
		doTypeTimes(1, KeyEvent.VK_SPACE);

		// Learn.
		doTypeTimes(6, KeyEvent.VK_TAB);
		doTypeTimes(1, KeyEvent.VK_SPACE);
		
		type(file);
		
		// Learn data
		doTypeTimes(2, KeyEvent.VK_TAB);
		doTypeTimes(1, KeyEvent.VK_SPACE);
	}

	public static void key(Robot r, String key) {
		for(int i=0;i<key.length();i++) {
			r.keyPress(key.charAt(i));  
			r.delay(50); 
			r.keyRelease(key.charAt(i));  
			r.delay(50);
		}
	}
	
	public static void key(Robot r, int key, int times) {
		for(int i=0;i<times;i++) {
			r.keyPress(key);  
			r.delay(50); 
			r.keyRelease(key);  
			r.delay(50);
		}
	}
	
	public static void key(Robot r, int key1, int key2, int times) {
		for(int i=0;i<times;i++) {
			r.keyPress(key1);  
			r.keyPress(key2);  
			r.delay(50); 
			r.keyRelease(key2);  
			r.keyRelease(key1);  
			r.delay(50);
		}
	}	
	public static void type(String characters) {
		for(int i=0;i<characters.length();i++) {
			type(characters.charAt(i));
		}
	}
	
    public static void type(char character) {
    	switch (character) {
    	case 'a': doType(KeyEvent.VK_A); break;
    	case 'b': doType(KeyEvent.VK_B); break;
    	case 'c': doType(KeyEvent.VK_C); break;
    	case 'd': doType(KeyEvent.VK_D); break;
    	case 'e': doType(KeyEvent.VK_E); break;
    	case 'f': doType(KeyEvent.VK_F); break;
    	case 'g': doType(KeyEvent.VK_G); break;
    	case 'h': doType(KeyEvent.VK_H); break;
    	case 'i': doType(KeyEvent.VK_I); break;
    	case 'j': doType(KeyEvent.VK_J); break;
    	case 'k': doType(KeyEvent.VK_K); break;
    	case 'l': doType(KeyEvent.VK_L); break;
    	case 'm': doType(KeyEvent.VK_M); break;
    	case 'n': doType(KeyEvent.VK_N); break;
    	case 'o': doType(KeyEvent.VK_O); break;
    	case 'p': doType(KeyEvent.VK_P); break;
    	case 'q': doType(KeyEvent.VK_Q); break;
    	case 'r': doType(KeyEvent.VK_R); break;
    	case 's': doType(KeyEvent.VK_S); break;
    	case 't': doType(KeyEvent.VK_T); break;
    	case 'u': doType(KeyEvent.VK_U); break;
    	case 'v': doType(KeyEvent.VK_V); break;
    	case 'w': doType(KeyEvent.VK_W); break;
    	case 'x': doType(KeyEvent.VK_X); break;
    	case 'y': doType(KeyEvent.VK_Y); break;
    	case 'z': doType(KeyEvent.VK_Z); break;
    	case 'A': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_A); break;
    	case 'B': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_B); break;
    	case 'C': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_C); break;
    	case 'D': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_D); break;
    	case 'E': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_E); break;
    	case 'F': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_F); break;
    	case 'G': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_G); break;
    	case 'H': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_H); break;
    	case 'I': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_I); break;
    	case 'J': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_J); break;
    	case 'K': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_K); break;
    	case 'L': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_L); break;
    	case 'M': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_M); break;
    	case 'N': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_N); break;
    	case 'O': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_O); break;
    	case 'P': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_P); break;
    	case 'Q': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Q); break;
    	case 'R': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_R); break;
    	case 'S': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_S); break;
    	case 'T': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_T); break;
    	case 'U': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_U); break;
    	case 'V': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_V); break;
    	case 'W': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_W); break;
    	case 'X': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_X); break;
    	case 'Y': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Y); break;
    	case 'Z': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Z); break;
    	case '`': doType(KeyEvent.VK_BACK_QUOTE); break;
    	case '0': doType(KeyEvent.VK_0); break;
    	case '1': doType(KeyEvent.VK_1); break;
    	case '2': doType(KeyEvent.VK_2); break;
    	case '3': doType(KeyEvent.VK_3); break;
    	case '4': doType(KeyEvent.VK_4); break;
    	case '5': doType(KeyEvent.VK_5); break;
    	case '6': doType(KeyEvent.VK_6); break;
    	case '7': doType(KeyEvent.VK_7); break;
    	case '8': doType(KeyEvent.VK_8); break;
    	case '9': doType(KeyEvent.VK_9); break;
    	case ' ': doType(KeyEvent.VK_SPACE); break;
    	case '/': doType(KeyEvent.VK_SLASH); break;
    	case '(': doType(KeyEvent.VK_LEFT_PARENTHESIS); break;
    	case ')': doType(KeyEvent.VK_RIGHT_PARENTHESIS); break;
    	case '.': doType(KeyEvent.VK_PERIOD); break;
    	
    	default:
    		throw new IllegalArgumentException("Cannot type character " + character);
    	}
    }

    private static void doType(int... keyCodes) {
    	doType(keyCodes, 0, keyCodes.length);
    }

    private static void doType(int[] keyCodes, int offset, int length) {
    	if (length == 0) {
    		return;
    	}

    	r.keyPress(keyCodes[offset]);
    	r.delay(50); 
    	
    	doType(keyCodes, offset + 1, length - 1);
    	r.keyRelease(keyCodes[offset]);
    	r.delay(50); 
    }
    
    private static void doTypeTimes(int times, int... keyCodes) {
    	for(int i=0;i<times;i++) {
    		doType(keyCodes, 0, keyCodes.length);
    	}
    }
    
    private static void doTypeTimesP(int times, int pause, int... keyCodes) {
    	for(int i=0;i<times;i++) {
    		doType(keyCodes, 0, keyCodes.length);
    		r.delay(pause);
    	}
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
		
		clustering = new JFrame("Clustering");
		clustering.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		clustering.setSize(800,600);
		clustering.add(new Clustering(wordMap, options));
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
		
		//visualizationPanel.setPreferredSize(new Dimension(3300,2550));
		add(new JScrollPane(visualizationPanel),BorderLayout.CENTER);
				
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

	@Override
	public void showClustering() {
		clustering.setVisible(true);
	}
}
