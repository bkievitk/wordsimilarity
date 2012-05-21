package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class Tutorial extends JPanel {

	private static final long serialVersionUID = 5796542849774150913L;
	private String initialRoot = "http://www.indiana.edu/~semantic/word2word/tutorial/";
	private String root = initialRoot;
	
	
	public Tutorial() {
		
		this.setPreferredSize(new Dimension(800,600));
		final JEditorPane html = new JEditorPane();
		final JButton home = new JButton("home");
		this.setLayout(new BorderLayout());
				
		this.add(html, BorderLayout.CENTER);
		this.add(home, BorderLayout.NORTH);
		
		try {	
			
			home.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					try {
						root = initialRoot;	
						html.setPage(new URL(root + "intro.html"));
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
							
			html.setPage(new URL(root + "intro.html"));
			html.setEditable(false);
			html.addHyperlinkListener(new HyperlinkListener() {
				public void hyperlinkUpdate(HyperlinkEvent arg0) {
					if(arg0.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
						try {
							
							String site = arg0.getDescription();
							if(site.startsWith("http://")) {
								html.setPage(new URL(site));
								root = root.substring(0,site.lastIndexOf("/") + 1);
							} else {
								html.setPage(new URL(root + site));
							}
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}				
			});
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}
