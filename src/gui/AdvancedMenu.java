package gui;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import relations.WordRelator;
import tools.FlickrImage;
import tools.GoogleImage;
import tools.WeightedObject;

/**
 * Make menus.
 * @author bkievitk
 */

public class AdvancedMenu {
	
	/**
	 * Replace current extension with another.
	 * @param f
	 * @param newExtension
	 * @return
	 */
	public static File replaceFileExtension(File f, String newExtension) {
		
		// Find current extension.
		String path = f.getAbsolutePath();
		int sep = path.lastIndexOf(File.separatorChar);
		int dot = path.lastIndexOf('.');
		
		if(dot > sep) {
			return new File(path.substring(0,dot) + "." + newExtension);
		} else {
			return new File(path + "." + newExtension);
		}
	}
		
	/**
	 * Build the main menu bar.
	 * @return
	 */
	public static JMenuBar buildMenu(final MainGUI main) {				
		// Menu bar.
		JMenuBar menu = new JMenuBar();
		
		// Add each sub-menu.
		menu.add(buildFileMenu(main.wordMap, main));
		menu.add(buildToolsMenu(main));
		menu.add(buildRenderMenu(main));
		menu.add(buildLayoutMenu(main.wordMap, main));
		menu.add(buildConnectionMenu(main));	
		menu.add(buildHelpMenu(main));	
		return menu;
	}
	
	
	/**
	 * File controls IO.
	 * @param wordMap
	 * @return
	 */
	public static JMenu buildFileMenu(final WordMap wordMap, final MainGUI main) {

		JMenu file = new JMenu("File");
		
			// Simply clear all relations and words.
			JMenuItem newMenu = new JMenuItem("New");
				newMenu.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						
						// Clear.
						wordMap.clear();
						
						// Show updates.
						wordMap.wordChanged();
						wordMap.relationChanged();
					}
				});
			file.add(newMenu);
						
			// Seperator.
			file.addSeparator();
			
			// Save worspace.
			JMenuItem save = new JMenuItem("Save");
				save.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						
						// File output types.	
						
						// Binary.
						javax.swing.filechooser.FileFilter full = new javax.swing.filechooser.FileFilter() {
							public boolean accept(File arg0) {	return arg0.getName().endsWith(".w2w") || arg0.isDirectory();	}
							public String getDescription() {	return "Binary (.w2w)";	}							
						};

						// CSV Matrix.
						javax.swing.filechooser.FileFilter csv = new javax.swing.filechooser.FileFilter() {
							public boolean accept(File arg0) {	return arg0.getName().endsWith(".csv") || arg0.isDirectory();	}
							public String getDescription() {	return "Comma Seperated Matrix File (.csv)";	}							
						};
						
						// CSV pair file.
						javax.swing.filechooser.FileFilter wordPairs = new javax.swing.filechooser.FileFilter() {
							public boolean accept(File arg0) {	return arg0.getName().endsWith(".csv") || arg0.isDirectory();	}
							public String getDescription() {	return "Comma Seperated Word Pair File (.csv)";	}							
						};

						// CSV word list.
						javax.swing.filechooser.FileFilter wordList = new javax.swing.filechooser.FileFilter() {
							public boolean accept(File arg0) {	return arg0.getName().endsWith(".wrd") || arg0.isDirectory();	}
							public String getDescription() {	return "Word List (.csv)";	}							
						};
						
						// compressed word list.
						javax.swing.filechooser.FileFilter compressedList = new javax.swing.filechooser.FileFilter() {
							public boolean accept(File arg0) {	return arg0.getName().endsWith(".cmp") || arg0.isDirectory();	}
							public String getDescription() {	return "Compressed (.cmp)";	}							
						};

						// Create file chooser.
						final javax.swing.filechooser.FileFilter[] selectedFilter = new javax.swing.filechooser.FileFilter[1];
						JFileChooser chooser = new JFileChooser(new File("."));
						chooser.addPropertyChangeListener(new PropertyChangeListener() {
							public void propertyChange(PropertyChangeEvent arg0) {
								if(arg0.getPropertyName() == JFileChooser.FILE_FILTER_CHANGED_PROPERTY) {
									selectedFilter[0] = (javax.swing.filechooser.FileFilter)arg0.getNewValue();
								}
							}
						});

						// Add filters.
						chooser.setAcceptAllFileFilterUsed(false);		
						chooser.addChoosableFileFilter(full);		
						chooser.addChoosableFileFilter(csv);			
						chooser.addChoosableFileFilter(wordPairs);		
						chooser.addChoosableFileFilter(wordList);	
						chooser.addChoosableFileFilter(compressedList);

						// On complete.
						if(chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
							try {
								File file = chooser.getSelectedFile();	

								// Check which filter was used.
								if(selectedFilter[0] == full) {
									file = replaceFileExtension(file,"w2w");
								} else if(selectedFilter[0] == csv) {
									file = replaceFileExtension(file,"csv");
								} else if(selectedFilter[0] == wordPairs) {
									file = replaceFileExtension(file,"csv");
								} else if(selectedFilter[0] == wordList) {
									file = replaceFileExtension(file,"wrd");
								} else if(selectedFilter[0] == compressedList) {
									file = replaceFileExtension(file,"cmp");
								}
																
								// Check if file already exists.
								if(file.exists()) {
									int n = JOptionPane.showConfirmDialog(
									    null,
									    "Overwrite file?",
									    "File already exists.",
									    JOptionPane.YES_NO_OPTION);
									
									if(n == JOptionPane.NO_OPTION) {
										System.out.println("Save canceled.");
										return;
									}
								}
								
								// Check which filter was used.
								if(selectedFilter[0] == full) {									
									// A full data dump.
									IO.saveWorkspace(wordMap, new FileOutputStream(file));									
								} else if(selectedFilter[0] == csv) {									
									// Blocks comparators into matricies.
									IO.saveCSV(wordMap, new FileOutputStream(file));								
								} else if(selectedFilter[0] == wordPairs) {
									// Blocks data into word pairs.
									// For stats.
									IO.savePairs(wordMap, new FileOutputStream(file));	
								} else if(selectedFilter[0] == wordList) {
									// List of all words.
									IO.saveWordList(wordMap, new FileOutputStream(file));									
								} else if(selectedFilter[0] == compressedList) {									
									// List of all words.
									IO.saveCMPFrame(wordMap, new FileOutputStream(file));
								}
								
							} catch (IOException e) {
								e.printStackTrace();
							} catch (NumberFormatException e) {
								e.printStackTrace();
							}
						}
					}
				});
			file.add(save);
			
			final javax.swing.filechooser.FileFilter fullFilter = new javax.swing.filechooser.FileFilter() {
				public boolean accept(File arg0) {	return arg0.getName().endsWith(".w2w") || arg0.isDirectory();	}
				public String getDescription() {	return "Binary (.w2w)";	}							
			};

			final javax.swing.filechooser.FileFilter csvFilter = new javax.swing.filechooser.FileFilter() {
				public boolean accept(File arg0) {	return arg0.getName().endsWith(".csv") || arg0.isDirectory();	}
				public String getDescription() {	return "Comma Seperated Matrix File (.csv)";	}							
			};

			final javax.swing.filechooser.FileFilter imagesFilter = new javax.swing.filechooser.FileFilter() {
				public boolean accept(File arg0) {	return arg0.isDirectory();	}
				public String getDescription() {	return "Image Directory (dir)";	}							
			};

			final javax.swing.filechooser.FileFilter compressedFilter = new javax.swing.filechooser.FileFilter() {
				public boolean accept(File arg0) {	return arg0.getName().endsWith(".cmp") || arg0.isDirectory();	}
				public String getDescription() {	return "Compressed (.cmp)";	}	
			};
			
			final javax.swing.filechooser.FileFilter wordSimilarityFilter = new javax.swing.filechooser.FileFilter() {
				public boolean accept(File arg0) {	return arg0.getName().endsWith(".csv") || arg0.isDirectory();	}
				public String getDescription() {	return "Word Similarity (.csv)";	}	
			};

			// Create file chooser.
			final javax.swing.filechooser.FileFilter[] selectedFilter = new javax.swing.filechooser.FileFilter[1];
			final JFileChooser chooser = new JFileChooser(new File("."));
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			chooser.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent arg0) {
					if(arg0.getPropertyName() == JFileChooser.FILE_FILTER_CHANGED_PROPERTY) {
						selectedFilter[0] = (javax.swing.filechooser.FileFilter)arg0.getNewValue();
					}
				}
			});

			// Add filters.
			chooser.setAcceptAllFileFilterUsed(false);		
			chooser.addChoosableFileFilter(fullFilter);		
			chooser.addChoosableFileFilter(csvFilter);		
			chooser.addChoosableFileFilter(imagesFilter);		
			chooser.addChoosableFileFilter(wordSimilarityFilter);	
			chooser.addChoosableFileFilter(compressedFilter);	
			
			// Load menu.
			JMenuItem load = new JMenuItem("Load");
				load.addActionListener(new ActionListener() {					
					public void actionPerformed(ActionEvent arg0) {
						
						// On complete.
						if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
							try {
								File file = chooser.getSelectedFile();			
								if(selectedFilter[0] == fullFilter) {
									IO.loadWorkSpace(wordMap, new BufferedInputStream(new FileInputStream(file)));				
								} else if(selectedFilter[0] == csvFilter) {
									IO.loadCSV(wordMap, new BufferedReader(new FileReader(file)));
								} else if(selectedFilter[0] == imagesFilter) {
									IO.loadImages(wordMap, file);
									main.visualizationChanged();
								} else if(selectedFilter[0] == compressedFilter) {
									IO.loadCMP(wordMap, new BufferedInputStream(new FileInputStream(file)));
								} else if(selectedFilter[0] == wordSimilarityFilter) {
									IO.loadWordSimilarity(wordMap, file);
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				});
			file.add(load);	
			
			JMenuItem loadWeb = new JMenuItem("Load Web");
			loadWeb.addActionListener(new ActionListener() {
				@SuppressWarnings("unchecked")
				public void actionPerformed(ActionEvent arg0) {
					try {
						BufferedReader r = new BufferedReader(new InputStreamReader((new URL("http://www.indiana.edu/~semantic/word2word/listFiles.php")).openStream()));
						String line;
						
						Vector<WeightedObject<String>> sizes = new Vector<WeightedObject<String>>();
						while((line = r.readLine()) != null) {
							String[] parts =line.split(";");
							String file = parts[0];
							int size = Integer.parseInt(parts[1]);
							sizes.add(new WeightedObject<String>(file, size));
						}
						
						WeightedObject<String> s = (WeightedObject<String>)JOptionPane.showInputDialog(
						                    null,
						                    "Select file to download.",
						                    "Web Files",
						                    JOptionPane.PLAIN_MESSAGE,
						                    null,
						                    sizes.toArray(),
						                    "ham");

						//If a string was returned, say so.
						if(s != null) {
							IO.loadCMP(wordMap, new BufferedInputStream(new FileInputStream("IEP.cmp")));
						}
						
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			file.add(loadWeb);	
			

			file.addSeparator();
			
			// Take screenshot.
			JMenuItem screen = new JMenuItem("Screen Shot");
			screen.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					
					JFileChooser chooser = new JFileChooser(new File("."));
					chooser.showSaveDialog(null);
					File file = chooser.getSelectedFile();
					try {
						ImageIO.write(main.visualizationPanel.getImage(), "png", file);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			file.add(screen);

			// Seperator.
			file.addSeparator();
			
			// Load images.
			JMenu images = new JMenu("Images");
			file.add(images);
			
			JMenuItem imagesGoogle = new JMenuItem("Google");
			imagesGoogle.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					//Custom button text
					Object[] options = {"No", "Yes"};
					int n = JOptionPane.showOptionDialog(null,
					    "This will load an image from Google for each active word, into a directory of your choosing.\nYou may then load the images for the nodes using File->Load->Image Directory.\nThis action may take a while. Are you sure you would like to continue?",
					    "Continue",
					    JOptionPane.YES_NO_CANCEL_OPTION,
					    JOptionPane.QUESTION_MESSAGE,
					    null,
					    options,
					    options[0]);
					if(n == 1) {
						JFileChooser dirChooser = new JFileChooser(new File("."));
						dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						if(dirChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
							File f = dirChooser.getSelectedFile();
							for(String word : wordMap.activeWords.keySet()) {
								BufferedImage image = GoogleImage.retrieveImages(word, 1).get(0);
								try {
									ImageIO.write(image, "png", new File(f.getAbsolutePath() + File.separatorChar + word + ".png"));
								} catch(IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			});
			images.add(imagesGoogle);
			
			JMenuItem imagesFlickr = new JMenuItem("Flickr");
			imagesFlickr.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					//Custom button text
					Object[] options = {"No", "Yes"};
					int n = JOptionPane.showOptionDialog(null,
					    "This will load an image from Flickr for each active word, into a directory of your choosing.\nYou may then load the images for the nodes using File->Load->Image Directory.\nThis action may take a while. Are you sure you would like to continue?",
					    "Continue",
					    JOptionPane.YES_NO_CANCEL_OPTION,
					    JOptionPane.QUESTION_MESSAGE,
					    null,
					    options,
					    options[0]);
					if(n == 1) {
						JFileChooser dirChooser = new JFileChooser(new File("."));
						dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						if(dirChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
							File f = dirChooser.getSelectedFile();
							for(String word : wordMap.activeWords.keySet()) {
								BufferedImage image = FlickrImage.retrieveImages(word, 1).get(0);
								try {
									ImageIO.write(image, "png", new File(f.getAbsolutePath() + File.separatorChar + word + ".png"));
								} catch(IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			});
			images.add(imagesFlickr);
			
			// Seperator.
			file.addSeparator();
			
			JMenu language = new JMenu("Language");
			
			int i = 0;
			for(String languageName : Options.languages) {
				JMenuItem languageChoise = new JMenuItem(languageName);
				final int languageID = i;
				languageChoise.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						Options.language = languageID;
					}
				});
				language.add(languageChoise);
				i++;
			}
			
			file.add(language);
			
		return file;
	}
	
	public static JMenu buildHelpMenu(final MainGUI main) {

		JMenu help = new JMenu("Help");
		
		JMenuItem tutorial = new JMenuItem("Tutorial");
		tutorial.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				main.showTutorial();
			}
		});
		help.add(tutorial);
		
		return help;
	}
	
	/**
	 * Tools opens the various tool windows.
	 * @param main
	 * @return
	 */
	public static JMenu buildToolsMenu(final MainGUI main) {

		JMenu tools = new JMenu("Tools");
			
			JMenuItem wizard = new JMenuItem("Similarity Wizard");
			wizard.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					main.showWizardFrame();
				}
			});
			tools.add(wizard);
				
			JMenuItem wordManager = new JMenuItem("Word Manager");
			wordManager.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					main.showWordFrame();
				}
			});
			tools.add(wordManager);
						
			JMenuItem networkToolsMenu = new JMenuItem("Network Tools");
			networkToolsMenu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					main.showNetworkTools();
				}
			});
			tools.add(networkToolsMenu);
			
			JMenuItem showOptionsMenu = new JMenuItem("Show Options");
			showOptionsMenu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					main.showOptions();
				}
			});
			tools.add(showOptionsMenu);
			
		return tools;
	}
	
	/**
	 * Manage rendering capabilities.
	 * @param main
	 * @return
	 */
	public static JMenu buildRenderMenu(final MainGUI main) {
		JMenu render = new JMenu("Render");
		
		JMenuItem renderNow = new JMenuItem("Render Once");
		renderNow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				main.showRenderingProgress();					
				main.visualizationPanel.imageChanged = true;
				main.visualizationPanel.renderState = Visualization.STATE_RENDER_ONCE;
				main.visualizationChanged();
			}
		});
		render.add(renderNow);
		
		final JMenuItem renderAlwaysMenu = new JMenuItem("Render Always");
		renderAlwaysMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				main.visualizationPanel.imageChanged = true;
				if(main.visualizationPanel.renderState == Visualization.STATE_RENDER_SMART || main.visualizationPanel.renderState == Visualization.STATE_RENDER_ONCE) {
					main.visualizationPanel.renderState = Visualization.STATE_RENDER_ALWAYS;
					renderAlwaysMenu.setText("Render Smart");
				} else {
					main.visualizationPanel.renderState = Visualization.STATE_RENDER_SMART;
					renderAlwaysMenu.setText("Render Always");
				}
				main.visualizationChanged();
			}
		});
		render.add(renderAlwaysMenu);	
		
		return render;	
	}
	
	/**
	 * Select a default layout.
	 * @param wordMap
	 * @param main
	 * @return
	 */
	public static JMenu buildLayoutMenu(final WordMap wordMap, final MainGUI main) {
		
		JMenu layout = new JMenu("Layout");
		
		JMenuItem randomize = new JMenuItem("Random Layout");
		randomize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Layouts.layoutRandom(wordMap, main.visualizationPanel.getSize());
				main.visualizationChanged();
			}
		});
		layout.add(randomize);

		JMenuItem grid = new JMenuItem("Grid Layout");
		grid.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Layouts.layoutGrid(wordMap, main.visualizationPanel.getSize());
				main.visualizationChanged();
			}
		});
		layout.add(grid);
		
		JMenuItem centered = new JMenuItem("Word Centered Layout");
		centered.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				WordRelator wordRelator = (WordRelator)JOptionPane.showInputDialog(
                    null,
                    "Select comparator.",
                    "Comparator",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    wordMap.activeRelations.toArray(),
                    null);
			
				String word = (String)JOptionPane.showInputDialog(
                    null,
                    "Select word.",
                    "Word",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    null);
					
				Layouts.layoutWordCentered(wordMap, main.visualizationPanel.getSize(),wordRelator,word);
				main.visualizationChanged();
			}
		});
		layout.add(centered);			

		JMenuItem mds = new JMenuItem("MDS Layout");
		mds.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				WordRelator wordRelator = (WordRelator)JOptionPane.showInputDialog(
                    null,
                    "Select comparator to use for MDS.",
                    "Comparator Selection",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    wordMap.activeRelations.toArray(),
                    null);

				Layouts.layoutMDS(wordMap, main.visualizationPanel.getSize(),wordRelator, false);
				Layouts.layoutFitScreen(wordMap, main.visualizationPanel.getSize());
				main.visualizationChanged();
			}
		});
		layout.add(mds);
		
		JMenuItem procrustes = new JMenuItem("Procrustes Layout");
		procrustes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				WordRelator wr1 = (WordRelator)JOptionPane.showInputDialog(
	                    null,
	                    "Select comparator to use for First Procrustes.",
	                    "Comparator Selection",
	                    JOptionPane.PLAIN_MESSAGE,
	                    null,
	                    wordMap.activeRelations.toArray(),
	                    null);

				WordRelator wr2 = (WordRelator)JOptionPane.showInputDialog(
	                    null,
	                    "Select comparator to use for Second Procrustes.",
	                    "Comparator Selection",
	                    JOptionPane.PLAIN_MESSAGE,
	                    null,
	                    wordMap.activeRelations.toArray(),
	                    null);
				

				WordRelator trans = (WordRelator)JOptionPane.showInputDialog(
	                    null,
	                    "Translation.",
	                    "Comparator Selection",
	                    JOptionPane.PLAIN_MESSAGE,
	                    null,
	                    wordMap.activeRelations.toArray(),
	                    null);


				Set<String> words1 = wr1.getWords();
				Set<String> words2 = wr2.getWords();

				Vector<String> words1Order = new Vector<String>();
				Vector<String> words2Order = new Vector<String>();
				
				for(String word1 : words1) {
					if(wordMap.activeWords.get(word1) != null) {
						for(String word2 : words2) {
							if(!word1.equals(word2) && wordMap.activeWords.get(word2) != null && trans.getDistance(word1, word2) > .99) {
								words1Order.add(word1);
								words2Order.add(word2);
								break;
							}
						}
					}
				}

				double[][] sim1 = new double[words1Order.size()][words1Order.size()];
				for(int i=0;i<sim1.length;i++) {
					for(int j=0;j<sim1.length;j++) {
						sim1[i][j] = wr1.getDistance(words1Order.get(i), words1Order.get(j));
					}
				}
				
				double[][] sim2 = new double[words2Order.size()][words2Order.size()];
				for(int i=0;i<sim2.length;i++) {
					for(int j=0;j<sim2.length;j++) {
						sim2[i][j] = wr2.getDistance(words2Order.get(i), words2Order.get(j));
					}
				}
				
				double[][][] ret = Correlation.transformProcrustes(sim1, sim2);
				double[][] pos1 = ret[0];
				double[][] pos2 = ret[1];
				
				for(int i=0;i<words1Order.size();i++) {
					WordNode wn = wordMap.words.get(words1Order.get(i));
					double[] loc = {pos1[i][0], pos1[i][1], 0};
					wn.location = loc;
				}
				
				for(int i=0;i<words2Order.size();i++) {
					WordNode wn = wordMap.words.get(words2Order.get(i));
					double[] loc = {pos2[i][0], pos2[i][1], 0};
					wn.location = loc;
				}
				
				Layouts.layoutFitScreen(wordMap, main.visualizationPanel.getSize());
				main.visualizationChanged();
			}
		});
		layout.add(procrustes);
		
		JMenuItem tsne = new JMenuItem("tSNE Layout");
		tsne.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				WordRelator wordRelator = (WordRelator)JOptionPane.showInputDialog(
                    null,
                    "Select comparator to use for tSNE.",
                    "Comparator Selection",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    wordMap.activeRelations.toArray(),
                    null);

				Layouts.layoutTSNE(wordMap, main.visualizationPanel.getSize(),wordRelator, false);
				Layouts.layoutFitScreen(wordMap, main.visualizationPanel.getSize());
				main.visualizationChanged();
			}
		});
		layout.add(tsne);
		
		JMenuItem fit = new JMenuItem("Fit Screen");
		fit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {					
				Layouts.layoutFitScreen(wordMap, main.visualizationPanel.getSize());
				main.visualizationChanged();
			}
		});
		layout.add(fit);
		
		JMenuItem binary = new JMenuItem("Sided Layout");
		binary.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {		
				
				WordRelator wordRelator1 = (WordRelator)JOptionPane.showInputDialog(
	                    null,
	                    "Select first comparator.",
	                    "Comparator Selection",
	                    JOptionPane.PLAIN_MESSAGE,
	                    null,
	                    wordMap.activeRelations.toArray(),
	                    null);
				
				WordRelator wordRelator2 = (WordRelator)JOptionPane.showInputDialog(
	                    null,
	                    "Select second comparator.",
	                    "Comparator Selection",
	                    JOptionPane.PLAIN_MESSAGE,
	                    null,
	                    wordMap.activeRelations.toArray(),
	                    null);
				
				Layouts.layoutBinary(wordMap, wordRelator1, wordRelator2);
				Layouts.layoutFitScreen(wordMap, main.visualizationPanel.getSize());
				main.visualizationChanged();
			}
		});
		layout.add(binary);
		
		JMenuItem split = new JMenuItem("Split Layout");
		split.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {		
				WordRelator wordRelator = (WordRelator)JOptionPane.showInputDialog(
                    null,
                    "Select comparator.",
                    "Comparator Selection",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    wordMap.activeRelations.toArray(),
                    null);

				int track = 0;
				int track2 = 0;
				for(WordNode word1 : wordMap.getActiveWordNodeList()) {
					boolean found = false;
					
					for(WordNode word2 : wordMap.getActiveWordNodeList()) {
						
						double dist = wordRelator.getDistance(word1.word, word2.word);
						if(dist > .5) {
							if(word1.word.startsWith("<")) {
								word1.location[0] = 1;
								word1.location[1] = track;
								word2.location[0] = 0;
								word2.location[1] = track;
								track++;
							}
							found = true;
						}
					}	
					
					if(!found) {
						word1.location[0] = 2 + track / 30;
						word1.location[1] = track2 % 30;
						track2++;
					}
				}
				
				Layouts.layoutFitScreen(wordMap, main.visualizationPanel.getSize());
				main.visualizationChanged();				
			}
		});
		layout.add(split);
		
		
		
		JMenu threeD = new JMenu("3D");
		layout.add(threeD);
		
		tsne = new JMenuItem("tSNE Layout 3D");
		tsne.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				WordRelator wordRelator = (WordRelator)JOptionPane.showInputDialog(
                    null,
                    "Select comparator to use for tSNE.",
                    "Comparator Selection",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    wordMap.activeRelations.toArray(),
                    null);

				Layouts.layoutTSNE(wordMap, main.visualizationPanel.getSize(),wordRelator, true);
				Layouts.layoutFitScreen(wordMap, main.visualizationPanel.getSize());
				main.visualizationChanged();
			}
		});
		threeD.add(tsne);

		mds = new JMenuItem("MDS Layout 3D");
		mds.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				WordRelator wordRelator = (WordRelator)JOptionPane.showInputDialog(
                    null,
                    "Select comparator to use for MDS.",
                    "Comparator Selection",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    wordMap.activeRelations.toArray(),
                    null);

				Layouts.layoutMDS(wordMap, main.visualizationPanel.getSize(),wordRelator, true);
				Layouts.layoutFitScreen(wordMap, main.visualizationPanel.getSize());
				main.visualizationChanged();
			}
		});
		threeD.add(mds);
		
		centered = new JMenuItem("Word Centered Layout 3D");
		centered.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				WordRelator wordRelator = (WordRelator)JOptionPane.showInputDialog(
                    null,
                    "Select comparator.",
                    "Comparator",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    wordMap.activeRelations.toArray(),
                    null);
			
				String word = (String)JOptionPane.showInputDialog(
                    null,
                    "Select word.",
                    "Word",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    null);
					
				Layouts.layoutWordCentered3D(wordMap, main.visualizationPanel.getSize(),wordRelator,word);
				main.visualizationChanged();
			}
		});
		threeD.add(centered);			
		
		return layout;	
	}
	
	/**
	 * Select connection type.
	 * @param wordMap
	 * @param main
	 * @return
	 */
	public static JMenu buildConnectionMenu(final MainGUI main) {

		JMenu connectionType = new JMenu("Connection");
		
			JMenuItem connLine = new JMenuItem("Line");
			connLine.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					main.options.biDirectionalType = Options.BI_DIRECTIONAL_NONE;
					main.visualizationChanged();
				}			
			});
			connectionType.add(connLine);
		
			JMenuItem connArrow = new JMenuItem("Arrow");
			connArrow.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					main.options.biDirectionalType = Options.BI_DIRECTIONAL_ARROW;
					main.visualizationChanged();
				}			
			});
			connectionType.add(connArrow);
	
			JMenuItem connTopBottom = new JMenuItem("Top & Bottom");
			connTopBottom.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					main.options.biDirectionalType = Options.BI_DIRECTIONAL_TOP_BOTTOM;
					main.visualizationChanged();
				}			
			});
			connectionType.add(connTopBottom);

			JMenuItem connArrowMid = new JMenuItem("Middle Arrow");
			connArrowMid.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					main.options.biDirectionalType = Options.BI_DIRECTIONAL_ARROW_MID;
					main.visualizationChanged();
				}			
			});
			connectionType.add(connArrowMid);
			
			JMenuItem connDotEnd = new JMenuItem("Dot End");
			connDotEnd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					main.options.biDirectionalType = Options.BI_DIRECTIONAL_DOT_END;
					main.visualizationChanged();
				}			
			});
			connectionType.add(connDotEnd);
		
			JMenuItem connCustom = new JMenuItem("Spike");
			connCustom.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					main.options.biDirectionalType = Options.BI_DIRECTIONAL_SPIKE;
					main.visualizationChanged();
				}			
			});
			connectionType.add(connCustom);

		return connectionType;			
	}
	
}
