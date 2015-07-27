package objects;

import java.util.Iterator;

/*
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
*/
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.jfree.data.time.DateRange;
import org.jfree.data.time.Millisecond;

public class Slider {
		
	Millisecond[] lastMSarray;
	int arraySize = 0;
	public boolean sliderActive;
	public boolean sliderEnabled;
	public int sliderSteps = 0;
	private CircularFifoQueue sliderBuffer;
		
	/**
	 * constructor
	 */
	public Slider(int s)
	{
		sliderSteps = s*12;
		sliderActive = false;
		sliderEnabled = false;
		lastMSarray = new Millisecond[sliderSteps];
		sliderBuffer = new CircularFifoQueue(600*5);
		initMSarray();
	}
	
	
	public void setActive(boolean ac)
	{
		this.sliderActive = ac;
	}
	
	
	public DateRange getDateRange(int sliderValue)
	{
		int amountExistingValues = sliderBuffer.size();
		
		if (amountExistingValues>=sliderValue)
		{			
			double s1 = (amountExistingValues-windows.sensorWindow.chartRangeSec*10);
			double s2 = (double)(windows.sensorWindow.sliderValuesNumber-sliderValue)/100.0;
			double shift = s1*s2;
			
			int start = (int) (amountExistingValues-(windows.sensorWindow.chartRangeSec*10)-shift);
			long tmp1 = this.getMilliseconds(start).getFirstMillisecond();
			int asda  = start+(windows.sensorWindow.chartRangeSec*10)-1;
			long tmp2 = this.getMilliseconds(asda).getFirstMillisecond();
			
			return new DateRange(tmp1, tmp2);
		}
		else return null;
	}
		
	
	public void setEnable(boolean en)
	{
		this.sliderEnabled = en;
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
     * 
     * @param
     * @return
     */
    public Millisecond getMilliseconds(int index)
    {
    	//int proper_index = sliderSteps + index - 1;
    	int proper_index = index;
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
        //if ((arraySize>sliderSteps*2) && (sliderActive == false))
    	if ((arraySize>=sliderSteps) && (sliderActive == false))
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
