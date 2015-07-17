package Events;

import java.util.ArrayList;
import java.util.List;

/**
 * Can be deleted??????????????
 * class which implements methods and actions for disabling a file storage functionality
 * @author KV1
 *
 */
public class ForbidFileStorage {
	
    static List<forbidFileStorageListener> listeners = new ArrayList<forbidFileStorageListener>();
    
    public void addListener(forbidFileStorageListener toAdd) 
    {
        listeners.add(toAdd);
    }
    	  
    public void removeListener(forbidFileStorageListener toDelete)
    {
    	listeners.remove(toDelete);
    }
    
    public static void forbidFileStorage() {
        System.out.println("forbid file storage");

        // Notify everybody that may be interested.
        for (forbidFileStorageListener hl : listeners)
            hl.forbidFileStorage();
    }

	/**
	 * interface which should be used when a file storage is about to become disabled
	 * @author KV1
	 *
	 */
	public interface forbidFileStorageListener {
	    public void forbidFileStorage();
	}


}
