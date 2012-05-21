package wizard;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import relations.WordRelator;
import tools.PanelTools;
import tools.VerticalLayout;

public abstract class PanelData extends WizardPanel {
	
	private static final long serialVersionUID = 6903092770396589820L;
	
	public WordRelator relator;
	public JTextArea messages;
	
	public JButton webLearn;
	public JButton learnFile;
	public JButton learnDirectory;
	public JButton learnText;
	public JLabel label;
	public JComboBox url;
	public JTextArea textArea;
	public JProgressBar progressBar = new JProgressBar(0, 100);
	
	public PanelData(Wizard wizard, final WordRelator relator, boolean linkListeners, String name) {
		super(wizard, name);
		this.relator = relator;
		
		
		setLayout(new VerticalLayout(5,5));
		TitledBorder title;

		JPanel webPanel = new JPanel(new BorderLayout());
		title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Learn From Website");
		title.setTitleJustification(TitledBorder.LEFT);
		webPanel.setBorder(title);
				webPanel.add(PanelTools.wrappingText("Enter a website and then select the 'learn' button to learn all of the text from a website."),BorderLayout.CENTER);
				JPanel urlPanel = new JPanel(new BorderLayout());
				//url = new JTextField("http://");
				
				String[] websites = {"http://","http://en.wikipedia.org/wiki/Dog","http://en.wikipedia.org/wiki/Cat"};
				url = new JComboBox(websites);
				url.setEditable(true);
				
				urlPanel.add(url,BorderLayout.CENTER);
				webLearn = new JButton("learn");
				urlPanel.add(webLearn,BorderLayout.WEST);
				webPanel.add(urlPanel,BorderLayout.SOUTH);
		add(webPanel);
		

		JPanel filePanel = new JPanel(new BorderLayout());
		title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Learn From File");
		title.setTitleJustification(TitledBorder.LEFT);
		filePanel.setBorder(title);				
			filePanel.add(PanelTools.wrappingText("Select the name of a file to learn. This will only work if this program has permissions to read your file structure. The file interpreter will try to read the file contents appropriately, but only some file types are supported at present."),BorderLayout.CENTER);
			learnFile = new JButton("learn");
			filePanel.add(learnFile,BorderLayout.SOUTH);		
			
		add(filePanel);
		
		JPanel directoryPanel = new JPanel(new BorderLayout());
		
		title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Learn From Directory");
		title.setTitleJustification(TitledBorder.LEFT);
		directoryPanel.setBorder(title);
				
			directoryPanel.add(PanelTools.wrappingText("Select the name of a directory to learn. This will learn all of the files within the directory."),BorderLayout.CENTER);
			
			learnDirectory = new JButton("learn");
			directoryPanel.add(learnDirectory,BorderLayout.SOUTH);		
			
		add(directoryPanel);
		

		JPanel textPanel = new JPanel(new BorderLayout());
		title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Learn From Text");
		title.setTitleJustification(TitledBorder.LEFT);
		textPanel.setBorder(title);
		
			textPanel.add(PanelTools.wrappingText("Enter text in the area below and then click on the 'learn' button to teach the similarity tool the relations in your text."),BorderLayout.NORTH);
			textArea = new JTextArea();
			textArea.setRows(6);
			textArea.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));
			learnText = new JButton("learn");
			
			textPanel.add(textArea,BorderLayout.CENTER);
			textPanel.add(learnText,BorderLayout.SOUTH);
		add(textPanel);
		
		
		JPanel messagePanel = new JPanel(new BorderLayout());
		title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Messages");
		title.setTitleJustification(TitledBorder.LEFT);
		messagePanel.setBorder(title);
		
			messages = PanelTools.wrappingText("");
			messages.setRows(5);
			JScrollPane pane = new JScrollPane(messages);
			
			messagePanel.add(pane,BorderLayout.CENTER);
		
		add(messagePanel);

		label = new JLabel("Waiting...");
		add(label);
		
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		add(progressBar);
		
		if(linkListeners) {
			linkListeners();
		}
	}
	
	public void linkListeners() {

		final Component myThis = this;
		
		webLearn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					InputStream input = (new URL((String)url.getSelectedItem())).openStream();	
					if(relator.learn(input, progressBar, label)) {
						messages.append("Learning [" + url.getSelectedItem() + "]\n");
					} else {
						messages.append("Error learning [" + url.getSelectedItem() + "]\n");
					}							
				} catch (MalformedURLException e1) {
					messages.append("Invalid URL [" + url.getSelectedItem() + "]\n");
				} catch (IOException e1) {
					messages.append("Error connecting to web page [" + url.getSelectedItem() + "]\n");
				} catch (Exception e1) {
					messages.append("Error learning [" + url.getSelectedItem() + "]\n");
				}
			}
		});
		
		learnFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser selector = new JFileChooser(new File("."));
				selector.showOpenDialog(myThis);
				File selected = selector.getSelectedFile();
				if(relator.learn(selected, progressBar, label)) {
					messages.append(relator + " sucessfully learned [" + selected + "]\n");
				} else {
					messages.append("Error learning [" + selected + "]\n");
				}
			}
		});
		
		learnDirectory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser selector = new JFileChooser(new File("."));
				selector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				selector.showOpenDialog(myThis);
				File selected = selector.getSelectedFile();
				
				for(File subfile : selected.listFiles()) {
					if(relator.learn(subfile, progressBar, label)) {
						messages.append("Sucessfully learned [" + subfile + "]\n");
					} else {
						messages.append("Error learning [" + subfile + "]\n");
					}
				}
			}
		});
		
		learnText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				relator.learnDocument(textArea.getText());
				messages.append("Sucessfully learned user text.\n");
			}
		});
	}

	public boolean canFinish() {
		return true;
	}

	public String getTitle() {
		return "Relation Manager";
	}

	public String nextPanel() {
		return null;
	}

	public void finish() {
		wizard.wordMap.setRelatorStatus(relator,true);
		wizard.wordMap.addRelatorWords(relator);
		relator.finalizeSpace();
	}
}
