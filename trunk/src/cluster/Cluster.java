package cluster;

import relations.WordRelator;
import gui.WordMap;

public abstract class Cluster {

	public abstract void cluster(WordMap map, WordRelator relator, int clusters);
}
