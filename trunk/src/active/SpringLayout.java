package active;

import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import relations.WordRelator;

import gui.MainGUI;
import gui.WordNode;

public class SpringLayout extends JPanel {

	private static final long serialVersionUID = 1854235124730618025L;
	
	public MainGUI main;
	public JSlider speed;
	public JComboBox combo;
	public boolean running;
	public Timer timer;
	public Hashtable<String, double[]> velocities = new Hashtable<String, double[]>();
	public int UPDATE_MS = 100;
	public double deadening = 1;

	public JSlider repulsion;
	public JSlider attraction;
	
	public SpringLayout(MainGUI main) {
		this.main = main;
		
		JFrame frame = new JFrame();
		frame.setSize(200, 100);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		frame.setVisible(true);

		this.setLayout(new GridLayout(0,1));
		
		JButton startStop = new JButton("Start/Stop");
		speed = new JSlider(1,1000);
		combo = new JComboBox(main.wordMap.activeRelations.toArray());

		startStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				running = !running;
				
				if(running) {
					timer.start();
				} else {
					timer.stop();
				}
			}
		});
		

		repulsion = new JSlider(0,10000);		
		attraction = new JSlider(0,10);
		
		this.add(startStop);
		this.add(new JLabel("speed"));
		this.add(speed);
		this.add(combo);
		this.add(new JLabel("repulsion"));
		this.add(repulsion);
		this.add(new JLabel("attraction"));
		this.add(attraction);
		
		timer = new Timer(UPDATE_MS, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tick();
			}
		});
	}
	
	public void tick() {
		
		System.out.println("tick");
		
		// Track energy to tell how the system has settled.
		//double total_kinetic_energy = 0;
		
		WordNode[] words = main.wordMap.activeWordsSorted.toArray(new WordNode[0]);
		
		WordRelator relator = (WordRelator)combo.getSelectedItem();
		
		// Update each word.
		for(WordNode thisFeature : words) {	
			
			double fx = 0;
			double fy = 0;

			double countScaller = Math.sqrt(words.length);

			// Each word is effected by every other word.
			for(WordNode thatFeature : words) {	
				
				// Do not effect yourself.
				if(thisFeature != thatFeature) {
					
					// Change in location.
					double dx = thisFeature.location[0] - thatFeature.location[0];
					double dy = thisFeature.location[1] - thatFeature.location[1];
					double dz = thisFeature.location[2] - thatFeature.location[2];
					
					double distSquare = Math.max(.01, dx * dx + dy * dy + dz * dz);
					double dist = Math.sqrt(distSquare);
					
					// Sum the force.
					double f = 0;
					
					// Coulomb_repulsion: F = ke * (q1 * q2) / r ^ 2
					f += 1 / (distSquare) * (repulsion.getValue() * 10.0);
					
					// Hooke_attraction: F = -kx (x is displacement, k is constant)
					// The power function can be tuned up to exaggerate stronger connections.
					double x = Math.pow(relator.getDistance(thisFeature.word, thatFeature.word), 1);
					f += -5 * x * (attraction.getValue() / 100.0);
					
					// This number is just a scaling speed constant.
					f /= 1000;
					
					f /= countScaller;
					
					f *= speed.getValue();
					
					// Calculate the X and Y components of the velocity.
					fx += dx * f / dist;
					fy += dy * f / dist;
				}
			}
			
			double[] velocity = velocities.get(thisFeature.word);
			if(velocity == null) {
				velocity = new double[3];
				velocities.put(thisFeature.word, velocity);
			}
			
			// Apply breaking.
			// .9 was choosen more or less randomly. It just must be greater than 0 and less than 1.
			double[] oldVelocity = velocity;
			double[] newVelocity = {
				(oldVelocity[0] + UPDATE_MS * fx) * (deadening / 100.0), 
				(oldVelocity[1] + UPDATE_MS * fy) * (deadening / 100.0),
				(oldVelocity[2] + UPDATE_MS * fy) * (deadening / 100.0),
			};

			velocities.put(thisFeature.word, newVelocity);
						
		    // Calculate kinetic energy.
		    //total_kinetic_energy += 1 * (fx * fx + fy * fy);
		    
		}
			
		// Now we move the bubbles.
		for(WordNode feature : words) {	
			double[] velocity = velocities.get(feature.word);
			
			System.out.println("   " + velocity[0] + "," + velocity[1] + "," + velocity[2]);

			// Move based on velocity.
			feature.location[0] += velocity[0] * UPDATE_MS;
			feature.location[1] += velocity[1] * UPDATE_MS;
			feature.location[2] += velocity[2] * UPDATE_MS;
		}
			
		main.wordMap.wordChanged();
	}
}
