package gui.simple;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.sussex.nlp.jws.JWS;
import gui.Layouts;
import gui.Options;
import gui.Visualization;
import gui.WordMap;
import gui.WordNode;

import relations.WordRelator;
import relations.wordnet.WordRelatorWordNet;
import tools.KBox;
import tools.PanelTools;
import tools.VerticalLayout;
import tools.WeightedObject;

public class GUISimple extends JTabbedPane {
	private static final long serialVersionUID = 2176722547541550050L;

	public JPanel view;
	public static final Random rand = new Random();
	
	public static void main(String[] args) {
		UIManager.put("TabbedPane.selected", Color.white);
		new GUISimple();
	}
			
	public GUISimple() {
		        
		final WordMap wordMap = new WordMap();
				
		File wordNet = new File("C:/Program Files/WordNet");
		if(!wordNet.exists()) {
			wordNet = new File("C:/Program Files (x86)/WordNet");
		}
		
		if(wordNet.exists()) {
			try {
				File dict = new File(wordNet + File.separator + "2.1" + File.separator + "dict");
				File semcor = new File(wordNet + File.separator + "2.1" + File.separator + "WordNet-InfoContent-2.1" + File.separator + "ic-semcor.dat");
				if(dict.exists() && semcor.exists()) {
					WordRelatorWordNet.setJWS(new JWS(wordNet.getAbsolutePath(), "2.1"),wordMap);
				}
			} catch(Exception e) {
				e.printStackTrace();
			} catch(NoClassDefFoundError e) {
				e.printStackTrace();
			}
		}
		
		JFrame mainFrame = new JFrame();
		mainFrame.setSize(1000,950);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel whitePanel = new JPanel(new BorderLayout());
		whitePanel.setBackground(Color.WHITE);
		whitePanel.add(this,BorderLayout.CENTER);		
		mainFrame.add(whitePanel);

		final Options options = new Options();
		
		this.addTab("1) Choose Similarity", loadIcon("menu_choose_similarity.png"), new GUISimpleChooseSimilarity(wordMap));
		this.addTab("2) Choose Words", loadIcon("menu_choose_words.png"), chooseWords(wordMap));
		this.addTab("3) Move Words", loadIcon("menu_move_words.png"), moveWords(wordMap));
		this.addTab("4) Line Type", loadIcon("menu_line_type.png"), lineType(options));
		
		view = new Visualization(wordMap,options,null);
		
		this.addTab("5) View", loadIcon("menu_view.png"), view);
		
		this.setBackground(Color.WHITE);
		
		mainFrame.setVisible(true);
				
	}
		
	public WordRelator newRelator = null;
	
	public static JLabel getLabel(String text, int size) {
		JLabel label = new JLabel(text);
		label.setFont(new Font(label.getFont().getName(),Font.PLAIN,size));
		label.setHorizontalAlignment(JLabel.CENTER);
		return label;
	}
	
