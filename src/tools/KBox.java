package tools;

import java.util.Vector;

/**
 * Store the top k elements.
 * @author Brent Kievit-Kylar
 */

public class KBox<V> {

	// All objects stored.
	private WeightedObject<V>[] objects;
	
	// Wether to add descending or assending.
	private boolean descending;
	
	// How many items in list at the moment.
    private int size = 0;
        
    @SuppressWarnings("unchecked")
	public KBox(int size, boolean descending) {
        objects = new WeightedObject[size];
    	this.descending = descending;
    }

    public WeightedObject<V> getObject(int i) {
    	if(i < size) {
    		return objects[i];
    	} else {
    		return null;
    	}
    }
    
    /**
     * Get all objects.
     * This will only return the part of the list that has been filled.
     * @return
     */
    public WeightedObject<V>[] getObjects() {
    	@SuppressWarnings("unchecked")
		WeightedObject<V>[] objects = new WeightedObject[size];
    	for(int i=0;i<size;i++) {
    		objects[i] = this.objects[i];
    	}
    	return objects;
    }
    
    /**
     * Get all objects.
     * This will only return the part of the list that has been filled.
     * @return
     */
    public Vector<WeightedObject<V>> getObjectsVector() {
    	Vector<WeightedObject<V>> objects = new Vector<WeightedObject<V>>();
    	for(int i=0;i<size;i++) {
    		objects.add(this.objects[i]);
    	}
    	return objects;
    }
    
    /**
     * How many items have been filled.
     * @return
     */
    public int size() {
    	return size;
    }
    
    /**
     * Add a new object to the list.
     * @param object
     */
    public void add(WeightedObject<V> object) {
    	
    	// Update size of list.
        if(size < objects.length) {
        	size++;
        }
        	
        // Find where it fits.
        for(int i=0;i<objects.length;i++) {
        	
        	// If empty space or it is a better object.
            if(objects[i] == null || ((objects[i].weight > object.weight && !descending) || (objects[i].weight < object.weight && descending))) {
            	
            	// Shuffle down the list.
                for(int j=objects.length-1;j>i;j--) {
                	objects[j] = objects[j-1];
                }
                objects[i] = object;
                return;
            }
        }
    }

    /**
     * Show objects.
     */
    public String toString() {
        String ret = "";
        for(int i=0;i<objects.length;i++) {
        	if(objects[i] == null) {
                return ret;
            }
            ret += (objects[i].object + "(" + objects[i].weight + ")" + "\n");
        }
        return ret;
    }
}
