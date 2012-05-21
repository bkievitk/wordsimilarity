package relations.holoc;

import java.io.Serializable;
import java.util.Hashtable;

public class WordData implements Serializable {

	private static final long serialVersionUID = 398773785287684614L;
	
	public Hashtable<WordData,Integer> cooccurences = new Hashtable<WordData,Integer>();
	public int id;
	public int count;
	public Function representation = new FunctionSin();
	
	public WordData(int id) {
		this.id = id;
		count = 0;
	}
	
	public int hashCode() {
		return (new Integer(id)).hashCode();
	}
	
	public void learnCoOccurence(WordData word) {
		Integer count = cooccurences.remove(word);
		if(count == null) {
			count = 0;
		}
		count++;
		cooccurences.put(word,count);
	}
	
	public boolean equals(Object o) {
		if(o instanceof WordData) {
			return ((WordData)o).id == id;
		}
		return false;
	}
}