	public JPanel chooseWords(final WordMap wordMap) {
		final JPanel chooseWords = new JPanel(new VerticalLayout(20,20));
		chooseWords.setBackground(Color.WHITE);
		
			// All words
			chooseWords.add(getLabel("All Words",20));
			
			JButton addAll = new JButton(loadIcon("words_add_all.png"));
			addAll.setBackground(Color.WHITE);
			JButton removeAll = new JButton(loadIcon("words_remove_all.png"));
			removeAll.setBackground(Color.WHITE);
			
			JPanel all = new JPanel(new GridLayout(1,2));
			all.add(addAll);
			all.add(removeAll);
			chooseWords.add(all);
			
			addAll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for(Object o : wordMap.wordsSorted) {
						wordMap.setWordStatus((WordNode)o,true);
					}
					wordMap.wordChanged();
				}			
			});
			
			removeAll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for(Object o : wordMap.wordsSorted) {
						wordMap.setWordStatus(((WordNode)o).word, false);
					}
					wordMap.wordChanged();
				}			

			});
			
			// Words like
			chooseWords.add(getLabel("Words Like",20));
			
			final JComboBox comparators = wordMap.getComboComparators();
			chooseWords.add(PanelTools.addLabel("Acording to this relation: ", comparators, BorderLayout.WEST));
			
			final JTextField word = new JTextField();
			chooseWords.add(PanelTools.addLabel("Like this word: ", word, BorderLayout.WEST));
			
			JButton addLike = new JButton(loadIcon("words_add_near.png"));
			addLike.setBackground(Color.WHITE);
			JButton removeLike = new JButton(loadIcon("words_remove_near.png"));
			removeLike.setBackground(Color.WHITE);
		
			addLike.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					Object o = comparators.getSelectedItem();
					if(o != null && o instanceof WordRelator) {
						WordNode center = wordMap.words.get(word.getText());
						if(center != null) {
							wordMap.setWordStatus(center,true);
							
							WordRelator relator = (WordRelator)o;
							int num = 50;
							
							KBox<WordNode> box = new KBox<WordNode>(num,true);
							for(WordNode node : wordMap.words.values()) {
								box.add(new WeightedObject<WordNode>(node, relator.getDistance(center.word, node.word)));
							}
							
							for(int i=0;i<box.size();i++) {
								wordMap.setWordStatus(box.getObjects()[i].object,true);
							}
							wordMap.wordChanged();
						}
					}
				}				
			});
			

			JPanel like = new JPanel(new GridLayout(1,2));
			like.add(addLike);
			like.add(removeLike);
			chooseWords.add(like);
			
			// Random
			chooseWords.add(getLabel("Random Words",20));
			
			JButton addRandom = new JButton(loadIcon("words_add_random.png"));
			addRandom.setBackground(Color.WHITE);
			JButton removeRandom = new JButton(loadIcon("words_remove_random.png"));
			removeRandom.setBackground(Color.WHITE);
			
			JPanel random = new JPanel(new GridLayout(1,2));
			random.add(addRandom);
			random.add(removeRandom);
			chooseWords.add(random);
			
			addRandom.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for(int i=0;i<50;i++) {
						WordNode newNode = wordMap.wordsSorted.get(rand.nextInt(wordMap.wordsSorted.size()));
						wordMap.setWordStatus(newNode.word,true);
					}
					wordMap.wordChanged();
				}
			});
			
			removeRandom.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for(int i=0;i<50;i++) {
						if(wordMap.activeWordsSorted.size() > 0) {
							wordMap.setWordStatus(wordMap.activeWordsSorted.get(rand.nextInt(wordMap.activeWordsSorted.size())).word,true);
						}
					}
					wordMap.wordChanged();
				}
			});
			
			chooseWords.add(getLabel("Words Selected",20));
			final JList dictionary = new JList(wordMap.activeWordsSorted);
			dictionary.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			dictionary.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			dictionary.setVisibleRowCount(-1);
			
			wordMap.wordChange.add(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					dictionary.invalidate();
					dictionary.validate();
					dictionary.repaint();
				}
			});
			
			JScrollPane slider = new JScrollPane(dictionary);
			slider.setPreferredSize(new Dimension(10,200));
			slider.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
			chooseWords.add(slider);
			
			wordMap.wordChange.add(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
				}				
			});
			
		return chooseWords;
	}
	
	public JPanel moveWords(final WordMap wordMap) {
		final JPanel moveWords = new JPanel(new GridLayout(2,2));
		moveWords.setBackground(Color.WHITE);
		
		ButtonGroup group = new ButtonGroup();
				
		JToggleButton grid = new JToggleButton(loadIcon("layout_grid.png"));
		grid.setBackground(Color.WHITE);
		grid.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Layouts.layoutGrid(wordMap, moveWords.getSize());
				wordMap.wordChanged();
			}			
		});
		moveWords.add(grid);
		
		JToggleButton mds = new JToggleButton(loadIcon("layout_mds.png"));
		mds.setBackground(Color.WHITE);
		mds.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				wordMap.wordChanged();
			}			
		});
		mds.setEnabled(false);
		moveWords.add(mds);
				
		JToggleButton random = new JToggleButton(loadIcon("layout_random.png"));
		random.setBackground(Color.WHITE);
		random.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Layouts.layoutRandom(wordMap, moveWords.getSize());
				wordMap.wordChanged();
			}			
		});
		moveWords.add(random);

		JToggleButton word = new JToggleButton(loadIcon("layout_word_centered.png"));
		word.setBackground(Color.WHITE);
		word.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				wordMap.wordChanged();
			}			
		});
		word.setEnabled(false);
		moveWords.add(word);
		
		group.add(grid);
		group.add(mds);
		group.add(random);
		group.add(word);
		
		random.setSelected(true);
		
		return moveWords;
	}
	
	public JPanel lineType(final Options options) {
		JPanel lineType = new JPanel(new VerticalLayout(20,20));
		lineType.setBackground(Color.WHITE);
		
		ButtonGroup group = new ButtonGroup();
		
		JToggleButton arrow = new JToggleButton(loadIcon("connection_arrow.png"));
		arrow.setBackground(Color.WHITE);
		lineType.add(arrow);
		arrow.setSelected(true);
		arrow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				options.biDirectionalType = Options.BI_DIRECTIONAL_ARROW;
			}			
		});
		
		JToggleButton dot = new JToggleButton(loadIcon("connection_dot.png"));
		dot.setBackground(Color.WHITE);
		lineType.add(dot);
		dot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				options.biDirectionalType = Options.BI_DIRECTIONAL_DOT_END;
			}			
		});
		
		JToggleButton line = new JToggleButton(loadIcon("connection_line.png"));
		line.setBackground(Color.WHITE);
		lineType.add(line);
		line.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				options.biDirectionalType = Options.BI_DIRECTIONAL_NONE;
			}			
		});
		
		JToggleButton mid_arrow = new JToggleButton(loadIcon("connection_mid_arrow.png"));
		mid_arrow.setBackground(Color.WHITE);
		lineType.add(mid_arrow);
		mid_arrow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				options.biDirectionalType = Options.BI_DIRECTIONAL_ARROW_MID;
			}			
		});
		
		JToggleButton spike = new JToggleButton(loadIcon("connection_spike.png"));
		spike.setBackground(Color.WHITE);
		lineType.add(spike);
		spike.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				options.biDirectionalType = Options.BI_DIRECTIONAL_SPIKE;
			}			
		});
		
		group.add(arrow);
		group.add(dot);
		group.add(line);
		group.add(mid_arrow);
		group.add(spike);
		
		return lineType;
	}
	
	public static ImageIcon loadIcon(String name) {
		
		URL url = GUISimple.class.getResource("/imgs/" + name);		
		if(url != null) {
			return new ImageIcon(url);
		}
		
		try {
			File f = new File("./imgs/" + name);
			return new ImageIcon(ImageIO.read(f));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
