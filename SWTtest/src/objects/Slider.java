package objects;

import java.util.Iterator;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.jfree.data.time.Millisecond;

public class Slider {
		
	Millisecond[] lastMSarray;
	int arraySize = 0;
	public boolean sliderActive;
	public int sliderSteps = 0;
	private Buffer sliderBuffer;
		
	/**
	 * constructor
	 */
	public Slider(int s)
	{
		sliderSteps = s;
		sliderActive = false;
		lastMSarray = new Millisecond[sliderSteps];
		sliderBuffer = new CircularFifoBuffer(sliderSteps*2);
		initMSarray();
	}

	
    /**
     * returns a proper index for the slider, given the real slider value
     * @param index	slider value
     * @return	corresponding index in the milliseconds array
     */
    public Millisecond getIndexMilliseconds(int index)
    {
    	int inversed_index = index;
    	System.out.println("inversed index = "+inversed_index);
    	if (arraySize>=inversed_index)
    	{
    		return lastMSarray[inversed_index];
    	}
    	else
    	{
    		return lastMSarray[arraySize];
    	}
    }
    
    
    /**
     * returns a proper index for the slider, given the real slider value
     * @param index	slider value
     * @return	corresponding index in the milliseconds array
     */
    public Millisecond getMilliseconds(int index)
    {
    	int proper_index = sliderSteps + index - 1;
    	int cnt = 0;
		for(Iterator<Millisecond> iterator = sliderBuffer.iterator(); iterator.hasNext();)
		{
			if (cnt == proper_index) return (Millisecond)iterator.next();
			else iterator.next();
			cnt++;
		}
		return null;
    }
    
		
    /**
     * add a new Millisecond to the array
     * @param m Millisecond
     */
    public void addMS(Millisecond m)
    {
    	sliderBuffer.add(m);
    	arraySize++;
      	// activate slider
        if ((arraySize>sliderSteps*2) && (sliderActive == false))
        {
        	functions.Events.activateSliderSensorWindow();
        	sliderActive = true;
        }
    }
    
    
    /**
     * returns the highest index, where value is unequal NULL;
     * @return
     */
    public int getMinMS()
    {
    	for (int i = 0; i <sliderSteps; i++)
    	{
    		if (lastMSarray[i] == null) return i;
    	}
    	return sliderSteps;
    }
    
    
    private void initMSarray()
    {
    	arraySize = 0;
       	for (int i = 0; i <sliderSteps; i++)
    	{
    		lastMSarray[i] = null;
    	}   	
    }	
}
